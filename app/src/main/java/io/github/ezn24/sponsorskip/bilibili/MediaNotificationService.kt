/*
 * Sponsor Skip - Auto-skips SponsorBlock segments in Bilibili videos
 * Copyright (C) 2026 ezn24
 */

package io.github.ezn24.sponsorskip.bilibili

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.service.notification.NotificationListenerService
import android.widget.Toast
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import kotlin.math.max
import kotlin.math.abs

data class Segment(
    val startMs: Long,
    val endMs: Long,
    val category: String,
    val uuids: List<String> = emptyList()
)

class MediaNotificationService : NotificationListenerService() {
    private val client = OkHttpClient()
    private val bilibiliResolver = BilibiliResolver(client)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var skipSegments = mutableListOf<Segment>()
    private var mediaController: MediaController? = null
    private var trackingJob: Job? = null
    private var fetchJob: Job? = null
    private var sessionManager: MediaSessionManager? = null
    private var currentTitleOrId = ""
    private val mainHandler = Handler(Looper.getMainLooper())

    private val sessionListener = MediaSessionManager.OnActiveSessionsChangedListener { sessions -> handleSessions(sessions) }

    private val callback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            val currentPkg = mediaController?.packageName ?: ""
            if (!SettingsManager.isServiceEnabled || currentPkg !in SettingsManager.BILIBILI_PACKAGES) return

            val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE) ?: ""
            val initialDuration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L

            val mediaId = metadata?.getString(MediaMetadata.METADATA_KEY_MEDIA_ID).orEmpty()
            val targetIdentifier = "$mediaId|$title"

            if (targetIdentifier.isNotBlank() && targetIdentifier != currentTitleOrId) {
                currentTitleOrId = targetIdentifier
                val modePrefix = "[BILI SERVICE]"
                
                AppLogger.log("$modePrefix === METADATA DETECTED ($currentPkg) ===")
                AppLogger.log("$modePrefix --- RAW METADATA DUMP ---")
                metadata?.keySet()?.forEach { key ->
                    val value = try { metadata.getString(key) ?: metadata.getLong(key).toString() } catch (e: Exception) { "Binary/Object" }
                    AppLogger.log("$modePrefix $key: $value")
                }
                AppLogger.log("$modePrefix ---------------------------")

                fetchJob?.cancel()
                fetchJob = scope.launch {
                    try {
                        if (mediaController?.playbackState?.state != PlaybackState.STATE_PLAYING) {
                            AppLogger.log("$modePrefix Target is buffering/paused. Waiting for playback...")
                            while (mediaController?.playbackState?.state != PlaybackState.STATE_PLAYING && isActive) { delay(100) }
                            if (!isActive) return@launch
                            AppLogger.log("$modePrefix Playback started for '$targetIdentifier'.")
                        }

                        val freshMetadata = mediaController?.metadata
                        val actualDuration = freshMetadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: initialDuration
                        if (!isActive) return@launch
                        fetchSegmentsAndTrack(title, freshMetadata, actualDuration, targetIdentifier)
                    } catch (e: Exception) {
                        if (e !is CancellationException) {
                            AppLogger.log("[SERVICE] fetchJob error: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    private val toggleReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SettingsManager.ACTION_TOGGLE_SERVICE) {
                if (!SettingsManager.isServiceEnabled) {
                    AppLogger.log("[SERVICE] MASTER KILL SIGNAL. Wiping memory and detaching hooks.")
                    trackingJob?.cancel(); fetchJob?.cancel(); mediaController?.unregisterCallback(callback); mediaController = null; currentTitleOrId = ""; skipSegments.clear()
                } else {
                    AppLogger.log("[SERVICE] CONFIG CHANGED. Re-evaluating active hooks.")
                    val component = ComponentName(this@MediaNotificationService, MediaNotificationService::class.java)
                    handleSessions(sessionManager?.getActiveSessions(component))
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        SettingsManager.init(this); AppLogger.init(this)
        val filter = IntentFilter(SettingsManager.ACTION_TOGGLE_SERVICE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) registerReceiver(toggleReceiver, filter, Context.RECEIVER_NOT_EXPORTED) else registerReceiver(toggleReceiver, filter)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        sessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val component = ComponentName(this, MediaNotificationService::class.java)
        try {
            sessionManager?.addOnActiveSessionsChangedListener(sessionListener, component)
            handleSessions(sessionManager?.getActiveSessions(component))
        } catch (e: Exception) { AppLogger.log("[ERROR] Failed NLS hook: ${e.message}") }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        AppLogger.log("[SERVICE] ⚠️ WARNING: System abruptly unbound NotificationListenerService!")
        try { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) requestRebind(ComponentName(this, MediaNotificationService::class.java)) } catch (e: Exception) {}
    }

    private fun handleSessions(sessions: List<MediaController>?) {
        val newController = sessions?.find { controller ->
            val pkg = controller.packageName
            SettingsManager.isServiceEnabled && pkg in SettingsManager.BILIBILI_PACKAGES
        }

        if (newController != null) {
            if (mediaController?.sessionToken == newController.sessionToken) return
            AppLogger.log("[SERVICE] Hooked into MediaController (${newController.packageName}).")
            mediaController?.unregisterCallback(callback); mediaController = newController; mediaController?.registerCallback(callback)
            callback.onMetadataChanged(mediaController?.metadata)
        } else {
            if (mediaController != null) {
                AppLogger.log("[SERVICE] Active playback detached.")
                mediaController?.unregisterCallback(callback); mediaController = null; currentTitleOrId = ""; trackingJob?.cancel(); fetchJob?.cancel(); skipSegments.clear()
            }
        }
    }

    private suspend fun fetchSegmentsAndTrack(
        targetInput: String,
        metadata: MediaMetadata?,
        durationMs: Long,
        expectedIdentifier: String
    ) {
        try {
            val resolved = bilibiliResolver.resolve(metadata, targetInput, durationMs)
            if (resolved == null) {
                AppLogger.log("[BILI RESOLVER] FATAL: Could not resolve a BVID for '$targetInput'.")
                if (SettingsManager.isLoggingEnabled) showToast(getString(R.string.bilibili_video_id_error))
                return
            }
            val targetVideoId = resolved.bvid
            var bilibiliCid = resolved.cid
            AppLogger.log("[BILI RESOLVER] Resolved media to BVID=$targetVideoId; fetching segments before optional CID lookup")
            val cidParam = bilibiliCid?.let { "&cid=${URLEncoder.encode(it, "UTF-8")}" }.orEmpty()
            val apiUrl = "https://www.bsbsb.top/api/skipSegments?videoID=$targetVideoId$cidParam"

            AppLogger.log("[API] Executing GET: $apiUrl")
            val requestBuilder = Request.Builder().url(apiUrl)
                .header("Origin", "android-app://$packageName")
                .header("x-ext-version", BuildConfig.VERSION_NAME)
            val sponsorRes = client.newCall(requestBuilder.build()).execute()
            AppLogger.log("[API] Response Code: ${sponsorRes.code}")

            if (currentTitleOrId != expectedIdentifier) {
                AppLogger.log("[API] Discarding stale response for '$expectedIdentifier'.")
                sponsorRes.close()
                return
            }

            if (!sponsorRes.isSuccessful) {
                sponsorRes.close()
                if (SettingsManager.isLoggingEnabled) showToast(getString(R.string.no_segments))
                return
            }

            val sponsorJson = sponsorRes.use { JSONArray(it.body?.string() ?: "[]") }
            skipSegments.clear()
            val armedSegments = mutableListOf<Segment>()

            if (bilibiliCid == null) {
                val matchingCids = buildSet {
                    for (i in 0 until sponsorJson.length()) {
                        val candidate = sponsorJson.optJSONObject(i) ?: continue
                        val submittedDuration = candidate.optLong("videoDuration", 0L)
                        val durationMatches = durationMs <= 0L || submittedDuration <= 0L ||
                            abs(durationMs / 1000L - submittedDuration) <= 5L
                        candidate.optString("cid").takeIf { it.isNotBlank() && durationMatches }?.let(::add)
                    }
                }
                bilibiliCid = when {
                    matchingCids.size == 1 -> matchingCids.first()
                    matchingCids.size > 1 -> bilibiliResolver.resolveCid(targetVideoId, targetInput, durationMs)
                    else -> null
                }
                AppLogger.log("[BILI RESOLVER] Candidate CIDs=${matchingCids.size}; selected=${bilibiliCid ?: "not required"}")
            }

            for (i in 0 until sponsorJson.length()) {
                val obj = sponsorJson.getJSONObject(i)
                val segment = obj.getJSONArray("segment")
                val category = obj.getString("category")
                val uuid = obj.optString("UUID", obj.optString("uuid", ""))
                val actionType = obj.optString("actionType", "skip")
                val segmentCid = obj.optString("cid")
                val submittedDuration = obj.optLong("videoDuration", 0L)
                val durationMatches = durationMs <= 0L || submittedDuration <= 0L ||
                    abs(durationMs / 1000L - submittedDuration) <= 5L
                val cidMatches = bilibiliCid == null || segmentCid.isBlank() || segmentCid == bilibiliCid
                if (actionType != "skip" || !cidMatches || !durationMatches) {
                    AppLogger.log("[BILI PARSE] Ignored [$category]: action=$actionType cid=$segmentCid duration=${submittedDuration}s")
                    continue
                }
                val action = SettingsManager.getSegmentAction(category)
                val actionStr = if (action == 1) "Skip" else "Off"

                if (action == 1) {
                    val durationSec = segment.getDouble(1) - segment.getDouble(0)
                    val minDur = SettingsManager.minSegmentDuration.toDouble()
                    if (durationSec < minDur) {
                        AppLogger.log("[PARSE] Evaluated [$category] = $actionStr (BLOCKED: ${String.format("%.2f", durationSec)}s < ${minDur}s min)")
                    } else {
                        AppLogger.log("[PARSE] Evaluated [$category] = $actionStr")
                        val uuids = if (uuid.isNotBlank()) listOf(uuid) else emptyList()
                        val skipOffsetMs = SettingsManager.skipOffset.toLong()
                        val rawStartMs = (segment.getDouble(0) * 1000).toLong()
                        val rawEndMs = (segment.getDouble(1) * 1000).toLong()
                        val startMs = maxOf(0L, rawStartMs + skipOffsetMs)
                        val endMs = maxOf(0L, rawEndMs + skipOffsetMs)
                        AppLogger.log("[PARSE] Armed [$category] at ${startMs}ms-${endMs}ms")
                        if (skipOffsetMs != 0L) {
                            AppLogger.log("[PARSE] Applied skip offset of ${skipOffsetMs}ms to [$category]: original (${rawStartMs}-${rawEndMs}ms) -> (${startMs}-${endMs}ms)")
                        }
                        armedSegments.add(Segment(startMs, endMs, category, uuids))
                    }
                } else { AppLogger.log("[PARSE] Evaluated [$category] = $actionStr") }
            }

            val sorted = armedSegments.sortedBy { it.startMs }
            val armedCount = sorted.size

            if (sorted.isNotEmpty()) {
                var current = sorted[0]
                for (i in 1 until sorted.size) {
                    val next = sorted[i]
                    if (current.endMs >= next.startMs - 1000) {
                        AppLogger.log("[TRACKER] Fusing adjacent segments into multiple block.")
                        current = Segment(
                            current.startMs,
                            max(current.endMs, next.endMs),
                            "multiple",
                            current.uuids + next.uuids
                        )
                    } else { skipSegments.add(current); current = next }
                }
                skipSegments.add(current)
            }

            if (skipSegments.isNotEmpty()) {
                AppLogger.log("[TRACKER] Engaging playback loop for ${skipSegments.size} merged blocks.")
                showToast(getString(R.string.segments_loaded, armedCount))
                startTracking()
            }
        } catch (e: Exception) {
            AppLogger.log("[FATAL] Trace: ${e.message}")
            if (SettingsManager.isLoggingEnabled) showToast(getString(R.string.segments_fetch_error))
        }
    }

    private fun sendSkipCount(uuid: String) {
        try {
            AppLogger.log("[API] Sending skip count for segment UUID: $uuid")
            val json = JSONObject().put("UUID", uuid).toString()
            val req = Request.Builder()
                .url("https://www.bsbsb.top/api/viewedVideoSponsorTime")
                .header("Origin", "android-app://$packageName")
                .header("x-ext-version", BuildConfig.VERSION_NAME)
                .post(json.toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()
            val resp = client.newCall(req).execute()
            AppLogger.log("[API] Skip count response code for $uuid: ${resp.code}")
            resp.close()
        } catch (e: Exception) {
            AppLogger.log("[API] Failed to send skip count for $uuid: ${e.message}")
        }
    }

    private fun startTracking() {
        trackingJob?.cancel()
        trackingJob = scope.launch(Dispatchers.Main) {
            while (isActive) {
                try {
                    val state = mediaController?.playbackState
                    if (state?.state == PlaybackState.STATE_PLAYING) {
                        val elapsed = if (state.lastPositionUpdateTime > 0L) {
                            (SystemClock.elapsedRealtime() - state.lastPositionUpdateTime).coerceAtLeast(0L)
                        } else 0L
                        val pos = (state.position + elapsed * state.playbackSpeed).toLong().coerceAtLeast(0L)
                        val hit = skipSegments.find { pos in it.startMs..it.endMs }

                        if (hit != null) {
                            AppLogger.log("[TRACKER] ⚠️ CROSSED BOUNDARY: ${hit.category.uppercase()} at $pos ms")
                            skipSegments.remove(hit)
                            try {
                                mediaController?.transportControls?.seekTo(hit.endMs)
                            } catch (e: Exception) {
                                AppLogger.log("[TRACKER] seekTo failed: ${e.message}")
                            }

                            SettingsManager.skippedCount += if (hit.category == "multiple") 2 else 1
                            SettingsManager.timeSavedMs += (hit.endMs - hit.startMs)
                            sendBroadcast(Intent(SettingsManager.ACTION_STATS_UPDATED).setPackage(packageName))

                            if (SettingsManager.isSkipCountTrackingEnabled && hit.uuids.isNotEmpty()) {
                                val uuidsToSend = hit.uuids.toList()
                                scope.launch(Dispatchers.IO) {
                                    for (uuid in uuidsToSend) {
                                        sendSkipCount(uuid)
                                    }
                                }
                            }

                            showToast(if (hit.category == "multiple") getString(R.string.skipped_multiple) else getString(R.string.skipped_category, localizedCategoryName(hit.category)))
                            delay(250)
                        }
                    }
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        AppLogger.log("[TRACKER] Loop error: ${e.message}")
                    }
                }
                delay(200)
            }
        }
    }

    private fun localizedCategoryName(category: String): String {
        val resourceId = when (category) {
            "sponsor" -> R.string.category_sponsor
            "selfpromo" -> R.string.category_selfpromo
            "interaction" -> R.string.category_interaction
            "intro" -> R.string.category_intro
            "outro" -> R.string.category_outro
            "preview" -> R.string.category_preview
            "exclusive_access" -> R.string.category_exclusive_access
            "padding" -> R.string.category_padding
            "filler" -> R.string.category_filler
            "music_offtopic" -> R.string.category_music_offtopic
            else -> return category.uppercase()
        }
        return getString(resourceId)
    }

    private fun showToast(msg: String) = mainHandler.post { Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show() }
    override fun onDestroy() {
        try { unregisterReceiver(toggleReceiver) } catch (e: Exception) {}
        scope.cancel()
        super.onDestroy()
    }
}

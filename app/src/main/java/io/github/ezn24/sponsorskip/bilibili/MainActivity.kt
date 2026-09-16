/*
 * Sponsor Skip - Auto-skips SponsorBlock segments in Bilibili videos
 * Copyright © 2026 ezn24
 */
package io.github.ezn24.sponsorskip.bilibili

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
  private val categories = listOf(
    Triple("sponsor", R.string.category_sponsor, R.string.category_sponsor_description),
    Triple("selfpromo", R.string.category_selfpromo, R.string.category_selfpromo_description),
    Triple("interaction", R.string.category_interaction, R.string.category_interaction_description),
    Triple("intro", R.string.category_intro, R.string.category_intro_description),
    Triple("outro", R.string.category_outro, R.string.category_outro_description),
    Triple("preview", R.string.category_preview, R.string.category_preview_description),
    Triple("exclusive_access", R.string.category_exclusive_access, R.string.category_exclusive_access_description),
    Triple("padding", R.string.category_padding, R.string.category_padding_description),
    Triple("filler", R.string.category_filler, R.string.category_filler_description),
    Triple("music_offtopic", R.string.category_music_offtopic, R.string.category_music_offtopic_description)
  )

  private val statsReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) { refreshStats() }
  }
  
  private val liveStateReceiver = object : android.content.BroadcastReceiver() {
    override fun onReceive(context: android.content.Context?, intent: android.content.Intent?) {
      if (intent?.action == SettingsManager.ACTION_TOGGLE_SERVICE) {
        val isEnabled = SettingsManager.isServiceEnabled
        val masterSwitch = findViewById<com.google.android.material.materialswitch.MaterialSwitch>(R.id.switchMaster)
        if (masterSwitch?.isChecked != isEnabled) {
          masterSwitch?.isChecked = isEnabled
        }
        updateGreyOutState(isEnabled)
      }
    }
  }

  private var privacyDialog: AlertDialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    SettingsManager.init(this)
    AppLogger.init(this)
    
    // Auto-clean any residual APK downloads eating up user Data storage
    try {
      getExternalFilesDir(null)?.listFiles()?.forEach { file ->
        if (file.name.endsWith(".apk")) file.delete()
      }
    } catch (e: Exception) {}
    val toggleFilter = android.content.IntentFilter(SettingsManager.ACTION_TOGGLE_SERVICE)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
      registerReceiver(liveStateReceiver, toggleFilter, android.content.Context.RECEIVER_NOT_EXPORTED)
    } else {
      registerReceiver(liveStateReceiver, toggleFilter)
    }
    AppLogger.log("[UI] MainActivity onCreate triggered.")
    setContentView(R.layout.activity_main)
    
    if (!SettingsManager.isPrivacyAccepted) { showPrivacyDialog() }

    fun View.haptic() = this.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

    val viewHome = findViewById<View>(R.id.view_home)
    val viewSettings = findViewById<View>(R.id.view_settings)

    findViewById<BottomNavigationView>(R.id.bottom_nav).setOnItemSelectedListener { item ->
      item.actionView?.haptic()
      AppLogger.log("[UI] Navigated to ${item.title}")
      when (item.itemId) {
        R.id.nav_home -> { viewHome.visibility = View.VISIBLE; viewSettings.visibility = View.GONE }
        R.id.nav_settings -> { viewHome.visibility = View.GONE; viewSettings.visibility = View.VISIBLE }
      }
      true
    }

    populateSegments()
    refreshStats()
    
    (findViewById<TextView>(R.id.tvStats).parent as View).setOnClickListener { view ->
      if (SettingsManager.skippedCount == 0) return@setOnClickListener
      view.haptic()
      AlertDialog.Builder(this@MainActivity)
        .setTitle(R.string.reset_statistics)
        .setMessage(R.string.reset_statistics_message)
        .setPositiveButton(R.string.reset) { _, _ ->
          SettingsManager.skippedCount = 0
          SettingsManager.timeSavedMs = 0L
          refreshStats()
        }
        .setNegativeButton(R.string.cancel, null)
        .show()
    }
    setupFooter()
    
    val switchMaster = findViewById<MaterialSwitch>(R.id.switchMaster)
    val hasNotifInit = NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
    if (!hasNotifInit && SettingsManager.isServiceEnabled) {
      SettingsManager.isServiceEnabled = false
    }
    switchMaster.isChecked = SettingsManager.isServiceEnabled

    switchMaster.setOnCheckedChangeListener { view, isChecked ->
      val hasNotif = NotificationManagerCompat.getEnabledListenerPackages(this@MainActivity).contains(packageName)
      if (isChecked && !hasNotif) {
        view.haptic()
        switchMaster.isChecked = false 
        Toast.makeText(this@MainActivity, R.string.permission_required, Toast.LENGTH_LONG).show()
        startActivity(Intent(this@MainActivity, PermissionsActivity::class.java))
        return@setOnCheckedChangeListener
      }
      if (SettingsManager.isServiceEnabled != isChecked) {
        view.haptic()
        SettingsManager.isServiceEnabled = isChecked
        sendBroadcast(Intent(SettingsManager.ACTION_TOGGLE_SERVICE).setPackage(packageName))
        updateGreyOutState(isChecked)
      }
    }

    try {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        findViewById<TextView>(R.id.tvVersion).text = getString(R.string.version_format, pInfo.versionName)
    } catch (e: Exception) { }
    
    findViewById<View>(R.id.cardUpdate).setOnClickListener { it.haptic(); lifecycleScope.launch { UpdateManager.checkUpdate(this@MainActivity, true) } }
    findViewById<View>(R.id.btnSetPerms).setOnClickListener { it.haptic(); startActivity(Intent(this, PermissionsActivity::class.java)) }
    findViewById<View>(R.id.btnSetMore).setOnClickListener { it.haptic(); startActivity(Intent(this, MoreActivity::class.java)) }
    findViewById<View>(R.id.btnSetRepo).setOnClickListener { it.haptic(); startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ezn24/Sponsor-Skip-Bilibili"))) }
    findViewById<View>(R.id.btnSetBugs).setOnClickListener { it.haptic(); startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ezn24/Sponsor-Skip-Bilibili/issues/new/choose"))) }
    findViewById<View>(R.id.btnSetFeature).setOnClickListener { it.haptic(); startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ezn24/Sponsor-Skip-Bilibili/issues/new/choose"))) }
    findViewById<View>(R.id.btnSetPrivacy).setOnClickListener { it.haptic(); startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ezn24/Sponsor-Skip-Bilibili/blob/main/PRIVACY.md"))) }
    findViewById<View>(R.id.btnSetLicense).setOnClickListener { it.haptic(); AlertDialog.Builder(this).setTitle(R.string.license_title).setMessage(R.string.license_message).setPositiveButton(R.string.view_full_gpl) { _, _ -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gnu.org/licenses/gpl-3.0.html"))) }.setNegativeButton(R.string.close, null).show() }
    findViewById<View>(R.id.btnSetCredits).setOnClickListener { view ->
      view.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
      
      val handler = java.lang.reflect.InvocationHandler { _, method, args ->
          try {
              // Intercept clicks on both the main card and the bottom license badge
              if ((method.name == "onLibraryContentClicked" || method.name == "onLibraryBottomClicked") && args != null && args.size >= 2) {
                  val v = args[0] as android.view.View
                  val library = args[1] as com.mikepenz.aboutlibraries.entity.Library
                  
                  val license = library.licenses.firstOrNull()
                  var url = license?.url ?: ""
                  val name = license?.name?.lowercase() ?: ""
                  val lowerUrl = url.lowercase()
                  
                  // SPDX URL Auto-Corrector
                  if (name.contains("apache") || lowerUrl.contains("apache.org/licenses/license-2.0")) {
                      url = "https://spdx.org/licenses/Apache-2.0.html"
                  } else if (name.contains("mit") || lowerUrl.contains("opensource.org/licenses/mit")) {
                      url = "https://spdx.org/licenses/MIT.html"
                  } else if (url.startsWith("http://")) {
                      // Force HTTPS for any other legacy links
                      url = url.replace("http://", "https://")
                  }
                  
                  if (url.isNotBlank()) {
                      try {
                          v.context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)))
                      } catch (e: Exception) {
                          android.widget.Toast.makeText(v.context, R.string.failed_open_link, android.widget.Toast.LENGTH_SHORT).show()
                      }
                  } else {
                      android.widget.Toast.makeText(v.context, R.string.no_license_url, android.widget.Toast.LENGTH_SHORT).show()
                  }
                  
                  // Returning TRUE consumes the click, completely blocking the release page from opening
                  return@InvocationHandler true 
              }
          } catch (e: Exception) { }
          
          // Disable ALL other default behaviors (like Repo/Author clicks) by forcing true
          if (method.returnType == Boolean::class.javaPrimitiveType || method.returnType == java.lang.Boolean::class.java || method.returnType.name == "boolean") {
              return@InvocationHandler true
          }
          null
      }
      
      com.mikepenz.aboutlibraries.LibsConfiguration.listener = java.lang.reflect.Proxy.newProxyInstance(
          com.mikepenz.aboutlibraries.LibsConfiguration.LibsListener::class.java.classLoader,
          arrayOf(com.mikepenz.aboutlibraries.LibsConfiguration.LibsListener::class.java),
          handler
      ) as com.mikepenz.aboutlibraries.LibsConfiguration.LibsListener
      
      com.mikepenz.aboutlibraries.LibsBuilder()
          .withLicenseShown(true)
          .start(this)
    }

    checkPendingUpdateIntent(intent)
    if (SettingsManager.isPrivacyAccepted) { lifecycleScope.launch { UpdateManager.checkUpdate(this@MainActivity, false) } }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      registerReceiver(statsReceiver, IntentFilter(SettingsManager.ACTION_STATS_UPDATED), Context.RECEIVER_NOT_EXPORTED)
    } else {
      registerReceiver(statsReceiver, IntentFilter(SettingsManager.ACTION_STATS_UPDATED))
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    checkPendingUpdateIntent(intent)
  }

  private fun checkPendingUpdateIntent(targetIntent: Intent?) {
    val showFromNotif = targetIntent?.getBooleanExtra("EXTRA_SHOW_UPDATE", false) == true
    var tag = targetIntent?.getStringExtra("EXTRA_UPDATE_TAG") ?: ""
    var url = targetIntent?.getStringExtra("EXTRA_UPDATE_URL") ?: ""
    
    if (tag.isBlank() || url.isBlank()) {
      tag = SettingsManager.pendingUpdateTag
      url = SettingsManager.pendingUpdateUrl
    }
    
    if (showFromNotif && tag.isNotBlank() && url.isNotBlank()) {
      targetIntent?.removeExtra("EXTRA_SHOW_UPDATE")
      val currentVersionRaw = try {
        packageManager.getPackageInfo(packageName, 0).versionName ?: "Unknown"
      } catch (e: Exception) { "Unknown" }
      val currentVersion = currentVersionRaw.removePrefix("v").trim()
      UpdateManager.showUpdateDialog(this, tag, url, currentVersion)
    }
  }

  private fun showPrivacyDialog() {
    if (privacyDialog?.isShowing == true) return
    privacyDialog = AlertDialog.Builder(this).setTitle(R.string.welcome_title).setMessage(R.string.privacy_message).setCancelable(false).setPositiveButton(R.string.accept) { _, _ -> SettingsManager.isPrivacyAccepted = true; lifecycleScope.launch { UpdateManager.checkUpdate(this@MainActivity, false) } }.setNegativeButton(R.string.decline) { _, _ -> finishAffinity() }.setNeutralButton(R.string.privacy_policy, null).create()
    privacyDialog?.setOnShowListener {
      privacyDialog?.getButton(AlertDialog.BUTTON_NEUTRAL)?.setOnClickListener { view ->
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ezn24/Sponsor-Skip-Bilibili/blob/main/PRIVACY.md")))
      }
    }
    privacyDialog?.show()
  }

  override fun onResume() {
    super.onResume()
    wakeUpListenerService()
    findViewById<com.google.android.material.materialswitch.MaterialSwitch>(R.id.switchMaster)?.isChecked = SettingsManager.isServiceEnabled
    updateGreyOutState(SettingsManager.isServiceEnabled)
  }

  
  private fun wakeUpListenerService() {
      if (SettingsManager.isServiceEnabled && NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)) {
          try {
              val component = ComponentName(this, MediaNotificationService::class.java)
              val pm = packageManager
              pm.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
              pm.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
              AppLogger.log("[UI] Performed internal component reset to cure NLS Zombie state.")
          } catch (e: Exception) {
              AppLogger.log("[UI] Component reset failed: ${e.message}")
          }
      }
  }

  private fun updateGreyOutState(isEnabled: Boolean) {
    val container = findViewById<LinearLayout>(R.id.segmentsContainer)
    for (i in 0 until container.childCount) {
      val card = container.getChildAt(i) as MaterialCardView
      card.alpha = if (isEnabled) 1.0f else 0.5f
      val rg = (card.getChildAt(0) as LinearLayout).getChildAt(2) as RadioGroup
      for (j in 0 until rg.childCount) rg.getChildAt(j).isEnabled = isEnabled
    }
  }

  private fun populateSegments() {
    val container = findViewById<LinearLayout>(R.id.segmentsContainer)
    categories.forEach { info ->
      val key = info.first
      val label = getString(info.second)
      val desc = getString(info.third)

      val card = MaterialCardView(this).apply {
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 24) }
        setCardBackgroundColor(Color.TRANSPARENT)
        strokeWidth = 2
      }
      val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(32, 32, 32, 32) }
      
      // Header containing the title and the dropdown arrow
      val headerLayout = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        gravity = android.view.Gravity.CENTER_VERTICAL
        setPadding(0, 0, 0, 16)
      }
      
      val title = TextView(this).apply { 
        text = label
        textSize = 16f
        setTypeface(null, Typeface.BOLD)
        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
      }
      
      val arrow = TextView(this).apply {
        text = "▼"
        textSize = 14f
        setPadding(16, 0, 0, 0)
      }
      
      headerLayout.addView(title)
      headerLayout.addView(arrow)
      
      // The hidden description text box
      val descText = TextView(this).apply {
        text = desc
        textSize = 14f
        visibility = View.GONE
        setPadding(0, 0, 0, 16)
        alpha = 0.8f // Slight dimming to differentiate from the title
      }
      
      // Toggle logic for expanding/collapsing the description
      headerLayout.setOnClickListener {
        it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        if (descText.visibility == View.GONE) {
            descText.visibility = View.VISIBLE
            arrow.text = "▲"
        } else {
            descText.visibility = View.GONE
            arrow.text = "▼"
        }
      }

      val radioGroup = RadioGroup(this).apply {
        orientation = RadioGroup.HORIZONTAL
        val offId = View.generateViewId()
        val skipId = View.generateViewId()
        val offLabel = getString(R.string.off)
        val skipLabel = getString(R.string.skip_automatically)
        addView(RadioButton(context).apply { id = offId; text = offLabel; contentDescription = getString(R.string.content_description_action, label, offLabel); minHeight = (48 * resources.displayMetrics.density).toInt() })
        addView(RadioButton(context).apply { id = skipId; text = skipLabel; contentDescription = getString(R.string.content_description_action, label, skipLabel); minHeight = (48 * resources.displayMetrics.density).toInt() })
        check(if (SettingsManager.getSegmentAction(key) == 1) skipId else offId)
        setOnCheckedChangeListener { view, checkedId ->
          if (checkedId != -1) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val action = if (checkedId == skipId) 1 else 0
            SettingsManager.setSegmentAction(key, action)
          }
        }
      }
      
      layout.addView(headerLayout)
      layout.addView(descText)
      layout.addView(radioGroup)
      card.addView(layout)
      container.addView(card)
    }
  }

  private fun refreshStats() {
    val ms = SettingsManager.timeSavedMs
    val s = (ms / 1000) % 60
    val m = (ms / (1000 * 60)) % 60
    val h = (ms / (1000 * 60 * 60))
    val timeStr = if (h > 0) "${h}h " + getString(R.string.duration_format, m, s) else getString(R.string.duration_format, m, s)
    findViewById<TextView>(R.id.tvStats).text = getString(R.string.stats_format, SettingsManager.skippedCount, timeStr)
  }

  private fun setupFooter() {
    val tvTagline = findViewById<TextView>(R.id.tvTagline)
    val taglineText = getString(R.string.tagline)
    val spannableTagline = SpannableString(taglineText)
    val clickableSponsorBlock = object : ClickableSpan() {
      override fun onClick(widget: View) {
        widget.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/hanydd/BilibiliSponsorBlock")))
      }
    }
    val sponsorBlockStart = taglineText.indexOf("BilibiliSponsorBlock").coerceAtLeast(0)
    spannableTagline.setSpan(clickableSponsorBlock, sponsorBlockStart, sponsorBlockStart + 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    tvTagline.text = spannableTagline
    tvTagline.movementMethod = LinkMovementMethod.getInstance()
    
    val footer = findViewById<TextView>(R.id.tvFooter)
    val text = getString(R.string.made_with_love)
    val spannable = SpannableString(text)
    val clickableSpan = object : ClickableSpan() {
      override fun onClick(widget: View) {
        widget.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ezn24")))
      }
    }
    val start = text.indexOf("ezn24").coerceAtLeast(0)
    spannable.setSpan(clickableSpan, start, start + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    footer.text = spannable
    footer.movementMethod = LinkMovementMethod.getInstance()
  }

  override fun onDestroy() {
    unregisterReceiver(statsReceiver)
    unregisterReceiver(liveStateReceiver)
    privacyDialog?.dismiss()
    super.onDestroy()
  }
  
}

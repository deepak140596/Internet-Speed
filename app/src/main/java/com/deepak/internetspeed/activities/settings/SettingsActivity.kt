package com.deepak.internetspeed.activities.settings

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Directory.PACKAGE_NAME
import androidx.appcompat.app.AppCompatDelegate
import com.deepak.internetspeed.R
import com.deepak.internetspeed.database.SharedPreferenceDB
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        title = getString(R.string.settings_title)
        setupTheme()
        setupNotifications()
        setupAboutUs()

    }

    private fun setupTheme() {
        var isNightMode = SharedPreferenceDB.getNightMode(this)
        profile_theme_switch.isChecked = isNightMode

        profile_theme_switch.setOnCheckedChangeListener { _, isChecked ->
            SharedPreferenceDB.setNightMode(this@SettingsActivity, isChecked)
            setupDarkTheme()
        }
    }

    private fun setupNotifications() {
        var notificationEnabled = SharedPreferenceDB.isNotificationEnabled(this)
        var persistentNotification = SharedPreferenceDB.isPersistentNotification(this)
        settings_notification_enable_switch.isChecked = notificationEnabled
        settings_persistent_notification_switch.isChecked = persistentNotification

        settings_notification_enable_switch.setOnCheckedChangeListener { _, isChecked ->
            SharedPreferenceDB.setNotification(this@SettingsActivity, isChecked)
        }

        settings_persistent_notification_switch.setOnCheckedChangeListener { _, isChecked ->
            SharedPreferenceDB.setPersistentNotification(this@SettingsActivity, isChecked)
        }
    }

    private fun setupAboutUs() {
        settings_rate_us_ll.setOnClickListener { view ->
            startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri
                        .parse("market://details?id=$PACKAGE_NAME")
                )
            )
        }

        settings_send_feedback_tv.setOnClickListener { view ->
            startEmailIntent(getString(R.string.email_id), getString(R.string.subject_feedback))
        }

        settings_privacy_policy_tv.setOnClickListener { view ->
            startIntent(getString(R.string.privacy_policy_url))
        }
    }

    private fun startEmailIntent(emailId: String, subject: String) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailId))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        startActivity(Intent.createChooser(intent, getString(R.string.hint_choose_email)))
    }

    private fun startIntent(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun setupDarkTheme() {
        if (SharedPreferenceDB.getNightMode(this)) {
            delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}

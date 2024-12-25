package net.geidea.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.custom.mdm.CustomAPI
import dagger.hilt.android.AndroidEntryPoint
import net.geidea.payment.databinding.ActivitySettingsBinding

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private enum class Action {
        DISABLE_POWER, ENABLE_POWER, DISABLE_NAVIGATION, ENABLE_NAVIGATION, RESTART, SHUTDOWN
    }

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        setClickListener()
    }


    private fun setClickListener() {
        binding.shutdown.setOnClickListener {
            initCustomApiAndPerformAction(Action.SHUTDOWN)
        }
        binding.restart.setOnClickListener {
            initCustomApiAndPerformAction(Action.RESTART)
        }
        binding.navSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                initCustomApiAndPerformAction(Action.ENABLE_NAVIGATION)
            } else {
                initCustomApiAndPerformAction(Action.DISABLE_NAVIGATION)
            }
        }

        binding.powerSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                initCustomApiAndPerformAction(Action.ENABLE_POWER)
            } else {
                initCustomApiAndPerformAction(Action.DISABLE_POWER)
            }
        }

        binding.toolbarView.layoutBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun initCustomApiAndPerformAction(action: Action) {
        if (CustomAPI.isConnected()) {
            performAction(action)
        } else {
            CustomAPI.init(this@SettingsActivity) {
                performAction(action)
            }
        }
    }


    private fun performAction(action: Action) {
        when (action) {
            Action.DISABLE_POWER -> CustomAPI.disablePowerKey(true)
            Action.ENABLE_POWER -> CustomAPI.disablePowerKey(false)
            Action.DISABLE_NAVIGATION -> {
                CustomAPI.setRecentKey(false)
                CustomAPI.setVolumeKey(false)
                CustomAPI.setHomeKey(false)
                CustomAPI.setNavigationBar(false)
                CustomAPI.setStatusBar(false)
            }

            Action.ENABLE_NAVIGATION -> {
                CustomAPI.setRecentKey(true)
                CustomAPI.setVolumeKey(true)
                CustomAPI.setHomeKey(true)
                CustomAPI.setNavigationBar(true)
                CustomAPI.setStatusBar(true)
            }

            Action.RESTART -> CustomAPI.reboot()
            Action.SHUTDOWN -> CustomAPI.shutdown()
        }
    }


}
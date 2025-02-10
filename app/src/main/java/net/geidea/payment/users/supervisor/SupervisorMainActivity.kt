package net.geidea.payment.users.supervisor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import net.geidea.payment.MainMenuActivity
import net.geidea.payment.ManualSaleReversal
import net.geidea.payment.R
import net.geidea.payment.TxnType
import net.geidea.payment.customdialog.DialogLogoutConfirm
import net.geidea.payment.databinding.ActivitySupervisorMainBinding
import net.geidea.payment.databinding.NavHeaderBinding
import net.geidea.payment.help.HelpMainActivity
import net.geidea.payment.login.LoginMainActivity

class SupervisorMainActivity : AppCompatActivity() {

    // Declare the binding variable
    private lateinit var binding: ActivitySupervisorMainBinding
    private val TAG = "SupervisorManageCashierActivity" // For logging purposes
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle // For managing the drawer layout
     private lateinit var sharedPreferences1: SharedPreferences
     private lateinit var editor:SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", TAG)
        // Initialize view binding
        // Inflate the layout using DataBindingUtil
        binding = DataBindingUtil.setContentView(this, R.layout.activity_supervisor_main)
        sharedPreferences1=getSharedPreferences("SHARED_DATA",Context.MODE_PRIVATE)
        editor=sharedPreferences1.edit()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@SupervisorMainActivity, LoginMainActivity::class.java))
                finish()
            }
        })
        if (!sharedPreferences1.getBoolean("MANUAL_ENTRY_ENABLED",false)){
            binding.supervisorManualCardEntery.visibility=View.GONE
        }


        // Setup ActionBarDrawerToggle for navigation drawer
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, binding.supervisorDrawerLayout, R.string.open, R.string.close
        )
        binding.supervisorDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Set up toolbar navigation icon click listener
        // Set up toolbar navigation icon click listener
        binding.toolbar.navIcon?.setOnClickListener {
            binding.supervisorDrawerLayout.openDrawer(GravityCompat.START)
        }

        // Retrieve user credentials from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        val userType = sharedPreferences.getString("userType", "") ?: ""

        navItemClickListener()
        setUpCardViewListeners()

        // Update the navigation header
        updateNavHeader("Username: "+ username +"", "User Role: "+userType+"")
    }

    private fun navItemClickListener() {
        // Set up navigation drawer interactions using binding
        binding.supervisorNavigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_manage_cashier -> {
                    startActivity(Intent(this, SupervisorManageCashierActivity::class.java))
                }
                R.id.nav_home -> {
                    // Handle navigation to Config Terminal
                }
                R.id.nav_terminal_info -> {
                    // Handle navigation to Terminal Info
                }
                R.id.nav_settings -> {
                    // Handle navigation to Settings
                }
                R.id.nav_help -> {
                    startActivity(Intent(this, HelpMainActivity::class.java))
                }
                R.id.nav_logout -> {
                    val exitDialog = DialogLogoutConfirm(
                        this,
                        title = "Logout",
                        message = "Are you sure you want to logout?",
                        cancelBtn = "Cancel",
                        logoutBtn = "Logout"
                    ) {
                        startActivity(Intent(this, MainMenuActivity::class.java))
                        finish() // Close the app
                    }
                    exitDialog.show()
                }
            }
            binding.supervisorDrawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setUpCardViewListeners() {
        // Set up onClickListeners for CardViews using binding
        binding.supervisorManageCashier.setOnClickListener {
            val intent = Intent(this@SupervisorMainActivity, SupervisorManageCashierActivity::class.java)
            startActivity(intent)
        }

        binding.supervisorConfigTerminal.setOnClickListener {
            // Handle Config Terminal click
        }

        binding.supervisorTerminalInfo.setOnClickListener {
            // Handle Terminal Info click
        }

        binding.supervisorSettings.setOnClickListener {
            // Handle Settings click
        }

        binding.supervisorSummaryReport.setOnClickListener {
            // Handle Summary Report click
        }

        binding.supervisorReprint.setOnClickListener {
            // Handle Reprint click
        }
        binding.supervisorHelp.setOnClickListener {
            // Handle Reprint click
        }
        binding.supervisorManualCardEntery.setOnClickListener {
            editor.putString("TXN_TYPE",TxnType.M_PURCHASE)
            editor.putString("transaction",TxnType.M_PURCHASE)
            editor.commit()
            startActivity(Intent(this, ManualSaleReversal::class.java))
            //startActivity(Intent(this, AmountActivity::class.java))
            finish()
        }
    }

    // Method to update the Navigation Header Texts
    private fun updateNavHeader(username: String, userrole: String) {
        // Get the navigation view binding for the header
        val headerBinding = NavHeaderBinding.bind(binding.supervisorNavigationView.getHeaderView(0))

        // Update the username and userrole
        headerBinding.username.text = username
        headerBinding.userrole.text = userrole

    }
}

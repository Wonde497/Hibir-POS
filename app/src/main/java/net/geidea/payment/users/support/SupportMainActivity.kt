package net.geidea.payment.users.support

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import net.geidea.payment.MainMenuActivity
import net.geidea.payment.R
import net.geidea.payment.customdialog.DialogLogoutConfirm
import net.geidea.payment.databinding.ActivitySupportMainBinding
import net.geidea.payment.databinding.NavHeaderBinding
import net.geidea.payment.help.HelpMainActivity
import net.geidea.payment.users.admin.AdminManageSupportActivity

@AndroidEntryPoint
class SupportMainActivity : AppCompatActivity() {
    private val TAG = "SupportMainActivity" // For logging purposes
    private lateinit var binding: ActivitySupportMainBinding // Binding object for layout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle // For managing the drawer layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", TAG)

        // Inflate the layout using DataBindingUtil
        binding = DataBindingUtil.setContentView(this, R.layout.activity_support_main)

        // Setup ActionBarDrawerToggle for navigation drawer
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, binding.supportDrawerLayout, R.string.open, R.string.close
        )
        binding.supportDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Set up toolbar navigation icon click listener
        binding.toolbar.navIcon?.setOnClickListener {
            binding.supportDrawerLayout.openDrawer(GravityCompat.START)
        }

        // Retrieve user credentials from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        val userType = sharedPreferences.getString("userType", "") ?: ""


        // Set up ItemClickListener for Navigation View
        navItemClickListener()

        // Set up OnClickListeners for CardViews
        setUpCardViewListeners()

        // Update the navigation header
        updateNavHeader("Username: "+username+"", "User Role: "+userType+"")
    }

    // Function to handle navigation item clicks
    private fun navItemClickListener() {
        binding.supportNavigationView.setNavigationItemSelectedListener { menuItem ->
            binding.supportDrawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                    //val intent = Intent(this, SettingsActivity::class.java) // Update to SettingsActivity
                    //startActivity(intent)
                }
                R.id.nav_help -> {
                    Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HelpMainActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_about -> {
                    Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_share -> {
                    Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_logout -> {
                    Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
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
                else -> false
            }
            true
        }
    }

    private fun setUpCardViewListeners() {
        binding.supportManageSupervisors.setOnClickListener {
            Toast.makeText(this, "Manage Supervisors", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@SupportMainActivity, SupportManageSupervisorActivity::class.java)
            startActivity(intent)
        }

        binding.supportConfigTerminal.setOnClickListener {
            Toast.makeText(this, "Config Terminal", Toast.LENGTH_SHORT).show()
           val intent = Intent(this, CommunicationConfig::class.java)
            startActivity(intent)
        }

        binding.supportTerminalInfo.setOnClickListener {
            Toast.makeText(this, "Terminal Info", Toast.LENGTH_SHORT).show()
           // val intent = Intent(this, TerminalInfoActivity::class.java)
           // startActivity(intent)
        }

        binding.supportSettlement.setOnClickListener {
            Toast.makeText(this, "Settlement ", Toast.LENGTH_SHORT).show()
            //val intent = Intent(this, SettlementActivity::class.java)
           // startActivity(intent)
        }

        binding.supportSummaryReport.setOnClickListener {
            Toast.makeText(this, "Summary Report ", Toast.LENGTH_SHORT).show()
            //val intent = Intent(this, SummaryReportActivity::class.java)
           // startActivity(intent)
        }

        binding.supportReprint.setOnClickListener {
            Toast.makeText(this, "Reprint ", Toast.LENGTH_SHORT).show()
            //val intent = Intent(this, ReprintActivity::class.java)
            //startActivity(intent)
        }

        binding.supportSettings.setOnClickListener {
            Toast.makeText(this, "Settings ", Toast.LENGTH_SHORT).show()
            //val intent = Intent(this, SettingsActivity::class.java)
            //startActivity(intent)
        }
    }
    // Method to update the Navigation Header Texts
    private fun updateNavHeader(username: String, userrole: String) {
        // Get the navigation view binding for the header
        val headerBinding = NavHeaderBinding.bind(binding.supportNavigationView.getHeaderView(0))

        // Update the username and userrole
        headerBinding.username.text = username
        headerBinding.userrole.text = userrole
    }
}

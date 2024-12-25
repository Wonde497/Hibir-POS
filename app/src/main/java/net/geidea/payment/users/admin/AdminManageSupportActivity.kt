package net.geidea.payment.users.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import net.geidea.payment.MainMenuActivity
import net.geidea.payment.R
import net.geidea.payment.customdialog.DialogLogoutConfirm
import net.geidea.payment.databinding.ActivityAdminManageSupportBinding
import net.geidea.payment.help.HelpMainActivity

class AdminManageSupportActivity : AppCompatActivity() {

    // Declare the binding variable
    private lateinit var binding: ActivityAdminManageSupportBinding
    private val TAG = "AdminManageSupportActivity" // For logging purposes
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle // For managing the drawer layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("TAG", TAG)

        // Inflate the layout using DataBindingUtil
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_manage_support)

        // Setup ActionBarDrawerToggle for navigation drawer
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, binding.adminDrawerLayout, R.string.open, R.string.close
        )
        binding.adminDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Set up toolbar navigation icon click listener
        // Set up toolbar navigation icon click listener
        binding.toolbar.navIcon?.setOnClickListener {
            binding.adminDrawerLayout.openDrawer(GravityCompat.START)
        }

        // Set up navigation drawer
        setupDrawer()

        navItemClickListener()
        setUpCardViewListeners()

        setUpImageButtonClickListeners()


    }


    private fun navItemClickListener() {
        binding.adminNavigationView.setNavigationItemSelectedListener { menuItem ->
            binding.adminDrawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_help -> {
                    Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HelpMainActivity::class.java))
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
        // Set onClickListeners for each card
        binding.adminAddSupport.setOnClickListener {
            // Handle the 'Add Support' click event
            startActivity(Intent(this, AddSupportActivity::class.java))
        }



        binding.adminViewSupport.setOnClickListener {
            startActivity(Intent(this, ViewSupport::class.java))
        }

        binding.adminBlockSupport.setOnClickListener {
            // Handle the 'Block Support' click event
        }

        binding.adminEnableSupport.setOnClickListener {
            // Handle the 'Enable Support' click event
        }

        binding.adminChangeSupportPin.setOnClickListener {
            // Handle the 'Change Support PIN' click event
        }

        binding.deleteSupport.setOnClickListener {
            startActivity(Intent(this, DeleteSupport::class.java))

        }
    }

    // Function to set up the navigation drawer
    private fun setupDrawer() {
        // Example of how to configure the drawer
        // Use binding.adminDrawerLayout and binding.adminNavigationView
        // Implement your drawer logic here
    }

    private fun setUpImageButtonClickListeners() {
        // Initialize each ImageButton
        val addSupportButton: ImageButton = findViewById(R.id.admin_add_support_icon)
        val viewSupportButton: ImageButton = findViewById(R.id.admin_view_support_icon)
        val blockSupportButton: ImageButton = findViewById(R.id.admin_block_support_icon)
        val enableSupportButton: ImageButton = findViewById(R.id.admin_enable_support_icon)
        val changeSupportPinButton: ImageButton = findViewById(R.id.admin_change_support_pin_icon)
        val deleteSupportButton: ImageButton = findViewById(R.id.delete_support_icon)

        // Set onClick listeners for each button
        addSupportButton.setOnClickListener {
            startActivity(Intent(this, AddSupportActivity::class.java))

        }

        viewSupportButton.setOnClickListener {
            startActivity(Intent(this, ViewSupport::class.java))

        }

        blockSupportButton.setOnClickListener {

        }

        enableSupportButton.setOnClickListener {

        }

        changeSupportPinButton.setOnClickListener {

        }
        deleteSupportButton.setOnClickListener {
            startActivity(Intent(this, DeleteSupport::class.java))
        }
    }

}

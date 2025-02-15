package net.geidea.payment.users.supervisor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import net.geidea.payment.MainMenuActivity
import net.geidea.payment.R
import net.geidea.payment.customdialog.DialogLogoutConfirm
import net.geidea.payment.databinding.ActivitySupervisorManageCashierBinding
import net.geidea.payment.help.HelpMainActivity
import net.geidea.payment.users.admin.AddSupportActivity
import net.geidea.payment.users.support.AddSupervisorActivity
import net.geidea.payment.users.support.SupportManageSupervisorActivity
import net.geidea.payment.users.support.SupportManageSupervisorActivity.Companion
import net.geidea.payment.users.support.ViewSupervisor

class SupervisorManageCashierActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupervisorManageCashierBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    companion object {
        private const val TAG = "SupervisorManageCashierActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // Initialize ViewBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_supervisor_manage_cashier)



        setupDrawer()
        setupToolbar()
        setupNavigationClickListener()
        setupCardViewListeners()

        setImageButtonListeners()
    }

    private fun setupDrawer() {
        // Initialize the ActionBarDrawerToggle and sync state
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, binding.supervisorDrawerLayout, R.string.open, R.string.close
        )
        binding.supervisorDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    private fun setupToolbar() {
        // Set the toolbar navigation icon listener
        binding.toolbar.navIcon?.setOnClickListener {
            binding.supervisorDrawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupNavigationClickListener() {
        binding.supervisorNavigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle item click and close the drawer
            binding.supervisorDrawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.nav_manage_cashier -> navigateToManageCashier()
                R.id.nav_home -> navigateToHome()
                R.id.nav_terminal_info -> navigateToTerminalInfo()
                R.id.nav_settings -> navigateToSettings()
                R.id.nav_help -> navigateToHelp()
                R.id.nav_logout -> handleLogout()
                else -> Log.w(TAG, "Unknown navigation item selected: ${menuItem.itemId}")
            }
            true
        }
    }

    private fun setupCardViewListeners() {
        // Handling click events for the card views

        binding.supervisorAddCashier.setOnClickListener {
            handleAddCashier()
        }
        binding.supervisorViewCashier.setOnClickListener {
            handleViewCashier()
        }
        binding.supervisorBlockCashier.setOnClickListener {
            handleBlockCashier()
        }
        binding.supervisorChangeCashierPin.setOnClickListener {
            handleChangeCashierPin()
        }
        binding.deleteCashier.setOnClickListener {
            handleDeleteCashier()
        }

        handleEnableCashier()
    }

    // Navigation methods
    private fun navigateToManageCashier() {
        Log.d(TAG, "Navigating to Manage Cashier")
        Snackbar.make(binding.root, "Navigating to Manage Cashier", Snackbar.LENGTH_SHORT).show()
    }

    private fun navigateToHome() {
        Log.d(TAG, "Navigating to Home")
        Snackbar.make(binding.root, "Navigating to Home", Snackbar.LENGTH_SHORT).show()
    }

    private fun navigateToTerminalInfo() {
        Log.d(TAG, "Navigating to Terminal Info")
        Snackbar.make(binding.root, "Navigating to Terminal Info", Snackbar.LENGTH_SHORT).show()
    }

    private fun navigateToSettings() {
        Log.d(TAG, "Navigating to Settings")
        Snackbar.make(binding.root, "Navigating to Settings", Snackbar.LENGTH_SHORT).show()
    }

    private fun navigateToHelp() {
        Log.d(TAG, "Navigating to Help")
        startActivity(Intent(this, HelpMainActivity::class.java))
    }

    private fun handleLogout() {
        Log.d(TAG, "Logging out")
       // Snackbar.make(binding.root, "Logging out...", Snackbar.LENGTH_SHORT).show()
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

    // CardView Actions
    private fun handleAddCashier() {
        Log.d(TAG, "Add Cashier clicked")
        Snackbar.make(binding.root, "Adding new Cashier...", Snackbar.LENGTH_SHORT).show()
        // Add logic for adding cashier
        startActivity(Intent(this, AddCashierActivity::class.java))

    }

    private fun handleViewCashier() {
        Log.d(TAG, "View Cashier clicked")
        Snackbar.make(binding.root, "Fetching Cashier details...", Snackbar.LENGTH_SHORT).show()
        startActivity(Intent(this, ViewCashier::class.java))
    }

    private fun handleBlockCashier() {
        Log.d(TAG, "Block Cashier clicked")
        Snackbar.make(binding.root, "Blocking Cashier...", Snackbar.LENGTH_SHORT).show()
        // Add logic for blocking cashier
    }

    private fun handleEnableCashier() {
        Log.d(TAG, "Enable Cashier clicked")



    }

    private fun handleChangeCashierPin() {
        Log.d(TAG, "Change Cashier PIN clicked")
        Snackbar.make(binding.root, "Changing Cashier PIN...", Snackbar.LENGTH_SHORT).show()
        // Add logic for changing cashier PIN
    }

    private fun handleDeleteCashier() {
        Log.d(TAG, "Delete Cashier clicked")
        Snackbar.make(binding.root, "Deleting Cashier...", Snackbar.LENGTH_SHORT).show()
        startActivity(Intent(this, DeleteCashier::class.java))

    }

    private fun setImageButtonListeners() {
        val addCashierButton: ImageButton = findViewById(R.id.supervisor_add_cashier_icon)
        val viewCashierButton: ImageButton = findViewById(R.id.supervisor_view_cashier_icon)
        val blockCashierButton: ImageButton = findViewById(R.id.supervisor_block_cashier_icon)
        //val enableCashierButton: ImageButton = findViewById(R.id.supervisor_enable_cashier_icon)
        val changeCashierPinButton: ImageButton = findViewById(R.id.supervisor_change_cashier_pin_icon)
        val deleteCashierButton: ImageButton = findViewById(R.id.delete_cashier_icon)

        addCashierButton.setOnClickListener {
            // Add Cashier action
            val intent = Intent(this, AddCashierActivity::class.java)
            startActivity(intent)
        }

        viewCashierButton.setOnClickListener {
            // View Cashier action
            val intent = Intent(this, ViewCashier::class.java)
            startActivity(intent)
        }

        blockCashierButton.setOnClickListener {
            // Block Cashier action
            // Implement your logic here
        }

       /* enableCashierButton.setOnClickListener {
            // Enable Cashier action
            // Implement your logic here
        }*/

        changeCashierPinButton.setOnClickListener {
            // Change Cashier PIN action
            // Implement your logic here
        }

        deleteCashierButton.setOnClickListener {
            startActivity(Intent(this, DeleteCashier::class.java))
        }
    }
}

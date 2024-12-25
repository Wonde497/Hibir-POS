package net.geidea.payment.users.support

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import net.geidea.payment.MainMenuActivity
import net.geidea.payment.R
import net.geidea.payment.customdialog.DialogLogoutConfirm
import net.geidea.payment.databinding.ActivitySupportManageSupervisorBinding
import net.geidea.payment.help.HelpMainActivity
import net.geidea.payment.users.admin.AddSupportActivity
import net.geidea.payment.users.admin.RegisterSupport

class SupportManageSupervisorActivity : AppCompatActivity() {

    // Declare the binding variable
    private lateinit var binding: ActivitySupportManageSupervisorBinding

    // Declare the drawer layout and navigation view
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle // For managing the drawer layout
    companion object {
        private const val TAG = "SupportManageSupervisorActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding with the layout inflater
        binding = ActivitySupportManageSupervisorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the drawer layout and navigation view
        drawerLayout = binding.manageSupervisorsDrawerLayout
        navigationView = binding.supervisorsNavigationView
// Setup ActionBarDrawerToggle for navigation drawer
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, binding.manageSupervisorsDrawerLayout, R.string.open, R.string.close
        )
        binding.manageSupervisorsDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Set up toolbar navigation icon click listener
        binding.toolbar.navIcon?.setOnClickListener {
            binding.manageSupervisorsDrawerLayout.openDrawer(GravityCompat.START)
        }


        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawer(GravityCompat.START)
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

        // Set click listeners for actions
        binding.AddSupervisors.setOnClickListener {
            // Code to handle Add Supervisors click
            startActivity(Intent(this, AddSupervisorActivity::class.java))

        }

        binding.viewSupervisors.setOnClickListener {
            Log.d(TAG, "View Super clicked")
            Snackbar.make(binding.root, "Fetching Supervisor details...", Snackbar.LENGTH_SHORT).show()
            startActivity(Intent(this, ViewSupervisor::class.java))

        }

        binding.blockSupervisors.setOnClickListener {
            // Code to handle Block Supervisors click
        }

        binding.enableSupervisors.setOnClickListener {
            // Code to handle Enable Supervisors click
        }

        binding.changeSupervisorPin.setOnClickListener {
            // Code to handle Change Supervisor PIN click
        }

        binding.deleteSupervisor.setOnClickListener {
            startActivity(Intent(this, DeleteSupervisor::class.java))
        }



        //Calling Func

        setUpImageButtonClickListeners()
    }

    private fun setUpImageButtonClickListeners() {
        val onClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.add_supervisors_icon -> {
                    Log.d(TAG, "Add Super")
                    Snackbar.make(binding.root, "Adding Supervisor details...", Snackbar.LENGTH_SHORT).show()
                    startActivity(Intent(this, AddSupervisorActivity::class.java))                }
                R.id.view_supervisors_icon -> {
                    Log.d(TAG, "View Super clicked")
                    Snackbar.make(binding.root, "Fetching Supervisor details...", Snackbar.LENGTH_SHORT).show()
                    startActivity(Intent(this, ViewSupervisor::class.java))
                }
                R.id.block_supervisors_icon -> {
                    // Handle Block Supervisors click
                }
                R.id.enable_supervisors_icon -> {
                    // Handle Enable Supervisors click
                }
                R.id.change_supervisor_pin_icon -> {
                    // Handle Change Supervisor PIN click
                }
                R.id.delete_supervisor_icon -> {
                    startActivity(Intent(this, DeleteSupervisor::class.java))
                }
            }
        }

        // Set the listener for each ImageButton
        with(binding) {
            addSupervisorsIcon.setOnClickListener(onClickListener)
            viewSupervisorsIcon.setOnClickListener(onClickListener)
            blockSupervisorsIcon.setOnClickListener(onClickListener)
            enableSupervisorsIcon.setOnClickListener(onClickListener)
            changeSupervisorPinIcon.setOnClickListener(onClickListener)
            deleteSupervisorIcon.setOnClickListener(onClickListener)
        }
    }

}

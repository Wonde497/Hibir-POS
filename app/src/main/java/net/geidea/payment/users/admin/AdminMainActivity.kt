package net.geidea.payment.users.admin



import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import net.geidea.payment.MainActivity
import net.geidea.payment.MainMenuActivity
import net.geidea.payment.R
import net.geidea.payment.customdialog.DialogLogoutConfirm
import net.geidea.payment.databinding.ActivityAdminMainBinding
import net.geidea.payment.databinding.NavHeaderBinding
import net.geidea.payment.help.HelpMainActivity
import net.geidea.payment.login.LoginMainActivity

@AndroidEntryPoint
class AdminMainActivity : AppCompatActivity() {
    private val TAG = "MainMenuActivity" // For logging purposes
    private lateinit var binding: ActivityAdminMainBinding // Binding object for layout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle // For managing the drawer layout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", TAG)

        // Inflate the layout using DataBindingUtil
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_main)

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

        // Retrieve user credentials from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        val userType = sharedPreferences.getString("userType", "") ?: ""


        // Set up ItemClickListener for Navigation View
        navItemClickListener()
        // Set up OnClickListeners for CardViews
        setUpCardViewListeners()

        val Int = R.drawable.nib_account_box_50

        // Update the navigation header
        updateNavHeader("Username:  "+username + "", "User Role: "+ userType+"", Int)


    }

    // Function to handle navigation item clicks
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
        binding.adminManageTransaction.setOnClickListener {
            Toast.makeText(this, "Manage Transaction ", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@AdminMainActivity, AdminManageTransaction::class.java)
            startActivity(intent)
        }

        binding.adminManageSupport.setOnClickListener {
           // Toast.makeText(this, "Manage Support clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@AdminMainActivity, AdminManageSupportActivity::class.java)
            startActivity(intent)
        }

        binding.adminReceiveKey.setOnClickListener {
            Toast.makeText(this, "Receive Key clicked", Toast.LENGTH_SHORT).show()
        }

        binding.adminConfigTerminal.setOnClickListener {
            Toast.makeText(this, "Config Terminal clicked", Toast.LENGTH_SHORT).show()
            //val intent = Intent(this@AdminMainActivity, HelpMainActivity::class.java)
           // startActivity(intent)
        }
    }
    // Method to update the Navigation Header Texts
    private fun updateNavHeader(username: String, userrole: String, userImageResId: Int) {
        // Get the navigation view binding for the header
        val headerBinding = NavHeaderBinding.bind(binding.adminNavigationView.getHeaderView(0))
        // Update the username and userrole
        headerBinding.username.text = username
        headerBinding.userrole.text = userrole
     //   headerBinding.navImage.drawable = userimg
        headerBinding.navImage.setImageResource(userImageResId)
    }

}

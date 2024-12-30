package net.geidea.payment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.pos.sdk.security.PedKcvInfo
import dagger.hilt.android.AndroidEntryPoint
import net.geidea.payment.customdialog.DialogLogoutConfirm
import net.geidea.payment.databinding.ActivityMainMenuBinding
import net.geidea.payment.help.HelpMainActivity
import net.geidea.payment.kernelconfig.view.KernelConfigActivity
import net.geidea.payment.kernelconfig.view.KernelConfigActivity2
import net.geidea.payment.login.LoginMainActivity
import net.geidea.payment.report.Report
import net.geidea.payment.users.cashier.CashierMainActivity
import net.geidea.payment.users.supervisor.SupervisorManageCashierActivity
import net.geidea.payment.utils.commonMethods

@AndroidEntryPoint
class MainMenuActivity : AppCompatActivity() {
    private val TAG = "MainMenuActivity" // For logging purposes
    private lateinit var binding: ActivityMainMenuBinding // Binding object for layout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle // For managing the drawer layout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor
    private val imageResIds = listOf(
        R.drawable.hibret_main,
        R.drawable.hibret_card,
        R.drawable.hiret_open,
        R.drawable.hibrhaq,
        R.drawable.hibret_mobile_bank,
        R.drawable.hibret_main,
        R.drawable.hibret_mobile_bank
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", TAG)

        // Inflate the layout using DataBindingUtil
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_menu)
        sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()

        // Setup ActionBarDrawerToggle for navigation drawer
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, R.string.open, R.string.close
        )
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Set up toolbar navigation icon click listener
        binding.toolbar.navIcon?.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // Set up ItemCLickListener for Navigation View
        navItemCLickListener()
        // Set up OnClickListeners for CardViews
        setUpCardViewListeners()
        // Set up ViewPager
        setupViewPager()

        // Set onClickListeners for ImageButtons
        setUpImageButtonListeners()

    }

    // Function to handle navigation item clicks
    private fun navItemCLickListener() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
//                  var  rep = Report(this,this)
//                    //rep.printdetailedReport()
//                    rep.printsummaryReport()

                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_kernel_config -> {
                    Toast.makeText(this, "Loading Kernel Config", Toast.LENGTH_SHORT).show()
                    val intent=Intent(this, KernelConfigActivity2::class.java)
                    startActivity(intent)
                }

                R.id.nav_help -> {
                    startActivity(Intent(this, HelpMainActivity::class.java))
                }
                R.id.nav_about -> {
                    Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_share -> {
                    Toast.makeText(this, "Rate", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_exit -> {
                    val exitDialog = DialogLogoutConfirm(
                        this,
                        title = "Exit App",
                        message = "Are you sure you want to close the app?",
                        cancelBtn = "Cancel",
                        logoutBtn = "Exit"
                    ) {
                        // Perform your exit app action here
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
        binding.topCardView.setOnClickListener {
           showTransactionBottomSheet()
        }
        binding.cardView1.setOnClickListener {
            sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
            val intent = Intent(this@MainMenuActivity, LoginMainActivity::class.java)
            startActivity(intent)
        }

        binding.cardView2.setOnClickListener {
            editor.putString("TXN_TYPE",Txntype.purchase)
            editor.putString("transaction",Txntype.purchase)
            editor.commit()
           // Toast.makeText(this, "CardView 2 clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainMenuActivity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.cardView3.setOnClickListener {
           // Toast.makeText(this, "CardView 3 clicked", Toast.LENGTH_SHORT).show()
            sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
            val intent = Intent(this@MainMenuActivity, CashierMainActivity::class.java)
            startActivity(intent)
        }

        binding.cardView4.setOnClickListener {
            //Toast.makeText(this, "CardView 4 clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainMenuActivity, HelpMainActivity::class.java)
            startActivity(intent)
        }
    }

    // Set onClickListeners for ImageButtons
    private fun setUpImageButtonListeners() {
        binding.transactionsImagebtn.setOnClickListener {
           showTransactionBottomSheet()
        }

        binding.mainMenuLogin.setOnClickListener {
            sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
            val intent = Intent(this@MainMenuActivity, LoginMainActivity::class.java)
            startActivity(intent)
        }

        binding.mainMenuSale.setOnClickListener {
            editor.putString("TXN_TYPE",Txntype.purchase)
            editor.commit()
            //Toast.makeText(this, "CardView 2 clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainMenuActivity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.mainMenuCashier.setOnClickListener {
            sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
            val intent = Intent(this@MainMenuActivity, CashierMainActivity::class.java)
            startActivity(intent)
        }

        binding.mainMenuHelp.setOnClickListener {
            sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
            val intent = Intent(this@MainMenuActivity, HelpMainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setupViewPager() {
        val adapter = ImageSliderAdapter(imageResIds)
        binding.viewPager.adapter = adapter

        // Automatically scroll the ViewPager
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val currentItem = binding.viewPager.currentItem
                val nextItem = if (currentItem + 1 < imageResIds.size) currentItem + 1 else 0
                binding.viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 3000) // Adjust the delay as needed
            }
        }
        handler.postDelayed(runnable, 3000)
    }


    private fun showTransactionBottomSheet() {
        val dialog = Dialog(this)
        try {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            dialog.setContentView(R.layout.dialog_transactions)
            val saleBtn=dialog.findViewById <ImageButton>(R.id.transaction_sale_icon)
            val reversalBtn=dialog.findViewById<ImageButton>(R.id.transaction_reversal_icon)
            val closeDialogBtn=dialog.findViewById <ImageButton>(R.id.closeDialog)
            saleBtn.setOnClickListener {
                editor.putString("TXN_TYPE",Txntype.purchase)
                editor.putString("transaction",Txntype.purchase)
                editor.commit()
                startActivity(Intent(this,MainActivity::class.java))
            }
            reversalBtn.setOnClickListener {
                editor.putString("TXN_TYPE",Txntype.reversal)
                editor.commit()
                startActivity(Intent(this,ReversalActivity::class.java))


            }
            closeDialogBtn.setOnClickListener{
                dialog.dismiss()
            }

            val window = dialog.window
            window?.let {
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(it.attributes)
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                layoutParams.height = (resources.displayMetrics.heightPixels * 0.7).toInt()
                layoutParams.gravity = Gravity.BOTTOM
                it.attributes = layoutParams
            }

            dialog.show()
        } catch (e: Exception) {
            Log.e("DialogError", "Failed to create or show dialog", e)
        }
    }
}

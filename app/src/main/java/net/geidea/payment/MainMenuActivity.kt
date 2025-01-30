package net.geidea.payment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import net.geidea.payment.customdialog.DialogLogoutConfirm
import net.geidea.payment.databinding.ActivityMainMenuBinding
import net.geidea.payment.help.HelpMainActivity
import net.geidea.payment.kernelconfig.view.KernelConfigActivity2
import net.geidea.payment.login.LoginMainActivity
import net.geidea.payment.users.cashier.CashierMainActivity
import java.util.Locale

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

        // Language Change
        changeLanguage()

        // Set up ItemCLickListener for Navigation View
        navItemCLickListener()
        // Set up OnClickListeners for CardViews
        setUpCardViewListeners()
        // Set up ViewPager
        setupViewPager()

        // Set onClickListeners for ImageButtons
        setUpImageButtonListeners()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitAppDialog()
            }
        })

        // Check network status
        if (!isNetworkConnected()) {
            showNoInternetDialog()
        }

    }


    //................Language......................................................................

    private fun changeLanguage() {
        val spinner: Spinner = findViewById(R.id.languageSpinner)

        // Set up the spinner with language options
        val languages = arrayOf("English", "አማረኛ", "Afaan Oromo", "ትግረኛ")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set selected language based on current preference
        val currentLang = getLanguagePreference()
        val selectedPosition = languages.indexOf(getLanguageDisplayName(currentLang))
        spinner.setSelection(selectedPosition)

        // Listen for language change
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = when (position) {
                    1 -> "am" // Amharic
                    2 -> "om" // Afaan Oromo
                    3 -> "ti" // Tigrinya
                    else -> "en" // English
                }
                // Get current language preference to avoid unnecessary language reload
                val currentLanguage = getLanguagePreference()

                // If the selected language is different from the current one, update it
                if (selectedLanguage != currentLanguage) {
                    // Save the selected language
                    setLanguagePreference(selectedLanguage)

                    // Apply language change
                    val locale = when (selectedLanguage) {
                        "am" -> Locale("am")
                        "om" -> Locale("om")
                        "ti" -> Locale("ti")
                        else -> Locale("en")
                    }
                    Locale.setDefault(locale)

                    val config = resources.configuration
                    config.setLocale(locale)

                    // Update the configuration to apply the new language
                    resources.updateConfiguration(config, resources.displayMetrics)

                    // Recreate the activity to reflect the language change
                    recreate()

                    Toast.makeText(this@MainMenuActivity, "Language changed to ${languages[position]}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Handle case where no item is selected, if needed
            }
        }
    }

    private fun getLanguagePreference(): String {
        val sharedPref = getSharedPreferences("app_settings", MODE_PRIVATE)
        return sharedPref.getString("language", "en") ?: "en"
    }

    private fun setLanguagePreference(language: String) {
        val sharedPref = getSharedPreferences("app_settings", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("language", language)
            apply()
        }
    }

    private fun getLanguageDisplayName(languageCode: String): String {
        return when (languageCode) {
            "am" -> "አማረኛ"
            "om" -> "Afaan Oromo"
            "ti" -> "ትግረኛ"
            else -> "English"
        }
    }



    // Function to handle navigation item clicks
    private fun navItemCLickListener() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
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
                        title = getString(R.string.exit_app_title),
                        message = getString(R.string.exit_app_message),
                        cancelBtn = getString(R.string.exit_app_cancel),
                        logoutBtn = getString(R.string.exit_app_exit)
                    ) {
                        // Perform your exit app action here
                        //finish() // Close the app
                        finishAndRemoveTask()

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
            val refundBtn=dialog.findViewById<ImageButton>(R.id.transaction_refund_icon)

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
            refundBtn.setOnClickListener{

                val dbHandler=DBHandler(this)

                val txnDataList:List<Map<String,String>> = dbHandler.getTxnData()
                if(txnDataList.isNotEmpty()){
                    Toast.makeText(this,"Settle first",Toast.LENGTH_SHORT).show()
                }else {
                    editor.putString("TXN_TYPE",Txntype.refund)
                    editor.commit()

                    startActivity(Intent(this, RefundActivity::class.java))
                }

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

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    private fun showNoInternetDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_no_internet_title))
            .setMessage(getString(R.string.dialog_no_internet_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.dialog_connection_settings_button)) { _, _ ->
                //startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
            }
            .setNegativeButton(getString(R.string.dialog_connection_exit_button)) { dialog, _ ->
                dialog.dismiss()
                //finish()
            }
            .show()
    }

    private fun exitAppDialog(){
        val exitDialog = DialogLogoutConfirm(
            this,
            title = getString(R.string.exit_app_title),
            message = getString(R.string.exit_app_message),
            cancelBtn = getString(R.string.exit_app_cancel),
            logoutBtn = getString(R.string.exit_app_exit)
        ) {
            // Perform your exit app action here
            //finish() // Close the app
            finishAndRemoveTask()

        }
        exitDialog.show()
    }
}
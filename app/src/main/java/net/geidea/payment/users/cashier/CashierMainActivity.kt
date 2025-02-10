package net.geidea.payment.users.cashier

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.pos.sdk.security.POIHsmManage
import dagger.hilt.android.AndroidEntryPoint
import net.geidea.payment.DBHandler
import net.geidea.payment.MainMenuActivity
import net.geidea.payment.R
import net.geidea.payment.TxnType
import net.geidea.payment.com.Comm
import net.geidea.payment.users.UserLogoutConfirmDialog
import net.geidea.payment.databinding.ActivityCashierMainBinding
import net.geidea.payment.databinding.NavHeaderBinding
import net.geidea.payment.help.HelpMainActivity
import net.geidea.payment.report.Report
import net.geidea.payment.tlv.HexUtil
import net.geidea.payment.transaction.model.TransData
import net.geidea.payment.transaction.model.TransData.RequestFields
import net.geidea.payment.transaction.viewmodel.CardReadViewModel
import net.geidea.payment.utils.MASTER_KEY_INDEX
import net.geidea.payment.utils.SESSION_PIN_KEY_INDEX
import net.geidea.payment.utils.commonMethods
import net.geidea.utils.dialog.SweetAlertDialog

@AndroidEntryPoint
class CashierMainActivity : AppCompatActivity() {
    private val TAG = "CashierMainActivity" // For logging purposes
    private lateinit var binding: ActivityCashierMainBinding // Binding object for layout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle // For managing the drawer layout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var showProgressDialog: SweetAlertDialog
    private lateinit var showProgressDialog2: SweetAlertDialog
    private lateinit var transData: TransData
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var dbHandler: DBHandler
    private val cardReadViewModel by viewModels<CardReadViewModel>()
    private lateinit var handler: Handler
    var common = commonMethods(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", TAG)
        showProgressDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        showProgressDialog.setTitleText("Please wait...")
        showProgressDialog.setCancelable(false)

        showProgressDialog2 = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)


        //dialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
        //showProgressDialog.setTitleText(message)
        //dialog.setCancelable(true)
        dbHandler=DBHandler(this)

        // Inflate the layout using DataBindingUtil
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cashier_main)
        binding.viewModel=cardReadViewModel
        sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()
        transData=TransData(this)


        // Setup ActionBarDrawerToggle for navigation drawer
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, binding.cashierDrawerLayout, R.string.open, R.string.close
        )
        binding.cashierDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Set up toolbar navigation icon click listener
        binding.toolbar.navIcon?.setOnClickListener {
            binding.cashierDrawerLayout.openDrawer(GravityCompat.START)
        }

        // Set up ItemClickListener for Navigation View
        navItemClickListener()

        // Set up OnClickListeners for CardViews
        setUpCardViewListeners()

        // Retrieve user credentials from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        val userType = sharedPreferences.getString("userType", "") ?: ""

        val uName = "Hibr Cashier"
        val uType = "Cashier"

        val Int = R.drawable.hb_baseline_account_circle_32
        // Update the navigation header
        updateNavHeader("Username:  "+uName + "", "User Role: "+ uType+"", Int)
    }

    // Function to handle navigation item clicks
    private fun navItemClickListener() {
        binding.cashierNavigationView.setNavigationItemSelectedListener { menuItem ->
            binding.cashierDrawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                    // val intent = Intent(this, SettingsActivity::class.java) // Update to SettingsActivity
                    // startActivity(intent)
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
                    Toast.makeText(this, "Rate", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_logout -> {
                    Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
                    val exitDialog = UserLogoutConfirmDialog(
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

        binding.cashierKeyDownload.setOnClickListener {
            transData.clearVariables()
            Toast.makeText(this, "Key Download", Toast.LENGTH_SHORT).show()
            editor.putString("TXN_TYPE",TxnType.KEY_DOWNLOAD)
            editor.commit()

            TransData.RequestFields.Header="00566020153535"
            RequestFields.MTI="0800"
            RequestFields.primaryBitmap="2020010000C00004"
            RequestFields.Field03="990000"
            val stan=sharedPreferences.getString("STAN", "1")
            val st = (stan?.toInt() ?: 1) + 1
            editor.putString("STAN", st.toString())
            editor.commit()
            RequestFields.Field11 = transData.fillGapSequence(st.toString(),6)
            Log.d("tag","packet "+TransData.RequestFields.Field11)
            RequestFields.Field24="0001"
            /* val intent=Intent(this,LoadingActivity::class.java)
             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
             startActivity(intent)*/
            //common.showloading()


            showProgressDialog.show()
            if (dbHandler.getIPAndPortNumber()?.first==null&& dbHandler.getIPAndPortNumber()?.second==null){
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        showProgressDialog.dismiss()
                        showAlertForConnectionFailure("Please fill the IP and port!")
                    }
                }

            }else {

                handler = Handler()
                handler.postDelayed({
                    Thread(startKeyDownload).start()
                }, 2000)
            }
            //cardReadViewModel.startTransaction()



            // val intent = Intent(this, KeyDownloadActivity::class.java)
            // startActivity(intent)
        }
        binding.cashierKeyDownloadIcon.setOnClickListener {
            transData.clearVariables()
            Toast.makeText(this, "Key Download", Toast.LENGTH_SHORT).show()
            editor.putString("TXN_TYPE",TxnType.KEY_DOWNLOAD)
            editor.commit()

            TransData.RequestFields.Header="00566020153535"
            RequestFields.MTI="0800"
            RequestFields.primaryBitmap="2020010000C00004"
            RequestFields.Field03="990000"
            val stan=sharedPreferences.getString("STAN", "1")
            val st = (stan?.toInt() ?: 1) + 1
            editor.putString("STAN", st.toString())
            editor.commit()
            RequestFields.Field11 = transData.fillGapSequence(st.toString(),6)
            Log.d("tag","packet "+TransData.RequestFields.Field11)
            RequestFields.Field24="0001"
            /* val intent=Intent(this,LoadingActivity::class.java)
             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
             startActivity(intent)*/
            //common.showloading()


            showProgressDialog.show()
            if (dbHandler.getIPAndPortNumber()?.first==null&& dbHandler.getIPAndPortNumber()?.second==null){
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        showProgressDialog.dismiss()
                        showAlertForConnectionFailure("Please fill the IP and port!")
                    }
                }

            }else {

                handler = Handler()
                handler.postDelayed({
                    Thread(startKeyDownload).start()
                }, 2000)
            }

        }
        binding.tvKeyDownload.setOnClickListener {
            transData.clearVariables()
            Toast.makeText(this, "Key Download", Toast.LENGTH_SHORT).show()
            editor.putString("TXN_TYPE",TxnType.KEY_DOWNLOAD)
            editor.commit()

            TransData.RequestFields.Header="00566020153535"
            RequestFields.MTI="0800"
            RequestFields.primaryBitmap="2020010000C00004"
            RequestFields.Field03="990000"
            val stan=sharedPreferences.getString("STAN", "1")
            val st = (stan?.toInt() ?: 1) + 1
            editor.putString("STAN", st.toString())
            editor.commit()
            RequestFields.Field11 = transData.fillGapSequence(st.toString(),6)
            Log.d("tag","packet "+TransData.RequestFields.Field11)
            RequestFields.Field24="0001"
            /* val intent=Intent(this,LoadingActivity::class.java)
             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
             startActivity(intent)*/
            //common.showloading()


            showProgressDialog.show()
            if (dbHandler.getIPAndPortNumber()?.first==null&& dbHandler.getIPAndPortNumber()?.second==null){
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        showProgressDialog.dismiss()
                        showAlertForConnectionFailure("Please fill the IP and port!")
                    }
                }

            }else{

            handler = Handler()
            handler.postDelayed({
                Thread(startKeyDownload).start()
            }, 2000)
            }

        }

        binding.cashierTerminalInfo.setOnClickListener {
            Toast.makeText(this, "Terminal Info ", Toast.LENGTH_SHORT).show()
            // val intent = Intent(this, TerminalInfoActivity::class.java)
            // startActivity(intent)
        }

        binding.cashierSummaryReport.setOnClickListener {
            Toast.makeText(this, "Summary Report ", Toast.LENGTH_SHORT).show()
            val rep = Report(this,this)
            //rep.printdetailedReport()
            rep.printsummaryReport()

        }

        binding.cashierReprint.setOnClickListener {
            Toast.makeText(this, "Reprint ", Toast.LENGTH_SHORT).show()
            // val intent = Intent(this, ReprintActivity::class.java)
            // startActivity(intent)
        }

        binding.cashierSettlement.setOnClickListener {

            val txnDataList: List<Map<String, String>> = dbHandler.getTxnData()

            if (txnDataList.isNotEmpty()) {

                editor.putString("TXN_TYPE", TxnType.SETTLEMENT)
                editor.commit()
                TransData.RequestFields.Header = "30606000000000"
                RequestFields.MTI = "0500"
                RequestFields.primaryBitmap = "2020010000C00016"
                RequestFields.Field03 = "960000"
                val stann = sharedPreferences.getString("STAN", "1")
                val stt = (stann?.toInt() ?: 1) + 1
                editor.putString("STAN", stt.toString())
                editor.commit()
                TransData.RequestFields.Field11 = transData.fillGapSequence(stt.toString(), 6)
                Log.d("tag", "packet11 " + TransData.RequestFields.Field11)
                RequestFields.Field24 = "0001"
                RequestFields.Field60 = "0006"
                RequestFields.Field62 = "00225665722E30312E313330312024243030313230313033"
                val report = Report(this, this)
                report.getReport()
                RequestFields.Field63 = "0030"
                val purchaseCount = transData.fillGapSequence(Report.purcount.toString(), 3)
                val reversalCount = transData.fillGapSequence(Report.revcount.toString(), 3)
                Log.d("tag", "sale count: $purchaseCount")
                Log.d("tag", "void count: $reversalCount")
                Log.d("tag", "totalSaleAmount ${Report.purchase.toLong()}")
                Log.d("tag", "totalReversalAmount ${Report.reversal.toLong()}")
                RequestFields.Header = "30606000000000"
                RequestFields.MTI = "0500"
                RequestFields.primaryBitmap = "2020010000C00016"
                RequestFields.Field03 = "960000"
                val stan = sharedPreferences.getString("STAN", "1")
                val st = (stan?.toInt() ?: 1) + 1
                editor = sharedPreferences.edit()
                editor.putString("STAN", st.toString())
                editor.commit()
                RequestFields.Field11 = transData.fillGapSequence(st.toString(), 6)
                Log.d("tag", "packet11 " + RequestFields.Field11)
                RequestFields.Field24 = "0001"
                RequestFields.Field60 = "0006"
                RequestFields.Field62 = "00225665722E30312E313330312024243030313230313033"



                RequestFields.endValue4F63 = purchaseCount + transData.fillGapSequence(
                    Report.purchase.toLong().toString(),
                    12
                ) + reversalCount + transData.fillGapSequence(
                    Report.reversal.toLong().toString(), 12
                )
                Log.d("tag", "endvalue of 63: ${TransData.RequestFields.endValue4F63}")

                showProgressDialog.show()
                if (dbHandler.getIPAndPortNumber()?.first==null&& dbHandler.getIPAndPortNumber()?.second==null){
                    runOnUiThread {
                        if (!isFinishing && !isDestroyed) {
                            showProgressDialog.dismiss()
                            showAlertForConnectionFailure("Please fill the IP and port!")
                        }
                    }

                }else {
                    handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        Thread(doSettlement).start()
                    }, 2000)

                    Toast.makeText(this, "Settlement", Toast.LENGTH_SHORT).show()
                }

                }else{
                runOnUiThread {
                    showAlert("No Transaction")
                }
                }
        }
        binding.cashierSettlementIcon.setOnClickListener {

            val txnDataList: List<Map<String, String>> = dbHandler.getTxnData()

            if (txnDataList.isNotEmpty()) {

                editor.putString("TXN_TYPE", TxnType.SETTLEMENT)
                editor.commit()
                TransData.RequestFields.Header = "30606000000000"
                RequestFields.MTI = "0500"
                RequestFields.primaryBitmap = "2020010000C00016"
                RequestFields.Field03 = "960000"
                val stann = sharedPreferences.getString("STAN", "1")
                val stt = (stann?.toInt() ?: 1) + 1
                editor.putString("STAN", stt.toString())
                editor.commit()
                TransData.RequestFields.Field11 = transData.fillGapSequence(stt.toString(), 6)
                Log.d("tag", "packet11 " + TransData.RequestFields.Field11)
                RequestFields.Field24 = "0001"
                RequestFields.Field60 = "0006"
                RequestFields.Field62 = "00225665722E30312E313330312024243030313230313033"
                val report = Report(this, this)
                report.getReport()
                RequestFields.Field63 = "0030"
                val purchaseCount = transData.fillGapSequence(Report.purcount.toString(), 3)
                val reversalCount = transData.fillGapSequence(Report.revcount.toString(), 3)
                Log.d("tag", "sale count: $purchaseCount")
                Log.d("tag", "void count: $reversalCount")
                Log.d("tag", "totalSaleAmount ${Report.purchase.toLong()}")
                Log.d("tag", "totalReversalAmount ${Report.reversal.toLong()}")
                RequestFields.Header = "30606000000000"
                RequestFields.MTI = "0500"
                RequestFields.primaryBitmap = "2020010000C00016"
                RequestFields.Field03 = "960000"
                val stan = sharedPreferences.getString("STAN", "1")
                val st = (stan?.toInt() ?: 1) + 1
                editor = sharedPreferences.edit()
                editor.putString("STAN", st.toString())
                editor.commit()
                RequestFields.Field11 = transData.fillGapSequence(st.toString(), 6)
                Log.d("tag", "packet11 " + RequestFields.Field11)
                RequestFields.Field24 = "0001"
                RequestFields.Field60 = "0006"
                RequestFields.Field62 = "00225665722E30312E313330312024243030313230313033"



                RequestFields.endValue4F63 = purchaseCount + transData.fillGapSequence(
                    Report.purchase.toLong().toString(),
                    12
                ) + reversalCount + transData.fillGapSequence(
                    Report.reversal.toLong().toString(), 12
                )
                Log.d("tag", "endValue of 63: ${TransData.RequestFields.endValue4F63}")

                showProgressDialog.show()
                if (dbHandler.getIPAndPortNumber()?.first==null&& dbHandler.getIPAndPortNumber()?.second==null){
                    runOnUiThread {
                        if (!isFinishing && !isDestroyed) {
                            showProgressDialog.dismiss()
                            showAlertForConnectionFailure("Please fill the IP and port!")
                        }
                    }

                }else {
                    handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        Thread(doSettlement).start()
                    }, 2000)

                    Toast.makeText(this, "Settlement", Toast.LENGTH_SHORT).show()
                }

            }else{
                runOnUiThread {
                    showAlert("No Transaction")
                }
            }

        }
        binding.tvSettlement.setOnClickListener {

            val txnDataList: List<Map<String, String>> = dbHandler.getTxnData()

            if (txnDataList.isNotEmpty()) {

                editor.putString("TXN_TYPE", TxnType.SETTLEMENT)
                editor.commit()
                TransData.RequestFields.Header = "30606000000000"
                RequestFields.MTI = "0500"
                RequestFields.primaryBitmap = "2020010000C00016"
                RequestFields.Field03 = "960000"
                val stann = sharedPreferences.getString("STAN", "1")
                val stt = (stann?.toInt() ?: 1) + 1
                editor.putString("STAN", stt.toString())
                editor.commit()
                TransData.RequestFields.Field11 = transData.fillGapSequence(stt.toString(), 6)
                Log.d("tag", "packet11 " + TransData.RequestFields.Field11)
                RequestFields.Field24 = "0001"
                RequestFields.Field60 = "0006"
                RequestFields.Field62 = "00225665722E30312E313330312024243030313230313033"
                val report = Report(this, this)
                report.getReport()
                RequestFields.Field63 = "0030"
                val purchaseCount = transData.fillGapSequence(Report.purcount.toString(), 3)
                val reversalCount = transData.fillGapSequence(Report.revcount.toString(), 3)
                Log.d("tag", "sale count: $purchaseCount")
                Log.d("tag", "void count: $reversalCount")
                Log.d("tag", "totalSaleAmount ${Report.purchase.toLong()}")
                Log.d("tag", "totalReversalAmount ${Report.reversal.toLong()}")
                RequestFields.Header = "30606000000000"
                RequestFields.MTI = "0500"
                RequestFields.primaryBitmap = "2020010000C00016"
                RequestFields.Field03 = "960000"
                val stan = sharedPreferences.getString("STAN", "1")
                val st = (stan?.toInt() ?: 1) + 1
                editor = sharedPreferences.edit()
                editor.putString("STAN", st.toString())
                editor.commit()
                RequestFields.Field11 = transData.fillGapSequence(st.toString(), 6)
                Log.d("tag", "packet11 " + RequestFields.Field11)
                RequestFields.Field24 = "0001"
                RequestFields.Field60 = "0006"
                RequestFields.Field62 = "00225665722E30312E313330312024243030313230313033"



                RequestFields.endValue4F63 = purchaseCount + transData.fillGapSequence(
                    Report.purchase.toLong().toString(),
                    12
                ) + reversalCount + transData.fillGapSequence(
                    Report.reversal.toLong().toString(), 12
                )
                Log.d("tag", "endValue of 63: ${RequestFields.endValue4F63}")

                showProgressDialog.show()
                if (dbHandler.getIPAndPortNumber()?.first==null&& dbHandler.getIPAndPortNumber()?.second==null){
                    runOnUiThread {
                        if (!isFinishing && !isDestroyed) {
                            showProgressDialog.dismiss()
                            showAlertForConnectionFailure("Please fill the IP and port!")
                        }
                    }

                }else {

                    handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        Thread(doSettlement).start()
                    }, 2000)

                    Toast.makeText(this, "Settlement", Toast.LENGTH_SHORT).show()
                }

            }else{
                runOnUiThread {
                    showAlert("No Transaction")
                }
            }

        }
            binding.cashierChangePassword.setOnClickListener {
                Toast.makeText(this, "Change Password ", Toast.LENGTH_SHORT).show()

            }

    }
    // Method to update the Navigation Header Texts
    private fun updateNavHeader(username: String, userrole: String, userImageResId: Int) {
        // Get the navigation view binding for the header
        val headerBinding = NavHeaderBinding.bind(binding.cashierNavigationView.getHeaderView(0))

        // Update the username and userrole
        headerBinding.username.text = username
        headerBinding.userrole.text = userrole

        //   headerBinding.navImage.drawable = userimg
        headerBinding.navImage.setImageResource(userImageResId)
    }
    private val startKeyDownload=Runnable{

        transData.assignValue2Fields()
        val packet=transData.packRequestFields()
        Log.d("tag","packet ")

        val com= Comm("${dbHandler.getIPAndPortNumber()?.first}","${dbHandler.getIPAndPortNumber()?.second}".toInt())

        if(!com.connect()){
            runOnUiThread {
                showProgressDialog.dismiss()
                showAlertForConnectionFailure("Connection failed")
            }

            Log.d("tag","Connection failed")


            //common.dismissdialog()
        }else{

            com.send(packet)
            Log.d("tag", "message sent...:")

            val response=com.receive(1024,30)
            Log.d("tag", "Received response:$response")
            Log.d("tag", "key download response:"+ response?.let { HexUtil.toHexString(it) })


            if(response==null){

                Log.d("tag","Response Null")
                //ActivityCollector.removeActivity()
                showProgressDialog.dismiss()
                //common.dismissdialog()

            }else{
                //ActivityCollector.removeActivity(loadingAct)
                showProgressDialog.dismiss()


                val responseHex=HexUtil.toHexString(response)
                val field62Hex=responseHex.substring(responseHex.length-32)

                val field62="AB3AF79EB105C5EE"//HexUtil.hexToAscii(field62Hex)
                Log.d("tag", "fld62:$field62")
                Log.d("tag","POIHsmManage.PED_TMK:"+POIHsmManage.PED_TMK)

                val result= common.updateKeyMKSK(
                    POIHsmManage.PED_TMK,
                    MASTER_KEY_INDEX,
                    field62,
                    POIHsmManage.PED_TPK,
                    SESSION_PIN_KEY_INDEX
                )
                Log.d("tag", "key download result...:$result")
                if (result != 0) {
                    runOnUiThread {
                        showAlert("Key Download Failed")
                    }

                    Log.d(TAG, "Key Download Failure")

                    // Toast.makeText(this,"Key Download Failure",Toast.LENGTH_SHORT).show()

                } else {
                    runOnUiThread {
                        showAlert("Key Download Success")
                    }
                    Log.d(TAG, "Key Download Success")

                    //Toast.makeText(this, "Key Download Success", Toast.LENGTH_SHORT).show()
                }


                //response?.let { HexUtil.toHexString(it) }
                //?.let { transData.unpackResponseFields(it) }
                //emvCoreManager.onSetOnlineResponse(processOnlineResult(TransData.ResponseFields.Field39))
                //common.dismissdialog()

            }








            //Log.d(tag,"Received response:"+ response?.let { HexUtil.toHexString(it) })
            //Log.d(tag,"Received response:"+response)



        }
    }
    private val doSettlement=Runnable{

        dbHandler=DBHandler(this)

        val report= Report(this,this)
        Log.d("tag","transaction type:${sharedPreferences.getString("TXN_TYPE","")}")
        transData.assignValue2Fields()
        val packet=transData.packRequestFields()
        Log.d("tag","packet ")

        val com= Comm("${dbHandler.getIPAndPortNumber()?.first}","${dbHandler.getIPAndPortNumber()?.second}".toInt())
        if (dbHandler.getIPAndPortNumber()?.first==null&& dbHandler.getIPAndPortNumber()?.second==null){
            runOnUiThread {
                if (!isFinishing && !isDestroyed) {
                    showProgressDialog.dismiss()
                    showAlertForConnectionFailure("Please fill the IP and port!")
                }
            }

        }
        if(!com.connect()){
            runOnUiThread {
                showProgressDialog.dismiss()
                showAlertForConnectionFailure("Connection failed")
            }
            Log.d("tag","Connection failed")


        }else{

            com.send(packet)
            Log.d("tag", "message sent...:")

            var response=com.receive(1024,30)
            Log.d("tag", "Received response:$response")
            Log.d("tag", "settlement response:"+ response?.let { HexUtil.toHexString(it) })


            if(response==null){
                showProgressDialog.dismiss()
                runOnUiThread {
                    showAlert("Settlement Failed")
                }
                Log.d("tag","Response Null")

            }else{

                showProgressDialog.dismiss()
                response?.let { HexUtil.toHexString(it) }
                    ?.let { transData.unpackResponseFields(it) }
                if(TransData.ResponseFields.Field39.equals("00")){
                    runOnUiThread {
                        showAlert("Settlement Success")
                    }
                    report.printsummaryReport()
                    dbHandler.deleteAllTxnData()

                }
            }
        }
    }
    private fun showAlert(message:String){

        showProgressDialog2.setTitleText(message)
        showProgressDialog2.setCancelable(false)
        showProgressDialog2.show()
        showProgressDialog2.setConfirmClickListener(
            listener = SweetAlertDialog.OnSweetClickListener {
                //startActivity(Intent(this, MainActivity::class.java))
                showProgressDialog2.dismiss() // Close
            }
        )

    }
    private fun showAlertForConnectionFailure(message:String){

        showProgressDialog2.setTitleText(message)
        showProgressDialog2.setCancelable(false)
        showProgressDialog2.show()
        showProgressDialog2.setConfirmClickListener(
            listener = SweetAlertDialog.OnSweetClickListener {
                startActivity(Intent(this@CashierMainActivity,MainMenuActivity::class.java))
                finish()

            }
        )

    }
}
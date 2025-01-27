package net.geidea.payment.users.supervisor
import net.geidea.utils.BuzzerUtils
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.text.Editable
import android.text.TextWatcher
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.pos.sdk.printer.POIPrinterManager
import com.pos.sdk.printer.models.BitmapPrintLine
import com.pos.sdk.printer.models.PrintLine
import com.pos.sdk.printer.models.TextPrintLine
import net.geidea.payment.AmountActivity
import net.geidea.payment.BuildConfig
import net.geidea.payment.DBHandler
import net.geidea.payment.R
import net.geidea.payment.Txntype
import net.geidea.payment.com.Comm
import net.geidea.payment.databinding.ActivityExpiryDateBinding
import net.geidea.payment.print.PrintStatus
import net.geidea.payment.print.Printer
import net.geidea.payment.print.Printer.printList
import net.geidea.payment.tlv.HexUtil
import net.geidea.payment.transaction.model.EntryMode
import net.geidea.payment.transaction.model.TransData
import net.geidea.payment.transaction.view.CardReadActivity
import net.geidea.payment.transaction.viewmodel.CardReadViewModel
import net.geidea.payment.utils.FirebaseDatabaseSingleton
import net.geidea.payment.utils.TransactionProcess
import net.geidea.utils.CurrencyConverter
import net.geidea.utils.DateTimeFormat
import net.geidea.utils.convertDateTime
import net.geidea.utils.dialog.SweetAlertDialog
import net.geidea.utils.extension.gone
import net.geidea.utils.extension.visible
import net.geidea.utils.formatExpiryDate
import net.geidea.utils.getCurrentDateTime
import net.geidea.utils.maskPan
class ExpiryDate : AppCompatActivity() {
    private lateinit var handler:Handler
    private lateinit var binding:ActivityExpiryDateBinding
    private lateinit var cardReadViewModel:CardReadViewModel
    private lateinit var dbHandler:DBHandler
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    private lateinit var transData:TransData
    private lateinit var cardReadActivity: CardReadActivity
    private lateinit var showProgressDialog:SweetAlertDialog
    private lateinit var showProgressDialog2:SweetAlertDialog
    private val printerManager: POIPrinterManager by lazy { POIPrinterManager(this) }
    var printStatus: MutableLiveData<PrintStatus> = MutableLiveData()
    var transactionStatus: MutableLiveData<TransactionProcess> = MutableLiveData()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityExpiryDateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()
        transData=TransData(this)
        cardReadViewModel=CardReadViewModel(this)
        cardReadActivity=CardReadActivity()

        showProgressDialog2 = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)

        showProgressDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        if(sharedPreferences.getInt("TimeoutFlagM",0)==1){
            showProgressDialog.setTitleText("Processing timeout...")

        }else{

            showProgressDialog.setTitleText("Please wait...")
        }
        showProgressDialog.setCancelable(false)
        binding.layoutTransactionStatus.printReceipt.setOnClickListener {
            FirebaseDatabaseSingleton.setLog("onClick - printReceipt")
            binding.layoutTransactionStatus.startNewTransaction.isClickable = false
            binding.layoutTransactionStatus.printReceipt.isClickable = false
            printReceipt()
        }
        binding.layoutTransactionStatus.startNewTransaction.setOnClickListener {

            startActivity(Intent(this,AmountActivity::class.java))
        }


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@ExpiryDate, SupervisorMainActivity::class.java))
                finish()
            }
        })
        //403246
        binding.edtExDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (it.isNotEmpty()) {
                        val firstChar = it[0]
                        if (firstChar > '1') {
                            binding.edtExDate.setText("")
                            binding.edtExDate.setSelection(0)
                        }
                    }
                    if (it.length >= 2) {
                        val firstTwoDigits = it.substring(0, 2).toIntOrNull()
                        if (firstTwoDigits != null && firstTwoDigits > 12) {
                            binding.edtExDate.setText(it.substring(0, 1))
                            binding.edtExDate.setSelection(binding.edtExDate.text.length)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })




        binding.btnOk.setOnClickListener {
            val transData=TransData(this)
            transData.ClearVariables()
            val dbHandler=DBHandler(this)
            TransData.RequestFields.Header="30606020153535"
            TransData.RequestFields.MTI="0200"
            TransData.RequestFields.primaryBitmap="7024058000C00004"
            TransData.RequestFields.Field02="${intent.getStringExtra("pan")}"
            Log.d("TAG","pan:${TransData.RequestFields.Field02}")
            TransData.RequestFields.Field03="000000"
            TransData.RequestFields.Field04="${intent.getStringExtra("amount")}"
            Log.d("TAG","fld04:${TransData.RequestFields.Field04}")

            val stann = sharedPreferences.getString("STAN", "1")
            val stt = (stann?.toInt() ?: 1) + 1
            editor.putString("STAN", stt.toString())
            editor.commit()
            TransData.RequestFields.Field11 = transData.fillGapSequence(stt.toString(), 6)
            val expiryDate=binding.edtExDate.text.toString()
            val month=expiryDate.substring(0,2)
            Log.d("TAG","month:${month}")
            val year=expiryDate.substring(2,4)
            Log.d("TAG","year:${year}")

            if(isCardExpired(month.toInt(),year.toInt())){
                binding.edtExDate.setError("The card has expired !")
            }else{
                TransData.RequestFields.Field14=binding.edtExDate.text.toString()
                TransData.RequestFields.Field22="0010"
                Log.d("TAG","fld22:${TransData.RequestFields.Field22}")

                TransData.RequestFields.Field24="0001"
                Log.d("TAG","fld24:${TransData.RequestFields.Field24}")

                TransData.RequestFields.Field25="00"
                Log.d("TAG","pfld25:${TransData.RequestFields.Field25}")

                TransData.RequestFields.Field41="${dbHandler.getTID()}"
                TransData.RequestFields.Field42="${dbHandler.getMID()}"
                TransData.RequestFields.Field62="0006"

                showProgressDialog.show()
                handler = Handler()
                handler.postDelayed({
                    Thread(doManualPurchase).start()
                }, 2000)

            }



        }
    }


    private val doManualPurchase = Runnable {
        dbHandler = DBHandler(this)
        //transData.assignValue2Fields()

        val packet = transData.packRequestFields()

        val timeoutData=transData.packFields4TimeoutReversal()
        val timeoutDataHex=timeoutData.let { HexUtil.toHexString(it) }
        Log.d("tag", "packet ")
        val com = Comm("${dbHandler.getIPAndPortNumber()?.first}", "${dbHandler.getIPAndPortNumber()?.second}".toInt())
        if (!com.connect()) {
            Log.d("tag", "Connection failed")
        } else {
            val timeout = sharedPreferences.getInt("TimeoutFlagM", 0)
            if (timeout == 1) {
                val savedTimeoutData=sharedPreferences.getString("TimeoutDataM","")
                val savedTimeoutByte=savedTimeoutData?.let { HexUtil.hexStr2Byte(it) }
                if (savedTimeoutByte != null) {
                    com.send(savedTimeoutByte)
                    Log.d("tag", "timeout data sentM ...:${savedTimeoutByte}")
                }
                val timeoutResponseM=com.receive(1024,30)
                Log.d("tag", "timeout responseM ...:${timeoutResponseM?.let { HexUtil.toHexString(it) }}")
                editor.putInt("TimeoutFlagM", 0)
                editor.putString("TimeoutDataM","")
                editor.commit()
                if(timeoutResponseM!=null){
                    TransData.RequestFields.primaryBitmap="7024058000C00004"
                    TransData.RequestFields.MTI="0200"

                    com.send(packet)
                    Log.d("tag", "message sent...")
                    val response = com.receive(1024, 30)
                    Log.d("tag", "Received response: $response")
                    Log.d("tag", "manual txn response: ${response?.let { HexUtil.toHexString(it) }}")
                    if (response == null) {
                        editor.putInt("TimeoutFlagM", 1)
                        editor.putString("TimeoutDataM", timeoutDataHex)
                        editor.commit()
                        runOnUiThread {
                            if (!isFinishing && !isDestroyed) {
                                showProgressDialog.dismiss()
                                showAlert("txn failed !")
                            }
                        }
                    } else {
                        editor.putInt("TimeoutFlagM", 0)
                        editor.commit()
                        response?.let { HexUtil.toHexString(it) }
                            ?.let { transData.unpackResponseFields(it) }
                        runOnUiThread {
                            if (!isFinishing && !isDestroyed) {
                                showProgressDialog.dismiss()
                            }
                            if (TransData.ResponseFields.Field39 == "00") {
                                val txntype = Txntype.manualPurchase
                                txntype?.let {
                                    dbHandler.registerTxnData(
                                        it,
                                        "",
                                        "",
                                        TransData.RequestFields.Field02,
                                        "",
                                        TransData.ResponseFields.Field04,
                                        TransData.ResponseFields.Field11,
                                        TransData.ResponseFields.Field12,
                                        TransData.ResponseFields.Field13,
                                        TransData.RequestFields.Field14,
                                        "",
                                        "",
                                        "",
                                        "",
                                        TransData.ResponseFields.Field37,
                                        TransData.ResponseFields.Field38,
                                        TransData.ResponseFields.Field39,
                                        "",
                                        "",
                                        "",
                                        "",
                                        TransData.RequestFields.Field60
                                    )
                                }
                                runOnUiThread() {
                                    txnStatusView4Approval()

                                }

                            } else {
                                transData.transactionStatus = true

                                runOnUiThread() {
                                    txnStatusView4Decline()
                                    //cardReadActivity.listenerForManualTxn()

                                }


                            }
                        }
                    }

                }



            } else {

                TransData.RequestFields.primaryBitmap="7024058000C00004"
                TransData.RequestFields.MTI="0200"
            com.send(packet)
            Log.d("tag", "message sent...")
            val response = com.receive(1024, 30)
            Log.d("tag", "Received response: $response")
            Log.d("tag", "manual txn response: ${response?.let { HexUtil.toHexString(it) }}")
            if (response == null) {
                editor.putInt("TimeoutFlagM", 1)
                editor.putString("TimeoutDataM", timeoutDataHex)
                editor.commit()
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        showProgressDialog.dismiss()
                        showAlert("txn failed !")
                    }
                }
            } else {
                editor.putInt("TimeoutFlagM", 0)
                editor.commit()
                response?.let { HexUtil.toHexString(it) }
                    ?.let { transData.unpackResponseFields(it) }
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        showProgressDialog.dismiss()
                    }
                    if (TransData.ResponseFields.Field39 == "00") {
                        transData.transactionStatus = true
                        val txntype = Txntype.manualPurchase
                        txntype?.let {
                            dbHandler.registerTxnData(
                                it,
                                "",
                                "",
                                TransData.RequestFields.Field02,
                                "",
                                TransData.ResponseFields.Field04,
                                TransData.ResponseFields.Field11,
                                TransData.ResponseFields.Field12,
                                TransData.ResponseFields.Field13,
                                TransData.RequestFields.Field14,
                                "",
                                "",
                                "",
                                "",
                                TransData.ResponseFields.Field37,
                                TransData.ResponseFields.Field38,
                                TransData.ResponseFields.Field39,
                                "",
                                "",
                                "",
                                "",
                                TransData.RequestFields.Field60
                            )
                            }
                        txntype?.let{
                            dbHandler.registerTxnData2(
                                it,
                                "",
                                "",
                                TransData.RequestFields.Field02,
                                "",
                                TransData.ResponseFields.Field04,
                                TransData.ResponseFields.Field11,
                                TransData.ResponseFields.Field12,
                                TransData.ResponseFields.Field13,
                                TransData.RequestFields.Field14,
                                "",
                                "",
                                "",
                                "",
                                TransData.ResponseFields.Field37,
                                TransData.ResponseFields.Field38,
                                TransData.ResponseFields.Field39,
                                "",
                                "",
                                "",
                                "",
                                TransData.RequestFields.Field60
                            )
                        }
                        runOnUiThread() {
                            txnStatusView4Approval()

                        }

                    } else {
                        runOnUiThread() {
                            txnStatusView4Decline()
                            //cardReadActivity.listenerForManualTxn()

                        }


                    }
                }
            }
        }
        }
    }
    private fun txnStatusView4Approval(){

        binding.cardView.gone()
        binding.layoutTransactionStatus.root.visible()
        BuzzerUtils.playForTransactionApproved()
        binding.layoutTransactionStatus.transactionStatus.text = getString(R.string.approved)
        binding.layoutTransactionStatus.tvApprovalCode.text="Approval Code :${TransData.ResponseFields.Field38}"
        binding.layoutTransactionStatus.transactionStatusImage.setImageResource(R.drawable.approved)
    }
    private fun txnStatusView4Decline(){
        binding.cardView.gone()
        binding.layoutTransactionStatus.root.visible()
        BuzzerUtils.playForTransactionDeclined()
        binding.layoutTransactionStatus.transactionStatus.text = getString(R.string.declined)
        binding.layoutTransactionStatus.tvApprovalCode.text="Response Code :${TransData.ResponseFields.Field38}"
        binding.layoutTransactionStatus.transactionStatusImage.setImageResource(R.drawable.ic_tranx_declined)


    }
    private fun isCardExpired(expiryMonth: Int, expiryYear: Int): Boolean {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MM/yy")
        val formattedCurrentDate = currentDate.format(formatter)
        val currentMonth = formattedCurrentDate.substring(0, 2).toInt()
        val currentYear = formattedCurrentDate.substring(3, 5).toInt()

        return if (currentYear > expiryYear % 100) {
            true
        } else if (currentYear == expiryYear % 100) {
            currentMonth > expiryMonth
        } else {
            false
        }
    }
    private fun showAlert(message:String){

        showProgressDialog2.setTitleText(message)
        showProgressDialog2.setCancelable(false)
        showProgressDialog2.show()
        showProgressDialog2.setConfirmClickListener(
            listener = {
                startActivity(Intent(this, SupervisorMainActivity::class.java))
                //showProgressDialog2.dismiss() // Close
            }
        )

    }
    private fun printReceipt() {
        FirebaseDatabaseSingleton.setLog("printReceipt")
        printerManager.open()
        printerManager.cleanCache()
        val state = printerManager.printerState
        if (state == POIPrinterManager.STATUS_IDLE) {
            setReceiptHeader()
            setAmountBlock()

                setCvmResult()

            setFooter()
            val listener: POIPrinterManager.IPrinterListener =
                object : POIPrinterManager.IPrinterListener {
                    override fun onStart() {
                        //Print started
                        printStatus.postValue(PrintStatus.PrintStarted)
                    }

                    override fun onFinish() {
                        printerManager.cleanCache();
                        printerManager.close()
                        printStatus.postValue(PrintStatus.PrintCompleted)
                    }

                    override fun onError(code: Int, msg: String) {
                        Log.e("POIPrinterManager", "code: " + code + "msg: " + msg)
                        printerManager.close()
                        printStatus.postValue(PrintStatus.PrintError(msg))
                    }
                }
            if (state == 4) {
                printerManager.close()
                return
            }
            printerManager.beginPrint(listener)
        }

    }

    private fun setReceiptHeader(
    ) {
        FirebaseDatabaseSingleton.setLog("setReceiptHeader")
        val bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.hb_logo2)
        //val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true)
        printerManager.addPrintLine(BitmapPrintLine(bitmap, PrintLine.CENTER))
        printerManager.addPrintLine(
            TextPrintLine(
                "Hibret Bank", PrintLine.CENTER, 20, false
            )
        )
        printerManager.addPrintLine(
            TextPrintLine(
                dbHandler.getMerchantAddress(),//CANARY CENTER, 7304 PRINCE ABDULAZIZ IBN MUSAID AS SULIMANIYAH DISTRICT 12243 OFF#112",
                PrintLine.CENTER,
                16,
                false
            )
        )

        printerManager.addPrintLine(
            Printer.printList(
                " TID :${dbHandler.getTID()}", "", "MID : ${dbHandler.getMID()}", 16, false
            )
        )

        printerManager.addPrintLine(
            printList(
                "DATE : ${
                    convertDateTime(
                        getCurrentDateTime(DateTimeFormat.DATE_TIME_PATTERN_TRANS_ONE),
                        DateTimeFormat.DATE_TIME_PATTERN_TRANS_ONE,
                        DateTimeFormat.DATE_PATTERN_DISPLAY_ONE
                    )
                }", "", "TIME : ${
                    convertDateTime(
                        getCurrentDateTime(DateTimeFormat.DATE_TIME_PATTERN_TRANS_ONE),
                        DateTimeFormat.DATE_TIME_PATTERN_TRANS_ONE,
                        DateTimeFormat.TIME_PATTERN_DISPLAY
                    )
                }", 16, false
            )
        )
    }

    private fun setAmountBlock() {
        FirebaseDatabaseSingleton.setLog("setAmountBlock")
        printerManager.addBlankView(1)

        printerManager.addPrintLine(
            TextPrintLine(
                "PURCHASE (Manual Card Entry)", PrintLine.LEFT, 20, true
            )
        )


        printerManager.addPrintLine(
            TextPrintLine(
                maskPan(TransData.RequestFields.Field02), PrintLine.LEFT, 20, true
            )
        )


        printerManager.addPrintLine(
            printList(
                "RRN : ${TransData.ResponseFields.Field37}", "", "RECEIPT No : ${TransData.RequestFields.Field11}", 16, false
            )
        )

        printerManager.addBlankView(1)
        printerManager.addPrintLine(
            printList(
                "AMOUNT", sharedPreferences.getString("Currency","").toString(), CurrencyConverter.convertWithoutSAR(TransData.RequestFields.Field04.toLong()), 20, true

            )
        )
        printerManager.addBlankView(1)

    }

    private fun setCvmResult() {
        FirebaseDatabaseSingleton.setLog("setCvmResult")
        if (transData.transactionStatus) {
            printerManager.addPrintLine(
                TextPrintLine(
                    "PLEASE DEBIT MY ACCOUNT", PrintLine.CENTER, 16, false
                )
            )
        }


        printerManager.addPrintLine(
            TextPrintLine(
                getVerificationMethod(transData.verificationMethod), PrintLine.CENTER, 16, false
            )
        )

        if (transData.transactionStatus) {
            printerManager.addBlankView(1)
            printerManager.addPrintLine(
                printList(
                    "APPROVAL CODE :", "", TransData.ResponseFields.Field38, 20, true
                )
            )
        } else {
            printerManager.addPrintLine(
                TextPrintLine(
                    "DECLINED", PrintLine.CENTER, 20, true
                )
            )

            printerManager.addPrintLine(
                printList(
                    "Resp Code :", TransData.ResponseFields.Field39, "", 20, true
                )
            )
            val response = TransData.ResponseFields.Field39
            when (response) {
                "01" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Refer to card issuer", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "02" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Refer to card issuer,special condition", PrintLine.CENTER, 10, false
                        )
                    )
                }

                "03" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Invalid merchant or service provider", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "04" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Pickup card", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "05" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Do not honor", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "06" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Error", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "07" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Pickup card,special condition(other than lost/stolen card)",
                            PrintLine.CENTER,
                            20,
                            false
                        )
                    )
                }

                "10" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Partial approval", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "11" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "V.I.P approval", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "12" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Invalid transaction", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "13" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Invalid amount(currency conversion field overflow);or amount exceeds maximum for card program",
                            PrintLine.CENTER,
                            20,
                            false
                        )
                    )
                }

                "14" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Invalid account number", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "15" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "No such issuer", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "19" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Renter-transaction", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "21" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "No action taken(unable back out prior transaction",
                            PrintLine.CENTER,
                            20,
                            false
                        )
                    )
                }

                "25" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Unable to locate record in file,or account number is missing from the inquiry",
                            PrintLine.CENTER,
                            20,
                            false
                        )
                    )
                }

                "28" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "File is temporarily unavailable", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "30" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Format error", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "41" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Pickup card(lost card)", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "43" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Pickup card(lost card)", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "51" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Insufficient funds", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "52" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "No checking account", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "53" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "No savings account", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "54" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Expired card", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "55" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Incorrect PIN", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "57" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Transaction not permitted to cardholder", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "58" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Transaction not permitted at terminal", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "59" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Suspected fraud", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "61" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Activity amount limit exceeded", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "62" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Restricted card", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "63" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Security violation", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "65" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Activity count limit exceeded", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "75" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "PIN-entry tries exceeded", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "76" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Unable to locate previous message", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "77" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Inconsistent with original message", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "78" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Blocked,first used", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "80" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Visa transactions:credit issuer unavailable.Private label and check acceptance:invalid date",
                            PrintLine.CENTER,
                            20,
                            false
                        )
                    )
                }

                "81" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "PIN cryptographic error", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "82" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Negative CAM,dCVV,iCVV,or CVV results", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "83" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Unable to verify PIN", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "85" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "No reason", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "91" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Issuer unavailable", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "92" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Destination cannot be found for routing", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "93" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Transaction cannot be completed;violation of law",
                            PrintLine.CENTER,
                            20,
                            false
                        )
                    )
                }

                "96" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "System malfunction", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "B1" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Surcharge amount not permitted on visa cards",
                            PrintLine.CENTER,
                            20,
                            false
                        )
                    )
                }

                "N0" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Force STIP", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "N3" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Cash service not available", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "N4" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Cashback request exceeds issuer limit", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "N7" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Decline for CVV2 failure", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "P2" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Invalid biller information", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "P5" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "PIN Change/Unblock request declined", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "P6" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Unsafe PIN", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "Q1" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Card Authentication failed", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "R0" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Stop payment order", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "R1" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Revocation of Authorization order", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "R3" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Revocation of All Authorization order", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "XA" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Forward to issuer", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "XD" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Forward to issuer", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "Z3" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Unable to go online", PrintLine.CENTER, 20, false
                        )
                    )
                }

                "998" -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Incorrect ARPC", PrintLine.CENTER, 20, false
                        )
                    )
                }

                else -> {
                    printerManager.addPrintLine(
                        TextPrintLine(
                            "Unable to read error code", PrintLine.CENTER, 20, false
                        )
                    )
                }

            }
        }
    }

    private fun setFooter() {
        FirebaseDatabaseSingleton.setLog("setFooter")
        if (transData.transactionStatus) {
            printerManager.addBlankView(1)

        }

        printerManager.addBlankView(1)
        printerManager.addPrintLine(
            TextPrintLine(
                "THANK YOU", PrintLine.CENTER, 20, true
            )
        )

        printerManager.addPrintLine(
            TextPrintLine(
                "<<Merchant Copy>>", PrintLine.CENTER, 20, true
            )
        )
        printerManager.addPrintLine(
            TextPrintLine(
                "APP VERSION :${BuildConfig.VERSION_NAME}", PrintLine.CENTER, 16, true
            )
        )
        printerManager.addBlankView(2)
        printerManager.addPrintLine(
            TextPrintLine(
                "Powered By SSC", PrintLine.CENTER, 16,true
            )
        )

        printerManager.addBlankView(5)

    }
    private fun getVerificationMethod(type: Int): String {
        FirebaseDatabaseSingleton.setLog("getVerificationMethod - $type")
        return when (type) {
            1, 4, 5 -> {
                this.getString(R.string.cardholder_pin_verified_eng)
            }

            2 -> {
                if (transData.entryMode == EntryMode.CONTACTLESS.toString()) {
                    this.getString(R.string.no_verification_eng)
                } else {
                    this.getString(R.string.card_holder_verified_signature_eng)
                }
            }

            3 -> {
                this.getString(R.string.cardholder_pin_plus_verified_eng)
            }

            6 -> {
                this.getString(R.string.device_owner_verification_msg_eng)
            }

            else -> {
                this.getString(R.string.no_verification_eng)
            }

        }
    }



}


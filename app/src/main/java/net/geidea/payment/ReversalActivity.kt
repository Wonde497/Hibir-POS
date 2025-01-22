package net.geidea.payment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.pos.sdk.printer.POIPrinterManager
import com.pos.sdk.printer.models.BitmapPrintLine
import com.pos.sdk.printer.models.PrintLine
import com.pos.sdk.printer.models.TextPrintLine
import net.geidea.payment.com.Comm
import net.geidea.payment.databinding.ActivityReversalBinding
import net.geidea.payment.print.PrintStatus
import net.geidea.payment.print.Printer
import net.geidea.payment.print.Printer.printList
import net.geidea.payment.tlv.HexUtil
import net.geidea.payment.transaction.model.EntryMode
import net.geidea.payment.transaction.model.TransData
import net.geidea.payment.users.supervisor.SupervisorLogin
import net.geidea.payment.users.supervisor.SupervisorMainActivity
import net.geidea.payment.utils.FirebaseDatabaseSingleton
import net.geidea.payment.utils.commonMethods
import net.geidea.utils.BuzzerUtils
import net.geidea.utils.CurrencyConverter
import net.geidea.utils.DateTimeFormat
import net.geidea.utils.convertDateTime
import net.geidea.utils.dialog.SweetAlertDialog
import net.geidea.utils.extension.gone
import net.geidea.utils.extension.visible
import net.geidea.utils.formatExpiryDate
import net.geidea.utils.getCurrentDateTime
import net.geidea.utils.maskPan

class ReversalActivity : AppCompatActivity() {
    private lateinit var binding:ActivityReversalBinding
    private lateinit var dbHandler: DBHandler
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var showProgressDialog:SweetAlertDialog
    private lateinit var showProgressDialog2:SweetAlertDialog

    private lateinit var transData:TransData
    private lateinit var handler:Handler
    private val printerManager: POIPrinterManager by lazy { POIPrinterManager(this) }
    var printStatus: MutableLiveData<PrintStatus> = MutableLiveData()

    var commonMethods = commonMethods(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityReversalBinding.inflate(layoutInflater)

        setContentView(binding.root)
        sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
        dbHandler=DBHandler(this)
        showProgressDialog2= SweetAlertDialog(this,SweetAlertDialog.NORMAL_TYPE)
        showProgressDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        showProgressDialog.setTitleText("Please wait...")
        showProgressDialog.setCancelable(false)
        binding.layoutTransactionStatus.printReceipt.setOnClickListener {
            FirebaseDatabaseSingleton.setLog("onClick - printReceipt")
            binding.layoutTransactionStatus.startNewTransaction.isClickable = false
            binding.layoutTransactionStatus.printReceipt.isClickable = false
            printReceipt()
        }
        binding.layoutTransactionStatus.startNewTransaction.setOnClickListener {

            startActivity(Intent(this,SupervisorMainActivity::class.java))
        }


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@ReversalActivity, SupervisorMainActivity::class.java))
                finish()
            }
        })
        transData=TransData(this)
        var txnDataList:List<Map<String,String>>

        binding.button.setOnClickListener {
            txnDataList=dbHandler.getTxnDataByReceiptNo(binding.editText.text.toString())
            if(txnDataList.isNotEmpty()){
                for (txnData in txnDataList) {
                    TransData.RequestFields.Field02=txnData[DBHandler.COLUMN_FIELD02].toString()
                    TransData.RequestFields.Field04=txnData[DBHandler.COLUMN_FIELD04].toString()

                    Log.d("Reversal Activity","amount:"+TransData.RequestFields.Field04)
                    TransData.RequestFields.Field11=txnData[DBHandler.COLUMN_FIELD11].toString()
                    TransData.RequestFields.Field12=txnData[DBHandler.COLUMN_FIELD12].toString()
                    TransData.RequestFields.Field13=txnData[DBHandler.COLUMN_FIELD13].toString()
                    TransData.RequestFields.Field14=txnData[DBHandler.COLUMN_FIELD14].toString()

                    TransData.RequestFields.Field37 = txnData[DBHandler.COLUMN_FIELD37].toString()
                    TransData.RequestFields.Field38 = txnData[DBHandler.COLUMN_FIELD38].toString()
                    Log.d("Reversal Activity","fld38:"+TransData.RequestFields.Field38)

                    TransData.RequestFields.Field39 = txnData[DBHandler.COLUMN_FIELD39].toString()
                    Log.d("Reversal Activity","fld39:"+TransData.RequestFields.Field39)

                    //Log.d("TAG", "filed39"+TransData.RequestFields.Field39)

                }
                Toast.makeText(this,"TXN Data found",Toast.LENGTH_SHORT).show()
                //val bundle=Bundle()
                //bundle.putString("field04",TransData.RequestFields.Field04)
               showConfirmDialog(TransData.RequestFields.Field04)


        }else{
                Toast.makeText(this,"NO TXN RECORDED with this receipt no.!",Toast.LENGTH_SHORT).show()
        }
        }

    }
    private fun showConfirmDialog(amount: String) {
        val dialog = SweetAlertDialog(this)
            .setTitleText("Confirmation")
            .setContentText("Are you sure you want to Reverse ${sharedPreferences.getString("Currency", "")}${commonMethods.getReadableAmount(amount)}?")
            .setConfirmText("Confirm")
            .setCancelText("Cancel")
            .setConfirmClickListener {dialog ->
                if (sharedPreferences.getString("TXN_TYPE", "").equals(Txntype.reversal)) {
                    // Code to execute when "Confirm" is clicked
                    val intent = Intent(this, SupervisorLogin::class.java)
                    //intent.putExtras(bundle.getString("field04",""))
                    startActivity(intent)
                    finish()
                } else {
                    // Code to execute when "Cancel" is clicked
                    dialog?.dismiss()
                    showProgressDialog.show()
                    handler = Handler()
                    handler.postDelayed({
                        Thread(doManualReversal).start()
                    }, 2000)

                }
            }
            .setCancelClickListener { dialog ->
                // Code to execute when "Cancel" is clicked
                dialog?.dismiss()
            }
        dialog.show()
    }

    private val doManualReversal = Runnable {
        dbHandler = DBHandler(this)
        transData.assignValue2Fields()
        val packet = transData.packRequestFields()
        Log.d("tag", "packet ")
        val com = Comm("${dbHandler.getIPAndPortNumber()?.first}", "${dbHandler.getIPAndPortNumber()?.second}".toInt())
        if (!com.connect()) {
            Log.d("tag", "Connection failed")
        } else {
            com.send(packet)
            Log.d("tag", "message sent...")
            val response = com.receive(1024, 30)
            Log.d("tag", "Received response: $response")
            Log.d("tag", "manual response response: ${response?.let { HexUtil.toHexString(it) }}")
            if (response == null) {
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        showProgressDialog.dismiss()
                        showAlert("txn failed!")
                    }
                }
            } else {
                response?.let { HexUtil.toHexString(it) }?.let { transData.unpackResponseFields(it) }
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        showProgressDialog.dismiss()
                    }
                    if (TransData.ResponseFields.Field39 == "00") {
                        transData.transactionStatus = true
                        runOnUiThread {
                            txnStatusView4Approval()
                        }
                    } else {
                        runOnUiThread {
                            txnStatusView4Decline()
                            //cardReadActivity.listenerForManualTxn()
                        }
                    }
                }
            }
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
        binding.layoutTransactionStatus.tvApprovalCode.text="Response Code :${TransData.ResponseFields.Field39}"
        binding.layoutTransactionStatus.transactionStatusImage.setImageResource(R.drawable.ic_tranx_declined)


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
        val bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.nib_logo)
        printerManager.addPrintLine(BitmapPrintLine(bitmap, PrintLine.CENTER))

        printerManager.addPrintLine(
            TextPrintLine(
                "NIB Bank", PrintLine.CENTER, 20, false
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
        /*  printerManager.addPrintLine(
              TextPrintLine(
                  "1/F, Riyadh Saudi Arabia", PrintLine.CENTER, 16, false
              )
          )*/
        //TID:30102023
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
                "REVERSAL (Manual Card Entry)", PrintLine.LEFT, 20, true
            )
        )


        printerManager.addPrintLine(
            TextPrintLine(
                maskPan(TransData.RequestFields.Field02), PrintLine.LEFT, 20, true
            )
        )

        printerManager.addPrintLine(
            TextPrintLine(
                "EXPIRY DATE : ${formatExpiryDate(TransData.RequestFields.Field14)}",
                PrintLine.LEFT,
                20,
                true
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
        printerManager.addPrintLine(
            TextPrintLine(
                "PLEASE DEBIT MY ACCOUNT", PrintLine.CENTER, 16, false
            )
        )


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
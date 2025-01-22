package net.geidea.payment.transaction.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import net.geidea.payment.transaction.model.EntryMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geidea.payment.MainActivity
import net.geidea.payment.MainMenuActivity
import net.geidea.payment.customviews.PasswordDialog
import net.geidea.payment.R
import net.geidea.payment.Txntype
import net.geidea.payment.customdialog.DialogLogoutConfirm
import net.geidea.payment.customviews.AmountFragment
import net.geidea.payment.utils.TransactionProcess
import net.geidea.payment.utils.TransactionProcess.MultipleApplication
import net.geidea.payment.databinding.ActivityCardReadBinding
import net.geidea.payment.listener.PinPadListener
import net.geidea.payment.print.PrintStatus
import net.geidea.payment.transaction.model.TransData
import net.geidea.payment.transaction.viewmodel.CardReadViewModel
import net.geidea.payment.users.supervisor.SupervisorLogin
import net.geidea.payment.users.supervisor.SupervisorMainActivity
import net.geidea.payment.utils.FirebaseDatabaseSingleton
import net.geidea.payment.utils.SESSION_PIN_KEY_INDEX
import net.geidea.payment.utils.commonMethods
import net.geidea.utils.BuzzerUtils
import net.geidea.utils.CurrencyConverter
import net.geidea.utils.dialog.DialogActionListener
import net.geidea.utils.dialog.SweetAlertDialog
import net.geidea.utils.extension.gone
import net.geidea.utils.extension.visible
import net.geidea.utils.extension.withCoroutineDelay

@AndroidEntryPoint
class CardReadActivity : AppCompatActivity() {

    private val tag = CardReadActivity::class.java.simpleName

    private lateinit var binding: ActivityCardReadBinding
    private val cardReadViewModel by viewModels<CardReadViewModel>()
    private var isTransactionCompleted = false
    public lateinit var showProgressDialog: SweetAlertDialog
    lateinit var showProgressDialog2: SweetAlertDialog
    private lateinit var showProgressDialog3: DialogLogoutConfirm

    private lateinit var sharedPreferences:SharedPreferences
    var commonMethods = commonMethods(this)

    companion object {
        fun startTransaction(context: Context, amount: Long) {
            val intent = Intent(context, CardReadActivity::class.java).apply {
                putExtra("amount", amount)

            }
            context.startActivity(intent)
        }
        lateinit var cardread:CardReadActivity;
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //showProgressDialog3 =  DialogLogoutConfirm(thi)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_card_read)
        binding.viewModel = cardReadViewModel
        binding.lifecycleOwner = this
        sharedPreferences= getSharedPreferences("SHARED_DATA",Context.MODE_PRIVATE)
        cardread = this
        binding.currency.text=sharedPreferences.getString("Currency","")
        val amount = intent.getLongExtra("amount", 0)
        if(sharedPreferences.getString("TXN_TYPE","").equals(Txntype.reversal)){
            binding.toolbarView.toolbarTitle.text="Reversal"
            binding.amountInEnglish.text= commonMethods.getReadableAmount(TransData.RequestFields.Field04)
        }else if(sharedPreferences.getString("TXN_TYPE","").equals(Txntype.purchase)){
            binding.toolbarView.toolbarTitle.text="Purchase"
            binding.amountInEnglish.text=commonMethods.getReadableAmount(amount.toString())
        }else if(sharedPreferences.getString("TXN_TYPE","").equals(Txntype.refund)){
            binding.toolbarView.toolbarTitle.text="Refund"
            binding.amountInEnglish.text=commonMethods.getReadableAmount(amount.toString())
        }
        cardReadViewModel.setAmountEnglish(CurrencyConverter.convertWithoutSAR(amount))
        cardReadViewModel.setAmount(amount)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@CardReadActivity, MainMenuActivity::class.java))
                finish()
            }
        })
        observeTransactionProgress()

        //Response Status
        responceobserver()

        cardReadViewModel.startTransaction()
        binding.toolbarView.backButton.setOnClickListener {
            if (isTransactionCompleted) {
                onBackPressedDispatcher.onBackPressed()
            } else {
                cardReadViewModel.stopTransaction()
            }
        }

        binding.layoutTransactionStatus.printReceipt.setOnClickListener {
            FirebaseDatabaseSingleton.setLog("onClick - printReceipt")
            binding.layoutTransactionStatus.startNewTransaction.isClickable = false
            binding.layoutTransactionStatus.printReceipt.isClickable = false
            observePrintStatus()
            cardReadViewModel.printReceipt()
            startActivity(Intent(this,MainMenuActivity::class.java))
        }
        binding.layoutTransactionStatus.startNewTransaction.setOnClickListener {
            FirebaseDatabaseSingleton.setLog("onClick - startNewTransaction")
            startActivity(Intent(this,MainActivity::class.java))
        }

    }

    private fun observeTransactionProgress() {
        showProgressDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        if(sharedPreferences.getInt("TimeoutFlag",0)==1){
            showProgressDialog.setTitleText("Processing timeout...")

        }else {
            showProgressDialog.setTitleText("Please wait...")

            }
        showProgressDialog.setCancelable(false)



        cardReadViewModel.transactionStatus.observe(this) {
            when (it) {
                is TransactionProcess.CardReadSuccess -> {
                    FirebaseDatabaseSingleton.setLog("CardReadSuccess")
                    Log.d(tag, "111111")
                    BuzzerUtils.playSuccessSound()
                    cardReadSuccess(it.entryMode, it.appLabelName)
                    if(it.entryMode.equals(EntryMode.CONTACTLESS)){
                        showProgressDialog.show()
                    }
                }

                is MultipleApplication -> {
                    FirebaseDatabaseSingleton.setLog("MultipleApplication")
                    Log.d(tag, "111222")
                    val builder = AlertDialog.Builder(this@CardReadActivity)
                    builder.setTitle(getString(R.string.selec_application))
                    builder.setItems(it.labelList.toTypedArray()) { _, selectedIndex ->
                        FirebaseDatabaseSingleton.setLog("selectedIndex - $selectedIndex")
                        cardReadViewModel.applicationSelected(selectedIndex)
                    }
                    builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        FirebaseDatabaseSingleton.setLog("AlertDialog - dismiss()")
                        dialog.dismiss()
                        cardReadViewModel.applicationSelected(-1)
                    }
                    builder.setCancelable(false)
                    builder.show()
                }

                is TransactionProcess.PinRequestedDukpt -> {
                    FirebaseDatabaseSingleton.setLog("PinRequested dukpt")
                    showPinPadDuKpt()
                }

                is TransactionProcess.PinRequestedMkSk -> {
                    FirebaseDatabaseSingleton.setLog("PinRequested mksk")
                    showPinPadMkSk()
                }

                is TransactionProcess.CardRetry -> {
                    FirebaseDatabaseSingleton.setLog("CardRetry - ${it.message}")
                    BuzzerUtils.playErrorSound()
                    processCardRetry(it.message)
                    Log.d(tag, "111444")
                }

                is TransactionProcess.TransactionErr -> {
                    FirebaseDatabaseSingleton.setLog("TransactionErr - ${it.message}")
                    showErrorMessage(it.message)
                    Log.d(tag, "111555${it.message}")
                }

                is TransactionProcess.EMVError -> {
                    FirebaseDatabaseSingleton.setLog("EMVError - ${it.message}")
                    showErrorMessage(it.message)
                    Log.d(tag, "111666${it.message}")
                    startActivity(Intent(this,MainMenuActivity::class.java))
                    finish()
                }

                is TransactionProcess.CardNotSupport -> {
                    FirebaseDatabaseSingleton.setLog("CardNotSupport - ${it.message}")
                    showErrorMessage(it.message)
                    Log.d(tag, "111777")
                }

                is TransactionProcess.TransactionStatus -> {

                    Log.d(tag, "111888")
                    isTransactionCompleted = true
                    binding.waveCardAgainLayout.gone()
                    binding.swipeInsertInfo.gone()
                    binding.message.gone()

                    binding.layoutTransactionStatus.root.visible()
                    if (it.status) {

                       showProgressDialog.dismiss()
                        //commonMethods.dismissdialog()
                        FirebaseDatabaseSingleton.setLog("TransactionStatus - Approved")
                        BuzzerUtils.playForTransactionApproved()
                        binding.layoutTransactionStatus.transactionStatus.text =
                            getString(R.string.approved)
                        binding.layoutTransactionStatus.tvApprovalCode.text="Approval Code :${TransData.ResponseFields.Field38}"
                        binding.layoutTransactionStatus.transactionStatusImage.setImageResource(R.drawable.approved)
                    } else {
                        showProgressDialog.dismiss()
                        //commonMethods.dismissdialog()
                        //commonMethods.showAlert("Declined")
                        FirebaseDatabaseSingleton.setLog("TransactionStatus - Declined")
                        BuzzerUtils.playForTransactionDeclined()
                        binding.layoutTransactionStatus.transactionStatus.text =
                            getString(R.string.declined)
                        binding.layoutTransactionStatus.tvApprovalCode.text="Response Code :${TransData.ResponseFields.Field39}"
                        binding.layoutTransactionStatus.transactionStatusImage.setImageResource(R.drawable.ic_tranx_declined)
                    }
                }

                else -> {

                }
            }
        }
    }

    private fun cardReadSuccess(entryMode: EntryMode, appLabelName: String) {
        FirebaseDatabaseSingleton.setLog("cardReadSuccess - $entryMode")
        lifecycleScope.launch(Dispatchers.Main.immediate) {
            if (entryMode != EntryMode.CONTACTLESS) {
                binding.ledLight.visibility = View.GONE
            }

            if (cardReadViewModel.getCardEntryMode()!=null && cardReadViewModel.getCardEntryMode() == EntryMode.CONTACTLESS) binding.ledLight.ledGlowCardTap(
                true, entryMode
            )
            binding.swipeInsertInfo.visibility = View.GONE
            binding.cardReadView.visibility = View.VISIBLE
            binding.message.text = appLabelName
            binding.message.visibility = View.VISIBLE
        }
    }

    private fun showPinPadDuKpt() {
        FirebaseDatabaseSingleton.setLog("showPinPad")
        var dialog: PasswordDialog
        if (sharedPreferences.getString("TXN_TYPE", "").equals(Txntype.reversal)) {
            dialog = PasswordDialog(
                this@CardReadActivity,
                "Reversal",
                cardReadViewModel.isIcSlot,
                cardReadViewModel.pinBundle!!,
                1,// DUKPT Key Index
                TransData.RequestFields.Field04.toLong(),
                cardReadViewModel.pinEntryCount,
                object : PinPadListener {
                    override fun pinPadDisplayed() {

                    }

                    override fun onPinTimeout() {
                        FirebaseDatabaseSingleton.setLog("onPinTimeout")
                        cardReadViewModel.isPinTimeout = true
                        cardReadViewModel.stopTransaction()
                    }

                    override fun onPinQuit() {
                        FirebaseDatabaseSingleton.setLog("onPinQuit")
                        cardReadViewModel.isPinQuit = true
                        cardReadViewModel.stopTransaction()
                    }

                    override fun pinType(pinType: Int) {
                        Log.d("tag","Pin type Verified"+ pinType)
                        FirebaseDatabaseSingleton.setLog("pinType - $pinType")
                    }

                    override fun offlinePinVerified() {
                        Log.d("tag","offline Verified")
                        FirebaseDatabaseSingleton.setLog("offlinePinVerified")
                        showProgressDialog.show()
                        Log.d("tag", "offlinePinVerified")
                        //commonMethods.showloading()
                    }

                    override fun on117Or196PinBlockSuccess(pinBlock: String) {
                        Log.d("tag", "hostWrongPinOrForcePin")

                        showProgressDialog.show()
                        FirebaseDatabaseSingleton.setLog("hostWrongPinOrForcePin - $pinBlock")
                        cardReadViewModel.transData.pinBlock = pinBlock
                        Log.d("tag", "online pin")

                        //commonMethods.showloading()

                    }

                    override fun pinRetry(pendingCount: Int, retryCount: Int, pinType: Int) {
                        FirebaseDatabaseSingleton.setLog("pendingCount - $pendingCount")
                        FirebaseDatabaseSingleton.setLog("retryCount - $retryCount")
                        FirebaseDatabaseSingleton.setLog("pinType - $pinType")
                    }
                },
            )
            dialog.showDialog()
        }
    }


        private fun showPinPadMkSk() {
            FirebaseDatabaseSingleton.setLog("showPinPadMkSk")
            var dialog:PasswordDialog
            if (sharedPreferences.getString("TXN_TYPE", "").equals(Txntype.reversal))
            {
                dialog = PasswordDialog(
                this@CardReadActivity,
                "Reversal",
                cardReadViewModel.isIcSlot,
                cardReadViewModel.pinBundle!!,
                SESSION_PIN_KEY_INDEX,// tpk Index
                TransData.RequestFields.Field04.toLong(),
                cardReadViewModel.pinEntryCount,
                object : PinPadListener {
                    override fun pinPadDisplayed() {

                    }

                    override fun onPinTimeout() {
                        FirebaseDatabaseSingleton.setLog("onPinTimeout")
                        cardReadViewModel.isPinTimeout = true
                        cardReadViewModel.stopTransaction()
                    }

                    override fun onPinQuit() {
                        FirebaseDatabaseSingleton.setLog("onPinQuit")
                        cardReadViewModel.isPinQuit = true
                        cardReadViewModel.stopTransaction()
                    }

                    override fun pinType(pinType: Int) {
                        Log.d("tag","Pin type Verified"+ pinType)
                        FirebaseDatabaseSingleton.setLog("pinType - $pinType")
                    }

                    override fun offlinePinVerified() {
                        Log.d("tag", "offlinePinVerified")
                        showProgressDialog.show()
                        FirebaseDatabaseSingleton.setLog("offlinePinVerified")
                    }

                    override fun on117Or196PinBlockSuccess(pinBlock: String) {
                       //FirebaseDatabaseSingleton.setLog("hostWrongPinOrForcePin - $pinBlock")
                        cardReadViewModel.transData.pinBlock = pinBlock
                        Log.d("tag", "online pin")
                    }

                    override fun pinRetry(pendingCount: Int, retryCount: Int, pinType: Int) {
                        FirebaseDatabaseSingleton.setLog("pendingCount - $pendingCount")
                        FirebaseDatabaseSingleton.setLog("retryCount - $retryCount")
                        FirebaseDatabaseSingleton.setLog("pinType - $pinType")
                        Log.d("tag", "pin retry")
                    }
                },
            )
                dialog.showDialog()

            }else if (sharedPreferences.getString("TXN_TYPE", "").equals(Txntype.refund))
            {
                dialog = PasswordDialog(
                    this@CardReadActivity,
                    "Refund",
                    cardReadViewModel.isIcSlot,
                    cardReadViewModel.pinBundle!!,
                    SESSION_PIN_KEY_INDEX,// tpk Index
                    TransData.RequestFields.Field04.toLong(),
                    cardReadViewModel.pinEntryCount,
                    object : PinPadListener {
                        override fun pinPadDisplayed() {

                        }

                        override fun onPinTimeout() {
                            FirebaseDatabaseSingleton.setLog("onPinTimeout")
                            cardReadViewModel.isPinTimeout = true
                            cardReadViewModel.stopTransaction()
                        }

                        override fun onPinQuit() {
                            FirebaseDatabaseSingleton.setLog("onPinQuit")
                            cardReadViewModel.isPinQuit = true
                            cardReadViewModel.stopTransaction()
                        }

                        override fun pinType(pinType: Int) {
                            Log.d("tag","Pin type Verified"+ pinType)
                            FirebaseDatabaseSingleton.setLog("pinType - $pinType")
                        }

                        override fun offlinePinVerified() {
                            Log.d("tag", "offlinePinVerified")
                            showProgressDialog.show()
                            FirebaseDatabaseSingleton.setLog("offlinePinVerified")
                        }

                        override fun on117Or196PinBlockSuccess(pinBlock: String) {
                            //FirebaseDatabaseSingleton.setLog("hostWrongPinOrForcePin - $pinBlock")
                            cardReadViewModel.transData.pinBlock = pinBlock
                            Log.d("tag", "online pin")
                        }

                        override fun pinRetry(pendingCount: Int, retryCount: Int, pinType: Int) {
                            FirebaseDatabaseSingleton.setLog("pendingCount - $pendingCount")
                            FirebaseDatabaseSingleton.setLog("retryCount - $retryCount")
                            FirebaseDatabaseSingleton.setLog("pinType - $pinType")
                            Log.d("tag", "pin retry")
                        }
                    },
                )
                dialog.showDialog()

            }else if(sharedPreferences.getString("TXN_TYPE", "").equals(Txntype.purchase))
            {
                dialog = PasswordDialog(
                    this@CardReadActivity,
                    "Purchase",
                    cardReadViewModel.isIcSlot,
                    cardReadViewModel.pinBundle!!,
                    SESSION_PIN_KEY_INDEX,// tpk Index
                    cardReadViewModel.transData.amount,
                    cardReadViewModel.pinEntryCount,
                    object : PinPadListener {
                        override fun pinPadDisplayed() {

                        }

                        override fun onPinTimeout() {
                            FirebaseDatabaseSingleton.setLog("onPinTimeout")
                            cardReadViewModel.isPinTimeout = true
                            cardReadViewModel.stopTransaction()
                        }

                        override fun onPinQuit() {
                            FirebaseDatabaseSingleton.setLog("onPinQuit")
                            cardReadViewModel.isPinQuit = true
                            cardReadViewModel.stopTransaction()
                        }

                        override fun pinType(pinType: Int) {
                            FirebaseDatabaseSingleton.setLog("pinType - $pinType")
                            Log.d("tag","Pin type Verified"+ pinType)
                        }

                        override fun offlinePinVerified() {
                            showProgressDialog.show()
                            Log.d("tag", "offlinePinVerified")
                            FirebaseDatabaseSingleton.setLog("offlinePinVerified")
                        }

                        override fun on117Or196PinBlockSuccess(pinBlock: String) {
                            //FirebaseDatabaseSingleton.setLog("hostWrongPinOrForcePin - $pinBlock")
                            Log.d("tag", "online pin")
                            cardReadViewModel.transData.pinBlock = pinBlock
                            Log.d("tag","pin block"+ pinBlock)
                        }

                        override fun pinRetry(pendingCount: Int, retryCount: Int, pinType: Int) {
                            FirebaseDatabaseSingleton.setLog("pendingCount - $pendingCount")
                            FirebaseDatabaseSingleton.setLog("retryCount - $retryCount")
                            FirebaseDatabaseSingleton.setLog("pinType - $pinType")
                            Log.d("tag","Pin type Verified"+ pinType)
                        }
                    },
                )
                dialog.showDialog()

            }
            //dialog.showDialog()
        }



    private fun showMessageWithAction(
        context: Context = this,
        dialogType: Int = SweetAlertDialog.ERROR_TYPE,
        title: String = "",
        contentText: String = "",
        positiveBtnText: String = getString(R.string.ok),
        negativeBtnText: String = getString(R.string.no),
        dialogActionListener: DialogActionListener? = null,
    ) {
        FirebaseDatabaseSingleton.setLog("showMessageWithAction - title - $title")
        FirebaseDatabaseSingleton.setLog("showMessageWithAction - contentText - $contentText")

        val isActionButtonRequired = false
        val timeOut = 5
        val customSweetAlertDialog = SweetAlertDialog(context, dialogType)
        if (title.isNotEmpty()) customSweetAlertDialog.setTitleText(title)
        if (contentText.isNotEmpty()) customSweetAlertDialog.setContentText(contentText)
        customSweetAlertDialog.setCancelable(false)
        customSweetAlertDialog.setTimeout(timeOut)
        customSweetAlertDialog.setCanceledOnTouchOutside(false)
        if (isActionButtonRequired) {
            if (positiveBtnText.isNotEmpty()) customSweetAlertDialog.setConfirmText(positiveBtnText)
            if (negativeBtnText.isNotEmpty()) customSweetAlertDialog.setCancelText(negativeBtnText)
        }

        customSweetAlertDialog.setCancelClickListener {
            customSweetAlertDialog.dismissWithAnimation()
            dialogActionListener?.cancelButtonClicked()
        }

        customSweetAlertDialog.setConfirmClickListener {
            customSweetAlertDialog.dismissWithAnimation()
            dialogActionListener?.confirmButtonClicked()
        }

        customSweetAlertDialog.setOnTimeoutDismissListener {
            customSweetAlertDialog.dismissWithAnimation()
            dialogActionListener?.cancelButtonClicked()
        }

        customSweetAlertDialog.setOnDismissListener { dialogActionListener?.onDismiss() }
        customSweetAlertDialog.show()
    }

    private fun showErrorMessage(message: String) {
        FirebaseDatabaseSingleton.setLog("showErrorMessage - $message")

        BuzzerUtils.playClickSound()
        binding.mainContainer.visibility = View.GONE
        showMessageWithAction(context = this@CardReadActivity,
            contentText = message,
            dialogActionListener = object : DialogActionListener {
                override fun onDismiss() {
                    super.onDismiss()

                    finish()

                }
            })
    }



    private fun processCardRetry(message: String) {
        FirebaseDatabaseSingleton.setLog("processCardRetry - $message")
        cardReadViewModel.isCardRetryError = true
        binding.swipeInsertInfo.gone()
        binding.waveCardAgainLayout.visible()

        if (cardReadViewModel.getCardEntryMode()!=null && cardReadViewModel.getCardEntryMode() == EntryMode.CONTACTLESS) {
            binding.ledLight.ledGlowCardTap(false)
            cardReadViewModel.setCardReadErrorMsg(
                getString(R.string.wave_card_again)
            )
            cardReadViewModel.startTransaction()
            withCoroutineDelay(1000) {
                binding.ledLight.ledGlowDefault()
                binding.swipeInsertInfo.visible()
                arrayOf(binding.waveCardAgainLayout, binding.message).gone()
            }
        } else {
            showErrorMessage(message)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        FirebaseDatabaseSingleton.setLog("onBackPressed")
        if (isTransactionCompleted) {
            onBackPressedDispatcher.onBackPressed()
        } else {
            cardReadViewModel.stopTransaction()
        }
    }

    private fun observePrintStatus() {
        FirebaseDatabaseSingleton.setLog("observePrintStatus")
        cardReadViewModel.printStatus.observe(this) {
            when (it) {
                is PrintStatus.PrintStarted -> {
                    FirebaseDatabaseSingleton.setLog("PrintStarted")
                    showProgressDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                    showProgressDialog.setTitleText("Printing...Please wait...")
                    showProgressDialog.setCancelable(false)
                    showProgressDialog.show()
                }

                is PrintStatus.PrintCompleted -> {
                    FirebaseDatabaseSingleton.setLog("PrintCompleted")
                    showProgressDialog.dismiss()
                    finish()
                }

                is PrintStatus.PrintError -> {
                    FirebaseDatabaseSingleton.setLog("PrintError")
                    binding.layoutTransactionStatus.startNewTransaction.isClickable = true
                    binding.layoutTransactionStatus.printReceipt.isClickable = true
                    if (this@CardReadActivity::showProgressDialog.isInitialized && showProgressDialog.isShowing) {
                        showProgressDialog.dismiss()
                    }
                    showErrorMessage(it.message)

                }
            }
        }
    }
    fun responceobserver(){
        cardReadViewModel.responcestatus.observe(this) {
            var status = ""
            status = it
            Log.d("CardviewActivity", "Response Status" + it)

                if (status == "Null"){
                    showAlert("Failure Timeout!")
            }
        }
    }
     fun showAlert(message:String){

        showProgressDialog2 = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
        showProgressDialog2.setTitleText(message)
        showProgressDialog2.setCancelable(false)
        showProgressDialog2.show()
        showProgressDialog2.setConfirmClickListener(
            listener = SweetAlertDialog.OnSweetClickListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish() // Close
            }
        )

    }

}
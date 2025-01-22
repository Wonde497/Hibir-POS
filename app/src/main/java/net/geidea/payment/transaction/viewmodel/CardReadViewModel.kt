package net.geidea.payment.transaction.viewmodel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.geidea.payment.transaction.model.EntryMode
import com.pos.sdk.emvcore.IPosEmvCoreListener
import com.pos.sdk.emvcore.POIEmvCoreManager
import com.pos.sdk.emvcore.POIEmvCoreManager.EmvOnlineConstraints
import com.pos.sdk.emvcore.PosEmvErrorCode
import com.pos.sdk.printer.POIPrinterManager
import com.pos.sdk.printer.models.BitmapPrintLine
import com.pos.sdk.printer.models.PrintLine
import com.pos.sdk.printer.models.TextPrintLine
import com.pos.sdk.security.POIHsmManage
import com.pos.sdk.utils.PosUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import net.geidea.payment.utils.AC
import net.geidea.payment.BuildConfig
import net.geidea.payment.DBHandler
import net.geidea.payment.MainMenuActivity
import net.geidea.payment.utils.CARD_AID
import net.geidea.payment.utils.CID
import net.geidea.payment.utils.CVM_PERFORMED_DC_TAG
import net.geidea.payment.utils.EXP_DATE
import net.geidea.payment.utils.PAN_NUMBER
import net.geidea.payment.print.Printer
import net.geidea.payment.print.Printer.printList
import net.geidea.payment.R
import net.geidea.payment.Txntype
import net.geidea.payment.com.Comm
import net.geidea.payment.customdialog.DialogLogoutConfirm
import net.geidea.payment.print.PrintStatus
import net.geidea.payment.utils.TRACK2
import net.geidea.payment.transaction.model.TransData
import net.geidea.payment.utils.TransactionProcess
import net.geidea.payment.tlv.BerTlv
import net.geidea.payment.tlv.BerTlvBuilder
import net.geidea.payment.tlv.BerTlvParser
import net.geidea.payment.tlv.BerTlvs
import net.geidea.payment.tlv.HexUtil
import net.geidea.payment.transaction.view.CardReadActivity
import net.geidea.payment.utils.FirebaseDatabaseSingleton
import net.geidea.payment.utils.commonMethods
import net.geidea.payment.utils.pinBlockType
import net.geidea.utils.BuzzerUtils
import net.geidea.utils.CurrencyConverter
import net.geidea.utils.DateTimeFormat
import net.geidea.utils.LogUtils
import net.geidea.utils.convertDateTime
import net.geidea.utils.decodeHex
import net.geidea.utils.dialog.SweetAlertDialog
import net.geidea.utils.extension.ioCoroutine
import net.geidea.utils.formatExpiryDate
import net.geidea.utils.generateRRN
import net.geidea.utils.getCurrentDateTime
import net.geidea.utils.getSTAN
import net.geidea.utils.maskPan
import javax.inject.Inject

@HiltViewModel
class CardReadViewModel @Inject constructor(@ApplicationContext val context: Context) :
    ViewModel() {
    private val tag = CardReadViewModel::class.java.simpleName
    private val emvCoreManager = POIEmvCoreManager.getDefault()
    private val dbhandler=DBHandler(context)

    private val sharedPreferences=context.getSharedPreferences("SHARED_DATA",Context.MODE_PRIVATE)
    private  var editor=sharedPreferences.edit()
    private val emvCoreListener = POIEmvCoreListener()
    private lateinit var showProgressDialog: DialogLogoutConfirm
   private lateinit var showProgressDialog2:SweetAlertDialog
    private lateinit var entryMode: EntryMode
    private val printerManager: POIPrinterManager by lazy { POIPrinterManager(context) }
    var transactionStatus: MutableLiveData<TransactionProcess> = MutableLiveData()
    var responcestatus: MutableLiveData<String> = MutableLiveData()

        private set
    val transData = TransData(context)
    private var tagValueMap = hashMapOf<String, String>()
    var pinEntryCount = -1
        private set
    var isIcSlot = false
        private set
    var pinBundle: Bundle? = null
    var isPinQuit = false
    var isPinTimeout = false
    var isCardRetryError = false

    var common = commonMethods(context)
    private val _cardReadErrorMsg: MutableLiveData<String> = MutableLiveData()
    val cardReadErrorMsg: LiveData<String> = _cardReadErrorMsg
    fun setCardReadErrorMsg(errorMessage: String) {
        _cardReadErrorMsg.postValue(errorMessage)
    }

    private val _amountEnglish: MutableLiveData<String> = MutableLiveData()
    val amountEnglish: LiveData<String> get() = _amountEnglish

    fun setAmountEnglish(amount: String) = _amountEnglish.setValue(amount)


    private var isCardReadSuccess = false

    var printStatus: MutableLiveData<PrintStatus> = MutableLiveData()
        private set


    fun startTransaction() {
        val transConfigBundle = Bundle().apply {
            putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_TYPE, 0x00)
            putLong(POIEmvCoreManager.EmvTransDataConstraints.TRANS_AMOUNT, transData.amount)
            putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_MODE, getCardSupportMode())
            putBoolean(POIEmvCoreManager.EmvTransDataConstraints.APPLE_VAS, false)
            putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_TIMEOUT, 30) // card read timeout
            putBoolean(POIEmvCoreManager.EmvTransDataConstraints.USE_FILTER, true)
            putBoolean(POIEmvCoreManager.EmvTransDataConstraints.USE_SPECIAL_AIDS_ELECTION, true)
            putBoolean(POIEmvCoreManager.EmvTransDataConstraints.SPECIAL_CONTACT, false)
            putBoolean(POIEmvCoreManager.EmvTransDataConstraints.SPECIAL_CONTACT_TIME, false)
            putBoolean(POIEmvCoreManager.EmvTransDataConstraints.SPECIAL_MAGSTRIPE, false)
            putBoolean(POIEmvCoreManager.EmvTransDataConstraints.USE_CARD_READ_SUCCESS, true)
            putBoolean(POIEmvCoreManager.EmvTransDataConstraints.USE_MAGSTRIPE_FILTER, true)
            putBoolean("useGEIDEA", true)
        }
        FirebaseDatabaseSingleton.setLog("$transConfigBundle")
        val result = emvCoreManager.startTransaction(transConfigBundle, emvCoreListener)
        FirebaseDatabaseSingleton.setLog("result - $result")
        if (PosEmvErrorCode.EXCEPTION_ERROR == result) {
            onTransEnd("Exception in Start Transaction")
        } else if (PosEmvErrorCode.EMV_ENCRYPT_ERROR == result) {
            onTransEnd("EMV Encryption error Start Transaction")
        }
    }


    private fun getCardSupportMode(): Int {
        return POIEmvCoreManager.DEVICE_CONTACT or POIEmvCoreManager.DEVICE_CONTACTLESS or POIEmvCoreManager.DEVICE_MAGSTRIPE
    }

    private inner class POIEmvCoreListener : IPosEmvCoreListener.Stub() {

        override fun onEmvProcess(code: Int, bundle: Bundle?) {
            FirebaseDatabaseSingleton.setLog("onEmvProcess")
            transData.transactionType = context.getString(R.string.purchase)
            when (code) {
                POIEmvCoreManager.DEVICE_CONTACT -> {
                    FirebaseDatabaseSingleton.setLog("DEVICE_CONTACT")
                    transactionStatus.postValue(TransactionProcess.CardDetected)
                    BuzzerUtils.tripleBeep()
                    entryMode = EntryMode.DIPPED
                    transData.entryMode = entryMode.toString()
                    transactionStatus.postValue(TransactionProcess.DippedCardDetected)
                }

                POIEmvCoreManager.DEVICE_CONTACTLESS -> {
                    FirebaseDatabaseSingleton.setLog("DEVICE_CONTACTLESS")
                    entryMode = EntryMode.CONTACTLESS
                    transData.entryMode = entryMode.toString()
                    transactionStatus.postValue(TransactionProcess.CardDetected)
                }

                POIEmvCoreManager.DEVICE_MAGSTRIPE -> {
                    FirebaseDatabaseSingleton.setLog("DEVICE_MAGSTRIPE")
                    entryMode = EntryMode.SWIPED
                    transData.entryMode = entryMode.toString()
                    BuzzerUtils.playSuccessSound()
                    transactionStatus.postValue(TransactionProcess.CardDetected)
                }

                PosEmvErrorCode.EMV_MULTI_CONTACTLESS -> {
                    FirebaseDatabaseSingleton.setLog("EMV_MULTI_CONTACTLESS")
                    transactionStatus.postValue(TransactionProcess.MultipleCardFound)
                }

            }
        }

        override fun onSelectApplication(labelList: MutableList<String>, p1: Boolean) {
            val appNameList = ArrayList<String>()
            labelList.forEachIndexed labelList@{ _, appName ->
                val tagValueMap = parseTLV(PosUtils.hexStringToBytes(appName))
                val labelName = tagValueMap["50"]
                appNameList.add(labelName!!)
            }
            transactionStatus.postValue(TransactionProcess.MultipleApplication(appNameList))
        }

        override fun onConfirmCardInfo(mode: Int, bundle: Bundle) {
            FirebaseDatabaseSingleton.setLog("onConfirmCardInfo - mode - $mode")
            when (mode) {
                POIEmvCoreManager.CMD_GPO_FILTER -> {
                    FirebaseDatabaseSingleton.setLog("POIEmvCoreManager.CMD_GPO_FILTER")
                    bundle.putByteArray(
                        POIEmvCoreManager.EmvCardInfoConstraints.OUT_TLV, ByteArray(0)
                    )
                    emvCoreManager.onSetCardInfoResponse(bundle)
                    return
                }

                POIEmvCoreManager.CMD_READ_RECORD_FILTER -> {
                    FirebaseDatabaseSingleton.setLog("POIEmvCoreManager.CMD_READ_RECORD_FILTER")
                    Log.d(tag, "CMD_READ_RECORD_FILTER")
                    if (entryMode == EntryMode.SWIPED) {
                        return
                    }
                    processReadRecordFilterEmvAndCtls(bundle)

                }

                POIEmvCoreManager.CMD_CARD_READ_SUCCESS -> {
                    FirebaseDatabaseSingleton.setLog("POIEmvCoreManager.CMD_CARD_READ_SUCCESS")
                    Log.d(tag, "CMD_CARD_READ_SUCCESS")
                    isCardReadSuccess = true
                    transactionStatus.postValue(
                        TransactionProcess.CardReadSuccess(
                            transData.cardLabelNameEng, entryMode
                        )
                    )
                }

                POIEmvCoreManager.CMD_TRY_OTHER_APPLICATION -> {
                    FirebaseDatabaseSingleton.setLog("POIEmvCoreManager.CMD_TRY_OTHER_APPLICATION")
                    bundle.putBoolean(POIEmvCoreManager.EmvCardInfoConstraints.OUT_CONFIRM, true)
                    emvCoreManager.onSetCardInfoResponse(bundle)
                }


            }
        }

        override fun onKernelType(kernelType: Int) {
            FirebaseDatabaseSingleton.setLog("onKernelType - $kernelType")
            Log.d(tag, "Kernel Type $kernelType")
        }

        override fun onSecondTapCard() {
            FirebaseDatabaseSingleton.setLog("onSecondTapCard")
        }

        override fun onRequestInputPin(bundle: Bundle) {
            Log.d(tag, "onRequestInputPin")
            //printTags()

            ioCoroutine {

                LogUtils.d(tag, "onRequestInputPin type ->$entryMode")
                isIcSlot = entryMode == EntryMode.DIPPED
                pinBundle = bundle
                pinEntryCount += 1
                pinBlockType = "mksk"
                if(pinBlockType == "dukpt") {
                    POIHsmManage.getDefault().PedDukptIncreaseKsn(1)
                    transactionStatus.postValue(TransactionProcess.PinRequestedDukpt)
                }
                else if(pinBlockType == "mksk")
                    transactionStatus.postValue(TransactionProcess.PinRequestedMkSk)
            }


        }


        override fun onRequestOnlineProcess(bundle: Bundle) {
            Log.d(tag, "onRequestOnlineProcess")
            ioCoroutine {
                val data = bundle.getByteArray(EmvOnlineConstraints.EMV_DATA)
                data?.let {
                    parseTLVAndStore(it)
                }

                setTransData()

                // common.showloading()
                //sent transaction online here
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.postDelayed({
                    Thread(onlineRequest).start()
                }, 2000)
                FirebaseDatabaseSingleton.setLog("$transData")

                printTags()
                // simulating response here

                //emvCoreManager.onSetOnlineResponse(processOnlineResult(TransData.ResponseFields.Field39))
                //emvCoreManager.onSetOnlineResponse(processOnlineResult(transData.responseMessage));
            }


        }
        val onlineRequest=Runnable{
            transData.assignValue2Fields()
            val packet=transData.packRequestFields()
            val timeoutData = transData.packFields4TimeoutReversal()
            val timeoutDataHex = timeoutData.let { HexUtil.toHexString(it) }
            //Log.d(tag, "packed data to be saved in case there is timeout...:${timeoutDataHex}")


            val com= Comm("${dbhandler.getIPAndPortNumber()?.first}","${dbhandler.getIPAndPortNumber()?.second}".toInt())
            if(!com.connect()){

                Log.d(tag,"Connection failed")

               // common.showAlert("Connection failed")
            }else{

                val timeoutFlag=sharedPreferences.getInt("TimeoutFlag",0)
                if(timeoutFlag==1){
                    val savedTimeoutData=sharedPreferences.getString("TimeoutData","")
                    val savedTimeoutDataByte=savedTimeoutData?.let { HexUtil.hexStr2Byte(it) }
                    if (savedTimeoutDataByte != null) {
                        com.send(savedTimeoutDataByte)
                        Log.d(tag, "timeout data sent ...:${savedTimeoutData}")

                    }

                    val timeoutResponse=com.receive(1024,30)
                    Log.d(tag, "timeout response...:"+timeoutResponse?.let { HexUtil.toHexString(it) })
                    editor.putInt("TimeoutFlag", 0)
                    editor.putString("TimeoutData","")
                    editor.commit()
                    if(timeoutResponse!=null){
                        com.send(packet)
                        Log.d(tag, "message sent...:")

                        val response1 = com.receive(1024, 30)
                        Log.d(tag, "Received response:$response1")
                        Log.d(tag, "Received response:" + response1?.let { HexUtil.toHexString(it) })

                        if (response1 == null) {
                            editor.putInt("TimeoutFlag", 1)
                            editor.putString("TimeoutData", timeoutDataHex)
                            editor.commit()
                            Log.d(tag, "timeout data saved:${timeoutDataHex}" )

                            CardReadActivity.cardread.showProgressDialog.dismiss()
                            responcestatus.postValue("Null")

                            Log.d(tag, "Response Null")

                        } else {
                            editor.putInt("TimeoutFlag", 0)
                            editor.commit()

                            response1?.let { HexUtil.toHexString(it) }
                                ?.let { transData.unpackResponseFields(it) }
                            emvCoreManager.onSetOnlineResponse(processOnlineResult(TransData.ResponseFields.Field39))

                        }
                    }


                }else
                {

                    com.send(packet)
                    Log.d(tag, "message sent...:")

                     val response = com.receive(1024, 30)
                    Log.d(tag, "Received response:$response")
                    Log.d(tag, "Received response:" + response?.let { HexUtil.toHexString(it) })

                    if (response == null) {
                        editor.putInt("TimeoutFlag", 1)
                        editor.putString("TimeoutData", timeoutDataHex)
                        editor.commit()
                        Log.d(tag, "timeout data saved:${timeoutDataHex}" )

                        CardReadActivity.cardread.showProgressDialog.dismiss()
                        responcestatus.postValue("Null")

                        Log.d(tag, "Response Null")

                    } else {
                        editor.putInt("TimeoutFlag", 0)
                        editor.commit()

                        response?.let { HexUtil.toHexString(it) }
                            ?.let { transData.unpackResponseFields(it) }
                        emvCoreManager.onSetOnlineResponse(processOnlineResult(TransData.ResponseFields.Field39))

                    }
                }








                //Log.d(tag,"Received response:"+ response?.let { HexUtil.toHexString(it) })
                //Log.d(tag,"Received response:"+response)
               // common.dismissdialog()
            }
        }


        override fun onTransactionResult(result: Int, bundle: Bundle) {
            ioCoroutine {
                Log.d(tag, "onTransactionResult $result")
                FirebaseDatabaseSingleton.setLog("result - $result")
                val emvData = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.EMV_DATA)
                emvData?.let {
                    parseTLVAndStore(emvData)
                    FirebaseDatabaseSingleton.setLog("tagValueMap - $tagValueMap")
                }
                if (tagValueMap.containsKey(CVM_PERFORMED_DC_TAG)) {
                    val dcTagByteArray: ByteArray =
                        PosUtils.stringToBcd(tagValueMap[CVM_PERFORMED_DC_TAG])
                    verificationMethodUsedForCurrentEMVTransaction(dcTagByteArray[2])
                }

                if (tagValueMap.containsKey(AC)) {
                    transData.applicationCryptogram = tagValueMap[AC] ?: ""
                    if (transData.applicationCryptogram.isEmpty()) {
                        transData.applicationCryptogram = "0000000000000000"
                    }
                }

                if (tagValueMap.containsKey(CID)) {
                    transData.genACResult = tagValueMap[CID] ?: ""
                    if (transData.genACResult.isEmpty()) {
                        transData.genACResult = "00"
                    }
                }
                /*if (tagValueMap.containsKey(TVR)) {
                    transData.genACResult = tagValueMap[TVR] ?: ""
                    if (transData.tvr.isEmpty()) {
                        transData.tvr = "00"
                    }
                }*/

                FirebaseDatabaseSingleton.setLog("$transData")
                // offline case
                if (transData.transactionReqDateTime.isEmpty()) {
                    setTransData()
                }

                when (result) {
                    PosEmvErrorCode.EMV_TIMEOUT -> {
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_TIMEOUT")
                        transactionStatus.postValue(TransactionProcess.EMVError(context.getString(R.string.card_read_timeout)))
                    }

                    PosEmvErrorCode.EMV_COMMAND_FAIL -> {
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_COMMAND_FAIL")
                        transactionStatus.postValue(TransactionProcess.EMVError(context.getString(R.string.emv_command_fail)))
                    }

                    PosEmvErrorCode.EMV_OTHER_ICC_INTERFACE -> {
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_OTHER_ICC_INTERFACE")
                        transactionStatus.postValue(TransactionProcess.EMVError(context.getString(R.string.use_smart_card_reader)))
                    }

                    PosEmvErrorCode.EMV_APP_EMPTY -> {
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_APP_EMPTY")
                        Log.d(tag, "PosEmvErrorCode.EMV_APP_EMPTY")

                        transactionStatus.postValue(
                            TransactionProcess.EMVError(
                                context.getString(R.string.card_scheme_not_supported)
                            )
                        )
                    }

                    PosEmvErrorCode.EMV_FALLBACK -> {
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_FALLBACK")
                        Log.d(tag, "PosEmvErrorCode.EMV_FALLBACK")

                        transactionStatus.postValue(
                            TransactionProcess.CardRetry(
                                context.getString(R.string.invalid_card)
                            )
                        )
                    }

                    PosEmvErrorCode.EMV_CARD_BLOCKED -> {
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_CARD_BLOCKED")
                        Log.d(tag, "PosEmvErrorCode.EMV_CARD_BLOCKED")

                        transactionStatus.postValue(
                            TransactionProcess.EMVError(
                                context.getString(R.string.blocked_card)
                            )
                        )
                    }

                    PosEmvErrorCode.EMV_APPROVED -> {//all emv offline approved case
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_APPROVED")
                        Log.d(tag, "PosEmvErrorCode.EMV_APPROVED")

                        printTags()
                        transData.transactionStatus = true
                        transactionStatus.postValue(
                            TransactionProcess.TransactionStatus(
                                true
                            )
                        )

                    }


                    PosEmvErrorCode.EMV_APPROVED_ONLINE -> {// all emv contact transaction host approved case
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_APPROVED_ONLINE")
                        Log.d(tag, "PosEmvErrorCode.EMV_APPROVED_ONLINE")
                        printTags()
                        transData.transactionStatus = true
                        transactionStatus.postValue(
                            TransactionProcess.TransactionStatus(
                                true
                            )
                        )
                    }

                    PosEmvErrorCode.EMV_DECLINED -> {//this error will return either offline declined or online declined
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_DECLINED")
                        Log.d(tag, "PosEmvErrorCode.EMV_DECLINED")

                        // decline
                        printTags()
                        transData.transactionStatus = false
                        transactionStatus.postValue(
                            TransactionProcess.TransactionStatus(
                                false
                            )
                        )

                    }


                    PosEmvErrorCode.EMV_CANCEL -> {
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_CANCEL")
                        Log.d(tag, "oPosEmvErrorCode.EMV_CANCEL")
                        transactionStatus.postValue(
                            TransactionProcess.EMVError(
                                context.getString(R.string.err_user_cancel)
                            )
                        )
                    }

                    PosEmvErrorCode.EMV_TERMINATED -> {
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_TERMINATED")
                        Log.d(tag, "PosEmvErrorCode.EMV_TERMINATED")
                        if (isPinQuit) {
                            Log.d(tag, "is pinquit")
                            transactionStatus.postValue(
                                TransactionProcess.EMVError(
                                    context.getString(R.string.pin_quit_msg)
                                )
                            )
                        } else if (isPinTimeout) {
                            transactionStatus.postValue(
                                TransactionProcess.EMVError(context.getString(R.string.pin_timeout))
                            )
                        } else {
                            TransactionProcess.EMVError(
                                context.getString(R.string.emv_terminated)


                            )
                           CardReadActivity.cardread.finish()
                            Log.d(tag, "PosEmvErrorCode.elseeeeeeeeeeeeee")

                        }
                    }

                    PosEmvErrorCode.EMV_OTHER_INTERFACE -> {
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_OTHER_INTERFACE")
                        transactionStatus.postValue(TransactionProcess.EMVError(context.getString(R.string.emv_other_interface)))
                    }

                    PosEmvErrorCode.EMV_MULTI_CONTACTLESS -> {
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_MULTI_CONTACTLESS")
                        transactionStatus.postValue(TransactionProcess.EMVError(context.getString(R.string.multiple_card_found)))
                    }


                    PosEmvErrorCode.EMV_ENCRYPT_ERROR, PosEmvErrorCode.EMV_NOT_ACCEPTED, PosEmvErrorCode.EMV_NOT_ALLOWED, PosEmvErrorCode.EMV_OTHER_ERROR, PosEmvErrorCode.APPLE_VAS_UNTREATED -> {
                        FirebaseDatabaseSingleton.setLog("EMVError")
                        Log.d(tag, "EMVError")

                        transactionStatus.postValue(TransactionProcess.EMVError("Error Code : $result"))
                    }

                    PosEmvErrorCode.EMV_APP_BLOCKED -> {
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_APP_BLOCKED")
                        transactionStatus.postValue(
                            TransactionProcess.EMVError(
                                context.getString(R.string.no_active_application_found)
                            )
                        )
                    }

                    PosEmvErrorCode.EMV_UNENCRYPTED, PosEmvErrorCode.PARAMETER_ERROR, PosEmvErrorCode.EXCEPTION_ERROR -> {
                        FirebaseDatabaseSingleton.setLog("EMV_UNENCRYPTED, EXCEPTION_ERROR")
                        Log.d(tag, "EMV_UNENCRYPTED, EXCEPTION_ERROR")
                        transactionStatus.postValue(TransactionProcess.EMVError("Error Code : $result"))
                    }

                    PosEmvErrorCode.EMV_GPO_6985 -> {
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_GPO_6985")
                        transactionStatus.postValue(TransactionProcess.EMVError(context.getString(R.string.card_scheme_not_found)))
                    }

                    PosEmvErrorCode.EMV_READ_RECORD_ERROR -> {
                        FirebaseDatabaseSingleton.setLog("PosEmvErrorCode.EMV_READ_RECORD_ERROR")
                        transactionStatus.postValue(
                            TransactionProcess.CardRetry(
                                context.getString(R.string.card_read_error_eng)
                            )
                        )
                    }

                    else -> {
                        FirebaseDatabaseSingleton.setLog("EMV Error")
                        transactionStatus.postValue(TransactionProcess.EMVError("EMV Error : $result"))
                    }
                }
            }
        }

        private fun processReadRecordFilterEmvAndCtls(bundle: Bundle) {
            val data = bundle.getByteArray(POIEmvCoreManager.EmvCardInfoConstraints.DATA)
            data?.let {
                parseTLVAndStore(data)
            }
            val applicationLabelName = tagValueMap["50"]?.decodeHex() ?: ""
            transData.cardLabelNameEng = applicationLabelName
            if (entryMode == EntryMode.DIPPED || (isCardReadSuccess && transData.cardLabelNameEng.isNotEmpty())) {
                transactionStatus.postValue(
                    TransactionProcess.CardReadSuccess(
                        transData.cardLabelNameEng, entryMode
                    )
                )
            }
            bundle.putByteArray(POIEmvCoreManager.EmvCardInfoConstraints.OUT_TLV, ByteArray(0))
            emvCoreManager.onSetCardInfoResponse(bundle)


        }

    }

    private fun parseTLV(data: ByteArray): HashMap<String, String> {
        val tlvParser = BerTlvParser()
        val tlvs: BerTlvs = tlvParser.parse(data)
        val tagValueMap = hashMapOf<String, String>()
        for (tlv in tlvs.list) {
            tagValueMap[tlv.tag.berTagHex] = tlv.hexValue
        }
        return tagValueMap
    }

    private fun parseTLVAndStore(data: ByteArray) {
        val tlvParser = BerTlvParser()
        val tlvs: BerTlvs = tlvParser.parse(data)
        for (tlv in tlvs.list) {
            tagValueMap[tlv.tag.berTagHex] = tlv.hexValue

        }
    }

    fun applicationSelected(selectedIndex: Int) {
        FirebaseDatabaseSingleton.setLog("applicationSelected - $selectedIndex")
        emvCoreManager.onSetSelectResponse(selectedIndex)
    }

    private fun onTransEnd(message: String) {
        FirebaseDatabaseSingleton.setLog("onTransEnd - $message")
        transactionStatus.postValue(TransactionProcess.EMVError(message))
    }


    fun getCardEntryMode(): EntryMode? {
        return if (::entryMode.isInitialized) entryMode else null
    }

    private fun processOnlineResult(data: String): Bundle? {
        FirebaseDatabaseSingleton.setLog("processOnlineResult - $data")
        Log.d("CardReader", "onClick - printReceipt")

        ///transData.unpackResponseFields(data)
        val bundle = Bundle()
        val tlvBuilder = BerTlvBuilder()
        var authRespCode1: String? = null
        var authCode: String? = null
        var script: String? = null
        val tlvParser = BerTlvParser()
        val tlvs: List<BerTlv> = tlvParser.parse(HexUtil.parseHex(TransData.ResponseFields.Field55)).list
        for (tlv in tlvs) {
            when (tlv.tag.berTagHex) {
                "8A" -> authRespCode1 = tlv.hexValue
                "91" -> authCode = tlv.hexValue
                "71", "72" -> tlvBuilder.addBerTlv(tlv)
                else -> {}
            }
            Log.d(tag, "authcode:$authCode")
        }
        val authRespCode=data
        //if (tlvBuilder.build() !== 0) {
        script = HexUtil.toHexString(tlvBuilder.buildArray())
        // }

        when (authRespCode) {
            "00" -> {
                Log.d("TAG","field04444444444 ${TransData.ResponseFields.Field04}")
                val txntype = sharedPreferences.getString("TXN_TYPE","")
                if (txntype != null) {
                    dbhandler.registerTxnData(
                        txntype,
                        "",
                        "",
                        TransData.RequestFields.Field02,
                        "",
                        TransData.ResponseFields.Field04,
                        TransData.ResponseFields.Field11,
                        TransData.ResponseFields.Field12,
                        TransData.ResponseFields.Field13,
                        TransData.RequestFields.Field14,"","","","",TransData.ResponseFields.Field37,TransData.ResponseFields.Field38,TransData.ResponseFields.Field39,"","","","",TransData.RequestFields.Field60
                    )
                    dbhandler.registerTxnData2(
                        txntype,
                        "",
                        "",
                        TransData.RequestFields.Field02,
                        "",
                        TransData.ResponseFields.Field04,
                        TransData.ResponseFields.Field11,
                        TransData.ResponseFields.Field12,
                        TransData.ResponseFields.Field13,
                        TransData.RequestFields.Field14,"","","","",TransData.ResponseFields.Field37,TransData.ResponseFields.Field38,TransData.ResponseFields.Field39,"","","","",TransData.RequestFields.Field60
                    )
                }
                bundle.putInt(
                    EmvOnlineConstraints.OUT_AUTH_RESP_CODE,
                    EmvOnlineConstraints.EMV_ONLINE_APPROVE
                )
                bundle.putByteArray(
                    EmvOnlineConstraints.OUT_SPECIAL_AUTH_RESP_CODE, HexUtil.parseHex("3030")
                )
            }

            "01" -> {
                bundle.putInt(
                    EmvOnlineConstraints.OUT_AUTH_RESP_CODE,
                    EmvOnlineConstraints.EMV_ONLINE_REFER_TO_CARD_ISSUER
                )
                bundle.putByteArray(
                    EmvOnlineConstraints.OUT_SPECIAL_AUTH_RESP_CODE, HexUtil.parseHex("3031")
                )
            }

            "02" -> {
                bundle.putInt(
                    EmvOnlineConstraints.OUT_AUTH_RESP_CODE,
                    EmvOnlineConstraints.EMV_ONLINE_DENIAL
                )
                bundle.putByteArray(
                    EmvOnlineConstraints.OUT_SPECIAL_AUTH_RESP_CODE, HexUtil.parseHex("3032")
                )
            }


            "55" -> {
                bundle.putInt(
                    EmvOnlineConstraints.OUT_AUTH_RESP_CODE,
                    EmvOnlineConstraints.EMV_ONLINE_FAIL
                )
                bundle.putByteArray(
                    EmvOnlineConstraints.OUT_SPECIAL_AUTH_RESP_CODE, HexUtil.parseHex("3535")
                )
            }
            "91" -> {
                bundle.putInt(
                    EmvOnlineConstraints.OUT_AUTH_RESP_CODE,
                    EmvOnlineConstraints.EMV_ONLINE_DENIAL
                )
                bundle.putByteArray(
                    EmvOnlineConstraints.OUT_SPECIAL_AUTH_RESP_CODE, HexUtil.parseHex("3931")
                )
            }
            "96" -> {
                bundle.putInt(
                    EmvOnlineConstraints.OUT_AUTH_RESP_CODE,
                    EmvOnlineConstraints.EMV_ONLINE_DENIAL
                )
                bundle.putByteArray(
                    EmvOnlineConstraints.OUT_SPECIAL_AUTH_RESP_CODE, HexUtil.parseHex("3936")
                )
            }

            else -> {
                bundle.putInt(
                    EmvOnlineConstraints.OUT_AUTH_RESP_CODE, EmvOnlineConstraints.EMV_ONLINE_FAIL
                )
            }
        }
        if (authCode != null) {
            bundle.putByteArray(EmvOnlineConstraints.OUT_AUTH_DATA, HexUtil.parseHex(authCode))
        }
        if (script != null) {
            bundle.putByteArray(EmvOnlineConstraints.OUT_ISSUER_SCRIPT, HexUtil.parseHex(script))
        }
        return bundle
    }

    fun stopTransaction() {
        FirebaseDatabaseSingleton.setLog("stopTransaction")
        emvCoreManager.stopTransaction()
    }

    private fun printTags() {
        tagValueMap.forEach {
            Log.d(tag, "Tag : ${it.key} Value : ${it.value}")

            Log.d(tag, "Tag : ${it.key} Value : ${it.value}")
            if(it.key.equals("57")){
                TransData.RequestFields.Field35=it.value
            }
            if(it.key.equals("95")){
                transData.tvr=it.value
            }
            if(it.key.equals("99")){
                transData.isOnlinePin=true
                Log.d("tag", "online pin entered is "+ it.value)

                TransData.RequestFields.Field52=it.value
              //  if(transData.isOnlinePin) {
                    //CardReadActivity.cardread.showProgressDialog.show()
                //}
            }
            if(it.key.equals("9F02")){
                if (!sharedPreferences.getString("TXN_TYPE","").equals(Txntype.reversal)){
                    TransData.RequestFields.Field04=it.value

                }
            }
        }
        val total = StringBuilder()
        lateinit var value:String
        val tagList = arrayListOf("82", "84", "95","9A","9C","5F2A","5F34","9F02","9F09","9F10","9F1A","9F1E","9F26","9F27","9F33","9F34","9F35","9F36","9F37","9F41")
        val tagListCTLS = arrayListOf("82", "84", "95","9A","9C","5F2A","5F34","9F02","9F09","9F10","9F1A","9F1E","9F26","9F27","9F33","9F34","9F35","9F36","9F37","9F41","9F6E","9F7C")
        if(entryMode.equals(EntryMode.CONTACTLESS)) {

             for(tags in tagListCTLS) {
                 var check: Int = 0

                 Log.d(tag, "check$check")

                     if (tagValueMap[tags].toString() == "null") {
                         if (tags == "95") {
                             value = "0000000000"
                             check = 1
                         } else if (tags.equals("9F34")) {
                             value = "000000"
                             check = 1
                         } else if (tags == "9F7C") {
                             value = "00000"
                             check = 1
                         }
                     } else {
                         value = tagValueMap[tags].toString()

                     }

                     Log.d(tag, "value$value")
                     total.append(tags).append(HexUtil.dec2Hex((value.length) / 2)).append(value)


             }
        }
            else if (entryMode == EntryMode.DIPPED){
            for(tags in tagList){
                value= tagValueMap[tags].toString()
                if(tagValueMap["95"].toString() == "null"){
                    value="0000000000"
                }
                if(tagValueMap["9F34"].toString() == "null"){
                    value="000000"
                }


                total.append(tags).append(HexUtil.dec2Hex((value.length)/2)).append(value)
            }

        }
        //}



        Log.d(tag, "Field55Total:${total}")
        TransData.RequestFields.Field55 = total.toString()
    }

    fun setAmount(amount: Long) {
        FirebaseDatabaseSingleton.setLog("setAmount - $amount")
        if (!sharedPreferences.getString("TXN_TYPE","").equals(Txntype.reversal)){
            transData.amount = amount
        }

    }


    private fun getCardExpireDate(strTrack2: String?): String? {
        if (strTrack2.isNullOrEmpty()) return ""
        var index = strTrack2.indexOf('=')
        if (index < 0) {
            index = strTrack2.indexOf('D')
            if (index < 0) return null
        }
        return if (index + 5 > strTrack2.length) null else strTrack2.substring(index + 1, index + 5)
    }

    private fun getPan(track2: String?): String? {
        if (track2 == null) return null
        var len = track2.indexOf('=')
        if (len < 0) {
            len = track2.indexOf('D')
            if (len < 0) return null
        }
        return if (len < 13 || len > 19) null else track2.substring(0, len)
    }

    fun printReceipt() {
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
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.hb_logo1)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true)
        printerManager.addPrintLine(BitmapPrintLine(resizedBitmap, PrintLine.CENTER))

        printerManager.addPrintLine(
            TextPrintLine(
                "Hibret Bank", PrintLine.CENTER, 20, false
            )
        )
        printerManager.addPrintLine(
            TextPrintLine(
                dbhandler.getMerchantAddress(),//CANARY CENTER, 7304 PRINCE ABDULAZIZ IBN MUSAID AS SULIMANIYAH DISTRICT 12243 OFF#112",
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
                " TID :${dbhandler.getTID()}", "", "MID : ${dbhandler.getMID()}", 16, false
            )
        )

        printerManager.addPrintLine(
            printList(
                "DATE : ${
                    convertDateTime(
                        transData.transactionReqDateTime,
                        DateTimeFormat.DATE_TIME_PATTERN_TRANS_ONE,
                        DateTimeFormat.DATE_PATTERN_DISPLAY_ONE
                    )
                }", "", "TIME : ${
                    convertDateTime(
                        transData.transactionReqDateTime,
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
                sharedPreferences.getString("TXN_TYPE","")?.uppercase() ?: "", PrintLine.LEFT, 20, true
            )
        )

        printerManager.addPrintLine(
            TextPrintLine(
                "${transData.cardLabelNameEng}(${transData.entryMode})", PrintLine.LEFT, 16, false
            )
        )
        printerManager.addPrintLine(
            TextPrintLine(
                maskPan(maskPan(transData.pan.substringBefore("F"))), PrintLine.LEFT, 20, true
            )
        )

        printerManager.addPrintLine(
            TextPrintLine(
                "EXPIRY DATE : ${formatExpiryDate(transData.cardExpiryDate)}",
                PrintLine.LEFT,
                20,
                true
            )
        )


        printerManager.addPrintLine(
            printList(
                "RRN : ${TransData.ResponseFields.Field37}", "", "RECEIPT No : ${transData.stan}", 16, false
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
            printerManager.addPrintLine(
                TextPrintLine(
                    "AID : ${transData.aid}", PrintLine.LEFT, 16, false
                )
            )

            printerManager.addPrintLine(
                printList(
                    "AC : ${transData.applicationCryptogram}",
                    "",
                    "CID : ${transData.genACResult}",
                    16,
                    false
                )
            )
        }
        printerManager.addPrintLine(
            TextPrintLine(
                "TVR : ${transData.tvr}", PrintLine.LEFT, 16, false
            )
        )
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
                "APP VERSION : ${BuildConfig.VERSION_NAME}", PrintLine.CENTER, 16, true
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

    private fun verificationMethodUsedForCurrentEMVTransaction(dcTagByte3: Byte) {
        FirebaseDatabaseSingleton.setLog("verificationMethodUsedForCurrentEMVTransaction - $dcTagByte3")
        transData.verificationMethod = when {
            isBitSet(dcTagByte3, 8) && isBitSet(
                dcTagByte3, 7
            ) -> 3 //"CARDHOLDER PIN VERIFIED+ " // This is because in swipe for TMS 1 means PIN and 3 means PIN+sig so matching for that else need to map there
            isBitSet(dcTagByte3, 8) -> 2 //"CARDHOLDER VERIFIED BY SIGNATURE"
            isBitSet(dcTagByte3, 7) -> 1 //"CARDHOLDER PIN VERIFIED "
            isBitSet(dcTagByte3, 6) -> 4 //"Encipher PIN "
            isBitSet(dcTagByte3, 5) -> 5 //"Plaintext PIN "
            isBitSet(dcTagByte3, 4) -> 6 //"DEVICE OWNER IDENTITY VERIFIED "
            isBitSet(dcTagByte3, 1) -> 0 // "NO CVM"
            else -> 0
        }
    }


    private fun isBitSet(byte: Byte, bit: Int): Boolean {
        return (byte.toInt() ushr (bit - 1) and 0x1) == 1
    }

    private fun getVerificationMethod(type: Int): String {
        FirebaseDatabaseSingleton.setLog("getVerificationMethod - $type")
        return when (type) {
            1, 4, 5 -> {
                context.getString(R.string.cardholder_pin_verified_eng)
            }

            2 -> {
                if (transData.entryMode == EntryMode.CONTACTLESS.toString()) {
                    context.getString(R.string.no_verification_eng)
                } else {
                    context.getString(R.string.card_holder_verified_signature_eng)
                }
            }

            3 -> {
                context.getString(R.string.cardholder_pin_plus_verified_eng)
            }

            6 -> {
                context.getString(R.string.device_owner_verification_msg_eng)
            }

            else -> {
                context.getString(R.string.no_verification_eng)
            }

        }
    }

    private suspend fun setTransData(){
        transData.rrn = generateRRN()
        transData.transactionReqDateTime=getCurrentDateTime(DateTimeFormat.DATE_TIME_PATTERN_TRANS_ONE)
        transData.stan = getSTAN(context)
        if (tagValueMap.containsKey(PAN_NUMBER)) {
            transData.pan = tagValueMap[PAN_NUMBER] ?: ""

        } else if (tagValueMap.containsKey(TRACK2) && transData.pan.isEmpty()) {
            transData.pan = getPan(tagValueMap[TRACK2]) ?: ""

        }

        if (tagValueMap.containsKey(EXP_DATE)) {
            transData.cardExpiryDate = tagValueMap[EXP_DATE] ?: ""
        } else if (tagValueMap.containsKey(TRACK2) && transData.cardExpiryDate.isEmpty()) {
            transData.cardExpiryDate = getCardExpireDate(tagValueMap[TRACK2]) ?: ""
            transData.cardExpiryDate=transData.cardExpiryDate.substringBefore("31")
        }

        if (tagValueMap.containsKey(CARD_AID)) {
            transData.aid = tagValueMap[CARD_AID] ?: ""
        }
    }

}
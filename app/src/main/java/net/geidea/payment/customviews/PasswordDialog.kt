package net.geidea.payment.customviews

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import net.geidea.payment.listener.PinPadListener
import com.pos.sdk.emvcore.POIEmvCoreManager
import com.pos.sdk.emvcore.POIEmvCoreManager.EmvPinConstraints
import com.pos.sdk.security.POIHsmManage
import com.pos.sdk.security.POIHsmManage.PED_PINBLOCK_DUKPT_FMT_ISO9564_0
import com.pos.sdk.security.PedRsaPinKey
import net.geidea.payment.R
import net.geidea.payment.tlv.HexUtil
import net.geidea.payment.transaction.view.CardReadActivity
import net.geidea.payment.utils.pinBlockType
import net.geidea.utils.BuzzerUtils.playClickSound
import net.geidea.utils.CurrencyConverter
import net.geidea.utils.LogUtils
import java.nio.ByteBuffer
import java.util.Arrays

class PasswordDialog(
    context: Activity,
    engTitle: String,
    isIcSlot: Boolean,
    bundle: Bundle,
    keyIndex: Int,
    amount: Long,
    var retryCount: Int,
    pinPadListener: PinPadListener?,

    ) {
    private val DEFAULT_EXP_PIN_LEN_IND = "4,5,6,7,8,9,10,11,12"
    private val DEFAULT_TIMEOUT_MS = 60000
    private val keyIndex: Int

    private var keyMode: Int = -1
    private var icSlot = 0
    private val isKeyboardFix = true
    private var isEncrypt = false
    private var pinCard: String? = null
    private var pinType = 0
    private var pinBypass = false
    private var pinCounter = 0
    private var pinRandom: ByteArray? = null
    private var pinModule: ByteArray? = null
    private var pinExponent: ByteArray? = null
    private val hsmManage: POIHsmManage
    private val pinEventListener: PinEventListener
    private val dialog: Dialog
    private val tvMessage: TextView
    private val etPin: EditText
    private val btnConfirm: AppCompatButton
    private val btnClear: AppCompatImageButton
    private val btnEsc: AppCompatImageButton
    private val btn0: TextView
    private val btn1: TextView
    private val btn2: TextView
    private val btn3: TextView
    private val btn4: TextView
    private val btn5: TextView
    private val btn6: TextView
    private val btn7: TextView
    private val btn8: TextView
    private val btn9: TextView
    private var pinPadTimer: CountDownTimer? = null
    private val pinPadListener: PinPadListener?
    private val context: Context
    private var isPinTimeout = false
    private lateinit var sharedPreferences: SharedPreferences

    init {
        hsmManage = POIHsmManage.getDefault()
        this.context = context
        this.pinPadListener = pinPadListener
        pinEventListener = PinEventListener()
        icSlot = if (isIcSlot) {
            0
        } else {
            10
        }
        this.keyIndex = keyIndex
        if(pinBlockType == "dukpt")
            keyMode = POIHsmManage.PED_PINBLOCK_FETCH_MODE_DUKPT
        else if(pinBlockType == "mksk")
            keyMode = POIHsmManage.PED_PINBLOCK_FETCH_MODE_TPK

        when (bundle.getInt(EmvPinConstraints.PIN_TYPE, -1)) {
            POIEmvCoreManager.PIN_PLAIN_PIN -> pinType = PLAIN_PIN
            POIEmvCoreManager.PIN_ONLINE_PIN -> {pinType = ONLINE_PIN
                                            CardReadActivity.cardread.showProgressDialog.show()
                                                                   }
            POIEmvCoreManager.PIN_ENCIPHER_PIN -> pinType = ENCIPHER_PIN
            else -> {
                //Do nothing
            }
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_ENCRYPT)) {
            isEncrypt = bundle.getBoolean(EmvPinConstraints.PIN_ENCRYPT)
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_CARD)) {
            pinCard = bundle.getString(EmvPinConstraints.PIN_CARD)
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_BYPASS)) {
            pinBypass = bundle.getBoolean(EmvPinConstraints.PIN_BYPASS)
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_COUNTER)) {
            pinCounter = bundle.getInt(EmvPinConstraints.PIN_COUNTER)
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_CARD_RANDOM)) {
            pinRandom = bundle.getByteArray(EmvPinConstraints.PIN_CARD_RANDOM)
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_MODULE)) {
            pinModule = bundle.getByteArray(EmvPinConstraints.PIN_MODULE)
        }
        if (bundle.containsKey(EmvPinConstraints.PIN_EXPONENT)) {
            pinExponent = bundle.getByteArray(EmvPinConstraints.PIN_EXPONENT)
        }
        LogUtils.e("Geidea", "Pintype in dialog $pinType")
        LogUtils.e("Geidea", "Pin counter $pinCounter")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_password, null) as ConstraintLayout
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        tvMessage = view.findViewById(R.id.tvMessage)
        etPin = view.findViewById(R.id.etPin)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        btnClear = view.findViewById(R.id.btnClear)
        btnEsc = view.findViewById(R.id.btnEsc)
        btn0 = view.findViewById(R.id.btn0)
        btn1 = view.findViewById(R.id.btn1)
        btn2 = view.findViewById(R.id.btn2)
        btn3 = view.findViewById(R.id.btn3)
        btn4 = view.findViewById(R.id.btn4)
        btn5 = view.findViewById(R.id.btn5)
        btn6 = view.findViewById(R.id.btn6)
        btn7 = view.findViewById(R.id.btn7)
        btn8 = view.findViewById(R.id.btn8)
        btn9 = view.findViewById(R.id.btn9)
//        btnConfirm.setOnClickListener {
//
//            CardReadActivity.cardread.showProgressDialog.show()
//        }
        sharedPreferences=context.getSharedPreferences("SHARED_DATA",Context.MODE_PRIVATE)

        val totalAmountEng: TextView = view.findViewById(R.id.total_amount_eng)
        val headerInEng: AppCompatTextView = view.findViewById(R.id.eng_title)


        val totalAmountEngTitle: TextView = view.findViewById(R.id.total_amount_txt_eng)


        totalAmountEngTitle.text = context.getString(R.string.total_amount)


        headerInEng.text = engTitle


        totalAmountEng.text =sharedPreferences.getString("Currency","")+CurrencyConverter.convertWithoutSAR(amount)



        tvTitle.text =
            context.getString(R.string.enter_pin)

        dialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        val window = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        window.attributes = wlp
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        window.setGravity(Gravity.BOTTOM)
        dialog.show()
        pinPadListener?.pinPadDisplayed()
    }

    fun showDialog(): Int {
        val result: Int = when (pinType) {
            PLAIN_PIN -> {
                onVerifyPlainPin()
            }

            ONLINE_PIN -> onOnlinePin()
            ENCIPHER_PIN -> {
                onVerifyEncipherPin()
            }

            else -> -1
        }
        return result
    }

    fun closeDialog() {
        dialog.dismiss()
        if (pinPadTimer != null) {
            pinPadTimer!!.cancel()
        }
        hsmManage.unregisterListener(pinEventListener)
    }

    private fun onVerifyPlainPin(): Int {
        hsmManage.registerListener(pinEventListener)
        return hsmManage.PedVerifyPlainPin(icSlot, 0, DEFAULT_TIMEOUT_MS, DEFAULT_EXP_PIN_LEN_IND)
    }

    private fun onVerifyEncipherPin(): Int {
        hsmManage.registerListener(pinEventListener)
        if (pinModule == null) {
            return -1
        }
        val module = ByteArray(pinModule!!.size)
        val exponent = ByteArray(pinExponent!!.size)
        val random = ByteArray(pinRandom!!.size)
        System.arraycopy(pinModule, 0, module, 0, pinModule!!.size)
        System.arraycopy(pinExponent, 0, exponent, 0, pinExponent!!.size)
        System.arraycopy(pinRandom, 0, random, 0, pinRandom!!.size)
        val rsaPinKey = PedRsaPinKey(module, exponent, random)
        return hsmManage.PedVerifyCipherPin(
            icSlot,
            0,
            DEFAULT_TIMEOUT_MS,
            DEFAULT_EXP_PIN_LEN_IND,
            rsaPinKey
        )
    }

    private fun onOnlinePin(): Int {
        hsmManage.registerListener(pinEventListener)
        val data = ByteArray(24)
        if (!isEncrypt) {
            val temp = CalcPinBlock.calcPinBlock(pinCard).toByteArray()
            System.arraycopy(temp, 0, data, 0, 16)
        } else {
            val temp = pinCard!!.toByteArray()
            System.arraycopy(temp, 0, data, 0, 16)
        }
        val formatData = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
        System.arraycopy(formatData, 0, data, 16, 8)

        // 0x20 need to pass to not auto increment while PIN. Send 0 to increase KSN in PIN.
        return hsmManage.PedGetPinBlock(
            keyMode,
            keyIndex,
            PED_PINBLOCK_DUKPT_FMT_ISO9564_0,
            DEFAULT_TIMEOUT_MS,
            data,
            DEFAULT_EXP_PIN_LEN_IND
        )
    }

    private fun parsePinRespBuffer(rspBuf: ByteArray): Pair<Int, Int> {
        val sw1 = if (rspBuf[1] >= 0) rspBuf[1].toInt() else rspBuf[1] + 256
        val sw2 = if (rspBuf[2] >= 0) rspBuf[2].toInt() else rspBuf[2] + 256
        return Pair(sw1, sw2)
    }

    private inner class PinEventListener : POIHsmManage.EventListener {
        private val TAG = "PinEventListener"
        override fun onPedVerifyPin(manage: POIHsmManage, type: Int, rspBuf: ByteArray) {
            LogUtils.e("Geidea", "onPedVerifyPin")
            if (type == POIHsmManage.PED_VERIFY_PIN_TYPE_PLAIN || type == POIHsmManage.PED_VERIFY_PIN_TYPE_CIPHER) {
                LogUtils.e("Geidea", "Here 0")

                val parseResult = parsePinRespBuffer(rspBuf)
                val sw1 = parseResult.first
                val sw2 = parseResult.second

                when {
                    sw1 == 0x90 && sw2 == 0x00 -> {
                        LogUtils.e("Geidea", "offline pin verified")
                        onPinSuccess(null, null)
                        pinPadListener?.offlinePinVerified()
                    }

                    sw1 == 0x63 && (sw2 and 0xc0) == 0xc0.toInt() -> {
                        handleError(sw2)
                    }

                    sw1 == 0x69 && (sw2 == 0x83.toInt() || sw2 == 0x84.toInt()) -> {
                        LogUtils.d("Geidea", "Here 1")
                        onPinError(EmvPinConstraints.VERIFY_PIN_BLOCK, 0)
                    }

                    else -> {
                        LogUtils.d("Geidea", "Here 3")
                        onPinError(EmvPinConstraints.VERIFY_NO_PASSWORD, 0)
                    }
                }

            } else {
                LogUtils.d("Geidea", "Here 3")
                onPinError(EmvPinConstraints.VERIFY_NO_PASSWORD, 0)
            }
            LogUtils.e("Geidea", "Here out")
            closeDialog()
        }

        override fun onPedPinBlockRet(manage: POIHsmManage, type: Int, rspBuf: ByteArray) {
            if (rspBuf[0].toInt() != 0) {
                val pinBlock = ByteArray(rspBuf[0].toInt())
                System.arraycopy(rspBuf, 1, pinBlock, 0, rspBuf[0].toInt())
                if (rspBuf.size > rspBuf[0] + 1) {
                    val ksn = ByteArray(
                        rspBuf[rspBuf[0] + 1]
                            .toInt()
                    )
                    System.arraycopy(rspBuf, rspBuf[0] + 2, ksn, 0, rspBuf[rspBuf[0] + 1].toInt())
                    onPinSuccess(pinBlock, ksn)
                } else {
                    onPinSuccess(pinBlock, null)
                }
            }
            closeDialog()
        }

        override fun onKeyboardShow(manage: POIHsmManage, keys: ByteArray, timeout: Int) {
            LogUtils.d("PasswordDialog", Arrays.toString(keys))
            if (isKeyboardFix) {
                val fix = ByteArray(keys.size)
                val random = ByteArray(keys.size)
                System.arraycopy(keys, 0, random, 0, keys.size)
                System.arraycopy(keys, 0, fix, 0, keys.size)
                fix[0] = 0x31
                fix[1] = 0x32
                fix[2] = 0x33
                fix[3] = 0x34
                fix[4] = 0x35
                fix[5] = 0x36
                fix[6] = 0x37
                fix[7] = 0x38
                fix[8] = 0x39
                fix[10] = 0x30
                val keyLayout = calculation(fix)
                LogUtils.d("PasswordDialog", Arrays.toString(keyLayout))
                val randomKeyLayout = ByteArray(keyLayout.size)
                switchFixToRandom(keyLayout, random, randomKeyLayout)
                LogUtils.d("PasswordDialog", Arrays.toString(randomKeyLayout))
                hsmManage.PedSetKeyLayout(randomKeyLayout, 0)
            } else {
                LogUtils.d("PasswordDialog", Arrays.toString(calculation(keys)))
                hsmManage.PedSetKeyLayout(calculation(keys), 0)
            }
            if (pinPadTimer != null) {
                pinPadTimer!!.cancel()
            }
            LogUtils.d("Geidea", "Pin pad timer started")
        }

        override fun onKeyboardInput(manage: POIHsmManage, numKeys: Int) {
            LogUtils.d("Geidea", "Input Key $numKeys")
            playClickSound()
            LogUtils.d("Geidea", "After Sound")
            var numKeys = numKeys
            val info = StringBuilder()
            while (0 != numKeys--) {
                info.append("*")
            }
            LogUtils.d("Geidea", "StringBuilder value ${info.toString()}")
            if (info.length <= 12) {
                etPin.setText(info.toString())
            }

        }

        override fun onInfo(manage: POIHsmManage, what: Int, extra: Int) {
            LogUtils.e(TAG, "onInfo")
        }

        override fun onError(manage: POIHsmManage, what: Int, extra: Int) {
            playClickSound()
            LogUtils.e(TAG, "onError:$extra")
            when (extra) {
                0xFFFF, 0xFFFD -> {
                    LogUtils.e(TAG, "onError:1")
                    closeDialog()
                    if (isPinTimeout) {
                        pinPadListener?.onPinTimeout()
                    } else {
                        pinPadListener?.onPinQuit()
                    }
                    isPinTimeout = false
                    onPinError(EmvPinConstraints.VERIFY_CANCELED, 0)
                    return
                }

                0xFFFC -> {
                    LogUtils.e(TAG, "onError:2")
                    tvMessage.text = "The terminal triggers a security check."
                    tvMessage.visibility = View.VISIBLE

                }

                0xFED3 -> {
                    LogUtils.e(TAG, "onError:3")
                    tvMessage.text = "The terminal did not write the PIN key. Please check."
                    tvMessage.visibility = View.VISIBLE
                }

                0XFECF -> {
                    if (pinBypass) {
                        LogUtils.e(TAG, "onError:4")
                        LogUtils.d("Geidea", "Here 8")
                        onPinError(EmvPinConstraints.VERIFY_NO_PASSWORD, 0)
                    } else {
                        LogUtils.e(TAG, "onError:5")
                        LogUtils.d("Geidea", "Here 9")
                        onPinError(EmvPinConstraints.VERIFY_ERROR, 0)
                    }
                    closeDialog()
                    return
                }

                else -> {
                    LogUtils.d("Geidea", "Here 10")
                    onPinError(EmvPinConstraints.VERIFY_NO_PASSWORD, 0)
                    closeDialog()
                    return
                }
            }
            Handler(Looper.getMainLooper()).postDelayed({
                LogUtils.d("Geidea", "Here 4")
                onPinError(EmvPinConstraints.VERIFY_CANCELED, 0)
                closeDialog()
            }, 2000)
        }

        override fun onHwSelfCheckRet(manage: POIHsmManage, type: Int, checkResult: Int) {
            LogUtils.e(TAG, "onHwSelfCheckRet")
        }

        override fun onHwSensorTriggered(
            manage: POIHsmManage,
            triggered: Int,
            sensorValue: ByteArray,
            triggerTime: ByteArray,
        ) {
            LogUtils.e(TAG, "onHwSensorTriggered")
        }

        override fun onPedKeyManageRet(manage: POIHsmManage, ret: Int) {
            LogUtils.e(TAG, "onPedKeyManageRet")
        }
    }

    private fun handleError(sw2: Int) {
        if ((sw2 and 0x0F) == 0) {
            LogUtils.d("Farman", "Here 5")
            pinPadListener?.pinRetry(pendingCount = pinCounter, retryCount = retryCount, pinType)
            onPinError(EmvPinConstraints.VERIFY_PIN_BLOCK, 0)
        } else {
            LogUtils.d("Farman", "Pin Retry count ${sw2 and 0x0F}")
            pinPadListener?.pinRetry(pendingCount = pinCounter, retryCount = retryCount, pinType)
            onPinError(EmvPinConstraints.VERIFY_ERROR, sw2 and 0x0F)
        }
    }

    private fun onPinSuccess(pinBlock: ByteArray?, pinKsn: ByteArray?) {
        val bundle = Bundle()
        bundle.putInt(EmvPinConstraints.OUT_PIN_VERIFY_RESULT, EmvPinConstraints.VERIFY_SUCCESS)
        bundle.putInt(EmvPinConstraints.OUT_PIN_TRY_COUNTER, 0)
        if (pinBlock != null) {
            LogUtils.e("Geidea", "Pin block : " + HexUtil.toHexString(pinBlock));
            bundle.putByteArray(EmvPinConstraints.OUT_PIN_BLOCK, pinBlock)
        }

        if (pinKsn != null) {
            LogUtils.e("Geidea", "KSN " + HexUtil.toHexString(pinKsn))
            if (pinBlock != null)
                pinPadListener?.on117Or196PinBlockSuccess(HexUtil.toHexString(pinBlock))
        }
        POIEmvCoreManager.getDefault().onSetPinResponse(bundle)
    }

    private fun onPinError(verifyResult: Int, pinTryCntOut: Int) {
        val bundle = Bundle()
        bundle.putInt(EmvPinConstraints.OUT_PIN_VERIFY_RESULT, verifyResult)
        bundle.putInt(EmvPinConstraints.OUT_PIN_TRY_COUNTER, pinTryCntOut)
        POIEmvCoreManager.getDefault().onSetPinResponse(bundle)
    }

    private fun calculation(keys: ByteArray): ByteArray {
        val map = HashMap<String, String>()
        val coordinate = ByteBuffer.allocate(104)
        val esc = "Esc"
        val enter = "Enter"
        val clear = "Clear"
        map["0"] = "0"
        map["1"] = "1"
        map["2"] = "2"
        map["3"] = "3"
        map["4"] = "4"
        map["5"] = "5"
        map["6"] = "6"
        map["7"] = "7"
        map["8"] = "8"
        map["9"] = "9"
        map["-21"] = esc
        map["-35"] = enter
        map["-40"] = clear
        val keyView = arrayOfNulls<TextView>(11)
        if (isKeyboardFix) {
            keyView[0] = btn1
            keyView[1] = btn2
            keyView[2] = btn3
            keyView[3] = btn4
            keyView[4] = btn5
            keyView[5] = btn6
            keyView[6] = btn7
            keyView[7] = btn8
            keyView[8] = btn9
            keyView[9] = btn0
        } else {
            keyView[0] = btn0
            keyView[1] = btn1
            keyView[2] = btn2
            keyView[3] = btn3
            keyView[4] = btn4
            keyView[5] = btn5
            keyView[6] = btn6
            keyView[7] = btn7
            keyView[8] = btn8
            keyView[9] = btn9
            // keyView[10] = btnEsc;
        }
        val ivClear = btnClear
        val btnConfirm = btnConfirm
        var viewIndex = 0
        for (i in 0..12) {
            val value = map[(keys[i] - 0x30).toString()]
            var tv: View?
            when (value) {
                null -> {
                    continue
                }

                enter -> {
                    tv = btnConfirm
                }

                clear -> {
                    tv = ivClear
                }

                else -> {
                    if (value == esc) {
                        tv = btnEsc
                    } else {
                        keyView[viewIndex]!!.text = value
                        tv = keyView[viewIndex++]
                    }
                }
            }
            val pos = ByteArray(8)
            val location = IntArray(2)
            tv!!.getLocationOnScreen(location)
            val leftX = location[0]
            val leftY = location[1]
            val rightX = location[0] + tv.width
            val rightY = location[1] + tv.height
            val tmp0 = intToBytes(leftX)
            val tmp1 = intToBytes(leftY)
            val tmp2 = intToBytes(rightX)
            val tmp3 = intToBytes(rightY)
            pos[0] = tmp0[2]
            pos[1] = tmp0[3]
            pos[2] = tmp1[2]
            pos[3] = tmp1[3]
            pos[4] = tmp2[2]
            pos[5] = tmp2[3]
            pos[6] = tmp3[2]
            pos[7] = tmp3[3]
            coordinate.put(pos)
        }
        return coordinate.array()
    }

    private fun switchFixToRandom(
        fixKeyLayout: ByteArray,
        random: ByteArray,
        randomKeyLayout: ByteArray,
    ) {
        var position: Int
        System.arraycopy(fixKeyLayout, 0, randomKeyLayout, 0, fixKeyLayout.size)
        for (i in random.indices) {
            if (i != 9 && i <= 10) {
                position = if (random[i] - 0x30 != 0) {
                    (random[i] - 0x30 - 1) * 8
                } else {
                    10 * 8
                }
                System.arraycopy(fixKeyLayout, position, randomKeyLayout, i * 8, 8)
            }
        }
    }

    private fun intToBytes(value: Int): ByteArray {
        return byteArrayOf(
            (value shr 24 and 255).toByte(),
            (value shr 16 and 255).toByte(),
            (value shr 8 and 255).toByte(),
            (value and 255).toByte()
        )
    }

    internal object CalcPinBlock {
        fun calcPinBlock(accountNumber: String?): String {
            return "0000" + extractAccountNumberPart(accountNumber)
        }

        private fun extractAccountNumberPart(accountNumber: String?): String? {
            var accountNumberPart = takeLastN(accountNumber, 13)
            accountNumberPart = takeFirstN(accountNumberPart, 12)
            return accountNumberPart
        }

        private fun takeLastN(str: String?, n: Int): String? {
            return if (str!!.length > n) {
                str.substring(str.length - n)
            } else {
                if (str.length < n) {
                    zero(str, n)
                } else {
                    str
                }
            }
        }

        private fun takeFirstN(str: String?, n: Int): String? {
            return if (str!!.length > n) {
                str.substring(0, n)
            } else {
                if (str.length < n) {
                    zero(str, n)
                } else {
                    str
                }
            }
        }

        private fun zero(str: String?, len: Int): String {
            var str = str
            str = str!!.trim { it <= ' ' }
            val builder = StringBuilder(len)
            var fill = len - str.length
            while (fill-- > 0) {
                builder.append(0.toChar())
            }
            builder.append(str)
            return builder.toString()
        }
    }


    companion object {
        const val PLAIN_PIN = 1
        const val ONLINE_PIN = 2
        const val ENCIPHER_PIN = 3
    }
}
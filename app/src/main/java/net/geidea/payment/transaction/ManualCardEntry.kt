package net.geidea.payment.transaction

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.pos.sdk.printer.POIPrinterManager
import com.pos.sdk.printer.models.BitmapPrintLine
import com.pos.sdk.printer.models.PrintLine
import com.pos.sdk.printer.models.TextPrintLine
import net.geidea.payment.BuildConfig
import net.geidea.payment.DBHandler
import net.geidea.payment.R
import net.geidea.payment.TxnType
import net.geidea.payment.com.Comm
import net.geidea.payment.databinding.ActivityManualCardEntryBinding
import net.geidea.payment.print.PrintStatus
import net.geidea.payment.print.Printer
import net.geidea.payment.print.Printer.printList
import net.geidea.payment.tlv.HexUtil
import net.geidea.payment.transaction.model.EntryMode
import net.geidea.payment.transaction.model.TransData
import net.geidea.payment.transaction.viewmodel.CardReadViewModel
import net.geidea.payment.users.supervisor.ExpiryDate
import net.geidea.payment.users.supervisor.SupervisorMainActivity
import net.geidea.payment.utils.FirebaseDatabaseSingleton
import net.geidea.utils.BuzzerUtils
import net.geidea.utils.CurrencyConverter
import net.geidea.utils.DateTimeFormat
import net.geidea.utils.convertDateTime
import net.geidea.utils.dialog.SweetAlertDialog
import net.geidea.utils.extension.gone
import net.geidea.utils.extension.visible
import net.geidea.utils.getCurrentDateTime
import net.geidea.utils.maskPan


class ManualCardEntry : AppCompatActivity() {

    private lateinit var binding: ActivityManualCardEntryBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor:SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualCardEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)






        sharedPreferences=getSharedPreferences("SHARED_DATA",Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()

        val txnType=sharedPreferences.getString("TXN_TYPE","")
        val amount=intent.getStringExtra("amount")

        binding.buttonPANconfirm.setOnClickListener() {
            val pan = binding.editTextPAN.text.toString()
            if(pan.isNotEmpty()){

            if (pan.length < 13 || pan.length > 19) {
                binding.editTextPAN.error = "Not Valid Number"
            } else {
                if (!isValidCardNumber(pan)) {
                    binding.editTextPAN.error = "Not Valid Number"

                } else {
                    val intent= Intent(this, ExpiryDate::class.java).apply {
                        putExtra("amount", amount)
                        putExtra("pan", pan)

                                }
                        startActivity(intent)
                        finish()



                }

            }


        }else{
                binding.editTextPAN.error = "Card number can't be empty"
        }


        }
    }

    fun isValidLength(cardNumber:String){
        val PAN = cardNumber
        if (PAN.equals(null)){
            Toast.makeText(this, "Please Enter PAN", Toast.LENGTH_SHORT).show()
        } else if (PAN.length < 13 || PAN.length >19 ){
            Toast.makeText(this, "Invalid PAN Length\nPAN must be between 13 and 19", Toast.LENGTH_SHORT).show()
//        } else if (!checkLuhn(PAN)){
//            Toast.makeText(this, "Invalid PAN", Toast.LENGTH_SHORT).show()
//        }
    }

//    fun checkLuhn( cardNo: String): Boolean {
//        var nDigits = cardNo.length;
//
//        var nSum = 0
//        var isSecond = false
//        var i: Int = 0
//        for ( i = nDigits - 1; i >= 0; i--){
//
//            var d = cardNo.charAt(i) - '0';
//
//            if (isSecond == true)
//                d = d * 2;
//
//            // We add two digits to handle
//            // cases that make two digits
//            // after doubling
//            nSum += d / 10;
//            nSum += d % 10;
//
//            isSecond = !isSecond;
//        }
//        return (nSum % 10 == 0)
//    }
     }
    private fun isValidCardNumber(cardNumber: String): Boolean {
        val reversedDigits = cardNumber.reversed().map { it.toString().toInt() }
        val checkSum = reversedDigits.mapIndexed { index, digit ->
            if (index % 2 == 0) digit
            else {
                val doubled = digit * 2
                if (doubled > 9) doubled - 9 else doubled
            }
        }.sum()
        return checkSum % 10 == 0
    }

}

package net.geidea.payment.transaction

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.databinding.ActivityManualCardEntryBinding
import net.geidea.payment.transaction.model.TransData
import net.geidea.payment.users.supervisor.ExpiryDate


class ManualCardEntry : AppCompatActivity() {

    private lateinit var binding: ActivityManualCardEntryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualCardEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val amount=intent.getStringExtra("amount")

        binding.buttonPANconfirm.setOnClickListener() {
            var pan = binding.editTextPAN.text.toString()
            if(pan.isNotEmpty()){

            if (pan.length < 13 || pan.length > 19) {
                binding.editTextPAN.setError("Not Valid Number")
            } else {
                if (!isValidCardNumber(pan)) {
                    binding.editTextPAN.setError("Not Valid Number")

                } else {
                    val intent = Intent(this, ExpiryDate::class.java).apply{
                        putExtra("amount", amount)
                        putExtra("pan", pan)
                    }

                    startActivity(intent)
                    finish()
                }

            }


        }else{
            binding.editTextPAN.setError("Card number can't be empty")
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

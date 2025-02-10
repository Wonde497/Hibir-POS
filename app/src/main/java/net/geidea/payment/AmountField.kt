package net.geidea.payment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.databinding.ActivityAmountFieldBinding

class AmountField : AppCompatActivity() {
    private lateinit var binding:ActivityAmountFieldBinding
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var transaction:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAmountFieldBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences=getSharedPreferences("SHARED_DATA",Context.MODE_PRIVATE)
        transaction=sharedPreferences.getString("TXN_TYPE","").toString()
        if(transaction==TxnType.PRE_AUTH_COMPLETION){
           binding.textView.text="Enter ${transaction} amount"
        }
       }
    }

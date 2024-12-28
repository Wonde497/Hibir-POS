package net.geidea.payment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.databinding.ActivityManualSaleReversalBinding
class ManualSaleReversal : AppCompatActivity() {
    private  lateinit var binding:ActivityManualSaleReversalBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityManualSaleReversalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()
        binding.manualSale.setOnClickListener{
            editor.putString("TXN_TYPE",Txntype.manualPurchase)
            editor.putString("transaction",Txntype.manualPurchase)
            editor.commit()
            startActivity(Intent(this,AmountActivity::class.java))
            finish()
        }
        binding.manualReversal.setOnClickListener{
            editor.putString("TXN_TYPE",Txntype.manualReversal)
            editor.commit()
            startActivity(Intent(this,ReversalActivity::class.java))
            finish()
        }

    }
}
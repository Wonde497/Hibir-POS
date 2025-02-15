package net.geidea.payment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.databinding.ActivityManualSaleReversalBinding
import net.geidea.payment.transaction.ManualCardEntry

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
        binding.manualSaleIcon.setOnClickListener{
            editor.putString("TXN_TYPE",TxnType.M_PURCHASE)
            editor.commit()
            startActivity(Intent(this,AmountActivity::class.java))
            finish()
        }
        binding.manualReversalIcon.setOnClickListener{
            editor.putString("TXN_TYPE",TxnType.M_REVERSAL)
            editor.commit()
            startActivity(Intent(this,ReversalActivity::class.java))
            finish()
        }
        binding.balanceInquiryIcon.setOnClickListener{
            editor.putString("TXN_TYPE",TxnType.M_BALANCE_INQUIRY)
            editor.commit()
            startActivity(Intent(this,ManualCardEntry::class.java))
            finish()
        }
        binding.refundIcon.setOnClickListener{
            editor.putString("TXN_TYPE",TxnType.M_REFUND)
            editor.commit()
            startActivity(Intent(this,RefundActivity::class.java))
            finish()


        }

    }
}
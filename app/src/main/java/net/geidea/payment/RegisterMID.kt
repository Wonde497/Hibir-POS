package net.geidea.payment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.databinding.ActivityRegisterMidBinding

class RegisterMID : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterMidBinding
    lateinit var dbHandler:DBHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterMidBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHandler=DBHandler(this)
        binding.btnRegister.setOnClickListener {
            if(binding.etMerchantID.text.toString().isEmpty()){
                binding.etMerchantID.setError("Merchant ID can't be empty!")

            }else{
              if(binding.etMerchantID.text.toString().length==15){
                    dbHandler.registerMID(binding.etMerchantID.text.toString())
                    val intent= Intent(this@RegisterMID,RegisterMerchantName::class.java)
                    startActivity(intent)
                    finish()
                }else binding.etMerchantID.setError("Terminal ID shouldn't be < 15 digits")

             }
        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,RegisterMerchantName::class.java))
    }
}
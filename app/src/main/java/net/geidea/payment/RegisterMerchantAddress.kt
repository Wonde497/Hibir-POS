package net.geidea.payment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.databinding.ActivityRegisterMerchantAddressBinding

class RegisterMerchantAddress : AppCompatActivity() {
    private lateinit var binding:ActivityRegisterMerchantAddressBinding
    private lateinit var dbHandler: DBHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterMerchantAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHandler=DBHandler(this)
        binding.btnRegister.setOnClickListener {
            if(binding.etMerchantAddress.text.isEmpty()){
                binding.etMerchantAddress.setError("Empty field!")
            }else{
                dbHandler.registerMerchantAddress(binding.etMerchantAddress.text.toString())
                val intent= Intent(this,RegisterTerminalMode::class.java)
                startActivity(intent)
                finish()
            }

        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,RegisterTerminalMode::class.java))
    }
}
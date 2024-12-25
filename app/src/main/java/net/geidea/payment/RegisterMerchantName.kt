package net.geidea.payment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.databinding.ActivityRegisterMerchantNameBinding

class RegisterMerchantName : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterMerchantNameBinding
    private lateinit var dbHandler: DBHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterMerchantNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHandler=DBHandler(this)
        binding.btnRegister.setOnClickListener {
            if(binding.etMerchantName.text.isEmpty()){
                binding.etMerchantName.setError("Field is Empty !")
            }else{
                dbHandler.registerMerchantName(binding.etMerchantName.text.toString())
                val intent= Intent(this,RegisterMerchantAddress::class.java)
                startActivity(intent)
                finish()
            }

        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,RegisterMerchantAddress::class.java))
    }
}
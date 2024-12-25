package net.geidea.payment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.databinding.ActivityRegisterTidBinding

class RegisterTID : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterTidBinding
    lateinit var dbHandler:DBHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterTidBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHandler=DBHandler(this)
        binding.btnRegister.setOnClickListener {
            if(binding.etTerminalID.text.toString().isEmpty()){
                binding.etTerminalID.setError("Terminal ID can't be empty!")

            }else{
                if(binding.etTerminalID.text.toString().length==8){
                    dbHandler.registerTID(binding.etTerminalID.text.toString())
                    val intent= Intent(this@RegisterTID,RegisterMID::class.java)
                    startActivity(intent)
                    finish()

                  }else binding.etTerminalID.setError("Terminal ID shouldn't be < 8 digits")

            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,RegisterMID::class.java))
    }
}
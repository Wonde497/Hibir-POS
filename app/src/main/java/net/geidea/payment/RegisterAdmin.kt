package net.geidea.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.databinding.ActivityRegisterAdminBinding

class RegisterAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterAdminBinding
    private lateinit var dbHandler: DBHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityRegisterAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHandler=DBHandler(this)

        binding.btnRegister.setOnClickListener {
            if(binding.etUsername.text.toString().isNotEmpty()&&binding.etPassword.text.toString().isNotEmpty()&&binding.etConfirmPassword.text.toString().isNotEmpty()){
                if (binding.etPassword.text.toString().equals(binding.etConfirmPassword.text.toString())){
                    dbHandler.registerAdmin(binding.etUsername.text.toString(),binding.etPassword.text.toString())
                    Toast.makeText(this,"Admin Registered Successfully", Toast.LENGTH_SHORT).show()
                    val intent= Intent(this@RegisterAdmin,RegisterTID::class.java)
                    startActivity(intent)
                    finish()
                }else binding.etConfirmPassword.setError("passwords should be same!")
            }else Toast.makeText(this,"Please fill all fields",Toast.LENGTH_SHORT).show()



        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

    }




    }


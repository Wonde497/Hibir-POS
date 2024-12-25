package net.geidea.payment.users.admin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.geidea.payment.DBHandler
import net.geidea.payment.R
import net.geidea.payment.databinding.ActivityRegisterSupportBinding

class RegisterSupport : AppCompatActivity() {
    private lateinit var binding:ActivityRegisterSupportBinding
    private lateinit var dbHandler: DBHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterSupportBinding.inflate(layoutInflater)
        dbHandler=DBHandler(this)
       setContentView(binding.root)
        binding.btnRegister.setOnClickListener {
            if(binding.etUsername.text.isNotEmpty()&&binding.etPassword.text.isNotEmpty()&& binding.etConfirmPassword.text.isNotEmpty()){
                if(!binding.etPassword.text.toString().equals(binding.etConfirmPassword.text.toString())){
                    binding.etConfirmPassword.setError("The 2 passwords must be same !")
                }else{
                    dbHandler.registerSupport(binding.etUsername.text.toString(),binding
                        .etPassword.text.toString())
                    Toast.makeText(this,"Support added successfully !",Toast.LENGTH_SHORT).show()
                    finish()

                }
            } else Toast.makeText(this,"Please fill all fields !",Toast.LENGTH_SHORT).show()

        }
    }
}
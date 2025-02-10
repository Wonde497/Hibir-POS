package net.geidea.payment.users.supervisor


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.DBHandler
import net.geidea.payment.TxnType
import net.geidea.payment.databinding.SupervisorLoginBinding
import net.geidea.payment.transaction.view.CardReadActivity

class SupervisorLogin : AppCompatActivity() {

    private lateinit var binding: SupervisorLoginBinding
    private var dbHandler= DBHandler(this)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor:SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SupervisorLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()
        // Populate user types in Spinner

        // Handle login button click
        binding.loginButton.setOnClickListener {
            handleLogin()
        }

        // Handle forgot password click
       
    }

   

    private fun handleLogin() {
        val username = binding.usernameInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            return
        }
        if (dbHandler.isSuperExists(username,password)) {
            //go to input pin
           /* val showpin  = commonMethods(this)
            showpin.showPinPad()*/
            if(sharedPreferences.getString("TXN_TYPE","").equals(TxnType.REFUND)){
            CardReadActivity.startTransaction(this, intent.getLongExtra("amount", 0L))
            }else {
                val intent = Intent(this, CardReadActivity::class.java)
                startActivity(intent)
            }
        } else {
            Toast.makeText(this, "Invalid credentials or user type", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        finish() // Optional: finish the LoginActivity if you don't want to return to it
    }

}

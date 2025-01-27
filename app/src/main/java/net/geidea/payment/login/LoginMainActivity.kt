package net.geidea.payment.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.DBHandler
import net.geidea.payment.MainMenuActivity
import net.geidea.payment.databinding.ActivityLoginBinding
import net.geidea.payment.users.admin.AdminMainActivity
import net.geidea.payment.users.cashier.CashierMainActivity
import net.geidea.payment.users.support.SupportMainActivity
import net.geidea.payment.users.supervisor.SupervisorMainActivity

class LoginMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHandler: DBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHandler = DBHandler(this)

        // Populate user types in Spinner
        setupUserTypeSpinner()

        // Handle login button click
        binding.loginButton.setOnClickListener { handleLogin() }

        // Handle forgot password click
        binding.forgotPassword.setOnClickListener { handleForgotPassword() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@LoginMainActivity, MainMenuActivity::class.java))
                finish()
            }
        })
    }

    private fun setupUserTypeSpinner() {
        val userTypes = arrayOf("Admin", "Support", "Supervisor", "Cashier")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, userTypes)
        binding.userTypeSpinner.adapter = adapter
    }

    private fun handleLogin() {
        val username = binding.usernameInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()
        val userType = binding.userTypeSpinner.selectedItem.toString()

        if (username.isEmpty() || password.isEmpty()) {
            showToast("Please enter both username and password")
            return
        }

        if (isLoginValid(username, password, userType)) {
            // Store username and user type in SharedPreferences
            saveUserCredentials(username, userType)
            navigateToMainActivity(userType)
        } else {
            showToast("Invalid credentials or user type")
        }
    }

    private fun isLoginValid(username: String, password: String, userType: String): Boolean {
        return when (userType) {
            "Admin" -> dbHandler.isAdminExists(username, password)
            "Support" -> dbHandler.isSupportExists(username, password)
            "Supervisor" -> dbHandler.isSuperExists(username, password)
            "Cashier" -> dbHandler.isCashierExists(username, password)
            else -> false
        }
    }

    private fun saveUserCredentials(username: String, userType: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("username", username)
            putString("userType", userType)
            apply()
        }
    }

    private fun navigateToMainActivity(userType: String) {
        val intent = when (userType) {
            "Admin" -> Intent(this, AdminMainActivity::class.java)
            "Support" -> Intent(this, SupportMainActivity::class.java)
            "Supervisor" -> Intent(this, SupervisorMainActivity::class.java)
            "Cashier" -> Intent(this, CashierMainActivity::class.java)
            else -> return
        }
        startActivity(intent)
        finish() // Optional: finish the LoginActivity if you don't want to return to it
    }

    private fun handleForgotPassword() {
        // Handle forgot password action
        showToast("Forgot Password")
        // You can navigate to a Forgot Password activity here
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

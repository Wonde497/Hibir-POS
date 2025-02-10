package net.geidea.payment.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.R

class ForgotPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val resetPasswordButton = findViewById<Button>(R.id.resetPasswordButton)
        val backToLogin = findViewById<TextView>(R.id.backToLogin)

        resetPasswordButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()

            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show()
            } else {
                resetPassword(username)
            }
        }

        backToLogin.setOnClickListener {
            val intent = Intent(this@ForgotPassword, LoginMainActivity::class.java)
            startActivity(intent)
            finish() // Closes the current activity and goes back
        }
    }

    private fun resetPassword(username: String) {
        // Simulate password reset logic
        Toast.makeText(this, "Password sent to $username", Toast.LENGTH_LONG).show()
    }
}

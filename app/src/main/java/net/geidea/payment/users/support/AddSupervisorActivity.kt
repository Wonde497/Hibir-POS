package net.geidea.payment.users.support


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.DBHandler
import net.geidea.payment.databinding.FragmentAddUserBinding

class AddSupervisorActivity : AppCompatActivity() {

    private lateinit var binding: FragmentAddUserBinding
    private lateinit var dbHandler: DBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHandler = DBHandler(this)
        // Set title for Support user
        binding.addUserTitle.text = "Add Supervisor"

        // Handle button click event
        binding.addUserButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val confirmPassword = binding.confirmPasswordInput.text.toString()

            // Validate inputs
            if (validateInputs(username, password, confirmPassword)) {
                // Check if the username is already registered
                if (!isSupervisorUsernameExists(username)) {
                    // Create user in the database
                    dbHandler.registerSuper(username, password)
                    Toast.makeText(this, "Supervisor user $username created successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Username is already registered as Supervisor.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun validateInputs(username: String, password: String, confirmPassword: String): Boolean {
        // 1. Validate username (only characters, no digits or special characters)
        val usernamePattern = Regex("^[a-zA-Z]+$")
        if (!usernamePattern.matches(username)) {
            Toast.makeText(this, "Username must contain only alphabetic characters.", Toast.LENGTH_SHORT).show()
            return false
        }

        // 2. Validate password (between 4 and 8 digits)
        if (password.length < 4 || password.length > 8) {
            Toast.makeText(this, "Password must be between 4 and 8 characters.", Toast.LENGTH_SHORT).show()
            return false
        }

        // 3. Validate confirm password (should match password)
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    fun isSupervisorUsernameExists(username: String): Boolean {
        // Query the database to check if the username exists
        return dbHandler.isSupervisorUsernameExists(username)
    }

}

package net.geidea.payment.customviews

import net.geidea.payment.R
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CustomKeyboardActivity : AppCompatActivity() {

    private lateinit var inputField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_keyboard)

        // Initialize the input field
        inputField = findViewById(R.id.inputField)

        // Set click listeners for all buttons
        val buttons = listOf(
            findViewById<Button>(R.id.btn_0),
            findViewById<Button>(R.id.btn_1),
            findViewById<Button>(R.id.btn_2),
            findViewById<Button>(R.id.btn_3),
            findViewById<Button>(R.id.btn_4),
            findViewById<Button>(R.id.btn_5),
            findViewById<Button>(R.id.btn_6),
            findViewById<Button>(R.id.btn_7),
            findViewById<Button>(R.id.btn_8),
            findViewById<Button>(R.id.btn_9),
            findViewById<Button>(R.id.btn_decimal),
            findViewById<Button>(R.id.btn_backspace),
            findViewById<Button>(R.id.btn_confirm)
        )

        buttons.forEach { button ->
            button.setOnClickListener { onKeyClick(it) }
        }
    }

    private fun onKeyClick(view: View) {
        when (view.id) {
            R.id.btn_0 -> appendToInput("0")
            R.id.btn_1 -> appendToInput("1")
            R.id.btn_2 -> appendToInput("2")
            R.id.btn_3 -> appendToInput("3")
            R.id.btn_4 -> appendToInput("4")
            R.id.btn_5 -> appendToInput("5")
            R.id.btn_6 -> appendToInput("6")
            R.id.btn_7 -> appendToInput("7")
            R.id.btn_8 -> appendToInput("8")
            R.id.btn_9 -> appendToInput("9")
            R.id.btn_decimal -> appendToInput(".")
            R.id.btn_backspace -> backspace()
            R.id.btn_confirm -> confirmInput()
        }
    }

    private fun appendToInput(value: String) {
        val currentText = inputField.text.toString()
        inputField.setText(currentText + value)
    }

    private fun backspace() {
        val currentText = inputField.text.toString()
        if (currentText.isNotEmpty()) {
            inputField.setText(currentText.dropLast(1))
        }
    }

    private fun clearInput() {
        inputField.setText("")
    }

    private fun confirmInput() {
        val amount = inputField.text.toString()
        if (amount.isNotEmpty()) {
            Toast.makeText(this, "Confirmed: $amount ETB", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
        }
    }
}
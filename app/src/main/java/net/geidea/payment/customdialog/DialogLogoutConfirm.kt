package net.geidea.payment.customdialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import net.geidea.payment.R

class DialogLogoutConfirm(
    context: Context,
    private val title: String,
    private val message: String,
    private val cancelBtn: String,
    private val logoutBtn: String,
    private val onConfirmAction: () -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Remove the default title
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Set the custom layout for the dialog
        setContentView(R.layout.dialog_logout_confirm)

        // Initialize views
        val titleTextView: TextView = findViewById(R.id.logoutDialogTitle)
        val messageTextView: TextView = findViewById(R.id.logoutDialogMessage)
        val cancelButton: Button = findViewById(R.id.cancelButton)
        val confirmButton: Button = findViewById(R.id.logoutButton)

        // Set title and message dynamically
        titleTextView.text = title
        messageTextView.text = message

        cancelButton.text = cancelBtn
        confirmButton.text = logoutBtn

        // Set up Cancel button click listener
        cancelButton.setOnClickListener {
            dismiss() // Close the dialog
        }

        // Set up Confirm button click listener
        confirmButton.setOnClickListener {
            onConfirmAction() // Trigger the confirm action
            dismiss() // Close the dialog
        }
        // Customize the dialog window if needed
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}

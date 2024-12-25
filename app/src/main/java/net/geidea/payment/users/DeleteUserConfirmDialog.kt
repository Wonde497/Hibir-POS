package net.geidea.payment.users

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import net.geidea.payment.R

class DeleteUserConfirmDialog(
    context: Context,
    private val username: String,
    private val title: String, // New parameter for the title
    private val onDeleteConfirmed: () -> Unit // Callback for when delete is confirmed
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.confirm_delete_dialog)

        // Set the message with the user's name
        val messageTextView = findViewById<TextView>(R.id.deleteDialogMessage)
        messageTextView.text = "Are you sure you want to delete $username?\nThis action cannot be undone!"

        // Set the title for the dialog
        val dialogTitle = findViewById<TextView>(R.id.deleteDialogTitle)
        dialogTitle.text = title // Set the title text here


        val cancelButton = findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            dismiss() // Close the dialog when cancel is pressed
        }

        // Set up the Delete button
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            onDeleteConfirmed() // Trigger the callback when delete is pressed
            dismiss() // Close the dialog after confirming
        }

        // Customize the dialog window if needed
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}

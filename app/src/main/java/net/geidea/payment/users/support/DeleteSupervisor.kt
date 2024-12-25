package net.geidea.payment.users.support

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.DBHandler
import net.geidea.payment.R
import net.geidea.payment.users.DeleteUserConfirmDialog

class DeleteSupervisor : AppCompatActivity() {

    private lateinit var dbHandler: DBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_delete_user) // Use your XML layout

        dbHandler = DBHandler(this)

        val usernameInput = findViewById<EditText>(R.id.usernameInput) // EditText from your XML layout
        val deleteButton = findViewById<Button>(R.id.deleteUserButton) // Button from your XML layout

//        deleteButton.setOnClickListener {
//            val usernameToDelete = usernameInput.text.toString()
//
//            if (usernameToDelete.isNotEmpty()) {
//                val deleted = dbHandler.deleteUser(DBHandler.TABLE_SUPER, usernameToDelete) // Deleting from Supervisor table
//                if (deleted) {
//                    Toast.makeText(this, "Supervisor deleted successfully", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "Supervisor not found", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
//            }
//        }



        deleteButton.setOnClickListener {
            val usernameToDelete = usernameInput.text.toString()

            if (usernameToDelete.isNotEmpty()) {
                // Show confirmation dialog before deleting
                val deleteDialog = DeleteUserConfirmDialog(
                    context = this,
                    username = usernameToDelete,
                    title = "Delete Supervisor",
                    onDeleteConfirmed = {
                        // When confirmed, delete the user
                        val deleted = dbHandler.deleteUser(DBHandler.TABLE_SUPER, usernameToDelete) // Deleting from Supervisor table
                        if (deleted) {
                            Toast.makeText(this, "Supervisor deleted successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Supervisor not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // Show the delete confirmation dialog
                deleteDialog.show()
            } else {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

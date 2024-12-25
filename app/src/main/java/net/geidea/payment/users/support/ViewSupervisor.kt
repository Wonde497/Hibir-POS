package net.geidea.payment.users.support

import android.os.Bundle
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import net.geidea.payment.DBHandler
import net.geidea.payment.R

class ViewSupervisor : AppCompatActivity() {
    private lateinit var dbHandler: DBHandler
    private lateinit var userTable: TableLayout
    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_view_user)

        dbHandler = DBHandler(this)
        userTable = findViewById(R.id.userTable)
        searchBar = findViewById(R.id.searchBar)

        // Load all users sorted alphabetically
        loadUsers("super", "Supervisor")

        // Add listener to filter search results
        searchBar.addTextChangedListener {
            val query = it.toString()
            loadUsers("super", "Supervisor", query)
        }
    }

    // Load users from the database
    private fun loadUsers(tableName: String, userType: String, searchQuery: String = "") {
        userTable.removeAllViews()
        // Fetch all users or filter by search query
        val users = dbHandler.viewUser(tableName, userType)
            .filter { it.username.contains(searchQuery, true) } // Filter results based on search query
        // Populate the table with user data
        users.forEach { user ->
            val row = TableRow(this)
            row.addView(createTextView(user.username))
            row.addView(createTextView(user.userType))
            row.addView(createTextView(user.password))
            userTable.addView(row)
        }
    }

    // Helper function to create a TextView for each table cell
    private fun createTextView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            setPadding(8, 8, 8, 8)
            setTextColor(resources.getColor(R.color.nib_brown1))
        }
    }
}

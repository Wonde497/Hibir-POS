
package net.geidea.payment.users.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import net.geidea.payment.DBHandler
import net.geidea.payment.R

class AdminManageTransaction : AppCompatActivity() {

    // Define all the SwitchCompat elements
    private lateinit var balanceInqSwitch: SwitchCompat
    private lateinit var depositSwitch: SwitchCompat
    private lateinit var manualCardEntrySwitch: SwitchCompat
    private lateinit var phoneAuthSwitch: SwitchCompat
    private lateinit var preAuthSwitch: SwitchCompat
    private lateinit var saleSwitch: SwitchCompat
    private lateinit var refundSwitch: SwitchCompat

    private lateinit var dbHelper: DBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_manage_transaction)

        dbHelper = DBHandler(this)

        // Initialize SwitchCompat elements
        balanceInqSwitch = findViewById(R.id.balanceinq)
        depositSwitch = findViewById(R.id.deposit)
        manualCardEntrySwitch = findViewById(R.id.manualcardentry)
        phoneAuthSwitch = findViewById(R.id.Phone)
        preAuthSwitch = findViewById(R.id.pre_auth)
        saleSwitch = findViewById(R.id.sale)
        refundSwitch = findViewById(R.id.refund)

        // Set listeners for each switch to handle the toggle actions
        balanceInqSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle Balance INQ switch toggle
            if (isChecked) {
                //dbHelper.addTransactionStatus("balanceINQ", "1")
            } else {
               // dbHelper.addTransactionStatus("balanceINQ", "0")
            }
        }

        depositSwitch.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
              //  dbHelper.addTransactionStatus("deposit", "1")
            } else {
               // dbHelper.addTransactionStatus("deposit", "0")
            }
        }

        manualCardEntrySwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle Manual Card Entry switch toggle
            if (isChecked) {
                // Switch is ON
            } else {
                // Switch is OFF
            }
        }

        phoneAuthSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle Phone Authorization switch toggle
            if (isChecked) {
                // Switch is ON
            } else {
                // Switch is OFF
            }
        }

        preAuthSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle Pre-Auth switch toggle
            if (isChecked) {
                // Switch is ON
            } else {
                // Switch is OFF
            }
        }

        saleSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle Sale switch toggle
            if (isChecked) {
                // Switch is ON
            } else {
                // Switch is OFF
            }
        }

        refundSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle Refund switch toggle
            if (isChecked) {
                // Switch is ON
            } else {
                // Switch is OFF
            }
        }
    }
}

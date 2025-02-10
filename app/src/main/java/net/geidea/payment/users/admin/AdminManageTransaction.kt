
package net.geidea.payment.users.admin

import android.content.Context
import android.content.SharedPreferences
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
    private lateinit var reversalSwitch: SwitchCompat
    private lateinit var manualCardEntrySwitch: SwitchCompat
    private lateinit var phoneAuthSwitch: SwitchCompat
    private lateinit var preAuthSwitch: SwitchCompat
    private lateinit var preAuthCompletionSwitch: SwitchCompat
    private lateinit var saleSwitch: SwitchCompat
    private lateinit var refundSwitch: SwitchCompat
    private lateinit var sharedPreferences:SharedPreferences
      private lateinit var editor: SharedPreferences.Editor
    private lateinit var dbHelper: DBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_manage_transaction)
        sharedPreferences = getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        dbHelper = DBHandler(this)

        // Initialize SwitchCompat elements
        balanceInqSwitch = findViewById(R.id.balanceinq)
        reversalSwitch = findViewById(R.id.reversal)
        manualCardEntrySwitch = findViewById(R.id.manualcardentry)
        phoneAuthSwitch = findViewById(R.id.Phone)
        preAuthSwitch = findViewById(R.id.pre_auth)
        preAuthCompletionSwitch = findViewById(R.id.pre_auth_completion)
        saleSwitch = findViewById(R.id.sale)
        refundSwitch = findViewById(R.id.refund)

        // Set initial state based on saved preferences
        balanceInqSwitch.isChecked = sharedPreferences.getBoolean("BALANCE_INQ_ENABLED", false)
        reversalSwitch.isChecked = sharedPreferences.getBoolean("REVERSAL_ENABLED", false)
        manualCardEntrySwitch.isChecked = sharedPreferences.getBoolean("MANUAL_ENTRY_ENABLED", false)
        preAuthSwitch.isChecked = sharedPreferences.getBoolean("PRE_AUTH_ENABLED", false)
        preAuthCompletionSwitch.isChecked = sharedPreferences.getBoolean("PRE_AUTH_COMPLETION_ENABLED", false)
        phoneAuthSwitch.isChecked = sharedPreferences.getBoolean("PHONE_AUTH_ENABLED", false)
        saleSwitch.isChecked = sharedPreferences.getBoolean("SALE_ENABLED", false)
        refundSwitch.isChecked = sharedPreferences.getBoolean("REFUND_ENABLED", false)

        // Set listeners for each switch to handle the toggle actions
        balanceInqSwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("BALANCE_INQ_ENABLED", isChecked).commit()
        }

        reversalSwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("REVERSAL_ENABLED", isChecked).commit()
        }

        manualCardEntrySwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("MANUAL_ENTRY_ENABLED", isChecked).commit()
        }

        preAuthSwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("PRE_AUTH_ENABLED", isChecked).commit()
        }
        preAuthCompletionSwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("PRE_AUTH_COMPLETION_ENABLED", isChecked).commit()
        }

        phoneAuthSwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("PHONE_AUTH_ENABLED", isChecked).commit()
        }

        saleSwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("SALE_ENABLED", isChecked).commit()
        }

        refundSwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("REFUND_ENABLED", isChecked).commit()
        }
    }
}

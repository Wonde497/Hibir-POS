package net.geidea.payment.help

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import net.geidea.payment.databinding.ActivityHelpMainBinding  // Use the correct binding class

class HelpMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpMainBinding  // Make sure it's ActivityHelpBinding, not ActivityMainBinding
    private lateinit var adapter: HelpAdapter
    private val helpItems = mutableListOf<HelpItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpMainBinding.inflate(layoutInflater)  // Inflate activity_help.xml
        setContentView(binding.root)

        // Setup RecyclerView with the help
        binding.helpRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HelpAdapter(helpItems)
        binding.helpRecyclerView.adapter = adapter

        // Populate help items (questions and answers)
        populateHelpItems()
    }

    private fun populateHelpItems() {
        helpItems.apply {
            add(HelpItem("How to login?", "To log in, click the 'Login' button from Main Menu Activity and then enter your username and then select your user type", "Guide: If you forgot password contact your admin"))
            add(HelpItem("How to logout?", "To log out, go to profile click Logout from bottom and confirm to logout", "Guide: Remember to log out after use."))
            add(HelpItem("How to do a transaction?", "Navigate to 'Transactions' section on Main Menu activity and select transaction type that you want to do", "Guide: Double-check details before confirming."))
            add(HelpItem("How to reverse a transaction?", "Go to 'Transactions' on Main Menu activity and select Reversal from transaction types and then enter your approval code then confirm to reverse the transaction", "Guide: Reversal only within 24 hours before settlement done."))
            add(HelpItem("How to do settlement?", "Go to 'Settlement' and follow prompts", "Guide: Only accessible after supervisor approval."))
        }
        adapter.notifyDataSetChanged()
    }
}

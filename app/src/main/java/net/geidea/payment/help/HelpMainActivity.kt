package net.geidea.payment.help

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import net.geidea.payment.R
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

    @SuppressLint("NotifyDataSetChanged")
    private fun populateHelpItems() {
        helpItems.apply {
            add(HelpItem(getString(R.string.how_to_login), getString(R.string.how_to_login_answer), getString(R.string.how_to_login_guide)))
            add(HelpItem(getString(R.string.how_to_logout), getString(R.string.how_to_logout_answer), getString(R.string.how_to_logout_guide)))
            add(HelpItem(getString(R.string.how_to_do_transaction), getString(R.string.how_to_do_transaction_answer), getString(R.string.how_to_do_transaction_guide)))
            add(HelpItem(getString(R.string.how_to_reverse_transaction), getString(R.string.how_to_reverse_transaction_answer), getString(R.string.how_to_reverse_transaction_guide)))
            add(HelpItem(getString(R.string.how_to_do_settlement), getString(R.string.how_to_do_settlement_answer), getString(R.string.how_to_do_settlement_guide)))
        }
        adapter.notifyDataSetChanged()
    }
}

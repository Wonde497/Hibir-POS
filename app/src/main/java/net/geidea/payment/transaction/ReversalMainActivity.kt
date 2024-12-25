package net.geidea.payment.transaction

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import net.geidea.payment.databinding.FragmentReversalBinding

class ReversalMainActivity : AppCompatActivity() {

    private lateinit var binding: FragmentReversalBinding
    private val approvalCodes = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentReversalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up ListView adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, approvalCodes)
        binding.approvalCodesListView.adapter = adapter

        // Handle search button click
        binding.searchApprovalCodeButton.setOnClickListener {
            searchApprovalCodes(adapter)
        }

        // Handle CardView click for expand/collapse
        binding.expandableCardView.setOnClickListener {
            toggleExpandableSection()
        }
    }

    private fun searchApprovalCodes(adapter: ArrayAdapter<String>) {
        // Get input from user
        val inputCode = binding.approvalCodeInput.text.toString().trim()
        if (inputCode.isNotEmpty()) {
            // Clear previous results
            approvalCodes.clear()

            // Simulate search logic (replace with your actual search logic)
            // For demonstration, we'll add some mock data
            approvalCodes.addAll(listOf("$inputCode-001", "$inputCode-002", "$inputCode-003"))

            // Notify adapter of data change
            adapter.notifyDataSetChanged()

            // Show expandable section if there are results
            binding.expandableSection.visibility = if (approvalCodes.isNotEmpty()) View.VISIBLE else View.GONE
        } else {
            // Handle empty input (e.g., show a toast or error message)
            binding.approvalCodeInput.error = "Please enter an approval code"
        }
    }

    private fun toggleExpandableSection() {
        if (binding.expandableSection.visibility == View.VISIBLE) {
            binding.expandableSection.visibility = View.GONE
        } else {
            binding.expandableSection.visibility = View.VISIBLE
        }
    }
}

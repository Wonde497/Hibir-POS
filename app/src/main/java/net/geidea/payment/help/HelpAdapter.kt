package net.geidea.payment.help

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.geidea.payment.databinding.HelpItemBinding  // Correct binding for help_item.xml

class HelpAdapter(private val helpItems: List<HelpItem>) : RecyclerView.Adapter<HelpAdapter.HelpViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpViewHolder {
        val binding = HelpItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HelpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HelpViewHolder, position: Int) {
        val helpItem = helpItems[position]
        holder.bind(helpItem)
    }

    override fun getItemCount(): Int = helpItems.size

    inner class HelpViewHolder(private val binding: HelpItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(helpItem: HelpItem) {
            binding.question.text = helpItem.question
            binding.answer.text = helpItem.answer
            binding.guide.text = helpItem.guide

            // Toggle visibility of answer and guide
            binding.root.setOnClickListener {
                val isVisible = binding.answer.visibility == View.VISIBLE
                binding.answer.visibility = if (isVisible) View.GONE else View.VISIBLE
                binding.guide.visibility = if (isVisible) View.GONE else View.VISIBLE

                // Animate dropdown icon rotation
                binding.dropdownIcon.animate().rotation(if (isVisible) 0f else 180f).setDuration(300).start()
            }
        }
    }
}


package net.geidea.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.geidea.payment.databinding.ItemViewPagerImageBinding

class ImageSliderAdapter(private val imageResIds: List<Int>) :
    RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSliderViewHolder {
        val binding = ItemViewPagerImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageSliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageSliderViewHolder, position: Int) {
        val imageResId = imageResIds[position]
        holder.binding.imageView.setImageResource(imageResId)
    }

    override fun getItemCount(): Int = imageResIds.size

    class ImageSliderViewHolder(val binding: ItemViewPagerImageBinding) :
        RecyclerView.ViewHolder(binding.root)
}

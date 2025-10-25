package com.example.petracker.feature_menu.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.petracker.R
import com.example.petracker.databinding.ItemSliderBinding

class ImageSliderAdapter(
    private val images: List<Int>
) : RecyclerView.Adapter<ImageSliderAdapter.VH>() {

    inner class VH(val b: ItemSliderBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.b.imgSlide.setImageResource(images[position])
    }
    override fun getItemCount() = images.size
}
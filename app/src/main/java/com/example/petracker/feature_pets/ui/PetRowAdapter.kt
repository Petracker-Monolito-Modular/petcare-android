package com.example.petracker.feature_pets.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.petracker.R
import com.example.petracker.common.model.Pet

class PetRowAdapter(
    ctx: Context,
    private val items: MutableList<Pet>,
    private val onEdit: (Pet) -> Unit,
    private val onDelete: (Pet) -> Unit
) : ArrayAdapter<Pet>(ctx, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: LayoutInflater.from(context).inflate(R.layout.row_pet, parent, false)
        val pet = items[position]

        v.findViewById<TextView>(R.id.tvTitle).text =
            "${pet.name} • ${pet.species}${if (pet.breed != null) " (${pet.breed})" else ""}"

        v.findViewById<ImageButton>(R.id.btnEdit).setOnClickListener { onEdit(pet) }
        v.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener { onDelete(pet) }

        return v
    }

    fun setData(newItems: List<Pet>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun removeItem(p: Pet) {
        val idx = items.indexOfFirst { it.id == p.id }
        if (idx >= 0) {
            items.removeAt(idx)
            notifyDataSetChanged()
        }
    }

    fun updateItem(p: Pet) {
        val idx = items.indexOfFirst { it.id == p.id }
        if (idx >= 0) {
            items[idx] = p
            notifyDataSetChanged()
        }
    }
}

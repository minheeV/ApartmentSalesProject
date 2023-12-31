package com.example.apartmentsalesproject.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apartmentsalesproject.databinding.ItemSalesBinding
import com.example.apartmentsalesproject.model.data.SaleItem

class SalesRecyclerAdapter(val items: List<SaleItem>) :
    RecyclerView.Adapter<SalesRecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemSalesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SaleItem) {
            binding.item = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemSalesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
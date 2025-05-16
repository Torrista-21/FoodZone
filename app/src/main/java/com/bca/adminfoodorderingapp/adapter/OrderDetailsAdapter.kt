package com.bca.adminfoodorderingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bca.adminfoodorderingapp.databinding.OrderDetailsItemsBinding

class OrderDetailsAdapter(
    private val context: Context,
    private val foodNames: ArrayList<String>,
    private val foodPrices: ArrayList<String>,
    private val foodQuantities: ArrayList<Int>
) : RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val binding = OrderDetailsItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = foodNames.size

    inner class OrderDetailsViewHolder(private val binding: OrderDetailsItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                foodName.text = foodNames.getOrNull(position) ?: "Unknown"
                foodPrice.text = foodPrices.getOrNull(position) ?: "0.00"
                foodQuantity.text = foodQuantities.getOrNull(position)?.toString() ?: "0"
            }
        }
    }
}
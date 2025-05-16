package com.bca.adminfoodorderingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bca.adminfoodorderingapp.databinding.PendingOrderItemBinding

class PendingOrderAdapter(
    private val context: Context,
    private val customerNames: MutableList<String>,
    private val quantity: MutableList<String>,
    private val itemClicked: OnItemClicked
) : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    interface OnItemClicked {
        fun onItemClickListener(position: Int)
        fun onItemAcceptClickListener(position: Int)
        fun onItemDispatchClickListener(position: Int)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = PendingOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size

    inner class PendingOrderViewHolder(private val binding: PendingOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isAccepted = false

        fun bind(position: Int) {
            binding.apply {
                orderfoodName.text = customerNames[position]
                quantityNumber.text = quantity[position]

                orderAcceptButton.apply {
                    text = if (!isAccepted) "Accept" else "Dispatch"
                    setOnClickListener {
                        if (!isAccepted) {
                            text = "Dispatch"
                            isAccepted = true
                            showToast("Order is Accepted")
                            itemClicked.onItemAcceptClickListener(position)
                        } else {
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                customerNames.removeAt(adapterPosition)
                                quantity.removeAt(adapterPosition)
                                notifyItemRemoved(adapterPosition)
                                showToast("Order is Dispatched")
                                itemClicked.onItemDispatchClickListener(position)
                            }
                        }
                    }
                }
                itemView.isClickable = true
                itemView.isFocusable = true
                itemView.setOnClickListener {
                    Toast.makeText(context, "Item clicked at position: $position", Toast.LENGTH_SHORT).show()
                    itemClicked.onItemClickListener(position)
                }
            }
        }

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }


}
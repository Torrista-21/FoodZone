package com.bca.adminfoodorderingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bca.adminfoodorderingapp.databinding.ItemItemBinding
import com.bca.adminfoodorderingapp.model.AllMenu
import com.google.firebase.database.DatabaseReference

class MenuItemAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    databaseReference: DatabaseReference,
    private val OnDeleteClickListener : (position : Int) -> Unit
) : RecyclerView.Adapter<MenuItemAdapter.AddItemViewHolder>() {

    private val itemQuantities = IntArray(menuList.size) { 0 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = ItemItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return AddItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size

    inner class AddItemViewHolder(private val binding: ItemItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val quantity = itemQuantities[position]
            val menuItem = menuList[position]

            binding.foodNameText.text = menuItem.foodName
            binding.foodPriceText.text = menuItem.foodPrice
            binding.quantityButton.text = quantity.toString()

            binding.minusButton.setOnClickListener {
                decreaseQuantity(position)
            }

            binding.trashButton.setOnClickListener {
                OnDeleteClickListener(position)
            }

            binding.addButton.setOnClickListener {
                increaseQuantity(position)
            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                binding.quantityButton.text = itemQuantities[position].toString()
            }
        }

        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 0) {
                itemQuantities[position]--
                binding.quantityButton.text = itemQuantities[position].toString()
            }
        }

        private fun deleteItem(position: Int) {
            menuList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, menuList.size)
        }
    }
}

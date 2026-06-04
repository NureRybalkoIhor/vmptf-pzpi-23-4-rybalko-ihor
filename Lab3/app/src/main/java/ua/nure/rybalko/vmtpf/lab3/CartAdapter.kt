package ua.nure.rybalko.vmtpf.lab3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onCartChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCartItemName: TextView = view.findViewById(R.id.tvCartItemName)
        val tvCartItemPrice: TextView = view.findViewById(R.id.tvCartItemPrice)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val btnMinus: Button = view.findViewById(R.id.btnMinus)
        val btnPlus: Button = view.findViewById(R.id.btnPlus)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.tvCartItemName.text = item.product.name
        holder.tvCartItemPrice.text = "${item.product.price} грн"
        holder.tvQuantity.text = item.quantity.toString()

        holder.btnPlus.setOnClickListener {
            CartManager.addProduct(item.product)
            notifyItemChanged(position)
            onCartChanged()
        }

        holder.btnMinus.setOnClickListener {
            CartManager.decrementProduct(item.product)
            if (item.quantity <= 0) {
                notifyDataSetChanged()
            } else {
                notifyItemChanged(position)
            }
            onCartChanged()
        }

        holder.btnDelete.setOnClickListener {
            CartManager.removeProduct(item.product)
            notifyDataSetChanged()
            onCartChanged()
        }
    }

    override fun getItemCount(): Int = cartItems.size
}

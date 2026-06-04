package ua.nure.rybalko.vmtpf.lab3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ProductDiffCallback(
    private val oldList: List<Product>,
    private val newList: List<Product>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.isFavorite == newItem.isFavorite &&
                oldItem.name == newItem.name &&
                oldItem.price == newItem.price &&
                CartManager.isInCart(oldItem.id) == CartManager.isInCart(newItem.id)
    }
}

class ProductAdapter(
    private var products: List<Product>,
    private val onFavoriteToggled: (Product) -> Unit,
    private val onCartUpdated: () -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName: TextView = view.findViewById(R.id.tvProductName)
        val tvProductCategory: TextView = view.findViewById(R.id.tvProductCategory)
        val tvProductDescription: TextView = view.findViewById(R.id.tvProductDescription)
        val tvProductPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val btnToggleFavorite: ImageView = view.findViewById(R.id.btnToggleFavorite)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
        val btnRemoveFromCart: Button = view.findViewById(R.id.btnRemoveFromCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.tvProductName.text = product.name
        holder.tvProductCategory.text = product.category
        holder.tvProductDescription.text = product.description
        holder.tvProductPrice.text = "${product.price} грн"

        val favIcon = if (product.isFavorite) {
            android.R.drawable.btn_star_big_on
        } else {
            android.R.drawable.btn_star_big_off
        }
        holder.btnToggleFavorite.setImageResource(favIcon)

        val inCart = CartManager.isInCart(product.id)
        if (inCart) {
            holder.btnAddToCart.text = "Вже у кошику"
            holder.btnAddToCart.isEnabled = false
            holder.btnRemoveFromCart.visibility = View.VISIBLE
        } else {
            holder.btnAddToCart.text = "У кошик"
            holder.btnAddToCart.isEnabled = true
            holder.btnRemoveFromCart.visibility = View.GONE
        }

        holder.btnToggleFavorite.setOnClickListener {
            product.isFavorite = !product.isFavorite
            onFavoriteToggled(product)
            notifyItemChanged(position)
        }

        holder.btnAddToCart.setOnClickListener {
            CartManager.addProduct(product)
            notifyItemChanged(position)
            onCartUpdated()
        }

        holder.btnRemoveFromCart.setOnClickListener {
            CartManager.removeProduct(product)
            notifyItemChanged(position)
            onCartUpdated()
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateList(newProducts: List<Product>) {
        val diffCallback = ProductDiffCallback(products, newProducts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        products = newProducts
        diffResult.dispatchUpdatesTo(this)
    }
}

package ua.nure.rybalko.vmtpf.lab3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvOrderItems: TextView = view.findViewById(R.id.tvOrderItems)
        val tvOrderTotal: TextView = view.findViewById(R.id.tvOrderTotal)
        val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvOrderId.text = "Замовлення #${order.id}"
        holder.tvOrderDate.text = order.createdAt.take(16).replace("T", " ")
        holder.tvOrderTotal.text = "Сума: ${order.totalPrice} грн"

        val itemsSummary = order.items.joinToString(", ") { "${it.productName} (x${it.quantity})" }
        holder.tvOrderItems.text = itemsSummary

        val statusText = when (order.status) {
            "pending" -> "Очікує"
            "processing" -> "В обробці"
            "shipped" -> "Відправлено"
            "delivered" -> "Доставлено"
            else -> order.status
        }
        holder.tvOrderStatus.text = statusText
    }

    override fun getItemCount(): Int = orders.size
}

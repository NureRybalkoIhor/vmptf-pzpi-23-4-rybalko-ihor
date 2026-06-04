package ua.nure.rybalko.vmtpf.lab3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class AdminOrderAdapter(
    private val orders: List<Order>,
    private val onStatusChanged: (Order, String) -> Unit
) : RecyclerView.Adapter<AdminOrderAdapter.AdminOrderViewHolder>() {

    class AdminOrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAdminOrderId: TextView = view.findViewById(R.id.tvAdminOrderId)
        val tvAdminOrderDate: TextView = view.findViewById(R.id.tvAdminOrderDate)
        val tvAdminOrderUser: TextView = view.findViewById(R.id.tvAdminOrderUser)
        val tvAdminOrderDelivery: TextView = view.findViewById(R.id.tvAdminOrderDelivery)
        val tvAdminOrderItems: TextView = view.findViewById(R.id.tvAdminOrderItems)
        val tvAdminOrderTotal: TextView = view.findViewById(R.id.tvAdminOrderTotal)
        val btnChangeStatus: Button = view.findViewById(R.id.btnChangeStatus)
        val tvAdminOrderStatus: TextView = view.findViewById(R.id.tvAdminOrderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_order, parent, false)
        return AdminOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminOrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvAdminOrderId.text = "Замовлення #${order.id}"
        holder.tvAdminOrderDate.text = order.createdAt.take(16).replace("T", " ")
        holder.tvAdminOrderUser.text = "Покупець: ${order.userName} (${order.userEmail})"
        holder.tvAdminOrderDelivery.text = "Доставка: ${order.shippingName}, ${order.shippingPhone}, ${order.shippingAddress}"
        holder.tvAdminOrderTotal.text = "Сума: ${order.totalPrice} грн"

        val itemsSummary = order.items.joinToString(", ") { "${it.productName} (x${it.quantity})" }
        holder.tvAdminOrderItems.text = itemsSummary

        val statusText = when (order.status) {
            "pending" -> "Очікує"
            "processing" -> "В обробці"
            "shipped" -> "Відправлено"
            "delivered" -> "Доставлено"
            else -> order.status
        }
        holder.tvAdminOrderStatus.text = statusText

        holder.btnChangeStatus.setOnClickListener {
            val statuses = arrayOf("Очікує", "В обробці", "Відправлено", "Доставлено")
            val statusKeys = arrayOf("pending", "processing", "shipped", "delivered")
            
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Змінити статус замовлення")
                .setItems(statuses) { _, which ->
                    val newStatus = statusKeys[which]
                    order.status = newStatus
                    onStatusChanged(order, newStatus)
                    notifyItemChanged(position)
                }
                .setNegativeButton("Скасувати", null)
                .show()
        }
    }

    override fun getItemCount(): Int = orders.size
}

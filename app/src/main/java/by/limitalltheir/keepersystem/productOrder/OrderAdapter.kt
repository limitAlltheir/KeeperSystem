package by.limitalltheir.keepersystem.productOrder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.limitalltheir.keepersystem.R
import kotlinx.android.synthetic.main.order_item.view.*

class OrderAdapter() : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private var orderListAdapter = ArrayList<Order>()

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(order: Order) {
            with(itemView) {
                var nameStr = ""
                var quantityStr = ""
                sum_tv.text = "Сумма: " + order.sumOrder.toString()
                for (product in order.products) {
                    nameStr += product.name + "\n"
                    quantityStr += product.quantity.toString() + "\n"
                }
                name_tv.text = nameStr
                quantity_tv.text = quantityStr
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orderListAdapter[position])
    }

    override fun getItemCount() = orderListAdapter.size

    fun setList(list: ArrayList<Order>) {
        orderListAdapter = list
        notifyDataSetChanged()
    }
}
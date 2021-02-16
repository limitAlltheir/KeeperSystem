package by.limitalltheir.keepersystem.productOrder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.limitalltheir.keepersystem.interfaces.OnItemClick
import by.limitalltheir.keepersystem.R
import by.limitalltheir.keepersystem.product.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_order_product.view.*
import kotlinx.android.synthetic.main.item_store_product.view.*
import kotlinx.android.synthetic.main.item_store_product.view.group_tv
import kotlinx.android.synthetic.main.item_store_product.view.name_tv
import kotlinx.android.synthetic.main.item_store_product.view.price_tv

class ProductOrderAdapter(val userItemClick: OnItemClick) :
    RecyclerView.Adapter<ProductOrderAdapter.OrderViewHolder>() {

    private var orderListAdapter = ArrayList<Product>()

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

        fun bind(product: Product) {
            with(itemView) {
                name_tv.text = product.name
                group_tv.text = product.group
                price_tv.text = product.price.toString()
                quantity_tv.text = product.quantity.toString()
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                userItemClick.onItemClick(position)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_product, parent, false)
        return OrderViewHolder(
            view
        )
    }

    override fun getItemCount() = orderListAdapter.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orderListAdapter[position])
    }

    fun setList(list: ArrayList<Product>) {
        orderListAdapter = list
        notifyDataSetChanged()
    }
}
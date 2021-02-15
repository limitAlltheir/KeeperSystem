package by.limitalltheir.keepersystem.productStorage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.limitalltheir.keepersystem.R
import by.limitalltheir.keepersystem.product.Product
import by.limitalltheir.keepersystem.interfaces.OnItemClick
import kotlinx.android.synthetic.main.item_store_product.view.*

class ProductStorageAdapter(val userItemClick: OnItemClick) :
    RecyclerView.Adapter<ProductStorageAdapter.ProductViewHolder>() {

    private var productListAdapter = ArrayList<Product>()

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

        fun bind(product: Product) {
            with(itemView) {
                name_tv.text = product.name
                group_tv.text = product.group
                price_tv.text = product.price.toString()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_store_product, parent, false)
        return ProductViewHolder(
            view
        )
    }

    override fun getItemCount() = productListAdapter.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productListAdapter[position])
    }

    fun setList(list: ArrayList<Product>) {
        productListAdapter = list
        notifyDataSetChanged()
    }
}
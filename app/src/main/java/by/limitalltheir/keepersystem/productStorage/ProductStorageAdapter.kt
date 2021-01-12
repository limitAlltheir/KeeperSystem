package by.limitalltheir.keepersystem.productStorage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import by.limitalltheir.keepersystem.R
import by.limitalltheir.keepersystem.product.Product
import kotlinx.android.synthetic.main.item_store_product.view.*

private const val KEY = "key"

class ProductStorageAdapter : RecyclerView.Adapter<ProductStorageAdapter.ProductViewHolder>() {

    private var productListAdapter = ArrayList<Product>()

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(product: Product) {
            with(itemView) {
                this.setOnClickListener {
                    val intent = Intent(context, DetailsProductActivity::class.java)
                    intent.putExtra(KEY, adapterPosition)
                    startActivity(context, intent, Bundle())
                }
                name_tv.text = product.name
                group_tv.text = product.group
                price_tv.text = product.price.toString()
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
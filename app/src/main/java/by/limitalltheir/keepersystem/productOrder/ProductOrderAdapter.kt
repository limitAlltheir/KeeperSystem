package by.limitalltheir.keepersystem.productOrder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.limitalltheir.keepersystem.R
import by.limitalltheir.keepersystem.product.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_store_product.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProductOrderAdapter : RecyclerView.Adapter<ProductOrderAdapter.OrderViewHolder>() {

    private var orderListAdapter = ArrayList<Product>()

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val orderStoreCollections = Firebase.firestore.collection("orders")

        private fun deleteOrder(product: Product) =
            CoroutineScope(Dispatchers.IO).launch {
                val productQuery = orderStoreCollections
                    .whereEqualTo("name", product.name)
                    .whereEqualTo("price", product.price)
                    .whereEqualTo("group", product.group)
                    .limit(1)
                    .get()
                    .await()
                if (productQuery.documents.isNotEmpty()) {
                    for (document in productQuery) {
                        try {
                            orderStoreCollections.document(document.id).delete().await()
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {

                            }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {

                    }
                }
            }

        fun bind(product: Product) {
            with(itemView) {
                this.setOnClickListener {
                    deleteOrder(product)
                }
                name_tv.text = product.name
                group_tv.text = product.group
                price_tv.text = product.price.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_store_product, parent, false)
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
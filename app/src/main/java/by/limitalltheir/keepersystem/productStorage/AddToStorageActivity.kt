package by.limitalltheir.keepersystem.productStorage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import by.limitalltheir.keepersystem.R
import by.limitalltheir.keepersystem.product.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_to_storage.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class AddToStorageActivity : AppCompatActivity() {

    private val productStoreCollections = Firebase.firestore.collection("products")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_storage)

        add_to_store_button.setOnClickListener {
            saveProduct(
                Product(
                    name_add_et.text.toString(),
                    price_add_et.text.toString().toDouble(),
                    group_add_et.text.toString()
                )
            )
        }
    }

    private fun saveProduct(product: Product) {
        productStoreCollections.add(product).addOnCompleteListener { request ->
            if (request.isSuccessful) {
                Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
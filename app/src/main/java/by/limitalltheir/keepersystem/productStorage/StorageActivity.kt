package by.limitalltheir.keepersystem.productStorage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.limitalltheir.keepersystem.R
import by.limitalltheir.keepersystem.interfaces.OnItemClick
import kotlinx.android.synthetic.main.activity_storage.*

private const val KEY = "key"

class StorageActivity : AppCompatActivity(), OnItemClick {

    private val productAdapter =
        ProductStorageAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)
        
        add_product_to_store_button.setOnClickListener {
            val intent = Intent(this, AddToStorageActivity::class.java)
            startActivity(intent)
        }

        recycler_view_store_container.apply {
            layoutManager = LinearLayoutManager(this@StorageActivity)
            adapter = productAdapter
            hasFixedSize()
        }

        val myViewModel = ViewModelProvider(this).get(StorageViewModel::class.java)
        myViewModel.getProductList().observe(this, androidx.lifecycle.Observer {
            productAdapter.setList(it)
        })
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, DetailsProductActivity::class.java)
        intent.putExtra(KEY, position)
        startActivity(intent)
    }

    override fun onItemClick(position: Int, id: View) {

    }
}
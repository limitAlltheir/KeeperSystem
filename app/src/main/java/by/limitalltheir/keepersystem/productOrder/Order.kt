package by.limitalltheir.keepersystem.productOrder

import by.limitalltheir.keepersystem.product.Product

data class Order(
    val products: ArrayList<Product> = ArrayList<Product>(),
    val sumOrder: Double = 0.0
)
package by.limitalltheir.keepersystem.utils

import com.google.firebase.auth.FirebaseAuth

val USER_ID = FirebaseAuth.getInstance().currentUser?.uid
const val USERS_COLLECTIONS = "users"
const val PRODUCTS_COLLECTIONS = "products"
const val ORDERS_COLLECTIONS = "orders"
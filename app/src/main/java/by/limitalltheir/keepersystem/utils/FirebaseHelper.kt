package by.limitalltheir.keepersystem.utils

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

const val TAG = "tag"
val USER_ID = FirebaseAuth.getInstance().currentUser?.uid
val USER = FirebaseAuth.getInstance().currentUser
const val USERS_COLLECTIONS = "users"
const val PRODUCTS_COLLECTIONS = "products"
const val ORDERS_COLLECTIONS = "orders"

fun reAuthUser(email: String, password: String) {
    val credential = EmailAuthProvider.getCredential(email, password)
    USER?.reauthenticate(credential)?.addOnCompleteListener { request->
        if (request.isSuccessful) {
            Log.d(TAG, "Successful")
        } else {
            Log.d(TAG, "ERROR")
        }
    }
}
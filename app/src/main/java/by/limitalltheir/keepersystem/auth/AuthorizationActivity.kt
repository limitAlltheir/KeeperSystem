package by.limitalltheir.keepersystem.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import by.limitalltheir.keepersystem.R
import by.limitalltheir.keepersystem.utils.USERS_COLLECTIONS
import by.limitalltheir.keepersystem.utils.USER_ID
import by.limitalltheir.keepersystem.productOrder.OrderActivity
import by.limitalltheir.keepersystem.users.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_authorization.*

class AuthorizationActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private val userCollection = Firebase.firestore.collection(USERS_COLLECTIONS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener {

            val user = it.currentUser
            if (user != null) {
                Toast.makeText(this, "Пользователь найден", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show()
            }
        }

        sign_in_button.setOnClickListener {
            signIn(email_edit_text.text.toString(), password_edit_text.text.toString())
        }

        sign_up_button.setOnClickListener {
            signUp(email_edit_text.text.toString(), password_edit_text.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }

    private fun signIn(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    checkLoggedInState()
                } else {
                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signUp(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    saveUser()
                    checkLoggedInState()
                } else {
                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkLoggedInState() {
        if (mAuth.currentUser == null) {
            Toast.makeText(this, "U R not logged in", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "U R logged in", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveUser() {
        userCollection.document("$USER_ID").set(User(email_edit_text.text.toString()))
    }
}
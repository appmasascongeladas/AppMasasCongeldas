package es.borja.appmasascongeldas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    lateinit var etEmail: EditText
    lateinit var etPassword: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        etEmail = findViewById(R.id.etEmailLogin)
        etPassword = findViewById(R.id.etPasswordLogin)

    }

    fun onClickAcceder(view: android.view.View) {

        if(TextUtils.isEmpty(etEmail.text) || TextUtils.isEmpty(etPassword.text)){
            Toast.makeText(this, "Rellena email y/o password", Toast.LENGTH_SHORT).show()
        }else{
            loginFirebase(etEmail.text.toString(), etPassword.text.toString())
        }
    }

    private fun loginFirebase(email: String, password: String){
        val auth: FirebaseAuth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    var intent = Intent(this, ListaProductosActivity::class.java)
                     startActivity(intent)
                    finish()
                } else {

                    Toast.makeText(baseContext, "No se ha podido acceder. Revisa tus credenciales",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }
}
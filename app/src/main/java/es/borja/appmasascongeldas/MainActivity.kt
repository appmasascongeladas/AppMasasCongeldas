package es.borja.appmasascongeldas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Cargamos el auth de firebase y preguntamos si ya hay un usuario con la sessión iniciada.
        auth = Firebase.auth
        if(auth.currentUser!=null){
            var intent = Intent(this, ListaProductosActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
package es.borja.appmasascongeldas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import es.borja.appmasascongeldas.clases.Usuario

import android.widget.Button
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.borja.appmasascongeldas.utilidades.Constantes


class CrearUsuariosActivity : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var etNombre: EditText
    lateinit var progressBar: ProgressBar
    lateinit var btnGuardar: Button
    private val auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_usuarios)
        etEmail = findViewById(R.id.etEmailCrearUsuarios)
        etPassword = findViewById(R.id.etPasswordCrearUsuario)
        etNombre = findViewById(R.id.etNombreCrearUsuario)
        progressBar = findViewById(R.id.progressBar)
        btnGuardar = findViewById(R.id.btnGuardarUsuario)
    }

    private fun limpiarFormulario(){
        etEmail.setText("")
        etNombre.setText("")
        etPassword.setText("")
        progressBar.isVisible = false
        btnGuardar.isEnabled = true
    }
    fun onClickCrearUsuario(view: android.view.View) {

        if(TextUtils.isEmpty(etEmail.text) || TextUtils.isEmpty(etPassword.text)){
            Toast.makeText(this, "Rellena email y/o password", Toast.LENGTH_SHORT).show()
        }else{
            progressBar.isVisible = true
            btnGuardar.isEnabled = false
            val nombre = etNombre.text.toString()
            val email = etEmail.text.toString()
            val pass = etPassword.text.toString()
            var usuario = Usuario(id = null, nombre = nombre, email = email)
            crearUsuarioFirebaseAuth(usuario, pass)
        }
    }

    private fun crearUsuarioFirebaseAuth(usuario: Usuario, password:String){
        var auth: FirebaseAuth = Firebase.auth
        auth.createUserWithEmailAndPassword(usuario.email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if(auth.currentUser!=null){
                        val idUser = auth.currentUser!!.uid
                        usuario.id = idUser
                        crearUsuarioFirebaseFirestore(usuario)
                    }

                } else {
                    Toast.makeText(baseContext, "Error al crear el usuario, revisa las credenciales",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }

    private fun crearUsuarioFirebaseFirestore(usuario: Usuario){
        usuario.id?.let{ idUsuario ->
            val db = Firebase.firestore
            db.collection("usuarios")
                .document(idUsuario)
                .set(usuario)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(baseContext, "Usuario creado con éxito",
                        Toast.LENGTH_SHORT).show()
                    reLoginAdmin()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(baseContext, "Error al crear el usuario",
                        Toast.LENGTH_SHORT).show()
                }
        }
        }

    private fun reLoginAdmin(){
        auth.signOut()
         auth.signInWithEmailAndPassword(Constantes.EMAIL_ADMIN, Constantes.PASS_ADMIN)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                limpiarFormulario()

            }
        }
    }

}
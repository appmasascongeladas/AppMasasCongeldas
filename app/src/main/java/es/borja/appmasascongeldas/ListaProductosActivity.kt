package es.borja.appmasascongeldas

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.borja.appmasascongeldas.adaptadores.ClickItemProducto
import es.borja.appmasascongeldas.adaptadores.ProductoAdaptador
import es.borja.appmasascongeldas.clases.Producto
import es.borja.appmasascongeldas.utilidades.Constantes

class ListaProductosActivity : AppCompatActivity() {

    lateinit var rvProductos: RecyclerView
    lateinit var fabCrear: FloatingActionButton
    private var esAdmin = false
    lateinit var adaptador: ProductoAdaptador
    private val auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_productos)
        rvProductos = findViewById(R.id.rvProducto)
        fabCrear = findViewById(R.id.fabCrear)
        fabCrear.setOnClickListener {
            onClickCrear()
        }
        if(auth.currentUser!=null){
           esAdmin = auth.currentUser!!.uid == Constantes.ID_ADMIN
            //Si es administrador muestro el botón
            fabCrear.isVisible = esAdmin
        }
        iniciarAdaptaer()

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            esAdmin = auth.currentUser!!.uid == Constantes.ID_ADMIN
        }
        cargarProductosFirebaseFirestore()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_admin, menu)
        val menuItem = menu.getItem(0)
        menuItem.isVisible = esAdmin
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection

        return when (item.itemId) {
            R.id.menu_item_usuarios -> {
                startActivity(Intent(this, CrearUsuariosActivity::class.java))
                true
            }
            R.id.menu_item_cerrar_sesion -> {
                cerrarSesion()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun cerrarSesion(){
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setPositiveButton("SI",
                DialogInterface.OnClickListener { dialog, id ->
                    auth.signOut()
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                })
            setNegativeButton("NO",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })
        }
            .setCancelable(true)
            .setTitle("Estás seguro de cerrar la sesión?")

        builder.create().show()
    }
    private fun onClickCrear(){
        startActivity(Intent(this, FormularioProductoActivity::class.java))
    }

    fun cargarProductosFirebaseFirestore(){
        Firebase.firestore.collection("productos").get()
            .addOnSuccessListener {
                val tempProductosList = mutableListOf<Producto>()
                for(document in it.documents) {
                    val producto = document.toObject(Producto::class.java)
                    if (producto != null) {
                        tempProductosList.add(producto)
                        }
                    }
                if(tempProductosList.size == 0 )
                    Toast.makeText(applicationContext, "No hay productos en la lista", Toast.LENGTH_SHORT).show()

                adaptador.submitList(tempProductosList)
                adaptador.notifyDataSetChanged()
                 rvProductos.adapter = adaptador

                }


            .addOnFailureListener {
                Toast.makeText(applicationContext,  "Error al cargar los productos", Toast.LENGTH_SHORT).show()

            }
    }
    fun iniciarAdaptaer(){
        adaptador = ProductoAdaptador(ClickItemProducto {

            FirebaseAuth.getInstance().currentUser?.let { user ->
                if (auth.currentUser!!.uid == Constantes.ID_ADMIN) {
                    var intent = Intent(applicationContext, FormularioProductoActivity::class.java)
                    intent.putExtra(Constantes.ID_PRODUCTO, it.id)
                    startActivity(intent)

                } else {
                    var intent = Intent(applicationContext, DetallesProductoActivity::class.java)
                    intent.putExtra(Constantes.ID_PRODUCTO, it.id)
                    startActivity(intent)
                }

            }
        })
        cargarProductosFirebaseFirestore()
    }





}
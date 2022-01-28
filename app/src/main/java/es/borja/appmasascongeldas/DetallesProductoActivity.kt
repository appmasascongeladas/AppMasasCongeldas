package es.borja.appmasascongeldas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import es.borja.appmasascongeldas.clases.Producto
import es.borja.appmasascongeldas.utilidades.Constantes

class DetallesProductoActivity : AppCompatActivity() {

    lateinit var tvNombre: TextView
    lateinit var tvDescripcion: TextView
    lateinit var tvPrecio: TextView
    lateinit var tvCategoria: TextView
    lateinit var ivProducto: ImageView
    private var  idProducto = ""
    private lateinit var producto: Producto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_producto)
        tvNombre = findViewById(R.id.tvNombreDetalleProducto)
        tvDescripcion = findViewById(R.id.tvDescripcionDetalle)
        tvPrecio = findViewById(R.id.tvPrecioDetalle)
        tvCategoria = findViewById(R.id.tvCategoriaDetalle)
        ivProducto = findViewById(R.id.ivDetallesProducto)

        var bundle = intent.extras
        if(bundle!=null){
            idProducto = bundle.getString(Constantes.ID_PRODUCTO).toString()
            obtenerProductoFirebase()
        }
        
    }


    private fun obtenerProductoFirebase(){
        Firebase.firestore.collection("productos").document(idProducto)
            .get()
            .addOnSuccessListener {
                var producto = it.toObject(Producto::class.java)
                producto?.let { prod ->
                    this.producto = prod
                    cargarProducto()
                }

            }
            .addOnFailureListener {

            }
    }


    private fun cargarProducto(){
        tvNombre.text=producto.nombre
        tvDescripcion.text=producto.descripcion
        tvPrecio.text="${producto.precio}€"
        tvCategoria.text=producto.categoria
        FirebaseStorage.getInstance().getReferenceFromUrl(producto.imagen).downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful){
                Glide.with(this).load(task.result).into(ivProducto)
            }
        }
    }
}
package es.borja.appmasascongeldas

import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import es.borja.appmasascongeldas.clases.Producto
import es.borja.appmasascongeldas.utilidades.Constantes
import es.borja.appmasascongeldas.utilidades.Constantes.PICK_IMAGE_REQUEST
import es.borja.appmasascongeldas.utilidades.cargarImagenFirestore
import java.util.*

class FormularioProductoActivity : AppCompatActivity() {
    private lateinit var ivHeader: ImageView
    private lateinit var etNombre: EditText
    private lateinit var etDescriptor: EditText
    private lateinit var etPrecio: EditText
    private lateinit var spCategoria: Spinner
    private lateinit var btnInsertar: Button
    private lateinit var btnMod: Button
    private lateinit var btnEliminar: Button
    private lateinit var fabSeleccionarImg: FloatingActionButton
    private var esModificar = false
    private var  idProductoModificar = ""
    private lateinit var productoModificar: Producto
    //Variables seleccion imagen
    private var rutaAbsolutaImagen = arrayListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_producto)
        etNombre = findViewById(R.id.etNombreProducto)
        etDescriptor = findViewById(R.id.etDescripcionProducto)
        spCategoria = findViewById(R.id.spCategoriaProducto)
        etPrecio = findViewById(R.id.etPrecioProducto)
        fabSeleccionarImg = findViewById(R.id.fabSeleccionarImg)
        ivHeader = findViewById(R.id.ivProducto)
        btnInsertar = findViewById(R.id.btnInsertar)
        btnMod = findViewById(R.id.btnModificar)
        btnEliminar = findViewById(R.id.btnEliminar)
        fabSeleccionarImg.setOnClickListener {
            onClickSeleccionarImagen()
        }
        var bundle = intent.extras
        if(bundle!=null){
            esModificar = true
            idProductoModificar = bundle.getString(Constantes.ID_PRODUCTO).toString()
            obtenerProductoFirebase()
        }else{
            esModificar = false
        }
        cargarFormulario()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_IMAGE_REQUEST && resultCode ==  RESULT_OK && data!=null){
            generarRutaFichero(data)
        }

    }
    private fun cargarFormulario(){
        if(esModificar){
           btnEliminar.isVisible = true
           btnMod.isVisible = true
           btnInsertar.isVisible = false
        } else{

            btnEliminar.isVisible = false
            btnMod.isVisible = false
            btnInsertar.isVisible = true
        }
    }
    private fun cargarProducto(){
        etNombre.setText(productoModificar.nombre)
        etDescriptor.setText(productoModificar.descripcion)
        etPrecio.setText("${productoModificar.precio}")

        FirebaseStorage.getInstance().getReferenceFromUrl(productoModificar.imagen).downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful){
                Glide.with(this).load(task.result).into(ivHeader)
            }
        }
        var categorias = resources.getStringArray(R.array.categoria)
        var indexCat = categorias.indexOf(productoModificar.categoria)
        spCategoria.setSelection(indexCat)
    }
    fun onClickSeleccionarImagen(){
        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(
            Intent.createChooser(intent, "Selecciona una imagen"),
            PICK_IMAGE_REQUEST
        )
    }
    private fun generarRutaFichero(intent: Intent){

        if(intent.clipData !=null){
            val clipData: ClipData = intent.clipData!!
            val count = clipData.itemCount
            mosrtarPreviewImagenSeleccionada(clipData.getItemAt(0).uri)
            for (i in 0 until count){
                val uri = clipData.getItemAt(i).uri
                rutaAbsolutaImagen.add(uri)
            }
        }else if(intent.data != null){
            rutaAbsolutaImagen.add(intent.data!!)
            mosrtarPreviewImagenSeleccionada(intent.data!!)
        }

    }
    private fun mosrtarPreviewImagenSeleccionada(uri: Uri){
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        ivHeader.setImageBitmap(bitmap)
        ivHeader.adjustViewBounds = true
        ivHeader.scaleType = ImageView.ScaleType.CENTER_CROP
    }
    private fun obtenerProductoFirebase(){
        Firebase.firestore.collection("productos").document(idProductoModificar)
            .get()
            .addOnSuccessListener {
                var producto = it.toObject(Producto::class.java)
                producto?.let { prod ->
                    productoModificar = prod
                    cargarProducto()
                }

            }
            .addOnFailureListener {

            }
    }
    private fun eliminarProductoFirebase(){
        Firebase.firestore.collection("productos").document(idProductoModificar).delete()
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Producto Eliminado", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, ListaProductosActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()

            }
    }
    private fun gestionarProducto(){
        if(TextUtils.isEmpty(etNombre.text) || TextUtils.isEmpty(etDescriptor.text) ||
            TextUtils.isEmpty(etPrecio.text)  ){
            Toast.makeText(this, "Debes de revisar los datos del formulario", Toast.LENGTH_SHORT).show()
        }else{
            var producto = Producto()
            if(esModificar){
                producto = Producto(
                    id = productoModificar.id,
                    nombre = etNombre.text.toString(),
                    descripcion = etDescriptor.text.toString(),
                    precio =  etPrecio.text.toString().toFloat(),
                    categoria = spCategoria.selectedItem.toString(),
                    imagen = productoModificar.imagen
                )
            }else{
                   producto = Producto(
                    id = UUID.randomUUID().toString(),
                    nombre = etNombre.text.toString(),
                    descripcion = etDescriptor.text.toString(),
                    precio =  etPrecio.text.toString().toFloat(),
                    categoria = spCategoria.selectedItem.toString()
                )

            }
            insertarModificarProductoFirebase(producto)


        }
    }

    private fun insertarModificarProductoFirebase(producto: Producto){

        if(rutaAbsolutaImagen.isNotEmpty()) {
            val listImagesFirestore = arrayListOf<String>()
            val progressDialog = ProgressBar(this)
            progressDialog.progress = 10
            progressDialog.isShown

            rutaAbsolutaImagen.forEachIndexed { index, it ->
                val nameFile = producto.id.plus("_".plus(index))
                listImagesFirestore.add("${Constantes.BASE_URL_GS}$nameFile")
                FirebaseStorage.getInstance().reference.child(nameFile).putFile(it)
                    .addOnSuccessListener {

                    }.addOnFailureListener {

                    }.addOnProgressListener {

                        val pro: Double =
                            100.0 * it.bytesTransferred / it.totalByteCount
                        progressDialog.progress = pro.toInt()

                    }

            }
            producto.imagen = listImagesFirestore[0]
        }

        Firebase.firestore.collection("productos")
            .document(producto.id)
            .set(producto)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Producto ${if (esModificar) "modificado" else "insertado"} ", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Error al ${if (esModificar) "modificar" else "insertar"} \" el producto", Toast.LENGTH_SHORT).show()

            }
    }
    fun onClickInsertar(view: android.view.View) {
        gestionarProducto()
    }
    fun onClickModificar(view: android.view.View) {
        gestionarProducto()
    }
    fun onClickEliminar(view: android.view.View) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setPositiveButton("SI",
                DialogInterface.OnClickListener { dialog, id ->
                    eliminarProductoFirebase()
                })
            setNegativeButton("NO",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })
        }
            .setCancelable(true)
            .setTitle("Estás seguro de eliminar el producto?")

        builder.create().show()
    }

}
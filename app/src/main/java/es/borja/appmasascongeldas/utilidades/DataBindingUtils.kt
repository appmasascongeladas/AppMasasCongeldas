package es.borja.appmasascongeldas.utilidades

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage


@BindingAdapter("cargarImagenFirestore")
fun ImageView.cargarImagenFirestore(url: String){
    FirebaseStorage.getInstance().getReferenceFromUrl(url).downloadUrl.addOnCompleteListener { task ->
        if (task.isSuccessful){
            Glide.with(context).load(task.result).into(this)
        }
    }
}

package es.borja.appmasascongeldas.adaptadores

import es.borja.appmasascongeldas.clases.Producto
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.borja.appmasascongeldas.databinding.ItemProductoBinding

class ProductoAdaptador(val clickItemProducto: ClickItemProducto): ListAdapter<Producto, ProductoAdaptador.ViewHolder>(ProductoDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickItemProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemProductoBinding) : RecyclerView.ViewHolder(binding.root){


        fun bind(item: Producto, clickItem: ClickItemProducto) {

            binding.producto = item
            binding.clickItem = clickItem
            binding.executePendingBindings()


        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemProductoBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class ProductoDiffCallback : DiffUtil.ItemCallback<Producto>() {

    override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
        return oldItem.id == newItem.id
    }


    override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
        return oldItem == newItem
    }


}

class ClickItemProducto(val clickItemProducto: (item: Producto) -> Unit) {
    fun onClick(item: Producto) = clickItemProducto(item)
}

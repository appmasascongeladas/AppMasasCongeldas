package es.borja.appmasascongeldas.clases

import es.borja.appmasascongeldas.utilidades.Constantes

data class Producto(
    var id: String = "",
    var categoria: String= "",
    var descripcion: String= "",
    var imagen: String  = Constantes.IMG_DEFAULT_PRODUCTO,
    var nombre: String= "",
    var precio: Float= 0.0F
)

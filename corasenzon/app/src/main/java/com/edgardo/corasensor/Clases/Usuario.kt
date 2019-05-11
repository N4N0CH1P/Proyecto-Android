package com.edgardo.corasensor.Clases

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Usuario(
        var userID: String,
        var nombre: String,
        var apellido: String,
        var sexo: Char,
        var fechaNacimiento: String,
        var rango: String,
        var email: String,
        var password: String): Parcelable
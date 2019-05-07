package com.edgardo.corasensor.Clases

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Presion(
        var presionID: String,
        var presionDist: Double,
        var presionSist: Double,
        var presionDistManual: Double,
        var presionSistManual: Double,
        var fechaPresion: String): Parcelable
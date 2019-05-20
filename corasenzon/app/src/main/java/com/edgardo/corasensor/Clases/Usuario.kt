/*Copyright 2019 ITESM MTY

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
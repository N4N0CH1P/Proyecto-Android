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

package com.edgardo.corasensor.activities

import com.edgardo.corasensor.Clases.Usuario
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.edgardo.corasensor.R
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileReader
import java.io.InputStream

class MyInfoAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)

        //Declaracion de variables
        var imagenCodigoQr:ImageView= findViewById(R.id.imagenCodigoQR)
        var textNombre: TextView = findViewById(R.id.textFecha)
        var textEmail: TextView = findViewById(R.id.textEmail1)
        var textSexo: TextView = findViewById(R.id.textSexo1)
        var textRango: TextView = findViewById(R.id.textRango)
        var textFechaNacimiento: TextView = findViewById(R.id.textFechaNacimiento1)
        //Ver si tenemos datos del intent
        var data = intent.extras
        if(data!=null){
            //consguir el objeto de tipo usuario
            var myUser: Usuario = data.getParcelable(MenuActivity.USER)
            var url: String="https://api.qrserver.com/v1/create-qr-code/?size=150x150&data="+myUser.userID
            //llenar la informacion
            textNombre.text=myUser.nombre+" "+myUser.apellido
            textEmail.text=myUser.email
            textSexo.text=myUser.sexo.toString()
            textRango.text=myUser.rango
            textFechaNacimiento.text=myUser.fechaNacimiento
            //Usar picaso para generar el codigo QR para ponerlo en el image view
            //cargar imagen con picaso
            Picasso.get()
                    .load(url)
                    .fit().centerCrop()
                    .error(R.mipmap.ic_launcher)
                    .into(imagenCodigoQr)
        }
    }
}
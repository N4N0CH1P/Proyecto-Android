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

import Adaptadores.PresionAdapter
import com.edgardo.corasensor.Clases.Presion
import com.edgardo.corasensor.Clases.Usuario
import com.edgardo.corasensor.networkUtility.NetworkConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.edgardo.corasensor.R
import kotlinx.android.synthetic.main.activity_historial.*
import kotlinx.android.synthetic.main.activity_informacion_paciente.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class InformacionPaciente : AppCompatActivity() {
    //Declaracion de variables
    var listaPresiones:MutableList<Presion> = mutableListOf()
    var adaptador = PresionAdapter(this, listaPresiones)
    var doctor:Usuario? = null
    var paciente:Usuario? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion_paciente)
        //Declaracion de variables
        var listViewHistorial: ListView = findViewById(R.id.listViewHistorial)
        var botonExportarExcel: Button = findViewById(R.id.botonEnviarExcel)
        var textNombrePaciente: TextView = findViewById(R.id.textNombre)
        var textFechaNacimiento: TextView = findViewById(R.id.textFecha)
        var textCorreoPaciente: TextView = findViewById(R.id.textEmail)
        var textSexoPaciente: TextView = findViewById(R.id.textSexo)
        var data = intent.extras
        listViewHistorial.adapter=adaptador
        //vemos si tenemos datos
        if(data!=null){
            //conseguimos el elemento Doctor y Paciente
            doctor = data.getParcelable(MenuActivity.USER)
            paciente = data.getParcelable(MisPacientesAct.PACIENTE)
            //llenamos la informacion del paciente
            textNombrePaciente.text=paciente!!.nombre+" "+paciente!!.apellido
            textFechaNacimiento.text=paciente!!.fechaNacimiento
            textCorreoPaciente.text=paciente!!.email
            textSexoPaciente.text=paciente!!.sexo.toString()
            //llenamos el mutable list con el historial del paciente
            getUserHistory(paciente!!)
        }
        botonExportarExcel.setOnClickListener {
            if(doctor!=null && paciente !=null){
                botonEnviarExcel.isEnabled = false
                sendUserHistory(doctor!!,paciente!!)
            }
        }
    }
    //Declaracion de la funcion para obtener el historial del usuario
    private fun getUserHistory(user:Usuario){
        //Declaracion de variables
        var parametrosPOST = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(user.email, "UTF-8")
        parametrosPOST += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(user.password, "UTF-8")
        parametrosPOST += "&" + URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(user.userID, "UTF-8")
        //Obtener el URL del servicio
        val serviceURL = NetworkConnection.buildUrl("getRegistroUser.php")
        //Crear un thear para la conexion con el servicio
        doAsync {
            //Abrir la conexion con el servicio web
            with(serviceURL.openConnection() as HttpURLConnection) {
                // La opcion default del request es GET, cabiamos a POST
                requestMethod = "POST"
                //Contruimos el body
                val body = OutputStreamWriter(getOutputStream())
                body.write(parametrosPOST)
                body.flush()
                //Leemos datos del servicio
                BufferedReader(InputStreamReader(inputStream)).use {
                    val respuestaServicio = StringBuffer()
                    var linea = it.readLine()
                    while (linea != null) {
                        respuestaServicio.append(linea)
                        linea = it.readLine()
                    }
                    //cerramos conexion con el servicio
                    it.close()
                    uiThread {
                        //convertir a objeto JSON
                        var objetoJSON: JSONArray = JSONArray(respuestaServicio.toString())
                        //llenar los datos del usuario en la app
                        fillUserData(objetoJSON)
                    }
                }
            }
        }
    }
    //Declaracion para llenar la informacion de usuario en mutable list
    private fun fillUserData(arrayJson: JSONArray){
        //iterar por los arrays JSON y meterlos en la mutable list
        for(i in 0 until arrayJson.length()){
            //Metemos el elemento
            listaPresiones.add(
                    Presion(
                            arrayJson.getJSONObject(i).getString("presionID"),
                            arrayJson.getJSONObject(i).getDouble("presionDiastolica"),
                            arrayJson.getJSONObject(i).getDouble("presionSistolica"),
                            arrayJson.getJSONObject(i).getDouble("presionSistolicaManual"),
                            arrayJson.getJSONObject(i).getDouble("presionDiastolicaManual"),
                            arrayJson.getJSONObject(i).getString("fecha")
                    )
            )
        }
        adaptador.notifyDataSetChanged()
    }
    //Declaracion de la funcion para mandar los datos por excel llamando al API
    private fun sendUserHistory(myUser:Usuario,paciente:Usuario){
        //Declaracion de variables
        var parametrosPOST = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(myUser.userID, "UTF-8")
        parametrosPOST += "&" + URLEncoder.encode("requestedUserID", "UTF-8") + "=" + URLEncoder.encode(paciente.userID, "UTF-8")
        parametrosPOST += "&" + URLEncoder.encode("userPassword", "UTF-8") + "=" + URLEncoder.encode(myUser.password, "UTF-8")
        Log.d("TEST",parametrosPOST)
        //Obtener el URL del servicio
        val serviceURL = URL("http://gato.orbi.mx/getExcel")
        //Crear un thear para la conexion con el servicio
        doAsync {
            //Abrir la conexion con el servicio web
            with(serviceURL.openConnection() as HttpURLConnection) {
                // La opcion default del request es GET, cabiamos a POST
                requestMethod = "POST"
                //Contruimos el body
                val body = OutputStreamWriter(getOutputStream())
                body.write(parametrosPOST)
                body.flush()
                //Leemos datos del servicio
                BufferedReader(InputStreamReader(inputStream)).use {
                    val respuestaServicio = StringBuffer()
                    var linea = it.readLine()
                    while (linea != null) {
                        respuestaServicio.append(linea)
                        linea = it.readLine()
                    }
                    //cerramos conexion con el servicio
                    it.close()
                    uiThread {
                        //convertir a objeto JSON
                        var objetoJSON: JSONObject = JSONObject(respuestaServicio.toString())
                        //ver si tenemos error
                        if (objetoJSON.has("error")){
                            //desplegar toast con mensaje de error del servidor
                            Toast.makeText(this@InformacionPaciente, objetoJSON.getString("error"), Toast.LENGTH_LONG).show()
                            //Habilitar el boton
                            botonEnviarExcel.isEnabled = true
                        }
                        else{
                            Toast.makeText(this@InformacionPaciente, objetoJSON.getString("success"), Toast.LENGTH_LONG).show()
                            //habilitar el boton
                            botonEnviarExcel.isEnabled = true
                        }
                    }
                }
            }
        }
    }
}
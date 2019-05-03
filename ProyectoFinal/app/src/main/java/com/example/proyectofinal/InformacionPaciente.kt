package com.example.proyectofinal

import Adaptadores.PresionAdapter
import Clases.Presion
import Clases.Usuario
import NetworkUtility.NetworkConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URLEncoder

class InformacionPaciente : AppCompatActivity() {
    //Declaracion de variables
    var listaPresiones:MutableList<Presion> = mutableListOf()
    var adaptador = PresionAdapter(this, listaPresiones)
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
            var doctor: Usuario = data.getParcelable(MainActivity.USER)
            var paciente: Usuario = data.getParcelable(MisPacientesAct.PACIENTE)
            //llenamos la informacion del paciente
            textNombrePaciente.text=paciente.nombre+" "+paciente.apellido
            textFechaNacimiento.text=paciente.fechaNacimiento
            textCorreoPaciente.text=paciente.email
            textSexoPaciente.text=paciente.sexo.toString()
            //llenamos el mutable list con el historial del paciente
            getUserHistory(paciente)
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
}

package com.edgardo.corasensor.activities

import Adaptadores.PacientesAdpater
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import com.edgardo.corasensor.Clases.GlobalUser
import com.edgardo.corasensor.Clases.Usuario
import com.edgardo.corasensor.R
import com.edgardo.corasensor.networkUtility.NetworkConnection
import kotlinx.android.synthetic.main.activity_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URLEncoder

class SaveToPatient : AppCompatActivity() {
    //Declaracion de variables
    var listaPacientes:MutableList<Usuario> = mutableListOf()
    var adaptador = PacientesAdpater(this, listaPacientes)
    var globalData = GlobalUser()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_to_patient)
        var data = intent.extras
        var listViewPacientes: ListView = findViewById(R.id.listViewP)
        listViewPacientes.adapter=adaptador

        //conseguir informacion de doctor
        if(globalData.isUserLog()&&data!=null){
            //Declaracion de variables TODO
            var myDoctor:Usuario = globalData.getData()
            var presionDist:
            getPacientesDoctor(myDoctor)
        }
        //agregar listener a la lista
        listViewPacientes.setOnItemClickListener { adapterView, view, position, id ->
            //TODO- LOGICA PARA GUARDAR TOMA DE PRESION
            savePresionToPatient()
        }
    }    //Declaracion de la funcion para obtener los pacientes del doctor
    private fun getPacientesDoctor(myDoctor:Usuario){
        //Declaracion de variables
        var parametrosPOST = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(myDoctor.email, "UTF-8")
        parametrosPOST += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(myDoctor.password, "UTF-8")
        //Obtener el URL del servicio
        val serviceURL = NetworkConnection.buildUrl("getPacientes.php")
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
                        var arrayJson: JSONArray = JSONArray(respuestaServicio.toString())
                        //agregar los elementos a la mutable list
                        addJsonToMutableList(arrayJson)
                    }
                }
            }
        }
    }
    //Declaracion de la funcion para convertir el JSON que nos llego y meterlo en la mutable list
    private fun addJsonToMutableList(arregloJson: JSONArray){
        //Iterar por los resultados y meterlos en la mutable list
        for(i in 0 until arregloJson.length()){
            listaPacientes.add(Usuario(
                    arregloJson.getJSONObject(i).getString("userID"),
                    arregloJson.getJSONObject(i).getString("nombre"),
                    arregloJson.getJSONObject(i).getString("apellido"),
                    arregloJson.getJSONObject(i).getString("sexo")[0],
                    arregloJson.getJSONObject(i).getString("fechaNacimiento"),
                    arregloJson.getJSONObject(i).getString("rango"),
                    arregloJson.getJSONObject(i).getString("email"),
                    arregloJson.getJSONObject(i).getString("password")
            ))
        }
        adaptador.notifyDataSetChanged()
    }
    private fun savePresionToPatient(){
        // TODO -Preparar los datos POST para mandar lllamar la funcion del registro
        var datosPost:String = ""
        //Llenar datos
        datosPost+= URLEncoder.encode("email", "UTF-8") + "=" +
                URLEncoder.encode(paciente!!.email, "UTF-8")+"&"
        datosPost+= URLEncoder.encode("password", "UTF-8") + "=" +
                URLEncoder.encode(paciente!!.password, "UTF-8")+"&"
        datosPost+= URLEncoder.encode("presionDist", "UTF-8") + "=" +
                URLEncoder.encode(text_pressure_diastolic.text.toString(), "UTF-8")+"&"
        datosPost+= URLEncoder.encode("presionAsist", "UTF-8") + "=" +
                URLEncoder.encode(text_pressure_systolic.text.toString(), "UTF-8")+"&"
        datosPost+= URLEncoder.encode("presionDistMan", "UTF-8") + "=" +
                URLEncoder.encode(text_diastolic_manual.text.toString(), "UTF-8")+"&"
        datosPost+= URLEncoder.encode("presionAsistMan", "UTF-8") + "=" +
                URLEncoder.encode(text_systolic_manual.text.toString(), "UTF-8")+"&"
        datosPost+= URLEncoder.encode("userID", "UTF-8") + "=" +
                URLEncoder.encode(paciente!!.userID, "UTF-8")+"&"
        datosPost+= URLEncoder.encode("presionID", "UTF-8") + "=" +
                URLEncoder.encode(text_identifier.text.toString(), "UTF-8")
        //llamar la funcion para registrar usuario
        registerPresion(datosPost)
    }
    private fun registerPresion(presionData:String){
        val serviceURL = NetworkConnection.buildUrl("uploadRegistro.php") //COMO SE LLAMA EL SERVICIO
        doAsync {
            //Abrir la conexion con el servicio web
            with(serviceURL.openConnection() as HttpURLConnection) {
                // La opcion default del request es GET, cabiamos a POST
                requestMethod = "POST"
                //Contruimos el body
                val body = OutputStreamWriter(getOutputStream())
                body.write(presionData)
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
                        var objetoJSON: JSONObject = JSONObject(respuestaServicio.toString())
                        //ver si tenemos error
                        if (objetoJSON.has("error")){
                            //desplegar toast con mensaje de error del servidor
                            Toast.makeText(this@SaveToPatient, objetoJSON.getString("error"), Toast.LENGTH_LONG).show()
                        }
                        else if(objetoJSON.has("success")){
                            Toast.makeText(this@SaveToPatient, "Presion almacenada con exito", Toast.LENGTH_LONG).show()
                        }
                        else{
                            Toast.makeText(this@SaveToPatient, "Error del servidor", Toast.LENGTH_LONG).show()
                        }
                        finish()
                    }
                }
            }
        }
    }
}

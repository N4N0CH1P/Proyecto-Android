package com.edgardo.corasensor.activities

import Adaptadores.PresionAdapter
import com.edgardo.corasensor.Clases.*
import com.edgardo.corasensor.networkUtility.NetworkConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.edgardo.corasensor.Clases.Presion
import com.edgardo.corasensor.Clases.Usuario
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

class HistorialActivity : AppCompatActivity() {
    //Declaracion de variables
    var listaPresiones:MutableList<Presion> = mutableListOf()
    var adaptador = PresionAdapter(this, listaPresiones)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)
        //Declaracion de variables
        var newUser: Usuario? = null
        var botonExcel: Button =findViewById(R.id.buttonExcel)
        //Agregar adaptador a la lista de presiones
        listViewPresion.adapter = adaptador
        val data = intent.extras
        //ver si tenemos data
        if(data!=null){
            //declaracion de variables
            newUser= data.getParcelable(MenuActivity.USER)
            //llamar al API para conseguir el JSON con todo el historial del paciente
            getUserHistory(newUser!!)
        }
        //agregar accion para el boton de enviar datos por excel
        botonExcel.setOnClickListener {
            Toast.makeText(this,"Loading...",Toast.LENGTH_SHORT).show()
            //Ver si el usuario no es nulo
            if(newUser!=null){
                botonExcel.isEnabled = false
                sendUserHistory(newUser)
            }
            else{
                //Desplegar mensaje de error en TOAST!!
                Toast.makeText(this, "No se tienen datos del usuario para enviar la información", Toast.LENGTH_LONG).show()
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
    private fun fillUserData(arrayJson:JSONArray){
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
    private fun sendUserHistory(myUser:Usuario){
        //Declaracion de variables
        buttonExcel.isEnabled = false
        var parametrosPOST = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(myUser.userID, "UTF-8")
        parametrosPOST += "&" + URLEncoder.encode("requestedUserID", "UTF-8") + "=" + URLEncoder.encode(myUser.userID, "UTF-8")
        parametrosPOST += "&" + URLEncoder.encode("userPassword", "UTF-8") + "=" + URLEncoder.encode(myUser.password, "UTF-8")
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
                        var objetoJSON:JSONObject=JSONObject(respuestaServicio.toString())
                        //ver si tenemos error
                        if (objetoJSON.has("error")){
                            //desplegar toast con mensaje de error del servidor
                            Toast.makeText(this@HistorialActivity, objetoJSON.getString("error"), Toast.LENGTH_LONG).show()
                            //Habilitar el boton
                            buttonExcel.isEnabled = true
                        }
                        else{
                            Toast.makeText(this@HistorialActivity, objetoJSON.getString("success"), Toast.LENGTH_LONG).show()
                            //habilitar el boton
                            buttonExcel.isEnabled = true
                        }
                    }
                }
            }
        }
    }

}

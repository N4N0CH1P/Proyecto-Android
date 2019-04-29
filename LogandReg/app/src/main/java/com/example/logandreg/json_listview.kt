package com.example.logandreg

import NetworkUtility.NetworkConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URLEncoder

class json_listview : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_json_listview)
    }

    private fun loginUsuario(userID:String){
        //Declaracion de variables
        var parametrosPOST = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8")
        //Obtener el URL del servicio
        val serviceURL = NetworkConnection.buildUrl("getUserData.php")
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
                        validateData(respuestaServicio.toString())
                    }
                }
            }
        }
    }

    private fun validateData(serviceResponse:String){
        //obtenemos los datos JSON del servicio para saber si fue un exito o fracaso el login
        var arregloJson: JSONArray = JSONArray(serviceResponse)
        var primerJSON: JSONObject = arregloJson.getJSONObject(0)
        //vere si tenemos un error
        if(primerJSON.has("error")){
            //Desplgar que tenemos session iniciada
            Toast.makeText(this, primerJSON.getString("success"), Toast.LENGTH_LONG).show()
        }
        else{
            Toast.makeText(this, primerJSON.getString("error"), Toast.LENGTH_LONG).show()
        }

        //val mutableList = mutableListOf<>()
       // val adapter= ArrayAdapter<String>(applicationContext, android.R.layout.activity_list_item, arregloJson)
       // for(i in mutableList){

       // }
    }




}
//hacer for que itere el arreglo de json y lo vaya metiendo al list view
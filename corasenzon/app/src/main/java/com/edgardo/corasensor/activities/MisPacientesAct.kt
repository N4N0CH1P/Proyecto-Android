package com.edgardo.corasensor.activities

import Adaptadores.PacientesAdpater
import com.edgardo.corasensor.Clases.Usuario
import com.edgardo.corasensor.networkUtility.NetworkConnection
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.widget.ListView
import android.widget.Toast
import com.edgardo.corasensor.R
import com.google.zxing.integration.android.IntentIntegrator
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URLEncoder

class MisPacientesAct : AppCompatActivity() {
    //Declaracion de variables
    var listaPacientes:MutableList<Usuario> = mutableListOf()
    var adaptador = PacientesAdpater(this, listaPacientes)
    lateinit var myDoctor:Usuario
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_pacientes)
        //Declaracion de variables
        val botonFlotante: FloatingActionButton = findViewById(R.id.botonFlotante)
        var listViewPacientes: ListView = findViewById(R.id.listViewMisPacientes)
        listViewPacientes.adapter=adaptador
        var data =intent.extras
        //Ver si tenemos datos
        if(data!=null){
            //Extraer el usuario doctor para conseguir los datos de sus pacientes
            myDoctor=data.getParcelable(MenuActivity.USER)
            //Llamar funcion para conseguir los pacientes del doctor
            getPacientesDoctor(myDoctor)
        }
        //agregar listener a la lista
        listViewPacientes.setOnItemClickListener { adapterView, view, position, id ->
            //Declaracion de variables
            var intent: Intent =Intent(this,InformacionPaciente::class.java)
            //metemos la informacion extra
            intent.putExtra(PACIENTE,listaPacientes.get(position))
            intent.putExtra(MenuActivity.USER,myDoctor)
            startActivity(intent)
        }
        //agreggar listener al boton flotante
        botonFlotante.setOnClickListener {
            //Crear Intent para el codigo QR
            IntentIntegrator(this).initiateScan()
        }
    }
    //Declaracion de la funcion para obtener los pacientes del doctor
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
    private fun addJsonToMutableList(arregloJson:JSONArray){
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
    //Lo que regresa del codigo QR
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        //ver si tenemos contenido en el result
        if(result!=null){
            //ver si el codigo qr contiene data
            if(result.contents == null){
                Toast.makeText(this, "Error, no existen datos en el codigo QR", Toast.LENGTH_SHORT).show()
            }else{
                //sacar los datos del codigo qr
                val pacienteID:String= result.contents
                //llamar servicio para meter el paciente dentro de la lista
                //Declaracion de variables
                var parametrosPOST = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(myDoctor.email, "UTF-8")
                parametrosPOST += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(myDoctor.password, "UTF-8")
                parametrosPOST += "&" + URLEncoder.encode("pacienteID", "UTF-8") + "=" + URLEncoder.encode(pacienteID, "UTF-8")
                //Obtener el URL del servicio
                val serviceURL = NetworkConnection.buildUrl("addPacienteToDoctor.php")
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
                                    Toast.makeText(this@MisPacientesAct, objetoJSON.getString("error"), Toast.LENGTH_LONG).show()
                                }
                                else{
                                    //Actualizar lista
                                    listaPacientes.clear()
                                    getPacientesDoctor(myDoctor)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    //Declaracion de companion objets
    companion object {
        val PACIENTE:String="paciente"
    }
}
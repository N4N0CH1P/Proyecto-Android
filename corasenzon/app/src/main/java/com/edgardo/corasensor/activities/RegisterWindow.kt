package com.edgardo.corasensor.activities

import com.edgardo.corasensor.networkUtility.NetworkConnection
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.edgardo.corasensor.R
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URLEncoder

class RegisterWindow : AppCompatActivity() {
    //Declaracion de variables
    lateinit var password:String
    lateinit var email:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_window)
        //Declaracion de variables
        var rango:String = "Paciente"
        var sexo:Char = 'H'
        var botonRegistro: Button = findViewById(R.id.buttonRegister)
        var arrayIds: Array<Int> = arrayOf(R.id.editNombre,R.id.editApellido,
                R.id.editEmail,R.id.editFechaNacimiento)
        var passwd1: EditText = findViewById(R.id.editPassword)
        var passwd2: EditText = findViewById(R.id.editPassword2)
        var arrayInputs: MutableList<EditText> = mutableListOf()
        var arrayLlavesPost: Array<String> = arrayOf("nombre","apellido","email","fechaNacimiento")
        for(i in 0 until arrayIds.size){ arrayInputs.add(findViewById(arrayIds[i]))}
        var spinnerRango: Spinner = findViewById(R.id.spinnerRango)
        var spinnerSexo: Spinner = findViewById(R.id.spinnerSexo)
        //Cargar los arreglos para los spinners
        var arregloSexosResumido: Array<String> = resources.getStringArray(R.array.sexos)
        var arregloRangos: Array<String> = resources.getStringArray(R.array.rangos)
        var arregloSexos: Array<String> = resources.getStringArray(R.array.sexosCompletos)
        //Cargar los adaptadores para los spinners
        spinnerRango.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arregloRangos)
        spinnerSexo.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arregloSexos)
        //agregar listener para el spinner del rango
        spinnerRango.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) { TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //Actualizar el valor del rango
                rango=arregloRangos.get(position)
            }
        }
        //agregar listener para el spinner del sexo
        spinnerRango.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) { TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //Actualizar el valor del rango
                sexo=arregloSexos.get(position).toCharArray().get(0)
            }
        }
        //listener al boton de registra
        botonRegistro.setOnClickListener {
            //Verificamos que las claves sean las mismas, en caso que no, desplegar toast de error
            if(passwd1.text.toString()==passwd2.text.toString()){
                //ver si tenemos conexion a Internet
                if(NetworkConnection.isNetworkConnected(this)){
                    //Acutalizar datos globales para mandar respuesta al main activity
                    password = passwd1.text.toString()
                    email = arrayInputs[2].text.toString()
                    //Preparar los datos POST para mandar lllamar la funcion del registro
                    var datosPost:String = ""
                    //Ciclo for para recorrer todos los parametros
                    for(i in 0 until arrayLlavesPost.size){
                        //concatenar los datos
                        datosPost += URLEncoder.encode(arrayLlavesPost[i], "UTF-8") + "=" +
                                URLEncoder.encode(arrayInputs[i].text.toString(), "UTF-8")+"&"
                    }
                    //agregar lo faltante
                    datosPost+= URLEncoder.encode("password", "UTF-8") + "=" +
                            URLEncoder.encode(passwd1.text.toString(), "UTF-8")+"&"
                    datosPost+= URLEncoder.encode("rango", "UTF-8") + "=" +
                            URLEncoder.encode(rango, "UTF-8")+"&"
                    datosPost+= URLEncoder.encode("sexo", "UTF-8") + "=" +
                            URLEncoder.encode(sexo.toString(), "UTF-8")
                    //llamar la funcion para registrar usuario
                    registerUser(datosPost)
                }
                else{
                    Toast.makeText(this, "No se encontró la conexion a Internet", Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun registerUser(userData:String){
        val serviceURL = NetworkConnection.buildUrl("register.php")
        doAsync {
            //Abrir la conexion con el servicio web
            with(serviceURL.openConnection() as HttpURLConnection) {
                // La opcion default del request es GET, cabiamos a POST
                requestMethod = "POST"
                //Contruimos el body
                val body = OutputStreamWriter(getOutputStream())
                body.write(userData)
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
                        verifyRegister(respuestaServicio.toString())
                    }
                }
            }
        }
    }
    //Funcion que verifica el registro con lo que contesta el servicio
    private fun verifyRegister(respuestaServicio:String){
        //declaracion de variables
        var objetoJsonRespuesta: JSONObject = JSONObject(respuestaServicio)
        //ver si tenemos succes
        if(objetoJsonRespuesta.has("success")){
            //regresar los datos nuevos del usuario para el login
            val returnIntent = Intent()
            returnIntent.putExtra(LoginActivity.EMAIL, email)
            returnIntent.putExtra(LoginActivity.PASSWORD,password)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
        else{
            Toast.makeText(this, objetoJsonRespuesta.getString("error"), Toast.LENGTH_LONG).show()
        }
    }
}
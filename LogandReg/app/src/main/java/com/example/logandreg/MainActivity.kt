package com.example.logandreg

import NetworkUtility.NetworkConnection
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {
    lateinit var inputEmail:EditText
    lateinit var inputPassword:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Declaracion de variables
        var botonLogin: Button = findViewById(R.id.loginButton)
        inputEmail = findViewById(R.id.editEmail)
        inputPassword = findViewById(R.id.editPassword)
        var botonRegistro: Button = findViewById(R.id.registerButton)
        //Logica para hacer loign cuando se de click al boton de login
        botonLogin.setOnClickListener {
            //Ver si tenemos conexion a internet
            if(NetworkConnection.isNetworkConnected(this)){
                //llamar la funcion para hacer login al usuario
                loginUsuario(inputEmail.text.toString(),inputPassword.text.toString());
            }
            else{
                //Desplegar toast indicando que no tenemos conexion a internet
                Toast.makeText(this, "Sin acceso a Internet!", Toast.LENGTH_LONG).show()
            }
        }
        botonRegistro.setOnClickListener {
            var intent = Intent(this,RegisterWindow::class.java)
            startActivityForResult(intent,0)
        }
    }
    private fun loginUsuario(email:String, password:String){
        //Declaracion de variables
        var parametrosPOST = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
        parametrosPOST += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")
        //Obtener el URL del servicio
        val serviceURL = NetworkConnection.buildUrl("login.php")
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
        var datosJson:JSONObject= JSONObject(serviceResponse)
        //vere si tenemos un error
        if(!datosJson.has("error")){
            //Desplgar que tenemos session iniciada
            Toast.makeText(this, datosJson.getString("success"), Toast.LENGTH_LONG).show()
        }
        else{
            Toast.makeText(this, datosJson.getString("error"), Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //ver si el request code es 0
        if(requestCode==0){
            //ver si tenemos datos
            if(data!=null){
                //Declaracion de variables
                var newEmail:String=data.getStringExtra(EMAIL)
                var newPassword:String=data.getStringExtra(PASSWORD)
                //Desplegar mensaje de success en toast
                Toast.makeText(this, "Usuario registrado con exito, ya puedes logear", Toast.LENGTH_LONG).show()
                //actualizar los campos text de las casillas
                inputEmail.setText(newEmail)
                inputPassword.setText(newPassword)

            }
        }
    }
    companion object {
        val EMAIL:String ="email"
        val PASSWORD:String = "password"
    }
}


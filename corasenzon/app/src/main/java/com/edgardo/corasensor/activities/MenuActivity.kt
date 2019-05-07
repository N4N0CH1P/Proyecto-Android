package com.edgardo.corasensor.activities


import com.edgardo.corasensor.networkUtility.NetworkConnection
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.edgardo.corasensor.Clases.Usuario
import com.edgardo.corasensor.R
import com.edgardo.corasensor.activities.HistorialActivity
import com.edgardo.corasensor.activities.LoginActivity
import com.edgardo.corasensor.activities.MisPacientesAct
import com.edgardo.corasensor.activities.MyInfoAct
import kotlinx.android.synthetic.main.activity_menu.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URLEncoder

class MenuActivity : AppCompatActivity() {
    var myCurrentUser: Usuario? = null
    lateinit var textNombreUsuario:TextView
    var email: String="test@mail.com"
    var password: String="password"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        //Declaracion de variables
        //BOTONES
        var botonSesion: Button = findViewById(R.id.botonSesion)
        var botonNuevaPresion: Button = findViewById(R.id.botonNuevaPresion)
        var botoInConsultaHistorial: Button = findViewById(R.id.botonConsultarRegistros)
        var botonMiInformacion: Button = findViewById(R.id.botonMiInformacion)
        var botonMisPacientes: Button = findViewById(R.id.botonMisPacientes)
        //TEXT VIEWS
        textNombreUsuario = findViewById(R.id.textoNombreUsuario)
        //conseguir los datos extra
        if(savedInstanceState?.get(USER)!=null){
            myCurrentUser=savedInstanceState?.getParcelable(USER)
            populateUserData()
        }

        //Obtener los datos de usuario de servicio web
        if(NetworkConnection.isNetworkConnected(this)) {
            if(myCurrentUser==null){
                getMyDataFromService()
            }
        }else{
            Toast.makeText(this, "No se tiene conexion a Internet", Toast.LENGTH_LONG).show()
        }
        //Listener para el boton de login
        botonSesion.setOnClickListener {
            //LOGIN
            if (myCurrentUser == null)
            {
                botonSesion.setText("LOGOUT")
                //Preparamos el intent
                var intent: Intent = Intent(this, LoginActivity::class.java)
                //ejecutramos el intent para un activity on result
                startActivityForResult(intent,0)
            }
            //LOGOUT
            else
            {
                botonSesion.setText("INICIAR SESION")
                myCurrentUser = null
                textoNombreUsuario.setText("No tienes sesion iniciada")
            }

        }
        //listener para el boton de historial
        botoInConsultaHistorial.setOnClickListener{
            //Ver si tenemos datos de User
            if(myCurrentUser!=null){
                //Declaracion de variables
                var intent:Intent=Intent(this, HistorialActivity::class.java)
                //agregar usuario al intent
                intent.putExtra(MenuActivity.USER,myCurrentUser)
                //INICIAR!
                startActivity(intent)
            }
            else{
                //mandar mensaje de error
                Toast.makeText(this, "No se tiene una sesion iniciada", Toast.LENGTH_LONG).show()
            }
        }
        //Listener para el boton mi informacion
        botonMiInformacion.setOnClickListener {
            //Ver si tenemos datos de User
            if(myCurrentUser!=null){
                //Declaracion de variables
                var intent:Intent=Intent(this, MyInfoAct::class.java)
                //agregar usuario al intent
                intent.putExtra(MenuActivity.USER,myCurrentUser)
                //INICIAR!
                startActivity(intent)
            }
            else{
                //mandar mensaje de error
                Toast.makeText(this, "No se tiene una sesion iniciada", Toast.LENGTH_LONG).show()
            }
        }
        //agregar listener a boton mis pacientes
        botonMisPacientes.setOnClickListener {
            //Ver si tenemos datos de User
            if(myCurrentUser!=null){
                //Declaracion de variables
                var intent:Intent=Intent(this, MisPacientesAct::class.java)
                //agregar usuario al intent
                intent.putExtra(MenuActivity.USER,myCurrentUser)
                //INICIAR!
                startActivity(intent)
            }
            else{
                //mandar mensaje de error
                Toast.makeText(this, "No se tiene una sesion iniciada", Toast.LENGTH_LONG).show()
            }
        }
        //agregar listener a boton registrar nueva presion
        botonNuevaPresion.setOnClickListener {
            var intent:Intent = Intent(this, MainActivity::class.java)
            //agregar usuario al intent
            //Checar si se ha iniciado sesion
            if (myCurrentUser == null)
            {
                intent.putExtra(MenuActivity.USER, Usuario("", "", "",
                        'M', "", "", "", ""))
                startActivity(intent)
            }
            else
            {
                intent.putExtra(MenuActivity.USER,myCurrentUser)
                startActivity(intent)
            }

        }
    }
    //Funcion para obtener los datos del usuario
    private fun getMyDataFromService(){
        //Declaracion de variables
        var parametrosPOST = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
        parametrosPOST += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")
        //Obtener el URL del servicio
        val serviceURL = NetworkConnection.buildUrl("getMyInfo.php")
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
                            Toast.makeText(this@MenuActivity, objetoJSON.getString("error"), Toast.LENGTH_LONG).show()
                        }
                        else{
                            //llenar los datos del usuario en la app
                            storeUserData(objetoJSON)
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //ver si tenemos datos
        if(data!=null){
            //REQUEST CODE 0 ES DEL LOGIN SCREEN
            if(requestCode==0){
                //conseguir el usuario y meterlo dentro de nuestro objeto
                email=data.getStringExtra(LoginActivity.EMAIL)
                password=data.getStringExtra(LoginActivity.PASSWORD)
                //llenar la informacion del usuario
                getMyDataFromService()
            }
        }
    }
    //Funcion para almacenar la informacion de usuario local
    private fun storeUserData(jsonData:JSONObject){
        myCurrentUser= Usuario(
                jsonData.getString("userID"),
                jsonData.getString("nombre"),
                jsonData.getString("apellido"),
                jsonData.getString("sexo")[0],
                jsonData.getString("fechaNacimiento"),
                jsonData.getString("rango"),
                jsonData.getString("email"),
                jsonData.getString("password"))
        //llamar funcion para llenar la informacion dentro del texto
        populateUserData()
    }
    //Funcion para llenar la informacion del usuario en el main screen
    private fun populateUserData(){
        textNombreUsuario.text=myCurrentUser!!.nombre+" "+myCurrentUser!!.apellido
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        super.onSaveInstanceState(savedInstanceState)
        if(myCurrentUser!=null){
            savedInstanceState?.putParcelable(USER,myCurrentUser)
        }
        else{
            savedInstanceState?.putParcelable(USER,null)
        }
    }
    //Declaracion del companion object
    companion object{
        val USER:String="user"
    }
}
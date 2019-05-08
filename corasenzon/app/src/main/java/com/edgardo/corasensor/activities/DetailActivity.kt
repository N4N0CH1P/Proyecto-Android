// HearAssistent
//
//Copyright (C) 2018 - ITESM
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.edgardo.corasensor.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.edgardo.corasensor.Clases.Usuario
import com.edgardo.corasensor.R
import com.edgardo.corasensor.Scan.Scan
import com.edgardo.corasensor.database.ScanDatabase
import com.edgardo.corasensor.helpers.Converters
import com.edgardo.corasensor.networkUtility.Executor
import com.edgardo.corasensor.networkUtility.NetworkConnection
import kotlinx.android.synthetic.main.activity_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URLEncoder

class DetailActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var extras: Bundle
    lateinit var instanceDatabase: ScanDatabase
    lateinit var scanRec: Scan
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        extras = intent.extras ?: return

        instanceDatabase = ScanDatabase.getInstance(this)

        scanRec = extras.getParcelable(MainActivity.SCAN_KEY)!!
        text_pressure_systolic.setText(scanRec.pressureSystolic.toString())
        text_pressure_diastolic.setText(scanRec.pressureDiastolic.toString())
        text_systolic_manual.setText("")
        text_diastolic_manual.setText("")
        text_identifier.setText(scanRec.idManual)

        graph.setImageBitmap(Converters.toBitmap(scanRec.image))
        print(scanRec.image.toString())

        disableEditText(text_pressure_systolic)
        disableEditText(text_pressure_diastolic)


        button_dont_save.setOnClickListener { onClick(it) }

        button_save.setOnClickListener { onClick(it) }
    }

    private fun onClick(view: View) {

        when (view.id) {
            R.id.button_save -> {
                if(camposLlenos())
                {
                    Executor.ioThread {
                        scanRec.pressureSystolicManual = text_systolic_manual.text.toString().toDouble()
                        scanRec.pressureDiastolicManual = text_diastolic_manual.text.toString().toDouble()
                        scanRec.idManual = text_identifier.text.toString()

                        instanceDatabase.scanDao().updateScan(scanRec)
                        runOnUiThread {
                            Toast.makeText(applicationContext, applicationContext.getString(R.string.save), Toast.LENGTH_LONG).show()
                            finish()
                        }
                    }



                    //ver si tenemos conexion a Internet

                    var email:String = ""
                    var fine = FileReader(File(this.filesDir, "user.txt"))
                    email = fine.readText()

                    var password:String = ""
                    var finp = FileReader(File(this.filesDir, "user.txt"))
                    password = finp.readText()

                    var userID:String = ""
                    var fin = FileReader(File(this.filesDir, "user.txt"))
                    userID = fin.readText()

                        //Preparar los datos POST para mandar lllamar la funcion del registro
                        var datosPost:String = ""
                        //Llenar datos
                        datosPost+= URLEncoder.encode("email", "UTF-8") + "=" +
                                URLEncoder.encode(email, "UTF-8")+"&"
                        datosPost+= URLEncoder.encode("password", "UTF-8") + "=" +
                                URLEncoder.encode(password, "UTF-8")+"&"
                        datosPost+= URLEncoder.encode("presionDist", "UTF-8") + "=" +
                                URLEncoder.encode(text_pressure_diastolic.text.toString(), "UTF-8")+"&"
                        datosPost+= URLEncoder.encode("presionAsist", "UTF-8") + "=" +
                                URLEncoder.encode(text_pressure_systolic.text.toString(), "UTF-8")+"&"
                        datosPost+= URLEncoder.encode("presionDistMan", "UTF-8") + "=" +
                                URLEncoder.encode(text_diastolic_manual.text.toString(), "UTF-8")+"&"
                        datosPost+= URLEncoder.encode("presionAsistMan", "UTF-8") + "=" +
                                URLEncoder.encode(text_systolic_manual.text.toString(), "UTF-8")+"&"
                        datosPost+= URLEncoder.encode("userID", "UTF-8") + "=" +
                                URLEncoder.encode(userID, "UTF-8")+"&"
                        datosPost+= URLEncoder.encode("presionID", "UTF-8") + "=" +
                                URLEncoder.encode(text_identifier.text.toString(), "UTF-8")
                        //llamar la funcion para registrar usuario
                        registerPresion(datosPost)

                }else{
                    Toast.makeText(applicationContext, "Hay campos sin llenar", Toast.LENGTH_LONG).show()
                }

            }
            R.id.button_dont_save -> {
                Toast.makeText(applicationContext, applicationContext.getString(R.string.dont_save), Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null
    }

        fun camposLlenos():Boolean{
            var llenos = true;

            if(text_diastolic_manual.text.toString() == "") {
                llenos = false
            }
            if(text_systolic_manual.text.toString() == "") {
                llenos = false
            }
            return llenos
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
                        var objetoJSON:JSONObject=JSONObject(respuestaServicio.toString())
                        //ver si tenemos error
                        if (objetoJSON.has("error")){
                            //desplegar toast con mensaje de error del servidor
                            Toast.makeText(this@DetailActivity, objetoJSON.getString("error"), Toast.LENGTH_LONG).show()
                        }
                        else{
                            Toast.makeText(this@DetailActivity, objetoJSON.getString("success"), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

}

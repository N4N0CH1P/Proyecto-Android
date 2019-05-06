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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
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
                        scanRec.pressureAvgManual = (text_systolic_manual.text.toString().toDouble() + (2 * text_diastolic_manual.text.toString().toDouble()))/3
                        scanRec.idManual = text_identifier.text.toString()

                        instanceDatabase.scanDao().updateScan(scanRec)
                        runOnUiThread {
                            Toast.makeText(applicationContext, applicationContext.getString(R.string.save), Toast.LENGTH_LONG).show()
                            finish()
                        }
                    }
                    /*
                    //ver si tenemos conexion a Internet
                    if(NetworkConnection.isNetworkConnected(this)){
                        //Preparar los datos POST para mandar lllamar la funcion del registro
                        var datosPost:String = ""
                        //Llenar datos
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
                    }*/
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
        val serviceURL = NetworkConnection.buildUrl("register.php") //COMO SE LLAMA EL SERVICIO
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
                        //verifyRegister(respuestaServicio.toString())
                    }
                }
            }
        }
    }
/*
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
    */
}

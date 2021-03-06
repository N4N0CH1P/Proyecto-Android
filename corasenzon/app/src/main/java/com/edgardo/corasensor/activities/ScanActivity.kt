/*Copyright 2019 ITESM MTY

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.edgardo.corasensor.activities

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextPaint
import android.util.Log
import android.view.View
import android.widget.Toast
import com.edgardo.corasensor.Clases.Usuario
import com.edgardo.corasensor.HeartAssistantApplication
import com.edgardo.corasensor.R
import com.edgardo.corasensor.Scan.Scan
import com.edgardo.corasensor.database.ScanDatabase
import com.edgardo.corasensor.helpers.Converters
import com.edgardo.corasensor.helpers.calculate
import com.edgardo.corasensor.networkUtility.BluetoothConnectionService
import com.edgardo.corasensor.networkUtility.Executor.Companion.ioThread
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_scan.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class ScanActivity : AppCompatActivity() {
    //Declarar base de datos Room
    lateinit var instanceDatabase: ScanDatabase
    //Declarar la variable para almacenar presión
    var pressureVal: Int = 0
    val _tag = "ActivityScan"
    // List of bluetooth devices
    var btDevices = ArrayList<BluetoothDevice>()
    // Selected devices
    lateinit var selectedBtDevices: BluetoothDevice

    // Bluetooth adapter
    var btAdapter: BluetoothAdapter? = null
    // Bluetooth connection
    lateinit var btConnection: BluetoothConnectionService

    lateinit var progressDialogConnection: ProgressDialog
    var firstTime = 0.0

    lateinit var series: LineGraphSeries<DataPoint>
    lateinit var viewport: Viewport

    //Declaración de variables para medidor de presión
    var time = 0
    var end_scan = false
    var lastPress = 0.0
    lateinit var time_measure: ArrayList<Long>
    lateinit var pressure: ArrayList<Double>
    lateinit var result: ArrayList<Double>
    lateinit var needle: Needle
    var runtime: Long = 0

    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    lateinit var canvass: Canvass
    lateinit var white: Canvass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        btDevices = ArrayList()
        time_measure = ArrayList()
        pressure = ArrayList()
        result = ArrayList()
        runtime = System.currentTimeMillis()

        btAdapter = BluetoothAdapter.getDefaultAdapter()
        btConnection = BluetoothConnectionService(this)

        instanceDatabase = ScanDatabase.getInstance(this)

        //Dibujar el medidor de presión
        val layout1 = findViewById<android.support.constraint.ConstraintLayout>(R.id.manometro)
        canvass = Canvass(this, 230f, false)
        white = Canvass(this, 180f, true)
        needle = Needle(this)
        var rotation = 30f
        var nameVal = 20
        for (i in 0..10) {
            val grade = Grade(this, rotation, nameVal)
            layout1.addView(grade)
            rotation = rotation + 30f
            nameVal += 20
        }
        layout1.addView(needle)
        layout1.addView(canvass)
        layout1.addView(white)

        //Mostrar la gráfica de presión sobre datos recibidos
        val graph = findViewById<View>(R.id.graph) as GraphView
        series = LineGraphSeries()
        graph.addSeries(series)
        viewport = graph.viewport
        viewport.isYAxisBoundsManual = true
        viewport.setMinX(0.0)
        viewport.setMinY(0.0)
        viewport.setMaxY(180.0)
        viewport.isScrollable = true
        viewport.isScalable = true

        //Validar que el bluetooth esté activado y si no regresar al inicio
        validateBTOn()
        val application = application
        if (application is HeartAssistantApplication) {
            val device = application.device
            val uuid = application.uuidConnection
            if (device != null && uuid != null) {
                startBTConnection(device, uuid)
            } else {
                // Back to Home
                finish()
            }
        }

        //RECIBIR AL USUARIO SI ES QUE HAY
        var data = intent.extras
        if(data!=null){
            //conseguimos el elemento Paciente
            var paciente: Usuario = data.getParcelable(MenuActivity.USER)

            if (paciente.nombre != "")
            {
                //Imprimimos la info del paciente
                Toast.makeText(this, "Sesión iniciada como " + paciente.nombre + " " +
                        paciente.apellido, Toast.LENGTH_LONG).show()
            }

        }

        button_cancel_scan.setOnClickListener { onClick(it) }
        button_finish.setOnClickListener { onClick(it) }

    }

    private fun onClick(v: View) {
        when (v.id) {
            R.id.button_cancel_scan -> {
                Log.d(_tag, "Activity finish")
                btConnection.disconnect()
                finish()
            }
            R.id.button_finish -> {
                finish_scan()
            }
        }
    }

    private fun finish_scan() {
        //Desconectar del dispositivo Bluetooth
        btConnection.disconnect()
        var finishTime = (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - runtime).toDouble()


        var count = 0
        var stable_num = 0
        var prev: Int = 0
        for (i in 0 until pressure.size -1) {
            if (count > 20) {
                stable_num = i
                break
            }
            if (pressure[i] > 100 && Math.abs(pressure[i].toInt() - prev) < 2) {
                count += 1
            } else {
                count = 0
                prev = pressure[i].toInt()
            }
        }

        val downPressure = pressure.subList(stable_num, pressure.size - 1)
        val downTime = time_measure.subList(stable_num, time_measure.size - 1)
        Log.d("StablePress", downPressure[0].toString())
        Log.d("StableTime", downTime[0].toString())



        //viewport.setMaxX(downTime[downTime.size - 1].toDouble())
        //viewport.setMinX(downTime[0].toDouble())
        val graphPhoto = findViewById<View>(R.id.graph) as GraphView
        var viewportPhoto = graph.viewport
        viewportPhoto.isYAxisBoundsManual = true
        viewportPhoto.setMinX(downTime[0].toDouble())
        viewportPhoto.setMaxX(downTime[downTime.size - 1].toDouble())
        viewportPhoto.setMinY(0.0)
        viewportPhoto.setMaxY(180.0)
        var seriesPhoto: LineGraphSeries<DataPoint>
        seriesPhoto = LineGraphSeries()

        for(i in 0 until downPressure.size){
            seriesPhoto.appendData(DataPoint(downTime[i].toDouble(), downPressure[i]), false, 600)
        }
        graphPhoto.addSeries(seriesPhoto)
        val image: ByteArray = Converters.toByteArray(graphPhoto.takeSnapshot())

        result = calculate(this, downPressure,  downTime)
        val currentDate = sdf.format(Date())
        val avg = (result[0] * 2 + result[1]) / 3
        Log.d(_tag, result.toString() )

        //El scan que se crea con los datos
        val scan = Scan(brazo = true, idManual = "", pressureAvg = avg.roundToInt().toDouble(), pressureSystolic = result[1].roundToInt().toDouble(), pressureDiastolic = result[0].roundToInt().toDouble(), scanDate = currentDate, pressureSystolicManual = result[1].roundToInt().toDouble(), pressureDiastolicManual = result[0].roundToInt().toDouble(), pressureAvgManual = avg.roundToInt().toDouble(), image = image)
        ioThread {
            val id = instanceDatabase.scanDao().insertScan(scan)
            val sc = instanceDatabase.scanDao().loadScanById(id)
            runOnUiThread() {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("SCAN_KEY", sc)
                startActivityForResult(intent, 1)
            }

        }

    }

    inner class Grade(context: Context, var rotate: Float, var name: Int) : View(context) {
        val paint = Paint()
        val textPaint = TextPaint().apply {
            textSize = 60f
            rotation = -rotate
        }

        override fun onDraw(canvas: Canvas) {
            val width = getWidth()
            val centerX = width.toFloat() / 2
            val centerY = height.toFloat() / 2
            paint.strokeWidth = 15f
            canvas.drawLine(centerX, centerY, centerX, centerY + 320, paint).apply {
                rotation = rotate
            }
        }
    }

    inner class Canvass(context: Context, var radius: Float, var white: Boolean) : View(context) {
        val paint = Paint()
        var textPaint = TextPaint().apply {
            textSize = 120f
        }

        override fun onDraw(canvas: Canvas) {
            val width = getWidth()
            val centerX = width.toFloat() / 2
            val centerY = height.toFloat() / 2
            if (white) {
                paint.setARGB(255, 250, 250, 255)
                canvas.drawCircle(centerX, centerY, this.radius, paint)
                canvas.drawText(pressureVal.toString(), centerX - 80, centerY + 40, textPaint)
            } else {
                paint.setARGB(255, 200, 200, 200)
                canvas.drawCircle(centerX, centerY, this.radius, paint)
            }
        }
    }

    inner class Needle(context: Context) : View(context) {
        val paint = Paint()
        val textPaint = TextPaint().apply { textSize = 16f }
        override fun onDraw(canvas: Canvas) {
            val width = getWidth()
            paint.setARGB(255, 255, 0, 0)
            val centerX = width.toFloat() / 2
            val centerY = height.toFloat() / 2
            val downY = centerY + 300f
            paint.strokeWidth = 20f
            canvas.drawLine(centerX, centerY, centerX, downY, paint).apply {
                rotation = 30f
            }
        }


    }

    private fun updateValue(v: View?, newVal: Float) {
        var new_rotation = newVal
        if (new_rotation > 260f) {
            new_rotation = 280f
        } else if (new_rotation < 20f) {
            new_rotation = 20f
        }
        new_rotation += 50f

        ObjectAnimator.ofFloat(v, "rotation", new_rotation).start()
    }


    private fun addEntry(tiempo: Double, presion: Double) {

        series.appendData(DataPoint(tiempo, presion), true, 300)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        finish()
    }


    /**
     * starting listening service method
     */
    private fun startBTConnection(device: BluetoothDevice, uuid: UUID) {
        Log.d(_tag, "startBTConnection: Initializing RFCOM Bluetooth Connection.")


        val scanPoints = btConnection.startClient(device, uuid)
                .map {
                    try {
                        var value = it.trim().replace("\\s".toRegex(), "").split(";")

                        if (value.size > 1) {
                            value
                        } else {
                            emptyList()
                        }
                    } catch (e: Exception) {

                        emptyList<String>()
                    }
                }
                .map {
                    try {
                        it[1].toDouble()
                        if (it[1].toDouble() > 180 || it[1].toDouble() < 10) {
                            emptyList<String>()
                            //it
                        } else {
                            it
                        }
                    } catch (e: java.lang.Exception) {
                        emptyList<String>()
                    }

                }
                .filter { !it.isEmpty() }
                .map {
                    if (it[0].isEmpty() || it[1].isEmpty()) {
                        emptyList()
                    } else {
                        it
                    }
                }
                .filter { !it.isEmpty() }
                .map {
                    ScanPoint(it[0].toDouble(), it[1].toDouble())
                }
                .observeOn(AndroidSchedulers.mainThread())


        val disposable = scanPoints.subscribe {


            updateValue(needle, it.pressure.toFloat())
            var actual = (System.currentTimeMillis() - runtime)

//            Log.d(_tag, "time ${actual}")
//            Log.d(_tag, "time ${actual} ---- value ${it.pressure}")

            addEntry(actual / 100.0, it.pressure)
            pressureVal = it.pressure.toInt()
            canvass.textPaint
            time_measure.add(actual)//it.time)
            pressure.add(it.pressure)

            if (it.pressure >= 100) {
                end_scan = true
            }

            white.invalidate()

            if (end_scan && it.pressure <= 45
            ) {
                finish_scan()
            }
        }

    }


    /**
     * Function to validate if the phone have BT and check if is turn on
     */
    private fun validateBTOn() {
        if (btAdapter == null) {
            AlertDialog.Builder(this).setMessage(
                    applicationContext.getString(R.string.device_bt_capability)
            ).setCancelable(false)
        }
        // if bluetooth is off
        if (!btAdapter!!.isEnabled) {
            val alert = AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(applicationContext.getString(R.string.msg_enable_bluetooth))
                    .setCancelable(true)
                    .setPositiveButton("Ok") { dialog, which ->
                        // Enable BT
                        val enableBT = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivity(enableBT)
                        // Notify changes on BT status
                        val btIntent = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                        registerReceiver(changeOnAction, btIntent)
                    }
                    .setNegativeButton("Cancel") { dialog, which ->
                        // Back to Home
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }

            alert.show()

        }

    }

    /**
     * Create a BroadcastReceiver for ACTION_FOUND
     * Verify if BT status has changes
     */
    private val changeOnAction = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            // When discovery finds a device
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

                when (state) {
                    BluetoothAdapter.STATE_OFF -> Log.d(_tag, "onReceive: STATE OFF")
                    BluetoothAdapter.STATE_TURNING_OFF -> Log.d(_tag, "changeOnAction: STATE TURNING OFF")
                    BluetoothAdapter.STATE_ON -> Log.d(_tag, "changeOnAction: STATE ON")
                    BluetoothAdapter.STATE_TURNING_ON -> Log.d(_tag, "changeOnAction: STATE TURNING ON")
                }
            }
        }
    }


    override fun onDestroy() {
        Log.d(_tag, "onDestroy: called.")
        super.onDestroy()
        try {
            unregisterReceiver(changeOnAction)
        } catch (e: Exception) {

        }

    }

    data class ScanPoint(val time: Double, val pressure: Double)//, val time: Double)
}
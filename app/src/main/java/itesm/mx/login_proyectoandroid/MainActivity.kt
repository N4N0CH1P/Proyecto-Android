package itesm.mx.login_proyectoandroid

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var buttonIn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonIn = findViewById(R.id.button_ingresar)

        buttonIn.setOnClickListener {
            val intent = Intent(this,DespliegaActivity::class.java)
            intent.putExtra("USUARIO", editText_usuario.text)
            intent.putExtra("PASSWORD",editText_password.text)

            startActivity(intent)
        }
    }
}

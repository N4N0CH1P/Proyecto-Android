package itesm.mx.login_proyectoandroid

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_despliega.*

class DespliegaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_despliega)

        var intent = Intent()

        if(intent.extras != null)
        {
            textView_password.text = intent.getStringExtra("PASSWORD")
            textView_usuario.text = intent.getStringExtra("USUARIO")
        }
    }
}

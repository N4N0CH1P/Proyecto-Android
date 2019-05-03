package Adaptadores

import Clases.Usuario
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.proyectofinal.R
import kotlinx.android.synthetic.main.rowpacientes.view.*

class PacientesAdpater (private val context: Context, private val listaUsuarios: MutableList<Usuario>): BaseAdapter(){
    //Delaramos el override de la funcion get view
    override fun getView(position:Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Get viewm for row item
        val rowView = inflater.inflate(R.layout.rowpacientes, parent, false)

        val usurio : Usuario = getItem(position) as Usuario

        with (rowView){
            textNombrePaciente.text=usurio.nombre+" "+usurio.apellido
            textIDPaciente.text=usurio.userID
        }
        return rowView
    }
    override fun getItem(position: Int): Any {
        return listaUsuarios.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listaUsuarios.size
    }
}
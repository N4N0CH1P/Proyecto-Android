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

package Adaptadores

import com.edgardo.corasensor.Clases.Presion
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.edgardo.corasensor.R
import kotlinx.android.synthetic.main.rowv.view.*

class PresionAdapter (private val context: Context, private val listaPresiones: MutableList<Presion>): BaseAdapter(){
    //Delaramos el override de la funcion get view
    override fun getView(position:Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Get viewm for row item
        val rowView = inflater.inflate(R.layout.rowv, parent, false)

        val presion : Presion = getItem(position) as Presion

        with (rowView){
            textFechaToma.text=presion.fechaPresion
            textPresion.text=presion.presionDist.toString()+"/"+presion.presionSist.toString()
            textPresionManual.text=presion.presionDistManual.toString()+"/"+presion.presionSistManual.toString()
        }
        return rowView
    }
    override fun getItem(position: Int): Any {
        return listaPresiones.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listaPresiones.size
    }
}
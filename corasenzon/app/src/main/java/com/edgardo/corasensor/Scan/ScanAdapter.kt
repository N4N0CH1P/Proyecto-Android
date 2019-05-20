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

package com.edgardo.corasensor.Scan

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.edgardo.corasensor.R
import kotlinx.android.synthetic.main.row.view.*

class ScanAdapter(var scans: List<Scan>,
                  var listener: ((Scan) -> Unit)?) : RecyclerView.Adapter<ScanAdapter.ScanViewHolder>() {
    lateinit var scan: Scan
    private var numberOfItems = scans.size


    override fun onCreateViewHolder(ViewGroup: ViewGroup, p1: Int): ScanViewHolder {

        val scanViewHolder: ScanViewHolder
        val rowView = LayoutInflater.from(ViewGroup.context).inflate(R.layout.row, ViewGroup, false)

        scanViewHolder = ScanViewHolder(rowView)

        return scanViewHolder
    }

    override fun getItemCount(): Int {
        return numberOfItems
    }

    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class ScanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        fun bind(index: Int) {
            scan = scans[index]

            itemView.text_date.text = "Date: " + scan.scanDate.toString()
            itemView.text_pressure.text = "Pressure: " + "${scan.pressureSystolic} / ${scan.pressureDiastolic} "
            if(scan.idManual != ""){
                itemView.text_id.text = "Id: " + scan.idManual.toString()
            }
            else{
                itemView.text_id.text = "Id: " + scan._id.toString()
            }
        }

        override fun onClick(p0: View?) {
            val scan = scans[adapterPosition]
            listener?.invoke(scan)
        }
    }
}







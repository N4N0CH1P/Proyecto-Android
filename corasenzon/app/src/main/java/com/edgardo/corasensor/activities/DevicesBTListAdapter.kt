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

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.edgardo.corasensor.R
import kotlinx.android.synthetic.main.row_devices_bt.view.*
import java.util.ArrayList

//Adapter para desplegar los dispositivos encontrados via Bluetooth
class DevicesBTListAdapter(
        context: Context,
        val resourceId: Int,
        val devices: ArrayList<BluetoothDevice>
) : ArrayAdapter<BluetoothDevice>(context, resourceId, devices) {

    var layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        convertView = layoutInflater.inflate(resourceId, null)

        val device = devices[position]


        convertView.device_name.text = device.name
        convertView.device_addr.text = device.address



        return convertView
    }

}
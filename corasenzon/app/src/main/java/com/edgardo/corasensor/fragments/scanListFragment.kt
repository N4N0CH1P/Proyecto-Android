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

package com.edgardo.corasensor.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.edgardo.corasensor.R
import com.edgardo.corasensor.Scan.Scan
import com.edgardo.corasensor.Scan.ScanAdapter

class scanListFragment : Fragment() {
    var onScanClick: ((Scan) -> Unit)? = null
        set(value) {
            field = value
            scanAdapter?.listener = value
        }

    var scans: List<Scan>
        get() = scanAdapter?.scans ?: emptyList()
        set(value) {
            scanAdapter = ScanAdapter(value, scanAdapter?.listener)

            with(view as RecyclerView) {
                adapter = scanAdapter
            }
        }

    var scanAdapter : ScanAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            scans = it.getParcelableArrayList(SCAN_ARRAY)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_scan_list, container, false)

        val scansList = scans

        if(scansList != null) {
            scanAdapter = ScanAdapter(scansList, onScanClick).apply {
                listener = onScanClick

            }
        }

        // Set the adapter
        if (rootView is RecyclerView) {
            with(rootView) {
                layoutManager = android.support.v7.widget.LinearLayoutManager(context)
                adapter = scanAdapter
            }
        }

        return rootView
    }

    companion object {
        const val SCAN_ARRAY = "scanarray"
        @JvmStatic
        fun newInstance(scans: ArrayList<Scan>) =
                scanListFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList(SCAN_ARRAY, scans)
                    }
                }
    }

}

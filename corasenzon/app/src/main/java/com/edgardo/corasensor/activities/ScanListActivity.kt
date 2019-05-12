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

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.edgardo.corasensor.R
import com.edgardo.corasensor.Scan.Scan
import com.edgardo.corasensor.database.ScanDataTest
import com.edgardo.corasensor.database.ScanDatabase
import com.edgardo.corasensor.networkUtility.Executor.Companion.ioThread
import com.edgardo.corasensor.scanData.ScanData
import kotlinx.android.synthetic.main.activity_scan_list.*

class ScanListActivity : AppCompatActivity() {

    lateinit var instanceDatabase: ScanDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_list)

        val layoutManager = LinearLayoutManager(this)
        recycler_view_list_scans.layoutManager = layoutManager


        ioThread {
            val scanNum = instanceDatabase.scanDao().getAnyScan()
            if (scanNum == 0) {
                insertScans()
            } else {
                loadAllScans()
            }
        }


    }

    // Start -> set initial data
    private fun insertScans() {
        val scan_list: List<Scan> = ScanDataTest(applicationContext).scanList
        ioThread {
            instanceDatabase.scanDao().insertScanList(scan_list)
            loadAllScans()
        }
    }

    private fun loadAllScans() {
        ioThread {
            val scan = instanceDatabase.scanDao().loadAllScan()
            scan.observe(this, Observer<List<Scan>> { scans ->

            })
        }
    }

}

interface CustomItemClickListener {
    fun onCustomItemClickListener(scan: Scan)
}

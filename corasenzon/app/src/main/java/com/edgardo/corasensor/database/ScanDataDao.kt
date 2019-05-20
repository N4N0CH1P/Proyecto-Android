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

package com.edgardo.corasensor.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.edgardo.corasensor.scanData.ScanData

@Dao
interface ScanDataDao{
    @Query("SELECT * FROM ScanData ORDER BY time")
    fun loadAllScanDatav(): LiveData<List<ScanData>>

    @Insert
    fun insertDatoList(data:List<ScanData>)

    @Query("SELECT COUNT (*) FROM ScanData")
    fun getAnyScanData(): Int

    @Insert
    fun insertScanData(data: ScanData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateScanData(data: ScanData)

    @Delete
    fun deleteScanData(data: ScanData)

    @Query("SELECT * FROM ScanData WHERE _id = :id")
    fun loadScanDataById(id: Int) : ScanData

    @Query("Select * FROM ScanData WHERE fk_scan = :id")
    fun loadScanDataByScanId(id:Int): List<ScanData>
}

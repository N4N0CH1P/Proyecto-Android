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
import com.edgardo.corasensor.Scan.Scan

@Dao
interface ScanDao {
    @Query("SELECT * FROM Scan ORDER BY _id")
    fun loadAllScan(): LiveData<List<Scan>>

    @Insert
    fun insertScanList(medicion: List<Scan>)

    @Query("SELECT COUNT (*) FROM Scan")
    fun getAnyScan(): Int

    @Insert
    fun insertScan(medicion: Scan): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateScan(medicion: Scan)

    @Delete
    fun deleteScan(medicion: Scan)

    @Query("SELECT * FROM Scan WHERE _id = :id")
    fun loadScanById(id: Long): Scan


    // TODO: Agregar query con join de la tabla de scan y dato para un scan
}

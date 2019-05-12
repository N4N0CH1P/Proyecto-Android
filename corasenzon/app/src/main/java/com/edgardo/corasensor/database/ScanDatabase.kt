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

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.edgardo.corasensor.helpers.Converters
import com.edgardo.corasensor.scanData.ScanData
import com.edgardo.corasensor.Scan.Scan


@Database(entities = [Scan::class, ScanData::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ScanDatabase : RoomDatabase() {
    abstract fun scanDao(): ScanDao
    abstract fun scanDataDao(): ScanDataDao

    companion object {
        private val DATABASE_NAME = "ScanPressureDB.db"
        private var dbInstance: ScanDatabase? = null


        @Synchronized
        fun getInstance(context: Context): ScanDatabase {
            if (dbInstance == null) {
                dbInstance = buildDatabase(context)
            }
            return dbInstance!!
        }

        private fun buildDatabase(context: Context): ScanDatabase {
            return Room.databaseBuilder(context, ScanDatabase::class.java, DATABASE_NAME).fallbackToDestructiveMigration().build()
        }
    }
}

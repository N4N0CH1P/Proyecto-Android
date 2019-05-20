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

package com.edgardo.corasensor.scanData

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import android.os.Parcel
import android.os.Parcelable
import com.edgardo.corasensor.helpers.Converters

@Entity(tableName = "ScanData")
@TypeConverters(Converters::class)

data class ScanData(@ColumnInfo(name = "Time") var time: Double?,
                    @ColumnInfo(name = "Pulse") var pulse: Double?,
                    @ColumnInfo(name = "Pressure") var pressure: Double?,
                    @ColumnInfo(name = "fk_scan") var fk_sacan: Int?
) : Parcelable {
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var _id: Int = 0

    constructor(parcel: Parcel) : this(
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Int::class.java.classLoader) as? Int) {
        _id = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(time)
        parcel.writeValue(pulse)
        parcel.writeValue(pressure)
        parcel.writeValue(fk_sacan)
        parcel.writeInt(_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScanData> {
        override fun createFromParcel(parcel: Parcel): ScanData {
            return ScanData(parcel)
        }

        override fun newArray(size: Int): Array<ScanData?> {
            return arrayOfNulls(size)
        }
    }

}
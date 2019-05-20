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

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.edgardo.corasensor.helpers.Converters

@Entity(tableName = "Scan")
@TypeConverters(Converters::class)
data class Scan(@ColumnInfo(name = "PressureScanAvg") var pressureAvg: Double?,
                @ColumnInfo(name = "PressureSystolic") var pressureSystolic: Double?,
                @ColumnInfo(name = "PressureDiastolic") var pressureDiastolic: Double?,
                @ColumnInfo(name = "PressureScanManual") var pressureAvgManual: Double? = null,
                @ColumnInfo(name = "PressureSystolicManual") var pressureSystolicManual: Double? = null,
                @ColumnInfo(name = "PressureDiastolicManual") var pressureDiastolicManual: Double? = null,
                @ColumnInfo(name = "ScanDate") var scanDate: String?,
                @ColumnInfo(name = "idManual") var idManual: String?,
                @ColumnInfo(name = "brazo") var brazo: Boolean?,
                @ColumnInfo(name = "image") var image: ByteArray?
        //True = right, False = left
) : Parcelable {
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var _id: Int = 0

    constructor(parcel: Parcel) : this(
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.createByteArray()) {

        _id = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(pressureAvg)
        parcel.writeValue(pressureSystolic)
        parcel.writeValue(pressureDiastolic)
        parcel.writeValue(pressureAvgManual)
        parcel.writeValue(pressureSystolicManual)
        parcel.writeValue(pressureDiastolicManual)
        parcel.writeString(scanDate)
        parcel.writeString(idManual)
        parcel.writeValue(brazo)
        parcel.writeByteArray(image)
        parcel.writeInt(_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Scan> {
        override fun createFromParcel(parcel: Parcel): Scan {
            return Scan(parcel)
        }

        override fun newArray(size: Int): Array<Scan?> {
            return arrayOfNulls(size)
        }
    }

}
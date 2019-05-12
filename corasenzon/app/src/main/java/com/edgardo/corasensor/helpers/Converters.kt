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

package com.edgardo.corasensor.helpers

import android.arch.persistence.room.TypeConverter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class Converters {

    companion object {
        const val DATE_FORMAT: String = "yyyy/MM/dd"

        @TypeConverter
        @JvmStatic
        fun toString(date: Date?): String? {
            val format = SimpleDateFormat(DATE_FORMAT)
            return format.format(date)
        }

        @JvmStatic
        @TypeConverter
        fun toDate(dateString: String): Date? {
            return if (dateString == null) null else SimpleDateFormat(DATE_FORMAT).parse(dateString)
        }

        @TypeConverter
        fun toByteArray(bitmap: Bitmap): ByteArray {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
            return outputStream.toByteArray()
        }

        @TypeConverter
        fun toBitmap(image: ByteArray?): Bitmap {
            return BitmapFactory.decodeByteArray(image, 0, image!!.size)
        }


    }
}
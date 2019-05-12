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

package com.edgardo.corasensor.networkUtility

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import java.io.IOException
import java.net.URL

class NetworkConnection {

    companion object {
        const val BASE_URL = "http://gato.orbi.mx/Proyecto-Android/SERVICIOS/"
        //Regresa la URL del servicio
        fun buildUrl(servicio:String): URL = URL("$BASE_URL" + servicio)

        //Funcion para leer datos del servicio web
        fun getResponseFromHttpUrl(url: URL): String =
                try {
                    //readText() does have an internal limit of 2 GB file size.
                    url.readText()
                } catch (e: IOException) {
                    e.printStackTrace()
                    throw IOException("Not connected")
                }

        /**
         * isNetworkConnected. verify if there is connectivity
         */

        //Funcion para verificar la conectividad a la red
        fun isNetworkConnected(context:Context): Boolean {
            val connectivityManager: ConnectivityManager = context.getSystemService(
                    Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

            return networkInfo?.isConnectedOrConnecting ?: false
        }

        fun isNetworkAvailable(context:Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            return if (connectivityManager is ConnectivityManager) {
                val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
                networkInfo?.isConnected ?: false
            } else false
        }

    }

}
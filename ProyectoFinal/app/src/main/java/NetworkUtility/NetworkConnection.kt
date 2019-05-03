package NetworkUtility

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
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

    }

}
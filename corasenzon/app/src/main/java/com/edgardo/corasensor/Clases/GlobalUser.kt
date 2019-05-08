package com.edgardo.corasensor.Clases

import android.app.Application
import android.R.attr.data
//Variable global para el usuario logeado
class GlobalUser() {
    companion object {
        var myUser:Usuario? = null
    }
    fun getData():Usuario{
        return myUser!!
    }
    fun isUserLog():Boolean{
        return myUser!=null
    }
    fun setUser(newUser:Usuario?){
        myUser=newUser
    }
}
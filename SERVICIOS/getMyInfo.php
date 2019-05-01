<?php
    //incluir la clase usuario
    include("config.php");
    include("class_usuario.php");
    //iniciar sesion en el servidor
    session_start();   
    //Ver si tenemos informacion del usuario
    if(isset($_SESSION["user"])){
        //Declaracion de variables
        $myCurrentUSER = $_SESSION["user"];
        $jsonString = $_SESSION["user"]->getUserDataAsJsonString();
        //regresar la informacion y despelegarla
        echo $jsonString;   
    }else{
        //Mandamos mensaje de error
        mandarMensajeError("Error, no se tiene session iniciada");
        //DIE!!!
        die();
    }
?>
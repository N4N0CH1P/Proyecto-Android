<?php
    //Iniciar session
	session_start();
	//Incluir configuracion de la base de datos
    include("config.php");
    //Incluir la clase presion
    include("class_presion.php");
	//Incluir la clase usuario
	include("class_usuario.php");
    //ver si tenemos una session inciada
    if(isset($_SESSION["user"])){
        //conseguir el user ID de los datos POST
        if(!isset($_POST["userID"])){
            mandarMensajeError("Error en los datos POST");
            die();
        }
        //Declaracion de variables
        $usuario = new Usuario(null,null,null,null,null,null,null);
        //obtener la informacion del usuario
        $usuario->populateDataFromDatabase($_POST["userID"]);
        //conseguir el historial
        $usuario->populateUserHistory();
        //desplegar el JSON
        echo $usuario->getUserHistory();
    }else{
        mandarMensajeError("Error, no se tiene session iniciada actualmente");
        die();
    }
?>
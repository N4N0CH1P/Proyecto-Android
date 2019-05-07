<?php
    //Iniciar session
	session_start();
	//Incluir configuracion de la base de datos
    include("config.php");
    //Incluir la clase presion
    include("class_presion.php");
	//Incluir la clase usuario
	include("class_usuario.php");
	//Ver si tenemos datos post
	if(!(isset($_POST["email"])&&isset($_POST["password"])&&isset($_POST["userID"]))){
        mandarMensajeError("Error, No se tienen datos post suficientes");
        die();
    }
    //ver si los datos de login son correctos
    validateUserAndPassword($_POST["email"],$_POST["password"]);
    //Declaracion de variables
    $usuario = new Usuario(null,null,null,null,null,null,null);
    //obtener la informacion del usuario
    $usuario->populateDataFromDatabase($_POST["userID"]);
    //conseguir el historial
    $usuario->populateUserHistory();
    //desplegar el JSON
    echo $usuario->getUserHistory();   
?>


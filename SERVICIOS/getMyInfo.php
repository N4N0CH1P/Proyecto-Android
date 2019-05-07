<?php
    //incluir la clase usuario
    include("config.php");
    include("class_usuario.php");  
	//Ver si tenemos datos post
	if(isset($_POST["email"])&&isset($_POST["password"])){
        $newUser=validateUserAndPassword($_POST["email"],$_POST["password"]);
        echo $newUser->getUserDataAsJsonString();
	}else{
        //Mandamos mensaje de error
        mandarMensajeError("Error, no se tiene datos POST necesarios");
        //DIE!!!
        die();
    }
?>
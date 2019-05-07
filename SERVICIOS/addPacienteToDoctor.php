<?php
    //incluir la clase usuario
    include("config.php");
    include("class_usuario.php");  
    //ver si tenemos los datos post necesarios
    //Ver si tenemos datos post
	if(isset($_POST["email"])&&isset($_POST["password"])&&isset($_POST["pacienteID"])){
        $newUser=validateUserAndPassword($_POST["email"],$_POST["password"]);
        //preparamos query para insertar los datos
        $query='INSERT atiendeA (doctorID,pacienteID) VALUES ("'.$newUser->getUserID().'","'.$_POST["pacienteID"].'")';
        //ejecutamos query
        if(!$conexionMySQL->query($query)){
           mandarMensajeError("Error, no se pudo insertar el dato en la base de datos"); 
        }
        else{
            $resultado = new \stdClass();
            $resultado->success="Paciente insertado con exito";
            //despelgarlo
            echo json_encode($resultado);	
        }
	}else{
        //Mandamos mensaje de error
        mandarMensajeError("Error, no se tiene datos POST necesarios");
        //DIE!!!
        die();
    }
?>
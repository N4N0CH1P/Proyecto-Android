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
	if(isset($_POST["email"])&&isset($_POST["password"])&&isset($_POST["userID"])){
		//preparar query
		$query='SELECT userID FROM usuario WHERE email="'.$_POST["email"].'" AND password="'.$_POST["password"].'"';
		//hacer query
		$result=$conexionMySQL->query($query);
		//ver si se pudo hacer el query
		if(!$result){
			mandarMensajeError("Error, no se pudo hacer el query a la base de datos");
			//DIE!!
			die();
		}
		//ver si tenemos un resultado dentro del query, en caso de serlo, el usuario se logeo correctamente
		if($result->num_rows==1){
			//conseguir los datos de usuario para guardarlo en la sesion
			$row=$result->fetch_assoc();
			$newUser=new Usuario(null,null,null,null,null,null,null);
			if($newUser->populateDataFromDatabase($row["userID"])){
                //Declaracion de variables
                $usuario = new Usuario(null,null,null,null,null,null,null);
                //obtener la informacion del usuario
                $usuario->populateDataFromDatabase($_POST["userID"]);
                //conseguir el historial
                $usuario->populateUserHistory();
                //desplegar el JSON
                echo $usuario->getUserHistory();	
			}
			else{
				mandarMensajeError("Error consiguiendo datos del servidor");
				die();
			}
		}
		else{
			mandarMensajeError("Error interno del servidor, problemas con base de datos");
			die();
		}
	}else{
        mandarMensajeError("Error, No se tienen datos post suficientes");
        die();
    }
?>
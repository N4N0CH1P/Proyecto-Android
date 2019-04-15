<?php
	//Iniciar session
	session_start();
	//Incluir la clase usuario
	include("class_usuario.php");
	//ver si ya tenemos una session iniciada
	if(isset($_SESSION["user"])){
		//mandar codigo de error en jsson
		$resultado = new \stdClass();
		$resultado->status="session_iniciada";
		//despelgarlo
		echo json_encode($resultado);
		//DIE!!
		die();
	}
	//Ver si tenemos datos post
	if(isset($_POST["email"])&&isset($_POST["password"])){
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
				$_SESSION["user"]=$newUser;
				//mandar JSON con mensaje de success
				$resultado = new \stdClass();
				$resultado->success="Session iniciada correctamente con el usuario: ".$_SESSION["user"]->getUserName();
				//despelgarlo
				echo json_encode($resultado);	
			}
			else{
				mandarMensajeError("Error guardando usuario en session");
				die();
			}
		}
		else{
			mandarMensajeError("Error interno del servidor, problemas con base de datos");
			die();
		}
	}
	else{
		mandarMensajeError("Error, no se tienen datos POST necesarios");
		die();
	}
	
?>

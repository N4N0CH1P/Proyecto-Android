<?php
	//funcion para mandar mensajkes de error
	function mandarMensajeError($mensajeError){
		//mandar codigo de error en jsson
		$resultado = new \stdClass();
		$resultado->error=$mensajeError;
		//despelgarlo
		echo json_encode($resultado);		
	}
	//Stat MySQL
	$conexionMySQL =  new mysqli('127.0.0.1','msva','NWsVOiAWHPD8d6JB','msva');
	//Condition if there is an MySQL error
	if($conexionMySQL->connect_error){
		mandarMensajeError("Error conectando con la base de datos: " .$conexionMySQL->connect_errno);
		die();
	}
	//Funcion para validar usuario y contrasena con base de datos
	function validateUserAndPassword($email,$password){
		global $conexionMySQL;
		//preparar query
		$query='SELECT userID FROM usuario WHERE email="'.$email.'" AND password="'.$password.'"';
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
				//regresar el usuario que se logeo
				return $newUser;
			}
			else{
				mandarMensajeError("Error guardando usuario en session");
				die();
			}
		}
		else{
			mandarMensajeError("Email o Contrasena Incorrectos");
			die();
		}
	}
?>
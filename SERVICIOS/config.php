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
?>
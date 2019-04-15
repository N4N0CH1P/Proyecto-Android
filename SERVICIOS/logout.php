<?php
	//Start the session
	session_start();
	// remove all session variables
	session_unset();
	// destroy the session
	session_destroy(); 
	//redirect to login page
	$resultado = new \stdClass();
	$resultado->success="Session cerrada con exito";
	//despelgarlo
	echo json_encode($resultado);	
?>

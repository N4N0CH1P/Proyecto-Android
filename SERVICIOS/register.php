<?php
	//Incluir la clase usuario
	include("class_usuario.php");

	//declarar el array para la informacion
	$arregloDatosPost = array("nombre","apellido","fechaNacimiento","rango","email","password","sexo");

	//Ver si tenemos los datos del post
	for($i=0 ; $i < sizeof($arregloDatosPost) ; $i++){
		if(!isset($_POST[$arregloDatosPost[$i]])){
			mandarMensajeError("No se tienen los datos POST necesarios para crear la cuenta");
			//DIE!!
			die();
		}
	}
	//preparar query
	$query='SELECT * FROM usuario WHERE email="'.$_POST["email"].'"';
	//hacer consulta
	$result=$conexionMySQL->query($query);
	//ver si tenemos resultados
	if(!$result){
		mandarMensajeError("Error interno del servidor, haciendo query a base de datos!");
		//DIE !!
		die();
	}
	//ver si la cantidad rows es igual a 0
	if(!$result->num_rows==0){
		mandarMensajeError("Error, ya existe un usuario con este correo electronico");
		//DIE !!
		die();
	}
	//crear un objeto usuario para meterlo en la base de datos
	$nuevoUsuario = new Usuario($_POST["nombre"],$_POST["apellido"],$_POST["sexo"],$_POST["fechaNacimiento"],$_POST["rango"],$_POST["email"],$_POST["password"]);
	//insertar datos en la base de datos y ver si se pudieron insertar correctamente
	if(!$nuevoUsuario->sendUserToDatabase()){
		mandarMensajeError("Error interno del servidor, insertando datos en base de datos");
		//DIE !!
		die();
	}
	else{
		//Mandar mensae de succes en JSON
		$resultado = new \stdClass();
		$resultado->success="Datos insertados con exito en la base de datos";
		//despelgarlo
		echo json_encode($resultado);	
	}
?>
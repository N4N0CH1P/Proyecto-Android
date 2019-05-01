<?php
    //incluir la clase usuario
    include("config.php");
    include("class_usuario.php");  
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
                //Desplear la informacion de usuario en JSON
                echo $newUser->getUserDataAsJsonString();
			}
			else{
				mandarMensajeError("Error obteniendo informacion del usuario");
				die();
			}
		}
		else{
			mandarMensajeError("Error interno del servidor, problemas con base de datos");
			die();
		}
	}else{
        //Mandamos mensaje de error
        mandarMensajeError("Error, no se tiene datos POST necesarios");
        //DIE!!!
        die();
    }
?>
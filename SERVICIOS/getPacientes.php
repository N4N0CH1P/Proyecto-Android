<?php
    //Incluir los archivos de configuracion
    include("config.php");
    include("class_usuario.php");  
    //Ver si tenemos los datos post Necesarios para el query
    if(isset($_POST["email"])&&isset($_POST["password"])){
        //Declaracion de variables
        $returnJsonString="[";
        //Validar los datos del usuario
        $myDoctor=validateUserAndPassword($_POST["email"],$_POST["password"]);
        //Conseguir el arreglo de los usuarios que atiende el doctor
        $arrayPacientes=$myDoctor->getPacientes();
        //Convertirlos a formato JSON
        for($i=0; $i<sizeof($arrayPacientes); $i++){
            $returnJsonString=$returnJsonString.$arrayPacientes[$i]->getUserDataAsJsonString();
            //ver si no es el ultimo
            if($i!=sizeof($arrayPacientes)-1){$returnJsonString=$returnJsonString.",";}
        }
        //regresamos el JSON
        return $returnJsonString;
    }else{
        //mandamos mensaje de error
        mandarMensajeError("Error, faltan datos POST para cumplir la conexion");
        //DIE!!!! <3
        die();
    }
?>
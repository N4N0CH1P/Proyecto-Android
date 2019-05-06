<?php
    //Incluir las clases y los archivos de configuracion
    include("config.php");
    include("class_presion.php");
    include("class_usuario.php"); 
    //Ver si tenemos parametros POST necesarios para completar la peticion
    $parametrosPost=array("email","password","presionDist","presionAsist","presionDistMan","presionAsistMan","userID","presionID");
    for($i=0; $i < sizeof($parametrosPost); $i++){
        if(!isset($parametrosPost[$i])){
            //mandar mensaje error
            mandarMensajeError("Error, no se tienen datos post necesarios");
            //DIE!!
            die();
        }
    }
    //Creamos un nuevo objeto de la clase presion para meter todos los datos
    $newPresion=new Presion($_POST["presionID"],$_POST["presionAsist"],$_POST["presionDist"],$_POST["presionAsistMan"],$_POST["presionDistMan"],getdate());
    //Creamos la instancia del usuario
    $myUser=new Usuario(null,null,null,null,null,null,null);
    $myUser->populateDataFromDatabase($_POST["userID"]);
    //subimos la informacion al servidor
    $myUser->uploadNewPresionToDb($newPresion);
?>
<?php
    //Declaracion de la clase Presion
    class Presion{
        //Declaracion de los atributos
        var $presionID;
        var $presionSistolica;
        var $presionDiastolica;
        var $presionSistolicaManual;
        var $presionDiastolicaManual;
        //Declaracion del constructor default
        function __construct($presionID,$presionSistolica,$presionDiastolica,$presionSistolicaManual,$presionDiastolicaManual){
            $this->presionID=$presionID;
            $this->presionSistolica=$presionSistolica;
            $this->presionDiastolica=$presionDiastolica;
            $this->presionSistolicaManual=$presionSistolicaManual;
            $this->presionDiastolicaManual=$presionDiastolicaManual;
        }
        //metodo para parsear a json los datos
        function getJsonString(){
            //construir el objeto json
            $resultado = new \stdClass();
            $resultado->presionID=$this->presionID;
            $resultado->presionSistolica=$this->presionSistolica;
            $resultado->presionDiastolica=$this->presionDiastolica;
            $resultado->presionSistolicaManual=$this->presionSistolicaManual;
            $resultado->presionDiastolicaManual=$this->presionDiastolicaManual;
            //regresar el string del objeto JSON
            return json_encode($resultado);
        }
    }
?>
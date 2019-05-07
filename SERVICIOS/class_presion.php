<?php
    //Declaracion de la clase Presion
    class Presion{
        //Declaracion de los atributos
        var $presionID;
        var $presionSistolica;
        var $presionDiastolica;
        var $presionSistolicaManual;
        var $presionDiastolicaManual;
        var $fechaPresion;
        //Declaracion del constructor default
        function __construct($presionID,$presionSistolica,$presionDiastolica,$presionSistolicaManual,$presionDiastolicaManual,$fechaPresion){
            $this->presionID=$presionID;
            $this->presionSistolica=$presionSistolica;
            $this->presionDiastolica=$presionDiastolica;
            $this->presionSistolicaManual=$presionSistolicaManual;
            $this->presionDiastolicaManual=$presionDiastolicaManual;
            $this->fechaPresion=$fechaPresion;
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
            $resultado->fecha=$this->fechaPresion;
            //regresar el string del objeto JSON
            return json_encode($resultado);
        }
    }
?>
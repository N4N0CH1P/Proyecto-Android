import MySQLdb
import mysql_config
from excelapi import sendErrorMssg

#Funcion para validar usuario
def validateUser(userID, userPassword):
    #prepara query
    query="SELECT password FROM usuario WHERE userID='"+userID+"'"
    #Iniciamos el cursor dentro de nuestra base de datos
    cursor=db.cursor()
    #hacer query
    try:
        cursor.execute(query)
        row=cursor.fetchone()
        #Ver si las claves estan correctas
        if(row["password"]!=userPassword):
            print("Las claves no son validas para el usuario " + userID)
            return sendErrorMssg("Error, las claves no son correctas")
        #Regresar success
        returnJson={"success","yes"}
        return returnJson
    except:
        print("Error verificando usuario y contrasenia, problemas con conexion a base de datos")
        return sendErrorMssg("Error haciendo query a la base de datos")

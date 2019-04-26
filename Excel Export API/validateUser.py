import mysql_config as mysql

#Funcion para validar usuario
def validateUser(userID, userPassword):
    print("Validando usuario con la base de datos....")
    #Pedir al servidor mysql los datos
    data=mysql.fetchDataFromDatabase("SELECT password FROM usuario WHERE userID='"+userID+"'")
    row=mysql.getFirstElement(data)
    #Ver si las claves estan correctas
    if(row[0]!=userPassword):
        print("Las claves no son validas para el usuario " + userID)
        return mysql.sendErrorMssg("Error, las claves no son correctas")
    #Regresar success
    print("Usuario validado con exito!")
    returnJson={"success","yes"}
    return returnJson

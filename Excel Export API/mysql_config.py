import MySQLdb
#DECLARACION DE VARIABLES PARA LA BASE DE DATOS
direcionIP="localhost"
dbUsername="victor"
dbPassword="password"
databaseName="msva"

#Establecer la conexion con la base de datos
db = MySQLdb.connect(direcionIP,dbUsername,dbPassword,databaseName)

#funcion para regresar el primer valor
def getFirstElement(list,default=None):
    if list:
        for item in list:
            return item
    return default

#Funcion para mandar mensaje de error
def sendErrorMssg(mssg):
    returnJson = {"error":mssg}
    return returnJson

#Funcion para obtener informacion de la base de datos
def fetchDataFromDatabase(query):
    #Iniciamos el cursor dentro de nuestra base de datos
    cursor=db.cursor()
    #TRY CATCH BLOCK
    try:
        cursor.execute(query)
        data = cursor.fetchall()
        return data
    except:
        print("Error haciendo query a base de datos")
        return None

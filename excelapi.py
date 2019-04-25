import xlsxwriter
import MySQLdb
#DECLARACION DE VARIABLES PARA LA BASE DE DATOS
direcionIP="localhost"
dbUsername="victor"
dbPassword="password"
databaseName="msva"

#Establecer la conexion con la base de datos
db = MySQLdb.connect(direcionIP,dbUsername,dbPassword,databaseName)

#Funcion para mandar mensaje de error
def sendErrorMssg(mssg):
    #TODO-Mandar mensaje de error en JSON
    #Desplegar mensaje de error
    print("Error interno del servidor") 

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
        sendErrorMssg("Error haciendo query a base de datos")
        return False

#Funcion para agregar la informacion de paciente en el archivo excel
def addHeaderToExcelFile(worksheet,userID):
    #llamar base de datos para conseguir la informacion del paciente
    data=fetchDataFromDatabase("SELECT * FROM usuario WHERE userID='"+userID+"'")
    worksheet.write(0,0,"User ID")
    worksheet.write(1,0,row[0])
    worksheet.write(0,1,"Nombre Paciente")
    worksheet.write(1,1,row[1]+" "+row[2])
    worksheet.write(0,2,"Sexo")
    worksheet.write(1,2,row[3])
    worksheet.write(0,3,"Fecha de nacimiento")
    worksheet.write(1,3,row[4])
    worksheet.write(3,0,"Presion Diastolica")
    worksheet.write(3,1,"Presion Sistolica")

#Declaracion de la funcion que regresara un archivo excel con los registros del usuario
def getUserDataToExml(userID):
    #Creamos el archivo de excel
    workbook = xlsxwriter.Workbook(userID+'.xlsx')
    worksheet = workbook.add_worksheet()

    #Preparamos el query en SQL para obtener el historial del usuario
    query="SELECT * FROM preison WHERE pacienteID='"+userID+"'"
    #try catch block para ver si tenemos un error durante el query
    try:
        cursor.execute(query)
        row = cursor.fetchone()
        #Metemos primero la informacion de paciente al archivo de excel
        addHeaderToExcelFile(worksheet,userID)
        cont = 4
        #Ciclo for para iterar por los resultados y meterlos dentro del archivo de excel
        while row is not None:
            #aregar los datos a las celdas de excel
            worksheet.write(cont,0,row[9])
            worksheet.write(cont,1,row[10])
            cont+=1
            #Conseeguir el nuevo renglon
            row = cursor.fetchone()
        #Cerramos el archivo
        workbook.close()
    except:

getUserDataToExml("14d62206-65df-11e9-9a2d-b827eb7fd899")
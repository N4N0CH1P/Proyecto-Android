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
    #Declaracion de variables
    arregloDatosHeader=["User ID", "Nombre" , "Apellido" , "Sexo" , "Fecha Nacimiento" , "Rango" , "Email"]
    #llamar base de datos para conseguir la informacion del paciente
    data=fetchDataFromDatabase("SELECT * FROM usuario WHERE userID='"+userID+"'")
    row=data[0]
    #Ciclo for para meter todo al woksheet
    for i in range(0,len(arregloDatosHeader)):
        worksheet.write(0,i,arregloDatosHeader[i])
        worksheet.write(1,i,row[i])
    #regresar el nuevo worksheet
    return worksheet

#Funcion para cargar los registros dentro del worksheet
def loadUserDataIntoWorksheet(worksheet,userID):
    #conseguir la informacion de la base de datos
    data=fetchDataFromDatabase("SELECT * FROM presion WHERE pacienteID='"+userID+"'")
    headerExcel=["ID","Presion Distolica","Presion Asistolica","Presion Distolica Manual","Presion Asistolica Manual"]
    #agregar header
    for i in range(0,len(headerExcel)):
        worksheet.write(3,i,headerExcel[i])
    #Declaracion del contador
    cont=4
    #CIclo for para iterar por lo que coneguimos
    for row in data:
        for i in range(0,len(row)-1):
            worksheet.write(cont,i,row[i])
    return worksheet

#Declaracion de la funcion que regresara un archivo excel con los registros del usuario
def getUserDataToExml(userID):
    #Creamos el archivo de excel
    workbook = xlsxwriter.Workbook(userID+'.xlsx')
    worksheet = workbook.add_worksheet()
    #agregar header al archivo de excel
    worksheet=addHeaderToExcelFile(worksheet,userID)
    #agregar el historial de los pacientes
    worksheet=loadUserDataIntoWorksheet(worksheet,userID)
    #cerramos el archivo de excel
    workbook.close()

getUserDataToExml("14d62206-65df-11e9-9a2d-b827eb7fd899")
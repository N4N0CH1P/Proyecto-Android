import xlsxwriter
import mysql_config as mysql

#Funcion para agregar la informacion de paciente en el archivo excel
def addHeaderToExcelFile(worksheet,userID):
    #Declaracion de variables
    arregloDatosHeader=["User ID", "Nombre" , "Apellido" , "Sexo" , "Fecha Nacimiento" , "Rango" , "Email"]
    #llamar base de datos para conseguir la informacion del paciente
    data=mysql.fetchDataFromDatabase("SELECT * FROM usuario WHERE userID='"+userID+"'")
    #ver si tenemos datos
    if(data):
        row=data[0]
        #Ciclo for para meter todo al woksheet
        for i in range(0,len(arregloDatosHeader)):
            worksheet.write(0,i,arregloDatosHeader[i])
            worksheet.write(1,i,row[i])
        #regresar el nuevo worksheet
        return worksheet
    else:
        return False

#Funcion para cargar los registros dentro del worksheet
def loadUserDataIntoWorksheet(worksheet,userID):
    #conseguir la informacion de la base de datos
    data=mysql.fetchDataFromDatabase("SELECT * FROM presion WHERE pacienteID='"+userID+"'")
    #Ver si tenemos data
    if(data):
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
            cont+=1
        return worksheet
    else:   
        return False

#Declaracion de la funcion que regresara un archivo excel con los registros del usuario
def getUserDataToExml(userID):
    print("Generando archivo XLSX para usuario " + userID)
    #Creamos el archivo de excel
    workbook = xlsxwriter.Workbook(userID+'.xlsx')
    worksheet = workbook.add_worksheet()
    print("Agregando Header con la informacion del paciente...")
    #agregar header al archivo de excel
    worksheet=addHeaderToExcelFile(worksheet,userID)
    if(worksheet==False):return mysql.sendErrorMssg("Error creando el header del archivo")
    #agregar el historial de los pacientes
    print("Agregando historial del paciente...")
    worksheet=loadUserDataIntoWorksheet(worksheet,userID)
    if(worksheet==False):return mysql.sendErrorMssg("Error insertando el historial del paciente")
    #cerramos el archivo de excel
    print("Success!!...")
    workbook.close()
    print("Guardado en disco!")
    successJson={"success":"yes"}
    return successJson
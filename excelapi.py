import xlsxwriter
import MySQLdb
#DECLARACION DE VARIABLES PARA LA BASE DE DATOS
direcionIP="localhost"
dbUsername="victor"
dbPassword="password"
databaseName="msva"

#Establecer la conexion con la base de datos
db = MySQLdb.connect(direcionIP,dbUsername,dbPassword,databaseName)

#Declaracion de la funcion que regresara un archivo excel con los registros del usuario
def getUserDataToExml(userID):
    #Creamos el archivo de excel
    workbook = xlsxwriter.Workbook(userID+'.xlsx')
    worksheet = workbook.add_worksheet()
    #Iniciamos el cursor dentro de nuestra base de datos
    cursor=db.cursor()
    #Preparamos el query en SQL para obtener el historial del usuario
    query="SELECT * FROM usuario U JOIN presion P ON U.userID=P.pacienteID WHERE U.userID='"+userID+"'"
    #try catch block para ver si tenemos un error durante el query
    try:
        cursor.execute(query)
        row = cursor.fetchone()
        #Metemos primero la informacion de paciente al archivo de excel
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
        #TODO-Mandar mensaje de error en JSON
        #Desplegar mensaje de error
        print("Error interno del servidor")
getUserDataToExml("14d62206-65df-11e9-9a2d-b827eb7fd899")
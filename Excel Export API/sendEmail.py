import smtplib
import mysql_config as mysql
from email.MIMEMultipart import MIMEMultipart
from email.MIMEText import MIMEText
from email.MIMEBase import MIMEBase
from email import Encoders
import os


#Funcion para obtener la direccion de correo electronico de un userID
def getUserEmailAddress(userID):
    print("Conectando con base de datos para obtener email destinatario...")
    #Conseguir el correo del usuario
    data=mysql.fetchDataFromDatabase("SELECT email FROM usuario WHERE userID='"+userID+"'")
    #Ver si tenemos datos
    if data:
        row=mysql.getFirstElement(data)
        print("Success!!")
        return row[0]
    else:
        print("ERROR!!!")
        return None

def sendEmailToUser(userIDData,userIDDest):
    #Declaracion de informacion del servidor
    emailServerAddressSMTP="mail.orbi.mx"
    SMTPPort=25
    smtpUsername="msva@orbi.mx"
    smtpPassword="msvaemail"
    #Declaracion de variables
    server = smtplib.SMTP(emailServerAddressSMTP, SMTPPort)
    #Logear en el servidor
    server.ehlo()
    server.starttls()
    server.ehlo()
    server.login(smtpUsername, smtpPassword)
    #Conseguir el correo del usuario para obtener la direccion
    mail=getUserEmailAddress(userIDDest)
    if(mail):
        print("Preparando correo electronico...")
        #preparar el correo
        msg = MIMEMultipart()
        msg['From'] = smtpUsername
        msg['To'] = mail
        msg['Subject'] = "Historial tomas de presion para el usuario " + userIDData
        body = "Se anexa el archivo excel con el historial del usuario " + userIDData
        msg.attach(MIMEText(body, 'plain'))
        print("Success!!")
        print("Agregando archivo "+userIDData+".xlsx a correo electronico")
        #Agregamos el archivo
        part = MIMEBase('application', "octet-stream")
        part.set_payload(open(userIDData+".xlsx", "rb").read())
        Encoders.encode_base64(part)
        part.add_header('Content-Disposition', 'attachment; filename="'+userIDData+'.xlsx"')
        msg.attach(part)
        text = msg.as_string()
        print("Success!!")
        print("Enviando email...")
        server.sendmail(smtpUsername, mail, text)
        print("Correo enviado con exito a "+mail)
        #Eliminar el arhcivo
        print("Eliminando archivo cache")
        os.remove(userIDData+".xlsx")
        print("Success!!")
        resultJson={"success":"yes"}
        return resultJson
    else:
        return mysql.sendErrorMssg("Error consiguiendo el correo electronico del usuario")
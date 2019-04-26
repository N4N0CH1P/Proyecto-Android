import smtplib
import mysql_config as mysql
from email.MIMEMultipart import MIMEMultipart
from email.MIMEText import MIMEText

#Declaracion de informacion del servidor
emailServerAddressSMTP="smtp.gmail.com"
SMTPPort=587
smtpUsername="msva@orbi.mx"
smtpPassword="msvaemail"

#Declaracion de variables
server = smtplib.SMTP(emailServerAddressSMTP, SMTPPort)
#Logear en el servidor
server.ehlo()
server.starttls()
server.ehlo()
server.login(smtpUsername, smtpPassword)


#Funcion para obtener la direccion de correo electronico de un userID
def getUserEmailAddress(userID):
    #Conseguir el correo del usuario
    data=mysql.fetchDataFromDatabase("SELECT email FROM usuario WHERE userID='"+userID+"'")
    #Ver si tenemos datos
    if data:
        row=mysql.getFirstElement(data)
        if "email" in row:
            return row["email"]
        else:
            return None
    else:
        return None

def sendEmailToUser(userIDData,userIDDest):
    #Conseguir el correo del usuario para obtener la direccion
    mail=getUserEmailAddress(userIDDest)
    if(mail):
        #preparar el correo
        msg = MIMEMultipart()
        msg['From'] = smtpUsername
        msg['To'] = mail
        msg['Subject'] = "Python email"
        body = "Python test mail"
        msg.attach(MIMEText(body, 'plain'))
        text = msg.as_string()
        server.sendmail(smtpUsername, mail, text)
    else:
        return mysql.sendErrorMssg("Error consiguiendo el correo electronico del usuario")
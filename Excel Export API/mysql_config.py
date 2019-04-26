#DECLARACION DE VARIABLES PARA LA BASE DE DATOS
direcionIP="localhost"
dbUsername="victor"
dbPassword="password"
databaseName="msva"

#Establecer la conexion con la base de datos
db = MySQLdb.connect(direcionIP,dbUsername,dbPassword,databaseName)
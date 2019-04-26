from flask import Flask,jsonify
from excelapi import getUserDataToExml
from excelapi import sendErrorMssg
from validateUser import validateUser
from sendEmail import sendEmailToUser
from flask_cors import CORS
from flask import request

app = Flask(__name__)
CORS(app)
@app.route("/",methods=["GET","POST"])
def summary():
    try:
        if (request.method == "POST"):
            #Conseguir los parametros POST
            requestedUserID = request.form["requestedUserID"]
            userID = request.form['userID']
            userPassword = request.form['userPassword']
            #Validar que tenga una cuenta en la base de datos
            d = validateUser(userID,userPassword)
            if 'error' in d:
                return jsonify(d)
            d = getUserDataToExml(requestedUserID)
            #Ver si ocurrio un error
            if 'error' in d:
                return jsonify(d)
            #llamar funcion para enviar el correo con el archivo
            d=sendEmailToUser(requestedUserID,userID)
            return jsonify(d)
        else:
            return jsonify(sendErrorMssg("Error en los parametros POST"))
    except Exception as e:
        print(e)
        return jsonify(sendErrorMssg("Error interno de servidor"))

if __name__ == "__main__":
    app.run(host="192.168.15.15")
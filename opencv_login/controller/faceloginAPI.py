# -*- coding:utf-8 -*-
from flask import Flask, request

from service.face_create_image_train import face_train

application = Flask(__name__)

@application.route("/")
def hello():
    return "<h1> 파이썬!! </h1>"

@application.route("/faceloginAPI", methods=['POST', 'GET'])
def faceAnalysis():

    print('hello')
    user_id = "test"
    res = face_train(user_id)

    return res

if __name__ == "__main__":
    application.run(host="0.0.0.0", port=8000)
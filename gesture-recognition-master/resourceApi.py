import cv2
from flask import Flask, request
from tensorflow.keras.models import load_model
import mediapipe as mp
import urllib.request
import checkHandMove as t


app  = Flask(__name__)

@app.route('/')
def hello():
    return "<h1>유상인</h1>"

@app.route('/stopWatchProc')
def checkHand():
    print("stopWatch Start")
    status = str(request.args.get("status"))
    print("status : " + status)
    if status == "a":
        print("모션인식 시작")
        t.doProcMotion(status)
        res = "<h1>성공<h1>"
    elif status == "b":
        t.doProcMotion(status)
        print("모션인식 종료")
        res = "<h1>종료<h1>"
    print("stopWatch End")
    return res
# 호출 받기 위한 형식 지정 모두 0으로 해야지 외부에서 접근가능
# port 번호는 안겹치게 확인 할 것
if __name__ == "__main__":
    debeug = True
    app.run(host="0.0.0.0", port=5005)
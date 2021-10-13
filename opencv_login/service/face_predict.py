import cv2

# 학습된 얼굴 정면검출기 사용하기
face_cascade = cv2.CascadeClassifier("../data/haarcascade_frontalface_alt2.xml")

# 카메라로부터 이미지 가져오기
vcp = cv2.VideoCapture(0, cv2.CAP_DSHOW)

# LBPH 알고리즘 선언
model = cv2.face.LBPHFaceRecognizer_create()

# 학습 모델 가져오기
model.read("../model/test-trainner.yml")

# 카메라로부터 이미지 가져오기
while True:
    ret, my_image = vcp.read()

    if ret is True:
        # 동영상의 프레임을 얼굴인식율을 높이기 위해 흑백으로 변경함
        gray = cv2.cvtColor(my_image, cv2.COLOR_BGR2GRAY)

        # 변환한 흑백사진으로부터 히스토그램 평활화
        gray = cv2.equalizeHist(gray)

        # 얼굴 인식하기
        faces = face_cascade.detectMultiScale(gray, 1.5, 5, 0, (20, 20))

        facesCnt = len(faces)

        if facesCnt == 1:

            x, y, w, h = faces[0]

            face_image = gray[y:y + h, x:x + w]

            # 유사도 분석
            id_, res = model.predict(face_image)
            # 예측 결과 문자열
            result = "result : " + str(res) + "%"
            # 예측결과 문자열 사진에 추가하기
            cv2.putText(my_image, result, (x, y - 15), 0, 1, (255, 0, 0), 2)
            # 얼굴 검출 사각형 그리기
            cv2.rectangle(my_image, faces[0], (255, 0, 0), 4)

        # 사이즈 변경된 이미지로 출력
        cv2.imshow("predict_my_face", my_image)

    if cv2.waitKey(1) > 0:
        break

vcp.release()

cv2.destroyAllWindows()

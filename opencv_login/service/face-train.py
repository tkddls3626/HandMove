import cv2
import numpy as np

# 학습된 얼굴 정면검출기 사용하기
face_cascade = cv2.CascadeClassifier("../data/haarcascade_frontalface_alt2.xml")

vcp = cv2.VideoCapture(0, cv2.CAP_DSHOW)

model = cv2.face.LBPHFaceRecognizer_create()

training_data, labels = [], []

count = 0

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

            count += 1

            x, y, w, h = faces[0]

            face_image = gray[y:y + h, x:x + w]

            training_data.append(face_image)
            labels.append(count)

            print(training_data)
            print(labels)

            model.train(training_data, np.array(labels))

            model.save("../model/face-trainner.yml")

            # 얼굴 검출 사각형 그리기
            cv2.rectangle(my_image, faces[0], (255, 0, 0), 4)

        cv2.imshow("train_my_face", my_image)

    if cv2.waitKey(1) == 13 or count == 100:
        break

vcp.release()

cv2.destroyAllWindows()
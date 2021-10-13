import cv2

# 학습된 얼굴 정면검출기 사용하기
face_cascade = cv2.CascadeClassifier("../data/haarcascade_frontalface_alt2.xml")

# 카메라 캡처
vcp = cv2.VideoCapture(0, cv2.CAP_DSHOW)

count = 0

# 얼굴 이미지 학습 횟수가 100번 될때까지 while문을 계속 반복함
while True:
    ret, my_image = vcp.read()

    # 카메라로부터 프레임(이미지)를 잘 받았으면 실행함
    if ret is True:

        # 카메라의 프레임을 얼굴인식율을 높이기 위해 흑백으로 변경함
        gray = cv2.cvtColor(my_image, cv2.COLOR_BGR2GRAY)

        # 변환된 흑백사진으로부터 히스토그램 평활화
        gray = cv2.equalizeHist(gray)

        # 얼굴 인식하기
        faces = face_cascade.detectMultiScale(gray, 1.1, 5, 0, (20, 20))

        faceCnt = len(faces)
        cv2.imshow("train_my_face", my_image)
        if faceCnt == 1:
            count += 1
            cv2.imwrite("../my_face/" + str(count) + ".jpg", my_image)

    if cv2.waitKey(1) == 13 or count == 100:
        break

vcp.release()

cv2.destroyAllWindows()

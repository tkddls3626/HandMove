import cv2
import numpy as np
import pathlib

def face_train(user_id):

    try:
        # 학습된 얼굴 정면검출기 사용하기
        face_cascade = cv2.CascadeClassifier("../data/haarcascade_frontalface_alt2.xml")

        # LBPH 알고리즘 선언
        model = cv2.face.LBPHFaceRecognizer_create()

        # 학습할 데이터구조 선언(학습데이터, 라벨링)
        training_data, labels = [], []
        count = 0

        # 학습시킬 이미지 파일 경로
        data_dir = pathlib.Path("C:/faceimg/"+user_id)
        print("data_dir : ", data_dir)

        # 학습데이터 수 출력하기
        image_count = len(list(data_dir.glob("*.jpeg")))
        print("image_count : ", image_count)

        # 얼굴 이미지 수만큼 학습하기 위해 반복하기
        for image_path in data_dir.glob("*.jpeg"):

            # 폴더 내 이미지 모두 가져와서 학습시키기
            my_image = cv2.imread(str(image_path), cv2.IMREAD_COLOR)

            # 카메라의 프레임을 얼굴인식율을 높이기 위해 흑백으로 변경함
            gray = cv2.cvtColor(my_image, cv2.COLOR_BGR2GRAY)

            # 변환된 흑백사진으로부터 히스토그램 평활화
            gray = cv2.equalizeHist(gray)

            # 얼굴 인식하기
            faces = face_cascade.detectMultiScale(gray, 1.1, 5, 0, (20, 20))

            # 인식된 얼굴의 수
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

                model.save("../model/"+user_id+"-trainner.yml")

                # 얼굴 검출 사각형 그리기
                cv2.rectangle(my_image, faces[0], (255, 0, 0), 4)

        cv2.destroyAllWindows()
        res = "success"
    except:
        print("error!! image_train fail")
        res = "fail"
    return res
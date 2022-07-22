import cv2
import mediapipe as mp
import numpy as np
from tensorflow.keras.models import load_model
import requests

# 저장된 액션을 검출할수있게 선언 ok, vi
actions = ['ok','vi','vi','ok']
# 윈도우 사이즈
seq_length = 30

# 손동작인식 모델을 로드해옴
model = load_model('models/model2_1.0.h5')

# 미디어 파이프 객체 선언과 초기화를 간단하게 구조함
mp_hands = mp.solutions.hands
mp_drawing = mp.solutions.drawing_utils
hands = mp_hands.Hands(
    max_num_hands=1,
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5)

def sendSign(url):
    rep = requests.get(url=url)
    return rep

def doProcMotion(status):
    # 나의 웹캠을 화면에 보여줌
    cap = cv2.VideoCapture(0, cv2.CAP_DSHOW)
    seq = []
    action_seq = []

    while cap.isOpened():
        # 웹캠에 순차적으로 n번째 동작을 읽어온다
        ret, img = cap.read()
        img0 = img.copy()
        # 이미지를 읽어 들이고, 보기편하게 이미지를 좌우 반전한다.
        img = cv2.flip(img, 1)
        # RGB이미지를 RGB로 변환한다.
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        # 변경된 이미지를 결과같에 넣는다.
        result = hands.process(img)
        # 기존의 색상으로 돌아오게한다.
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        # 각도를 사용하여 이미지를 뽑아낸다
        if result.multi_hand_landmarks is not None:
            for res in result.multi_hand_landmarks:
                joint = np.zeros((21, 4))
                for j, lm in enumerate(res.landmark):
                    joint[j] = [lm.x, lm.y, lm.z, lm.visibility]

                # 관절사이의 각도 계산
                v1 = joint[[0,1,2,3,0,5,6,7,0,9,10,11,0,13,14,15,0,17,18,19], :3] # Parent joint
                v2 = joint[[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20], :3] # Child joint
                v = v2 - v1 # [20, 3]
                # v = 정규화시키기
                v = v / np.linalg.norm(v, axis=1)[:, np.newaxis]

                # 각도 구하기
                angle = np.arccos(np.einsum('nt,nt->n',
                    v[[0,1,2,4,5,6,8,9,10,12,13,14,16,17,18],:],
                    v[[1,2,3,5,6,7,9,10,11,13,14,15,17,18,19],:])) # [15,]

                angle = np.degrees(angle) # Convert radian to degree
                # x,y,z,visibility 변수들을 joint.flatten을 사용하여 횡열을 만든다.
                d = np.concatenate([joint.flatten(), angle])
                # 데이터를 추가시켜줌
                seq.append(d)
                # 손가락 마디의 숫자를 부여함(랜드마크)
                mp_drawing.draw_landmarks(img, res, mp_hands.HAND_CONNECTIONS)

                if len(seq) < seq_length:
                    continue
                input_data = np.expand_dims(np.array(seq[-seq_length:], dtype=np.float32), axis=0)
                # y_pred에 학습을 마친 결과를 저장함
                y_pred = model.predict(input_data).squeeze()
                # x,y,z,visibility를 구분하여 학습한 결과만 True고 나머지는 모두 false이다.
                i_pred = int(np.argmax(y_pred))
                # 정확한 결과를 받는다.
                conf = y_pred[i_pred]
                # 90%이하 정확한 결과라면 해당된 액션은 제스처를 취하지 않았다고 판단함.
                if conf < 0.9:
                    continue
                # 90퍼샌트 이상이라면 시퀸스 데이터에 해당된 액션을 저장한다.
                action = actions[i_pred]
                action_seq.append(action)

                if len(action_seq) < 3:
                    continue
                # 마지막 3개의 액션이 같다면 정확한 제스처라고 판단하는 로직
                this_action = '?'
                tempValueOk = 0
                tempValueStop = 0

                if action_seq[-1] == action_seq[-2] == action_seq[-3]:
                    this_action = action
                # 결과값을 보냄!
                    if this_action == "ok":
                        tempValueOk += 1
                        if tempValueOk == 1:
                            print("진입1")
                            print(sendSign("http://localhost:8085/stopWatchStart.do?status=ok"))
                            tempValueOk+=1
                            if tempValueOk >= 2 :
                                status='b'
                    elif this_action == "vi":
                        tempValueStop += 1
                        if tempValueStop == 1:
                            print("진입2")
                            print(sendSign("http://localhost:8085/stopWatchStart.do?status=vi"))
                            tempValueStop += 1
                            if tempValueStop >= 2:
                                status='b'
                cv2.putText(img, f'{this_action.upper()}', org=(int(res.landmark[0].x * img.shape[1]), int(res.landmark[0].y * img.shape[0] + 20)),
                            fontFace=cv2.FONT_HERSHEY_SIMPLEX, fontScale=1, color=(255, 255, 255), thickness=2)
        # 사이즈 변경된 이미지로 출력
        cv2.imshow('img', img)

        if cv2.waitKey(1) == ord('b') or status == 'b':
            print("멈춤진입")
            break

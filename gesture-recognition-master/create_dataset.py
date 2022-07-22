import cv2
import cv2.cv2
import mediapipe as mp
import numpy as np
import time, os

actions = ['ok', 'vi']
seq_length = 30
# 녹화시간 60초
secs_for_action = 60

# 미디어 파이프 객체 선언과 초기화를 간단하게 구조함
mp_hands = mp.solutions.hands
mp_drawing = mp.solutions.drawing_utils
hands = mp_hands.Hands(
    max_num_hands=1,
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5)

# 웹캠을 화면에 보여줌
cap = cv2.VideoCapture(0, cv2.CAP_DSHOW)

created_time = int(time.time())
# 저장할 폴더명
os.makedirs('dataset', exist_ok=True)

# 제스처마다 녹화하도록 함
while cap.isOpened():
    for idx, action in enumerate(actions):
        data = []
        # 웹캠에 순차적으로 n번째 동작을 읽어온다
        ret, img = cap.read()
        # 이미지의 대칭을 하게해준다.
        img = cv2.flip(img, 1)
        # vi,ok 액션을 모을건지를 표시해준다.
        cv2.putText(img, f'Waiting for collecting {action.upper()} action...', org=(10, 30), fontFace=cv2.FONT_HERSHEY_SIMPLEX, fontScale=1, color=(255, 255, 255), thickness=2)
        cv2.imshow('img', img)
        # 3초동안 대기한다
        cv2.waitKey(3000)

        start_time = time.time()

        # 30초동안 반복해서 미디어 파이프에 넣어준다.
        while time.time() - start_time < secs_for_action:
            # 프레임을 한개씩 읽어옴
            ret, img = cap.read()

            img = cv2.flip(img, 1)
            img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
            # 결과를 도출함
            result = hands.process(img)
            img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)
            # 각도를 사용하여 이미지를 뽑아낸다
            if result.multi_hand_landmarks is not None:
                for res in result.multi_hand_landmarks:
                    joint = np.zeros((21, 4))
                    for j, lm in enumerate(res.landmark):
                        joint[j] = [lm.x, lm.y, lm.z, lm.visibility] # visibility 이미지상에서 보이는지 안보이는지 확인하는 변수

                    # Compute angles between joints
                    v1 = joint[[0,1,2,3,0,5,6,7,0,9,10,11,0,13,14,15,0,17,18,19], :2] # Parent joint
                    v2 = joint[[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20], :2] # Child joint
                    v = v2 - v1 # [20, 2]
                    # Normalize v
                    v = v / np.linalg.norm(v, axis=1)[:, np.newaxis]

                    # Get angle using arcos of dot product
                    angle = np.arccos(np.einsum('nt,nt->n',
                        v[[0,1,2,4,5,6,8,9,10,12,13,14,16,17,18],:], 
                        v[[1,2,3,5,6,7,9,10,11,13,14,15,17,18,19],:])) # [15,]

                    angle = np.degrees(angle) # Convert radian to degree

                    angle_label = np.array([angle], dtype=np.float32)
                    angle_label = np.append(angle_label, idx) # idx = vi=0 ,ok 1 라벨을 넣어줌
                    # x,y,z,visibility 변수들을 joint.flatten을 사용하여 횡열을 만든다.
                    d = np.concatenate([joint.flatten(), angle_label]) # 100개짜리 횡열이 됨

                    data.append(d) # 데이터를 추가시켜줌
                    # 렌드마크를 그림
                    mp_drawing.draw_landmarks(img, res, mp_hands.HAND_CONNECTIONS)

            cv2.imshow('img', img)
            if cv2.waitKey(1) == ord('q'):
                break
        # 데이터를 수집하였으면 넘파이 배열로 변환시켜줌
        data = np.array(data)
        print(action, data.shape)
        # 데이터를 raw(로우)파일이름으로 저장시켜준다.
        np.save(os.path.join('dataset', f'raw_{action}_{created_time}'), data)

        # 30개씩 모아서 시퀸스 데이터를 만들어줌
        full_seq_data = []
        for seq in range(len(data) - seq_length):
            full_seq_data.append(data[seq:seq + seq_length])
        # 데이터를 seq(시퀸스)파일이름으로 저장시켜준다.
        full_seq_data = np.array(full_seq_data)
        print(action, full_seq_data.shape)
        np.save(os.path.join('dataset', f'seq_{action}_{created_time}'), full_seq_data)
    break

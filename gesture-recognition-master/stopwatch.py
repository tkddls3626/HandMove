import tkinter as tk

# 변수를 사용하여 시간상태(실행중 or 실행중아님)을 제어함
running = False
# 시간계산하는 변수 선언
hours, minutes, seconds = 0, 0, 0


#버튼을 클릭하면 시작, 일시중지 및 재설정 기능이 호출이됨
# 시작하는 버튼 함수
def start():
    global running
    if not running:
        update()
        running = True

# 멈추는버튼 함수
def pause():
    global running
    if running:
        # after_cancel을 사용하여 멈추게게함
        stopwatch_label.after_cancel(update_time)
        running = False

# 리셋버튼 함수
def reset():
    global running
    if running:
        # after_cancel을 사용하여 리셋하게함
        stopwatch_label.after_cancel(update_time)
        running = False
    # 모든 변수를 0으로 설정함
    global hours, minutes, seconds
    hours, minutes, seconds = 0, 0, 0
    # 초기값 0으로 다시 설정함
    stopwatch_label.config(text='00:00:00')

# 스톱워치 기능하는 함수
def update():
    # 시분초로 할당함
    global hours, minutes, seconds
    seconds += 1
    if seconds == 60:
        minutes += 1
        seconds = 0
    if minutes == 60:
        hours += 1
        minutes = 0
    # 0을 시작하도록 지정함
    hours_string = f'{hours}' if hours > 9 else f'0{hours}'
    minutes_string = f'{minutes}' if minutes > 9 else f'0{minutes}'
    seconds_string = f'{seconds}' if seconds > 9 else f'0{seconds}'
    # 1초마다 타이머가 돌아가도록함
    stopwatch_label.config(text=hours_string + ':' + minutes_string + ':' + seconds_string)
    # 1초마다 함수를 호출한다.
    global update_time
    # 시간업데이트 변수를 사용하여 취소하거나 일시정지하도록한다.
    update_time = stopwatch_label.after(1000, update)


# 윈도우창 생성
root = tk.Tk()
root.geometry('485x220')
root.title('Stopwatch')

# 시간을 표시하는 시계
stopwatch_label = tk.Label(text='00:00:00', font=('Arial', 80))
stopwatch_label.pack()

# 시작,멈춤,리셋,나가기 버튼을 생성함
start_button = tk.Button(text='start', height=5, width=7, font=('Arial', 20), command=start)
start_button.pack(side=tk.LEFT)
pause_button = tk.Button(text='pause', height=5, width=7, font=('Arial', 20), command=pause)
pause_button.pack(side=tk.LEFT)
reset_button = tk.Button(text='reset', height=5, width=7, font=('Arial', 20), command=reset)
reset_button.pack(side=tk.LEFT)
quit_button = tk.Button(text='quit', height=5, width=7, font=('Arial', 20), command=root.quit)
quit_button.pack(side=tk.LEFT)

#사용자의 입력을 계속해서 기다리게 하는 목적
root.mainloop()
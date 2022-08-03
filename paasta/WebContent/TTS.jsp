<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="/js/annyang.js"></script>
<script type="text/javascript">

	// annyang 라이브러리 실행
	annyang.start({
		autoRestart : true,
		continuous : true
	})
	
	// 음성인식 값 받아오기위한 객체 생성
	var recognition = annyang.getSpeechRecognizer();
	
	// 최종 음성인식 결과값 저장변수
	var final_transcript = "";
	
	// 말하는 동안에 인식되는 값 가져오기(허용)
	recognition.interimResults = true;
	
	// 음성 인식결과 가져오기
	recognition.onresult = function(event) {

		var interim_transcript = "";
		final_transcript = "";
		for (var i = event.resultIndex; i < event.results.length; ++i) {
			if (event.results[i].isFinal) {
				final_transcript += event.results[i][0].transcript;
			} else {
				interim_transcript += event.results[i][0].transcript;
			}
		}

		document.getElementById("ing").innerHTML = "말하는중:" + interim_transcript;
		document.getElementById("result").innerHTML = "말 다했음:"
				+ final_transcript;
	}
</script>
</head>
<body>
	<h1>한국어 음성 처리 테스트</h1>
	<hr>
	<div id="ing">결과가 없습니다</div>
	<div id="result">결과가 없습니다.</div>

</body>
</html>
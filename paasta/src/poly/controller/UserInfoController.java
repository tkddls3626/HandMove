package poly.controller;
import static poly.util.CmmUtil.nvl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import poly.dto.UserDTO;
import poly.service.IUserService;
import poly.util.DateUtil;


@Controller
public class UserInfoController {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name="UserService")
	IUserService userService;
	
	// 로그인
	@RequestMapping(value="user/login")
	public String login(HttpServletRequest request, HttpSession session) {
		log.info(this.getClass().getClass().getName() +"user/login start!!");
		session.invalidate();
			
		return "user/login";
	}
	
	@RequestMapping(value="user/loginProc", method = RequestMethod.POST)
	public String loginProc(HttpServletRequest request, HttpSession session, ModelMap model) 
			throws Exception {
		
		log.info("user/loginProc Start!!");
		String id = nvl(request.getParameter("user_id"));
		String pwd = nvl(request.getParameter("user_pwd"));
		
		log.info("id : " + id);
		log.info("pwd : " + pwd);
		
		UserDTO uDTO = new UserDTO();
		
		uDTO.setUser_id(id);
		uDTO.setUser_pwd(pwd);
		
		UserDTO rDTO = new UserDTO();
		rDTO = userService.getUserInfo(uDTO);
		
		String msg = "";
		String url = "";
		if(rDTO == null) {
			log.info("rDTO == null?"+(rDTO==null));
			msg = "아이디 비밀번호를 확인해주세요";
			url = "/user/login.do";
		}else {
			log.info("데이터 조회완료");
			
			session.setAttribute("user_id", rDTO.getUser_id());
			session.setAttribute("user_pwd", rDTO.getUser_pwd());
			session.setAttribute("join_dt", rDTO.getJoin_dt());
			
			msg = "환영합니다.";
			url = "/spoilbroth/mystudy.do";
		
		}
		model.addAttribute("msg", msg);
		model.addAttribute("url", url);
		log.info("user/loginproc End!!");
		return "/redirect";
	}
	
	@RequestMapping(value="/user/logOut")
	public String logOut(HttpSession session, ModelMap model) throws Exception{
		log.info(this.getClass().getName() + "user/logOut start!!");
		
		String msg ="";
		String url ="";
		
		msg = "로그아웃 성공";
		
		session.invalidate();
		url ="/user/login.do";
		
		model.addAttribute("msg", msg);
		model.addAttribute("url", url);
		
		
		log.info(this.getClass().getName() + "user/logOut end!!");
		return "/redirect";
	}
	
		// 회원가입
		@RequestMapping(value="user/signup")
		public String signup(HttpServletRequest request, HttpSession session) {
			log.info(this.getClass().getClass().getName() +"user/signup start!!");
			
			
			return "user/signup";
		}
		
		@RequestMapping(value="user/inserUserInfo")
		public String inserUserInfo(HttpServletRequest request, HttpServletResponse response, ModelMap model) 
			throws Exception{
			
			log.info(this.getClass().getName()+ "inserUserInfo Start!!");
			
			// 회원가입 결과에 대한 메시지 전달할 변수
			String msg = "";
			String url ="";
			
			// 웹에서 받는 정보를 저장할 변수
			UserDTO pDTO = null;
			
			try {
				
				String user_id = nvl(request.getParameter("user_id"));
				String user_name = nvl(request.getParameter("user_name"));
				String user_email = nvl(request.getParameter("user_email"));
				String user_pwd = nvl(request.getParameter("user_pwd"));
				String user_dept = nvl(request.getParameter("user_dept"));
				String user_age = nvl(request.getParameter("user_age"));
				String user_study = "";
				String user_mbti = "";
				
				
				log.info("user_id : " + user_id);
				log.info("user_email : " + user_email);
				log.info("user_pwd : " + user_pwd);
				log.info("user_dept : " + user_dept);
				log.info("user_age : " + user_age);
				
				pDTO = new UserDTO();
				
				pDTO.setUser_id(user_id);
				pDTO.setUser_name(user_name);
				pDTO.setUser_email(user_email);
				// 이메일은 AES128-CBC로 암호화
				// pDTO.setUser_email(EncryptUtil.encAES128CBC(user_email));
				pDTO.setUser_pwd(user_pwd);
				// 비밀번호는 절대로 복호화 되지않도록 해시알고리즘으로 암호화
				// pDTO.setUser_pwd(EncryptUtil.encHashSHA256(user_pwd));
				
				pDTO.setUser_dept(user_dept);
				pDTO.setUser_age(user_age);
				pDTO.setUser_mbti(user_mbti);
				pDTO.setUser_study(user_study);
				pDTO.setJoin_dt(DateUtil.getDateTime("yyyy-MM-dd-hh:mm:ss"));
				log.info("date : " + pDTO.getJoin_dt());
				
				/**
				 * 회원가입
				 */
				int res = userService.insertUserInfo(pDTO);
				
				if (res == 1) {
					msg = "회원가입이 되었습니다.";
					url = "/user/login.do";
				}else if (res == 2) {
					msg = "이미 가입된 이메일 주소 입니다.";
				}else {
					msg = "오류로 인해  회원가입이 실패 하였습니다.";
					url = "/user/signup.do";
				}
			}catch(Exception e) {
				// 저장이 실패되면 사용자에세 보여줄 메시지
				msg = "실패하였습니다. : " + e.toString();
				log.info(e.toString());
				e.printStackTrace();
			}finally {
				log.info(this.getClass().getName()+ "inserUserInfo End!!");
				
				// 회원가입 여부 결과 메시지 전달하기
				model.addAttribute("msg", msg);
				model.addAttribute("url", url);
				// 회원가입 여부 결과 메시지 전달하기
				model.addAttribute("pDTO", pDTO);
				
				// 변수 초기화
				pDTO = null;
			}
			
			return "/redirect";
		}
		
		// 아이디 중복확인
		@ResponseBody
		@RequestMapping(value = "/user/idCheck", method = RequestMethod.POST)
		public int idCheck(HttpServletRequest request) throws Exception {
			log.info("idCheck 시작");

			String userId = request.getParameter("userId");

			log.info("TheService.idCheck 시작");
			UserDTO idCheck = userService.idCheck(userId);
			log.info("TheService.idCheck 종료");

			int res = 0;

			log.info("if 시작");
			if (idCheck != null)
				res = 1;

			log.info("result : " + res);
			log.info("if 종료");

			log.info("idCheck 종료");
			return res;
		}

	
}

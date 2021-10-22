package poly.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MypageController {

	private Logger log = Logger.getLogger(this.getClass());

	@RequestMapping(value = "index")
	public String Index() {
		log.info(this.getClass());

		return "/index";
	}
	
	@RequestMapping(value = "mypage/setting")
	public String setting(HttpServletRequest request, HttpSession session) {
		log.info("mypage/setting\" start!!");
		
		String user_id = (String)session.getAttribute("user_id");
		if (user_id == null) {
			return "/user/login";
		}
		log.info("mypage/setting\" end!!");
		return "mypage/setting";
	}
	
	@RequestMapping(value = "mypage/mbtiModify")
	public String mbtiModify(HttpServletRequest request, HttpSession session) {
		log.info(this.getClass());
		String user_id = (String)session.getAttribute("user_id");
		if (user_id == null) {
			return "/user/login";
		}
		return "/mypage/mbtiModify";
	}
	
	@RequestMapping(value = "mypage/userCorrection")
	public String userCorrection(HttpServletRequest request, HttpSession session) {
		log.info(this.getClass());
		String user_id = (String)session.getAttribute("user_id");
		if (user_id == null) {
			return "/user/login";
		}
		return "/mypage/userCorrection";
	}
	
	@RequestMapping(value = "mypage/passWordChange")
	public String passWordChange(HttpServletRequest request, HttpSession session) {
		log.info(this.getClass());
		String user_id = (String)session.getAttribute("user_id");
		if (user_id == null) {
			return "/user/login";
		}
		return "/mypage/passWordChange";
	}
	
	@RequestMapping(value = "mypage/userDelete")
	public String userDelete(HttpServletRequest request, HttpSession session) {
		log.info(this.getClass());
		String user_id = (String)session.getAttribute("user_id");
		if (user_id == null) {
			return "/user/login";
		}
		return "/mypage/userDelete";
	}
	
}

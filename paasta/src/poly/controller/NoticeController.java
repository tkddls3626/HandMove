package poly.controller;

import static poly.util.CmmUtil.nvl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import poly.dto.BoardDTO;
import poly.dto.StudyListDTO;
import poly.dto.UserDTO;
import poly.service.IImgService;
import poly.service.IBoardService;
import poly.service.IStudyService;
import poly.service.IUserService;
import poly.service.impl.BoardService;
import poly.util.CmmUtil;
import poly.util.DateUtil;




@Controller
public class NoticeController {

	private Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name="UserService")
	IUserService userService;
	
	@Resource(name = "StudyService")
	IStudyService studyService;

	@Resource(name = "BoardService")
	IBoardService boardService;
	
	/**
	 * 게시판 리스트 보여주기
	 */
	@RequestMapping(value="board/BoardReg", method=RequestMethod.GET)
	public String BoardReg(HttpServletRequest request, HttpServletResponse response, 
			ModelMap model) throws Exception {
		log.info(this.getClass().getName() + ".BoardReg start!");
		String study_seq = nvl(request.getParameter("study_seq"));
		String study_name = studyService.getStudyName(study_seq);

		
		model.addAttribute("study_seq", study_seq);
		model.addAttribute("study_name", study_name);
		log.info(this.getClass().getName() + ".BoardReg end!");
		
		return "/board/BoardReg";
	}
	
	/**
	 * 게시판 글 등록
	 * */
	@RequestMapping(value="board/BoardInsert", method=RequestMethod.POST)
	public String BoardInsert(HttpSession session, HttpServletRequest request, HttpServletResponse response, 
			ModelMap model) throws Exception {
		
		log.info(this.getClass().getName() + ".BoardInsert start!");
		
		String msg = "";
		String url = "";
		
		try{
			/*
			 * 게시판 글 등록되기 위해 사용되는 form객체의 하위 input 객체 등을 받아오기 위해 사용함
			 * */
			String user_id = CmmUtil.nvl((String)session.getAttribute("user_id")); //아이디
			String title = CmmUtil.nvl(request.getParameter("title")); //제목
			String notice_yn = CmmUtil.nvl(request.getParameter("notice_yn")); //공지글 여부
			String contents = CmmUtil.nvl(request.getParameter("contents")); //내용
			String study_seq = CmmUtil.nvl(request.getParameter("study_seq")); // 스터디 그룹 고유번호
			String study_name = studyService.getStudyName(study_seq);

			/*
			 * #######################################################
			 * 	 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함
			 * 						반드시 작성할 것
			 * #######################################################
			 * */
			log.info("user_id : "+ user_id);
			log.info("title : "+ title);
			log.info("notice_yn : "+ notice_yn);
			log.info("contents : "+ contents);		
			log.info("study_seq : "+ study_seq);		
			
			BoardDTO pDTO = new BoardDTO();
			
			pDTO.setUser_id(user_id);
			pDTO.setTitle(title);
			pDTO.setNotice_yn(notice_yn);
			pDTO.setContents(contents);
			pDTO.setStudy_seq(study_seq);
			pDTO.setReg_dt(DateUtil.getDateTime("yyyy-MM-dd"));
			/*
			 * 게시글 등록하기위한 비즈니스 로직을 호출
			 * */
			boardService.InsertBoardInfo(pDTO);

			
			//저장이 완료되면 사용자에게 보여줄 메시지
			msg = "등록되었습니다.";
			
			
			//변수 초기화(메모리 효율화 시키기 위해 사용함)
			pDTO = null;
			
		}catch(Exception e){
			
			//저장이 실패되면 사용자에게 보여줄 메시지			
			msg = "실패하였습니다. : "+ e.toString();
			log.info(e.toString());
			e.printStackTrace();
			
		}finally{
			log.info(this.getClass().getName() + ".BoardInsert end!");
			String study_seq = CmmUtil.nvl(request.getParameter("study_seq")); // 스터디 그룹 고유번호
			String study_name = studyService.getStudyName(study_seq);
			
			url="/study/studyboard.do?study_name="+study_name;
			//결과 메시지 전달하기
			model.addAttribute("msg", msg);
			model.addAttribute("url", url);
			
		}
		
		return "/redirect";
	}	
	
	
	/**
	 * 게시판 상세보기
	 * */
	@RequestMapping(value="board/BoardInfo", method=RequestMethod.GET)
	public String BoardInfo(HttpServletRequest request, HttpServletResponse response, 
			ModelMap model) throws Exception {
		
		log.info(this.getClass().getName() + ".BoardInfo start!");
		

		/*
		 * 게시판 글 등록되기 위해 사용되는 form객체의 하위 input 객체 등을 받아오기 위해 사용함
		 * */
		String notice_seq = nvl(request.getParameter("notice_seq")); //공지글번호(PK)
		String study_seq = nvl(request.getParameter("study_seq"));
		
		String study_name = studyService.getStudyName(study_seq);

		/*
		 * #######################################################
		 * 	 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함
		 * 						반드시 작성할 것
		 * #######################################################
		 * */
		log.info("notice_seq : "+ notice_seq);
		log.info("study_seq : "+ study_seq);
		log.info("study_name : "+ study_name);
		
		
		/*
		 * 값 전달은 반드시 DTO 객체를 이용해서 처리함
		 * 전달 받은 값을 DTO 객체에 넣는다.
		 * */		
		BoardDTO pDTO = new BoardDTO();

		
		pDTO.setNotice_seq(notice_seq);
		pDTO.setStudy_seq(study_seq);
		
		//공지사항 글 조회수 증가
		boardService.updateBoardReadCnt(pDTO);
		
		log.info("read_cnt update success!!!");
		
		//공지사항 상세정보 가져오기
		BoardDTO rDTO = boardService.getBoardInfo(pDTO);
		
		if (rDTO==null){
			rDTO = new BoardDTO();
			
		}
		
		log.info("getBoardInfo success!!!");
		
		//조회된 리스트 결과값 넣어주기
		model.addAttribute("rDTO", rDTO);
		model.addAttribute("study_name", study_name);
		//변수 초기화(메모리 효율화 시키기 위해 사용함)
		rDTO = null;
		pDTO = null;
		
		log.info(this.getClass().getName() + ".BoardInfo end!");
		
		return "/board/BoardInfo";
	}
	
	
	/**
	 * 게시판 수정 보기
	 * */
	@RequestMapping(value="board/BoardEditInfo", method=RequestMethod.GET)
	public String BoardEditInfo(HttpServletRequest request, HttpServletResponse response, 
			ModelMap model) throws Exception {
		
		log.info(this.getClass().getName() + ".BoardEditInfo start!");
		
		
		String notice_seq = nvl(request.getParameter("notice_seq")); //공지글번호(PK)
		String study_seq = nvl(request.getParameter("study_seq")); // 스터디 고유번호
		
		log.info("notice_seq : "+ notice_seq);
		
		
		BoardDTO pDTO = new BoardDTO();
		
		pDTO.setNotice_seq(notice_seq);		
		pDTO.setStudy_seq(study_seq);
		/*
		 * #######################################################
		 * 	공지사항 수정정보 가져오기(상세보기 쿼리와 동일하여, 같은 서비스 쿼리 사용함)
		 * #######################################################
		 */
		BoardDTO rDTO = boardService.getBoardInfo(pDTO);
		
		if (rDTO==null){
			rDTO = new BoardDTO();
			
		}
		
		//조회된 리스트 결과값 넣어주기
		model.addAttribute("rDTO", rDTO);
		
		
		//변수 초기화(메모리 효율화 시키기 위해 사용함)
		rDTO = null;
		pDTO = null;
		
		log.info(this.getClass().getName() + ".BoardEditInfo end!");
		
		return "/board/BoardEditInfo";
	}
	
	
	/**
	 * 게시판 글 수정
	 * */
	@RequestMapping(value="board/BoardUpdate", method=RequestMethod.POST)
	public String BoardUpdate(HttpSession session, HttpServletRequest request, HttpServletResponse response, 
			ModelMap model) throws Exception {
		
		log.info(this.getClass().getName() + ".BoardUpdate start!");
		
		String msg = "";
		String url = "";
		
		try{
			
			String user_id = nvl((String)session.getAttribute("user_id")); //아이디
			String notice_seq = nvl(request.getParameter("notice_seq")); //글번호(PK)
			String title = nvl(request.getParameter("title")); //제목
			String notice_yn = nvl(request.getParameter("notice_yn")); //공지글 여부
			String contents = nvl(request.getParameter("contents")); //내용
			String study_seq = nvl(request.getParameter("study_seq")); //스터 고유번호
	
			log.info("user_id : "+ user_id);
			log.info("notice_seq : "+ notice_seq);
			log.info("title : "+ title);
			log.info("notice_yn : "+ notice_yn);
			log.info("contents : "+ contents);		
			
			BoardDTO pDTO = new BoardDTO();
			
			pDTO.setUser_id(user_id);
			pDTO.setNotice_seq(notice_seq);
			pDTO.setTitle(title);
			pDTO.setNotice_yn(notice_yn);
			pDTO.setContents(contents);
			pDTO.setChg_dt(DateUtil.getDateTime("yyyy-MM-dd"));
			pDTO.setStudy_seq(study_seq);
	
			//게시글 수정하기 DB
			boardService.updateBoardInfo(pDTO);
			
			msg = "수정되었습니다.";
			url = "/board/BoardInfo.do?notice_seq="+nvl(request.getParameter("notice_seq"))+"&&study_seq="+nvl(request.getParameter("study_seq"));
			
			//변수 초기화(메모리 효율화 시키기 위해 사용함)
			pDTO = null;
			
		}catch(Exception e){
			msg = "실패하였습니다. : "+ e.toString();
			url = "/board/BoardInfo.do?notice_seq="+nvl(request.getParameter("notice_seq"))+"&&study_seq="+nvl(request.getParameter("study_seq"));
			log.info(e.toString());
			e.printStackTrace();
			
		}finally{
			log.info(this.getClass().getName() + ".BoardUpdate end!");
			
			//결과 메시지 전달하기
			model.addAttribute("msg", msg);
			model.addAttribute("url", url);
			
		}
		
		return "/redirect";
	}	
	
	/**
	 * 게시판 글 삭제
	 * */
	@RequestMapping(value="board/BoardDelete", method=RequestMethod.GET)
	public String BoardDelete(HttpSession session, HttpServletRequest request, HttpServletResponse response, 
			ModelMap model) throws Exception {
		
		log.info(this.getClass().getName() + ".BoardDelete start!");
		
		String msg = "";
		String url = "";
		
		try{
			
			String notice_seq = nvl(request.getParameter("notice_seq")); //글번호(PK)
			String study_seq = nvl(request.getParameter("study_seq"));
			
			String study_name = studyService.getStudyName(study_seq);
			
			BoardDTO pDTO = new BoardDTO();
			
			pDTO.setNotice_seq(notice_seq);
			pDTO.setStudy_seq(study_seq);
			
			//게시글 삭제하기 DB
			boardService.deleteBoardInfo(pDTO);;
			
			msg = "삭제되었습니다.";
			url = "/study/studyboard.do?study_name=" + study_name;
			//변수 초기화(메모리 효율화 시키기 위해 사용함)
			pDTO = null;
			
		}catch(Exception e){
			String study_seq = nvl(request.getParameter("study_seq"));
			String study_name = studyService.getStudyName(study_seq);
			
			msg = "실패하였습니다. : "+ e.toString();
			url = "/study/studyboard.do?study_name=" + study_name;
			log.info(e.toString());
			e.printStackTrace();
			
		}finally{
			log.info(this.getClass().getName() + ".BoardDelete end!");
			
			//결과 메시지 전달하기
			model.addAttribute("msg", msg);
			model.addAttribute("url", url);
		}
		
		return "/redirect";
	}	
	
}

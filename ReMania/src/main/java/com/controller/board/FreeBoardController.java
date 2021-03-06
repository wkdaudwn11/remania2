package com.controller.board;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.entity.board.Comment;
import com.entity.board.FreeBoardDTO;
import com.entity.board.FreeBoardPage;
import com.entity.member.MemberDTO;
import com.service.board.FreeBoardService;

@Controller
public class FreeBoardController {

	@Autowired
	private FreeBoardService service;
	
	@RequestMapping("/freeBoardWrite")
	public String freeBoardWrite(HttpSession session,FreeBoardDTO board){
		board.setEmail(((MemberDTO)session.getAttribute("login")).getEmail());
		service.freeBoardWrite(board);
		return "redirect:freeBoardList";
	}// end freeBoardWrite()
	
	@RequestMapping("/freeBoardList")
	public String freeBoardList(@ModelAttribute("FreeBoardPage")FreeBoardPage boardPage){
		if(boardPage.getCurPage() == 0){
			boardPage.setCurPage(1);
		}
		boardPage = service.freeBoardList(boardPage);
		return "board/free/freeBoardList";
	}// end public String freeBoardList
	
	@RequestMapping("/freeBoardDetail")
	public ModelAndView freeBoardDetail(FreeBoardDTO board){
		ModelAndView modelAndView = service.freeBoardDetail(board);
		modelAndView.setViewName("board/free/freeBoardDetail");
		return modelAndView; 
 	}// end public String freeBoardDetail()
	
	@RequestMapping("/comment")
	public String comment(@SessionAttribute("login")MemberDTO member,Comment comment,String call,RedirectAttributes redirectAttributes){
		comment.setEmail(member.getEmail());
		comment.setAuthor(member.getName());
		if(call.equals("comment")){
			comment.setCategory("board");
			service.comment(comment);
		}else if(call.equals("recomment")){
			service.recomment(comment);
		}
		redirectAttributes.addAttribute("commentWrite", "작성 완료");
		return "redirect:freeBoardDetail?freeboardnum="+comment.getBoardnum();
	}// end public String comment()
	
	@RequestMapping("/commentList")
	@ResponseBody
	public List<Comment> commentList(int num,int skip){
		return service.commetList(num,skip);
	}
}//end Controller

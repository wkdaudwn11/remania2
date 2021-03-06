package com.controller.trade;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.params.HttpParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.entity.trade.BuyDTO;
import com.entity.trade.BuyPageDTO;
import com.service.trade.BuyService;

@Controller
public class BuyController {
	
	@Autowired
	private BuyService service;
	
	/** 삽니다 리스트를 뿌려주는 메소드 */
	@RequestMapping(value="buyList")
	public ModelAndView buyList(String curPage, String category, String sort, String searchType, String searchValue){
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("trade/buy/buyList");
		
		if(curPage == null) curPage="1";
		if(sort == null || sort.equals("")) sort="최신순";
		
		mav.addObject("sort", sort);
		
		BuyPageDTO buyPageDTO = service.buyList(Integer.parseInt(curPage), category, sort, searchType, searchValue);
		
		mav.addObject("buyPageDTO", buyPageDTO);
		mav.addObject("category", category);
		mav.addObject("searchType", searchType);
		mav.addObject("searchValue", searchValue);
		
		return mav;
		
	}//buyList(String curPage, String category, String sortName, String sortValue)
	
	/**삽니다 글작성 화면에서 등록 버튼을 누르면 이 메소드가 호출됨.*/
	@RequestMapping(value="buyWrite")
	public String buyWrite(BuyDTO dto) throws Exception {
		
		int curval; // buy테이블의 최신글 번호를 담을 변수
		
		try{
			curval = service.getCurval();
		}catch(NullPointerException e){
			curval = 0; // 게시글이 없어서 에러가 뜨면 걍 0으로 설정.
		}
		
		//앞으로 등록하 게시물은 최신글의 +1이니까, curval에다가 +1을 해줌
		//nextval은 사진을 저장할 폴더명에 쓰일 것임.
		int nextval = curval + 1;

		CommonsMultipartFile image1 = dto.getUpfile(); // 대표사진
		CommonsMultipartFile image2[] = dto.getUpfileContent(); // 내용에 있는 사진은 여러장 일 수도 있기 때문에 배열로 받는다.
		
		//사진을 저장할 경로 지정 (번호_글제목)
		String filePath = "C:\\project\\images\\trade\\buy\\"+nextval+"_"+dto.getTitle();
		
		//없는 경로라면(무조건 없겠지만), 해당 폴더(경로)를 새로 만듬
		if(!new File(filePath).exists()){
			new File(filePath).mkdir();
		}
		
		try{
			//대표사진 저장
			String fileName1 = image1.getOriginalFilename();
			if(!(fileName1.equals(""))){ // 사진을 올렸을 경우에만 사진을 저장한다.
				File file1 = new File(filePath, fileName1);
				image1.transferTo(file1);
				StringTokenizer image1Token = new StringTokenizer(fileName1, ".");
				dto.setImage1(image1Token.nextToken()); //dto에 사진이름을 저장 (.jpg를 뺀 순수한 이름)
			}else{
				dto.setImage1(""); // SQLException을 막기 위해서 빈문자열이라도 넣어준다. (값이 없는 상태에서 insert하니까 에러뜸)
			}
			
			//내용사진 저장
			String contentImages[] = new String[image2.length];
			String fileName2 = "";
			//image2는 여러장일 수도 있으므로 for문을 돌려서 사진을 저장한다.
			for(int i=0; i<image2.length; i++){
				fileName2 = image2[i].getOriginalFilename();
				File file2 = new File(filePath, fileName2);
				image2[i].transferTo(file2);
				
				contentImages[i] = fileName2; // 내용에 들어간 사진들의 이름을 저장. '.jpg'도 포함되어있어서 토크나이저로 짤라줘야함.
			}
			
			if(!(fileName2.equals(""))){ // 내용사진도 마찬가지로 사진을 올렸을 경우에만 사진을 저장한다.
				StringTokenizer image2Token = null;
				String imagesName = ""; // .jpg를 제외한 파일명을 x변수에다가 중첩시킬 것이다. (image1,image2,image3) 이런 식으로.
				
				for(int i=0; i<contentImages.length; i++){
					image2Token = new StringTokenizer(contentImages[i], ".");
					imagesName += image2Token.nextToken()+",";
					
					image2Token.nextToken(); //.jpg는 필요없음.
				}
				dto.setImage2(imagesName); //dto에 사진이름들을 저장 (.jpg를 뺀 순수한 이름)
			}else{
				dto.setImage2(""); // 마찬가지로 SQLException을 막기 위해서 빈문자열이라도 넣어준다. (값이 없는 상태에서 insert하니까 에러뜸)
			}
			service.buyInsert(dto);
			
		}catch(IllegalStateException e){
			e.printStackTrace();
		}catch(IOException e2){
			e2.printStackTrace();
		}
		
		return "redirect:buyList";
	}//buyWrite(BuyDTO dto)
	
	/** 넘겨받은 buynum으로 해당 게시글의 정보를 가지고 글 수정 폼으로 넘김 */
	@RequestMapping(value="buyUpdate", method=RequestMethod.POST)
	public String buyUpdate(BuyDTO buyDTO, String curPage, String category, String sort, String searchType, String searchValue){
		
		if(category.equals(",자전거")){
			category="자전거";
		}else if(category.equals(",카메라")){
			category="카메라";
		}
		
		//2017.05.23 05:08
		
		//여기서도 buyDetail로 넘겨야 하지만 get방식으로 넘길 때 한글처리가 안되므로 그냥 list로 넘어감
		return "redirect:buyList";
	}//buyDelete(String buynum) GET
	
	/**게시글을 클릭하면 이 메소드가 DB에서 해당 게시글에 대한 정보를 가지고 buyDetail.jsp로 넘김*/
	@RequestMapping(value="buyDetail")
	public String buyDetail(String buynum, String curPage, String category, String sort, String searchType, String searchValue, Model m){
		
		BuyDTO buyDTO = service.buyDetail(Integer.parseInt(buynum));
		
		if(buyDTO.getImage2() != null){
			ArrayList<String> image2List = new ArrayList<>();
			StringTokenizer image2 = new StringTokenizer(buyDTO.getImage2(), ",");
			
			while(image2.hasMoreElements()){
				image2List.add(image2.nextToken());
			}
			m.addAttribute("image2List", image2List);
		}
		
		if(curPage == null) curPage="1";
		if(sort == null || sort.equals("")) sort="최신순";
		if(category.equals("")) category=null;
		if(searchType.equals("") || searchType == null) searchType="제목";
		if(searchValue.equals("")) searchValue=null;
		
		m.addAttribute("sort", sort);
		
		BuyPageDTO buyPageDTO = service.buyList(Integer.parseInt(curPage), category, sort, searchType, searchValue);
		
		m.addAttribute("buyPageDTO", buyPageDTO);
		m.addAttribute("buyDTO", buyDTO);
		m.addAttribute("category", category);
		m.addAttribute("searchType", searchType);
		m.addAttribute("searchValue", searchValue);
		
		return "trade/buy/buyDetail";
	}//buyDetail(String buynum)
	
	/** 글 삭제 후, 게시물 목록으로 넘어감. */
	@RequestMapping(value="buyDelete")
	public String buyDelete(String buynum, String curPage, String category, String sort, String searchType, String searchValue) {
		
		service.buyDelete(Integer.parseInt(buynum));
		
		//지우고 난 다음에 list로 넘길 때, 현재 페이지, 검색결과, 분류를 그대로 유지할려고 했으나, get방식으로 넘길 땐 한글처리가 안되서 실패.
		//그러므로 그냥  buyList로 넘기는 걸로.
		//return "redirect:buyList?curPage="+curPage+"&category="+category+"&sort="+sort+"&searchType="+searchType+"&searchValue="+searchValue;
		return "redirect:buyList";
	}//buyDelete(String buynum, String curPage, String category, String sort, String searchType, String searchValue)
	
	/** 넘겨받은 buynum으로 해당 게시글의 정보를 가지고 글 수정 폼으로 넘김 */
	@RequestMapping(value="buyUpdate", method=RequestMethod.GET)
	public String buyUpdateUI(String buynum, String curPage, String category, String sort, String searchType, String searchValue, Model m){
		
		BuyDTO buyDTO = service.buyDetail(Integer.parseInt(buynum));
		
		if(buyDTO.getImage2() != null){
			ArrayList<String> image2List = new ArrayList<>();
			StringTokenizer image2 = new StringTokenizer(buyDTO.getImage2(), ",");
			
			while(image2.hasMoreElements()){
				image2List.add(image2.nextToken());
			}
			m.addAttribute("image2List", image2List);
		}
		
		m.addAttribute("buyDTO", buyDTO);
		m.addAttribute("curPage", curPage);
		m.addAttribute("category", category);
		m.addAttribute("sort", sort);
		m.addAttribute("searchType", searchType);
		m.addAttribute("searchValue", searchValue);
		
		return "trade/buy/buyUpdate";
	}//buyUpdate(String buynum, Model m) GET
}

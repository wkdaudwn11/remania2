package com.dao.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.entity.board.Comment;
import com.entity.board.FreeBoardDTO;
import com.entity.board.FreeBoardPage;

@Repository
public class FreeBoardDAO {

	@Autowired
	private SqlSessionTemplate template;
	
	private final String namespace = "com.remania.BoardMapper.";
	
	/** write 레코드 추가*/
	public void freeBoardWrite(FreeBoardDTO board){
		template.insert(namespace+"freeBoardWrite",board);
	}// end public void freeBoardWrite

	/** list 전체 레코드 가져오기*/
	public FreeBoardPage freeBoardList(FreeBoardPage boardPage){
		
		int skip = (boardPage.getCurPage()-1)*boardPage.getPERPAGE();
		
		List<FreeBoardDTO> boardList = template.selectList(namespace+"freeBoardList", null,new RowBounds(skip, boardPage.getPERPAGE()));
		
		boardPage.setBoardList(boardList);
		boardPage.setTotalRecord(totalRecord());
		
		return boardPage;
	}// end public void freeBoardList
	
	/** totalRecord 전체 레코드 수*/
	private int totalRecord(){
		return template.selectOne(namespace+"totalRecord",null);
	}// end private int totalRecord()
	
	/** Detail 레코드 하나 가져오기*/
	public FreeBoardDTO freeBoardDetail(FreeBoardDTO board){
		template.update(namespace+"readcnt",board.getFreeboardnum());
		return template.selectOne(namespace+"freeBoardDetail",board);
	}// end public FreeBoardDTO freeBoardDetail
	
	/** comment 레코드 가져오기 */
	public List<Comment> commentList(int num,Integer skip){
		if(skip == null){
			skip = 0;
		}
		return template.selectList(namespace+"commentList",num,new RowBounds(skip,5));
	}
	
	/** comment 추가*/
	public void comment(Comment comment){
		template.insert(namespace+"comment",comment);
	}
	
	/** recomment */
	public void recomment(Comment recomment){
		HashMap<String,Object> map = new HashMap<>();
		map.put("category", recomment.getCategory());
		map.put("step",recomment.getStep());
		template.update(namespace+"stepManager",map);
		template.insert(namespace+"recomment",recomment);
	}
}// end Repository


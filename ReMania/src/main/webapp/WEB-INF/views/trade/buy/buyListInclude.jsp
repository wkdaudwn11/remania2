<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.entity.trade.BuyPageDTO"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script type="text/javascript">

	$(document).ready(function(){
		
		//검색버튼을 눌렀을 경우
		$("#searchBtn").on("click", function(){
			var category = document.getElementById("category").value;
			var sort = document.getElementById("sort").value;
			var searchType = document.getElementById("searchType").value;
			var searchValue = document.getElementById("searchValue").value;
			
			location.replace("buyList?category="+category+"&sort="+sort+"&searchType="+searchType+"&searchValue="+searchValue);
		});
		
		//분류를 했을 경우
		$("#category,#sort").on("change", function(){
			var category = document.getElementById("category").value;
			var sort = document.getElementById("sort").value;
			var searchType = document.getElementById("searchType").value;
			var searchValue = document.getElementById("searchValue").value;
			
			location.replace("buyList?category="+category+"&sort="+sort+"&searchType="+searchType+"&searchValue="+searchValue);
		});
		
		$("#buyWriteBtn").on("click", function(){
			location.replace('buyWriteUI');
		});
		
	});
</script>

<c:set var="buyList" value="${buyPageDTO.buyList}" scope="request"/> <!-- 페이지에 보여줄 리스트 -->
<c:set var="curPage" value="${buyPageDTO.curPage}" scope="request"/> <!-- 요청된 현재 페이지 -->
<c:set var="perPage" value="${buyPageDTO.perPage}" scope="request"/> <!-- 페이지 당 보여줄 리스트 수(9) -->
<c:set var="page" value="${buyPageDTO.page}" scope="request"/> <!-- 표시할 페이지 수 -->
<c:set var="pageblock" value="${Math.ceil(curPage/page)}" scope="request"/> <!-- 표시할 페이지 블럭수 -->
<c:set var="totalRecord" value="${buyPageDTO.totalRecord}" scope="request"/> <!-- 전체 게시물 수 -->

<br /><br /><br /><br /><br /><br />

<center>
	<font size="100"><b><a href="buyList" style="text-decoration: none; color:black;">삽니다</a></b></font><br /><br />
	<form class="form-inline">
		<div class="form-group">
			<select class="form-control" id="searchType">
				<c:choose>
					<c:when test="${searchType == 'title' || searchType == '제목' || empty searchType}">
						<option value="title" selected="selected">제목</option>
						<option value="content">내용</option>
					</c:when>
					<c:when test="${searchType == 'content' || searchType == '내용'}">
						<option value="title">제목</option>
						<option value="content" selected="selected">내용</option>
					</c:when>
				</c:choose>
			</select>
		</div>
		<input type="text" class="form-control" size="50" id="searchValue" value="${searchValue}" placeholder="검색어를 입력하세요.">
		<button type="button" class="btn btn-danger" id="searchBtn">Search</button>
	</form>
</center>

<div class="container">

	<form class="form-inline">
		<div class="form-group">
			<select class="form-control" id="category">
				<c:choose>
					<c:when test="${category == '전체' || empty category}">
						<option value="전체" selected="selected">전체</option>
						<option value="자전거">자전거</option>
						<option value="카메라">카메라</option>
					</c:when>
					<c:when test="${category == '자전거'}">
						<option value="전체">전체</option>
						<option value="자전거" selected="selected">자전거</option>
						<option value="카메라">카메라</option>
					</c:when>
					<c:when test="${category == '카메라'}">
						<option value="전체">전체</option>
						<option value="자전거">자전거</option>
						<option value="카메라" selected="selected">카메라</option>
					</c:when>
				</c:choose>
			</select>
		</div>
		<div class="form-group">
			<select class="form-control" id="sort">
				<c:choose>
					<c:when test="${sort == '최신순' || empty sort}">
						<option value="최신순" selected="selected">최신순</option>
						<option value="높은 가격순">높은 가격순</option>
						<option value="낮은 가격순">낮은 가격순</option>
					</c:when>
					<c:when test="${sort == '높은 가격순'}">
						<option value="최신순">최신순</option>
						<option value="높은 가격순" selected="selected">높은 가격순</option>
						<option value="낮은 가격순">낮은 가격순</option>
					</c:when>
					<c:when test="${sort == '낮은 가격순'}">
						<option value="최신순">최신순</option>
						<option value="높은 가격순">높은 가격순</option>
						<option value="낮은 가격순" selected="selected">낮은 가격순</option>
					</c:when>
				</c:choose>
			</select>
		</div>
	</form>
	<br />
	
	<div class="row">
		<c:forEach var="buyDTO2" items="${buyList}" varStatus="status">
			<a href="buyDetail?buynum=${buyDTO2.buynum}&curPage=${curPage}&category=${category}&sort=${sort}&searchType=${searchType}&searchValue=${searchValue}&buyPageDTO=${buyPageDTO}" style="text-decoration: none; color:black;">
				<div class="col-sm-4">
					<div class="panel panel-danger">
						<div class="panel-heading">${buyDTO2.category}</div>
						<div class="panel-body">
						<c:choose>
							<c:when test="${!(empty buyDTO2.image1)}">
								<img src="buy/${buyDTO2.buynum}_${buyDTO2.title}/${buyDTO2.image1}.jpg" class="img-responsive"
									style="width: 100%; height: 15em;" alt="Image">
							</c:when>
							<c:otherwise> <!-- 사진 등록을 안했을 경우는 기본 사진을 뿌려줌 -->
								<img src="images/ImgNotFound.png" class="img-responsive"
									style="width: 100%; height: 15em;" alt="Image">
							</c:otherwise>
						</c:choose>
						</div>
						<div class="panel-footer">
							<c:if test="${!(empty buyDTO2.image2)}">
								
							</c:if>
							<strong>${buyDTO2.title}</strong><br />
							<small>희망가격:
							<c:choose>
								<c:when test="${buyDTO2.pricechoice == '범위 설정'}">
									<fmt:formatNumber value="${buyDTO2.price1}" type="currency" /> ~ <fmt:formatNumber value="${buyDTO2.price2}" type="currency" />
								</c:when>
								<c:otherwise>
									합의 후 결정
								</c:otherwise>
							</c:choose>
							</small>
							
						</div>
					</div>
				</div>
			</a>
			<c:if test="${status.count % 3 == 0}">
				</div>
				<div class="row">
			</c:if>
		</c:forEach>
	</div>
	
	<br />
	
	<!-- 페이징처리 -->
	<c:if test="${pageblock*page <= Math.ceil(totalRecord/perPage)}">
		<c:set var="endPage" value="${pageblock*page}" scope="request"/>
	</c:if>
	<c:if test="${pageblock*page > Math.ceil(totalRecord/perPage)}">
		<c:set var="endPage" value="${Math.ceil(totalRecord/perPage)}" scope="request"/>
	</c:if>
			
	<div id="paging" style="width: 44em; margin: 0 auto;">
		<ul class="pager" style="float: left;">
			<c:if test="${curPage != 1}">
				<li><a href="buyList?curPage=1&category=${category}&searchType=${searchType}&searchValue=${searchValue}">
					처음
				</a></li>
			</c:if>
			<c:if test="${pageblock > 1}">
				<li><a href="buyList?curPage=${Math.round((pageblock*page)-19)}&category=${category}&searchType=${searchType}&searchValue=${searchValue}">
					<
				</a></li>
			</c:if>
		</ul>
		
		<c:if test="${pageblock < 1}">
			<c:set var="pageblock" value="1" scope="request"/> <!-- 표시할 페이지 블럭수 -->
		</c:if>
		
		<ul class="pagination" style="float: left;">
			<c:forEach var="i" begin="${(pageblock*page)-(page-1)}" end="${endPage}" >
				<c:choose>
					<c:when test="${curPage == i}">
						<li class="active"><a>${i}</a></li>
					</c:when>
					<c:otherwise>
						<li><a href="buyList?curPage=${i}&category=${category}&searchType=${searchType}&searchValue=${searchValue}">
							${i}
						</a></li>
						
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</ul>
		
		<ul class="pager" style="float: left;">
			<c:if test="${pageblock != Math.ceil((totalRecord/perPage+1)/page)}">
				<li><a href="buyList?curPage=${Math.round((pageblock*page)+1)}&category=${category}&searchType=${searchType}&searchValue=${searchValue}">
					>
				</a></li>
			</c:if>
			
			<c:if test="${curPage != Math.ceil(totalRecord/perPage)}">
				<c:set var="lastPage" value="${Math.ceil(totalRecord/perPage)}" />
				<li><a href="buyList?curPage=${Math.round(Math.ceil(totalRecord/perPage))}&searchType=${searchType}&searchValue=${searchValue}">
					맨끝
				</a></li>
			</c:if>
			
		</ul>
		<br />
		<br />
		<br />
		<br />
	</div>
	<p style="text-align: right;">
		<button type="button" class="btn btn-danger" id="buyWriteBtn">
			삽니다 등록
		</button>
	</p>
</div>

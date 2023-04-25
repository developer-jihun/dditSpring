<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<link rel="stylesheet" href="/resources/css/bootstrap.min.css" />
<script src="https://ssl.daumcdn.net/dmaps/map_js_init/postcode.v2.js"></script>
<script type="text/javascript" src="/resources/js/jquery-3.6.0.js"></script>
<script type="text/javascript">
$(function(){
	//다음 우편번호 검색
	$("#btnPostno").on("click",function(){
		new daum.Postcode({
			//다음 창에서 검색이 완료되면 콜백함수에 의해 결과 데이터가 data 객체로 들어옴
			oncomplete:function(data){
				//우편번호
				$("#postno").val(data.zonecode);
				$("#cusAddr").val(data.address);
				$("#addrDet").val(data.buildingName);
			}
		}).open();
	});
});
</script>
<!-- ctrl + shift + F : 인덴트 자동 처리 -->
<nav class="navbar bg-body-tertiary">
  <div class="container-fluid">
    <a class="navbar-brand" href="#">고객관리</a>
  </div>
</nav>
<!-- 폼페이지
요청URI : /cus/createPost
요청파라미터 : {cusNum=12345,cusNm=개똥이,postno=33233,cusAddr=주소
	 	   ,addrDet=상세주소,cusPhe=010-123-1223}
요청방식 : post
 -->
<form:form modelAttribute="cusVO" action="/cus/createPost" method="post">
<!-- 폼데이터 -->
<div class="bd-example">
	<div class="mb-3">
		<label for="cusNum" class="form-label">고객번호</label> 
<!-- 		<input type="text" class="form-control" -->
<!-- 			id="cusNum" name="cusNum" placeholder="고객번호를 입력해주세요" /> -->
		<!-- 꺽임쇄form:input 에는 type="text"랑 id="cusNum"이랑 name="cusNum"이 내장 -->
		<form:input class="form-control" 
 			path="cusNum" placeholder="고객번호를 입력해주세요" />
		<!-- path : cusVO객체의 cusNum 멤버변수 -->
		<form:errors path="cusNum" style="color:red;" />
	</div>
	<div class="mb-3">
		<label for="cusNm" class="form-label">고객명</label> 
		<form:input class="form-control"
			path="cusNm" placeholder="고객명을 입력해주세요" />
		<form:errors path="cusNm" style="color:red;" />
	</div>
	<div class="mb-3">
		<div>
			<label for="postno" class="form-label">우편번호</label>
		</div>
		<div> 
			<input type="text" class="form-control"
				id="postno" name="postno" placeholder="우편번호를 입력해주세요"
				style="width:80%;float:left;" value="${cusVO.postno}" />
			<!-- path="postno" : id="postno" name="post"
			form:input : <input type="text"
			 -->
			<input class="btn btn-info" type="button" value="우편번호 검색"
				style="width:20%;float:right;" id="btnPostno" />
		</div>
	</div>
	<div class="mb-3">
		<label for="cusAddr" class="form-label">주소</label> 
		<input type="text" class="form-control"
			id="cusAddr" name="cusAddr" placeholder="주소를 입력해주세요"
			value="${cusVO.cusAddr}" />
	</div>
	<div class="mb-3">
		<label for="addrDet" class="form-label">상세 주소</label> 
		<input type="text" class="form-control"
			id="addrDet" name="addrDet" placeholder="상세 주소를 입력해주세요"
			value="${cusVO.addrDet}" />
	</div>
	<div class="mb-3">
		<label for="cusPhe" class="form-label">연락처</label> 
		<input type="text" class="form-control"
			id="cusPhe" name="cusPhe" placeholder="연락처를 입력해주세요"
			value="${cusVO.cusPhe}" />
		<form:errors path="cusPhe" style="color:red;" />
	</div>
	<div class="mb-3">
		<label for="cusBir" class="form-label">생일</label> 
		<input type="text" class="form-control"
			id="cusBir" name="cusBir" placeholder="생일을 입력해주세요"
			value="<fmt:formatDate pattern='yyyy-MM-dd' value='${cusVO.cusBir}'/>" />
		<form:errors path="cusBir" style="color:red;" />
	</div>
	<div class="mb-3">
		<!-- path : id="hobbyList" name="hobbyList" -->
		<label for="hobbyList" class="form-label">취미</label> 
		<form:checkbox path="hobbyList" value="Music" label="Music" />
		<form:checkbox path="hobbyList" value="Movie" label="Movie" />
		<form:checkbox path="hobbyList" value="Sports" label="Sports" />
	</div>
	<div class="mb-3">
		<!-- 성별 등록(남성 : male, 여성 : female, 기타 : others) -->
		<label for="gender" class="form-label">성별</label> 
		<form:radiobutton path="gender" value="male" label="male" />
		<!-- cusVO.setGender("female"); -->
		<form:radiobutton path="gender" value="female" label="female" />
		<form:radiobutton path="gender" value="others" label="others" />
	</div>
	<div class="mb-3">
		<!-- 국적(한개 선택)->select박스 -->
		<label for="nationality" class="form-label">국적</label> 
		<form:select path="nationality" items="${nationalityMap}" />
	</div>
	<div class="mb-3">
		<!-- 고객 : 소유 자동차(List<CarVO> carVOList)  = 1 : N -->
		<div class="card" style="width: 100%;">
		  <div class="card-header">
		    소유자동차
		  </div>
		  <ul class="list-group list-group-flush">	
		  <!-- 
		  cusVO : CusVO cusVO
		  cusVO.carVOList : List<CarVO> carVOList
		   -->
		  <c:forEach var="carVO" items="${cusVO.carVOList}" varStatus="stat" >	  
		    <li class="list-group-item">
		    	<input type="hidden" name="carVOList[${stat.index}].cusNum" value="${carVO.cusNum}" />
		    	<input type="text" class="form-control" name="carVOList[${stat.index}].carNum"
		    		style="width:25%;float:left;" placeholder="자동차번호"
		    		 value="${carVO.carNum}" required/>
		    	<input type="text" class="form-control" name="carVOList[${stat.index}].mnfNum"
					style="width:25%;float:left;" placeholder="제조 번호"
					value="${carVO.mnfNum}" required />
		    	<input type="text" class="form-control" name="carVOList[${stat.index}].dt"
		    		style="width:25%;float:left;" placeholder="연식"
		    		value="<fmt:formatDate pattern='yyyy-MM-dd' value='${carVO.dt}' />"	 />
		    	<input type="text" class="form-control" name="carVOList[${stat.index}].dist"
		    		style="width:25%;float:left;" placeholder="주행거리"
		    		value="${carVO.dist}" />
		    </li>
		    </c:forEach>
		  </ul>
		</div>
	</div>
	<div class="mb-3">
		<button type="submit" class="btn btn-primary btn-lg">등록</button>
	</div>
</div>
</form:form>






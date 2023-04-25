<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
				$("#zipCode").val(data.zonecode);
				$("#address").val(data.address);
				$("#detAddress").val(data.buildingName);
			}
		}).open();
	});
	
	
	//직원번호 자동등록
	//아작났어유..피씨다타써
	//피 : processType:false는 파일업로드 시 사용(let formData = new formData())
	//씨 : contentType:"application/json;charset:utf-8"(보내는 타입)
	//다 : data:JSON.stringify(data);
	//타 : type:"post"
	//써 : success:function(){}
	$.ajax({
		url:"/emp/getEmpNum",
		type:"post",
		beforeSend : function(xhr) {   // 데이터 전송 전  헤더에 csrf값 설정
            xhr.setRequestHeader("${_csrf.headerName}", "${_csrf.token}");
		},
		success:function(result){
			console.log("result : " + result);
			$("#empNumAjax").val(result);
		}
	});
	
	//매니저 선택하기
	$("#btnEmpMjNum").on("click",function(){
		$.ajax({
			url:"/emp/getEmpAll",
			type:"post",
			beforeSend : function(xhr) {   // 데이터 전송 전  헤더에 csrf값 설정
                xhr.setRequestHeader("${_csrf.headerName}", "${_csrf.token}");
			},
			success:function(result){
				//result : List<EmpVO> empVOList
				let str = "";
				
				$.each(result,function(index, empVO){
					console.log("empVO.empNum : " + empVO.empNum);
					console.log("empVO.empNm : " + empVO.empNm);
					//<th scope="row"> : 해당 셀이 행(row)을 위한 헤더 셀임을 명시함.
					str += "<tr class='trSelect'><th scope='col'>"+(index+1)+"</th>";
					str += "<td>"+empVO.empNum+"</td><td>"+empVO.empNm+"</td></tr>";
				});
				
				console.log("str : " + str);
				//.html() : 새로고침 / .append() : 누적
				$("#trAdd").html(str);	//초기화
// 				$("#trAdd").append(str);
			}
		});
	});
	
	//동적으로 생성된 요소의 이벤트 처리
// 	달러(".trSelect").on("click",function(){})은 작동을 안할것임
	$(document).on("click",".trSelect",function(){
		//this : tr이 여러개인데 그 중 클릭한 바로 그 tr
		//td들을 가져옴. 그 안에 데이터를 가져와보자
		let resultStr = $(this).children().map(function(){
			return $(this).text();}).get().join(",");
		console.log("resultStr : " + resultStr);
		
		//resultStr : 5,EMP005,이정재 => split(",")을 해서 배열로 만들고
		//[1]는 매니저번호(empMjNum)로 입력, [2]는 매니저명(empMjNm)으로 입력
		//arr[0] : 5 / arr[1] : EMP005 / arr[2] : 이정재
		let arr = resultStr.split(",");
		$("#empMjNum").val(arr[1]);
		$("#empMjNm").val(arr[2]);
	});
});
</script>
<!-- 요청URI : /emp/createPost
	요청파라미터 : {empNum=EMP001,zipCode=12345,address=대전...,empMjNum=}
	요청방식 : post
 -->
<form:form modelAttribute="empVO" action="/emp/createPost" method="post"
		 class="row g-3">
	<!-- 직원번호 시작 -->
	<div class="col-md-6" style="clear:both;">
	  <label for="empNum" class="form-label">직원번호</label>
<!-- 	  <input type="text" name="empNum" class="form-control" id="empNum" -->
<%-- 	  	value="${empNum}" placeholder="직원번호를 입력해주세요" /> --%>
<%-- 	  <form:input path="empNum" class="form-control" placeholder="직원번호를 입력해주세요" /> --%>
	  <input type="text" name="empNum" class="form-control" id="empNumAjax"
	  	value="" placeholder="직원번호를 입력해주세요" readonly />
	</div>
	<!-- 직원번호 끝 -->
	<!-- 주소 시작 -->
	<div class="col-md-2">
    	<label for="zipCode" class="form-label">우편번호</label>
    	<input type="text" name="zipCode" class="form-control" id="zipCode"
    		placeholder="우편번호를 검색해주세요" readonly />
    	<button type="button" class="btn btn-primary" id="btnPostno">검색</button>
	</div>
	<div class="col-12">
	    <label for="address" class="form-label">주소</label>
	    <input type="text" name="address" class="form-control" id="address" 
	    	placeholder="주소를 검색해주세요" readonly />
	</div>
	<div class="col-12">
	    <label for="detAddress" class="form-label">상세주소</label>
	    <input type="text" name="detAddress" class="form-control" id="detAddress" 
	    	placeholder="상세주소를 입력해주세요">
	</div>
    <!-- 주소 끝 -->
    <!-- 연락처 시작 -->
	<div class="col-md-6">
	  <label for="empPne" class="form-label">연락처</label>
	  <input type="text" name="empPne" class="form-control" id="empPne"
	  	placeholder="연락처를 입력해주세요" required />
	  <form:errors path="empPne" />
	</div>
	<!-- 연락처 끝 -->
    <!-- 직원명 시작 -->
	<div class="col-md-6">
	  <label for="empNm" class="form-label">직원명</label>
	  <input type="text" name="empNm" class="form-control" id="empNm"
	  	placeholder="직원명을 입력해주세요" required />
	  <form:errors path="empNm" />
	</div>
	<!-- 직원명 끝 -->
    <!-- 급여 시작 -->
	<div class="col-md-6">
	  <label for="empPay" class="form-label">급여</label>
	  <input type="number" name="empPay" class="form-control" id="empPay"
	  	placeholder="급여를 입력해주세요" required />
	  <form:errors path="empPay" />
	</div>
	<!-- 급여 끝 -->
	<!-- 매니저 등록 시작 -->
	<div class="col-md-6">
	  <label for="empMjNm" class="form-label">매니저명</label>
	  <input type="hidden" name="empMjNum" class="form-control" id="empMjNum" />
	  <input type="text" class="form-control" id="empMjNm"
	  	placeholder="직원명을 입력해주세요" readonly />
	  <button type="button" class="btn btn-primary" id="btnEmpMjNum"
	  	 data-toggle="modal" data-target="#exampleModal"
	  >검색</button>
	</div>
	<!-- 매니저 등록 끝 -->
	<div class="col-12">
	  <button type="submit" class="btn btn-primary">등록</button>
	</div>
</form:form>
<!-- Modal -->
<div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h1 class="modal-title fs-5" id="exampleModalLabel">Modal title</h1>
        <button type="button" class="btn-close" data-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <!-- -------- 직원 목록 시작 ------------- -->
        <div class="bd-example">
		  <table class="table table-hover">
		      <thead>
		    <tr>
		      <th scope="col">#</th>
		      <th scope="col">직원번호</th>
		      <th scope="col">이름</th>
		    </tr>
		  </thead>
		  <tbody id="trAdd">
		    <tr>
		      <th scope="row">1</th>
		      <td>Mark</td>
		      <td>Otto</td>
		    </tr>
		  </tbody>
		  </table>
		</div>
        <!-- -------- 직원 목록 끝 ------------- -->
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Save changes</button>
      </div>
    </div>
  </div>
</div>





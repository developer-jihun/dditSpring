<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript" src="/resources/js/jquery-3.6.0.js"></script>
<script type="text/javascript">
//document의 요소들이 모두 로딩된 후 실행
$(function(){
	//이미지 미리보기 시작//////////////////////////
	$("#input_imgs").on("change",handleImgFileSelect);
	
	function handleImgFileSelect(e){
		let files = e.target.files;
		let fileArr = Array.prototype.slice.call(files);
		fileArr.forEach(function(f){
			if(!f.type.match("image.*")){
				alert("이미지만 가능합니다.");
				return;
			}
			
			let reader = new FileReader();
			reader.onload = function(e){
				//<img src="AFSOIASFDPOFID" />
				let img_html = "<img src=\"" + e.target.result + "\" style='width:100%;' />";
				$(".bg-register-image").html(img_html);
			}
			reader.readAsDataURL(f);
		});
	}
	//이미지 미리보기 끝//////////////////////////
	
	$(".btn-icon-split").on("click",function(){
		console.log("개똥이");
		//상품분류코드 자동생성
		//아작났어유..피씨다타써
		/*
		url : 요청경로
		contentType:보내는 데이터의 타입
		dataType:응답 데이터 타입
		data : 요청 시 전송할 데이터
		type : 요청방식. get, post, put
		*/
		$.ajax({
			url:"/lprod/getLprodGu",
			type:"post",
			beforeSend : function(xhr) {   // 데이터 전송 전  헤더에 csrf값 설정
                xhr.setRequestHeader("${_csrf.headerName}", "${_csrf.token}");
			},
			success:function(result){
				console.log("result : " + result);
				$("input[name='lprodGu']").val(result);
			}
		});
	});
	
	$("#btnRegist").on("click",function(){
		let lprodGu = $("#lprodGu").val();
		let lprodNm = $("#lprodNm").val();
		
		if(jQuery.trim(lprodGu)==""){
			alert("상품분류코드를 작성해주세요");
			$("#lprodGu").focus();
			return false;
		}
		
		if(jQuery.trim(lprodNm)==""){
			alert("상품분류명을 작성해주세요");
			$("#lprodNm").focus();
			return false;
		}
		
		//[submit]을 실행
		//달러("#frm").submit(function(){alert("등록이 진행됩니다.");});
		/*
		요청URI : /lprod/createPost
		요청파라미터 : {"lprodId":"10","lprodGu":"P404","lprodNm":"간식류"}
		요청방식 : post 
		*/
		$("form").submit();
	});
});
</script>
<div class="container">

	<div class="card o-hidden border-0 shadow-lg my-5">
		<div class="card-body p-0">
			<!-- Nested Row within Card Body -->
			<div class="row">
				<div class="col-lg-5 d-none d-lg-block bg-register-image"></div>
				<div class="col-lg-7">
					<div class="p-5">
						<div class="text-center">
							<h1 class="h4 text-gray-900 mb-4">Create an Account!</h1>
						</div>
						<form id="frm" class="user" action="/lprod/createPost"
							 method="post" enctype="multipart/form-data"
								accept-charset="UTF-8">
							<div class="form-group row">
								<div class="col-sm-6 mb-3 mb-sm-0">
								<!-- 변경 제약 
									disabled : 값이 전달 안됨
									readonly : 파라미터로 값이 전달됨
								 -->
									<input type="text" class="form-control form-control-user"
										id="lprodId" name="lprodId" 
										value="${lprodId}" 
										placeholder="상품분류 아이디"
										readonly>
								</div>
								<div class="col-sm-6">
									<a href="#" class="btn btn-info btn-icon-split">
                                        <span class="icon text-white-50">
                                            <i class="fas fa-info-circle"></i>
                                        </span>
                                        <span class="text">분류코드 자동생성</span>
                                    </a>
								</div>
							</div>
							<div class="form-group">
								<input type="text" class="form-control form-control-user"
									id="lprodGu" name="lprodGu" placeholder="상품분류 코드" 
									value="${data.lprodGu}" required />
							</div>
							<div class="form-group">
								<input type="text" class="form-control form-control-user"
									id="lprodNm" name="lprodNm" placeholder="상품분류 명"
									value="${data.lprodNm}" required />
							</div>
							<div class="form-group">
							  <input class="form-control" type="file" id="input_imgs" 
							  	name="uploadFile" multiple />
							</div>
							<a href="#" id="btnRegist" class="btn btn-primary btn-user btn-block">
								등록 </a>
							<hr />
							<a href="/lprod/list" class="btn btn-google btn-user btn-block">
								<i class="fab fa-google fa-fw"></i> 목록보기
							</a> 
						</form>
						<hr>
						<div class="text-center">
							<a class="small" href="forgot-password.html">Forgot Password?</a>
						</div>
						<div class="text-center">
							<a class="small" href="login.html">Already have an account?
								Login!</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

</div>
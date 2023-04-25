package kr.or.ddit.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.service.EmpService;
import kr.or.ddit.util.ArticlePage;
import kr.or.ddit.vo.EmpVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/emp")
@Controller
public class EmpController {
	@Autowired
	EmpService empService;
	
	//요청URI : /emp/create
	//요청방식 : get
	@GetMapping("/create")
	public String create(@ModelAttribute("empVO") EmpVO empVO
			, Model model) {
		
		//다음 직원번호를 가져옴
		//서비스의 메서드 호출
		String empNum = this.empService.getEmpNum();
		//<form:form modelAttribute="empVO"
		empVO.setEmpNum(empNum);
		//달러{empNum}
		model.addAttribute("empNum", empNum);		
		
		//forwarding
		return "emp/create";
	}
	
	//신규 직원번호 가져오기
	//요청URI : /emp/getEmpNum
	//요청방법 : post
	//ResponseBody : JSON데이터로 리턴
	@ResponseBody
	@PostMapping("/getEmpNum")
	public String getEmpNum() {
		String empNum = this.empService.getEmpNum();
		
		log.info("empNum : " + empNum);
		
		return empNum;
	}
	
	//신규 직원 등록하기
	/*
	 요청URI : /emp/createPost
	요청파라미터 : {empNum=EMP001,zipCode=12345,address=대전...,empMjNum=
				zipCode=12345,detAddress=주소상세}
	요청방식 : post
	 */
	@PostMapping("/createPost")
	public String createPost(@Valid @ModelAttribute EmpVO empVO,
			String zipCode, String address, String detAddress,
			@RequestParam(required=false,defaultValue="new") String mode,
			Errors errors) {
		log.info("empVO : " + empVO);
		log.info("zipCode : " + zipCode + ", address : " + address +
				 "detAddress : " + detAddress);
		//mode
		//- create에서 오면 new
		//- detail에서 오면 update
		log.info("mode : " + mode);
		
		String oldEmpNum = "";
		
		//update를 실행한다면..
		if(mode.equals("update")) {
			oldEmpNum = empVO.getEmpNum();//기본키 데이터 백업
		}
		
		if(errors.hasErrors()) {
			//forwarding
			return "emp/create";
		}
		
		//우편번호 + 주소 + 상세주소 => empVO의 empAddr 멤버변수에 setting하기
		String empAddr = zipCode + " " + address + " " + detAddress;
		
		empVO.setEmpAddr(empAddr);
		
		//empVO : {empNum=EMP001,zipCode=12345,address=대구...,empMjNum=}
		int result = this.empService.createPost(empVO);
		//empVO : {empNum=EMP003,zipCode=12345,address=대구...,empMjNum=}
		
		//redirect
		//update
		if(mode.equals("update")) {
			return "redirect:/emp/detail?empNum="+oldEmpNum;
		}else {
			//insert
			return "redirect:/emp/detail?empNum="+empVO.getEmpNum();
		}
	}
	
	/*
	 모든 직원 정보 가져오기
	 요청URI : /emp/getEmpAll
	 요청방식 : post	 
	 da(응)taT(답)ype : json
	 */
	@ResponseBody
	@PostMapping("/getEmpAll")
	public List<EmpVO> getEmpAll(){
		//모든 직원 정보 가져오기
		List<EmpVO> empVOList = this.empService.getEmpAll();
		
		log.info("empVOList : " + empVOList);
		
		return empVOList;
	}
	
	//직원 상세보기
	/*
	 요청URI : /emp/detail?empNum=EMP007
	 요청파라미터 : empNum=EMP007
	 요청방식 :get
	 
	 골뱅이RequestParam("empNum") String empNum 
	 */
	@GetMapping("/detail")
	public String detail(String empNum, @ModelAttribute EmpVO empVO,
			Model model) {
		log.info("empNum : " + empNum);
		log.info("empVO : " + empVO);
		
		//상세화면은 등록화면과 동일 + empVO데이터로 채우면 됨
		List<EmpVO> empVOList = this.empService.detail(empVO);
		log.info("empVOList : " + empVOList);
		
		model.addAttribute("data", empVOList);
		
		//emp폴더의 detail.jsp를 forwarding
		return "emp/detail";
	}
	
	/*
	 요청URI : /emp/deletePost
	 요청파라미터 : let data = {"empNum":empNum};
	 요청방식 : post
	 응답데이터타입 : json
	 응답데이터 : {"result":"1"}
	 */
	@ResponseBody
	@PostMapping("/deletePost")
	public Map<String,String> deletePost(@RequestBody EmpVO empVO){
		//empVO : {empNum=EMP001,zipCode=null,address=null...,empMjNum=null}
		log.info("empVO : " + empVO);
		
		int result = this.empService.deletePost(empVO);
		
		Map<String,String> map = new HashMap<String, String>();
		map.put("result", result+"");
		
		log.info("map : " + map);
		
		return map;
	}
	
	/*
	 요청URI : /emp/list
	 요청방식 : get
	 요청파라미터 : show=10&keyword=개똥이&currentPage=1
	 
	 map은 RequestParam
	 vo는 ModelAttribute
	 json은 RequestBody
	 
	 ?show=10&keyword=김철수 처럼 currentPage가 없을 수 있음(required=false)
	 
	 /emp/list
	 */
	@GetMapping("/list")
	public String list(Model model
			, @RequestParam Map<String,String> map
			, @RequestParam(value="currentPage",required=false,defaultValue="1") int currentPage
			, @RequestParam(value="show",required=false,defaultValue="10") int size){
		
		log.info("map : " + map);
		log.info("currentPage : " + currentPage);
		log.info("size : " + size);
		
		//map{show=10,keyword=개똥이,currentPage:1}
		map.put("currentPage", currentPage+"");
		
		//1) /emp/list : show가 null
		//2) /emp/list?show= : show의 값이 없음
		if(map.get("show")==null || map.get("show").length()<1) {
			map.put("show", "10");
		}
		//map{show=10,currentPage=1}
		//map으로 파라미터를 받아서 매퍼xml에서 검색 조건으로 사용
		List<EmpVO> empVOList =  this.empService.list(map);
		log.info("empVOList : " + empVOList);
		
		//total
		int total = this.empService.getTotal(map);
		log.info("total : " + total);
		
		//size <- map.get("show")
//		int size = Integer.parseInt(map.get("show"));
		
		log.info("empVOList : " + empVOList);
		//empVOList 객체를 페이징 처리해보자
		//new ArticlePage<EmpVO>(total, currentPage, size, content)
		model.addAttribute("data", new ArticlePage<EmpVO>(total, currentPage, size, empVOList));
		//forwarding
		return "emp/list";
	}
	
	/** 직원 1명의 정보를 리턴
	 요청URI : /emp/showMj
	 요청파라미터 : let data = {"empMjNum":empMjNum};
	 요청방식 : post
	 응답데이터타입 : json
	 */
	@ResponseBody
	@PostMapping("/showMj")
	public EmpVO showMj(@RequestBody EmpVO empVO) {
		log.info("empVO : " + empVO);
		
		empVO = this.empService.showMj(empVO);
		log.info("empVO : " + empVO);
		
		return empVO;
	}
}

















package kr.or.ddit.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import kr.or.ddit.service.BookService;
import kr.or.ddit.service.LprodService;
import kr.or.ddit.util.ArticlePage;
import kr.or.ddit.vo.AttachVO;
import kr.or.ddit.vo.BookVO;
import kr.or.ddit.vo.LprodVO;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;

import org.apache.commons.dbcp2.BasicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;


/* Controller 어노테이션
  스프링 프레임워크에게 "이 클래스는 웹 브라우저(클라이언트)의 요청(request)을
  받아들이는 컨트롤러야"라고 알려주는 것임.
  스프링은 servlet-context.xml의 context:component-scan의 설정에 의해
  이 클래스를 자바빈 객체로 미리 등록(메모리에 바인딩)
 */
@RequestMapping("/lprod")
@Slf4j
@Controller
public class LprodController {
	@Autowired
	LprodService lprodService;
	
	//요청URI : /lprod/list?currentPage=2&size=10&keyword=개똥이
	//1) 요청파라미터 : currentPage=2&size=10&keyword=개똥이
	//2) 요청파라미터 : 
	//required : 필수니?
	//defaultValue : 없으면 어쩔티비?  defaultValue="1" : 1페이지로 간주함
	//currentPage=2 파라미터의 타입은 String.. but, int 타입의 매개변수로 자동 형변환 가능 
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(ModelAndView mav,
			 @RequestParam(value="currentPage", required=false, defaultValue="1") int currentPage,
			 @RequestParam(value="size", required=false, defaultValue="10") int size,
			 @RequestParam(value="keyword", required=false) String keyword
			 ) {
		//검색 조건
		Map<String,String> map = new HashMap<String, String>();
				
		//1) 전체 행의 수 구하기(total)
		int total = this.lprodService.getTotal(keyword);
		//3) 한 페이지에 보여질 행의 수(size)
//		int size = 10;
		
		//map{"size":"10","currentPage":"1","keyword":""}
		map.put("size", size+"");
		map.put("currentPage", currentPage+"");
		map.put("keyword", keyword);
		log.info("map : " + map);
		
		List<LprodVO> lprodVOList = this.lprodService.list(map);
		
		//페이징 처리 전.. data
		//mav.addObject("data", lprodVOList);
		//페이징 처리 후..
		mav.addObject("data"
			, new ArticlePage<LprodVO>(total, currentPage, size, lprodVOList));
		//jsp경로
		//tiles-config.xml의 
		// */*은 lprod/list와 {1}/{2}.jsp에 의해 lprod/list.jsp가 forwarding됨
		mav.setViewName("lprod/list");
		
		return mav;
	}
	
	/*
	 요청URI : /lprod/create
	 방식 : GET
	*/
	//골뱅이RequestMapping(value="/create",method=RequestMethod.GET)
	@GetMapping("/create")
	public String create(Model model) {
		//상품분류 아이디 자동생성
		int lprodId = this.lprodService.getLprodId();
		
		//lprodId 데이터를 model에 담아서 create.jsp로 보내줌
		model.addAttribute("lprodId", lprodId);
		
		//상품대분류 등록 jsp를 forwarding
		//1. 타일즈의 index.jsp 우선 적용
		//2. <definition name="*/*" extends="tiles-layout">
		//		/WEB-INF/views/lprod/create.jsp => tiles의 body로 include됨
		//forwarding
		return "lprod/create";
	}
	
	/*
	 url:"/lprod/getLprodGu",
	 type:"post",
	 */
	//골뱅이RequestMapping(value="/getLprodGu",method=RequestMethod.POST)
	//비동기 방식 요청 처리 시 골뱅이ResponseBody를 사용함=>JSON데이터로 변환하여 리턴해줌
	@ResponseBody
	@PostMapping("/getLprodGu")
	public String getLprodGu() {
		log.info("getLprodGu에 왔다");
		
		//비즈니스(기능, service) 로직
		String lprodGu = this.lprodService.getLprodGu();
		log.info("lprodGu : " + lprodGu);
		
		return lprodGu;
	}
	
	/* 
	요청URI : /lprod/createPost
	요청파라미터 : {"lprodId":"10","lprodGu":"P404","lprodNm":"간식류"}
	요청방식 : post 
	*/
	@PostMapping("/createPost")
	public String createPost(@ModelAttribute LprodVO lprodVO,
			Model model, HttpServletRequest request) throws SQLException, UnsupportedEncodingException {
		
		String uploadFolder 
			= "C:\\eclipse_202006\\workspace\\springProj\\src\\main\\webapp\\resources\\upload";
		
		//make folder 시작-----------------------
		File uploadPath = new File(uploadFolder, getFolder());
		log.info("uploadPath : " + uploadPath);
		
		//만약 연/월/일 해당 폴더가 없다면 생성
		if(uploadPath.exists()==false) {
			uploadPath.mkdirs();
		}
		//makd folder 끝-----------------------
		
		//lprodVO : LprodVO[lprodId:19,lprodGu:P413,lprodNm:간식류,
		//uploadFile:있음,attachVOList=null]
		log.info("lprodVO : " + lprodVO);
		//<input class="form-control" type="file" id="input_imgs" name="uploadFile" />
		MultipartFile[] multipartFiles = lprodVO.getUploadFile();
		log.info("multipartFiles : " + multipartFiles);
		
		List<AttachVO> voList = new ArrayList<AttachVO>();
		int seq = 1;
		//uploadFile 정보를 통해서 attachVOList에 값들을 setting해줘야 함
		//배열로부터 하나씩 파일을 꺼내오자
		for(MultipartFile multipartFile : multipartFiles) {
			AttachVO vo = new AttachVO();
			//실제 파일명
			String uploadFileName = multipartFile.getOriginalFilename();
			
			log.info("-----------------");
			log.info("fileName : " + uploadFileName);
			log.info("fileSize : " + multipartFile.getSize());
			log.info("contentType : " + multipartFile.getContentType());//MIME타입
			
			//----------같은날 같은 이미지를 업로드 시 파일 중복 방지 시작 ----------------
			//java.util.UUID => 랜덤값 생성
			UUID uuid = UUID.randomUUID();	//임의의 값을 생성
			//원래의 파일 이름과 구분하기 위해 _를 붙임
			uploadFileName = uuid.toString() + "_" + uploadFileName;
			//----------같은날 같은 이미지를 업로드 시 파일 중복 방지 끝 ----------------
			
			//파일 객체 설계(복사할 대상 경로, 파일명)
			File saveFile = new File(uploadPath, uploadFileName);
			
			try {
				//파일 복사가 일어남
				multipartFile.transferTo(saveFile);
				
				//썸네일 처리 시작-------------------
				//파일이 이미지인지 체킹
				if(checkImageType(saveFile)) {//이미지라면 실행
					FileOutputStream thumbnail = new FileOutputStream(
							new File(uploadPath, "s_"+uploadFileName)
							);
					//썸네일 생성(원본파일, 대상,가로크기,세로크기)
					Thumbnailator.createThumbnail(multipartFile.getInputStream(),
							thumbnail,100,100);
					thumbnail.close();
				}
				//썸네일 처리 끝-------------------
			}catch (IllegalStateException e) {
				log.error(e.getMessage());
				return "0";
			}catch(IOException e) {
				log.error(e.getMessage());
				return "0";
			}
			
			Long l = multipartFile.getSize();
			
			vo.setSeq(seq++);
			// /2023/01/30/sadflkasdlf_개똥이.jpg
			String filename = "/" + getFolder().replace("\\", "/") + "/" +
					uploadFileName;
			vo.setFilename(filename);			
			vo.setFilesize(l.intValue());
			// /2023/01/30/s_sadflkasdlf_개똥이.jpg
			String thumbFileName = "/" + getFolder().replace("\\", "/") + "/s_" +
					uploadFileName;
			vo.setThumbnail(thumbFileName);
			//전사적 아이디(ex) P301)
			vo.setEtpId(lprodVO.getLprodGu());
			voList.add(vo);
		}
		//lprodVO : LprodVO[lprodId:19,lprodGu:P413,lprodNm:간식류,
		//uploadFile:있음,attachVOList=있음]
		lprodVO.setAttachVOList(voList);
		
		log.info("(after) lprodVO : " + lprodVO);
		
		int result = this.lprodService.createPost(lprodVO);
		log.info("result : " + result);
		
		if(result>0) {//입력 성공
			// /lprod/detail
			return "redirect:/lprod/detail?lprodGu="+lprodVO.getLprodGu();
		}else {//입력실패
			// /lprod/create => create.jsp에 데이터가 입력되어있기
			model.addAttribute("data", lprodVO);
			model.addAttribute("lprodId", lprodVO.getLprodId());
			//forwarding
			return "lprod/create";
		}
	}
	
	//요청URI : /lprod/detail?lprodGu=P404
	//요청URL : /lprod/detail
	//요청파라미터 : lprodGu=P404
	//요청방식 : GET
	@GetMapping("/detail")
	public String detail(@ModelAttribute LprodVO lprodVO,
			Model model) {
//		lprodVO : LprodVO[lprodId:0,lprodGu:P404,lprodNm:null]
		log.info("lprodVO : " + lprodVO);
		
		//lprodGu의 값이 P404인 상품분류 정보 1 행을 가져오자
		lprodVO = this.lprodService.detail(lprodVO);
		//lprodVO(후) : LprodVO [lprodId=10, lprodGu=P404, lprodNm=간식류]
		model.addAttribute("data", lprodVO);
		
		//forwarding
		//detail.jsp는 create.jsp를 기본폼으로 하고, data를 받아서 정보를 화면에 출력
		return "lprod/detail";
	}
	
	//상품분류 정보 변경
	//요청URL : /lprod/updatePost
	//요청파라미터 : {"lprodId":"10","lprodGu":"P404","lprodNm":"간식류변경"}
	//요청방식 : post
	@PostMapping("/updatePost")
	public String updatePost(@ModelAttribute LprodVO lprodVO,
			Model model) {
		log.info("lprodVO : " + lprodVO);
		
		//정보 변경
		int result = this.lprodService.updatePost(lprodVO);
		log.info("result : " + result);
		
		if(result>0) {//정보 변경 성공
			//detail로 되돌아오기
			return "redirect:/lprod/detail?lprodGu="+lprodVO.getLprodGu();
		}else {//정보 변경 실패=>lprodVO가 넘어온 그대로 detail.jsp로 넘겨주자
			//lprodVO(후) : LprodVO [lprodId=10, lprodGu=P404, lprodNm=간식류변경]
			model.addAttribute("data", lprodVO);
			
			//forwarding
			//detail.jsp는 create.jsp를 기본폼으로 하고, data를 받아서 정보를 화면에 출력
			return "lprod/detail";
		}
	}
	
	//상품분류정보 삭제
	//요청URI : /lprod/deletePost
	//요청파라미터 : {"lprodId":"10","lprodGu":"P404","lprodNm":"간식류변경"}
	//요청방식 : post
	@PostMapping("/deletePost")
	public String deletePost(@ModelAttribute LprodVO lprodVO) {
		log.info("lprodVO : " + lprodVO);
		
		//삭제 작업
		int result = this.lprodService.deletePost(lprodVO);
				
		if(result>0) {//삭제 성공 : list로 재요청
			return "redirect:/lprod/list";
		}else {//삭제 실패 : detail로 재요청
			return "redirect:/lprod/detail?lprodGu="+lprodVO.getLprodGu();
		}
	}
	
	//요청URL : /lprod/uploadForm
	//요청방식 : get
	@GetMapping("/uploadForm")
	public String uploadForm() {
		//forwarding
		return "lprod/uploadForm";
	}
	
	/*
	 요청URL : /lprod/uploadFormAction
  	요청파라미터 : uploadFile이라는 이름의 파일객체 
  	요청방식 : post
	*/
	@PostMapping("/uploadFormAction")
	public String uploadFormAction(MultipartFile uploadFile) {
		//MultipartFile : 스프링에서 제공해주는 타입
		/*
		 String, getOriginalFileName() : 업로드 되는 파일의 실제 파일명
		 boolean, isEmpty() : 파일이 없다면 true
		 long, getSize() : 업로드되는 파일의 크기
		 transferTo(File file) : 파일을 저장    
		 */
		//파일이 저장되는 경로
		String uploadFolder = "c:\\upload";
		
		//make folder 시작-------------------------------------------------------
		//c:\\upload\\2023\\01\\27
		File uploadPath = new File(uploadFolder, getFolder());
		log.info("upload Path : " + uploadPath);
		
		//만약 연/월/일 해당 폴더가 없다면 생성
		if(uploadPath.exists()==false) {
			uploadPath.mkdirs();
		}
		//make folder 끝--------------------------------------------------------
		
		//파일명
		String uploadFileName = uploadFile.getOriginalFilename();		
		log.info("----------------");
		log.info("이미지 명 : " + uploadFileName);
		log.info("파일 크기 : " + uploadFile.getSize());
		
		//------------파일명 중복 방지 시작-----------------
		//java.util.UUID => 랜덤값을 생성
		UUID uuid = UUID.randomUUID();
		//ERASDFERASDFA_개똥이.jpg
		uploadFileName = uuid.toString() + "_" + uploadFileName;
		//------------파일명 중복 방지 끝-----------------
		
		//(어디에, 무엇을)
		//계획을 세움
		//C:\\upload\\2023\\01\\27\\ca80147d-2fe2-4e46-b80c-323251417b09_nullPointer.jpg
		File saveFile = new File(uploadPath,uploadFileName);
		
		try {
			//계획 실행. 파일 복사됨(클라이언트의 파일을 서버의 공간으로 복사)
			uploadFile.transferTo(saveFile);
			
			AttachVO attachVO = new AttachVO();
			//1) filename : /2023/01/27/323251417b09_nullPointer.jpg
			String filename = "/" + getFolder().replace("\\", "/") + "/" +
									uploadFileName;
			attachVO.setFilename(filename);
			
			//2) filesize
			Long l = uploadFile.getSize();
			attachVO.setFilesize(l.intValue());
			
			//3) thumbnail : /2023/01/27/s_323251417b09_nullPointer.jpg
			String thumb = 	"/" + getFolder().replace("\\", "/") + "/s_" +
							uploadFileName;		
			attachVO.setThumbnail(thumb);
			
			log.info("attachVO : " + attachVO);
			
			//이미지인지 체킹
			try {
				//MIME 타입을 가져옴. image/jpeg
				String contentType = Files.probeContentType(saveFile.toPath());
				log.info("contentType : " + contentType);
				//MIME 타입 정보가 image로 시작하는지 여(true)부(false)
				if(contentType.startsWith("image")) {//이미지가 맞다면 true
					//C:\\upload\\2023\\01\\27\\s_323251417b09_nullPointer.jpg
					FileOutputStream thumbnail = new FileOutputStream(
							new File(uploadPath, "s_" + uploadFileName)
						);
					//섬네일 생성
					Thumbnailator.createThumbnail(uploadFile.getInputStream(),thumbnail,100,100);
					thumbnail.close();
				}
				//ATTACH 테이블에 insert
				int result = this.lprodService.uploadFormAction(attachVO);
				log.info("result : " + result);
			}catch(IOException e) {
				e.printStackTrace();
			}
		} catch (IllegalStateException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return "redirect:/lprod/uploadForm";
	}
	
	/*
	요청URL : /lprod/uploadFormMultiAction
	  요청파라미터 : uploadFile이라는 이름의 파일객체 
	  요청방식 : post
	*/
	@PostMapping("/uploadFormMultiAction")
	public String uploadFormMultiAction(MultipartFile[] uploadFile) {
		//파일이 저장되는 경로
		String uploadFolder = "c:\\upload";
		
		//make folder 시작-------------------------------------------------------
		//c:\\upload\\2023\\01\\27
		File uploadPath = new File(uploadFolder, getFolder());
		log.info("upload Path : " + uploadPath);
		
		//만약 연/월/일 해당 폴더가 없다면 생성
		if(uploadPath.exists()==false) {
			uploadPath.mkdirs();
		}
		//make folder 끝--------------------------------------------------------
		
		for(MultipartFile multipartfile : uploadFile) {
			//파일명
			String uploadFileName = multipartfile.getOriginalFilename();
			
			log.info("----------------");
			log.info("이미지 명 : " + uploadFileName);
			log.info("파일 크기 : " + multipartfile.getSize());
			log.info("컨텐츠(MIME)타입 : " + multipartfile.getContentType());
			
			//------------파일명 중복 방지 시작-----------------
			//java.util.UUID => 랜덤값을 생성
			UUID uuid = UUID.randomUUID();
			//ERASDFERASDFA_개똥이.jpg
			uploadFileName = uuid.toString() + "_" + uploadFileName;
			//------------파일명 중복 방지 끝-----------------
			
			//(어디에, 무엇을)
			//계획을 세움
			//c:\\upload\\2023\\01\\27\\ERASDFERASDFA_개똥이.jpg
			File saveFile = new File(uploadPath,uploadFileName);
			
			try {
				//계획 실행. 파일 복사됨(클라이언트의 파일을 서버의 공간으로 복사)
				multipartfile.transferTo(saveFile);
				
				//이미지인지 체킹
				try {
					//MIME 타입을 가져옴. image/jpeg
					String contentType = Files.probeContentType(saveFile.toPath());
					log.info("contentType : " + contentType);
					//MIME 타입 정보가 image로 시작하는지 여(true)부(false)
					if(contentType.startsWith("image")) {//이미지가 맞다면 true
						//C:\\upload\\2023\\01\\27\\s_323251417b09_nullPointer.jpg
						FileOutputStream thumbnail = new FileOutputStream(
								new File(uploadPath, "s_" + uploadFileName)
							);
						//섬네일 생성
						Thumbnailator.createThumbnail(multipartfile.getInputStream(),thumbnail,100,100);
						thumbnail.close();
					}
				}catch(IOException e) {
					e.printStackTrace();
				}
			} catch (IllegalStateException e) {
				log.error(e.getMessage());
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		
		//redirect
		return "redirect:/lprod/uploadForm";
	}
	
	//연/월/일 폴더 생성
	public static String getFolder() {
		//2023-01-27 형식(format) 지정
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//날짜 객체 생성(java.util 패키지)
		Date date = new Date();
		//2023-01-27=>2023\\01\\27
		String str = sdf.format(date);
		//단순 날짜 문자를 File객체의 폴더 타입으로 바꾸기
		//2023\\01\\27
		return str.replace("-", File.separator);
	}
	
	//* 썸네일 처리 전 이미지 파일의 판단
	//용량이 큰 파일을 썸네일 처리를 하지 않으면
	//모바일과 같은 환경에서 많은 데이터를 소비해야 하므로
	//이미지의 경우 특별한 경우가 아니면 썸네일을 제작해야 함
	//썸네일은 이미지만 가능함
	public static boolean checkImageType(File file) {
		/*
		 .jpeg / .jpg(JPEG 이미지)의 MIME 타입 : image/jpeg
		 */
		//MIME 타입을 통해 이미지 여부 확인
		try {
			//file.toPath() : 파일 객체를 path객체로 변환
			String contentType = Files.probeContentType(file.toPath());
			log.info("contentType : " + contentType);
			//MIME 타입 정보가 image로 시작하는지 여부를 return
			return contentType.startsWith("image");
		}catch(IOException e) {
			log.error(e.getMessage());
		}
		//이 파일이 이미지가 아닐 경우
		return false;
	}
	
	//파일 다운로드
	
}



























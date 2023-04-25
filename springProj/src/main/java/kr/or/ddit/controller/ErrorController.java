package kr.or.ddit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/error")
@Controller
public class ErrorController {
	///error/error400
	@GetMapping("/error400")
	public String error400() {
		//forwarding
		return "error/400";
	}
	
	@GetMapping("/error404")
	public String error404() {
		//forwarding
		return "error/404";
	}
	
	@GetMapping("/error500")
	public String error500() {
		//forwarding
		return "error/500";
	}
}

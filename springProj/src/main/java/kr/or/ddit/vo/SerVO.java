package kr.or.ddit.vo;

import java.util.Date;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

//서비스
public class SerVO {
	//서비스 번호
	@NotBlank
	private String serNum;
	//수리비
	@NotBlank
	private int serCs;
	//수리 시간
	@Size(max=3)
	private int serTm;
	//수리 완료 예정일
	//미래 날짜인지 검사(23/02/03(x), 2023-02-03(o)..)
	@Future
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date repCompDt;
	//고객 번호
	@NotBlank
	private String cusNum;
	//직원 번호
	@NotBlank
	private String empNum;
	//자동차 번호
	@NotBlank
	private String carNum;
}

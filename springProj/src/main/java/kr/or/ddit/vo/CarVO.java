package kr.or.ddit.vo;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

//자동차
@Data
public class CarVO {
	//자동차 번호
	@NotBlank
	private String carNum;
	//제조 번호
	@NotBlank
	private String mnfNum;
	//연식
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date dt;
	//주행 거리
	private int dist;
	//고객 번호
	@NotBlank
	private String cusNum;
	//자동차(CAR) : 서비스(SER) = 1 : N
	private List<SerVO> serVOList;
}

package kr.or.ddit.vo;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

//직원
@Data
public class EmpVO {
	//직원 번호
	@NotBlank
	private String empNum;
	//주소
	private String empAddr;
	//연락처
	@NotBlank
	private String empPne;
	//직원 명
	@NotBlank
	private String empNm;
	//급여
	@NotNull
	private int empPay;
	//매니저번호
	private String empMjNum;
	//매니저명(EMP_MJ_NM)
	private String empMjNm;
	//행번호
	private int rnum;
	
	//직원(EMP) : 서비스(SER) = 1 : N
	private List<SerVO> serVOList;
}









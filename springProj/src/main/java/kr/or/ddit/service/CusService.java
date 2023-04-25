package kr.or.ddit.service;

import kr.or.ddit.vo.CusVO;

public interface CusService {
	//메소드 시그니처
	public String getCusNum();

	//고객(CUS) 등록
	public int createPost(CusVO cusVO);

	//고객 상세보기
	public CusVO detail(CusVO cusVO);
	
}

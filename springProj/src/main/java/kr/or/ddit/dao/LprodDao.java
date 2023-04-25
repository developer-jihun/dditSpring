package kr.or.ddit.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.or.ddit.vo.AttachVO;
import kr.or.ddit.vo.LprodVO;
import lombok.extern.slf4j.Slf4j;

/*
 매퍼XML(book_SQL.xml)을 실행시키는 DAO(Data Access Object) 클래스
 Repository 어노테이션 : 데이터에 접근하는 클래스
 => 스프링이 데이터를 관리하는 클래스라고 인지하여 자바빈 객체로 등록하여 관리함 
 */
@Slf4j
@Repository
public class LprodDao {
	//이미 만들어져있는 sqlSessionTemplate 객체를 
	//LprodDao에 주입
	//DI(Dependency Injection) : 의존성 주입
	//개발자가 new를 하지 않고 스프링에게 요청하여 객체를 사용
	//IoC(Inversion of Control) : 제어의 역전
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	public List<LprodVO> list(Map<String,String> map){
		return this.sqlSessionTemplate.selectList("lprod.list",map);
	}
	
	//상품분류 자동생성
	//<select id="getLprodId" resultType="int">
	public int getLprodId() {
		//selectOne("namespace.id")
		return this.sqlSessionTemplate.selectOne("lprod.getLprodId");
	}
	
	//상품분류코드 자동생성
	//<select id="getLprodGu" resultType="String">
	public String getLprodGu() {
		//selectOne("namespace.id")
		return this.sqlSessionTemplate.selectOne("lprod.getLprodGu");
	}
	
	//상품분류 정보를 입력
	//<insert id="createPost" parameterType="lprodVO">
	public int createPost(LprodVO lprodVO) throws SQLException {
		return this.sqlSessionTemplate.insert("lprod.createPost",lprodVO);		
	}
	
	//상품분류 상세보기 
	//<select id="detail" parameterType="lprodVO" resultType="lprodVO">
	public LprodVO detail(LprodVO lprodVO) {
		//매퍼xml을 호출. sql실행
		//.selectOne("namespace.id",파라미터)
		return this.sqlSessionTemplate.selectOne("lprod.detail", lprodVO);
	}
	
	//상품분류 정보변경 
//	<update id="updatePost" parameterType="lprodVO">
	public int updatePost(LprodVO lprodVO) {
		//.update("namespace.id",파라미터)
		return this.sqlSessionTemplate.update("lprod.updatePost", lprodVO);
	}
	
	//상품정보 삭제 
//	lprodVO{"lprodId":"10","lprodGu":"P404","lprodNm":"간식류변경"}
//	<delete id="deletePost" parameterType="lprodVO">
	public int deletePost(LprodVO lprodVO) {
		//.delete("namespace.id",파라미터)
		return this.sqlSessionTemplate.delete("lprod.deletePost", lprodVO);
	}
	
	//첨부파일 등록
	//<insert id="uploadFormAction" parameterType="attachVO">
	public int uploadFormAction(AttachVO attachVO) {
		return this.sqlSessionTemplate.insert("lprod.uploadFormAction", attachVO);
	}
	
	//다중 insert 시 
	//<update id="createPostAttach" parameterType="java.util.List">
	public int createPostAttach(List<AttachVO> attachVOList) {
		return this.sqlSessionTemplate.update("lprod.createPostAttach", attachVOList);
	}
	
	//전체 행의 수(total).
	//<select id="getTotal" resultType="int">
	public int getTotal(String keyword) {
		return this.sqlSessionTemplate.selectOne("lprod.getTotal",keyword);
	}
	
	//상품별 판매금액의 합계가 천만원이 넘은 데이터
//	 <select id="cartMoney" resultType="hashMap">
	public List<Map<String,Object>> cartMoney(){
		return this.sqlSessionTemplate.selectList("lprod.cartMoney");
	}
	
	//회원별 구매회수 구하기
//	 <select id="memberMoney" resultType="hashMap">
	 public List<Map<String,Object>> memberMoney(){
		 return this.sqlSessionTemplate.selectList("lprod.memberMoney");
	 }
}











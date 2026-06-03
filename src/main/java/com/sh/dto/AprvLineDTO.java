package com.sh.dto;

public class AprvLineDTO
{
	private String docCd;         // 문서코드
    private int flow;             // 순서
    private String empCd;         // 결재자 사원 코드
    private String empName;       // 결재자 이름
    private String comments;      // 코멘트
    private String regDt;         // 결재일
    private String typeCd;        // 결재 종류 코드
    private String typeName;      // 결재 종류명
    
    // getter / setter
	public String getDocCd()
	{
		return docCd;
	}
	public void setDocCd(String docCd)
	{
		this.docCd = docCd;
	}
	public int getFlow()
	{
		return flow;
	}
	public void setFlow(int flow)
	{
		this.flow = flow;
	}
	public String getEmpCd()
	{
		return empCd;
	}
	public void setEmpCd(String empCd)
	{
		this.empCd = empCd;
	}
	public String getEmpName()
	{
		return empName;
	}
	public void setEmpName(String empName)
	{
		this.empName = empName;
	}
	public String getComments()
	{
		return comments;
	}
	public void setComments(String comments)
	{
		this.comments = comments;
	}
	public String getRegDt()
	{
		return regDt;
	}
	public void setRegDt(String regDt)
	{
		this.regDt = regDt;
	}
	public String getTypeCd()
	{
		return typeCd;
	}
	public void setTypeCd(String typeCd)
	{
		this.typeCd = typeCd;
	}
	public String getTypeName()
	{
		return typeName;
	}
	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}
}

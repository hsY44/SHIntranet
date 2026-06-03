package com.sh.dto;

public class AprvDetailDTO
{
	private String docCd;        // 문서코드
    private String title;        // 제목
    private String regDt;        // 기안일
    private String empCd;        // 기안자 사원 코드
    private String empName;		 // 기안자 이름
    private String workName;     // 업무명
    private String typeCd;     // 문서종류
    private String typeName;     // 문서종류
    private String parentDocCd;  // 참조문서
    private String parentDocTitle;  // 참조문서
    private String contents;     // 내용
    private int fileCnt;         // 파일 개수
    
    // getter / setter
	public String getDocCd()
	{
		return docCd;
	}
	public void setDocCd(String docCd)
	{
		this.docCd = docCd;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getRegDt()
	{
		return regDt;
	}
	public void setRegDt(String regDt)
	{
		this.regDt = regDt;
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
	public String getWorkName()
	{
		return workName;
	}
	public void setWorkName(String workName)
	{
		this.workName = workName;
	}
	public String getTypeName()
	{
		return typeName;
	}
	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}
	public String getTypeCd()
	{
		return typeCd;
	}
	public void setTypeCd(String typeCd)
	{
		this.typeCd = typeCd;
	}
	public String getParentDocCd()
	{
		return parentDocCd;
	}
	public void setParentDocCd(String parentDocCd)
	{
		this.parentDocCd = parentDocCd;
	}
	public String getParentDocTitle()
	{
		return parentDocTitle;
	}
	public void setParentDocTitle(String parentDocTitle)
	{
		this.parentDocTitle = parentDocTitle;
	}
	public String getContents()
	{
		return contents;
	}
	public void setContents(String contents)
	{
		this.contents = contents;
	}
	public int getFileCnt()
	{
		return fileCnt;
	}
	public void setFileCnt(int fileCnt)
	{
		this.fileCnt = fileCnt;
	}
}

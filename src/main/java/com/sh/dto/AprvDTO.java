package com.sh.dto;

public class AprvDTO
{
	private String docCd, parentCd, docTypeCd, workCd, empCd, title, content, regDt;
	
	private String[] workTargetEmpCd;
	
	
	public String[] getWorkTargetEmpCd()
	{
		return workTargetEmpCd;
	}

	public void setWorkTargetEmpCd(String[] workTargetEmpCd)
	{
		this.workTargetEmpCd = workTargetEmpCd;
	}

	public String getDocCd()
	{
		return docCd;
	}

	public void setDocCd(String docCd)
	{
		this.docCd = docCd;
	}

	public String getParentCd()
	{
		return parentCd;
	}

	public void setParentCd(String parentCd)
	{
		this.parentCd = parentCd;
	}

	public String getDocTypeCd()
	{
		return docTypeCd;
	}

	public void setDocTypeCd(String docTypeCd)
	{
		this.docTypeCd = docTypeCd;
	}

	public String getWorkCd()
	{
		return workCd;
	}

	public void setWorkCd(String workCd)
	{
		this.workCd = workCd;
	}

	public String getEmpCd()
	{
		return empCd;
	}

	public void setEmpCd(String empCd)
	{
		this.empCd = empCd;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getRegDt()
	{
		return regDt;
	}

	public void setRegDt(String regDt)
	{
		this.regDt = regDt;
	}
}

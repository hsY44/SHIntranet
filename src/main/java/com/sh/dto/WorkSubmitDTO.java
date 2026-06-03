package com.sh.dto;

public class WorkSubmitDTO
{
	private String docCd, typeCd, workCd, empCd, title, contents, regDt, parentCd;
	
	public String getParentCd()
	{
		return parentCd;
	}

	public void setParentCd(String parentCd)
	{
		this.parentCd = parentCd;
	}

	public String getTypeCd()
	{
		return typeCd;
	}

	public void setTypeCd(String typeCd)
	{
		this.typeCd = typeCd;
	}

	public String getContents()
	{
		return contents;
	}

	public void setContents(String contents)
	{
		this.contents = contents;
	}

	public String getDocCd()
	{
		return docCd;
	}

	public void setDocCd(String docCd)
	{
		this.docCd = docCd;
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

	public String getRegDt()
	{
		return regDt;
	}

	public void setRegDt(String regDt)
	{
		this.regDt = regDt;
	}
}

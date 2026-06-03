package com.sh.dto;

public class AprvReceiveDTO
{
	private int num;
	private String docCd;
	private String parentDocCd;
	private String workName;
	private String title;
	private String empCd;
	private String empName;
	private String aprvRegDt; // 결재 도착일
	private String typeName;
	private String aprvEmpCd; // 결재자(본인empCd)
	private String aprvEmpName;
	private String aprvTypeCd;
	private String aprvTypeName;
	private String regDt; // 내 결재일
	private String docTypeName;
	private String status; // 현재 상태

	// getter / setter
	public int getNum()
	{
		return num;
	}
	public void setNum(int num)
	{
		this.num = num;
	}
	public String getDocCd()
	{
		return docCd;
	}
	public void setDocCd(String docCd)
	{
		this.docCd = docCd;
	}
	public String getParentDocCd()
	{
		return parentDocCd;
	}
	public void setParentDocCd(String parentDocCd)
	{
		this.parentDocCd = parentDocCd;
	}
	public String getWorkName()
	{
		return workName;
	}
	public void setWorkName(String workName)
	{
		this.workName = workName;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
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
	public String getAprvRegDt()
	{
		return aprvRegDt;
	}
	public void setAprvRegDt(String aprvRegDt)
	{
		this.aprvRegDt = aprvRegDt;
	}
	public String getTypeName()
	{
		return typeName;
	}
	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}
	public String getAprvEmpCd()
	{
		return aprvEmpCd;
	}
	public void setAprvEmpCd(String aprvEmpCd)
	{
		this.aprvEmpCd = aprvEmpCd;
	}
	public String getAprvEmpName()
	{
		return aprvEmpName;
	}
	public void setAprvEmpName(String aprvEmpName)
	{
		this.aprvEmpName = aprvEmpName;
	}
	public String getAprvTypeCd()
	{
		return aprvTypeCd;
	}
	public void setAprvTypeCd(String aprvTypeCd)
	{
		this.aprvTypeCd = aprvTypeCd;
	}
	public String getAprvTypeName()
	{
		return aprvTypeName;
	}
	public void setAprvTypeName(String aprvTypeName)
	{
		this.aprvTypeName = aprvTypeName;
	}
	public String getRegDt()
	{
		return regDt;
	}
	public void setRegDt(String regDt)
	{
		this.regDt = regDt;
	}
	public String getDocTypeName()
	{
		return docTypeName;
	}
	public void setDocTypeName(String docTypeName)
	{
		this.docTypeName = docTypeName;
	}
	public String getStatus()
	{
		return status;
	}
	public void setStatus(String status)
	{
		this.status = status;
	}
}

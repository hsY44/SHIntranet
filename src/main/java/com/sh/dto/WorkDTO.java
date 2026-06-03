package com.sh.dto;

import java.io.Serializable;

/**
 * VIEW_WORJ_SEARCH DTO
 */
public class WorkDTO implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int rowNum;
	private String typeName;
	private String workName;
	private String regDt;
	private String empCd;
	private String deptName;
	private String empName;
	private String positionName;
	private String workCd;
	private String typeCd;
	
	public String getTypeCd()
	{
		return typeCd;
	}
	public void setTypeCd(String typeCd)
	{
		this.typeCd = typeCd;
	}
	public String getWorkCd()
	{
		return workCd;
	}
	public void setWorkCd(String workCd)
	{
		this.workCd = workCd;
	}
	public int getRowNum()
	{
		return rowNum;
	}
	public void setRowNum(int rowNum)
	{
		this.rowNum = rowNum;
	}
	public String getTypeName()
	{
		return typeName;
	}
	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}
	public String getWorkName()
	{
		return workName;
	}
	public void setWorkName(String workName)
	{
		this.workName = workName;
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
	public String getDeptName()
	{
		return deptName;
	}
	public void setDeptName(String deptName)
	{
		this.deptName = deptName;
	}
	public String getEmpName()
	{
		return empName;
	}
	public void setEmpName(String empName)
	{
		this.empName = empName;
	}
	public String getPositionName()
	{
		return positionName;
	}
	public void setPositionName(String positionName)
	{
		this.positionName = positionName;
	}
}

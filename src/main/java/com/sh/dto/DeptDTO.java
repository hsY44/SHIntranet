package com.sh.dto;

public class DeptDTO
{
	private String deptCd;
	private String deptName;
	private int empCount;

	public String getDeptCd()
	{
		return deptCd;
	}

	public String getDeptName()
	{
		return deptName;
	}

	public int getEmpCount()
	{
		return empCount;
	}

	public void setDeptCd(String deptCd)
	{
		this.deptCd = deptCd;
	}

	public void setDeptName(String deptName)
	{
		this.deptName = deptName;
	}

	public void setEmpCount(int empCount)
	{
		this.empCount = empCount;
	}
}

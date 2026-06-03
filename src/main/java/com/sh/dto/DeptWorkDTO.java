package com.sh.dto;

public class DeptWorkDTO
{
	private String deptName;
	private String workType;
	private String workName;
	private int flow;
	
	public String getDeptName()
	{
		return deptName;
	}
	public void setDeptName(String deptName)
	{
		this.deptName = deptName;
	}
	public String getWorkType()
	{
		return workType;
	}
	public void setWorkType(String workType)
	{
		this.workType = workType;
	}
	public String getWorkName()
	{
		return workName;
	}
	public void setWorkName(String workName)
	{
		this.workName = workName;
	}
	public int getFlow()
	{
		return flow;
	}
	public void setFlow(int flow)
	{
		this.flow = flow;
	}
}

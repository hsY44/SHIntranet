package com.sh.dto;

import java.util.Date;

public class ManagerDTO
{
	private int rowNum;
    private int managerNo;
    private String typeName;
    private String workName;
    private String deptCd;
    private String deptName;
    private String managerEmpCd;
    private String managerEmpName;
    private Date startIssueDate;
    private Date endIssueDate;
    private String regEmpCd;
    private String regEmpName;
    private String issueType;
    private String issueDate;
    private String workCd;

    // Getter and Setter
	public int getRowNum()
	{
		return rowNum;
	}
	public void setRowNum(int rowNum)
	{
		this.rowNum = rowNum;
	}
	public int getManagerNo()
	{
		return managerNo;
	}
	public void setManagerNo(int managerNo)
	{
		this.managerNo = managerNo;
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
	public String getDeptCd()
	{
		return deptCd;
	}
	public void setDeptCd(String deptCd)
	{
		this.deptCd = deptCd;
	}
	public String getDeptName()
	{
		return deptName;
	}
	public void setDeptName(String deptName)
	{
		this.deptName = deptName;
	}
	public String getManagerEmpCd()
	{
		return managerEmpCd;
	}
	public void setManagerEmpCd(String managerEmpCd)
	{
		this.managerEmpCd = managerEmpCd;
	}
	public String getManagerEmpName()
	{
		return managerEmpName;
	}
	public void setManagerEmpName(String managerEmpName)
	{
		this.managerEmpName = managerEmpName;
	}
	public Date getStartIssueDate()
	{
		return startIssueDate;
	}
	public void setStartIssueDate(Date startIssueDate)
	{
		this.startIssueDate = startIssueDate;
	}
	public Date getEndIssueDate()
	{
		return endIssueDate;
	}
	public void setEndIssueDate(Date endIssueDate)
	{
		this.endIssueDate = endIssueDate;
	}
	public String getRegEmpCd()
	{
		return regEmpCd;
	}
	public void setRegEmpCd(String regEmpCd)
	{
		this.regEmpCd = regEmpCd;
	}
	public String getRegEmpName()
	{
		return regEmpName;
	}
	public void setRegEmpName(String regEmpName)
	{
		this.regEmpName = regEmpName;
	}
    public String getIssueType() {
        return issueType;
    }
    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }
    public String getIssueDate()
    {
        return issueDate;
    }
    public void setIssueDate(String issueDate)
    {
        this.issueDate = issueDate;
    }
    public String getWorkCd()
    {
        return workCd;
    }
    public void setWorkCd(String workCd)
    {
        this.workCd = workCd;
    }
}

package com.sh.dto;

public class AttendDTO
{
	private long logNo; // ATTEND_LOG.LOG_NO
	private String typeCd; // ATTEND_LOG.TYPE_CD
	private String typeName; // ATTEND_TYPE.TYPE_NAME 
	private String empCd; // ATTEND_LOG.EMP_CD
	private String empName; // EMP.EMP_NAME 
	private String deptCd; // EMP.DEPT_CD 
	private String deptName; // DEPT.DEPT_NAME 
	private String positionCd; // EMP.POSITION_CD 
	private String positionName; // EMP_POSITION.POSITION_NAME 
	private String regDt; // ATTEND_LOG.REG_DT

	public long getLogNo()
	{
		return logNo;
	}

	public String getTypeCd()
	{
		return typeCd;
	}

	public String getTypeName()
	{
		return typeName;
	}

	public String getEmpCd()
	{
		return empCd;
	}

	public String getEmpName()
	{
		return empName;
	}

	public String getDeptCd()
	{
		return deptCd;
	}

	public String getDeptName()
	{
		return deptName;
	}

	public String getPositionCd()
	{
		return positionCd;
	}

	public String getPositionName()
	{
		return positionName;
	}

	public String getRegDt()
	{
		return regDt;
	}

	public void setLogNo(long logNo)
	{
		this.logNo = logNo;
	}

	public void setTypeCd(String typeCd)
	{
		this.typeCd = typeCd;
	}

	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}

	public void setEmpCd(String empCd)
	{
		this.empCd = empCd;
	}

	public void setEmpName(String empName)
	{
		this.empName = empName;
	}

	public void setDeptCd(String deptCd)
	{
		this.deptCd = deptCd;
	}

	public void setDeptName(String deptName)
	{
		this.deptName = deptName;
	}

	public void setPositionCd(String positionCd)
	{
		this.positionCd = positionCd;
	}

	public void setPositionName(String positionName)
	{
		this.positionName = positionName;
	}

	public void setRegDt(String regDt)
	{
		this.regDt = regDt;
	}
}

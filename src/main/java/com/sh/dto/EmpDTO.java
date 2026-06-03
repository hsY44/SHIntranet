package com.sh.dto;

public class EmpDTO
{
	private String empCd; // EMP.EMP_CD 
	private String empName; // EMP.EMP_NAME
	private String deptCd; // EMP.DEPT_CD 
	private String deptName; // DEPT.DEPT_NAME 
	private String positionCd; // EMP.POSITION_CD 
	private String positionName; // EMP_POSITION.POSITION_NAME 
	private int grade; // EMP_POSITION.GRADE 
	private String tel; // EMP_INFO.TEL
	private String email; // EMP_INFO.EMAIL
	private String addr; // EMP_INFO.ADDR
	private String pwd; // EMP_INFO.PWD
	private String hireDate; // EMP_INFO.HIRE_DATE 
	private String regDt; // EMP.REG_DT 

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

	public int getGrade()
	{
		return grade;
	}

	public String getTel()
	{
		return tel;
	}

	public String getEmail()
	{
		return email;
	}

	public String getAddr()
	{
		return addr;
	}

	public String getPwd()
	{
		return pwd;
	}

	public String getHireDate()
	{
		return hireDate;
	}

	public String getRegDt()
	{
		return regDt;
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

	public void setGrade(int grade)
	{
		this.grade = grade;
	}

	public void setTel(String tel)
	{
		this.tel = tel;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public void setAddr(String addr)
	{
		this.addr = addr;
	}

	public void setPwd(String pwd)
	{
		this.pwd = pwd;
	}

	public void setHireDate(String hireDate)
	{
		this.hireDate = hireDate;
	}

	public void setRegDt(String regDt)
	{
		this.regDt = regDt;
	}
	
	public String getTelFormatted() {
	    if (tel == null || tel.isEmpty()) return "";
	    // 011, 016, 017, 018, 019 -> 3-3-4
	    if (tel.matches("^01[1-9]\\d{7}$"))
	        return tel.substring(0, 3) + "-" + tel.substring(3, 6) + "-" + tel.substring(6);
	    // 010 -> 3-4-4
	    if (tel.matches("^010\\d{8}$"))
	        return tel.substring(0, 3) + "-" + tel.substring(3, 7) + "-" + tel.substring(7);
	    return tel; // 포맷 불일치 시 원본 반환
	}
}

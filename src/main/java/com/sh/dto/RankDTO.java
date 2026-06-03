package com.sh.dto;

public class RankDTO
{
	private String positionCd;
	private String positionName;
	private int grade;
	private int empCount;

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

	public int getEmpCount()
	{
		return empCount;
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

	public void setEmpCount(int empCount)
	{
		this.empCount = empCount;
	}
}

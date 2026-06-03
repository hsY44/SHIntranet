package com.sh.dto;

public class BoardDropDTO
{
	private int dropNum, boardNum;
	private String empCd, dropDt;
	
	public int getDropNum()
	{
		return dropNum;
	}
	public void setDropNum(int dropNum)
	{
		this.dropNum = dropNum;
	}
	public int getBoardNum()
	{
		return boardNum;
	}
	public void setBoardNum(int boardNum)
	{
		this.boardNum = boardNum;
	}
	public String getEmpCd()
	{
		return empCd;
	}
	public void setEmpCd(String empCd)
	{
		this.empCd = empCd;
	}
	public String getDropDt()
	{
		return dropDt;
	}
	public void setDropDt(String dropDt)
	{
		this.dropDt = dropDt;
	}
}

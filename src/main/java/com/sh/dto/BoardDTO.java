package com.sh.dto;

// 게시글
public class BoardDTO
{
	private int num, dropNum;
	private String title, contents, regDate, name, type, empCd, typeName;
	
	public String getTypeName()
	{
		return typeName;
	}
	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}
	public int getDropNum()
	{
		return dropNum;
	}
	public void setDropNum(int dropNum)
	{
		this.dropNum = dropNum;
	}
	public String getEmpCd()
	{
		return empCd;
	}
	public void setEmpCd(String empCd)
	{
		this.empCd = empCd;
	}
	public int getNum()
	{
		return num;
	}
	public void setNum(int num)
	{
		this.num = num;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getContents()
	{
		return contents;
	}
	public void setContents(String content)
	{
		this.contents = content;
	}
	public String getRegDate()
	{
		return regDate;
	}
	public void setRegDate(String regDate)
	{
		this.regDate = regDate;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}

}
	
	
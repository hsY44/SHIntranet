package com.sh.dto;

public class BoardFileDTO 
{
	// 파일번호, 게시글번호
	private int fileNo, boardNum;
	// 원본 파일명, 저장된 파일명, 파일 경로
    private String originName, savedName, path;
    
	public int getFileNo()
	{
		return fileNo;
	}
	public void setFileNo(int fileNo)
	{
		this.fileNo = fileNo;
	}
	public int getBoardNum()
	{
		return boardNum;
	}
	public void setBoardNum(int boardNum)
	{
		this.boardNum = boardNum;
	}
	public String getOriginName()
	{
		return originName;
	}
	public void setOriginName(String originName)
	{
		this.originName = originName;
	}
	public String getSavedName()
	{
		return savedName;
	}
	public void setSavedName(String savedName)
	{
		this.savedName = savedName;
	}
	public String getPath()
	{
		return path;
	}
	public void setPath(String path)
	{
		this.path = path;
	} 
    
    
}

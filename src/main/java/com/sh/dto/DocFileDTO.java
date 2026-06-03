package com.sh.dto;

public class DocFileDTO
{
	private int docFileNo;  // 문서 파일 번호
    private String originName; // 원본 파일명
    private String savedName;  // 저장된 파일명
    private String path;       // 파일 경로
    private String docCd;      // 문서 코드
    private int fileNo;
    
    public int getFileNo()
	{
		return fileNo;
	}
	public void setFileNo(int fileNo)
	{
		this.fileNo = fileNo;
	}
	// getter / setter
	public int getDocFileNo()
	{
		return docFileNo;
	}
	public void setDocFileNo(int docFileNo)
	{
		this.docFileNo = docFileNo;
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
	public String getDocCd()
	{
		return docCd;
	}
	public void setDocCd(String docCd)
	{
		this.docCd = docCd;
	}
}

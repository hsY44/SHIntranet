package com.sh.dto;

public class AprvSubmitDTO
{
    private int rowNum;
    private String docCd;
    private String workName;
    private String title;
    private String regDt;
    private String typeCd;
    private String typeName;
    private String status;
    private String empCd;
    private int childCnt;

	// Getter & Setter 메서드
    public int getRowNum()
    {
        return rowNum;
    }
    public void setRowNum(int rowNum)
    {
        this.rowNum = rowNum;
    }
    public String getDocCd() {
        return docCd;
    }
    public void setDocCd(String docCd) {
        this.docCd = docCd;
    }
    public String getWorkName() {
        return workName;
    }
    public void setWorkName(String workName) {
        this.workName = workName;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getRegDt() {
        return regDt;
    }
    public void setRegDt(String regDt) {
        this.regDt = regDt;
    }
    public String getTypeCd() {
        return typeCd;
    }
    public void setTypeCd(String typeCd) {
        this.typeCd = typeCd;
    }
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getEmpCd() {
        return empCd;
    }
    public void setEmpCd(String empCd) {
        this.empCd = empCd;
    }
    public int getChildCnt()
	{
		return childCnt;
	}
	public void setChildCnt(int childCnt)
	{
		this.childCnt = childCnt;
	}
}

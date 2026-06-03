package com.sh.dto;

public class CommentDropDTO 
{
	private int num, commentNum;
	private String empCd, dropDt;
	
	public String getEmpCd() {
		return empCd;
	}
	public void setEmpCd(String empCd) {
		this.empCd = empCd;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getCommentNum() {
		return commentNum;
	}
	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}
	
	public String getDropDt() {
		return dropDt;
	}
	public void setDropDt(String dropDt) {
		this.dropDt = dropDt;
	}
}

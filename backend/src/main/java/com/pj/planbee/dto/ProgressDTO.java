package com.pj.planbee.dto;

public class ProgressDTO {
	double tdProgress;
	int calId;
	   String calDetail1, calDetail2, calDetail3, calDate, userId; //userId는 나중에 찬교님이 사용한 userID와 맞춰주면 됨
	   
	   public int getCalId() {
	      return calId;
	   }
	   public void setCalId(int calId) {
	      this.calId = calId;
	   }
	   public String getCalDetail1() {
		return calDetail1; 
	   }
	   public void setCalDetail1(String calDetail1) {
		this.calDetail1 = calDetail1;
	   }
	   public String getCalDetail2() {
		return calDetail2;
	   }
	   public void setCalDetail2(String calDetail2) {
		this.calDetail2 = calDetail2;
	   }
	   public String getCalDetail3() {
		return calDetail3;
	   }
	   public void setCalDetail3(String calDetail3) {
		this.calDetail3 = calDetail3;
	   }
	   public String getCalDate() {
	      return calDate;
	   }
	   public void setCalDate(String calDate) {
	      this.calDate = calDate;
	   }
	   public String getUserId() {
	      return userId;
	   }
	   public void setUserId(String userId) {
	      this.userId = userId;
	   }
	   
	public double getTdProgress() {
		return tdProgress;
	}

	public void setTdProgress(double tdProgress) {
		this.tdProgress = tdProgress;
	}
}

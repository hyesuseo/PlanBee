package com.pj.planbee.dto;

public class ProgressShareDTO {

	String userId;
	String date;
	int completed;
	int total;

	public ProgressShareDTO() {
	}

	public ProgressShareDTO(String userId, String date, int completed, int total) {
		this.userId = userId;
		this.date = date;
		this.completed = completed;
		this.total = total;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getCompleted() {
		return completed;
	}

	public void setCompleted(int completed) {
		this.completed = completed;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
}

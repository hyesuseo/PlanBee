package com.pj.planbee.dto;

import java.util.List;

public class TodoDashboardDTO {

	private List<TDdetailDTO> todoList;
	private String memo;
	private double progress;
	private int todoId;
	
	
	public List<TDdetailDTO> getTodoList() {
		return todoList;
	}
	public void setTodoList(List<TDdetailDTO> list) {
		this.todoList = list;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public double getProgress() {
		return progress;
	}
	public void setProgress(double progress) {
		this.progress = progress;
	}
	public void setTodoId(int todoId) {
		this.todoId = todoId;
	}
	public int getTodoId() {
		return todoId;
	}
	
}

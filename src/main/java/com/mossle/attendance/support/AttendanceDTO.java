
package com.mossle.attendance.support;

import java.util.Date;

public class AttendanceDTO {
	private String userId;
	private Date upTime;
	private String upStatus;
	private Date downTime;
	private String downStatus;

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getUpTime() {
		return upTime;
	}
	public void setUpTime(Date upTime) {
		this.upTime = upTime;
	}

	public String getUpStatus() {
		return upStatus;
	}
	public void setUpStatus(String upStatus) {
		this.upStatus = upStatus;
	}

	public Date getDownTime() {
		return downTime;
	}
	public void setDownTime(Date downTime) {
		this.downTime = downTime;
	}

	public String getDownStatus() {
		return downStatus;
	}
	public void setDownStatus(String downStatus) {
		this.downStatus = downStatus;
	}
}

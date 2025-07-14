package com.stocktrader.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Employee extends Person {

	/*
	 * This class is a representation of the employee table in the database Each
	 * instance variable has a corresponding getter and setter
	 */

	public long getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(long l) {
		this.employeeID = l;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date date) {
		this.startDate = date;
	}

	public BigDecimal getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(BigDecimal bigDecimal) {
		this.hourlyRate = bigDecimal;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}







	private long employeeID;
	private Date startDate;
	private BigDecimal hourlyRate;
	private String level;

}

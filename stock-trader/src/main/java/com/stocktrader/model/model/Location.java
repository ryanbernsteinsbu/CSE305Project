package com.stocktrader.model;

public class Location {

	private String zipCode;
	private String city;
	private String state;

	public void setZipCode(int zip) {
		this.zipCode = String.valueOf(zip);
	}

	public void setZipCode(String zip) {
		this.zipCode = zip;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public int getZipCodeInt() {
		return Integer.parseInt(zipCode);
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}
}

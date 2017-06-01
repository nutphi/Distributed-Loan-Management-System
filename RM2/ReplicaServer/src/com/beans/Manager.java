package com.beans;

import java.io.Serializable;

/**
 * This is a manager class.
 * @author Anunay
 *
 */
public class Manager extends User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}	
}

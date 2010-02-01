package com.googlecode.gmail2ldap.model;

public class User {

	private String firstName;

	private String lastName;

	private String nickName;

	private String email;

	public String getFullName() {
		return firstName + " " + lastName;
	}

	public String getUid() {
		if (email == null) {
			throw new IllegalStateException("email can't be null");
		}
		return email.substring(email.indexOf("@"));
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

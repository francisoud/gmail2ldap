package com.googlecode.gmail2ldap.config;

public class Account {

	private String username;

	private String email;

	private String password;

	private boolean isHosted = false;

	public boolean isHosted() {
		if (email == null) {
			throw new IllegalStateException("Set email first to know if it's an hosted account");
		}
		if (email.indexOf("@gmail.com") == -1) {
			isHosted = true;
		}
		return isHosted;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

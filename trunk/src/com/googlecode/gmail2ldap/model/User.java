package com.googlecode.gmail2ldap.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {

	private static final char POINT = '.';

	private transient static final Logger logger = LoggerFactory.getLogger(User.class);

	private String firstName;

	private String lastName;

	private String nickName;

	private String fullName;

	private String email;

	public String getFullName() {
		if (fullName == null) {
			final String tmpFirstName = getFirstName();
			final String tmpLastName = getLastName();
			if (tmpFirstName.equalsIgnoreCase(tmpLastName)) {
				fullName = getUid();
			} else {
				fullName = tmpFirstName + " " + tmpLastName;
			}
		}
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUid() {
		if (email == null) {
			throw new IllegalStateException("email can't be null");
		}
		return email.substring(0, email.indexOf("@"));
	}

	public String getFirstName() {
		if (firstName == null) {
			final int index = getUid().indexOf(POINT);
			if (index > 0) {
				firstName = getUid().substring(0, index);
			} else {
				logger.debug("no first name");
				return getUid();
			}
		}
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		if (lastName == null) {
			final int index = getUid().indexOf(POINT);
			if (index > 0) {
				lastName = getUid().substring(index + 1, getUid().length());
			} else {
				logger.debug("no last name");
				return getUid();
			}
		}
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNickName() {
		if (nickName == null) {
			nickName = getUid();
		}
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

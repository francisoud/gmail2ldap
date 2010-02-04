package com.googlecode.gmail2ldap.model;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {

	private static final char POINT = '.';

	private transient static final Logger logger = LoggerFactory.getLogger(User.class);

	private UUID uid;

	private String firstName;

	private String lastName;

	private String nickName;

	private String fullName;

	private String email;

	public User() {
		uid = UUID.randomUUID();
	}

	public UUID getUid() {
		return uid;
	}

	public void setUid(UUID uid) {
		this.uid = uid;
	}

	public String getFullName() {
		if (fullName == null) {
			final String tmpFirstName = getFirstName();
			final String tmpLastName = getLastName();
			if (tmpFirstName.equalsIgnoreCase(tmpLastName)) {
				fullName = getDeprecatedUid();
			} else {
				fullName = tmpFirstName + " " + tmpLastName;
			}
		}
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @deprecated wrong uid
	 */
	public String getDeprecatedUid() {
		if (email == null) {
			throw new IllegalStateException("email can't be null");
		}
		return email.substring(0, email.indexOf("@"));
	}

	public String getFirstName() {
		if (firstName == null) {
			final int index = getDeprecatedUid().indexOf(POINT);
			if (index > 0) {
				firstName = getDeprecatedUid().substring(0, index);
			} else {
				logger.debug("no first name");
				return getDeprecatedUid();
			}
		}
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		if (lastName == null) {
			final int index = getDeprecatedUid().indexOf(POINT);
			if (index > 0) {
				lastName = getDeprecatedUid().substring(index + 1, getDeprecatedUid().length());
			} else {
				logger.debug("no last name");
				return getDeprecatedUid();
			}
		}
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNickName() {
		if (nickName == null) {
			nickName = getDeprecatedUid();
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

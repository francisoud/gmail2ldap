package com.googlecode.gmail2ldap.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transaction {

	private final SchemaAdministrator administrator;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public Transaction(final Loader loader) {
		this.administrator = loader.getAdministrator();
	}

	public void begin() {
		logger.info("Begin transaction");
		administrator.createTmpNode();
	}

	public void commit() {
		logger.info("Commit transaction");
		administrator.replaceUsersNode();
	}

	public void rollback() {
		logger.info("Rollback transaction");
		administrator.deleteTmpNode();
	}
}

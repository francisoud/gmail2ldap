package com.googlecode.gmail2ldap.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transaction {

	private final Loader loader;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public Transaction(final Loader loader) {
		this.loader = loader;
	}

	public void begin() {
		logger.info("Begin transaction");
		loader.createTmpNode();
	}

	public void commit() {
		logger.info("Commit transaction");
		loader.replaceUsersNode();
	}

	public void rollback() {
		logger.info("Rollback transaction");
		loader.deleteTmpNode();
	}
}

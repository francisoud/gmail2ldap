package com.googlecode.gmail2ldap.ldap;

import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.ldap.LdapServer;

public class Server {

	private final LdapServer ldapServer;

	public Server() {
		ldapServer = Factory.getLdapServer();
	}

	public void start() {
		try {
			final DirectoryService service = ldapServer.getDirectoryService();
			service.startup();
			final Loader loader = new Loader(service, Constants.PARTITION_ID);
			loader.load();
			ldapServer.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		try {
			ldapServer.stop();
			ldapServer.getDirectoryService().shutdown();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

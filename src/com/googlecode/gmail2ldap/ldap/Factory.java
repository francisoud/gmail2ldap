package com.googlecode.gmail2ldap.ldap;

import java.io.File;

import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Factory {

	private static final Logger logger = LoggerFactory.getLogger(Factory.class);

	public static DirectoryService getDirectoryService() {
		// Initialize the LDAP service
		final DirectoryService service = new DefaultDirectoryService();
		service.setShutdownHookEnabled(true);

		// Disable the ChangeLog system
		service.getChangeLog().setEnabled(false);
		service.setDenormalizeOpAttrsEnabled(true);

		final String tmpDir = System.getProperty("java.io.tmpdir");
		logger.debug(tmpDir);
		final File workingDir = (File) new File(tmpDir);
		service.setWorkingDirectory(workingDir);

		final DirectoryUtil util = new DirectoryUtil(service);

		// TODO make it configurable
		final Partition partition = util.addPartition(Constants.PARTITION_ID, Constants.PARTITION_DN);
		// Index some attributes on the partition
		util.addIndex(partition, "objectClass", "ou", "uid");

		return service;
	}

	public static LdapServer getLdapServer() {
		final LdapServer ldapServer = new LdapServer();
		ldapServer.setDirectoryService(getDirectoryService());
		ldapServer.setTransports(new TcpTransport(389));
		ldapServer.setAllowAnonymousAccess(true);
		return ldapServer;
	}
}

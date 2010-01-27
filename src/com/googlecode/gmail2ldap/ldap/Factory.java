package com.googlecode.gmail2ldap.ldap;

import java.io.File;

import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;

public class Factory {

	public static DirectoryService getDirectoryService() {
		// Initialize the LDAP service
		final DirectoryService service = new DefaultDirectoryService();

		// Disable the ChangeLog system
		service.getChangeLog().setEnabled(false);
		service.setDenormalizeOpAttrsEnabled(true);

		String tmpDir = System.getProperty("java.io.tmpdir");
		System.out.println(tmpDir);
		final File workingDir = (File) new File(tmpDir);
		service.setWorkingDirectory(workingDir);

		final DirectoryUtil util = new DirectoryUtil(service);

		// TODO make it configurable
		final Partition partition = util.addPartition(Constants.PARTITION_ID, Constants.PARTITION_DN);

		// Index some attributes on the partition
		// TODO really necessary ?
		// util.addIndex(partition, "objectClass", "ou", "uid");

		return service;
	}

	public static LdapServer getLdapServer() {
		final LdapServer ldapServer = new LdapServer();
		ldapServer.setDirectoryService(getDirectoryService());
		ldapServer.setTransports(new TcpTransport(389));
		return ldapServer;
	}
}

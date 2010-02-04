package com.googlecode.gmail2ldap.sandbox;

import java.io.File;

import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.directory.server.ldap.LdapServer;



public class CopyOfServer {

	// TODO provide getter/setter ?
    public static DirectoryService directoryService;

    private SocketAcceptor socketAcceptor;
    private LdapServer ldapServer;

	public void start() {
		try {
            directoryService = new DefaultDirectoryService();
            directoryService.setShutdownHookEnabled(true);

//            socketAcceptor = new SocketAcceptor(null);
            ldapServer = new LdapServer();
//            ldapServer.setSocketAcceptor(socketAcceptor);
            ldapServer.setDirectoryService(directoryService);

            // Set LDAP port to 10389
//            ldapServer.setIpPort(10389);

            // Determine an appropriate working directory

            File workingDir = (File) new File(System.getProperty("java.io.tmpdir"));
            directoryService.setWorkingDirectory(workingDir);

            directoryService.startup();
            ldapServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

	}
	
	public void stop() {
        try {
            ldapServer.stop();
            directoryService.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
}

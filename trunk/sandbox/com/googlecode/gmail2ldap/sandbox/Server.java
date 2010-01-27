package com.googlecode.gmail2ldap.sandbox;

import java.io.File;

import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;

public class Server {

    private DirectoryService directoryService;

    private LdapServer ldapServer;

    public Server(DirectoryService directoryService) {
    	this.directoryService = directoryService;
    	
        final File workingDir = (File) new File(System.getProperty("java.io.tmpdir"));
        directoryService.setWorkingDirectory(workingDir);
        
        ldapServer = new LdapServer();
        ldapServer.setDirectoryService(directoryService);
        ldapServer.setTransports(new TcpTransport(389));
	}
    
	public void start() {
		try {
//            directoryService.startup();
			System.out.println(ldapServer.getDirectoryService().toString());
            ldapServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
	
	public void stop() {
        try {
            ldapServer.stop();
//            directoryService.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
}

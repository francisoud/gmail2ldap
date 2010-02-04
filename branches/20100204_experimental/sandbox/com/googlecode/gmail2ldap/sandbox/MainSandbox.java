package com.googlecode.gmail2ldap.sandbox;

import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.name.LdapDN;


public class MainSandbox {

	public static void main(String[] args) throws Exception {
		// Create the server
        EmbeddedADS ads = new EmbeddedADS();
        
        // Read an entry
        Entry result = ads.service.getAdminSession().lookup( new LdapDN( "dc=apache,dc=org" ) );
        
        // And print it if available
        System.out.println( "Found entry : " + result );
        
		Server server = new Server(ads.service);
		System.out.println("starting...");
		server.start();
		System.out.println("started");
		// Thread.sleep(100000);
		// server.stop();
		// System.out.println("stoped");
	}

}

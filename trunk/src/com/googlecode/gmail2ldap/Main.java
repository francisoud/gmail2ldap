package com.googlecode.gmail2ldap;

import com.googlecode.gmail2ldap.ldap.Server;

public class Main {

	public static void main(String[] args) {
		// TODO make this configurable (system property)
		// delete previous ldap store
		// final String tmpDir = System.getProperty("java.io.tmpdir") + "/" +
		// Constants.PARTITION_ID;
		// final File workingDir = (File) new File(tmpDir);
		// workingDir.delete();

		Server server = new Server();
		server.start();

		ProjectSystemTray systemTray = new ProjectSystemTray();

	}
}

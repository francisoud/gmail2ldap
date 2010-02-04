package com.googlecode.gmail2ldap.sandbox;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;

public class EnvHelper {
	public static Hashtable createEnv() {
        Hashtable env = new Properties();

        env.put(Context.PROVIDER_URL, "");
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.directory.server.jndi.ServerContextFactory");

        env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
        env.put(Context.SECURITY_CREDENTIALS, "secret");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        return env;
    }
}

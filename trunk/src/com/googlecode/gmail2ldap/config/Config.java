package com.googlecode.gmail2ldap.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.xml.sax.SAXException;

public class Config {

	private Digester digester;

	private Properties properties = new Properties();

	public static String GMAIL_MAX_CONTACTS = "gmail.max.contacts";

	public Config() {
		final URL rules = getClass().getResource("rules.xml");
		digester = DigesterLoader.createDigester(rules);

		try {
			final FileInputStream input = new FileInputStream("config/config.properties");
			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Account> parse(final InputStream input) {
		final List<Account> beans = new ArrayList<Account>();
		digester.push(beans);
		try {
			return (List<Account>) digester.parse(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	public Properties getProperties() {
		return properties;
	}
}

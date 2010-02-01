package com.googlecode.gmail2ldap.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.xml.sax.SAXException;

public class Config {

	private Digester digester;

	public Config() {
		final URL rules = getClass().getResource("rules.xml");
		digester = DigesterLoader.createDigester(rules);
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

}

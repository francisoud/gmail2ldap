# gmail2ldap
Automatically exported from code.google.com/p/gmail2ldap


# Summary #

![https://raw.githubusercontent.com/francisoud/gmail2ldap/master/doc/images/gdata-contacts-icon.png](https://raw.githubusercontent.com/francisoud/gmail2ldap/master/doc/images/gdata-contacts-icon.png)

**Provide a synchronized copy of gmail contacts to thunderbird using a local ldap server**

# Install #

Just unzip somewhere

_Linux_: `gmail2ldap$ chmod u+x gmail2ldap.sh`

# Configure #

Go to the 'config' dir and rename accounts-sample.xml to accounts.xml.

Edit accounts.xml with your own email/password.

# Run #

_Windows_:

![https://raw.githubusercontent.com/francisoud/gmail2ldap/master/doc/images/windows.png](https://raw.githubusercontent.com/francisoud/gmail2ldap/master/doc/images/windows.png)

_Linux_:

`gmail2ldap$ gmail2ldap.sh`


# Synchronize #

Check your system tray, right click on icon...

| ![https://raw.githubusercontent.com/francisoud/gmail2ldap/master/doc/images/gmail2ldap-tray-windows.png](https://raw.githubusercontent.com/francisoud/gmail2ldap/master/doc/images/gmail2ldap-tray-windows.png) | ![https://raw.githubusercontent.com/francisoud/gmail2ldap/master/doc/images/gmail2ldap-tray.png](https://raw.githubusercontent.com/francisoud/gmail2ldap/master/doc/images/gmail2ldap-tray.png) |
|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------|

# Thunderbird #

How to configure [Thunderbird Address Book](http://directory.apache.org/apacheds/1.5/41-mozilla-thunderbird.html#4.1.MozillaThunderbird-DefineApacheDirectoryServerasanaddressbook)

Use this parameters:

gmail2ldap dn value is:
`dc=gmail2ldap,dc=googlecode,dc=com`

![https://raw.githubusercontent.com/francisoud/gmail2ldap/master/doc/images/gmail2ldap-thunderbird.png](https://raw.githubusercontent.com/francisoud/gmail2ldap/master/doc/images/gmail2ldap-thunderbird.png)

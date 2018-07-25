package org.gluu.radius;

import java.util.Properties;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.InvalidPropertiesFormatException;


public class RadiusConfig {

	private static class RadiusClientAttributes {

		private Properties props;

		public RadiusClientAttributes(Properties props) {

			this.props = props;
		}


		public String getAttributeValue(String name,String defaultvalue) {

			return props.getProperty(name,defaultvalue);
		}

		public Set<String> getAttributeNames() {

			return props.stringPropertyNames();
		}
	}

	private String ipaddress;
	private Integer authport;
	private Integer acctport;
	private String sharedsecret;
	private Integer retrycount;
	private String  username;
	private String  password;
	private RadiusClientAttributes attributes;

	private static final String IPADDRESS_PROPERTY_KEY = "server.ipaddress";
	private static final String AUTHPORT_PROPERTY_KEY = "server.authport";
	private static final String ACCTPORT_PROPERTY_KEY = "server.acctport";
	private static final String SHARED_SECRET_KEY = "server.sharedsecret";
	private static final String RADIUS_ATTRIBUTES_KEY = "radius.attributes";
	private static final String RETRYCOUNT_KEY = "auth.retrycount";
	private static final String USERNAME_KEY = "auth.username";
	private static final String PASSWORD_KEY = "auth.password";

	private RadiusConfig() {

	}

	public String getIpAddress() {

		return ipaddress;
	}

	public Integer getAuthPort() {

		return authport;
	}


	public Integer getAcctPort() {

		return acctport;
	}


	public String getSharedSecret() {

		return sharedsecret;
	}

	public Integer getRetryCount() {

		return retrycount;
	}

	public String getUsername() {

		return username;
	}

	public String getPassword() {

		return password;
	}

	public Boolean hasAttributes() {

		return attributes != null;
	}

	public List<String> getAttributeNames() {

		List<String> ret = new ArrayList<String>();
		if(attributes!=null) {
			for(String attributename : attributes.getAttributeNames())
				ret.add(attributename);
		}
		return ret;
	}

	public String getAttributeValue(String name) {

		return getAttributeValue(name,null);
	}

	public String getAttributeValue(String name,String defaultvalue) {

		String ret = null;
		if(attributes != null)
			ret = attributes.getAttributeValue(name,defaultvalue);
		return ret;
	}



	public static final RadiusConfig loadFromFile(String filename) {

		RadiusConfig config = new RadiusConfig();
		Properties configprops = loadPropertiesFromXml(filename);

		config.ipaddress = configprops.getProperty(IPADDRESS_PROPERTY_KEY);
		config.authport  = propertyAsInt(AUTHPORT_PROPERTY_KEY,configprops);
		config.acctport  = propertyAsInt(ACCTPORT_PROPERTY_KEY,configprops);
		config.retrycount = propertyAsInt(RETRYCOUNT_KEY,configprops);
		config.username = configprops.getProperty(USERNAME_KEY);
		config.password = configprops.getProperty(PASSWORD_KEY);
		config.sharedsecret = configprops.getProperty(SHARED_SECRET_KEY);

		String attributesfile = configprops.getProperty(RADIUS_ATTRIBUTES_KEY);

		if(attributesfile!=null)
			config.attributes = new RadiusClientAttributes(loadPropertiesFromXml(attributesfile));

		return config;
	}

	private static final Properties loadPropertiesFromXml(String filename) {

		try {

			Properties properties = new Properties();
			FileInputStream infile = new FileInputStream(filename);
			properties.loadFromXML(infile);
			return properties;
		}catch(FileNotFoundException fe) {
			throw new RadiusConfigException("File <"+filename+"> not found",fe); 
		}catch(SecurityException se) {
			throw new RadiusConfigException("Permission denied opening <"+filename+">",se);
		}catch(IOException ioe) {
			throw new RadiusConfigException("Error reading from <"+filename+">",ioe);
		}
	}


	private static final Integer propertyAsInt(String propname,Properties props) {

		try {
			String propvalue = props.getProperty(propname);
			if(propvalue!=null)
				return Integer.parseInt(props.getProperty(propname));
			else
				return null;
		}catch(NumberFormatException e) {
			throw new RadiusConfigException("Property " + propname + " is not an integer. Check your config file");
		}
	}
}
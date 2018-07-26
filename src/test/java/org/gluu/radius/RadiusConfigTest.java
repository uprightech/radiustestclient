package org.gluu.radius; 

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Test;

public class RadiusConfigTest {
	
	private static final String MISSING_CONFIG_FILE = "missing-config.xml";
	private static final String INVALID_CONFIG_FILE = "invalid-config.xml";
	private static final String EMPTY_CONFIG_FILE = "empty-config.xml";
	private static final String MINIMAL_CONFIG_FILE = "minimal-config.xml";
	private static final String CONFIG_WITH_NO_ATTRIBUTES_FILE = "config-with-no-attributes.xml";
	private static final String MISSING_ATTRIBUTES_CONFIG_FILE = "missing-attributes.xml";
	private static final String INVALID_ATTRIBUTES_CONFIG_FILE = "invalid-attributes.xml";
	private static final String MISSING_ATTRIBUTES_FILE = "awol.xml";
	private static final String INVALID_ATTRIBUTES_FILE = "attrs-invalid.xml";
	private static final String CONFIG_WITH_ATTRIBUTES_FILE = "config-with-attributes.xml";
	private static final String FULL_METAL_JACK_CONFIG_FILE = "full-metal-jacket.xml";

	@Test
	public void missingConfigFile() {

		try {
			RadiusConfig config = RadiusConfig.create(MISSING_CONFIG_FILE);
		}catch(RadiusConfigException rce) {
			assertThat(rce.getMessage(),is("File <"+MISSING_CONFIG_FILE+"> not found"));
		}
	}


	@Test
	public void invalidConfigFile() {

		try  {
			RadiusConfig config = RadiusConfig.create(INVALID_CONFIG_FILE);
		}catch(RadiusConfigException rce) {
			assertThat(rce.getMessage(),is("Error reading from <"+INVALID_CONFIG_FILE+">"));
		}
	}

	
	@Test
	public void configIsEmpty() {

		RadiusConfig config = RadiusConfig.create(EMPTY_CONFIG_FILE);
		assertNotNull(config);
		assertNull(config.getIpAddress());
		assertNull(config.getAuthPort());
		assertNull(config.getAcctPort());
		assertNull(config.getSharedSecret());
		assertNull(config.getTimeout());
		assertNull(config.getUsername());
		assertNull(config.getPassword());
		assertNull(config.getRetryCount());
		assertFalse(config.hasAttributes());
	}

	@Test
	public void minimalConfigIsComplete() {

		RadiusConfig config = RadiusConfig.create(MINIMAL_CONFIG_FILE);
		assertNotNull(config);
		assertNotNull(config.getIpAddress());
		assertNotNull(config.getAuthPort());
		assertNotNull(config.getAcctPort());
		assertNotNull(config.getSharedSecret());
		assertNotNull(config.getTimeout());
		assertNotNull(config.getUsername());
		assertNotNull(config.getPassword());
		assertNotNull(config.getRetryCount());

		String ipaddress = "192.168.1.1";
		Integer authport = 1812;
		Integer acctport = 1813;
		String sharedsecret = "*wS?U+W6use_wppS";
		Integer timeout = 3000; 
		Integer retrycount = 3;
		String username = "user";
		String password = "pass1234";

		assertEquals(ipaddress,config.getIpAddress());
		assertEquals(authport,config.getAuthPort());
		assertEquals(acctport,config.getAcctPort());
		assertEquals(sharedsecret,config.getSharedSecret());
		assertEquals(timeout,config.getTimeout());
		assertEquals(retrycount,config.getRetryCount());
		assertEquals(username,config.getUsername());
		assertEquals(password,config.getPassword());
	}

	@Test
	public void configHasNoAttributes() {

		RadiusConfig config = RadiusConfig.create(CONFIG_WITH_NO_ATTRIBUTES_FILE);
		assertNotNull(config);
		assertFalse(config.hasAttributes());
	}


	@Test
	public void configWithMissingAttributesFile() {

		try {
			RadiusConfig config = RadiusConfig.create(MISSING_ATTRIBUTES_CONFIG_FILE);
		}catch(RadiusConfigException rce) {
			assertThat(rce.getMessage(),is("File <"+MISSING_ATTRIBUTES_FILE+"> not found"));
		}
	}

	@Test 
	public void configWithInvalidAttributesFile() {

		try  {
			RadiusConfig config = RadiusConfig.create(INVALID_ATTRIBUTES_CONFIG_FILE);
		}catch(RadiusConfigException rce) {
			assertThat(rce.getMessage(),is("Error reading from <"+INVALID_ATTRIBUTES_FILE+">"));
		}
	}


	@Test
	public void configHasAttributes() {

		RadiusConfig config = RadiusConfig.create(CONFIG_WITH_ATTRIBUTES_FILE);
		assertNotNull(config);
		assertTrue(config.hasAttributes());
	}

	@Test
	public void configWithAttributesIsComplete() {

		RadiusConfig config = RadiusConfig.create(FULL_METAL_JACK_CONFIG_FILE);
		assertNotNull(config);

		assertNotNull(config);
		assertNotNull(config.getIpAddress());
		assertNotNull(config.getAuthPort());
		assertNotNull(config.getAcctPort());
		assertNotNull(config.getSharedSecret());
		assertNotNull(config.getTimeout());
		assertNotNull(config.getUsername());
		assertNotNull(config.getPassword());
		assertNotNull(config.getRetryCount());

		String ipaddress = "192.168.1.1";
		Integer authport = 1812;
		Integer acctport = 1813;
		String sharedsecret = "*wS?U+W6use_wppS";
		Integer timeout = 1500;
		Integer retrycount = 6;
		String username = "user";
		String password = "password@123456";

		assertEquals(ipaddress,config.getIpAddress());
		assertEquals(authport,config.getAuthPort());
		assertEquals(acctport,config.getAcctPort());
		assertEquals(sharedsecret,config.getSharedSecret());
		assertEquals(timeout,config.getTimeout());
		assertEquals(retrycount,config.getRetryCount());
		assertEquals(username,config.getUsername());
		assertEquals(password,config.getPassword());

		// now test the attributes
		// we will include just two attributes
		String Nas_Ip_Address = "192.168.111.101";
		String Nas_Identifier = "GluuNAS";
		Integer attributecount = 2;

		assertTrue(config.hasAttributes());
		List<String> attributenames = config.getAttributeNames();
		assertEquals(attributecount,(Integer)attributenames.size());
		assertEquals(Nas_Ip_Address,config.getAttributeValue("NAS-IP-Address"));
		assertEquals(Nas_Identifier,config.getAttributeValue("NAS-Identifier"));

	}
} 	
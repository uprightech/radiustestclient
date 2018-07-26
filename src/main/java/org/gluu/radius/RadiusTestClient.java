package org.gluu.radius;

import java.io.IOException;
import java.net.SocketException;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusClient;
import org.tinyradius.util.RadiusException;


public class RadiusTestClient {

	private static final String  DEFAULT_IP_ADDRESS = "127.0.0.1";
	private static final Integer DEFAULT_AUTH_PORT = 1812;
	private static final Integer DEFAULT_ACCT_PORT = 1813;
	private static final Integer DEFAULT_RETRY_COUNT = 1;
	private static final String  DEFAULT_SHARED_SECRET = "mysecret";
	private static final Integer DEFAULT_TIMEOUT = 2000; // 2 seconds
	private static final String  DEFAULT_USERNAME = "";
	private static final String  DEFAULT_PASSWORD = "";

	private RadiusClient radiusclient;
	private RadiusConfig config;

	private RadiusTestClient(RadiusConfig config) {

		this.config = config;
		initRadiusClient();
	}

	private void initRadiusClient() {

		String ipaddress = (config.getIpAddress()==null?DEFAULT_IP_ADDRESS:config.getIpAddress());
		String secret = (config.getSharedSecret()==null?DEFAULT_SHARED_SECRET:config.getSharedSecret());
		Integer authport = (config.getAuthPort()==null?DEFAULT_AUTH_PORT:config.getAuthPort());
		Integer acctport = (config.getAcctPort()==null?DEFAULT_ACCT_PORT:config.getAcctPort());
		Integer retrycount = (config.getRetryCount()==null?DEFAULT_RETRY_COUNT:config.getRetryCount());
		Integer timeout = (config.getTimeout()==null?DEFAULT_TIMEOUT:config.getTimeout());
		if(timeout<=0)
			timeout = DEFAULT_TIMEOUT;

		radiusclient = new RadiusClient(ipaddress,secret);
		radiusclient.setRetryCount(retrycount);
		radiusclient.setAuthPort(authport);
		radiusclient.setAcctPort(acctport);
		try {
			radiusclient.setSocketTimeout(timeout);
		}catch(SocketException se) {
			//silently ignore this if it doesn't work
		}
	}

	private AccessRequest createRadiusAccessRequest() {

		String username = (config.getUsername()==null?DEFAULT_USERNAME:config.getUsername());
		String password = (config.getPassword()==null?DEFAULT_PASSWORD:config.getPassword());

		AccessRequest request = new AccessRequest(username,password);
		addRadiusAttributesToRequest(request);
		return request;
	}

	private void addRadiusAttributesToRequest(AccessRequest request) {

		if(!config.hasAttributes()) 
			return;

		for(String attribute : config.getAttributeNames()) {
			try {
				request.addAttribute(attribute,config.getAttributeValue(attribute));
			}catch(IllegalArgumentException e) {
				System.out.println("Error. Unsupported radius attribute <"+attribute+">. "+e.getMessage());
			}
		}
	}

	
	private boolean authenticate() throws IOException , RadiusException {

		boolean ret = false;
		AccessRequest request = createRadiusAccessRequest();
		RadiusPacket responsepacket = radiusclient.authenticate(request);
		return responsepacket.getPacketType() == RadiusPacket.ACCESS_ACCEPT;
	}

	private static final void printUsage() {

		System.out.println("Radius Test Utility v1.0.0");
		System.out.println("Usage: radiustestclient <config_file>");
		System.out.println("     <config_file> : a configuration xml file containing the request parameters");
	}
	
	public static void main(String [] args) {

		if(args.length < 1) {

			printUsage();
			return;
		}

		try {
			RadiusConfig config = RadiusConfig.create(args[0]);
			RadiusTestClient client = new RadiusTestClient(config);
			if(client.authenticate()) 
				System.out.println("Authentication success");
			else
				System.out.println("Authentication error");
		}catch(RadiusConfigException rce) {

			System.out.println("An error occured loading the configuration " + rce.getMessage());
		}catch(RadiusException re) {
			System.out.println("Radius error. " + re.getMessage());
		}catch(IOException ioe) {
			System.out.println("I/O error. " + ioe.getMessage());
		}
	}
}
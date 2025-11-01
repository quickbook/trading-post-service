package com.tps.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Service
public class IpAllowlistService {
	
	private final List<String> allowed;	  

	  public IpAllowlistService(
	      @Value("#{'${security.ip-allowlist}'.split(',')}") List<String> allowed) {
	    this.allowed = allowed.stream().map(String::trim).toList();
	  }

	public boolean isAllowed(String clientIp) {
		if (clientIp == null)
			return false;
		if (clientIp.startsWith("::ffff:"))
			clientIp = clientIp.substring(7);
		if (allowed.contains(clientIp))
			return true;
		try {
			InetAddress addr = InetAddress.getByName(clientIp);
			byte[] ip = addr.getAddress();
			for (String entry : allowed) {
				if (!entry.contains("/"))
					continue;
				String[] parts = entry.split("/");
				InetAddress net = InetAddress.getByName(parts[0]);
				int prefix = Integer.parseInt(parts[1]);
				byte[] network = net.getAddress();
				if (matchesPrefix(ip, network, prefix))
					return true;
			}
		} catch (UnknownHostException ignored) {
		}
		return false;
	}

	private boolean matchesPrefix(byte[] ip, byte[] network, int prefix) {
		int fullBytes = prefix / 8;
		int remainingBits = prefix % 8;
		for (int i = 0; i < fullBytes; i++) {
			if (ip[i] != network[i])
				return false;
		}
		if (remainingBits == 0)
			return true;
		int mask = (-1) << (8 - remainingBits);
		return (ip[fullBytes] & mask) == (network[fullBytes] & mask);
	}
}
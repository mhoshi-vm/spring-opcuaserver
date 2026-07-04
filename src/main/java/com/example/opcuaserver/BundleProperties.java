package com.example.opcuaserver;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuration properties selecting the SSL bundle used by the OPC UA server.
 */
@ConfigurationProperties("bundle")
record BundleProperties(
// @formatter:off
		@DefaultValue("self-signed")
		String name
		// @formatter:on
) {
}

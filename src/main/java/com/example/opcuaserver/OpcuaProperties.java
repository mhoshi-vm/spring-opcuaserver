package com.example.opcuaserver;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuration properties for the OPC UA server endpoints.
 */
@ConfigurationProperties("opcua")
record OpcuaProperties(
// @formatter:off
		@DefaultValue("4048")
		Integer tcpBindPort
		// @formatter:on
) {
}

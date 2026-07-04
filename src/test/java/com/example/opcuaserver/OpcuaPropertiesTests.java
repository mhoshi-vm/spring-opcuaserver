package com.example.opcuaserver;

import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link OpcuaProperties}.
 */
class OpcuaPropertiesTests {

	@Test
	void tcpBindPortDefaultsTo4048() {
		OpcuaProperties properties = bind(Map.of());
		assertThat(properties.tcpBindPort()).isEqualTo(4048);
	}

	@Test
	void tcpBindPortCanBeOverridden() {
		OpcuaProperties properties = bind(Map.of("opcua.tcp-bind-port", "14048"));
		assertThat(properties.tcpBindPort()).isEqualTo(14048);
	}

	private OpcuaProperties bind(Map<String, String> properties) {
		Binder binder = new Binder(new MapConfigurationPropertySource(properties));
		return binder.bindOrCreate("opcua", Bindable.of(OpcuaProperties.class));
	}

}

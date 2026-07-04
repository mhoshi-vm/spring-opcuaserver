package com.example.opcuaserver;

import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link BundleProperties}.
 */
class BundlePropertiesTests {

	@Test
	void nameDefaultsToSelfSigned() {
		BundleProperties properties = bind(Map.of());
		assertThat(properties.name()).isEqualTo("self-signed");
	}

	@Test
	void nameCanBeOverridden() {
		BundleProperties properties = bind(Map.of("bundle.name", "custom"));
		assertThat(properties.name()).isEqualTo("custom");
	}

	private BundleProperties bind(Map<String, String> properties) {
		Binder binder = new Binder(new MapConfigurationPropertySource(properties));
		return binder.bindOrCreate("bundle", Bindable.of(BundleProperties.class));
	}

}

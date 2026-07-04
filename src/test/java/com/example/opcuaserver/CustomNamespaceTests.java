package com.example.opcuaserver;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.security.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.eclipse.milo.opcua.stack.server.security.DefaultServerCertificateValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

/**
 * Unit tests for {@link CustomNamespace}.
 */
class CustomNamespaceTests {

	private static OpcUaServer server;

	private CustomNamespace namespace;

	@BeforeAll
	static void createServer() throws Exception {
		TestCertificates.CertificateAndKeyPair material = TestCertificates.generateSelfSigned();
		SslBundleReader reader = new SslBundleReader(TestCertificates.sslBundles("test-bundle", material),
				new BundleProperties("test-bundle"));
		OpcUaServerConfig config = OpcUaServerConfig.builder()
			.setApplicationUri("urn:example:opcuaserver:test")
			.setApplicationName(LocalizedText.english("test server"))
			.setEndpoints(Set.of())
			.setCertificateManager(new DefaultCertificateManager(material.keyPair(), material.certificate()))
			.setCertificateValidator(new DefaultServerCertificateValidator(new CustomTrustListManager(reader)))
			.setProductUri("urn:example:opcuaserver:test")
			.build();
		server = new OpcUaServer(config);
	}

	@BeforeEach
	void setUp() {
		this.namespace = new CustomNamespace(server);
	}

	@Test
	void startsUpAndShutsDown() {
		assertThatNoException().isThrownBy(() -> {
			this.namespace.startup();
			this.namespace.shutdown();
		});
	}

	@Test
	void revisesDataItemParametersAsRequested() {
		AtomicReference<Double> revisedSamplingInterval = new AtomicReference<>();
		AtomicReference<UInteger> revisedQueueSize = new AtomicReference<>();

		this.namespace.onCreateDataItem(readValueId(), 500.0, uint(10), (samplingInterval, queueSize) -> {
			revisedSamplingInterval.set(samplingInterval);
			revisedQueueSize.set(queueSize);
		});

		assertThat(revisedSamplingInterval.get()).isEqualTo(500.0);
		assertThat(revisedQueueSize.get()).isEqualTo(uint(10));

		this.namespace.onModifyDataItem(readValueId(), 250.0, uint(5), (samplingInterval, queueSize) -> {
			revisedSamplingInterval.set(samplingInterval);
			revisedQueueSize.set(queueSize);
		});

		assertThat(revisedSamplingInterval.get()).isEqualTo(250.0);
		assertThat(revisedQueueSize.get()).isEqualTo(uint(5));
	}

	@Test
	void revisesEventItemQueueSizeAsRequested() {
		AtomicReference<UInteger> revisedQueueSize = new AtomicReference<>();

		this.namespace.onCreateEventItem(readValueId(), uint(7), revisedQueueSize::set);
		assertThat(revisedQueueSize.get()).isEqualTo(uint(7));

		this.namespace.onModifyEventItem(readValueId(), uint(3), revisedQueueSize::set);
		assertThat(revisedQueueSize.get()).isEqualTo(uint(3));
	}

	private static ReadValueId readValueId() {
		return new ReadValueId(NodeId.NULL_VALUE, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);
	}

}

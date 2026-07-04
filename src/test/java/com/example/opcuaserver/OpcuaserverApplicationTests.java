package com.example.opcuaserver;

import java.net.Socket;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.TestSocketUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests loading the full application context with self-signed certificates
 * generated on the fly, so no external certificate setup is required.
 */
@SpringBootTest
class OpcuaserverApplicationTests {

	static final int TCP_BIND_PORT = TestSocketUtils.findAvailableTcpPort();

	@TempDir
	static Path certificates;

	@Autowired
	ApplicationContext applicationContext;

	@BeforeAll
	static void writeCertificates() throws Exception {
		TestCertificates.CertificateAndKeyPair material = TestCertificates.generateSelfSigned();
		TestCertificates.writePem(certificates, "ca.crt", "CERTIFICATE", material.certificate().getEncoded());
		TestCertificates.writePem(certificates, "server.crt", "CERTIFICATE", material.certificate().getEncoded());
		TestCertificates.writePem(certificates, "server.key", "PRIVATE KEY",
				material.keyPair().getPrivate().getEncoded());
	}

	@DynamicPropertySource
	static void sslBundleProperties(DynamicPropertyRegistry registry) {
		registry.add("opcua.tcp-bind-port", () -> TCP_BIND_PORT);
		registry.add("spring.ssl.bundle.pem.self-signed.truststore.certificate", () -> pemLocation("ca.crt"));
		registry.add("spring.ssl.bundle.pem.self-signed.keystore.certificate", () -> pemLocation("server.crt"));
		registry.add("spring.ssl.bundle.pem.self-signed.keystore.private-key", () -> pemLocation("server.key"));
	}

	private static String pemLocation(String fileName) {
		return "file:" + certificates.resolve(fileName);
	}

	@Test
	void contextLoads() {
		assertThat(this.applicationContext.getBean(MiloServer.class).isRunning()).isTrue();
		assertThat(this.applicationContext.getBean(SslBundleReader.class)).isNotNull();
		assertThat(this.applicationContext.getBean(BundleProperties.class).name()).isEqualTo("self-signed");
		assertThat(this.applicationContext.getBean(OpcuaProperties.class).tcpBindPort()).isEqualTo(TCP_BIND_PORT);
	}

	@Test
	void opcUaServerListensOnConfiguredPort() throws Exception {
		try (Socket socket = new Socket("localhost", TCP_BIND_PORT)) {
			assertThat(socket.isConnected()).isTrue();
		}
	}

}

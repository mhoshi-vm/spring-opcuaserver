package com.example.opcuaserver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Base64;

import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;

import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslStoreBundle;

/**
 * Test utility generating self-signed certificates and exposing them as
 * {@link SslBundles} or PEM files.
 */
final class TestCertificates {

	static final String KEY_PASSWORD = "changeit";

	private TestCertificates() {
	}

	record CertificateAndKeyPair(
	// @formatter:off
			X509Certificate certificate,
			KeyPair keyPair
			// @formatter:on
	) {
	}

	static CertificateAndKeyPair generateSelfSigned() throws Exception {
		KeyPair keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);
		X509Certificate certificate = new SelfSignedCertificateBuilder(keyPair).setCommonName("localhost")
			.setApplicationUri("urn:example:opcuaserver:test")
			.addDnsName("localhost")
			.build();
		return new CertificateAndKeyPair(certificate, keyPair);
	}

	static SslBundles sslBundles(String bundleName, CertificateAndKeyPair material) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(null, null);
		keyStore.setKeyEntry("test", material.keyPair().getPrivate(), KEY_PASSWORD.toCharArray(),
				new X509Certificate[] { material.certificate() });

		KeyStore trustStore = KeyStore.getInstance("PKCS12");
		trustStore.load(null, null);
		trustStore.setCertificateEntry("ca", material.certificate());

		SslBundle bundle = SslBundle.of(SslStoreBundle.of(keyStore, KEY_PASSWORD, trustStore));
		return new DefaultSslBundleRegistry(bundleName, bundle);
	}

	static Path writePem(Path directory, String fileName, String type, byte[] encoded) throws IOException {
		String base64 = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII)).encodeToString(encoded);
		String pem = "-----BEGIN " + type + "-----\n" + base64 + "\n-----END " + type + "-----\n";
		return Files.writeString(directory.resolve(fileName), pem);
	}

}

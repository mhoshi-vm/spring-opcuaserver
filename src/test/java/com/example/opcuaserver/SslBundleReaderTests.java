package com.example.opcuaserver;

import org.junit.jupiter.api.Test;

import org.springframework.boot.ssl.SslBundles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link SslBundleReader}.
 */
class SslBundleReaderTests {

	@Test
	void exposesCertificateKeyPairAndTrustMaterialFromBundle() throws Exception {
		TestCertificates.CertificateAndKeyPair material = TestCertificates.generateSelfSigned();
		SslBundles sslBundles = TestCertificates.sslBundles("test-bundle", material);

		SslBundleReader reader = new SslBundleReader(sslBundles, new BundleProperties("test-bundle"));

		assertThat(reader.getServerCertificate()).isEqualTo(material.certificate());
		assertThat(reader.getServerCertificateChain()).containsExactly(material.certificate());
		assertThat(reader.getServerKeyPair().getPublic()).isEqualTo(material.keyPair().getPublic());
		assertThat(reader.getServerKeyPair().getPrivate().getEncoded())
			.isEqualTo(material.keyPair().getPrivate().getEncoded());
		assertThat(reader.getAcceptedIssuers()).containsExactly(material.certificate());
		assertThat(reader.getX509TrustManagers()).isNotEmpty();
	}

}

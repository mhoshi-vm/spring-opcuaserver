package com.example.opcuaserver;

import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CustomTrustListManager}.
 */
class CustomTrustListManagerTests {

	private TestCertificates.CertificateAndKeyPair material;

	private CustomTrustListManager trustListManager;

	@BeforeEach
	void setUp() throws Exception {
		this.material = TestCertificates.generateSelfSigned();
		SslBundleReader reader = new SslBundleReader(TestCertificates.sslBundles("test-bundle", this.material),
				new BundleProperties("test-bundle"));
		this.trustListManager = new CustomTrustListManager(reader);
	}

	@Test
	void trustedAndIssuerCertificatesComeFromSslBundle() {
		assertThat(this.trustListManager.getTrustedCertificates()).containsExactly(this.material.certificate());
		assertThat(this.trustListManager.getIssuerCertificates()).containsExactly(this.material.certificate());
	}

	@Test
	void removalsAreNotSupported() {
		ByteString thumbprint = ByteString.of(new byte[] { 1, 2, 3 });
		assertThat(this.trustListManager.removeIssuerCertificate(thumbprint)).isFalse();
		assertThat(this.trustListManager.removeTrustedCertificate(thumbprint)).isFalse();
		assertThat(this.trustListManager.removeRejectedCertificate(thumbprint)).isFalse();
	}

}

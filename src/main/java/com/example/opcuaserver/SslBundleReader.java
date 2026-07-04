package com.example.opcuaserver;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.X509TrustManager;

import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.stereotype.Component;

/**
 * Reads the configured {@link SslBundle} and exposes the server certificate, key pair and
 * trust material required by the OPC UA server.
 */
@Component
class SslBundleReader {

	private final X509Certificate[] serverCertificateChain;

	private final X509Certificate serverCertificate;

	private final KeyPair serverKeyPair;

	private final X509TrustManager[] x509TrustManagers;

	private final List<X509Certificate> acceptedIssuers;

	SslBundleReader(SslBundles sslBundles, BundleProperties properties) throws KeyStoreException,
			UnrecoverableEntryException, NoSuchAlgorithmException, CertificateParsingException {

		SslBundle sslBundle = sslBundles.getBundle(properties.name());

		KeyStore keyStore = sslBundle.getStores().getKeyStore();
		char[] keyPassArray = sslBundle.getStores().getKeyStorePassword() != null
				? sslBundle.getStores().getKeyStorePassword().toCharArray() : null;

		Iterator<String> aliases = keyStore.aliases().asIterator();
		// Assumes only 1 alias is defined
		String alias = aliases.next();

		List<X509Certificate> serverCertificateChain = new ArrayList<>();
		X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
		certificate.getSubjectAlternativeNames();

		serverCertificateChain.add(certificate);

		List<X509Certificate> acceptedIssuers = new ArrayList<>();
		List<X509TrustManager> trustManagers = new ArrayList<>();

		Arrays.stream(sslBundle.getManagers().getTrustManagers()).forEach(trustManager -> {
			if (trustManager instanceof X509TrustManager x509TrustManager) {
				trustManagers.add(x509TrustManager);
				acceptedIssuers.addAll(Arrays.asList(x509TrustManager.getAcceptedIssuers()));
			}
		});

		this.serverCertificateChain = serverCertificateChain.toArray(new X509Certificate[0]);
		this.serverCertificate = certificate;

		this.serverKeyPair = new KeyPair(this.serverCertificate.getPublicKey(),
				(PrivateKey) keyStore.getKey(alias, keyPassArray));
		this.acceptedIssuers = List.copyOf(acceptedIssuers);
		this.x509TrustManagers = trustManagers.toArray(new X509TrustManager[0]);
	}

	X509Certificate[] getServerCertificateChain() {
		return this.serverCertificateChain;
	}

	X509Certificate getServerCertificate() {
		return this.serverCertificate;
	}

	KeyPair getServerKeyPair() {
		return this.serverKeyPair;
	}

	X509TrustManager[] getX509TrustManagers() {
		return this.x509TrustManagers;
	}

	List<X509Certificate> getAcceptedIssuers() {
		return this.acceptedIssuers;
	}

}

package com.example.opcuaserver;

import com.google.common.collect.ImmutableList;
import org.eclipse.milo.opcua.stack.core.security.TrustListManager;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

class CustomTrustListManager implements TrustListManager {

    ImmutableList<X509Certificate> acceptedIssuers;



    CustomTrustListManager(SslBundleReader sslBundleReader){
        this.acceptedIssuers = sslBundleReader.getAcceptedIssuers();
    }

    @Override
    public ImmutableList<X509CRL> getIssuerCrls() {
        return null;
    }

    @Override
    public ImmutableList<X509CRL> getTrustedCrls() {
        return null;
    }

    @Override
    public ImmutableList<X509Certificate> getIssuerCertificates() {
        return this.acceptedIssuers;
    }

    @Override
    public ImmutableList<X509Certificate> getTrustedCertificates() {
        return this.acceptedIssuers;
    }

    @Override
    public ImmutableList<X509Certificate> getRejectedCertificates() {
        return null;
    }

    @Override
    public void setIssuerCrls(List<X509CRL> issuerCrls) {

    }

    @Override
    public void setTrustedCrls(List<X509CRL> trustedCrls) {

    }

    @Override
    public void setIssuerCertificates(List<X509Certificate> issuerCertificates) {

    }

    @Override
    public void setTrustedCertificates(List<X509Certificate> trustedCertificates) {

    }

    @Override
    public void addIssuerCertificate(X509Certificate certificate) {

    }

    @Override
    public void addTrustedCertificate(X509Certificate certificate) {

    }

    @Override
    public void addRejectedCertificate(X509Certificate certificate) {

    }

    @Override
    public boolean removeIssuerCertificate(ByteString thumbprint) {
        return false;
    }

    @Override
    public boolean removeTrustedCertificate(ByteString thumbprint) {
        return false;
    }

    @Override
    public boolean removeRejectedCertificate(ByteString thumbprint) {
        return false;
    }
}

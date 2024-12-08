package com.example.opcuaserver;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.ManagedAddressSpaceFragmentWithLifecycle;
import org.eclipse.milo.opcua.sdk.server.api.ManagedAddressSpaceWithLifecycle;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.util.HostnameUtil;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaRuntimeException;
import org.eclipse.milo.opcua.stack.core.security.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.transport.TransportProfile;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.stack.core.types.structured.BuildInfo;
import org.eclipse.milo.opcua.stack.core.util.CertificateUtil;
import org.eclipse.milo.opcua.stack.server.EndpointConfiguration;
import org.eclipse.milo.opcua.stack.server.security.DefaultServerCertificateValidator;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig.*;

@Configuration
class MiloServer {

    private static final int TCP_BIND_PORT = 4048;
    SslBundleReader sslBundleReader;

    public MiloServer(SslBundleReader sslBundleReader) throws IOException, CertificateParsingException {
        this.sslBundleReader = sslBundleReader;

        DefaultCertificateManager certificateManager = new DefaultCertificateManager(
                sslBundleReader.getServerKeyPair(),
                sslBundleReader.getServerCertificateChain()
        );

        CustomTrustListManager trustListManager = new CustomTrustListManager(this.sslBundleReader);

        DefaultServerCertificateValidator certificateValidator =
                new DefaultServerCertificateValidator(trustListManager);

        // If you need to use multiple certificates you'll have to be smarter than this.
        X509Certificate certificate = this.sslBundleReader.getServerCertificate();


        // The configured application URI must match the one in the certificate(s)
        String applicationUri = "localhost";

        Set<EndpointConfiguration> endpointConfigurations = createEndpointConfigurations(certificate);

        OpcUaServerConfig serverConfig = OpcUaServerConfig.builder()
                .setApplicationUri(applicationUri)
                .setApplicationName(LocalizedText.english("Eclipse Milo OPC UA Example Server"))
                .setEndpoints(endpointConfigurations)
                .setBuildInfo(
                        new BuildInfo(
                                "urn:eclipse:milo:example-server",
                                "eclipse",
                                "eclipse milo example server",
                                OpcUaServer.SDK_VERSION,
                                "", DateTime.now()))
                .setCertificateManager(certificateManager)
                .setCertificateValidator(certificateValidator)
                .setProductUri("urn:eclipse:milo:example-server")
                .build();

        OpcUaServer server = new OpcUaServer(serverConfig);
        CustomNamespace namespace = new CustomNamespace(server);
        namespace.startup();
    }


    private Set<EndpointConfiguration> createEndpointConfigurations(X509Certificate certificate) {
        Set<EndpointConfiguration> endpointConfigurations = new LinkedHashSet<>();

        List<String> bindAddresses = new ArrayList<>();
        bindAddresses.add("0.0.0.0");

        Set<String> hostnames = new LinkedHashSet<>();
        hostnames.add(HostnameUtil.getHostname());
        hostnames.addAll(HostnameUtil.getHostnames("0.0.0.0"));

        for (String bindAddress : bindAddresses) {
            for (String hostname : hostnames) {
                EndpointConfiguration.Builder builder = EndpointConfiguration.newBuilder()
                        .setBindAddress(bindAddress)
                        .setHostname(hostname)
                        .setPath("/milo")
                        .setCertificate(certificate)
                        .addTokenPolicies(
                                USER_TOKEN_POLICY_ANONYMOUS,
                                USER_TOKEN_POLICY_USERNAME,
                                USER_TOKEN_POLICY_X509);


                EndpointConfiguration.Builder noSecurityBuilder = builder.copy()
                        .setSecurityPolicy(SecurityPolicy.None)
                        .setSecurityMode(MessageSecurityMode.None);

                endpointConfigurations.add(buildTcpEndpoint(noSecurityBuilder));

                // TCP Basic256Sha256 / SignAndEncrypt
                endpointConfigurations.add(buildTcpEndpoint(
                        builder.copy()
                                .setSecurityPolicy(SecurityPolicy.Basic256Sha256)
                                .setSecurityMode(MessageSecurityMode.SignAndEncrypt))
                );


                /*
                 * It's good practice to provide a discovery-specific endpoint with no security.
                 * It's required practice if all regular endpoints have security configured.
                 *
                 * Usage of the  "/discovery" suffix is defined by OPC UA Part 6:
                 *
                 * Each OPC UA Server Application implements the Discovery Service Set. If the OPC UA Server requires a
                 * different address for this Endpoint it shall create the address by appending the path "/discovery" to
                 * its base address.
                 */

                EndpointConfiguration.Builder discoveryBuilder = builder.copy()
                        .setPath("/milo/discovery")
                        .setSecurityPolicy(SecurityPolicy.None)
                        .setSecurityMode(MessageSecurityMode.None);

                endpointConfigurations.add(buildTcpEndpoint(discoveryBuilder));
            }
        }

        return endpointConfigurations;
    }

    private static EndpointConfiguration buildTcpEndpoint(EndpointConfiguration.Builder base) {
        return base.copy()
                .setTransportProfile(TransportProfile.TCP_UASC_UABINARY)
                .setBindPort(TCP_BIND_PORT)
                .build();
    }
}

package net.tenie.plugin.sqlServerConnector.impl;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class CustomSSLSocketFactory extends SSLSocketFactory {
    private final SSLSocketFactory defaultFactory;

    public CustomSSLSocketFactory() {
        this.defaultFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return defaultFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return defaultFactory.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket() throws IOException {
        SSLSocket sslSocket = (SSLSocket) defaultFactory.createSocket();
        configureSocket(sslSocket);
        return sslSocket;
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        SSLSocket sslSocket = (SSLSocket) defaultFactory.createSocket(s, host, port, autoClose);
        configureSocket(sslSocket);
        return sslSocket;
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        SSLSocket sslSocket = (SSLSocket) defaultFactory.createSocket(host, port);
        configureSocket(sslSocket);
        return sslSocket;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        SSLSocket sslSocket = (SSLSocket) defaultFactory.createSocket(host, port, localHost, localPort);
        configureSocket(sslSocket);
        return sslSocket;
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        SSLSocket sslSocket = (SSLSocket) defaultFactory.createSocket(host, port);
        configureSocket(sslSocket);
        return sslSocket;
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        SSLSocket sslSocket = (SSLSocket) defaultFactory.createSocket(address, port, localAddress, localPort);
        configureSocket(sslSocket);
        return sslSocket;
    }

    private void configureSocket(SSLSocket sslSocket) {
        sslSocket.setEnabledProtocols(new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"});
    }
}

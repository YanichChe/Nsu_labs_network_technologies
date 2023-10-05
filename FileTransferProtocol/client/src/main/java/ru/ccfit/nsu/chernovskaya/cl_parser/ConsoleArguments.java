package ru.ccfit.nsu.chernovskaya.cl_parser;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConsoleArguments {
    private final ConsoleArgumentsData inner;

    public ConsoleArguments(ConsoleArgumentsData innerValues) {
        this.inner = innerValues;
    }

    public InetAddress getAddress() throws UnknownHostException {
        return InetAddress.getByName(this.inner.address);
    }

    public int getPort() {
        return this.inner.port;
    }

    public int getClientPort() {
        return this.inner.clientPort;
    }

    public String getFilePath() {
        return this.inner.filePath;
    }
}

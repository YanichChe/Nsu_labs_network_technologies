package ru.nsu.ccfit.chernovskaya.parser;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConsoleArguments {
    private final ConsoleArgumentsData inner;

    public ConsoleArguments(ConsoleArgumentsData innerValues) {
        this.inner = innerValues;
    }

    public int getPort() {
        return this.inner.port;
    }

    public InetAddress getAddress() throws UnknownHostException {
        return InetAddress.getByName(this.inner.address);
    }

}

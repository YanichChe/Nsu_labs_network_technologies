package ru.ccfit.nsu.chernovskaya.cl_parser;

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
}

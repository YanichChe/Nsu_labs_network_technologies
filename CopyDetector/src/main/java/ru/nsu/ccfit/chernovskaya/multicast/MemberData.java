package ru.nsu.ccfit.chernovskaya.multicast;

import java.net.InetAddress;
import java.util.Objects;

public record MemberData (InetAddress inetAddress, int port, String pid) {
    @Override
    public String toString() {
        return "MemberData{" + "inetAddress=" + inetAddress + ", port=" + port + ", pid='" + pid + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MemberData that = (MemberData) o;
        return port == that.port && Objects.equals(inetAddress, that.inetAddress) && Objects.equals(pid, that.pid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inetAddress, port, pid);
    }
}

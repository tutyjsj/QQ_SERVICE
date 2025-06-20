package com.qq.tools.security;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 */
public class get {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        System.out.println(localHost);
    }
}
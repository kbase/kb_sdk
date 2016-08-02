package us.kbase.common.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class NetUtils {
    private static final Pattern IPADDRESS_PATTERN = Pattern.compile(
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static List<String> findNetworkAddresses(String... networkNames)
            throws SocketException {
        Set<String> networkNameSet = new HashSet<String>(Arrays.asList(networkNames));
        List<String> ret = new ArrayList<String>();
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();
            // breaks linux, if required for windows needs to be os-dependent
            // if (!intf.isUp()) continue;
            if (networkNameSet.contains(intf.getName()) || networkNameSet.contains(intf.getDisplayName())) {
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    String ip = enumIpAddr.nextElement().getHostAddress();
                    if (IPADDRESS_PATTERN.matcher(ip).matches()) 
                        ret.add(ip);
                }
            }
        }
        return ret;
    }
    
    public static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {}
        throw new IllegalStateException("Can not find available port in the system");
    }
}

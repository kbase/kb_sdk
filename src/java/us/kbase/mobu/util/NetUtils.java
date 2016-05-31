package us.kbase.mobu.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
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

    public static List<String> findNetworkAddresses(String... networkNames) throws Exception {
        Set<String> networkNameSet = new HashSet<String>(Arrays.asList(networkNames));
        List<String> ret = new ArrayList<String>();
        System.out.println("!!!!findNetworkAddresses");
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();
            String networkName = intf.getName();
            System.out.println("INTF:"+networkName);
            if (networkNameSet.contains(networkName)) {
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    String ip = enumIpAddr.nextElement().getHostAddress();
                    if (IPADDRESS_PATTERN.matcher(ip).matches())
                        ret.add(ip);
                }
            }
        }
        return ret;
    }
}

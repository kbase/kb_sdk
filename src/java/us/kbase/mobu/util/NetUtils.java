package us.kbase.mobu.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NetUtils {
    public static List<String> findNetworkAddresses(String... networkNames) throws Exception {
        Set<String> networkNameSet = new HashSet<String>(Arrays.asList(networkNames));
        List<String> ret = new ArrayList<String>();
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();
            String networkName = intf.getName();
            if (networkNameSet.contains(networkName)) {
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    ret.add(enumIpAddr.nextElement().getHostAddress());
                }
            }
        }
        return ret;
    }
}

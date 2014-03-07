
package skyseraph.nfc_wifi_assistant;

public class SimpleWifiInfoConverter {

    public static String toString(SimpleWifiInfo simpleWifiInfo) {
        return simpleWifiInfo.getType() + ";" + simpleWifiInfo.getSsid() + ";"
                + simpleWifiInfo.getKey();
    }

    public static SimpleWifiInfo fromString(String content) {
        String[] split = content.split(";");
        if (split.length == 3) {
            return new SimpleWifiInfo(split[0], split[1], split[2]);
        } else
            throw new IllegalArgumentException("Cannot parse given content");

    }
}

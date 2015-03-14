
package skyseraph.android.util;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

public class WiFiConnect {
    WifiManager wifiManager;

    private static final String TAG_ASSIST = "[WiFiConnect]-";

    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    public WiFiConnect(WifiManager wifiManager) {
        LogUtil.d(MyConstant.TAG, TAG_ASSIST + "WiFiConnect");
        this.wifiManager = wifiManager;
    }

    private boolean OpenWifi() {
        boolean bRet = true;
        if (!wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    public boolean Connect(String SSID, String Password, WifiCipherType Type) {
        if (!this.OpenWifi()) {
            LogUtil.e(MyConstant.TAG, TAG_ASSIST + "OpenWifi failue");
            return false;
        }
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                LogUtil.e(MyConstant.TAG, TAG_ASSIST + "WIFI_STATE_ENABLING");
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
        }

        WifiConfiguration wifiConfig = this.CreateWifiInfo(SSID, Password, Type);
        //
        if (wifiConfig == null) {
            LogUtil.e(MyConstant.TAG, TAG_ASSIST + "wifiConfig == null");
            return false;
        }

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            LogUtil.e(MyConstant.TAG, TAG_ASSIST + "tempConfig != null");
            wifiManager.removeNetwork(tempConfig.networkId);
        }

        boolean bRet = addNetwork(wifiConfig);
        return bRet;
    }

    private boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = wifiManager.addNetwork(wcg);
        LogUtil.d(MyConstant.TAG, TAG_ASSIST + "addNetwork wcgID is:" + wcgID);
        if (wcgID != -1) {
            boolean b = wifiManager.enableNetwork(wcgID, true);
            // boolean b = wifiManager.enableNetwork(wcgID, false);
            LogUtil.d(MyConstant.TAG, TAG_ASSIST + "addNetwork b is:" + b);
            LogUtil.d(MyConstant.TAG, TAG_ASSIST + "wifi connect success");
            wifiManager.saveConfiguration();
            return true;
        }
        return false;
    }

    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private WifiConfiguration CreateWifiInfo(String SSID, String Password, WifiCipherType Type) {
        LogUtil.d(MyConstant.TAG, TAG_ASSIST + "CreateWifiInfo Type=" + Type + ",Password="+Password+ ",SSID="+SSID);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiCipherType.WIFICIPHER_WPA) {
//            config.preSharedKey = "\"" + Password + "\"";
//            config.hiddenSSID = true;
//            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            
            //WPA/WPA2 Security
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.preSharedKey = "\"".concat(Password).concat("\"");
            
        } else {
            return null;
        }
        return config;
    }

}


package skyseraph.nfc_wifi_assistant;

import com.google.common.base.Preconditions;

import skyseraph.android.util.LogUtil;
import skyseraph.android.util.MyConstant;
import skyseraph.android.util.WiFiConnect;
import skyseraph.android.util.WiFiConnect.WifiCipherType;
import skyseraph.android.util.WifiAdmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class WifiConnectHandler extends Activity {
    private static final String TAG_ASSIST = "[WifiConnectHandler]-";

    @Override
    protected void onResume() {
        super.onResume();
        handleIntent();
        finish();
    }

    private void handleIntent() {
        resolveNdefMessagesIntent(getIntent());
    }

    void resolveNdefMessagesIntent(Intent intent) {
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "into resolveNdefMessagesIntent");
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        // if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) ||
        // NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
        {
            LogUtil.i(MyConstant.TAG, TAG_ASSIST + "ACTION_NDEF_DISCOVERED");
            NdefMessage[] messages = null;
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                messages = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    messages[i] = (NdefMessage)rawMsgs[i];
                    LogUtil.i(MyConstant.TAG, TAG_ASSIST + "messages[i] = " + messages[i]);
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                    record
                });
                messages = new NdefMessage[] {
                    msg
                };
            }
            // Setup the views
            // setTitle(R.string.title_scanned_tag);
            processNDEFTag_RTDText(messages);
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            LogUtil.i(MyConstant.TAG, TAG_ASSIST + "ACTION_TECH_DISCOVERED");
        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            LogUtil.i(MyConstant.TAG, TAG_ASSIST + "ACTION_TAG_DISCOVERED");
        } else {
            LogUtil.e(MyConstant.TAG, TAG_ASSIST + "Unknown intent " + intent);
            finish();
            return;
        }
    }

    private void processNDEFTag_RTDText(NdefMessage[] messages) {
        // TODO Auto-generated method stub
        if (messages == null || messages.length == 0) {
            LogUtil.e(MyConstant.TAG, TAG_ASSIST + "NdefMessage is null");
            return;
        }

        for (int i = 0; i < messages.length; i++) {
            int length = messages[i].getRecords().length;
            LogUtil.i(MyConstant.TAG, TAG_ASSIST + "Message " + (i + 1) + "," + "length=" + length);
            NdefRecord[] records = messages[i].getRecords();
            for (int j = 0; j < length; j++) {
                for (NdefRecord record : records) {
                    if (isTextRecord(record)) {
                        parseRTD_TEXTRecord(record);
                    }
                }
            }
        }
    }

    public static boolean isTextRecord(NdefRecord record) {
        if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
            if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    void parseRTD_TEXTRecord(NdefRecord record) {
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "into parseRTD_TEXTRecord");
        Preconditions.checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT));

        String payloadStr = "";
        byte[] payload = record.getPayload();
        Byte statusByte = record.getPayload()[0];

        String textEncoding = ((statusByte & 0200) == 0) ? "UTF-8" : "UTF-16";// 0x80=0200
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "textEncoding = " + textEncoding);
        int languageCodeLength = statusByte & 0077; // & 0x3F=0077(bit 5 to 0)
        String languageCode = new String(payload, 1, languageCodeLength, Charset.forName("UTF-8"));
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "languageCodeLength = " + languageCodeLength
                + ",languageCode = " + languageCode);
        try {
            payloadStr = new String(payload, languageCodeLength + 1, payload.length
                    - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "the Record Tnf: " + record.getTnf() + "\n");// 1
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "the Record type: " + new String(record.getType())
                + "\n");// T
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "the Record id: " + new String(record.getId())
                + "\n");
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "the Record payload: " + payloadStr + "\n");

        String wifiConfigString[] = payloadStr.split(";");
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "type: " + wifiConfigString[0] + "ssid: "
                + wifiConfigString[1] + "key: " + wifiConfigString[2] + "\n");
        String type, ssid, key;
        if (wifiConfigString[0] == null || wifiConfigString[1] == null
                || wifiConfigString[2] == null) {
            LogUtil.i(MyConstant.TAG, TAG_ASSIST + "wifiConfigString null!!! " + "\n");
            return;
        }
        type = wifiConfigString[0];
        ssid = wifiConfigString[1];
        key = wifiConfigString[2];
        SimpleWifiInfo wifiInfo = new SimpleWifiInfo(type, ssid, key);

        // Method 1
//         setNewWifi(wifiInfo);

        // Method 2
//        WifiAdmin wifiAdmin = new WifiAdmin(this);
//        wifiAdmin.openWifi();
//        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, key, type));

        // Method 3
        WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WiFiConnect wiFiConnect = new WiFiConnect(wifiManager);
        WifiCipherType Type;
        if (type.equals("none")) {
            Type = WifiCipherType.WIFICIPHER_NOPASS;
        } else if (type.equals("wep")) {
            Type = WifiCipherType.WIFICIPHER_WEP;
        } else if (type.equals("wpa")) {
            Type = WifiCipherType.WIFICIPHER_WPA;
        } else {
            Type = WifiCipherType.WIFICIPHER_INVALID;
        }
        boolean flag = wiFiConnect.Connect(ssid, key, Type);
        if (flag == true) {
            showLongToast("Now connected to known network \"" + wifiInfo.getSsid());
        } else {
            showLongToast("Creating connection failed.");
        }
    }

    protected void setNewWifi(SimpleWifiInfo wifiInfo) {
        WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        boolean foundAKnownNetwork = false;
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration wifiConfiguration : configuredNetworks) {
            if (wifiConfiguration.SSID.equals("\"" + wifiInfo.getSsid() + "\"")) {
                foundAKnownNetwork = true;
                boolean result = wifiManager.enableNetwork(wifiConfiguration.networkId, true);
                if (result) {
                    showLongToast("Now connected to known network \""
                            + wifiInfo.getSsid()
                            + "\". If you want to set a new WPA key, please delete the network first.");
                } else {
                    showLongToast("Connection to a known network failed.");
                }
            }
        }

        if (!foundAKnownNetwork) {
            setupNewNetwork(wifiInfo, wifiManager);
        }
    }

    protected void setupNewNetwork(SimpleWifiInfo wifiInfo, WifiManager wifiManager) {
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + wifiInfo.getSsid() + "\"";

        if (wifiInfo.isKeyPreHashed())
            wc.preSharedKey = wifiInfo.getKey();
        else
            wc.preSharedKey = "\"" + wifiInfo.getKey() + "\"";

        int networkId = wifiManager.addNetwork(wc);
        boolean result = wifiManager.enableNetwork(networkId, true);

        if (result) {
            showLongToast("Now connected to \"" + wifiInfo.getSsid() + "\"");
            wifiManager.saveConfiguration();
        } else {
            showLongToast("Creating connection failed. " + wc);
        }
    }

    private void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}


package skyseraph.nfc_wifi_assistant;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleWifiInfo implements Parcelable {
    private String type;

    private String ssid;

    private String key;

    public static final Parcelable.Creator<SimpleWifiInfo> CREATOR = new Parcelable.Creator<SimpleWifiInfo>() {

        @Override
        public SimpleWifiInfo createFromParcel(Parcel source) {
            return new SimpleWifiInfo(source.readString(), source.readString(), source.readString());
        }

        @Override
        public SimpleWifiInfo[] newArray(int size) {
            return new SimpleWifiInfo[size];
        }
    };

    public SimpleWifiInfo(String type, String ssid, String key) {
        this.type = type;
        this.ssid = ssid;
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public String getSsid() {
        return ssid;
    }

    public String getKey() {
        return key;
    }

    public boolean isKeyPreHashed() {
        return key != null && key.length() == 64;
    }

    @Override
    public String toString() {
        return "Type: " + type + " SSID: " + ssid + " Key: " + key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(ssid);
        dest.writeString(key);
    }
}

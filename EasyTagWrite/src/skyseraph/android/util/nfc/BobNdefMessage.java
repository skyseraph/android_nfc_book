
package skyseraph.android.util.nfc;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import skyseraph.android.util.LogUtil;
import skyseraph.android.util.MyConstant;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.nio.charset.Charset;
import java.util.Locale;

/**
 * @Title ：BobNdefMessage.java
 * @Package ：skyseraph.android.util.nfc
 * @ClassName : BobNdefMessage
 * @Description ：Custom Class For NdefMessage
 * @author ： skyseraph00@163.com
 * @date ： 2013-5-13 上午11:14:51
 * @version ： V1.0 《Android NFC 开发实战详解》
 */
public class BobNdefMessage {
    private static final String TAG_ASSIST = "[BobNdefMessage]-";

    /**
     * @About:create a TNF_WELL_KNOW NDEF record as RTD_URI
     * @param uriFiledStr , The rest of the URI, or the entire URI (if
     *            identifier code is 0x00).
     * @param identifierCode = prefixes(URI identifier code), 0x01=http://www.
     *            ,0x02=https://www. , 0x03=http://
     * @param flagAddAAR, true means add AAR
     * @return NdefMessage
     * @Ref: NFCForum-TS-RTD_URI_1.0
     * @By SkySeraph-2013
     */
    public static NdefMessage getNdefMsg_from_RTD_URI(String uriFiledStr, byte identifierCode,
            boolean flagAddAAR) {
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "into getNdefMsg_from_RTD_URI");
        byte[] uriField = uriFiledStr.getBytes(Charset.forName("US-ASCII"));
        byte[] payLoad = new byte[uriField.length + 1]; // add 1 for the URI
                                                        // Prefix
        payLoad[0] = identifierCode; // 0x01 = prefixes http://www. to the URI
        // appends URI to payload
        System.arraycopy(uriField, 0, payLoad, 1, uriField.length);

        // Method1:
        NdefRecord rtdUriRecord1 = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI,
                new byte[0], payLoad);

        // Method2:only in API 14
        String prefix = URI_PREFIX_MAP.get(identifierCode);
        NdefRecord rtdUriRecord2 = NdefRecord.createUri(prefix + uriFiledStr);

        // Method3:only in API 14
        NdefRecord rtdUriRecord3 = NdefRecord.createUri(Uri.parse(prefix + uriFiledStr));

        if (flagAddAAR) {
            // note: returns AAR for different app (nfcreadtag)
            return new NdefMessage(new NdefRecord[] {
                    rtdUriRecord1, NdefRecord.createApplicationRecord("skyseraph.nfc_demo")
            }); // packageName
        } else {
            return new NdefMessage(new NdefRecord[] {
                rtdUriRecord1
            });
        }
    }

    /**
     * @About:create a TNF_WELL_KNOW NDEF record as RTD_TEXT
     * @param text , the really text data
     * @param encodeInUtf8 , false means TEXT encoded by UTF-8
     * @param flagAddAAR , true means add AAR
     * @return NdefMessage
     * @Ref: NFCForum-TS-RTD_Text_1.0
     * @By SkySeraph-2013
     */
    public static NdefMessage getNdefMsg_from_RTD_TEXT(String text, boolean encodeInUtf8,
            boolean flagAddAAR) {
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "into getNdefMsg_from_RTD_TEXT");

        Locale locale = new Locale("en", "US"); // a new Locale is created with
                                                // US English.
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        // boolean encodeInUtf8 = false;
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char)(utfBit + langBytes.length);
        // String text = "This is an RTD_TEXT exp";
        byte[] textBytes = text.getBytes(utfEncoding);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte)status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
                new byte[0], data);

        if (flagAddAAR) {
            // note: returns AAR for different app (nfcreadtag)
            return new NdefMessage(new NdefRecord[] {
                    textRecord, NdefRecord.createApplicationRecord("skyseraph.nfc_demo")
            });
        } else {
            return new NdefMessage(new NdefRecord[] {
                textRecord
            });
        }
    }

    /**
     * @About: create a TNF_ABSOLUTE_URI NDEF record
     * @param absoluteUri ,the absolute Uri
     * @param flagAddAAR , true means add AAR
     * @return NdefMessage
     * @Note: TNF_ABSOLUTE_URI indicates the absolute form of a URI that follows
     *        the absolute-URI rule defined by RFC 3986
     * @Note: Recommend that you use the RTD_URI type instead of
     *        TNF_ABSOLUTE_URI, because it is more efficient
     * @By SkySeraph-2013
     */
    public static NdefMessage getNdefMsg_from_ABSOLUTE_URI(String absoluteUri, boolean flagAddAAR) {
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "into getNdefMsg_from_ABSOLUTE_URI");
        // String absoluteUri = "http://developer.android.com/index.html";
        byte[] absoluteUriBytes = absoluteUri.getBytes(Charset.forName("US-ASCII"));
        NdefRecord uriRecord = new NdefRecord(NdefRecord.TNF_ABSOLUTE_URI, absoluteUriBytes,
                new byte[0], new byte[0]);
        if (flagAddAAR) {
            // note: returns AAR for different app (nfcreadtag)
            return new NdefMessage(new NdefRecord[] {
                    uriRecord, NdefRecord.createApplicationRecord("skyseraph.nfc_demo")
            });
        } else {
            return new NdefMessage(new NdefRecord[] {
                uriRecord
            });
        }
    }

    /**
     * @About:create a TNF_MIME_MEDIA NDEF record
     * @param payLoad,the MIME data
     * @param mimeType,the MIME Type
     * @param flagAddAAR, true means add AAR
     * @return NdefMessage
     * @By SkySeraph-2013
     */
    @SuppressLint("NewApi")
    public static NdefMessage getNdefMsg_from_MIME_MEDIA(String payLoad, String mimeType,
            boolean flagAddAAR) {
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "into getNdefMsg_from_MIME_MEDIA");
        byte[] payLoadBytes = payLoad.getBytes(Charset.forName("US-ASCII"));
        // String mimeType = "application/skyseraph.nfc_demo";

        // method1:Creating the NdefRecord manually
        NdefRecord mimeRecord1 = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                mimeType.getBytes(Charset.forName("US-ASCII")), new byte[0], payLoadBytes);
        // the identfier of the record is given as 0, since it will be the first
        // record in the NdefMessage

        // method2:Using the createMime() method, in API-16
        NdefRecord mimeRecord2 = NdefRecord.createMime(mimeType, payLoadBytes);

        if (flagAddAAR) {
            // note: returns AAR for different app (nfcreadtag)
            return new NdefMessage(new NdefRecord[] {
                    mimeRecord1, NdefRecord.createApplicationRecord("skyseraph.nfc_demo")
            });
        } else {
            return new NdefMessage(new NdefRecord[] {
                mimeRecord1
            });
        }
    }

    /**
     * @About:create a TNF_EXTERNAL_TYPE NDEF record
     * @param payLoad,the EXTERNAL data
     * @param flagAddAAR, true means add AAR
     * @return NdefMessage
     * @By SkySeraph-2013
     */
    @SuppressLint("NewApi")
    public static NdefMessage getNdefMsg_from_EXTERNAL_TYPE(String payLoad, boolean flagAddAAR) {
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "into getNdefMsg_from_EXTERNAL_TYPE");
        byte[] payLoadBytes = payLoad.getBytes();
        String domain = "skyseraph.nfc_demo"; // usually your app's package name
        String type = "externalType";
        String externalType = domain + ":" + type;

        // method1:Creating the NdefRecord manually
        NdefRecord exteralRecord1 = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE,
                externalType.getBytes(), new byte[0], payLoadBytes);

        // method2:Using the createExternal() method, in API-16
        NdefRecord exteralRecord2 = NdefRecord.createExternal(domain, type, payLoadBytes);

        if (flagAddAAR) {
            // note: returns AAR for different app (nfcreadtag)
            return new NdefMessage(new NdefRecord[] {
                    exteralRecord1, NdefRecord.createApplicationRecord("skyseraph.nfc_demo")
            });
        } else {
            return new NdefMessage(new NdefRecord[] {
                exteralRecord1
            });
        }
    }

    /**
     * checkSystemVersion()
     */
    private boolean flagVersion = false;

    private void checkSystemVersion() {
        // TODO Auto-generated method stub
        // if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)

        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "Product Model=" + android.os.Build.MODEL + ", "
                + android.os.Build.VERSION.SDK + ", " + android.os.Build.VERSION.RELEASE);
        String systemModel;
        String releaseVersion;
        String sdkVersion;
        systemModel = android.os.Build.MODEL;
        sdkVersion = android.os.Build.VERSION.SDK;
        releaseVersion = android.os.Build.VERSION.RELEASE;
        if (Integer.parseInt(sdkVersion) > 15) {
            flagVersion = true;
        } else {
            flagVersion = false;
            LogUtil.e(MyConstant.TAG, TAG_ASSIST + "Your android system version is low to API-16");
        }
    }

    /**
     * NFC Forum "URI Record Type Definition" This is a mapping of
     * "URI Identifier Codes" to URI string prefixes, per section 3.2.2 of the
     * NFC Forum URI Record Type Definition document.
     */
    private static final BiMap<Byte, String> URI_PREFIX_MAP = ImmutableBiMap
            .<Byte, String> builder().put((byte)0x00, "").put((byte)0x01, "http://www.")
            .put((byte)0x02, "https://www.").put((byte)0x03, "http://").put((byte)0x04, "https://")
            .put((byte)0x05, "tel:").put((byte)0x06, "mailto:")
            .put((byte)0x07, "ftp://anonymous:anonymous@").put((byte)0x08, "ftp://ftp.")
            .put((byte)0x09, "ftps://").put((byte)0x0A, "sftp://").put((byte)0x0B, "smb://")
            .put((byte)0x0C, "nfs://").put((byte)0x0D, "ftp://").put((byte)0x0E, "dav://")
            .put((byte)0x0F, "news:").put((byte)0x10, "telnet://").put((byte)0x11, "imap:")
            .put((byte)0x12, "rtsp://").put((byte)0x13, "urn:").put((byte)0x14, "pop:")
            .put((byte)0x15, "sip:").put((byte)0x16, "sips:").put((byte)0x17, "tftp:")
            .put((byte)0x18, "btspp://").put((byte)0x19, "btl2cap://").put((byte)0x1A, "btgoep://")
            .put((byte)0x1B, "tcpobex://").put((byte)0x1C, "irdaobex://")
            .put((byte)0x1D, "file://").put((byte)0x1E, "urn:epc:id:")
            .put((byte)0x1F, "urn:epc:tag:").put((byte)0x20, "urn:epc:pat:")
            .put((byte)0x21, "urn:epc:raw:").put((byte)0x22, "urn:epc:")
            .put((byte)0x23, "urn:nfc:").build();
}

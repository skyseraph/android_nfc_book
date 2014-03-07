
package skyseraph.nfc_wifi_assistant;

import skyseraph.android.util.CustomDialog;
import skyseraph.android.util.LogUtil;
import skyseraph.android.util.MyActivityFinishHandler;
import skyseraph.android.util.MyConstant;
import skyseraph.android.util.nfc.BobNdefMessage;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class WifiConnectConfigWriter extends Activity {
    private static final String TAG_ASSIST = "[WifiConnectConfigWriter]-";

    // NFC Declarations
    private NfcAdapter mNfcAdapter = null;

    private IntentFilter[] mFilters = null;

    private PendingIntent mPendingIntent = null;

    private String[][] mTechLists = null;

    private Context mContext;

    private NdefMessage NDEFMsg2Write = null;

    // UI Declarations
    private Button mButton = null;

    private SimpleWifiInfo simpleWifiInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_connect_config_write);
        mContext = this;

        checkNFCFunction(); // NFC Check
        initUI();// Init UI
        initNFC();// Init NFC
    }

    private void initUI() {
        mButton = (Button)findViewById(R.id.btn);
        mButton.setOnClickListener(new MyActivityFinishHandler(this));
    }

    /**
     * Init NFC
     */
    private void initNFC() {
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "into initNFC");
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // FLAG_ACTIVITY_SINGLE_TOP: not creating multiple instances of the same
        // application.
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        // Intent filters for writing to a tag
        mFilters = new IntentFilter[] {
            ndefDetected,
        };// just trying to find a ndef

        mTechLists = new String[][] {
                new String[] {
                    Ndef.class.getName()
                }, new String[] {
                    NdefFormatable.class.getName()
                }
        };

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "into onResume");
        // get Wifi Inf.
        simpleWifiInfo = WifiConnectConfigWriter.this.getIntent().getParcelableExtra(
                "SIMPLE_WIFI_INFO");
        enableForegroundDispatch();
    }

    @Override
    public void onNewIntent(Intent intent) {
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "Discovered tag with intent: " + intent);
        setIntent(intent);
        // get NFC object
        Tag detectTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (detectTag != null && simpleWifiInfo != null) {
            // validate that this tag can be written
            if (supportedTechs(detectTag.getTechList())) {
                String payloadStr = SimpleWifiInfoConverter.toString(simpleWifiInfo);
                LogUtil.i(MyConstant.TAG, TAG_ASSIST + "payloadStr: " + payloadStr);
                NDEFMsg2Write = BobNdefMessage.getNdefMsg_from_RTD_TEXT(payloadStr, false, false);
                new WriteTask(this, NDEFMsg2Write, detectTag).execute(); // By
                                                                         // AsyncTask
                                                                         // Class
            } else {
                Toast.makeText(mContext, "This tag type is not supported", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        disableForegroundDispatch();
    }

    /**
     * @Title: supportedTechs
     * @Description: Check Support Techs
     * @param @param techs
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean supportedTechs(String[] techs) {
        LogUtil.i(MyConstant.TAG, TAG_ASSIST + "into supportedTechs");
        for (String s : techs) {
            LogUtil.i(MyConstant.TAG, TAG_ASSIST + "all supportedTechs = " + s);
        }
        boolean tech_ndef = false;
        for (String tech : techs) {
            if (tech.equals("android.nfc.tech.Ndef")
                    || tech.equals("android.nfc.tech.NdefFormatable")) {
                tech_ndef = true;
                LogUtil.i(MyConstant.TAG, TAG_ASSIST + "supportedTechs is:Ndef/NdefFormatable");
            } else {
                tech_ndef = false;
            }
        }
        if (tech_ndef) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * enable TagWrite
     */
    private void enableForegroundDispatch() {
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
    }

    /**
     * disable TagWrite
     */
    private void disableForegroundDispatch() {
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    /**
     * NFC Function Check By skyseraph 2013-2
     */
    private void checkNFCFunction() {
        // TODO Auto-generated method stub
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // check the NFC adapter first
        if (mNfcAdapter == null) {
            // mTextView.setText("NFC apdater is not available");
            Dialog dialog = null;
            CustomDialog.Builder customBuilder = new CustomDialog.Builder(mContext);
            customBuilder
                    .setTitle(getString(R.string.inquire))
                    .setMessage(getString(R.string.nfc_notice2))
                    .setIcon(R.drawable.dialog_icon2)
                    .setNegativeButton(getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                    .setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
            dialog = customBuilder.create();
            dialog.setCancelable(false);// back
            dialog.setCanceledOnTouchOutside(false);
            SetDialogWidth(dialog).show();
            return;
        } else {
            if (!mNfcAdapter.isEnabled()) {
                Dialog dialog = null;
                CustomDialog.Builder customBuilder = new CustomDialog.Builder(mContext);
                customBuilder
                        .setTitle(getString(R.string.inquire))
                        .setMessage(getString(R.string.nfc_notice3))
                        .setIcon(R.drawable.dialog_icon2)
                        .setNegativeButton(getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                        .setPositiveButton(getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent setnfc = new Intent(
                                                Settings.ACTION_WIRELESS_SETTINGS);
                                        // Intent setnfc = new
                                        // Intent(Settings.ACTION_NFC_SETTINGS);
                                        startActivity(setnfc);
                                    }
                                });
                dialog = customBuilder.create();
                dialog.setCancelable(false);// back
                dialog.setCanceledOnTouchOutside(false);
                SetDialogWidth(dialog).show();
                return;
            }
        }
    }

    /**
     * @param dialog
     * @return
     */
    private Dialog SetDialogWidth(Dialog dialog) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        if (screenWidth > screenHeight) {
            params.width = (int)(((float)screenHeight) * 0.875);

        } else {
            params.width = (int)(((float)screenWidth) * 0.875);
        }
        dialog.getWindow().setAttributes(params);

        return dialog;
    }

    /**
     * @Title ：WriteNdefTag.java
     * @Package ：skyseraph.nfc_demo.tag.write
     * @ClassName : WriteTask
     * @Description ： TODO
     * @author ： skyseraph00@163.com
     * @date ： 2013-9-13 上午11:38:58
     * @version ： V1.0
     */
    static class WriteTask extends AsyncTask<Void, Void, Void> {
        Activity host = null;

        NdefMessage msg = null;

        Tag tag = null;

        String text = null;

        WriteTask(Activity host, NdefMessage msg, Tag tag) {
            LogUtil.i(MyConstant.TAG, TAG_ASSIST + "into WriteTask AsyncTask");
            this.host = host;
            this.msg = msg;
            this.tag = tag;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            int size = msg.toByteArray().length;

            try {
                Ndef ndef = Ndef.get(tag);

                if (ndef == null) {
                    NdefFormatable formatable = NdefFormatable.get(tag);
                    if (formatable != null) {
                        try {
                            formatable.connect();
                            try {
                                formatable.format(msg);
                            } catch (Exception e) {
                                text = "Failed to format tag，Tag refused to format";
                            }
                        } catch (Exception e) {
                            text = "Failed to connect tag，Tag refused to connect";
                        } finally {
                            formatable.close();
                        }
                    } else {
                        text = "NDEF is not supported in this Tag";
                    }
                } else {
                    ndef.connect();

                    try {
                        if (!ndef.isWritable()) {
                            text = "Tag is read-only";
                        } else if (ndef.getMaxSize() < size) {
                            text = "The data cannot written to tag，Message is too big for tag，Tag capacity is "
                                    + ndef.getMaxSize() + " bytes, message is " + size + " bytes.";
                        } else {
                            ndef.writeNdefMessage(msg);
                            text = "Message is written tag, message=" + msg;
                        }
                    } catch (Exception e) {
                        text = "Tag refused to connect";
                    } finally {
                        ndef.close();
                    }
                }
            } catch (Exception e) {
                text = "Write opreation is failed，General exception: " + e.getMessage();
                LogUtil.i(MyConstant.TAG, TAG_ASSIST
                        + "Exception when writing tag，Write opreation is failed" + text);
            }

            return (null);
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (text != null) {
                Toast.makeText(host, text, Toast.LENGTH_SHORT).show();
                LogUtil.e(MyConstant.TAG, TAG_ASSIST + text);
            }

            host.finish(); // after writed, auto finish
        }
    }
}

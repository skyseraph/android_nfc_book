
package skyseraph.nfc_wifi_assistant;

import skyseraph.android.util.LogUtil;
import skyseraph.android.util.MyConstant;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NfcWiFiAssistant extends Activity {
    private static final String TAG_ASSIST = "[NfcWiFiAssistant]-";

    private Button mBtn = null;

    private EditText mEditText1 = null;

    private EditText mEditText2 = null;

    private Spinner mSpinner = null;

    private ArrayAdapter mAdapter = null;

    private static final String[] spinnerStr = {
            "NONE", "WEP", "WPA/WPA2",
    };

    private String ssidString = null;

    private String keyString = null;

    private String securityStirng = null;

    private AlertDialog alertDialog = null;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_wifi_assistant_main);
        mContext = this;
        initUI();
        initFunction();
    }

    private void initFunction() {
        // TODO Auto-generated method stub
        mBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (null == ssidString) {
                    Toast.makeText(NfcWiFiAssistant.this, "Default SSID", Toast.LENGTH_LONG).show();
                    ssidString = "TOTOLINK_Jeff";
                } else {
                    ssidString = mEditText1.getText().toString();
                }
                if (null == keyString) {
                    Toast.makeText(NfcWiFiAssistant.this, "Default KEY, NULL", Toast.LENGTH_LONG)
                            .show();
                    keyString = "qaz123456";
                } else {
                    keyString = mEditText2.getText().toString();
                }
                SimpleWifiInfo simpleWifiInfo = new SimpleWifiInfo(securityStirng, ssidString,
                        keyString);
                Intent intent = new Intent(mContext, WifiConnectConfigWriter.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("SIMPLE_WIFI_INFO", simpleWifiInfo);
                NfcWiFiAssistant.this.startActivityForResult(intent, 100);
            }
        });
    }

    private void initUI() {
        // TODO Auto-generated method stub
        mBtn = (Button)findViewById(R.id.btn);
        mEditText1 = (EditText)findViewById(R.id.et1);
        mEditText2 = (EditText)findViewById(R.id.et3);
        mSpinner = (Spinner)findViewById(R.id.sp2);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerStr);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(new SpinnerXMLSelectedListener());
        mSpinner.setVisibility(View.VISIBLE);
        mSpinner.setSelection(2, true);
        
        mEditText1.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView paramTextView, int paramInt,
                    KeyEvent paramKeyEvent) {
                // TODO Auto-generated method stub
                ssidString = mEditText1.getText().toString();
                Toast.makeText(getApplicationContext(), mEditText1.getText().toString(),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mEditText1.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2,
                    int paramInt3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1,
                    int paramInt2, int paramInt3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable paramEditable) {
                // TODO Auto-generated method stub
                ssidString = mEditText1.getText().toString();
            }
        });
        
        //
        mEditText2.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView paramTextView, int paramInt,
                    KeyEvent paramKeyEvent) {
                // TODO Auto-generated method stub
                keyString = mEditText2.getText().toString();
                Toast.makeText(getApplicationContext(), mEditText2.getText().toString(),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mEditText2.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2,
                    int paramInt3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1,
                    int paramInt2, int paramInt3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable paramEditable) {
                // TODO Auto-generated method stub
                keyString = mEditText2.getText().toString();
            }
        });
    }
    
    class SpinnerXMLSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            LogUtil.i(MyConstant.TAG,
                    TAG_ASSIST + "What you have selected=" + mAdapter.getItem(arg2) + ",position="
                            + arg2);
            switch (arg2) {
                case 0:
                    securityStirng = "none";
                    break;
                case 1:
                    securityStirng = "wep";
                    break;
                case 2:
                    securityStirng = "wpa";
                    break;
                default:
                    securityStirng = "wpa";
                    break;
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100)
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        super.onOptionsItemSelected(item);
        switch (item.getItemId())
        // item clicked
        {
            case R.id.nfc_setting:
                // Intent setnfc = new
                // Intent(Settings.ACTION_WIRELESS_SETTINGS);
                Intent setnfc = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(setnfc);
                break;
            case R.id.wifi_setting:
                Intent setwifi = new Intent(Settings.ACTION_SETTINGS);
                startActivity(setwifi);
                break;
            case R.id.action_about:
                dialog();
                break;
        }
        return true;
    }

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getString(R.string.dialog_notice));
        alertDialog = builder.create();
        builder.setCancelable(true);// back
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }
}

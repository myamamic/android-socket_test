
package myamamic.tp.network.sockettest;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Enumeration;

public class MainActivity extends Activity {
    private static final String TAG = "MYAMA";

    private static final int DEFAULT_SERVER_PORT = 6969;

    TextView mTextView;
    EditText mEditText;
    ServerSocket mServerSocket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView1);
        String str = getDeviceIpAddress(getApplicationContext());
        if (str != null) {
            mTextView.setText(str);
        }

        mEditText = (EditText) findViewById(R.id.editText1);
        mEditText.setText(String.valueOf(DEFAULT_SERVER_PORT));

        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(mOnClickListner);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mServerSocket != null) {
            try {
                Log.e(TAG, "onPause(): close ServerSocket(" + mServerSocket.getLocalPort() + ")");
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mServerSocket = null;
        }
    }

    OnClickListener mOnClickListner = new OnClickListener() {
        @Override
        public void onClick(View v) {
            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    // ソケット作成
                    boolean ret = createServerSocket();
                    return ret;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);
                    String log;
                    if (result) {
                        log = "created ServerSocket(" + mServerSocket.getLocalPort() + ")";
                    } else {
                        log = "failed to create ServerSocket(" + Integer.valueOf(mEditText.getText().toString()) + ")";
                    }
                    Toast toast = Toast.makeText(getApplicationContext(), log, Toast.LENGTH_LONG);
                    toast.show();
                }
            };
            task.execute((Void[])null);
        }
    };

    private boolean createServerSocket() {
        Log.e(TAG, "createServerSocket(): IN");
        if (mServerSocket != null) {
            try {
                Log.e(TAG, "createServerSocket(): close ServerSocket(" + mServerSocket.getLocalPort() + ")");
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mServerSocket = null;
        }

        int serverPort = DEFAULT_SERVER_PORT;
        try {
            serverPort = Integer.valueOf(mEditText.getText().toString());
        } catch (NumberFormatException e) {
            // Use default port
        }
        if ((serverPort < 0)||(65535 < serverPort)) {
            serverPort = DEFAULT_SERVER_PORT;
        }

        boolean ret = false;
        String log;
        try {
            // ポートを指定して、ソケットを作成します。
            // このメソッドは、内部でsocket()->bind()->listen()をしているようです。
            Log.e(TAG, "createServerSocket(): CALL new ServerSocket(" + serverPort + ")");
            mServerSocket = new ServerSocket(serverPort);
            ret = true;

            log = "created ServerSocket(" + mServerSocket.getLocalPort() + ")";
            Log.e(TAG, "createServerSocket(): " + log);
        } catch (IOException e) {
            e.printStackTrace();
            log = "failed to create ServerSocket(" + serverPort + ")";
        }

        Log.e(TAG, "createServerSocket(): OUT");
        return ret;
    }

    private String getDeviceIpAddress(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        int type = ni.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {
            return getWifiIPAddress(context);
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            return getMobileIPAddress();
        } else {
            Log.e(TAG, "getDeviceIpAddress(): Cannot get ip address.");
            return null;
        }
    }

    private static final String LOCAL_LOOPBACK_ADDR = "127.0.0.1";
    private static final String INVALID_ADDR = "0.0.0.0";
    private static String getMobileIPAddress() {
        try {
            NetworkInterface ni = NetworkInterface.getByName("hso0"); // インターフェース名
            if (ni == null) {
                Log.d(TAG, "Failed to get mobile interface.");
                return null;
            }

            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                String address = addresses.nextElement().getHostAddress();
                if (!LOCAL_LOOPBACK_ADDR.equals(address) && !INVALID_ADDR.equals(address)) {
                    // Found valid ip address.
                    return address;
                }
            }
            return null;
        } catch (Exception e) {
            Log.d(TAG, "Exception occured. e=" + e.getMessage());
            return null;
        }
    }

    private static String getWifiIPAddress(Context context) {
        WifiManager manager = (WifiManager)context.getSystemService(WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        int ipAddr = info.getIpAddress();
        String ipString = String.format("%02d.%02d.%02d.%02d",
                (ipAddr>>0)&0xff, (ipAddr>>8)&0xff, (ipAddr>>16)&0xff, (ipAddr>>24)&0xff);
        return ipString;
    }
}

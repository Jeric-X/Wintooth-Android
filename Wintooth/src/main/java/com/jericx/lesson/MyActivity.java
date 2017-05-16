package com.jericx.lesson;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Handler;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
//import java.io.Serializable;


public class MyActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    private static TextView textview;
    private Messenger rMessenger;
    private Messenger mMessenger = new Messenger(new MainHandler());//initial the object of local service
    private Message msg;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Boolean status = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }

    @Override
    protected void onStart(){
        super.onStart();
        textview = (TextView) findViewById(R.id.show_message);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                update();
            }
        };
        mTimer.schedule(mTimerTask, 0, 5000);
    }

    @Override
    protected  void onPause(){
        super.onPause();
        mTimer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_stop) {
            try{
                if(status){
                    unbindService(serConn);
                    Intent intent = new Intent(this, BluetoothBindService.class);
                    stopService(intent);
                }else{
                    Intent intent = new Intent(this, BluetoothBindService.class);
                    startService(intent);
                    bindService(intent, serConn, BIND_AUTO_CREATE);
                }
            }catch(Exception e){
                Log.e("JERICX",e.getMessage());
            }
            update();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_stop);
         if (status) {
             menuItem.setIcon(R.mipmap.ic_not_interested_white_18dp);
         } else {
             menuItem.setIcon(R.mipmap.ic_play_circle_outline_white_18dp);
         }
        return true;
    }

    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_account);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
    public void showMessage(View view) {
        /*
        // Do something in response to button
        uiHandler = new  MainHandler();
        MyThread th = new MyThread(uiHandler);
        th.start();
        */
    }

    private ServiceConnection serConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("JERICX", "onServiceDisconnected()...");
            rMessenger = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
           Log.i("JERICX", "onServiceConnected()...");
            rMessenger = new Messenger(service);//get the object of remote service
            sendMessage2();
        }
    };

    private void sendMessage2() {
        Message msg = Message.obtain(null, BluetoothBindService.TEST);//MessengerService.TEST=0
        msg.replyTo = mMessenger;
        try {
            rMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    static class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                textview.setText(msg.obj.toString());
            } catch (Exception ex) {
                textview.setText(ex.getMessage());
            }
        }
    }

    public void addAccount (View view)
    {
        String pcName,account ,password;
        EditText editText = (EditText) findViewById(R.id.edit_account);
        account = editText.getText().toString();
        editText = (EditText) findViewById(R.id.edit_password);
        password = editText.getText().toString();
        editText = (EditText) findViewById(R.id.edit_name);
        pcName = editText.getText().toString();

        try {
            new AccountWriter(getApplicationContext()).write(pcName, account, password);
        }catch(IOException e){
            Log.e("JERICX",e.toString());
        }
    }

    private boolean update(){
        status = isServiceRunning(getApplicationContext(),"com.jericx.lesson.BluetoothBindService");
        invalidateOptionsMenu();
        msg = Message.obtain();
        if(status)
        {
            msg.obj="服务正在运行";
        }else {
            msg.obj="服务没有运行";
        }
        try {
            mMessenger.send(msg);
        }catch(RemoteException e){
            Log.e("JERICX",e.getMessage());
        }
        return status;
    }
    public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}

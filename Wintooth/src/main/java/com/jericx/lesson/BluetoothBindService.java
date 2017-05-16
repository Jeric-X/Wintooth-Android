package com.jericx.lesson;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public class BluetoothBindService extends Service {

    public final static int TEST = 0;
    public final static int INFOR = 1;
    public final static String BLUETOOTH_UUID = "0F4706E3-0BBE-0189-2219-7D294D6C3545";

    private BluetoothServerSocket mBThServer;
    private List<Account> accounts;
    //It's the messenger of server
    private Messenger cMessenger;
    Message msg;
    private Messenger mMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TEST:
                    Log.e("Event", "Get Message from MainActivity.");
                    cMessenger = msg.replyTo;
                    break;
                default:
                    break;
            }
        }
    });

    @Override
        public void onCreate(){
        super.onCreate();
        accounts = new AccountReader(getApplicationContext()).accounts();
        BluetoothBindService_MainThread th = new BluetoothBindService_MainThread();
        th.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // The service is starting, due to a call to startService()
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mMessenger.getBinder();
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            mBThServer.close();
        }catch(IOException e){
            Log.e("JERICX",e.getMessage());
        }
    }

    private void SendMessage(){
        try{
            cMessenger.send(msg);
        }catch(Exception e){
            Log.e("Event","Lose Binder");
        }
    }

    private class BluetoothBindService_MainThread extends Thread {
        @Override
        public void run(){
                StartBluetooth();
        }
        private void StartBluetooth() {
            BluetoothAdapter MyBthAdt = null;
            msg = Message.obtain();
            msg.obj = "Opening 打开中";
            msg.what = INFOR;
            SendMessage();
            int a = 0;
            while (MyBthAdt == null) {
                MyBthAdt = BluetoothAdapter.getDefaultAdapter();
                a += 1;
                if (a != 1) {
                    try {
                        msg = Message.obtain();
                        msg.obj = "researching";
                        msg.what = INFOR;
                        SendMessage();
                        Thread.sleep(500);
                    } catch (Exception ex) {
                        msg = Message.obtain();
                        msg.obj = "failed";
                        msg.what = INFOR;
                        SendMessage();
                        return;
                    }
                }
                if (a >= 5) {
                    msg = Message.obtain();
                    msg.obj = "failed search";
                    msg.what = INFOR;
                    SendMessage();
                    return;
                }
            }
            MyBthAdt.enable();

            // Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            // discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 150);
            // startActivity(discoverableIntent);


            a = 0;
            while (true) {
                try {
                    a += 1;
                    if(a!=1) {
                        Thread.sleep(500);
                    }

                    mBThServer = MyBthAdt.listenUsingRfcommWithServiceRecord("Server1", UUID.fromString(BLUETOOTH_UUID));
                    // Always cancel discovery because it will slow down a connection
                    MyBthAdt.cancelDiscovery();
                    break;
                    } catch (Exception e) {
                        if (a > 10) {
                            msg = Message.obtain();
                            msg.what = INFOR;
                            msg.obj = "Failed 蓝牙打开失败";
                            SendMessage();
                        return;
                    }
                }
            }

            msg = Message.obtain();
            msg.obj = "Listening";
            msg.what = INFOR;
            SendMessage();
            BluetoothSocket socket;
            EachBluetoothDeviceThread thProcess;
            while(true) {
                try {
                    socket = mBThServer.accept();
                    thProcess = new EachBluetoothDeviceThread(socket);
                    thProcess.start();
                }catch(Exception ex){
                    msg = Message.obtain();
                    msg.obj = ex.getMessage();
                    msg.what = INFOR;
                    SendMessage();
                    return;
                }
            }
        }
    }

    private class EachBluetoothDeviceThread extends Thread {
        private BluetoothSocket socAccept;
        public EachBluetoothDeviceThread( BluetoothSocket soc) {
            this.socAccept = soc;
        }

        @Override
        public void run() {
            msg = Message.obtain();
            msg.obj = "Connecting";
            msg.what = INFOR;
            SendMessage();
            InputStreamReader inputStream;
            try {
                inputStream = new InputStreamReader(this.socAccept.getInputStream());
            } catch (IOException ex) {
                msg = Message.obtain();
                msg.obj = ex.getMessage();
                msg.what = INFOR;
                SendMessage();
                return;
            }
            BufferedReader br = new BufferedReader(inputStream);
            String info;
            while (true) {
                try {
                    info = br.readLine();
                    switch(info){
                        case "1":

                            info = br.readLine();
                            Account tempAccount;
                            for(int i = 0; i<accounts.size(); i++){
                                tempAccount = accounts.get(i);
                                if(tempAccount.pcName.equals(info)){
                                    OutputStream os = socAccept.getOutputStream();
                                    String Account = tempAccount.account;
                                    String Password = tempAccount.password;
                                    int lengthA = Account.length();
                                    int lengthB = Password.length();
                                    os.write(lengthA);
                                    os.flush();
                                    os.write(lengthB);
                                    os.flush();
                                    os.write((Account).getBytes());
                                    os.flush();
                                    os.write((Password).getBytes());
                                    os.flush();
                                    os.close();
                                    break;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                } catch (IOException ex) {
                    msg = Message.obtain();
                    msg.obj = "Lost 已丢失";
                    msg.what = INFOR;
                    SendMessage();
                    break;
                }
            }
        }
    }
}

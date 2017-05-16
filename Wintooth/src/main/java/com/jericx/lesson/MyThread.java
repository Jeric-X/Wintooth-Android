package com.jericx.lesson;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.UUID;

/**
 * Created by ???? on 2015/7/16.
 */

public class MyThread extends Thread{
    private Handler uiHandler;
    private BluetoothServerSocket mBThServer;
    private Message msg;
    public  MyThread( Handler uiHandler){
        this.uiHandler = uiHandler;
    }

    @Override
    public void run(){
        StartBluetooth();
    }
    private void StartBluetooth (){
        BluetoothAdapter mybthadt = null;
        msg = Message.obtain();
        msg.obj = "researching ???????????????";
        uiHandler.sendMessage(msg);
        int a = 0;
        while(mybthadt==null){
            mybthadt = BluetoothAdapter.getDefaultAdapter();
            a+=1;
            if(a != 1){
                try {
                    msg = Message.obtain();
                    msg.obj = "researching";
                    uiHandler.sendMessage(msg);
                    Thread.sleep(500);
                }catch(Exception ex){
                    msg = Message.obtain();
                    msg.obj = "failed";
                    uiHandler.sendMessage(msg);
                    return;
                }
            }
            if(a>=5){
                msg = Message.obtain();
                msg.obj = "failed search";
                uiHandler.sendMessage(msg);
                return;
            }
        }

        msg = Message.obtain();
        msg.obj = "open ??????";
        uiHandler.sendMessage(msg);
        mybthadt.enable();

        msg = Message.obtain();
        msg.obj = "create ????????";
        uiHandler.sendMessage(msg);
        // Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        // discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 150);
        // startActivity(discoverableIntent);

        // Always cancel discovery because it will slow down a connection
        mybthadt.cancelDiscovery();

        try {
            mBThServer = mybthadt.listenUsingRfcommWithServiceRecord("Server1", UUID.fromString("0F4706E3-0BBE-0189-2219-7D294D6C3545"));
        }catch(IOException e)  {
            msg = Message.obtain();
            msg.obj = "Failed create server";
            uiHandler.sendMessage(msg);
        }

            msg = Message.obtain();
            msg.obj = "Listening";
            uiHandler.sendMessage(msg);
            BluetoothSocket socket;
            HandleBluetoothThread thProcess;
            while(true) {
                try {
                    socket = mBThServer.accept();
                    thProcess = new HandleBluetoothThread(new Messenger(uiHandler), socket);
                    thProcess.start();
                }catch(Exception ex){
                    msg = Message.obtain();
                    msg.obj = ex.getMessage();
                    uiHandler.sendMessage(msg);
                    return;
                }
            }
    }



}


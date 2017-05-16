package com.jericx.lesson;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Èô³¿ on 2015/8/5.
 */
public class HandleBluetoothThread extends Thread {
    private Messenger cMessenger;
    private BluetoothSocket socAccept;
    public HandleBluetoothThread(Messenger cMessenger, BluetoothSocket soc) {
        this.cMessenger = cMessenger;
        this.socAccept = soc;
    }

    @Override
    public void run() {

        try {
            Message msg;
            msg = Message.obtain();
            msg.obj = "Connecting";
            msg.what = BluetoothBindService.INFOR;
            cMessenger.send(msg);
            InputStreamReader inputStream;
            try {
                inputStream = new InputStreamReader(this.socAccept.getInputStream());
            } catch (IOException ex) {
                msg = Message.obtain();
                msg.obj = ex.getMessage();
                msg.what = BluetoothBindService.INFOR;
                cMessenger.send(msg);
                return;
            }
            BufferedReader br = new BufferedReader(inputStream);
            String info;
            while (true) {
                try {
                    info = br.readLine();
                    msg = Message.obtain();
                    msg.obj = info;
                    msg.what = BluetoothBindService.INFOR;
                    cMessenger.send(msg);
                } catch (IOException ex) {
                    msg = Message.obtain();
                    msg.obj = "Lost ÒÑ¶ªÊ§";
                    msg.what = BluetoothBindService.INFOR;
                    cMessenger.send(msg);
                    break;
                }
            }

        }catch(RemoteException ex){

        }
    }
}
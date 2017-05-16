package com.jericx.lesson;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by 若晨 on 2015/9/2.
 */
public class AccountWriter {
    Context context;
    public AccountWriter(Context context){
       this.context = context;
    }
    public void write(String pcName,String account,String password) throws IOException{
        if((account.equals("")||password.equals("")||pcName.equals(""))){
            Toast toast=Toast.makeText(context, "有项目为空", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 300);
            toast.show();
        }else if(new AccountReader(context).ifExist(pcName,account,password)){
            Toast toast=Toast.makeText(context, "已存在", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 300);
            toast.show();
        }else{
            BufferedWriter bufferedWriter = null;
            OutputStream outputStream;
            try{
                outputStream = context.openFileOutput("data", Context.MODE_APPEND) ;
            }catch(FileNotFoundException e){
                Toast toast=Toast.makeText(context, "添加失败", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 300);
                toast.show();
                return;
            }
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(pcName);
            bufferedWriter.newLine();
            bufferedWriter.write(account);
            bufferedWriter.newLine();
            bufferedWriter.write(password);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStreamWriter.close();
            outputStream.close();
            Toast toast=Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 300);
            toast.show();
        }
    }
}

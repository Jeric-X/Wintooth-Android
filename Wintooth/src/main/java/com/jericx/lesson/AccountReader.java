package com.jericx.lesson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;


/**
 * Created by »Ù≥ø on 2015/9/2.
 */
public class AccountReader {
    private List<Account> accounts;
    public AccountReader(Context context){
        try {
            readFile(context);
        }catch (IOException e){
            Log.e("JERICX","IOException");
        }
    }
    private void readFile(Context context) throws IOException{
        accounts = new ArrayList<Account>();

        BufferedReader reader = null;
        InputStream inputStream = context.openFileInput("data") ;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        reader = new BufferedReader(inputStreamReader);

        String tempPcname,tempAccount,tempPassword;
        try {
            while ((tempPcname = reader.readLine()) != null) {
                tempAccount = reader.readLine();
                tempPassword = reader.readLine();
                accounts.add(new Account(tempPcname,tempAccount,tempPassword));
            }
        }catch (IOException e){
            reader.close();
            inputStream.close();
        }
    }

    public List<Account> accounts(){
        return accounts;
    }

    public ArrayList<Map<String,String>> pcNameAndAccounts(){
        ArrayList<Map<String,String>> pcNameAndAccounts = new ArrayList<>();
        Account tempAccount;
        for(int i = 0; i<accounts.size(); i++){
            tempAccount = accounts.get(i);
            Map<String,String> map = new HashMap<>();
            map.put("ItemTitle",tempAccount.pcName);
            map.put("ItemText",tempAccount.account);
            pcNameAndAccounts.add(map);
        }
        return pcNameAndAccounts;
    }

    public boolean ifExist(String pcName,String account,String password){
        Account tempAccount;
        for(int i = 0; i<accounts.size(); i++){
            tempAccount = accounts.get(i);
            if(tempAccount.pcName.equals(pcName)/*&&tempAccount.account.equals(account)*/){
                return true;
            }
        }
        return false;
    }
}

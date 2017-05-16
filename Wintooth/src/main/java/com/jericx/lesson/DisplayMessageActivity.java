package com.jericx.lesson;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DisplayMessageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        /*
        Intent intent = getIntent();
        String message = intent.getStringExtra(MyActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);
        setContentView(textView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        */
    }

    @Override
    protected void onStart(){
        super.onStart();

        /*
        List<Account> accounts = new AccountReader(getApplicationContext()).accounts();
        TextView textView;
        Account account;
        for(int i = 0; i<accounts.size(); i++){
            account = accounts.get(i);
            textView = new TextView(this);
            textView.setTextSize(20);
            textView.setText(account.pcName);
            setContentView(textView);
            textView = new TextView(this);
            textView.setTextSize(20);
            textView.setText(account.account);
            setContentView(textView);
        }
        if(accounts.size()==0){
            textView = new TextView(this);
            textView.setTextSize(20);
            textView.setText("没有记录");
            textView.setGravity(Gravity.CENTER);
            setContentView(textView);
        }
        */

        Log.e("JERICX","到这了");
        ListView list = (ListView) findViewById(R.id.MyListView);
        ArrayList<Map<String,String>> pcNameAndAccounts =
                new AccountReader(getApplicationContext()).pcNameAndAccounts();
        SimpleAdapter mSchedule = new SimpleAdapter(this,                                 //没什么解释
                pcNameAndAccounts,                                                        //数据来源
                R.xml.my_listitem,                                                      //ListItem的XML实现
                new String[] {"ItemTitle", "ItemText"},                                 //动态数组与ListItem对应的子项
                new int[] {R.id.ItemTitle,R.id.ItemText});                                 //ListItem的XML文件里面的两个TextView ID

        list.setAdapter(mSchedule);
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_message, menu);
        return true;
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

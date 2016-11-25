package com.yj.chatting.chattingsample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.yj.chatting.chattingmodule.ChatEndPoint;
import com.yj.chatting.chattingmodule.ServerConnectListner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView m_rvContacts;
    private RecyclerViewDemoAdapter m_adpContacts;
    private ArrayList<Contact> m_lstContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.activity_title_chatting_list);
        setSupportActionBar(toolbar);

        m_rvContacts = (RecyclerView)findViewById(R.id.rv_contacts);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // actually VERTICAL is the default,
        // just remember: LinearLayoutManager
        // supports HORIZONTAL layout out of the box
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // you can set the first visible item like this:
        layoutManager.scrollToPosition(0);
        m_rvContacts.setLayoutManager(layoutManager);

        m_adpContacts = new RecyclerViewDemoAdapter(m_lstContacts);
        m_rvContacts.setAdapter(m_adpContacts);

        getContactsList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void getContactsList() {
        Contact contact = new Contact();
        contact.id = "test2";
        contact.name = "test2";
        contact.pwd = "test2";

        m_lstContacts.add(contact);

        m_adpContacts.notifyDataSetChanged();
    }

    ChatEndPoint mChatEndPoint = null;

    private void test() {

        mChatEndPoint = new ChatEndPoint(this);

        connectTest();
    }

    private  void  connectTest() {
        mChatEndPoint.connect("192.168.0.11", "conference", 5222, new ServerConnectListner() {
            @Override
            public void onSuccess() {
                Log.d("connect", "success");
            }

            @Override
            public void onFailed(Exception e) {
                Log.d("connect", "fail");
            }
        });
    }
}

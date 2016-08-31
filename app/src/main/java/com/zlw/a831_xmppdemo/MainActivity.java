package com.zlw.a831_xmppdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_username, et_password, et_msg, et_friendID;
    private Button bt_link, bt_register, bt_login, bt_friend, bt_sendtext;

    //XMPP相关
    private XMPPTCPConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        et_username = (EditText) findViewById(R.id.username);
        et_password = (EditText) findViewById(R.id.password);
        bt_link = (Button) findViewById(R.id.bt_link);
        bt_register = (Button) findViewById(R.id.bt_register);
        bt_login = (Button) findViewById(R.id.bt_login);
        bt_friend = (Button) findViewById(R.id.bt_friend);
        bt_sendtext = (Button) findViewById(R.id.bt_sendtext);
        et_msg = (EditText) findViewById(R.id.et_msg);
        et_friendID = (EditText) findViewById(R.id.et_friendID);

        bt_link.setOnClickListener(this);
        bt_register.setOnClickListener(this);
        bt_login.setOnClickListener(this);
        bt_friend.setOnClickListener(this);
        bt_sendtext.setOnClickListener(this);
        et_msg.setOnClickListener(this);
        et_friendID.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        final String username = et_username.getText().toString();
        final String password = et_password.getText().toString();
        switch (v.getId()) {
            case R.id.bt_link:
                Log.i("zlw", "开始链接");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connection = getConnection();
                        try {
                            connection.connect();
                        } catch (SmackException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;

            case R.id.bt_register:
                if (username.equals("") || password.equals("")) {
                    Toast.makeText(MainActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }
                //实现注册
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (connection.isConnected()) {
                                connection.disconnect();
                            }
                            connection.connect();
                            AccountManager accountManager = AccountManager.getInstance(connection);
                            if (accountManager.supportsAccountCreation()) {
                                Map<String, String> map = new Hashtable<>();
                                map.put("email", "demo@qq.com");
                                map.put("android", "createUser_android");
                                accountManager.createAccount(username, password);
                            }
                        } catch (SmackException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;

            case R.id.bt_login:
                if (username.equals("") || password.equals("")) {
                    Toast.makeText(MainActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }
                //实现注册
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (connection.isConnected()) {
                                connection.disconnect();
                            }
                            connection.connect();
                            connection.login(username, password);
                            Presence presence = new Presence(Presence.Type.available);
                            presence.setStatus("我在线");
                            connection.sendStanza(presence); // 修改状态
                        } catch (SmackException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;

            case R.id.bt_friend:
                String friendId = et_friendID.getText().toString();
                if ("".equals(friendId)) {
                    Toast.makeText(MainActivity.this, "friendID不能为空", Toast.LENGTH_SHORT).show();
                }

                Roster roster = Roster.getInstanceFor(connection);
                try {
                    //添加好友
                    roster.createEntry(friendId, friendId, null);
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }


                break;
            case R.id.bt_sendtext:
                String msg = et_msg.getText().toString();
                String FriendID = et_friendID.getText().toString();
                if ("".equals(msg)) {
                    Toast.makeText(MainActivity.this, "msg不能为空", Toast.LENGTH_SHORT).show();
                }

                ChatManager chatManager = ChatManager.getInstanceFor(connection);
                Chat mChat = chatManager.createChat(FriendID);
                try {
                    mChat.sendMessage(msg);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                break;
            default:

                break;


        }
    }

    public XMPPTCPConnection getConnection() {

        String server = "192.168.56.1";
        int port = 5222;
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setHost(server);
        builder.setPort(port);
        builder.setServiceName(""); //使用域名  ， 此处使用计算机名
        builder.setCompressionEnabled(false); //是否压缩
        builder.setDebuggerEnabled(true);  //是否显示通讯日志
        builder.setSendPresence(true);

        //登陆安全认证：SASL安全机制
        SASLAuthentication.blacklistSASLMechanism("DIGSET-MD5");
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        XMPPTCPConnection connection = new XMPPTCPConnection(builder.build());

        return connection;
    }
}

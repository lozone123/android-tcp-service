package com.example.tcpservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TcpServiceConnection conn;
    TcpService.TcpBinder binder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent =new Intent(this,TcpService.class);
        conn=new TcpServiceConnection();
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //解绑服务，避免泄露内存
        unbindService(conn);
    }

    public class TcpServiceConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder=(TcpService.TcpBinder)service;
            binder.getService().setDataCallBack(new TcpService.DataCallback() {
                @Override
                public void dataChange(String str) {
                    Message message=new Message();
                    message.obj=str;
                    handler.sendMessage(message);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder=null;
        }
    }

    Handler handler=new Handler(){
         public void handleMessage(android.os.Message msg) {
             String data=msg.obj.toString();
             Toast.makeText(MainActivity.this,data,Toast.LENGTH_LONG).show();
             Log.d("MainActivity Receive:",data);
         }
    };
}

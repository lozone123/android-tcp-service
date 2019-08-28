package com.example.tcpservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpService extends Service {

    final static int PORT=7788;
    final static String HOST="10.0.2.2";

    static Socket socket;
    static InputStream in;
    static OutputStream out;

    @Override
    public IBinder onBind(Intent intent) {
        return new TcpBinder();
    }

    public class TcpBinder extends Binder {
        TcpService getService() {
            return TcpService.this;
        }
    }

    @Override
    public void onCreate() {
        initSocket();
        setHeartBeat();
        receiveData();
        super.onCreate();
    }

    synchronized public void initSocket(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(socket==null){
                        socket=new Socket(HOST,PORT);
                        in=socket.getInputStream();
                        out=socket.getOutputStream();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void setHeartBeat(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(;;){
                    if(out!=null){
                        try {
                            out.write(0);
                            out.flush();
                        } catch (IOException e) {
                            //出现异常可能是服务器关闭了或出现其它问题，重新连接
                            try {
                                socket.close();
                                out.close();
                                in.close();
                                socket=null;
                                out=null;
                                in=null;
                                initSocket();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            e.printStackTrace();
                        }
                    }else{
                        initSocket();
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void receiveData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(;;){
                    try {
                        if(in!=null){
                            int readCount=0;
                            byte buffer[]=new byte[1024*4];
                            StringBuilder stringBuilder=new StringBuilder();
                            try{
                                //如果服务器发送频繁的话，这里会一直接受数据，就会出现死循环
//                                while((readCount=in.read(buffer))!=-1 && in!=null){
//                                    String str=new String(buffer,0,readCount);
//                                    stringBuilder.append(str);
//                                }
                                if((readCount=in.read(buffer))!=-1){
                                    String str=new String(buffer,0,readCount);
                                    stringBuilder.append(str);
                                }
                            }catch(IOException e){
                            }
                            if(!stringBuilder.toString().isEmpty() && dataCallback!=null){
                                dataCallback.dataChange(stringBuilder.toString());
                            }
                        }
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    DataCallback dataCallback = null;
    public void setDataCallBack(DataCallback dataCallback){
        this.dataCallback=dataCallback;
    }
    public interface DataCallback{
        void dataChange(String str);
    }
}

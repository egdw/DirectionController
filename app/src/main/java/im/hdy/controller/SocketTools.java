package im.hdy.controller;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketTools {
    private String ip;
    private int code;
    private Socket socket;
    private OutputStream outputStream;
    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.i("handler","");

            send(msg.what);
        }
    };

    public SocketTools(String ip, int code) {
        this.ip = ip;
        this.code = code;

        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("socketinfo","init");

    }

    public Handler getHandler(){
        return messageHandler;
    }

    private void init() throws IOException {
        socket = new Socket(ip,code);
        outputStream = socket.getOutputStream();
    }


    public void send(int type){
        Log.i("socketSend",type+"");
        try {
            outputStream.write(type);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void destory(){
        if (socket != null){
            try {
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

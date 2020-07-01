package com.sate7.wlj.developerreader.lib_test_java.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class ReadHelper extends Thread{
    public interface OnMessageReceivedListener{
        void onMessageReceived(String msg);
    };
    private OnMessageReceivedListener listener;
    //注意如果使用BufferedReader有个小坑；
    public static final boolean USE_BUFFERED_READER = true;
    private BufferedReader reader;
    private InputStream inputStream;
    public ReadHelper(InputStream inputStream){
        if(USE_BUFFERED_READER){
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }else{
            this.inputStream = inputStream;
        }
    }

    public void setMessageReceivedListener(OnMessageReceivedListener listener){
        this.listener = listener;
    }

    @Override
    public void run() {
        while (true){
            if(USE_BUFFERED_READER){
                String msg;
                try {
                    if((msg = reader.readLine()) != null && listener!= null){
                        listener.onMessageReceived(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                byte[] bytes = new byte[1024];
                int length = -1;
                try {
                    if((length = inputStream.read(bytes))>1){
                        listener.onMessageReceived(new String(bytes,0,length));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("ReadHelper IOException ..." + e);
                }
            }
        }
    }
    public void close(){
        if(reader != null){
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(inputStream != null){
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

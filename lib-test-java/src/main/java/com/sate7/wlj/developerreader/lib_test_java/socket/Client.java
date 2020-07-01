package com.sate7.wlj.developerreader.lib_test_java.socket;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

class Client implements ISocket, ReadHelper.OnMessageReceivedListener {
    private Socket socket;
    private ReadHelper readHelper;
    private BufferedWriter writer;
    public Client(int port){
        this("localhost",port);
    }
    public Client(String ip,int port){
        try {
            socket = new Socket(ip,port);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            readHelper = new ReadHelper(socket.getInputStream());
            readHelper.start();
            readHelper.setMessageReceivedListener(this);
            System.out.println("客户端连接成功 " + Util.getTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(String msg) {
       out(msg);
    }

    @Override
    public void close() {
        try {
            socket.close();
            readHelper.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMsg(String msg) {
        Util.debug("client send msg ..." + msg);
        try {
            writer.write(ReadHelper.USE_BUFFERED_READER ? msg + "\r": msg);
            writer.flush();//坑爹
        } catch (IOException e) {
            e.printStackTrace();
            Util.debug("client send msg IOException ..." + e.getMessage());
        }
    }

    private void out(String msg){
        System.out.println("[Client receive] " + msg + " AT " +  Util.getTime());
    }
    public static void main(String[] args) {
        Client client = new Client(Server.PORT);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String msg = scanner.nextLine();
            if(msg.equals("exit")){
                client.close();
                break;
            }else{
                client.sendMsg(msg);
            }
        }
        System.out.println("client exit ...");
    }
}

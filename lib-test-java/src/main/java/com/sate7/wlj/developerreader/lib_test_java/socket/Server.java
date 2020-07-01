package com.sate7.wlj.developerreader.lib_test_java.socket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Server implements ISocket, ReadHelper.OnMessageReceivedListener {
    public static final int PORT = 6789;
    private ExecutorService service = Executors.newCachedThreadPool();
    private BufferedWriter writer;
    public Server(){
    }
    public void start(){
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket socket = serverSocket.accept();
            System.out.println("服务端和客户端建立了连接....");
            ReadHelper readHelper = new ReadHelper(socket.getInputStream());
            readHelper.start();
            readHelper.setMessageReceivedListener(this);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(String msg) {
        Util.printLogWithTime("[Server receive]" + msg);
    }

    @Override
    public void sendMsg(String msg) {
        Util.debug("Server sendMsg ... " + msg);
        try {
            writer.write(ReadHelper.USE_BUFFERED_READER ? msg + "\r": msg);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Util.debug("Server sendMsg IOException ... " + e.getMessage());
        }
    }

    @Override
    public void close() {

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
        System.out.println("---------------SocketServer(" + Server.PORT + ")start---------------");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String msg = scanner.nextLine();
            if(msg.equals("exit")){
                break;
            }
            server.sendMsg(msg);
        }
        System.out.println("end ... ");
    }

}

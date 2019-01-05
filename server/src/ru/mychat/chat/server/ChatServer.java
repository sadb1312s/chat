package ru.mychat.chat.server;

import ru.mychat.network.TCPConnection;
import ru.mychat.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {
    public static void main(String[] args){
        new ChatServer();
    }

    private ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer(){
        System.out.println("Server running...");

        try(ServerSocket serverSocket = new ServerSocket(8199)){
            while (true){
                try {
                    new TCPConnection(this, serverSocket.accept());
                }catch (IOException e){
                    System.out.println("TCPConnection exception: "+e);
                }
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        System.out.println("client connected "+tcpConnection);
        sendToAll("client connected "+tcpConnection);

    }

    @Override
    public synchronized void onRecieveReady(TCPConnection tcpConnection, String str) {
        sendToAll(str);
    }

    @Override
    public synchronized void onDisconect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAll("client disconnected "+tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCP connection: "+e);
    }

    private void sendToAll(String str){
        System.out.println(str);

        for (int i=0;i<connections.size();i++)
            connections.get(i).sendString(str);



    }
}

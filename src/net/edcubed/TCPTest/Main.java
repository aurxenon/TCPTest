package net.edcubed.TCPTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Main {

    private static String serverIP = "localhost";
    private static int tcpPort = 26655;
    public static Socket tcpSocket;

    public static ObjectInputStream objectInput;
    public static ObjectOutputStream objectOutput;

    public static void sendTCPData(Object message) {
        try {
            objectOutput.writeObject(message);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void update() {
        new Thread(new Runnable() {
            public void run() {
                Object message = "uninitialized";
                while (true) {
                    try {
                        message = objectInput.readObject();
                        System.out.println(message);
                    }catch(IOException e){
                        e.printStackTrace();
                        closeSocket(tcpSocket);
                        return;
                    }catch(ClassNotFoundException e) {
                        e.printStackTrace();
                        closeSocket(tcpSocket);
                        return;
                    }
                }
            }
        }).start();

        //keep alive thread thread
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(15000);
                        sendTCPData("i'm still here please don't leave me");
                    }catch(InterruptedException e){
                        e.printStackTrace();
                        closeSocket(tcpSocket);
                        return;
                    }
                }
            }
        }).start();
    }

    public static void closeSocket(Socket skt) {
        try {
            skt.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main (String[] args) {
        try {
            tcpSocket = new Socket(serverIP,tcpPort);
            objectOutput = new ObjectOutputStream(tcpSocket.getOutputStream());
            objectInput = new ObjectInputStream(tcpSocket.getInputStream());

            sendTCPData("hello");

            //updates
            update();
        }catch(IOException e){
            e.printStackTrace();
            closeSocket(tcpSocket);
            return;
        }
    }
}

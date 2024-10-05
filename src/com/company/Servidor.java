package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class Servidor {
    private ServerSocket socket;
    private BaseDados dados;
    private ReentrantLock lock;

    public Servidor() throws IOException, ClassNotFoundException {
        this.socket= new ServerSocket(12345);
        deSerialize();
        this.lock= new ReentrantLock();
    }



    public void run() throws IOException {
        while(true){
            Socket s= this.socket.accept();
            Thread worker= new Thread(new ServerWorker(s, this.dados, this.lock));
            worker.start();
        }
        //serialize();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Servidor s= new Servidor();
        s.run();
    }

    public void serialize(){
        try {
            FileOutputStream fileOut = new FileOutputStream("baseDados.bd");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this.dados);
            objectOut.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deSerialize(){
        try {
            FileInputStream fileIn = new FileInputStream("baseDados.bd");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();

            objectIn.close();
            this.dados = (BaseDados) obj;
        }
        catch(Exception e){
            this.dados= new BaseDados();
        }
    }


}

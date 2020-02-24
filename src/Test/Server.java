package Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

class Server implements Runnable{
    private Queue<DatagramPacket> packetQueue;
    public DatagramSocket socket;
    private boolean running;

    public Server(Queue<DatagramPacket> packetQueue) {
        this.packetQueue = packetQueue;
        try {
            socket = new DatagramSocket(4445);
            socket.setSoTimeout(30000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        System.out.println("Server is running");
        running = true;
        while (running) {
                try {
                    byte[] buf = new byte[100];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    packetQueue.offer(packet);
                } catch (IOException e) {
                    running = false;
                    System.out.println("Server is done waiting for packets");
                    while (!packetQueue.isEmpty()){

                    }
                    System.out.println("Queue should be empty, closing thread");
                    return;
                }
            }
        }

        //while (running) {
          //  DatagramPacket packet = new DatagramPacket(buf, buf.length);
            //socket.receive(packet);
            //receivedCounter.getAndSet(new Double((double) (receivedCounter.get() + 1)));
            /*InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            */
            //String received = new String(packet.getData(), 0, packet.getLength());
            //received = received.trim();
            //if (received.equals("end")) {
              //  running = false;
                //byte[] buf2 = "end".getBytes();
                //packet = new DatagramPacket(buf2, buf2.length, address, port);
                //socket.send(packet);
                //continue;
            //}
            //socket.send(packet);

        //calculateLoss(receivedCounter);
        //socket.close();


    public void sendPacket(DatagramPacket packet){
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receivePacket(){

    }

}

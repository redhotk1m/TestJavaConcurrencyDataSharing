package Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class PacketHandler implements Runnable {

    Queue<DatagramPacket> packetQueue;
    AtomicInteger packetsHandled;
    Thread serverThread;
    Server server;
    byte[] buf = new byte[30];

    PacketHandler(Queue<DatagramPacket> packetQueue, AtomicInteger packetsHandled, Thread serverThread, Server server) {
        this.packetQueue = packetQueue;
        this.packetsHandled = packetsHandled;
        this.serverThread = serverThread;
        this.server = server;
    }

    @Override
    public void run() {
        Random rand = new Random();
        while (serverThread.isAlive()) {
            DatagramPacket packet = packetQueue.poll();
            //packet = packetQueue.poll();
            if (packet != null) {
                //String a = new String(packet.getData());
                Packet receivedPacket = new Packet(packet.getData());
                receivedPacket.type="SYN-ACK";
                DatagramPacket respPacket = new DatagramPacket(receivedPacket.getData(),receivedPacket.getData().length,packet.getAddress(),packet.getPort());
                //DatagramPacket responsePacket = new DatagramPacket(a.getBytes(),a.getBytes().length,packet.getAddress(),packet.getPort());
                if (receivedPacket.name.equals("resent"))
                    ServerMain.resentPackets.incrementAndGet();
                if (rand.nextInt(50)<40)
                    server.sendPacket(respPacket);
                System.out.println(packetsHandled.incrementAndGet());
                //System.out.println("I am handling packet: " + packetsHandled.incrementAndGet() + " " + Thread.currentThread().getName() + " data: " + a);
            }  //Thread.sleep(1000);
            //calculateLoss(packetsHandled);
        }
        System.out.println("Serverthread is dead, calculating now");
        System.out.println("Received a total of " + packetsHandled + " and resent: " + ServerMain.resentPackets);
        //calculateLoss(packetsHandled);
    }

    private void calculateLoss(AtomicInteger packetsReceiveds){
        int packetReceived = packetsReceiveds.get();
        double expectedPackets = 1000000;
        double lostPackets = expectedPackets - packetReceived;
        double lostPercentage = (lostPackets/expectedPackets)*100;
        System.out.println("Lost packets: " + lostPackets + " which is a total loss of " + lostPercentage + "%");
    }
}

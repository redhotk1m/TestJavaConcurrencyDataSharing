import java.net.DatagramPacket;
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
        int s = 0;
        int k = 0;
        long startTime = 0;
        while (serverThread.isAlive()) {
            DatagramPacket packet = packetQueue.poll();
            if (packet != null) {
                //if (s<1){
                //    s++;
                //    startTime = System.currentTimeMillis();
               // }
                Packet receivedPacket = new Packet(packet.getData());
                receivedPacket.type="SYN-ACK";
                DatagramPacket respPacket = new DatagramPacket(receivedPacket.getData(),receivedPacket.getData().length,packet.getAddress(),packet.getPort());
                if (receivedPacket.name.equals("resent"))
                    ServerMain.resentPackets.incrementAndGet();
                if (rand.nextInt(50)<25) {
                    server.sendPacket(respPacket);
                    System.out.println("Resent: " + ++k);
                }
                System.out.println("Received: " + packetsHandled.incrementAndGet());
                //if (packetsHandled.get() > 89999) {
                //    System.out.println(System.currentTimeMillis() - startTime);
                //}

            }
        }
        System.out.println("Serverthread is dead, calculating now");
        System.out.println("Received a total of " + packetsHandled + " and resent: " + ServerMain.resentPackets);
    }

    private void calculateLoss(AtomicInteger packetsReceiveds){
        int packetReceived = packetsReceiveds.get();
        double expectedPackets = 1000000;
        double lostPackets = expectedPackets - packetReceived;
        double lostPercentage = (lostPackets/expectedPackets)*100;
        System.out.println("Lost packets: " + lostPackets + " which is a total loss of " + lostPercentage + "%");
    }
}

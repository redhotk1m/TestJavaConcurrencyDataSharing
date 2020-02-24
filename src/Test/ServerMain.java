package Test;

import java.net.DatagramPacket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerMain {
    public static AtomicInteger resentPackets = new AtomicInteger(0);
    public static void main(String[] args) {

        Queue<DatagramPacket> packetQueue = new ConcurrentLinkedQueue<>();
        AtomicInteger packetsHandled = new AtomicInteger(0);
        Server server = new Server(packetQueue);

        Thread serverThread = new Thread(server);

        serverThread.start();
        startPacketHandlers(packetQueue, packetsHandled,serverThread,server);


    }

    private static void startPacketHandlers(Queue<DatagramPacket> packetQueue, AtomicInteger packetsHandled, Thread serverThread,Server server) {

        PacketHandler packetHandler = new PacketHandler(packetQueue,packetsHandled,serverThread,server);
        PacketHandler packetHandler1 = new PacketHandler(packetQueue,packetsHandled,serverThread,server);
        PacketHandler packetHandler2 = new PacketHandler(packetQueue,packetsHandled,serverThread,server);
        PacketHandler packetHandler3 = new PacketHandler(packetQueue,packetsHandled,serverThread,server);

        Thread packetHandlerThread = new Thread(packetHandler);
        Thread packetHandlerThread1 = new Thread(packetHandler1);
        Thread packetHandlerThread2 = new Thread(packetHandler2);
        Thread packetHandlerThread3 = new Thread(packetHandler3);

        packetHandlerThread.start();
        packetHandlerThread1.start();
        packetHandlerThread2.start();
        packetHandlerThread3.start();
    }


}

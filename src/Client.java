import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Client {

    private DatagramSocket datagramSocket;
    private int resendDelay = 5000;
    String hei = "hei123";
    private byte[] buf = hei.getBytes();
    private byte[] buf2 = new byte[256];
    int receiveds = 0;
    BlockingQueue<WaitForResponse> DQ = new DelayQueue<WaitForResponse>();
    ConcurrentHashMap<String, Packet> CHM = new ConcurrentHashMap<>();
    DelayQueue<Packet> packet2BlockingQueue = new DelayQueue<>();
    AtomicLong startTime = new AtomicLong(0);
    String ID;
    int SeqNr = 1;
    private ScheduledExecutorService executorService;


    Client(String ID){
        this.ID = ID;
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        //executorService = Executors.newSingleThreadScheduledExecutor();
        //ScheduledExecutorService executorService2 = Executors.newSingleThreadScheduledExecutor();
        //executorService.scheduleAtFixedRate(this::sendAllPackets, 0, 33, TimeUnit.MILLISECONDS);
        //executorService.scheduleAtFixedRate(this::resendUnreceivedPackets3, 0, 10, TimeUnit.MICROSECONDS);

        //Sends all SYN packets
        new Thread(this::sendAllPackets).start();
        //Receives all SYN-ACKs
        new Thread(this::recievePacketTest).start();

        //Handles all received packets, removes them from HashMap with all packets waiting for be handled.
        //new Thread(this::packetLookup2).start();

        //Resends all packets which no SYN-ACK was received for
        new Thread(this::resendUnreceivedPackets4).start();

    }

    private synchronized void sendPacket(Packet packet){
        DatagramPacket packet3 = new DatagramPacket(packet.getData(), packet.getData().length, packet.address, packet.port);
        try {
            datagramSocket.send(packet3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAllPackets(){
        //if (SeqNr >= 1000)
            //executorService.shutdownNow();
        try {
            System.out.println("Sent: " + SeqNr);
            InetAddress addressI = InetAddress.getByName("37.191.218.118");
            InetAddress address = InetAddress.getByName("localhost");

            for (int i = 0; i<100000;i++) {
            //    if (i < 1)
            //        startTime.set(System.currentTimeMillis());
                String count = Integer.toString(++SeqNr);
                Packet packetMain2 = new Packet("Kim",count,"SYN",resendDelay);
                packetMain2.port = 4445;
                packetMain2.address = address;

                DatagramPacket packet3 = new DatagramPacket(packetMain2.getData(), packetMain2.getData().length, address, 4445);
                CHM.put(packetMain2.SeqNr,packetMain2);
                datagramSocket.send(packet3);
                //packet2BlockingQueue.add(packetMain2);
                //packetQueue123.add(packetMain2);
                //sendPacket(packetMain2);
            //Executors.newSingleThreadScheduledExecutor().schedule(this::resendUnreceivedPackets3,10,TimeUnit.MILLISECONDS);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void resendUnreceivedPackets3(){
            Packet resendPacket;
            while (true) {
                try {
                    resendPacket = packet2BlockingQueue.take();
                    resendPacket.name = "resent";
                    DatagramPacket resendDatagramPacket = new DatagramPacket(resendPacket.getData(), resendPacket.getData().length, resendPacket.address, resendPacket.port);
                    if (CHM.get(resendPacket.SeqNr) != null) {
                        //sendPacket(resendPacket);
                        datagramSocket.send(resendDatagramPacket);
                        resendPacket.delayTime = resendDelay + System.currentTimeMillis();
                        packet2BlockingQueue.add(resendPacket);
                        //System.out.println("PACKET RESENT!");
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }
    private Queue<Packet> packetQueue123 = new ConcurrentLinkedQueue<>();
    private void resendUnreceivedPackets5(){
            Packet resendPacket;
            try {
                resendPacket = packetQueue123.poll();
                if (resendPacket == null) {
                    System.out.println("Ingen pakke i køen?");
                    return;
                }
                resendPacket.name = "resent";
                DatagramPacket resendDatagramPacket = new DatagramPacket(resendPacket.getData(), resendPacket.getData().length, resendPacket.address, resendPacket.port);
                if (CHM.get(resendPacket.SeqNr) != null) {
                    //sendPacket(resendPacket);
                    datagramSocket.send(resendDatagramPacket);
                    //resendPacket.delayTime = resendDelay + System.currentTimeMillis();
                    packetQueue123.add(resendPacket);
                    executorService.schedule(this::resendUnreceivedPackets5,100,TimeUnit.MILLISECONDS);
                    System.out.println("PACKET RESENT!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void resendUnreceivedPackets4(){
        while (true) {
            for (Map.Entry<String, Packet> p : CHM.entrySet()) {
                //System.out.println(p.getValue().createdTime + " " + System.currentTimeMillis());
                if (p.getValue().createdTime < System.currentTimeMillis() - resendDelay) {
                    DatagramPacket resendDatagramPacket = new DatagramPacket(p.getValue().getData(), p.getValue().getData().length, p.getValue().address, p.getValue().port);
                    try {
                        if (CHM.get(p.getKey()) != null) {
                            datagramSocket.send(resendDatagramPacket);
                            p.getValue().createdTime = System.currentTimeMillis();
                            System.out.println("RESENT");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Queue<DatagramPacket> packetQueue = new ConcurrentLinkedQueue<>();
    public void recievePacketTest() {
            int i = 0;
            DatagramPacket datagramPacket = null;
            while (true) {
                try {
                    byte[] buf123 = new byte[100];
                    datagramPacket = new DatagramPacket(buf123,buf123.length);
                    datagramSocket.receive(datagramPacket);
                    Packet a = new Packet(datagramPacket.getData());
                    //packetQueue.add(datagramPacket);
                    CHM.remove(a.SeqNr);
                    System.out.println("Received " + ++i + " so far " + ID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

    }
    public void packetLookup2() {
        while (true) {
            DatagramPacket a = packetQueue.poll();
            if (a != null) {
                if (packetQueue.size() > 20)
                System.out.println("PQ er: " + packetQueue.size() + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                Packet p = new Packet(a.getData());
                CHM.remove(p.SeqNr);
            }
        }
    }


        public void packetLookup(DatagramPacket packet) {
            Packet p = new Packet(packet.getData());
            //WaitForResponse a = new WaitForResponse(packet,10000);
            Iterator val = DQ.iterator();
            WaitForResponse b;
            while (val.hasNext()) {
                b = (WaitForResponse) val.next();
                if (b.SeqNr.equals(p.SeqNr)) {
                    DQ.remove(b);
                    return;
                }
            }
            System.out.println("DETTE BOR IKKE SKJE? Ingen match funnet." + p.SeqNr);
        }

    public void packetLookup() {
        while (true) {
            DatagramPacket a = packetQueue.poll();
            if (a != null) {
                boolean notFound = true;
                Packet p = new Packet(a.getData());
                //Old.Packet p = new Old.Packet(packet.getData());
                Iterator val = DQ.iterator();
                WaitForResponse b;
                while (val.hasNext() && notFound) {
                    b = (WaitForResponse) val.next();
                    if (b.SeqNr.equals(p.SeqNr)) {
                        DQ.remove(b);
                        notFound = false;
                    }
                }
            }
        }
    }


    void sendPacketTest(Packet packet) throws InterruptedException {
        Thread.sleep(0,1);
        System.out.println("Sender packet: " + packet);
        if (packet.type.equals("SYN")) {
            System.out.println("Sending SYN"); //Waits for response
            //TCPList2.add(new WaitForResponse(packet));
            //addTCPPacket(packet);
            //new Thread(new Old.TCP(packet)).start();//new Old.TCP(packet).start();
            //Send packet, wait for response
        } else if (packet.type.equals("SYN-ACK")) {
            //System.out.println("Sending SYN-ACK"); //Waits for response
            //addTCPPacket(packet);
            //new Old.TCP(packet).start();
            //Old.TCP synack = new Old.TCP(packet);
            //new Thread(synack).start();//new Old.TCP(packet).start();
            //Send packet, wait for response
        } else if (packet.type.equals("ACK")) {
            //System.out.println("Sending ACK, and starts to handle packet. Terminating thread");
        }
        //Random rand = new Random();
        //if (rand.nextInt(50)<40)

        //recievePacketTest(new Old.Packet("K1m","0","SYN"));
        //recievePacketTest(packet); //TODO, HER MOTTAR JEG PACKET
        //TCPList.add(packet);
        //new Old.TCP(packet); //Thread venter nA pA svar
    }
    private void recieveEcho() throws IOException {
        boolean running = true;
        while (running) {
            DatagramPacket packet = new DatagramPacket(buf2, buf2.length);
            //socket.receive(packet);
            System.out.println("Received packet" + receiveds);
            String rec = new String(packet.getData(), 0, packet.getLength());
            if (rec.equals("end")) {
                running = false;
                System.out.println("DONE");
            }
            receiveds++;
        }
    }

    private void resendUnreceivedPackets(){
        int i = 0;
        WaitForResponse a = null;
        while (true){
            if (!DQ.isEmpty()) {
                try {
                    DQ.add(a = new WaitForResponse(DQ.take().packet,500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //a = DQ.peek();
                if (a != null) {
                    //String b = new String (a.packet.getData());
                    Packet adc = new Packet(a.packet.getData());
                    DatagramPacket pcks;
                    adc.name="resent";
                    pcks = new DatagramPacket(adc.getData(),adc.getData().length,a.packet.getAddress(),a.packet.getPort());
                    try {
                        datagramSocket.send(pcks);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Have resent: " + ++i);
                    //WaitForResponse abc = new WaitForResponse(pcks,1000);
                    //if (DQ.remove(a))
                    //    DQ.add(abc);
                    //System.out.println("Resent " + ++i + "with data: " + b);
                }
            }
        }
    }

    public void sendEcho() throws IOException {
        //socket.send(packet);
        //packet = new DatagramPacket(buf, buf.length);
        //socket.receive(packet);
        //String received = new String(
        //        packet.getData(), 0, packet.getLength());
        //receiveds++;
        //return received;
    }

    private void resendUnreceivedPackets2(){
        int i = 0;
        WaitForResponse a = null;
        Packet p = null;
        long time;
        while (true){
            for(String SeqNr : CHM.keySet()){
                Packet resendPacket = CHM.get(SeqNr);
                if (resendPacket != null && resendPacket.createdTime + 1000 < (time = System.currentTimeMillis())){//todo problem med concurrency
                    //System.out.println("Created " + resendPacket.createdTime + " and read: " + time);
                    resendPacket.name = "resent";
                    DatagramPacket resendDatagramPacket = new DatagramPacket(resendPacket.getData(),resendPacket.getData().length,resendPacket.address,resendPacket.port);
                    try {
                        datagramSocket.send(resendDatagramPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    resendPacket.createdTime = time;
                    //CHM.put(resendPacket.SeqNr,resendPacket);
                    System.out.println("Resent: " + ++i);
                }
            }
        }
    }



    public void close() {
        //socket.close();
    }
    }


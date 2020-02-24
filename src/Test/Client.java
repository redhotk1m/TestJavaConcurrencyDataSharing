package Test;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;

public class Client {

    private DatagramSocket datagramSocket;
    private InetAddress address;
    String hei = "hei123";
    private byte[] buf = hei.getBytes();
    private byte[] buf2 = new byte[256];
    DatagramPacket packet;
    int receiveds = 0;
    Thread clientThread = Thread.currentThread();
    static int TCPClients = 0;
    static boolean isReady = false;
    static ArrayList<Packet> TCPList = new ArrayList<>();
    Queue<WaitForResponse> TCPList2 = new ConcurrentLinkedQueue<>();
    BlockingQueue<WaitForResponse> DQ = new DelayQueue<WaitForResponse>();
    ConcurrentHashMap<String, Packet> CHM = new ConcurrentHashMap<>();
    BlockingQueue<Packet2> packet2BlockingQueue = new DelayQueue<>();


    Client(){
        try {
            datagramSocket = new DatagramSocket(5555);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        //Sends all SYN packets
        new Thread(this::sendAllPackets).start();
        //Receives all SYN-ACKs
        new Thread(this::recievePacketTest).start();

        //Resends all packets which no SYN-ACK was received for
        new Thread(this::packetLookup2).start();

        new Thread(this::resendUnreceivedPackets2).start();

        //new Thread(() -> {
            /*try {
                for (int sent = 0; sent < shouldSend; sent++) {
                    System.out.println("Sender packet " + sent);
                    sendEcho();
                }
                Thread.sleep(10000);
                buf = "end".getBytes();
                packet = new DatagramPacket(buf, buf.length, address, 4445);
                socket.send(packet);
            }
            catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }*/
        //}).start();

        //new Thread(() -> {
            //try {
            //    recieveEcho();
            //}catch (IOException e){
            //    e.printStackTrace();
            //}
        //}).start();
    }
    private void sendAllPackets(){

        try {
            byte[] buffer = "hei".getBytes();
            byte[] buffer2 = "SYN".getBytes();

            InetAddress addressHome = InetAddress.getByName("37.191.218.118");
            InetAddress address = InetAddress.getByName("localhost");

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
            //DatagramPacket packet2 = new DatagramPacket(buffer2, buffer2.length, address, 4445);
            //Packet packetMain = new Packet("Kim","0","SYN");
            //Packet packetMain2 = new Packet("Kim","0","SYN");

            long start;
            for (int i = 0; i<999999;i++) {
                //if (i % 2 == 0) {
                String count = Integer.toString(i);
                //packetMain.SeqNr=count;
                Packet packetMain2 = new Packet("Kim",count,"SYN");
                packetMain2.port = 4445;
                packetMain2.address = address;

                //DatagramPacket packet2 = new DatagramPacket(packetMain.getData(), packetMain.getData().length, address, 4445);
                DatagramPacket packet3 = new DatagramPacket(packetMain2.getData(), packetMain2.getData().length, address, 4445);

                CHM.put(packetMain2.SeqNr,packetMain2);
                //DQ.add(new WaitForResponse(packet2,2000));
                //datagramSocket.send(packet2);
                datagramSocket.send(packet3);

                //System.out.println("Sending nr: " + i + " with size" + Arrays.toString(packet2.getData()));
            }
            //System.out.println(DQ.size() + " er total size etter kjøring");

            //Thread.sleep(1000);
            //packet = new DatagramPacket("end".getBytes(),"end".getBytes().length,address,4445);
            //datagramSocket.send(packet);
            //socket = new DatagramSocket();
            //address = InetAddress.getByName("127.0.0.1");
            //packet = new DatagramPacket(buf, buf.length, address, 4445);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void resendUnreceivedPackets(){
        int i = 0;
        WaitForResponse a = null;
        while (true){
            if (!DQ.isEmpty()) {
                try {
                    DQ.add(a = new WaitForResponse(DQ.take().packet,2000));
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

    Queue<DatagramPacket> packetQueue = new ConcurrentLinkedQueue<>();

    public void recievePacketTest() {
            int i = 0;
            DatagramPacket datagramPacket = null;
            while (true) {
                try {
                    byte[] buf123 = new byte[100];
                    datagramPacket = new DatagramPacket(buf123,buf123.length);
                    datagramSocket.receive(datagramPacket);
                    packetQueue.add(datagramPacket);
                    System.out.println("Received " + ++i + " so far");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //packetLookup(datagramPacket);//todo Slutter før queuen er tom, fordi denne kun bli kalt når vi mottar en packet.
                //todo De som gjenstår i listen BØR være de pakkene som ble mistet.
                //if (foundPacketThread != null) {//todo server sender raskt nok, mens klient ikke klarer å motta raskt?
                    //foundPacketThread.interrupt();
                    //System.out.println("\nSYN OK, should not resend");
                    //System.out.println("Removes thread");
                    //System.out.println("Does removing work?: " + DQ.remove(foundPacketThread));
                    //System.out.println("Removes packetThread");
                    //DQ.remove(foundPacketThread);
                  //  System.out.println("Nå er DQ: " + DQ.size());
                    //TCPList2.remove(foundPacketThread);
                //} //else {
                    //System.out.println("Already received this packet and handled it");
                //}
            }
                //sendPacketTest(packet);//Sender SYN-ACK, VENTER PA ACK
    } /*else if (packet.type.equals("SYN-ACK")) {
                if (TCPList.get(0).SeqNr.equals(packet.SeqNr)) {
                    synchronized (TCPList.get(0)) {
                        TCPList.get(0).notify();
                    }
                }
                packet.type = "ACK";
                System.out.println("\nSYN-ACK OK");
                sendPacketTest(packet);//Sender ACK, Handle Packet
            } else if (packet.type.equals("ACK")) {
                //Handle packet
                if (TCPList.get(1).SeqNr.equals(packet.SeqNr)) {
                    synchronized (TCPList.get(1)) {
                        TCPList.get(1).notify();
                    }
                    //System.out.println("IT DOES CONTAIN");
                }
                System.out.println("\nACK OK, Terminating thread.");//Mottar ACK, avslutt
            }
        }*/

    /*private static void sendPacket(String input){
        if (input.equals("0")) {
            //Mangler A sjekke om packet ligger i listen, hvis den gjOr det, gA videre.
            Packet zero = new Packet("zero",0,false);
            for (int i = 0; i < TCPList.size(); i++){
                if (TCPList.get(i).ID==0){
                    synchronized (TCPList.get(i)) {
                        zero.isReceived=true;
                        TCPList.get(i).notify();
                        removeTCPPacket(TCPList.get(i));
                    }
                }
            }
            if (!zero.isReceived) {
                System.out.println("NA lager vi TCP med zero");
                addTCPPacket(zero);
                new TCP(zero);
            }
        }
        if (input.equals("1")) {
            Packet one = new Packet("one",1,false);
            for (int i = 0; i < TCPList.size(); i++){
                if (TCPList.get(i).ID==1){
                    synchronized (TCPList.get(i)) {
                        one.isReceived=true;
                        TCPList.get(i).notify();
                        removeTCPPacket(TCPList.get(i));
                    }
                }
            }
            if (!one.isReceived) {
                System.out.println("NA lager vi TCP med one");
                addTCPPacket(one);
                new TCP(one);
            }
        }
    }*/


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
            System.out.println("DETTE BØR IKKE SKJE? Ingen match funnet." + p.SeqNr);
        }
    public void packetLookup() {
        while (true) {
            DatagramPacket a = packetQueue.poll();
            if (a != null) {
                boolean notFound = true;
                Packet p = new Packet(a.getData());
                //Packet p = new Packet(packet.getData());
                Iterator val = DQ.iterator();
                WaitForResponse b;
                while (val.hasNext() && notFound) {
                    b = (WaitForResponse) val.next();
                    if (b.SeqNr.equals(p.SeqNr)) {
                        DQ.remove(b);
                        notFound = false;
                    }
                }
                //System.out.println("DETTE BØR IKKE SKJE? Ingen match funnet." + p.SeqNr);
            }
        }
    }

    public void packetLookup2() {
        while (true) {
            DatagramPacket a = packetQueue.poll();
            if (a != null) {
                //boolean notFound = true;
                Packet p = new Packet(a.getData());
                //Packet p = new Packet(packet.getData());
                //System.out.println(CHM.size() + "before");
                CHM.remove(p.SeqNr);
                //System.out.println(CHM.size() + "after");

                /*Iterator val = DQ.iterator();
                WaitForResponse b;
                while (val.hasNext() && notFound) {
                    b = (WaitForResponse) val.next();
                    if (b.SeqNr.equals(p.SeqNr)) {
                        DQ.remove(b);
                        notFound = false;
                    }
                }*/
                //System.out.println("DETTE BØR IKKE SKJE? Ingen match funnet." + p.SeqNr);
            }
        }
    }
            /*DQ.iterator().next().SeqNr.equals(p.SeqNr){
                return
            }
            if (DQ.contains(a))//todo Problemet er at når jeg leter gjennom listen, går dette for tregt. Bør bruke contains, eventuelt sende til ny tråd.
              //  System.out.println("IT CONTAINS @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            for (WaitForResponse tcp2 : DQ) {
                String SeqNr = new String(tcp2.packet.getData());
                if (SeqNr.trim().equals(packetSeqNr.trim())) {
                    return tcp2;
                }
                //if (tcp2.getPacket().getData().equals(packet.SeqNr) && tcp2.getPacket().name.equals(packet.name))
                    //return tcp2;
            }
            return null;*/


        public synchronized void addTCPclient() {
            TCPClients++;
        }

        public synchronized void subTCPclient() {
            TCPClients--;
        }

        public synchronized void addTCPPacket(Packet packet) {
            TCPList.add(packet);
        }

        public synchronized void removeTCPPacket(Packet packet) {
            TCPList.remove(packet);
        }

    void sendPacketTest(Packet packet) throws InterruptedException {
        Thread.sleep(0,1);
        System.out.println("Sender packet: " + packet);
        if (packet.type.equals("SYN")) {
            System.out.println("Sending SYN"); //Waits for response
            //TCPList2.add(new WaitForResponse(packet));
            //addTCPPacket(packet);
            //new Thread(new TCP(packet)).start();//new TCP(packet).start();
            //Send packet, wait for response
        } else if (packet.type.equals("SYN-ACK")) {
            //System.out.println("Sending SYN-ACK"); //Waits for response
            //addTCPPacket(packet);
            //new TCP(packet).start();
            //TCP synack = new TCP(packet);
            //new Thread(synack).start();//new TCP(packet).start();
            //Send packet, wait for response
        } else if (packet.type.equals("ACK")) {
            //System.out.println("Sending ACK, and starts to handle packet. Terminating thread");
        }
        //Random rand = new Random();
        //if (rand.nextInt(50)<40)

        //recievePacketTest(new Packet("K1m","0","SYN"));
        //recievePacketTest(packet); //TODO, HER MOTTAR JEG PACKET
        //TCPList.add(packet);
        //new TCP(packet); //Thread venter nA pA svar
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


    public void close() {
        //socket.close();
    }
    }


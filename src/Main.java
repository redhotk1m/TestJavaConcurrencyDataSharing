import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static int TCPClients = 0;
    static boolean isReady = false;
    static ArrayList<Packet> TCPList = new ArrayList<>();
    public static void main(String[] args) {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            //Sends packet to server
            //Receives syn-ack from server (Packet with TCP syn-ack)
            String input = scanner.nextLine();
            String[] a = input.split(";");
            Packet packet = new Packet(a[0],a[1],a[2]);
            try {
                sendPacketTest(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //recievePacketTest(packet);
            //recievePacketTest(packet);
            //recievePacketTest(packet);

            //sendPacket(input);
        }
    }

    private static void sendPacketTest(Packet packet) throws InterruptedException {
        Thread.sleep(100);
        System.out.println("Sender packet: " + packet);
        if (packet.type.equals("SYN")){
            System.out.println("Sending SYN"); //Waits for response
            addTCPPacket(packet);
            new Thread(new TCP(packet)).start();//new TCP(packet).start();
            //Send packet, wait for response
        }else if (packet.type.equals("SYN-ACK")){
            System.out.println("Sending SYN-ACK"); //Waits for response
            addTCPPacket(packet);
            //new TCP(packet).start();
            TCP synack = new TCP(packet);
            new Thread(synack).start();//new TCP(packet).start();
            //Send packet, wait for response
        }else if (packet.type.equals("ACK")){
            System.out.println("Sending ACK, and starts to handle packet. Terminating thread");
        }
        Random rand = new Random();
        //if (rand.nextInt(50)<40)
            recievePacketTest(packet);
        //TCPList.add(packet);
        //new TCP(packet); //Thread venter nå på svar
    }

    private static void recievePacketTest(Packet packet) throws InterruptedException {
        Thread.sleep(100);
        if (packet.type.equals("SYN")){
            packet.type="SYN-ACK";
            System.out.println("\nSYN OK");
            sendPacketTest(packet);//Sender SYN-ACK, VENTER PÅ ACK
        }else if (packet.type.equals("SYN-ACK")){
            if (TCPList.get(0).SeqNr.equals(packet.SeqNr)) {
                synchronized (TCPList.get(0)) {
                    TCPList.get(0).notify();
                }
            }
            packet.type="ACK";
            System.out.println("\nSYN-ACK OK");
            sendPacketTest(packet);//Sender ACK, Handle Packet
        }else if (packet.type.equals("ACK")){
            //Handle packet
            if (TCPList.get(1).SeqNr.equals(packet.SeqNr)) {
                synchronized (TCPList.get(1)) {
                    TCPList.get(1).notify();
                }
                //System.out.println("IT DOES CONTAIN");
            }
            System.out.println("\nACK OK, Terminating thread.");//Mottar ACK, avslutt
        }
    }

    /*private static void sendPacket(String input){
        if (input.equals("0")) {
            //Mangler å sjekke om packet ligger i listen, hvis den gjør det, gå videre.
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
                System.out.println("Nå lager vi TCP med zero");
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
                System.out.println("Nå lager vi TCP med one");
                addTCPPacket(one);
                new TCP(one);
            }
        }
    }*/

    public static synchronized void addTCPclient(){
        TCPClients++;
    }
    public static synchronized void subTCPclient(){
        TCPClients--;
    }

    public static synchronized void addTCPPacket(Packet packet){
        TCPList.add(packet);
    }

    public static synchronized void removeTCPPacket(Packet packet){
        TCPList.remove(packet);
    }

}

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Client abc = new Client();
        JFrame frame = new JFrame("Overlay");

        // Canvas, to have a video and on top a button
        final Canvas canvas = new Canvas();
        Button button = new Button();
        button.setBounds(10,10, 100, 40);
        canvas.setPreferredSize(new Dimension(200, 200));

        // Layout
        JPanel content = new JPanel(new GridLayout(2,1));
        //content.add(canvas);
        JButton CS1 = new JButton("Client sends 1");
        JButton CS2 = new JButton("Client sends 2");
        JButton CR1 = new JButton("Client receives SYN-ACK 1");
        JButton CR2 = new JButton("Client receives SYN-ACK 2");
        CS1.addActionListener(e -> {
            try {
                abc.sendPacketTest(new Packet("KIM","0","SYN"));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        CS2.addActionListener(e -> {
            try {
                abc.sendPacketTest(new Packet("KIM2","1","SYN"));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        CR1.addActionListener(e -> {
            try {
                abc.recievePacketTest(new Packet("KIM","0","SYN"));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        CR2.addActionListener(e -> {
            try {
                abc.recievePacketTest(new Packet("KIM2","1","SYN"));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        content.add(CS1);  // for empty cell
        content.add(CS2);  // for empty cell
        content.add(CR1);  // for empty cell
        content.add(CR2);  // for empty cell

        // Show
        frame.add(content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        /*while (true) {
            Scanner scanner = new Scanner(System.in);
            //Sends packet to server
            //Receives syn-ack from server (Packet with TCP syn-ack)
            String input = scanner.nextLine();
            String[] a = input.split(";");
            Packet packet = new Packet(a[0], a[1], a[2]);
            MainClass abc = new MainClass();
            try {
                abc.sendPacketTest(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //recievePacketTest(packet);
            //recievePacketTest(packet);
            //recievePacketTest(packet);

            //sendPacket(input);
        }*/
    }
}

/*class Client {

        static int TCPClients = 0;
        static boolean isReady = false;
        static ArrayList<Packet> TCPList = new ArrayList<>();
        ArrayList<TCP2> TCPList2 = new ArrayList<>();

        void sendPacketTest(Packet packet) throws InterruptedException {
            Thread.sleep(100);
            System.out.println("Sender packet: " + packet);
            if (packet.type.equals("SYN")) {
                System.out.println("Sending SYN"); //Waits for response
                TCPList2.add(new TCP2(packet));
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
            Random rand = new Random();
            //if (rand.nextInt(50)<40)

            //recievePacketTest(new Packet("K1m","0","SYN"));
            //recievePacketTest(packet); //TODO, HER MOTTAR JEG PACKET
            //TCPList.add(packet);
            //new TCP(packet); //Thread venter nå på svar
        }

        public void recievePacketTest(Packet packet) throws InterruptedException {
            Thread.sleep(100);
            if (packet.type.equals("SYN")) {
                //packet.type="SYN-ACK";
                //packet.name = "Ki2m";
                TCP2 foundPacketThread = packetLookup(packet);
                if (foundPacketThread != null) {
                    foundPacketThread.interrupt();
                    System.out.println("\nSYN OK, should not resend");
                    TCPList2.remove(foundPacketThread);
                }else {
                    System.out.println("Already received this packet and handled it");
                }
                //sendPacketTest(packet);//Sender SYN-ACK, VENTER PÅ ACK
            } else if (packet.type.equals("SYN-ACK")) {
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
    }

        public TCP2 packetLookup(Packet packet) {
            for (TCP2 tcp2 : TCPList2) {
                if (tcp2.getPacket().SeqNr.equals(packet.SeqNr) && tcp2.getPacket().name.equals(packet.name))
                    return tcp2;
            }
            return null;
        }

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
    }*/

    /*class Server{


        public void sendPacket(){

        }

        public void receivePacket(){

        }

    }*/

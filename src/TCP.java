public class TCP implements Runnable {
    String input;
    String ID;
    Packet packet;
    boolean isNotified = false;
    TCP(Packet packet){
        //System.out.println("TCP IS CREATED WITH ID: " + packet.SeqNr);
        this.packet = packet;
        this.input = packet.name;
        this.ID = packet.SeqNr;
        //this.start();
    }

    @Override
    public void run() {
        System.out.println("Thread started");
        //System.out.println("TCP Socket sent, waiting for response");
        Thread thisThread = Thread.currentThread();
        synchronized (packet) {
            try {
                    // System.out.println("Timeout 10 sec: " + packet.ID);
                    System.out.println("Waiting for response-packet " + packet.type);
                new Thread(()->{
                    try {
                        int i = 0;
                        Thread.sleep(500);
                        while (thisThread.isAlive()) {
                            if (i > 5)
                                break;
                            System.out.println("RESENDING PACKET: " + Thread.currentThread().getName() + " : " + i);
                            synchronized (packet){
                                packet.notify();
                            }
                            i++;
                            Thread.sleep(500);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                    packet.wait();//If timed out, resend packet. How?..//TODO FIX THIS!
                    System.out.println("Packet received and handled, thread closing: " + packet.type);
                //Main.removeTCPPacket(packet);
                /*if (packet.equals(this.packet)) {
                    //packet.isReceived = "true";
                    System.out.println("Har mottatt pakken min, avslutter nå tråd");
                }
                else {
                    System.out.println("Mottok: " + packet.toString() + " men vil ha: " + this.packet.toString());
                }*/
              //  System.out.println("Nå har jeg fått packet, med data: " + packet.name + packet.ID);
                //Main.isReady = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setNotified(boolean notified) {
        isNotified = notified;
    }

}

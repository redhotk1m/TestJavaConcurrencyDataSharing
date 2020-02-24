package Test;

import javax.print.PrintServiceLookup;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class WaitForResponse extends Thread implements Delayed {
    DatagramPacket packet;
    DatagramSocket socket;
    private long startTime;
    public String SeqNr;

    WaitForResponse(DatagramPacket packet, DatagramSocket socket, long delayInMilliseconds){
        this.packet = packet;
        //this.start();
        this.socket = socket;
        this.startTime = System.currentTimeMillis() + delayInMilliseconds;
        String[] d = new String (packet.getData()).trim().split(",");
        SeqNr = d[1];

    }
    WaitForResponse(DatagramPacket packet, long delayInMilliseconds){
        this.packet = packet;
        //this.start();
        this.startTime = System.currentTimeMillis() + delayInMilliseconds;
        String[] d = new String (packet.getData()).trim().split(",");
        SeqNr = d[1];
    }

    @Override
    public void run() {
        while (!this.interrupted()){
            try {
                //System.out.println("Waiting for response");
                sleep(5000);
            } catch (InterruptedException e) {
                //System.out.println("Packet received, waiting thread: " + this.getName() + " is closing");
                return;
            }
            if (!Thread.interrupted()) {

                System.out.println("No response yet, RESENDING PACKET " + new String(packet.getData()));
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public DatagramPacket getPacket() {
        return packet;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = startTime - System.currentTimeMillis();
        return unit.convert(diff,TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this.startTime < ((WaitForResponse)o).startTime){
            return -1;
        }
        if (this.startTime > ((WaitForResponse)o).startTime){
            return 1;
        }
        return 0;
    }
}

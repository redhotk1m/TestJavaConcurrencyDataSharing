package Test;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Packet2 implements Delayed {
    DatagramPacket datagramPacket;
    String name,SeqNr,type;
    int resentSYN = 0, resentSYNACK = 0, resentACK = 0;
    long createdTime;
    int port;
    InetAddress address;

    Packet2(String name, String SeqNr, String type){
        this.name = name;
        this.SeqNr = SeqNr;
        this.type=type;
        this.createdTime = System.currentTimeMillis();
    }

    Packet2(byte[] data){
        String[] dataArray = readData(data).split(",");
        this.name = dataArray[0];
        this.SeqNr = dataArray[1];
        this.type = dataArray[2];
        this.createdTime = System.currentTimeMillis();
    }

    Packet2(DatagramPacket datagramPacket){
        this.datagramPacket = datagramPacket;
    }



    public int getResentSYN() {
        return resentSYN;
    }

    public void setResentSYN(int resentSYN) {
        this.resentSYN = resentSYN;
    }

    public int getResentSYNACK() {
        return resentSYNACK;
    }

    public void setResentSYNACK(int resentSYNACK) {
        this.resentSYNACK = resentSYNACK;
    }

    public int getResentACK() {
        return resentACK;
    }

    public void setResentACK(int resentACK) {
        this.resentACK = resentACK;
    }

    @Override
    public String toString() {
        return "Name: " + name + " SeqNr: " + SeqNr + " type: " + type;
    }

    public byte[] getData(){
        return (name + "," + SeqNr + "," + type).getBytes();
    }

    public String readData(byte[] data){
        String message = new String(data).trim();
        return message;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = createdTime - System.currentTimeMillis();
        return unit.convert(diff,TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this.createdTime < ((Packet2)o).createdTime){
            return -1;
        }
        if (this.createdTime > ((Packet2)o).createdTime){
            return 1;
        }
        return 0;
    }

}

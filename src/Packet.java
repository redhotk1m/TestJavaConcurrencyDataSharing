import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Packet implements Delayed {
    DatagramPacket datagramPacket;
    String name,SeqNr,type;
    int resentSYN = 0, resentSYNACK = 0, resentACK = 0;
    long createdTime,delayTime;
    int port;
    InetAddress address;

    Packet(String name, String SeqNr, String type){
        this.name = name;
        this.SeqNr = SeqNr;
        this.type=type;
        this.createdTime = System.currentTimeMillis();
    }

    Packet(byte[] data){
        String[] dataArray = readData(data).split(",");
        this.name = dataArray[0];
        this.SeqNr = dataArray[1];
        this.type = dataArray[2];
        this.createdTime = System.currentTimeMillis();
    }

    Packet(String name, String SeqNr, String type, long delayTime){
        this.name = name;
        this.SeqNr = SeqNr;
        this.type=type;
        this.createdTime = System.currentTimeMillis();
        this.delayTime = createdTime + delayTime;
    }

    Packet(byte[] data, long delayTime){
        String[] dataArray = readData(data).split(",");
        this.name = dataArray[0];
        this.SeqNr = dataArray[1];
        this.type = dataArray[2];
        this.createdTime = System.currentTimeMillis();
        this.delayTime = createdTime + delayTime;
    }

    Packet(DatagramPacket datagramPacket){
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
        long diff = delayTime - System.currentTimeMillis();
        return unit.convert(diff,TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this.delayTime < ((Packet)o).delayTime){
            return -1;
        }
        if (this.delayTime > ((Packet)o).delayTime){
            return 1;
        }
        return 0;
    }
}

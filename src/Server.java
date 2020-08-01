import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Queue;

class Server implements Runnable{
    private Queue<DatagramPacket> packetQueue;
    public DatagramSocket socket;
    private boolean running;

    public Server(Queue<DatagramPacket> packetQueue) {
        this.packetQueue = packetQueue;
        try {
            socket = new DatagramSocket(4445);
            socket.setSoTimeout(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        System.out.println("Server is running");
        running = true;
        while (running) {
                try {
                    byte[] buf = new byte[100];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    packetQueue.offer(packet);
                } catch (IOException e) {
                    running = false;
                    System.out.println("Server is done waiting for packets");
                    while (!packetQueue.isEmpty()){

                    }
                    System.out.println("Queue should be empty, closing thread");
                    return;
                }
            }
        }

    public void sendPacket(DatagramPacket packet){
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

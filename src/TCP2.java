public class TCP2 extends Thread {
    Packet packet;

    TCP2(Packet packet){
        this.packet = packet;
        this.start();
    }

    @Override
    public void run() {
        while (!this.interrupted()){
            try {
                System.out.println("Waiting for response");
                sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Packet received, waiting thread: " + this.getName() + " is closing");
                return;
            }
            if (!Thread.interrupted())
                System.out.println("No response yet, RESENDING PACKET" + packet.toString());
        }
    }


    public Packet getPacket() {
        return packet;
    }
}

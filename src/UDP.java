public class UDP extends Thread {

    String input;

    UDP(String input){
        System.out.println("UDP IS CREATED");
        this.input = input;
        this.start();
    }

    @Override
    public void run() {
        System.out.println("UDP THREAD HAS STARTED");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("UDP Socket sent");
    }



}

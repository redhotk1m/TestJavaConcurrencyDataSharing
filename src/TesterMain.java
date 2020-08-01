public class TesterMain {
    public static void main(String[] args) {
        new Thread(() -> {
            ServerMain.main(null);
        }).start();
        new Thread(() -> {
            ClientMain.main(null);
        }).start();
    }


}

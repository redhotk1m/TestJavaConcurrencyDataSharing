public class Packet {
    String name;
    String SeqNr;
    String type;
    Packet(String name, String SeqNr, String type){
        this.name = name;
        this.SeqNr = SeqNr;
        this.type=type;
    }

    @Override
    public String toString() {
        return "Name: " + name + " SeqNr: " + SeqNr + " type: " + type;
    }
}

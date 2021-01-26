import java.util.ArrayList;

public class Node {
    class transition{
        String from;
        String to;
        String value;
        transition(String from, String to, String value){
            this.from = from;
            this.to = to;
            this.value = value;
        }
    }
    String label;
    Boolean isStart;
    Boolean isAccept;
    ArrayList<transition> incomingTransition;
    ArrayList<transition> outgoingTransition;
    transition selfLoop;

    Node(String label, Boolean isAccept, Boolean isStart) {
        this.label = label;
        this.isAccept = isAccept;
        this.isStart = isStart;
        incomingTransition = new ArrayList<>();
        outgoingTransition = new ArrayList<>();
    }

    public void setSelfLoop(String value) {
        this.selfLoop = new transition(label, label, value);
    }

    public void addIncomingTransition(String from, String value) {
        this.incomingTransition.add(new transition(from, label, value));
    }

    public void addOutgoingTransition(String to, String value) {
        this.outgoingTransition.add(new transition(label, to, value));
    }
}

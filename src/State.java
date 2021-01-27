import java.util.HashMap;
import java.util.Map;

public class State {
    class transition {
        String from;
        String to;
        String value;

        transition(String from, String to, String value) {
            this.from = from;
            this.to = to;
            this.value = value;
        }
    }

    String label;
    Boolean isStart;
    Boolean isAccept;
    Map<String, transition> inTransitions;
    Map<String, transition> outTransitions;
    transition selfLoop;

    State(String label, Boolean isAccept, Boolean isStart) {
        this.label = label;
        this.isAccept = isAccept;
        this.isStart = isStart;
        inTransitions = new HashMap<>();
        outTransitions = new HashMap<>();
    }

    public void addSelfLoop(String value) {
        if (selfLoop == null) {
            this.selfLoop = new transition(label, label, value);
        } else {
            this.selfLoop.value = "(" + this.selfLoop.value + "+" + value + ")";
        }
    }

    public void addInTransition(String from, String value) {
        if (!inTransitions.containsKey(from)) {
            this.inTransitions.put(from, new transition(from, label, value));
        } else {
            this.inTransitions.get(from).value = "(" + this.inTransitions.get(from).value + "+" + value + ")";
        }
    }

    public void addOutTransition(String to, String value) {
        if (!outTransitions.containsKey(to)) {
            this.outTransitions.put(to, new transition(label, to, value));
        } else {
            this.outTransitions.get(to).value  = "(" + value + "+" + this.outTransitions.get(to).value + ")";
        }
    }
    public void removeInTransition(String dest){
        this.inTransitions.remove(dest);
    }
    public void removeOutTransition(String dest){
        this.outTransitions.remove(dest);
    }

}

import java.io.*;
import java.util.*;

public class Main {
    // Finite state machine
    static Map<String, State> fsm = new HashMap<>();
    static String start;
    static List<String> accept;
    static List<String> allStates;
    static final String filePath = "files/problem5.txt";
    public static void main(String[] args) throws IOException {

        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        // Read variables
        start = br.readLine().split("=")[1];
        accept = new ArrayList<>(Arrays.asList(br.readLine().split("=")[1].split(",")));
        br.readLine(); // Alphabet is not used so skip that line
        allStates = new ArrayList<>(Arrays.asList(br.readLine().split("=")[1].split(",")));

        // Save states to map
        for (String str : allStates) {
            fsm.put(str, new State(str, accept.contains(str), str.equals(start)));
        }

        // Read and save transitions
        String st;
        while ((st = br.readLine()) != null && !st.equals("")) {
            String[] temp = st.split(",");
            String[] temp2 = temp[1].split("=");
            String from = temp[0];
            String value = temp2[0];
            String to = temp2[1];
            if (from.equals(to)) {
                fsm.get(from).addSelfLoop(value);
            } else {
                fsm.get(from).addOutTransition(to, value);
                fsm.get(to).addInTransition(from, value);
            }
        }
        System.out.println("\n------------------" + fsm.size() + "-state DFA---------------------");
        showFSM();
        System.out.println("--------------------------------------------------");
        System.out.println("Adding new start state (qinit)");
        System.out.println("Adding new final state (qfin)");

        // Add accept state qfin
        addNewAccept();
        // Add start state qinit
        addNewStart();

        // Repeat until qinit and qfin remains
        while (fsm.size() > 2) {
            System.out.println("-------------------" + fsm.size() + "-state GNFA-------------------");
            // print each step
            showFSM();
            // remove q rip
            eliminateState();
        }
        System.out.println("-------------------2-state GNFA-------------------");
        showFSM();
        System.out.println("--------------------------------------------------");
        System.out.println("Regular Expression: " + fsm.get("qinit").outTransitions.get("qfin").value);
        System.out.println("--------------------------------------------------");

    }
    // Add new start state and replace previous start
    public static void addNewStart() {
        State newStart = new State("qinit", false, true);
        newStart.addOutTransition(fsm.get(start).label, "€");
        fsm.put(newStart.label, newStart);
        fsm.get(start).isStart = false;
        fsm.get(start).addInTransition(newStart.label, "€");
        start = newStart.label;
        allStates.add(0, newStart.label);
    }
    // Add new accept state and replace previous accepts
    public static void addNewAccept() {
        State newAccept = new State("qfin", true, false);
        for (State state : fsm.values()) {
            if (accept.contains(state.label)) {
                newAccept.addInTransition(state.label, "€");
                state.addOutTransition(newAccept.label, "€");
                state.isAccept = false;
                accept.remove(state.label);
            }
        }
        fsm.put(newAccept.label, newAccept);
        accept.add(newAccept.label);
        allStates.add(newAccept.label);
    }

    // Print steps
    public static void showFSM() {
        System.out.println("Start -> " + start);
        System.out.print("Accept -> ");
        for (String str : accept) System.out.print(str + " ");
        System.out.println();
        System.out.print("All States -> ");
        for (String str : allStates) System.out.print(str + " ");
        System.out.println();
        System.out.println("Transitions: ");
        for (State state : fsm.values()) {
            if (state.selfLoop != null) {
                System.out.println("  " + state.selfLoop.from + "\t---" + state.selfLoop.value + "--->\t" + state.selfLoop.to);
            }
            for (State.transition tran : state.outTransitions.values()) {
                System.out.println("  " + tran.from + "\t---" + tran.value + "--->\t" + tran.to);
            }
        }
    }
    // Remove q rip
    public static void eliminateState() {
        // Remove dead states
        removeDeadState();
        // Pick the least amount of transitions
        String removeState = pickState();
        // State = qrip
        State state = fsm.get(removeState);
        // removeIn, removeOut are transitions to remove after loop finished
        ArrayList<String> removeIn = new ArrayList<>();
        ArrayList<String> removeOut = new ArrayList<>();
        System.out.println("--------------------------------------------------");
        System.out.println("Removing " + state.label + "...");
        // For all incoming states
        for (State.transition transIn : state.inTransitions.values()) {
            // For all outgoing states
            for (State.transition transOut : state.outTransitions.values()) {
                transIn.value = transIn.value.equals("€") ? "": transIn.value;
                transOut.value = transOut.value.equals("€") ? "": transOut.value;
                // If incoming from and outgoing to are same (self loop)
                if (transIn.from.equals(transOut.to)) {
                    // If state does not have self loop
                    if (fsm.get(state.label).selfLoop == null) {
                        fsm.get(transIn.from).addSelfLoop(transIn.value + transOut.value);
                    } else {
                        String selfLoopValue = state.selfLoop.value.length() == 1 ? state.selfLoop.value : "(" + state.selfLoop.value + ")";
                        fsm.get(transIn.from).addSelfLoop(transIn.value + selfLoopValue + "*" + transOut.value);
                    }
                    // Ex:  q1 -> q2 -> q3   transIn.from = q1 tranOut.to = q3
                } else {
                    // If state does not have self loop
                    if (fsm.get(state.label).selfLoop == null) {
                        fsm.get(transIn.from).addOutTransition(transOut.to, transIn.value + transOut.value);
                        fsm.get(transOut.to).addInTransition(transIn.from, transIn.value + transOut.value);
                    } else {
                        String selfLoopValue = state.selfLoop.value.length() == 1 ? state.selfLoop.value : "(" + state.selfLoop.value + ")";
                        fsm.get(transIn.from).addOutTransition(transOut.to, transIn.value + selfLoopValue + "*" + transOut.value);
                        fsm.get(transOut.to).addInTransition(transIn.from, transIn.value + selfLoopValue + "*" + transOut.value);
                    }
                }
                removeOut.add(transOut.to);
            }
            removeIn.add(transIn.from);
        }
        // Remove all transitions
        allStates.remove(removeState);
        fsm.remove(removeState);
        for (String str : removeOut) {
            fsm.get(str).removeInTransition(removeState);
        }
        for (String str : removeIn) {
            fsm.get(str).removeOutTransition(removeState);
        }
    }
    // Sort the states by amount of transitions they have and return the minimum
    public static String pickState() {
        PriorityQueue<Map.Entry<String, Integer>> queue = new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());
        int sum;
        for (State state : fsm.values()) {
            sum = 0;
            if (!state.isAccept && !state.isStart) {
                if (state.selfLoop != null) {
                    sum++;
                }
                sum += state.inTransitions.size() + state.outTransitions.size();
                queue.offer(new AbstractMap.SimpleEntry<>(state.label, sum));
            }
        }
        String minimumState = null;
        for (Map.Entry<String, Integer> value : queue) {
            minimumState = value.getKey();
        }
        return minimumState;
    }
    // Remove states without outgoing transitions
    public static void removeDeadState() {
        boolean removed = true;
        while (removed) {
            String label = "";
            removed = false;
            for (State state : fsm.values()) {
                if (state.outTransitions.size() == 0 && !state.isAccept) {
                    for (State.transition trans : state.inTransitions.values()) {
                        fsm.get(trans.from).outTransitions.remove(trans.to);
                        removed = true;
                    }
                    label = state.label;
                    System.out.println("-------------------------------------------");
                    System.out.println("Dead State " + label + " is deleted.");
                    System.out.println("-------------------------------------------");
                }
            }
            fsm.remove(label);
            allStates.remove(label);
        }
    }
}

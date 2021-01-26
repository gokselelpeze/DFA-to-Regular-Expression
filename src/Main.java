import java.io.*;
import java.util.*;

public class Main {
    static Map<String, State> fsm = new HashMap<>();
    static String start;
    static List<String> accept;
    static List<String> allStates;
    static final String filePath = "src/problem1.txt";

    public static void main(String[] args) throws IOException {

        File file = new File(filePath);

        BufferedReader br = new BufferedReader(new FileReader(file));
        start = br.readLine().split("=")[1];
        accept = new ArrayList<>(Arrays.asList(br.readLine().split("=")[1].split(",")));
        br.readLine();  //.split("=")[1].split(",");
        allStates = new ArrayList<>(Arrays.asList(br.readLine().split("=")[1].split(",")));


        for (String str : allStates) {
            fsm.put(str, new State(str, accept.contains(str), str.equals(start)));
        }

        ArrayList<ArrayList<String>> transitions = new ArrayList<>();


        String st;
        while ((st = br.readLine()) != null) {
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
        System.out.println("--------" + fsm.size() + "-state DFA---------");
        showFSM();
        // Add accept state qfin
        addNewAccept();
        // Add start state qinit
        addNewStart();
        System.out.println("--------" + fsm.size() + "-state GNFA--------");
        showFSM();
        removeDeadState();
        System.out.println("--------" + fsm.size() + "-state GNFA--------");
        showFSM();
        while (fsm.size() > 2){
            eliminateState();
            showFSM();
        }
    }

    public static void addNewStart() {
        State newStart = new State("qinit", false, true);
        newStart.addOutTransition(fsm.get(start).label, "€");
        fsm.put(newStart.label, newStart);
        fsm.get(start).isStart = false;
        fsm.get(start).addInTransition(newStart.label, "€");
        start = newStart.label;
        allStates.add(0, newStart.label);
    }

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
        System.out.println("----------------------------");
    }

    public static void eliminateState() {
        removeDeadState();
        ArrayList<String> removeIn = new ArrayList<>();
        ArrayList<String> removeOut = new ArrayList<>();
        String removeState = "";
        // silecegimiz state -> state.label
        for (State state : fsm.values()) {
            if (!state.isAccept && !state.isStart) {
                System.out.println("Removing " + state.label + "...");
                for (State.transition transIn : state.inTransitions.values()) {
                    for (State.transition transOut : state.outTransitions.values()) {
                        if (transIn.from.equals(transOut.to)) {
                            // gelen okun geldigi statei bul, gelen ok + [self loop varsa] + oraya giden oku
                            // , o statein self loopuna ekle
                            if (fsm.get(state.label).selfLoop == null) {
                                fsm.get(transIn.from).addSelfLoop(transIn.value + transOut.value);
                            } else {
                                fsm.get(transIn.from).addSelfLoop(transIn.value + state.selfLoop.value + "*" + transOut.value);
                            }
                            // transIn.from = q1 tranOut.to = q3   q1 -> q2 -> q3
                        } else {
                            if (fsm.get(state.label).selfLoop == null) {
                                fsm.get(transIn.from).addOutTransition(transOut.to, transIn.value + transOut.value);
                                fsm.get(transOut.to).addInTransition(transIn.from, transIn.value + transOut.value);
                            } else {
                                fsm.get(transIn.from).addOutTransition(transOut.to, transIn.value + state.selfLoop.value + "*" + transOut.value);
                                fsm.get(transOut.to).addInTransition(transIn.from, transIn.value + state.selfLoop.value + "*" + transOut.value);
                            }
                        }
                        removeOut.add(transOut.to);
                        //fsm.get(transOut.to).removeInTransition(state.label);
                    }

                    removeIn.add(transIn.from);
                    //fsm.get(transIn.from).removeOutTransition(state.label);
                }

                removeState = state.label;
            }
            break;
        }
        allStates.remove(removeState);
        fsm.remove(removeState);
        for (String str: removeOut) {
            fsm.get(str).removeInTransition(removeState);
        }
        for (String str: removeIn) {
            fsm.get(str).removeOutTransition(removeState);
        }
    }

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
                    System.out.println("Dead State " + label + " is deleted.");
                }
            }
            fsm.remove(label);
        }
    }
}

import java.io.*;
import java.util.*;

public class Main {
    static Map<String, Node> fsm = new HashMap<>();
    static String start;
    static List<String> accept;
    static List<String> allStates;
    static final String filePath = "src/sample.txt";

    public static void main(String[] args) throws IOException {

        File file = new File(filePath);

        BufferedReader br = new BufferedReader(new FileReader(file));
        start = br.readLine().split("=")[1];
        accept = new ArrayList<>(Arrays.asList(br.readLine().split("=")[1].split(",")));
        br.readLine();  //.split("=")[1].split(",");
        allStates = new ArrayList<>(Arrays.asList(br.readLine().split("=")[1].split(",")));


        for(String str: allStates){
            fsm.put(str, new Node(str, accept.contains(str), str.equals(start)));
        }

        ArrayList<ArrayList<String>> transitions = new ArrayList<>();


        String st;
        while ((st = br.readLine()) != null) {
            // Eger from ve to ayniysa from un self loop una ekle
            // Degilse
                // from outgoing ine ekle
                // to nun incoming ine ekle
            String[] temp = st.split(",");
            String[] temp2 = temp[1].split("=");
            String from = temp[0];
            String value = temp2[0];
            String to = temp2[1];
            if (from.equals(to)){
                fsm.get(from).setSelfLoop(value);
            }
            else{
                fsm.get(from).addOutgoingTransition(to, value);
                fsm.get(to).addIncomingTransition(from, value);
            }
        }

        showFSM();
        addNewAccept();
        addNewStart();
        showFSM();
    }

    public static void addNewStart(){
        Node newStart = new Node("qinit", false, true);
        newStart.addOutgoingTransition(fsm.get(start).label, "€");
        fsm.put(newStart.label, newStart);
        fsm.get(start).isStart = false;
        fsm.get(start).addIncomingTransition(newStart.label, "€");
        start = newStart.label;
    }

    public static void addNewAccept(){
        Node newAccept = new Node("qfin", true, false);
        for(Node node: fsm.values()){
            if (accept.contains(node.label)){
                newAccept.addIncomingTransition(node.label, "€");
                node.addOutgoingTransition(newAccept.label, "€");
                node.isAccept = false;
                accept.remove(node.label);
            }
        }
        fsm.put(newAccept.label, newAccept);
        accept.add(newAccept.label);
    }

    public static void showFSM(){
        System.out.println("Start -> " + start);
        System.out.print("Accept -> ");
        for(String str: accept) System.out.print(str + " ");
        System.out.println();
        System.out.print("All Nodes -> ");
        for(String str: allStates) System.out.print(str + " ");
        System.out.println();
        System.out.println("Transitions: ");
        for(Node node: fsm.values()){
            if (node.selfLoop != null){
                System.out.println("  " + node.selfLoop.from + "\t--" + node.selfLoop.value + "--> " + node.selfLoop.to);
            }
            for(Node.transition tran: node.outgoingTransition){
                System.out.println("  " + tran.from + "\t--" + tran.value + "--> " + tran.to);
            }
        }
        System.out.println("\n");
    }
}

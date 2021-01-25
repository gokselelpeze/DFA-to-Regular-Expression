import java.io.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

        File file = new File("src/sample.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));
        String start = br.readLine().split("=")[1];
        String[] accept = br.readLine().split("=")[1].split(",");
        String[] alphabet = br.readLine().split("=")[1].split(",");
        String[] allStates = br.readLine().split("=")[1].split(",");


        System.out.println("Start State -> " + start);
        System.out.print("Accept state -> ");
        for (String s : accept) {
            System.out.print(s + " ");
        }
        System.out.print("\nAlphabet -> ");
        for (String s : alphabet) {
            System.out.print(s + " ");
        }
        System.out.print("\nAll States -> ");
        for (String s : allStates) {
            System.out.print(s + " ");
        }
        System.out.print("\n");

        ArrayList<ArrayList<String>> transitions = new ArrayList<>();


        String st;
        while ((st = br.readLine()) != null) {
            String[] temp = st.split(",");
            ArrayList<String> tran = new ArrayList<>();
            tran.add(temp[0]);
            String[] temp2 = temp[1].split("=");
            tran.add(temp2[0]);
            tran.add(temp2[1]);
            transitions.add(tran);
        }
        for (ArrayList<String> arraylist: transitions) {
            for (String s: arraylist) {
                System.out.print(s + " -> ");
            }
            System.out.println("\n");
        }


    }

}

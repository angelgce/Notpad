import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Engine {
    public static HashMap<String, Notpad> listNotpad = new HashMap<String, Notpad>();
    public static List<Token> tokens;
    public static int count = 0;

    public static void main(String[] args) {
        loadTokens();
        // tokens.forEach(token -> System.out.println(token.toString() + "\n"));
        Notpad bloc = new Notpad();
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean stop = false;
                while (!stop) {
                    if (listNotpad.size() <= 0 || listNotpad == null) {
                        System.out.println("There is not more notpads open");
                        System.out.println("closing...");
                        stop = true;
                        System.exit(1);
                    } else {
                        try {
                            // System.out.println("\nRuning... Size: " + listNotpad.size());
                            listNotpad.forEach((key, item) -> {
                                // System.out.println("Index: " + key + " visible " + item.isVisible());
                                if (item.isVisible() == false) {
                                    listNotpad.remove(key);
                                }
                            });

                            Thread.sleep(5000);
                        } catch (Exception e) {
                        }
                    }

                }
            }
        });
        hilo.start();

    }

    private static void loadTokens() {
        System.out.println("Loading");
        tokens = new ArrayList<Token>();
        String path = "C:\\Users\\abricot\\Documents\\Notpad\\resources\\tokens.csv";
        try {
            int index = 1;
            Scanner sc = new Scanner(new File(path));
            // sc.useDelimiter(",");

            while (sc.hasNext()) {
                // System.out.println(sc.nextLine() + " :: " + index);
                Token token = new Token();
                token.setId(index);
                token.setCSV(sc.nextLine());
                token.parsingCSV();
                tokens.add(token);
                index++;
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("error scanning :: " + e.getMessage());
        }

    }

}
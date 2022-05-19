import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import org.ini4j.Ini;


public class Engine {
    public static HashMap<String, Notpad> listNotpad = new HashMap<String, Notpad>();
    public static List<Token> tokens;
    public static int count = 0;
    private static Config config;

    public static void main(String[] args) {
        // Loading config.ini
        config = Config.getInstance(args);
        loadTokens(); // Loading tokens from csv
        Notpad notpad = new Notpad(); // starting notpad
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

        Ini.Section section = Config.ini.get("CSV");
        String path = section.get("path");
        System.out.println("path:: " +path);
        System.out.println(path);
        tokens = new ArrayList<Token>();
        try {
            int index = 1;
            Scanner sc = new Scanner(new File(path));
            while (sc.hasNext()) {
                // POJO - Tokens
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
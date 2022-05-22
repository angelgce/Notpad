import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ini4j.Ini;

public class Engine {
    public static

    HashMap<String, Notpad> listNotpad = new HashMap<String, Notpad>();
    public static List<Token> tokens;
    public static HashMap<String, Dictionary> hashRules = new HashMap<String, Dictionary>();
    public static int count = 0;
    private static Config config;
    private static List<String> reservedList = new ArrayList<String>();

    public static void main(String[] args) {
        // Loading config.ini
        config = Config.getInstance(args);
        loadTokens(); // Loading tokens from csv
        loadRules();
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
        String path = section.get("tokensPath");
        System.out.println("path:: " + path);
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

        reservedWords();

    }

    private static void loadRules() {
        Ini.Section section = Config.ini.get("CSV");
        String path = section.get("dirPath");
        System.out.println("path:: " + path);
        try {
            int index = 1;
            Scanner sc = new Scanner(new File(path));
            while (sc.hasNext()) {
                // System.out.println(sc.nextLine());
                // POJO - dir
                Dictionary dictionary = new Dictionary();
                dictionary.setId(index);
                dictionary.setStrLine(sc.nextLine());
                dictionary.formatLine();
                hashRules.put(dictionary.getKey(), dictionary);
                index++;
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("error scanning :: " + e.getMessage());
        }

    }

    private static void reservedWords() {
        StringBuilder allWords = new StringBuilder();
        tokens.forEach(token -> {
            Pattern pattern = Pattern.compile("([\\w]+){2}");
            Matcher matcher = pattern.matcher(token.getRegx());
            while (matcher.find()) {
                reservedList.add(matcher.group(0));
            }
        });

    }

    public static boolean validationRW(String uWord) {
        AtomicBoolean isMatch = new AtomicBoolean(false);
        reservedList.forEach(rWord -> {
            if (uWord.equals(rWord)) {
                isMatch.set(true);
            }
        });
        return isMatch.get();
    }
}
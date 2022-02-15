import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Engine {
    public static HashMap<String, Notpad> listNotpad = new HashMap<String, Notpad>();
    public static int count = 0;

    public static void main(String[] args) {
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
                            System.out.println("\nRuning... Size: " + listNotpad.size());
                            listNotpad.forEach((key, item) -> {
                                System.out.println("Index: " + key + " visible " + item.isVisible());
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

}
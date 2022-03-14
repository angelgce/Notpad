import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rules {

    private HashMap<String, Double> numeros = new HashMap<String, Double>();
    private HashMap<String, String> mensajes = new HashMap<String, String>();

    public Rules() {
    }

    public boolean errorKeyWords(String string) { // 53
        Pattern pattern = Pattern.compile("(Guarda_.+|Muestra:.+)");
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    public String showWord(String string) { // 53

        Pattern pattern = Pattern.compile("Muestra:(.+)");
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) {
            String msj = matcher.group(1);
            // verifico que haya alguna llamda a una variable :: ::
            pattern = Pattern.compile(":([\\S]+):");
            matcher = pattern.matcher(string);
            List<String> keys = new ArrayList<String>();
            while (matcher.find()) {
                keys.add(matcher.group(1));
            }
            // si no hay llamdas ....
            if (keys.size() == 0) {
                return msj;
            }
            // si hay llamdas pero las key no existen
            AtomicBoolean isMatch = new AtomicBoolean(true);
            keys.forEach(key -> {
                if (!numeros.containsKey(key) && !mensajes.containsKey(key)) {
                    isMatch.set(false);
                }

            });
            if (isMatch.get()) {
                String[] split = msj.split(" ");
                StringBuilder salida = new StringBuilder();
                Arrays.asList(split).forEach(line -> {
                    if (line.matches(":([\\S]+):")) {
                        String newLine = line.replaceAll(":", "");
                        if (numeros.containsKey(newLine)) {
                            salida.append(numeros.get(newLine));
                        } else {
                            salida.append(mensajes.get(newLine));
                        }
                    } else {
                        salida.append(line + " ");
                    }
                });
                return salida.toString();
            }
        }

        return null;
    }

    public Boolean saveData(String string) {
        // Guardamos los numeros...
        Pattern pattern = Pattern.compile("Guarda_numero:.(.+)en:.(.+)");
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) {
            try {
                String key = matcher.group(2);
                Double number = Double.parseDouble(matcher.group(1));
                if (!Character.isDigit(key.charAt(0))) { // no empiecen con numeros
                    if (!numeros.containsKey(key)) {
                        numeros.put(key, number);
                        return true;
                    }
                }
            } catch (Exception e) {
            }
        }
        // Guardamos los Strings...
        pattern = Pattern.compile("Guarda_texto:.(.+)en:.(.+)");
        matcher = pattern.matcher(string);
        if (matcher.matches()) {
            String key = matcher.group(2);
            String texto = matcher.group(1);
            // si no empeiza con una letra
            if (!Character.isDigit(key.charAt(0))) {
                if (!mensajes.containsKey(key)) {
                    mensajes.put(key, texto);
                    return true;
                }
            }
        }

        return false;
    }

}
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Semantic {

    private String textBox;
    private List<String> lines = new ArrayList<String>();
    private List<String> types = new ArrayList<String>();
    private List<String> variables = new ArrayList<String>();
    private List<String> values = new ArrayList<String>();

    private StringBuilder error = new StringBuilder();
    private boolean isError = false;

    public Semantic() {
    }

    public boolean isError() {
        return isError;
    }

    public StringBuilder getError() {
        return error;
    }

    public void setError(StringBuilder error) {
        this.error = error;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    public String getTextBox() {
        return textBox;
    }

    public void setTextBox(String textBox) {
        this.textBox = textBox;
    }

    public void semanticValidation() {
        System.out.println("\n\n");
        System.out.println("----> SEMANTIC <----\n");
        lines.clear();
        types.clear();
        variables.clear();
        values.clear();
        error = new StringBuilder();
        isError = false;
        // No used words
        variableList();
        if (lines.size() > 0) {
            uninitialized();
            duplicate();

        }
        if (!isError) {
            System.out.println("ya no hay errores ...");
            variables.forEach(item -> System.out.println(item));
        }
        System.out.println(error.toString());

    }

    private void variableList() {

        Pattern pattern = Pattern.compile("(Ent.+|Do.+|Flo.+|Car.+|Cad.+|Boo.+|SOBRES.+)");
        Matcher matcher = pattern.matcher(textBox);
        while (matcher.find()) {
            String salida = matcher.group();
            String[] split = salida.split(";");
            Arrays.asList(split).forEach(variable -> {
                if (variable.length() > 2) {
                    lines.add(variable);
                }
            });
        }

    }

    private void uninitialized() {

        lines.forEach(var -> {
            Pattern pattern = Pattern.compile("([A-z]+)[\\s]+([\\S]+)[\\s]+[:][\\s]+([\\S]+)");
            Matcher matcher = pattern.matcher(var);
            if (matcher.find()) {
                types.add(matcher.group(1));
                variables.add(matcher.group(2));
                values.add(matcher.group(3));
            } else {
                if (!var.contains("SOBRES")) {
                    setError(var, " has not been initialized yet");
                }
            }
        });

        // Find var of main
        lines.forEach(var -> {
            if (var.contains("SOBRES")) {
                Pattern pattern = Pattern.compile("(SOBRES)[\\s]+([\\S]+)[\\s]+([\\S]+)");
                Matcher matcher = pattern.matcher(var);
                if (matcher.find()) {
                    types.add(matcher.group(1));
                    values.add(matcher.group(2));
                    variables.add(matcher.group(3));
                }
            }
        });

    }

    private void duplicate() {
        List<String> dupList = new ArrayList<String>();
        for (String var : variables) {
            dupList.add(var);
        }
        List<String> varDup = dupList
                .stream()
                .distinct()
                .collect(Collectors.toList());
        for (String dupVar : varDup) {
            dupList.remove(dupVar);
        }
        dupList.forEach(var -> setError(var, " appears more than once"));
        List<String> noDu = new ArrayList<String>();
        variables.forEach(var -> {
            boolean isMatch = false;
            for (String varDos : dupList) {
                if (var.equals(varDos)) {
                    isMatch = true;
                }
            }
            if (!isMatch) {
                noDu.add(var);
            }
        });

        correctType(noDu);
        notUsed(noDu);

    }

    private void notUsed(List<String> list) {
        // list with no duplicate var
        list.forEach(var -> {
            int count = 0;
            Pattern pattern = Pattern.compile("(" + var + ")");
            Matcher matcher = pattern.matcher(textBox);
            while (matcher.find()) {
                count++;
            }
            if (count < 2) {
                error.append("Semantic error, variable " + var + " was declared but never used " + " \n");
                isError = true;
            }
        });

    }

    private void correctType(List<String> list) {
        AtomicInteger i = new AtomicInteger(0);
        variables.forEach(var -> {
            try {
                for (String item : list) {
                    if (var.equals(item)) {// Estoy sin duplicar ..
                        String type = types.get(i.get());
                        String value = values.get(i.get());
                        String regx = Engine.hashRules.get(type).getRules().get(0);
                        String desc = Engine.hashRules.get(type).getRules().get(1);
                        Pattern pattern = Pattern.compile(regx);
                        Matcher matcher = pattern.matcher(value);
                        if (!matcher.find()) {
                            setError(var, " expect a " + desc + " value");
                        }
                    }
                }
            } catch (Exception e) {
            }

            i.getAndIncrement();

        });

    }

    private void setError(String variable, String msg) {
        isError = true;
        error.append("Semantic error, variable " + variable + msg + " \n");
    }
}

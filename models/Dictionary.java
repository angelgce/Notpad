import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Dictionary {

    private int id;
    private String strLine;
    private String key;
    private List<String> rules = new ArrayList<String>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStrLine() {
        return strLine;
    }

    public void setStrLine(String strLine) {
        this.strLine = strLine;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public void formatLine() {
        String[] split = strLine.split(":");
        key = split[0];
        String[] strRules = split[1].split(" ");
        Arrays.asList(strRules).forEach(item -> rules.add(item));
    }

}

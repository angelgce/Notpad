public class Token {

    private int id;
    private String tkn_id;
    private String lexema;
    private String description;
    private String regx;
    private String CSV;

    public Token() {
    }

    public String getCSV() {
        return CSV;
    }

    public void setCSV(String csv) {
        this.CSV = csv;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTkn_id() {
        return tkn_id;
    }

    public void setTkn_id(String tkn_id) {
        this.tkn_id = tkn_id;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegx() {
        return regx;
    }

    public void parsingCSV() {
        try {
            String[] split = CSV.split(",");
            tkn_id = split[0];
            lexema = split[1];
            description = split[2];
            regx = split[3];
        } catch (Exception e) {
            System.out.println("Error parsingCSV :: " + e.getMessage());
        }
    }

    @Override
    public String toString() {

        return "\nID :: #" + id
                + "\n Token :: " + tkn_id
                + "\n Lexema :: " + lexema
                + "\n Information :: " + description
                + "\n Regx ::" + regx;
    }

    public void setRegx(String regx) {
        this.regx = regx;
    }

}
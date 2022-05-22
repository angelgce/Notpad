import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Syntactic {

    private List<Token> userTokens = new ArrayList<Token>();
    private List<Token> bodyTokens = new ArrayList<Token>();
    private StringBuilder result = new StringBuilder();
    private boolean isError = false;

    public Syntactic() {
    }

    public StringBuilder getResult() {
        return result;
    }

    public void setResult(StringBuilder result) {
        this.result = result;
    }

    public List<Token> getUserTokens() {
        return userTokens;
    }

    public void setUserTokens(List<Token> userTokens) {
        this.userTokens = userTokens;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    public void runParser() {
        result = new StringBuilder();
        isError = false;
        mainRule();
        bodyRule();
        openCloseRule();
        varRule();

        if (isError) {
            int count = result.toString().split("\n").length;
            result.append("Total error(s) :" + count);
        } else {
            result.append("Code executed successfully");
        }

    }

    private void mainRule() {
        // ----| Getting thhe Main rules from dictonary.agce |----
        List<String> syntacticRules = new ArrayList<String>();
        bodyTokens.clear();
        Engine.hashRules.get("Main").getRules().forEach(rule -> {
            if (rule.startsWith("Tkn")) {
                syntacticRules.add(rule);
            } else { // ----| if the rule has another rule ... |----
                Engine.hashRules.get(rule).getRules().forEach(rule2 -> {
                    syntacticRules.add(rule2);
                });
            }
        });
        // ----| MAIN ENGINE |----
        System.out.println("-------------------------------");
        System.out.println("\nMain Syntactic Tokens :: \n");
        AtomicInteger synIndex = new AtomicInteger(0);
        // ----| loop through main rules |----
        syntacticRules.forEach(synToken -> {
            String usrToken = "";
            int usrLine = 0;
            try {
                usrToken = userTokens.get(synIndex.get()).getTkn_id();
                usrLine = userTokens.get(synIndex.get()).getLine();
            } catch (Exception e) {
            }
            // ----| Ignoring token -> CUERPO && last token |----
            if (!synToken.contains("Cuerpo") && synIndex.get() != syntacticRules.size() - 1) {
                if (usrToken.contains(synToken)) {
                    System.out.println("[" + synToken + "]::[" + usrToken + "] -> MATCH <-");
                } else {
                    result.append("syntax error on line " + usrLine + " expected \"" + synToken + "\n");
                    System.out.println(
                            "[" + synToken + "]::[" + usrToken + "] -> ERROR <-");
                    isError = true;
                }
            }
            synIndex.getAndIncrement();
        });
        // ----| body |----

        // ----| last sysToken has to be the last token |----
        String lastSysToken = syntacticRules.get(syntacticRules.size() - 1);
        String lastUsrToken = userTokens.get(userTokens.size() - 1).getTkn_id();
        int lastLine = userTokens.get(userTokens.size() - 1).getLine();
        if (lastSysToken.contains(lastUsrToken)) {
            System.out.println("[" + lastUsrToken + "]::[" + lastSysToken + "] -> MATCH <-");
        } else {
            result.append("syntax error on line " + lastLine + " \"no more tokens expected\" \n");
            System.out.println("syntax error on line " + lastLine + " \"no more tokens expected\"");
            isError = true;
        }
        // ----| Finishing PROCESS |----
        System.out.println("-------------------------------");
    }

    private void openCloseRule() {
        // ----| Verification of open and close symbols |----
        // ----| Creating a counter for each symbole to verify |----
        AtomicInteger[] symbolCount = new AtomicInteger[4]; // { } ( ) = 4
        for (int i = 0; i < symbolCount.length; i++) {
            symbolCount[i] = new AtomicInteger(0);
        }
        // ----| Counting how many times a symbol appear on the users tokens |----
        userTokens.forEach(token -> {
            switch (token.getTkn_id()) {
                case "Tkn_LlaveAbre":
                    symbolCount[0].getAndIncrement();
                    break;
                case "Tkn_LlaveCierra":
                    symbolCount[1].getAndIncrement();
                    break;
                case "Tkn_ParAbre":
                    symbolCount[2].getAndIncrement();
                    break;
                case "Tkn_ParCierra":
                    symbolCount[3].getAndIncrement();
                    break;
            }
        });
        // ----| Engine to write errors on front end |----
        for (int i = 0; i < 2; i++) {
            int value1 = 0, value2 = 0;
            String symbol = "";
            // ----| First Rond { } |----
            if (i == 0) {
                value1 = symbolCount[0].get();
                value2 = symbolCount[1].get();
            } else {// ----| Second Rond ( ) |----
                value1 = symbolCount[2].get();
                value2 = symbolCount[3].get();
            }
            // ----| Engine to find the missing symbol |----
            if (value1 != value2) {
                if (value1 > value2) {
                    if (i == 0) {
                        symbol = "}";
                    } else {
                        symbol = ")";
                    }
                } else {
                    if (i == 0) {
                        symbol = "{";
                    } else {
                        symbol = "(";
                    }
                }
                // ----| Updating result text |----
                result.append("syntax error, missing \"" + symbol + "\n");
                System.out.println("syntax error, missing \"" + symbol);
                isError = true;
            }

        }

    }

    private void bodyRule() {
        // ----| Finding the tokens that are part of the body |----
        // ----| Set how many tokens are in the main before body starts |----
        int tokensBeforeBody = 4;
        AtomicInteger index = new AtomicInteger(0);
        // ----| Creating new array with only the tokens of the body |----
        userTokens.forEach(token -> {
            if (index.get() >= tokensBeforeBody && index.get() < userTokens.size() - 1) {
                bodyTokens.add(token);
            }
            index.getAndIncrement();
        });

    }

    private void varRule() {
        // ----| Getting thhe Main rules from dictonary.agce |----
        List<String> syntacticRules = new ArrayList<String>();
        bodyTokens.clear();
        Engine.hashRules.get("VAR").getRules().forEach(rule -> {
            if (rule.startsWith("Tkn")) {
                syntacticRules.add(rule);
            } else { // ----| if the rule has another rule ... |----
                Engine.hashRules.get(rule).getRules().forEach(rule2 -> {
                    syntacticRules.add(rule2);
                });
            }
        });
        // ----| MAIN ENGINE |----

    }

}

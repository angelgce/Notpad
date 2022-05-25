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
        fillBody();
        openCloseRule();
        bodyRules();

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
                    errorMsg(result, usrLine, synToken);
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
        if (isError) {
            return;
        }
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
                errorMsg(result, symbol);
            }

        }

    }

    private void fillBody() {
        if (isError) {
            return;
        }
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

    private void bodyRules() {
        if (isError) {
            return;
        }
        // ----| Starting Engine for body Tokens |----
        System.out.println("-------------------------------");
        System.out.println("\nBODY Syntactic Tokens :: \n");
        // Missing tokens
        System.out.println("Missing tokens :: ");
        bodyTokens.forEach(token -> {
            System.out.println(token.getTkn_id() + " -> " + token.getLexema() + " -> " + token.getLine());

        });
        // ----| Step 1 |----
        // ----| First word must to be a reserved word (RW)
        // ----| va, if, for, someting...|---
        // ----| Validation if the first token is RW |----
        if (Engine.validationRW(bodyTokens.get(0).getLexema())) {
            String firstToken = bodyTokens.get(0).getTkn_id();
            // ----| Step 2 |----
            // ----| if the first token is RW we call their method |----
            switch (firstToken) {
                case "Tkn_TD":
                    varRules();
                    break;
                case "Tkn_IF":
                    toolsRules("IF");
                    break;
                case "Tkn_For":
                    toolsRules("FOR");
                    break;
                // ----|END PROCESS|----
            }
        } else {
            // ----| Sending error if is n't a RW |----
            if (!bodyTokens.get(0).getTkn_id().equals("Tkn_TD")) {
                errorMsg(result, bodyTokens.get(0).getLine(), "Tkn_TD");
            }
        }

    }

    private void varRules() {
        // ----| Variable Rules |----
        // ----| Step 1 |----
        // ----| Variable must to end with a Tkn_End |---- ;
        System.out.println("\nVar Tokens ::\n");
        List<Token> sentence = new ArrayList<Token>();
        boolean isTokenEnd = false;
        for (Token token : bodyTokens) {
            if (token.getTkn_id().equals("Tkn_End")) {
                isTokenEnd = true;
                break;
            }
        }
        // ----| Step 2 |----
        if (isTokenEnd) {
            // ----| Shorting the bodyTokens list with first sentence |----
            for (Token token : bodyTokens) {
                String tkn_id = token.getTkn_id();
                if (tkn_id.equals("Tkn_End")) {
                    sentence.add(token);
                    break;
                } else {
                    sentence.add(token);
                }
            }
            // ----| Step 3 |----
            HashMap<String, List<String>> myRules = getMapRules("VAR");
            StringBuilder varErrors = new StringBuilder();
            Boolean[] isMatch = new Boolean[myRules.size()];
            String[] rulesKeys = new String[myRules.size()];
            for (int i = 0; i < isMatch.length; i++) {
                isMatch[i] = true;
            }
            // ----| loop through VAR rules |----
            AtomicInteger ruleIndex = new AtomicInteger(0);
            myRules.forEach((keyRule, ruleList) -> {
                System.out.println("\nRule [" + keyRule + "]");
                rulesKeys[ruleIndex.get()] = keyRule;
                int tokenCount = 0;
                try {
                    for (Token token : bodyTokens) {
                        String bodyToken = token.getTkn_id();
                        String rule = ruleList.get(tokenCount);
                        if (bodyToken.contains(rule)) {
                            System.out.println("[" + rule + "] :: [" + bodyToken + "] MATCH");
                        } else {
                            System.out.println("[" + rule + "] :: [" + bodyToken + "] ERROR");
                            errorMsg(varErrors, token.getLine(), rule);
                            isMatch[ruleIndex.get()] = false;
                            break;
                        }
                        tokenCount++;
                    }
                } catch (Exception e) {
                }
                ruleIndex.getAndIncrement();
            });

            // ----| Step 4 |----
            // ----| Validating if there is a match |----
            boolean isAny = false;
            int ruleMatch = 0;
            for (Boolean match : isMatch) {
                if (match) {
                    isAny = true;
                    break;
                }
                ruleMatch++;
            }
            // ----| Sending error if there's not rule matched |----
            if (!isAny) {
                result.append(varErrors);
            } else {
                // ----| Step 5 |----
                // ----| Removing the completed bodyTokens |----
                isError = false;
                List<Token> last = new ArrayList<Token>();
                int usedTokens = myRules.get(rulesKeys[ruleMatch]).size();
                for (int i = usedTokens; i < bodyTokens.size(); i++) {
                    last.add(bodyTokens.get(i));
                }
                bodyTokens = new ArrayList<Token>();
                bodyTokens = last;
                bodyTokens.forEach(item -> System.out.println(item.getTkn_id()));
                // ----| Step 6 |----
                // ----| Calling again |----
                if (bodyTokens.size() > 0) {
                    bodyRules();
                }
            }

        } else {// ----| Step 2 |----
            // ----| Sending error if doesnt end with a Tkn_end |----
            errorMsg(result, "Tkn_End");
        }

    }

    private void toolsRules(String keyRule) {
        // ----| Getting thhe Main rules from dictonary.agce |----
        List<String> ifRules = new ArrayList<String>();
        Engine.hashRules.get(keyRule).getRules().forEach(rule -> {
            ifRules.add(rule);
        });

        // ----| MAIN ENGINE |----
        System.out.println("-------------------------------");
        System.out.println("\nIF Syntactic Tokens :: \n");
        // ----| loop through IF rules |----
        int limit = ifRules.size() - 2;
        for (int i = 0; i < limit; i++) {
            String rule = ifRules.get(i);
            String token = "";
            int line = 0;
            try {
                token = bodyTokens.get(i).getTkn_id();
                line = bodyTokens.get(i).getLine();
            } catch (Exception e) {
            }
            if (token.contains(rule)) {
                System.out.println("[" + rule + "]::[" + token + "] -> MATCH <-");
            } else {
                System.out.println("[" + rule + "]::[" + token + "] -> ERROR <-");
                if (line == 0) {
                    errorMsg(result, rule);
                } else {
                    errorMsg(result, line, rule);
                }
                break;
            }
        }
        // ----| Step 5 |----
        System.out.println("\n\n\n MISSING tokens");
        boolean onDelete = false;
        List<Token> last = new ArrayList<Token>();
        for (int i = limit; i < bodyTokens.size(); i++) {
            String lastToken = "";
            try {
                lastToken = bodyTokens.get(i).getTkn_id();
                if (lastToken.equals("Tkn_LlaveCierra") && !onDelete) {
                    onDelete = true;
                } else {
                    last.add(bodyTokens.get(i));
                }
            } catch (Exception e) {
            }

        }
        // // ----| Removing the completed bodyTokens |----
        bodyTokens = new ArrayList<Token>();
        bodyTokens = last;
        for (Token token : bodyTokens) {
            System.out.println(token.getTkn_id());
        }
        // ----| Calling again |----
        if (bodyTokens.size() > 0) {
            bodyRules();
        }

    }

    private void forRules() {

    }

    private void errorMsg(StringBuilder result, int line, String expect) {
        result.append("syntax error on line " + line + " expected \"" + expect + "\n");
        isError = true;
    }

    private void errorMsg(StringBuilder result, String expect) {
        result.append("syntax error, missing \"" + expect + "\n");
        isError = true;
    }

    private HashMap<String, List<String>> getMapRules(String KEY) {
        // ----| Getting thhe Main rules from dictonary.agce |----
        HashMap<String, List<String>> syntacticRules = new HashMap<String, List<String>>();
        Engine.hashRules.forEach((key, rules) -> {
            if (key.contains(KEY)) {
                List<String> items = new ArrayList<String>();
                rules.getRules().forEach(rule -> {
                    items.add(rule);
                });
                syntacticRules.put(key, items);
            }
        });
        return syntacticRules;
    }

}

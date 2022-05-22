import java.awt.Color;
import java.awt.FileDialog;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Notpad extends JFrame {
    private HashMap<String, JMenuItem> mapItems = new HashMap<String, JMenuItem>();
    private String[] stringFile = { "New", "New Window", "Open", "Save", "Save As", "Exit" };
    private String[] Help = { "About" };
    private JTextArea boxtext = new JTextArea();
    private JTextArea resultBox = new JTextArea();
    private JTextArea resultSintac = new JTextArea();

    private String key;
    private int index = 1;
    private JButton runLex = new JButton();
    private JButton runSin = new JButton();
    private JTextArea lineBox;
    private boolean stopLine = false;

    private List<Token> matchedTokens = new ArrayList<Token>();

    public Notpad() {
        Engine.count += 1;
        index = Engine.count;
        key = "Untitled " + index;
        Engine.listNotpad.put(key, this);
        template();
        lineThread();
        Events();
        this.setVisible(true);

    }

    public int getIndex() {
        return index;
    }

    private void template() {
        this.setSize(800, 600);
        this.setTitle(key);
        ///
        // creo mi main panel
        JPanel mainPanel = new JPanel(null);
        mainPanel.setSize(800, 600);
        this.getContentPane().add(mainPanel);

        // Creo mi panel para el Menu
        JPanel menuPanel = new JPanel(null);
        menuPanel.setBounds(0, 0, 800, 30);
        menuPanel.setBackground(Color.white);
        menuPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        mainPanel.add(menuPanel);

        // Lines TEXTAREA
        lineBox = new JTextArea();
        lineBox.setText("");
        lineBox.setBounds(0, 30, 50, 400);
        lineBox.setFont(lineBox.getFont().deriveFont(12f));
        lineBox.setEditable(false);
        JScrollPane scroll3 = new JScrollPane(lineBox);
        scroll3.setBounds(0, 30, 50, 400);
        scroll3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(scroll3);

        // IN TextAREA
        boxtext = new JTextArea();
        boxtext.setBorder(BorderFactory.createLineBorder(Color.black));
        boxtext.setBounds(55, 30, 450, 400);
        boxtext.setFont(boxtext.getFont().deriveFont(12f));
        JScrollPane scroll = new JScrollPane(boxtext);
        scroll.setBounds(55, 30, 450, 400);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scroll);

        // OUT TextAREA
        resultBox = new JTextArea();
        resultBox.setBorder(BorderFactory.createLineBorder(Color.red));
        resultBox.setBounds(505, 30, 270, 400);
        resultBox.setFont(resultBox.getFont().deriveFont(10f));
        resultBox.setEditable(false);
        JScrollPane scroll2 = new JScrollPane(resultBox);
        scroll2.setBounds(505, 30, 270, 400);
        scroll2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scroll2);

        // SINT TextAREA
        resultSintac = new JTextArea();
        resultSintac.setBorder(BorderFactory.createLineBorder(Color.blue));
        // resultSintac.setBounds(540, 0, 770, 220);
        resultSintac.setBounds(0, 430, 775, 100);
        resultSintac.setFont(resultSintac.getFont().deriveFont(14f));
        resultSintac.setEditable(false);
        JScrollPane scroll4 = new JScrollPane(resultSintac);
        scroll4.setBounds(0, 430, 775, 100);
        scroll4.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scroll4);

        // creare el Menu y submenus
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 800, 30);
        menuPanel.add(menuBar);
        int x = 0;
        for (int i = 0; i < 5; i++) {
            // menu
            JMenu menu = new JMenu(boxName(i));
            menu.setBounds(x, 0, 100, 30);
            menuPanel.add(menu);
            x += 82;
            // item
            try {
                Arrays.stream(optionValues(i)).forEach(item -> {
                    JMenuItem jmItem = new JMenuItem(item);
                    menu.add(jmItem);
                    mapItems.put(item, jmItem);
                });
            } catch (Exception e) {
            }
            // add menu bar
            menuBar.add(menu);
        }
        // botton Lex
        runLex = new JButton();
        runLex.setBounds(500, 10, 100, 130);
        runLex.setText("Lexico");
        runLex.setLayout(null);
        runLex.setForeground((new Color(0, 0, 0)));
        runLex.setBackground((new Color(0, 255, 27)));
        menuBar.add(runLex);

        // botton Sin
        runSin = new JButton();
        runSin.setBounds(610, 145, 100, 130);
        runSin.setText("Sintactico");
        runSin.setLayout(null);
        runSin.setForeground((new Color(0, 0, 0)));
        runSin.setBackground((new Color(0, 255, 27)));
        menuBar.add(runSin);
    }

    private String boxName(int index) {
        switch (index) {
            case 0:
                return "File";
            case 1:
                return "Edit";
            case 2:
                return "Format";
            case 3:
                return "View";
            case 4:
                return "Help";
        }
        return "";
    }

    private String[] optionValues(int index) {
        switch (index) {
            case 0:
                return stringFile;
            case 1:
                return null;
            case 2:
                return null;
            case 3:
                return null;
            case 4:
                return Help;
        }
        return null;

    }

    private void lineThread() {

        Thread hilo = new Thread(new Runnable() {
            String data = "";
            String save = "";

            @Override
            public void run() {
                while (!stopLine) {
                    data = boxtext.getText();
                    if (!save.equals(data)) {
                        save = data;
                        Matcher m = Pattern.compile("\r\n|\r|\n").matcher(data);
                        int lines = 1;
                        while (m.find()) {
                            lines++;
                        }
                        StringBuilder txtOut = new StringBuilder();
                        for (int i = 1; i <= lines; i++) {
                            txtOut.append(i + "\n");
                        }
                        lineBox.setText(txtOut.toString());
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (Exception e) {
                    }
                }

            }
        });
        hilo.start();

    }

    private void Events() {
        try {
            // New window event
            mapItems.get("New").addActionListener(event -> {
                if (boxtext.getText().toString().length() > 0) {
                    int input = JOptionPane.showConfirmDialog(null, "Do you want to close the file?");
                    if (input == 0) {
                        this.setTitle(key);
                        boxtext.setText("");
                    }
                } else {
                    this.setTitle(key);
                    boxtext.setText("");
                }
            });
            // New window event
            mapItems.get("New Window").addActionListener(event -> {
                Notpad newWindow = new Notpad();
            });
            // Open file event
            mapItems.get("Open").addActionListener(event -> {
                FileDialog dialog = new FileDialog((this), "Select File to Open");
                dialog.setMode(FileDialog.LOAD);
                dialog.setVisible(true);
                // dialog.setFile("*.ace");
                String path = dialog.getDirectory() + dialog.getFile();
                System.out.println("path: " + path);

                if (path != null) {
                    StringBuilder txt = new StringBuilder();
                    try {
                        Path file = Paths.get(path);
                        Stream<String> lines = Files.lines(file);
                        lines.forEach(line -> {
                            txt.append(line + "\n");
                        });
                        loadText(txt.toString(), path);
                    } catch (Exception e) {
                        System.out.println("Error loadingo file: " + e.getMessage());
                    }
                }
            });
            // Save
            mapItems.get("Save").addActionListener(event -> {
                if (this.getTitle().contains("Untitled")) {
                    FileDialog dialog = new FileDialog((this), "Select the path to save");
                    dialog.setMode(FileDialog.SAVE);
                    dialog.setFile("*.ace");
                    dialog.setVisible(true);
                    String path = dialog.getDirectory() + dialog.getFile();
                    if (path != null) {
                        try {
                            Files.write(Paths.get(path), boxtext.getText().getBytes());
                            this.setTitle(path);
                        } catch (Exception e) {
                            System.out.println("Error saving file: " + e.getMessage());
                        }
                    }
                } else {
                    try {
                        Files.write(Paths.get(this.getTitle()), boxtext.getText().getBytes());
                    } catch (Exception e) {
                        System.out.println("Error saving file: " + e.getMessage());
                    }
                }

            });
            // Save as... event
            mapItems.get("Save As").addActionListener(event -> {
                FileDialog dialog = new FileDialog((this), "Select the path to save");
                dialog.setMode(FileDialog.SAVE);
                dialog.setFile("*.ace");
                dialog.setVisible(true);
                String path = dialog.getDirectory() + dialog.getFile();
                if (path != null) {
                    try {
                        Files.write(Paths.get(path), boxtext.getText().getBytes());
                        this.setTitle(path);
                    } catch (Exception e) {
                        System.out.println("Error saving file: " + e.getMessage());
                    }
                }
            });
            // Exit event
            mapItems.get("Exit").addActionListener(event -> {
                int input = JOptionPane.showConfirmDialog(null, "Do you want to close the file?");
                if (input == 0) {
                    this.setVisible(false);
                    stopLine = true;
                    Engine.listNotpad.remove(key);
                    Notpad trash = this;
                    trash = null;
                }

            });

            // ----| Lexical Analyzer |----
            runLex.addActionListener(event -> {
                // ----| Starting PROCESS |----
                if (boxtext.getText().length() > 0) {
                    matchedTokens.clear();
                    StringBuilder outTxt = new StringBuilder();
                    AtomicInteger indexLine = new AtomicInteger(1);
                    // ----| User String to lines |----
                    String[] userLines = boxtext.getText().split("\n");
                    // ----| loop through each line |----
                    Arrays.asList(userLines).forEach(xLine -> {
                        // ----| Header information |----
                        // System.out.println("Line :: " + indexLine.get());
                        outTxt.append("\n---> Line #" + indexLine.get() + " <---");
                        outTxt.append("\nString :: " + xLine + "\n");
                        // ----| User Lines to words |----
                        String[] wordLine = xLine.split(" ");
                        // ----| loop through each word |----
                        Arrays.asList(wordLine).forEach(word -> {
                            // ----| Header information |----
                            // System.out.println("word : " + word);
                            // ----|Comparing my word with the token list |----
                            // ----|Loop through each token to find matches |----
                            AtomicBoolean isToken = new AtomicBoolean(false);
                            Engine.tokens.forEach(csvToken -> {
                                Pattern pattern = Pattern.compile(csvToken.getRegx());
                                Matcher matcher = pattern.matcher(word);
                                while (matcher.find()) {
                                    boolean sendInfo = true;
                                    // ----| validation of the reserved words |----
                                    if (csvToken.getId() == 1 &&
                                            Engine.validationRW(matcher.group(0).replaceAll(" ", ""))) {
                                        sendInfo = false;
                                    }
                                    // ----| append information to the final stringnuilder |----
                                    if (sendInfo) {
                                        isToken.set(true);
                                        outTxt.append(csvToken.toString() + "\n");
                                        outTxt.append(" String :: " + matcher.group(0) + "\n");
                                        // ignoring blank space
                                        if (!csvToken.getTkn_id().equals("Tkn_Blank")) {
                                            Token token = new Token();
                                            token.setTkn_id(csvToken.getTkn_id());
                                            token.setLine(indexLine.get());
                                            matchedTokens.add(token);
                                        }
                                    }
                                }
                            });
                            // ----| Validation if there was a match |----
                            if (isToken.get() == false) {
                                outTxt.append(" Thre's not match + \n");
                            }
                        });
                        // ----| Next user Line |----
                        indexLine.getAndIncrement();
                    });
                    // ----| Sending the token information to the front-end |----
                    resultBox.setText(outTxt.toString());
                } else {
                    // ----| Error msg if the textarea is empty |----
                    JOptionPane.showMessageDialog(null, "no data to be analyzed");
                }
                // ----| END PROCESS |----
            });

        } catch (Exception e) {
            System.out.println("Event error: " + e.getMessage());
        }

        // ----| Sintectic Analyzer |----
        runSin.addActionListener(event -> {
            if (boxtext.getText().length() > 0 && !matchedTokens.isEmpty()) {
                // validation of syntactic
                Syntactic syntactic = new Syntactic();
                syntactic.setUserTokens(matchedTokens);
                syntactic.runParser();
                if (syntactic.isError()) {
                    resultSintac.setForeground(Color.RED);
                } else {
                    resultSintac.setForeground(new Color(4, 123, 43));
                }
                resultSintac.setText(syntactic.getResult().toString());

            } else {
                JOptionPane.showMessageDialog(null, "no data to be analyzed");
            }

        });

    }

    private void loadText(String txt, String path) {
        if (boxtext.getText().toString().length() > 0) {
            int input = JOptionPane.showConfirmDialog(null, "Do you want to close the file?");
            if (input == 0) {
                this.setTitle(path);
                boxtext.setText(txt);
            }
        } else {
            this.setTitle(path);
            boxtext.setText(txt);
        }
    }

}
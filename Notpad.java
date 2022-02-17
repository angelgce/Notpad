import java.awt.Color;
import java.awt.FileDialog;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Notpad extends JFrame {
    private HashMap<String, JMenuItem> mapItems = new HashMap<String, JMenuItem>();
    private String[] stringFile = { "New", "New Window", "Open", "Save", "Save As", "Exit" };
    private String[] Help = { "About" };
    private JTextArea boxtext = new JTextArea();
    private String key;
    private int index = 1;

    public Notpad() {
        Engine.count += 1;
        index = Engine.count;
        key = "Untitled " + index;
        Engine.listNotpad.put(key, this);
        this.setSize(800, 500);
        this.setTitle(key);
        ///
        // creo mi main panel
        JPanel mainPanel = new JPanel(null);
        mainPanel.setSize(800, 500);
        this.getContentPane().add(mainPanel);
        // Creo mi panel para el Menu
        JPanel menuPanel = new JPanel(null);
        menuPanel.setBounds(0, 0, 800, 30);
        menuPanel.setBackground(Color.white);
        menuPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        mainPanel.add(menuPanel);
        // Creo mi textArea para escribir
        boxtext = new JTextArea();
        boxtext.setBounds(0, 30, 790, 490);
        mainPanel.add(boxtext);
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
        // cargo los eventos
        Events();
        this.setVisible(true);

    }

    public int getIndex() {
        return index;
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
                    Engine.listNotpad.remove(key);
                    Notpad trash = this;
                    trash = null;
                }

            });

        } catch (Exception e) {
            System.out.println("Event error: " + e.getMessage());
        }

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
package editor;


import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextEditor extends JFrame {
    private final Controller controller;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private JTextArea area;
    private JTextField searchField;
    private JFileChooser chooser;
    private JCheckBox regexCheckBox;
    private final List<Integer> indexes;
    private int caretPosition;
    private boolean useRegex;
    private int len;
    private final String resourcesPath = System.getProperty("user.dir") + File.separator + "Text Editor" + File.separator +"task"
            + File.separator + "resources" + File.separator;

    public TextEditor() {
        controller = new Controller(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        this.setTitle("Text Editor");
        indexes = new ArrayList<>();
        caretPosition = 0;
        len = 0;
        useRegex = false;
        init();
        setVisible(true);

    }

    private void init() {
        System.out.println(System.getProperty("user.dir"));

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField();
        searchField.setName("SearchField");
        searchField.setPreferredSize(new Dimension(WIDTH / 3, HEIGHT / 15));

        JButton saveButton = new JButton();
        saveButton.setName("SaveButton");
        saveButton.setIcon(new ImageIcon(resourcesPath + "save-icon.png"));

        JButton openButton = new JButton();
        openButton.setName("OpenButton");
        openButton.setIcon(new ImageIcon(resourcesPath + "open-file-icon.png"));

        JButton searchButton = new JButton();
        searchButton.setName("StartSearchButton");
        searchButton.setIcon(new ImageIcon(resourcesPath + "search-icon.png"));

        JButton prevButton = new JButton();
        prevButton.setName("PreviousMatchButton");
        prevButton.setIcon(new ImageIcon(resourcesPath + "previous-icon.png"));

        JButton nextButton = new JButton();
        nextButton.setName("NextMatchButton");
        nextButton.setIcon(new ImageIcon(resourcesPath +"next-icon.png"));

        regexCheckBox = new JCheckBox("Use regex");
        regexCheckBox.setName("UseRegExCheckbox");

        menuPanel.add(openButton);
        menuPanel.add(saveButton);
        menuPanel.add(searchField);
        menuPanel.add(searchButton);
        menuPanel.add(prevButton);
        menuPanel.add(nextButton);
        menuPanel.add(regexCheckBox);
        this.add(menuPanel, BorderLayout.NORTH);

        chooser = new JFileChooser(System.getProperty("user.dir") + File.separator + "Text Editor" + File.separator +" task"
                + File.separator, FileSystemView.getFileSystemView());
        chooser.setName("FileChooser");
        this.add(chooser);

        area = new JTextArea();
        area.setName("TextArea");

        area.setFont(new Font("Serif", Font.PLAIN, 16));
        area.getCaret().setSelectionVisible(true);

        JScrollPane scrollText = new JScrollPane(area);
        scrollText.setName("ScrollPane");
        int PADDING = WIDTH / 20;
        scrollText.setPreferredSize(new Dimension(WIDTH - PADDING, HEIGHT - PADDING));
        scrollText.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        this.getContentPane().add(scrollText);

        saveButton.addActionListener(e -> save());
        openButton.addActionListener(e -> open());
        regexCheckBox.addItemListener(e -> useRegex = e.getStateChange() == ItemEvent.SELECTED);

        searchButton.addActionListener(e -> search(searchField.getText()));
        nextButton.addActionListener(e -> searchNext());
        prevButton.addActionListener(e -> searchPrev());

        addMenu();

    }

    private void addMenu() {

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setName("MenuSave");
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setName("MenuOpen");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("MenuExit");
        JMenuItem startSearchMenuItem = new JMenuItem("Start search");
        startSearchMenuItem.setName("MenuStartSearch");
        JMenuItem prevMatchMenuItem = new JMenuItem("Previous match");
        prevMatchMenuItem.setName("MenuPreviousMatch");
        JMenuItem nextMatchMenuItem = new JMenuItem("Next match");
        nextMatchMenuItem.setName("MenuNextMatch");
        JMenuItem useRegexMenuItem = new JMenuItem("Use regular expressions");
        useRegexMenuItem.setName("MenuUseRegExp");

        saveMenuItem.addActionListener(e -> save());
        openMenuItem.addActionListener(e -> open());
        exitMenuItem.addActionListener(e -> dispose());
        startSearchMenuItem.addActionListener(e->search(searchField.getText()));
        prevMatchMenuItem.addActionListener(e->searchPrev());
        nextMatchMenuItem.addActionListener(e->searchNext());
        useRegexMenuItem.addActionListener(e -> regexCheckBox.doClick());

        fileMenu.add(saveMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        searchMenu.add(startSearchMenuItem);
        searchMenu.add(prevMatchMenuItem);
        searchMenu.add(nextMatchMenuItem);
        searchMenu.add(useRegexMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(searchMenu);
        setJMenuBar(menuBar);
    }

    private void open() {
        int t = chooser.showOpenDialog(null);
        if (t == JFileChooser.APPROVE_OPTION) {
            String filename = chooser.getSelectedFile().getAbsolutePath();
            area.setText(controller
                    .load(filename));
        }
    }

    private void save() {
        int t = chooser.showSaveDialog(null);
        if (t == JFileChooser.APPROVE_OPTION) {
            String filename = chooser.getSelectedFile().getAbsolutePath();
            controller
                    .save(filename, area.getText());
        }
    }

    private void search(String textToFind) {
        String text = area.getText();
        if (!indexes.isEmpty()) indexes.clear();
        len = 0;

        if (useRegex) {
            Pattern pattern = Pattern.compile(textToFind);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                int start = matcher.start();
                len = matcher.end()-start;
                indexes.add(start);
            }
        }

        else {
            int wordLength = 0;
            int index = 0;
            while (index != -1) {
                index = text.indexOf(textToFind, index + wordLength);
                if (index != -1) {
                    indexes.add(index);
                }
                wordLength = textToFind.length();
            }
            len = textToFind.length();
        }
        moveCaret(0);
    }

    private void searchNext() {
        int index = indexes.indexOf(caretPosition) + 1;
        if (index >= indexes.size()) {
            index = 0;
        }
        moveCaret(index);
    }

    private void searchPrev() {
        int index = indexes.indexOf(caretPosition) - 1;
        if (index < 0) {
            index = indexes.size() - 1;
        }
        moveCaret(index);
    }

    private void moveCaret(int index) {
        if (!indexes.isEmpty()) {
            caretPosition = indexes.get(index);
            area.setCaretPosition(caretPosition + len);
            area.select(caretPosition, caretPosition + len);
            area.grabFocus();
        }
    }

}

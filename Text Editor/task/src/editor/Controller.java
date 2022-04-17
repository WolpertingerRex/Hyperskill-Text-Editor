package editor;

public class Controller {
    private FileManager fileManager;
    private TextEditor textEditor;

    public Controller(TextEditor textEditor) {
        this.textEditor = textEditor;
        fileManager = new FileManager();
    }

    public void save(String filename, String text){
        fileManager.save(filename, text);
    }

    public String load(String filename){
        return fileManager.load(filename);
    }

}

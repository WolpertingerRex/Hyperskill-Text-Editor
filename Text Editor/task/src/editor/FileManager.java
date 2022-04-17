package editor;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public void save(String filename, String content) {
        try {
            Files.write(Paths.get(filename), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String load(String filename) {
        try {
            return Files.readString(Paths.get(filename));
        } catch (IOException e) {
            return "";
        }
    }
}

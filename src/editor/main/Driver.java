package editor.main;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * Created by brandon on 8/2/16.
 */
public class Driver {

    public static Editor.Language[] languages = {
            new LanguagePython(),
    };

    public static String file;
    public static Editor.Language lang;

    public static void main(String[] args) {
        if (args.length > 0) {
            file = args[0];
        } else {
            System.out.println("I need a file!");
            System.out.println("Example:\n\teditor file.py");
            lang = languages[0];
            file = "test." + lang.getExtension();
        }
        String extension = "";
        int x = file.lastIndexOf('.');
        if (x > 0) {
            extension = file.substring(x + 1);
        }
        lang = languages[0];
        for (int i = 0; i < languages.length; i++) {
            if (extension.equals(languages[i].getExtension())) {
                lang = languages[i];
            }
        }
        new Editor(lang, loadNodes());
    }

    public static void saveNodes(LinkedList<Node> nodes) {
        try {
            Files.deleteIfExists(Paths.get(file));
            Files.deleteIfExists(Paths.get(file+"+"));
            String code = "";
            for (Node node : nodes) {
                code += node.parse() + "\n";
            }
            Files.write(Paths.get(file), code.getBytes());
            System.out.println(code);

            FileOutputStream stringOut = new FileOutputStream(file+"+");
            ObjectOutputStream out = new ObjectOutputStream(stringOut);
            out.writeObject(nodes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LinkedList<Node> loadNodes() {
        if (Files.exists(Paths.get(file + "+"))) {
            try {
                FileInputStream stringIn = new FileInputStream(file+"+");
                ObjectInputStream in = new ObjectInputStream(stringIn);
                LinkedList<Node> nodes = (LinkedList<Node>) in.readObject();
                in.close();
                return nodes;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}

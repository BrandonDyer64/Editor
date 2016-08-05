package editor.main.languages;

import editor.main.Editor;
import editor.main.Node;

import java.awt.*;
import java.io.Serializable;
import java.util.LinkedList;

import static editor.main.Editor.NodeType;

public class LanguageJava implements Serializable, Editor.Language {

    public NodeType[] nodeTypes = new NodeType[]{
            new NodeType() {

                {
                    name = "Code";
                    outputNum = 0;
                    isTextEditor = true;
                    syntaxColor = Color.CYAN;
                    width = 512 - 128;
                    height = 128 - 32;
                }

                public String parse(String meta, Node[] outputNodes) {
                    return "\n" + meta + "\n";
                }
            },
            new NodeType() {

                {
                    name = "Block";
                    outputNum = 1;
                    isTextEditor = true;
                    syntaxColor = Color.YELLOW;
                    width = 512 - 128;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    if (outputNodes[0] != null) {
                        data += meta + "\n" + tab(outputNodes[0].parse()) + "\n";
                    }
                    return data;
                }
            },
            new NodeType() {

                {
                    name = "Method";
                    outputNum = 1;
                    isTextEditor = true;
                    syntaxColor = Color.GREEN;
                    width = 512 - 128;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    if (outputNodes[0] != null) {
                        data += meta + "\n" + tab(outputNodes[0].parse()) + "\n";
                    }
                    return data;
                }
            },
            new NodeType() {

                {
                    name = "Interface";
                    outputNum = 1;
                    isTextEditor = true;
                    syntaxColor = Color.ORANGE;
                    width = 512 - 128;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    if (outputNodes[0] != null) {
                        data += "public interface " + meta + "\n" + tab(outputNodes[0].parse()) + "\n";
                    }
                    return data;
                }
            },
            new NodeType() {

                {
                    name = "Class";
                    outputNum = 1;
                    isTextEditor = true;
                    syntaxColor = Color.BLACK;
                    width = 512 - 128;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    if (outputNodes[0] != null) {
                        data += "public class " + meta + "\n" + tab(outputNodes[0].parse()) + "\n";
                    }
                    return data;
                }
            },
            new NodeType() {

                {
                    name = "SubClass";
                    outputNum = 1;
                    isTextEditor = true;
                    syntaxColor = Color.BLACK;
                    width = 512 - 128;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    if (outputNodes[0] != null) {
                        data += "public static class " + meta + "\n" + tab(outputNodes[0].parse()) + "\n";
                    }
                    return data;
                }
            },
            new NodeType() {

                {
                    name = "Split 2";
                    outputNum = 2;
                    isTextEditor = true;
                    syntaxColor = Color.MAGENTA;
                    width = 128;
                    height = 128;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    for (int i = 0; i < outputNum; i++) {
                        if (outputNodes[i] != null)
                            data += "\n" + outputNodes[i].parse();
                    }
                    return data;
                }
            },
            new NodeType() {

                {
                    name = "Split 4";
                    outputNum = 4;
                    isTextEditor = true;
                    syntaxColor = Color.MAGENTA;
                    width = 128;
                    height = 256;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    for (int i = 0; i < outputNum; i++) {
                        if (outputNodes[i] != null)
                            data += "\n" + outputNodes[i].parse();
                    }
                    return data;
                }
            },
            new NodeType() {

                {
                    name = "Split 8";
                    outputNum = 8;
                    isTextEditor = true;
                    syntaxColor = Color.MAGENTA;
                    width = 128;
                    height = 512;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    for (int i = 0; i < outputNum; i++) {
                        if (outputNodes[i] != null)
                            data += "\n" + outputNodes[i].parse();
                    }
                    return data;
                }
            },
    };

    public static String tab(String code) {
        return "{" + code + "}";
    }

    public NodeType[] getNodeTypes() {
        return nodeTypes;
    }

    public String getName() {
        return "Java";
    }

    @Override
    public String getExtension() {
        return "java";
    }

    @Override
    public String getComment() {
        return "//";
    }

    public LinkedList<Node> loadFromSource(String source) {
        LinkedList<Node> nodes = new LinkedList<>();

        return nodes;
    }

    public class TNode {
        public NodeType type;

    }

}


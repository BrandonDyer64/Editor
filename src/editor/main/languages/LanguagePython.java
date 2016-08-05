package editor.main.languages;

import editor.main.Editor;
import editor.main.Node;

import java.awt.*;
import java.io.Serializable;
import java.util.LinkedList;

import static editor.main.Editor.NodeType;

public class LanguagePython implements Serializable, Editor.Language {

    public Editor.NodeType[] nodeTypes = new Editor.NodeType[]{
            new Editor.NodeType() {

                {
                    name = "Code";
                    outputNum = 0;
                    isTextEditor = true;
                    syntaxColor = Color.CYAN;
                    width = 512 - 128;
                    height = 128 - 32;
                }

                public String parse(String meta, Node[] outputNodes) {
                    return meta;
                }
            },
            new Editor.NodeType() {

                {
                    name = "If";
                    outputNum = 2;
                    isTextEditor = true;
                    syntaxColor = Color.ORANGE;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    if (outputNodes[0] != null) {
                        data += "if " + meta + ":\n";
                        data += tab(outputNodes[0].parse()) + "\n";
                        if (outputNodes[1] != null) {
                            data += "else:\n";
                            data += tab(outputNodes[1].parse()) + "\n";
                        }
                    }
                    return data;
                }
            },
            new Editor.NodeType() {

                {
                    name = "Function";
                    outputNum = 1;
                    isTextEditor = true;
                    syntaxColor = Color.GREEN;
                    width = 512 - 128;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    if (outputNodes[0] != null) {
                        data += "def " + meta + ":\n" + tab(outputNodes[0].parse()) + "\n";
                    }
                    return data;
                }
            },
            new Editor.NodeType() {

                {
                    name = "For";
                    outputNum = 1;
                    isTextEditor = true;
                    syntaxColor = Color.PINK;
                    width = 512 - 128;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    if (outputNodes[0] != null) {
                        data += "for " + meta + ":\n" + tab(outputNodes[0].parse()) + "\n";
                    }
                    return data;
                }
            },
            new Editor.NodeType() {

                {
                    name = "While";
                    outputNum = 1;
                    isTextEditor = true;
                    syntaxColor = Color.PINK;
                    width = 512 - 128;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    if (outputNodes[0] != null) {
                        data += "while " + meta + ":\n" + tab(outputNodes[0].parse()) + "\n";
                    }
                    return data;
                }
            },
            new Editor.NodeType() {

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
                        data += "class " + meta + ":\n" + tab(outputNodes[0].parse()) + "\n";
                    }
                    return data;
                }
            },
            new Editor.NodeType() {

                {
                    name = "Custom";
                    outputNum = 1;
                    isTextEditor = true;
                    syntaxColor = Color.YELLOW;
                    width = 512 - 128;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    if (outputNodes[0] != null) {
                        data += meta + ":\n" + tab(outputNodes[0].parse()) + "\n";
                    }
                    return data;
                }
            },
            new Editor.NodeType() {

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
            new Editor.NodeType() {

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
            new Editor.NodeType() {

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
        return "\t" + code.replaceAll("\n", "\n\t");
    }

    public Editor.NodeType[] getNodeTypes() {
        return nodeTypes;
    }

    public String getName() {
        return "Python";
    }

    @Override
    public String getExtension() {
        return "py";
    }

    @Override
    public String getComment() {
        return "#";
    }

    public LinkedList<Node> loadFromSource(String source) {
        LinkedList<Node> nodes = new LinkedList<>();

        return nodes;
    }

    public class TNode {
        public Editor.NodeType type;

    }

}


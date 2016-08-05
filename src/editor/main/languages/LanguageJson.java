package editor.main.languages;

import editor.main.Editor;
import editor.main.Node;

import java.awt.*;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by brandon on 8/4/16.
 */
public class LanguageJson implements Editor.Language, Serializable {

    public static Editor.NodeType[] nodeTypes = new Editor.NodeType[]{
            new Editor.NodeType() {

                {
                    name = "Value";
                    outputNum = 0;
                    isTextEditor = true;
                    syntaxColor = Color.CYAN;
                    width = 512 - 128;
                }

                public String parse(String meta, Node[] outputNodes) {
                    return meta + "\n";
                }
            },
            new Editor.NodeType() {

                {
                    name = "Array";
                    outputNum = 1;
                    isTextEditor = true;
                    syntaxColor = Color.YELLOW;
                    width = 64;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    if (outputNodes[0] != null) {
                        data += "[\n" + outputNodes[0].parse() + "\n]\n";
                    }
                    return data;
                }
            },
            new Editor.NodeType() {

                {
                    name = "Field";
                    outputNum = 1;
                    isTextEditor = true;
                    syntaxColor = Color.GREEN;
                    width = 512 - 128;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    if (outputNodes[0] != null) {
                        data += "\n\"" + meta + "\":" + outputNodes[0].parse() + "\n";
                    }
                    return data;
                }
            },
            new Editor.NodeType() {

                {
                    name = "Object";
                    outputNum = 1;
                    isTextEditor = true;
                    syntaxColor = Color.BLACK;
                    width = 74;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    if (outputNodes[0] != null) {
                        data += tab(outputNodes[0].parse()) + "\n";
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
                    width = 128 - 32;
                    height = 128;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    for (int i = 0; i < outputNum; i++) {
                        if (outputNodes[i] != null) {
                            String add = outputNodes[i].parse().trim();
                            if (add.length() > 0)
                                data += "\n" + add + (add.charAt(add.length() - 1) != ',' ? "," : "");
                        }
                    }
                    return data.substring(0, data.length() - 1) + "\n";
                }
            },
            new Editor.NodeType() {

                {
                    name = "Split 4";
                    outputNum = 4;
                    isTextEditor = true;
                    syntaxColor = Color.MAGENTA;
                    width = 128 - 32;
                    height = 256;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    for (int i = 0; i < outputNum; i++) {
                        if (outputNodes[i] != null) {
                            String add = outputNodes[i].parse().trim();
                            if (add.length() > 0)
                                data += "\n" + add + (add.charAt(add.length() - 1) != ',' ? "," : "");
                        }
                    }
                    return data.substring(0, data.length() - 1) + "\n";
                }
            },
            new Editor.NodeType() {

                {
                    name = "Split 8";
                    outputNum = 8;
                    isTextEditor = true;
                    syntaxColor = Color.MAGENTA;
                    width = 128 - 32;
                    height = 256;
                }

                public String parse(String meta, Node[] outputNodes) {
                    String data = "";
                    for (int i = 0; i < outputNum; i++) {
                        if (outputNodes[i] != null) {
                            String add = outputNodes[i].parse().trim();
                            if (add.length() > 0)
                                data += "\n" + add + (add.charAt(add.length() - 1) != ',' ? "," : "");
                        }
                    }
                    return data.substring(0, data.length() - 1) + "\n";
                }
            },
    };

    public static String tab(String code) {
        return "{" + code + "}";
    }

    @Override
    public Editor.NodeType[] getNodeTypes() {
        return nodeTypes;
    }

    @Override
    public String getName() {
        return "JSON";
    }

    @Override
    public String getExtension() {
        return "json";
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public LinkedList<Node> loadFromSource(String source) {
        return null;
    }
}

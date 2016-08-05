package editor.main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by brandon on 8/2/16.
 */
public class Node implements Serializable {
    public static final int WIDTH = 256, HEIGHT = 128;
    public Rectangle bound;
    public Editor.NodeType type;
    public String meta;
    public Node[] outputNodes;
    public int curserLoc = 0;
    public char curserChar = 0;
    public boolean showCurser = true;
    public double curserTime = 0;
    public int activeOut = -1;

    public Node(Rectangle bound, Editor.NodeType type, String meta) {
        this.bound = bound;
        this.type = type;
        outputNodes = new Node[type.outputNum];
        System.out.println("Created Node of type " + type.name + " with " + type.outputNum + " outputs");
        this.meta = meta;
    }

    public Node(int x, int y, int w, int h, Editor.NodeType type, String meta) {
        this(new Rectangle(x, y, w, h), type, meta);
    }

    public Node(Rectangle bound, Editor.NodeType type) {
        this(bound, type, "");
    }

    public Node(int x, int y, int w, int h, Editor.NodeType type) {
        this(x, y, w, h, type, "");
    }

    public void move(int x, int y) {
        bound.x += x;
        bound.y += y;
        for (int i = 0; i < outputNodes.length; i++) {
            if (outputNodes[i] != null) {
                outputNodes[i].move(x, y);
            }
        }
    }

    public String parse() {
        if (Driver.lang.getComment() == null) {
            return type.parse(meta.trim(), outputNodes);
        } else {
            return (Editor.saveDebug ? Driver.lang.getComment() + "@$" + Editor.getNodes().indexOf(this) + "\n" : "") + type.parse(meta.trim(), outputNodes);
        }
    }

    public Point[] getOutputPoints() {
        Point[] points = new Point[outputNodes.length];
        for (int i = 0; i < outputNodes.length; i++) {
            points[i] = new Point(bound.x + bound.width - Editor.OVAL_SIZE, bound.y + (bound.height / (points.length + 1)) * (i + 1));
        }
        return points;
    }

    public Point getInputPoint() {
        return new Point(bound.x + Editor.OVAL_SIZE, bound.y + bound.height / 3);
    }

    public LinkedList<Node> getSubNodes(LinkedList<Node> nodes) {
        nodes.add(this);
        for (Node node : outputNodes) {
            if (node != null)
                node.getSubNodes(nodes);
        }
        return nodes;
    }

    public LinkedList<Node> getSubNodes() {
        return getSubNodes(new LinkedList<Node>());
    }

    public void moveToTop(Editor editor) {
        LinkedList<Node> nodes = editor.getNodes();
        for (Node node : nodes) {
            for (int i = 0; i < node.outputNodes.length; i++) {
                if (node.outputNodes[i] == this) {
                    node.outputNodes[i] = null;
                }
            }
        }
        if (!editor.topNodes.contains(this)) {
            editor.topNodes.add(this);
        }
    }

    public void render(Graphics2D g, Point offset, double delta) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(bound.x - offset.x, bound.y - offset.y, bound.width, bound.height);
        if (Editor.nodeToDrag == this) {
            g.setColor(Color.WHITE);
            curserTime += delta;
            if (curserTime > 0.5) {
                showCurser ^= true;
                curserTime -= 0.5;
            }
        } else {
            g.setColor(type.syntaxColor);
            showCurser = true;
        }
        if (meta.length() > 0) {
            char[] charray = meta.toCharArray();
            if (showCurser) {
                charray[curserLoc] = curserChar;
            } else {
                if (curserChar == '\n') {
                    charray[curserLoc] = '\n';
                } else if (curserChar != '_') {
                    charray[curserLoc] = '_';
                } else {
                    charray[curserLoc] = '|';
                }
            }
            meta = String.valueOf(charray);
        }
        g.drawRect(bound.x - offset.x, bound.y - offset.y, bound.width, bound.height);
        g.drawLine(bound.x - offset.x, bound.y + Editor.HEADER_SIZE - offset.y, bound.x + bound.width - offset.x, bound.y + Editor.HEADER_SIZE - offset.y);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, Editor.HEADER_SIZE / 2));
        g.setColor(type.syntaxColor);
        g.drawString(type.name, bound.x - offset.x + Editor.HEADER_SIZE * (1f / 3f), bound.y + Editor.HEADER_SIZE * (2f / 3f) - offset.y);
        g.setColor(Color.LIGHT_GRAY);
        String[] metas = meta.split("\n");
        if (Editor.zoom >= 0.5) {
            for (int i = 0; i < metas.length; i++) {
                g.drawString(metas[i], bound.x - offset.x + Editor.HEADER_SIZE * (1f / 3f), bound.y + Editor.HEADER_SIZE * (5f / 3f) - offset.y + i * (Editor.HEADER_SIZE * (2f / 3f)));
            }
        }
        Point[] outputPoints = getOutputPoints();
        Point myInputPoint = getInputPoint();
        g.setColor(Color.WHITE);
        g.fillOval(myInputPoint.x - Editor.OVAL_SIZE - offset.x, myInputPoint.y - Editor.OVAL_SIZE - offset.y, Editor.OVAL_SIZE * 2, Editor.OVAL_SIZE * 2);
        for (int i = 0; i < outputNodes.length; i++) {
            Node node = outputNodes[i];
            Point outputPoint = outputPoints[i];
            if (i == activeOut) {
                g.setColor(Color.ORANGE);
            } else {
                g.setColor(Color.WHITE);
            }
            g.fillOval(outputPoint.x - Editor.OVAL_SIZE - offset.x, outputPoint.y - Editor.OVAL_SIZE - offset.y, Editor.OVAL_SIZE * 2, Editor.OVAL_SIZE * 2);
            if (node == null)
                continue;
            Point inputPoint = node.getInputPoint();
            g.setColor(Color.GRAY);
            g.drawLine(outputPoint.x - offset.x, outputPoint.y - offset.y, inputPoint.x - offset.x, inputPoint.y - offset.y);
            if (i == activeOut) {
                g.setColor(Color.ORANGE);
            } else {
                g.setColor(Color.WHITE);
            }
            g.fillOval(outputPoint.x - Editor.OVAL_SIZE - offset.x, outputPoint.y - Editor.OVAL_SIZE - offset.y, Editor.OVAL_SIZE * 2, Editor.OVAL_SIZE * 2);
            node.render(g, offset, delta);
        }
    }

    public void keyTyped(KeyEvent keyEvent) {
        if (Editor.zoom < 0.5) {
            return;
        }
        if (keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE && curserLoc > 0) {
            char[] charray = meta.toCharArray();
            charray[curserLoc] = curserChar;
            charray[curserLoc - 1] = '\r';
            meta = String.valueOf(charray);
            meta = meta.replace("\r", "");
            curserLoc--;
            curserChar = meta.charAt(curserLoc);
            showCurser = false;
            curserTime = 0;
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_DELETE) {
            char[] charray = meta.toCharArray();
            charray[curserLoc] = '\r';
            meta = String.valueOf(charray);
            meta = meta.replace("\r", "");
            if (curserLoc >= meta.length()) {
                curserLoc--;
            }
            curserChar = meta.charAt(curserLoc);
            showCurser = false;
            curserTime = 0;
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT && curserLoc > 0) {
            char[] charray = meta.toCharArray();
            charray[curserLoc] = curserChar;
            meta = String.valueOf(charray);
            curserLoc--;
            curserChar = meta.charAt(curserLoc);
            showCurser = false;
            curserTime = 0;
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT && curserLoc < meta.length()) {
            char[] charray = meta.toCharArray();
            charray[curserLoc] = curserChar;
            meta = String.valueOf(charray);
            if (curserLoc < meta.length() - 1)
                curserLoc++;
            curserChar = meta.charAt(curserLoc);
            showCurser = false;
            curserTime = 0;
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER && curserLoc > 0) {
            if (meta == "") {
                meta = " ";
            }
            meta = meta.substring(0, curserLoc) + "\n" + meta.substring(curserLoc);
            curserLoc++;
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_SHIFT) {
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
        } else {
            if (meta == "") {
                meta = " ";
            }
            if (meta.charAt(meta.length() - 1) != ' ') {
                meta += " ";
            }
            if (meta.length() < 2 || meta.charAt(meta.length() - 2) != ' ') {
                meta += " ";
            }
            meta = meta.substring(0, curserLoc) + keyEvent.getKeyChar() + meta.substring(curserLoc);
            curserLoc++;
        }
    }
}

package editor.main;

import java.awt.event.*;
import java.io.Serializable;
import java.lang.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class Editor implements Serializable, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

    public Canvas canvas;
    public Language language;
    public LinkedList<Node> topNodes = new LinkedList<Node>();
    public Point viewportOffset = new Point(0, 0);
    public Point mouse = new Point(0, 0);
    public Menu menu = null;
    public boolean onClick = false;
    public MouseEvent mouseEvent;
    public Node deletedNode = null;

    public static final int
            HEADER_SIZE = 30,
            OVAL_SIZE = 4,
            RESCALE_SPEED = 12;

    public Editor(Language language) {
        this.language = language;
        System.out.println("Using language " + language.getName() + " with " + language.getNodeTypes().length + " node types.");
        JFrame frame = new JFrame("Editor");
        frame.add(canvas = new Canvas());
        canvas.addKeyListener(this);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        NodeType type = language.getNodeTypes()[0];
        Node myNode = new Node(128, 128, type.width, type.height, type);
        topNodes.add(myNode);
        lastNodeToDrag = myNode;

        System.out.println(myNode.parse());

        new Thread(new Runnable() {
            @Override
            public void run() {
                long oldTime = System.nanoTime();
                while (true) {
                    long newTime = System.nanoTime();
                    double delta = (newTime - oldTime) / 1000000000d;
                    oldTime = newTime;
                    try {
                        render(delta);
                        Thread.sleep(15);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        new Editor(new LanguagePython());
    }

    public int i = 0;

    public void render(double delta) {
        i++;
        BufferStrategy bs = canvas.getBufferStrategy();
        if (bs == null) {
            System.out.println("BufferStrategy not running.\nStarting BufferStrategy.");
            canvas.createBufferStrategy(3);
            return;
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        g.scale(1, 1);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        g.scale(zoom, zoom);
        for (Node node : topNodes) {
            node.render(g, viewportOffset, delta);
        }

        if (menu != null) {
            menu.render(g);
        }

        g.dispose();
        bs.show();
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    public static Node nodeToDrag, lastNodeToDrag;

    public static Node nodeWithActiveOut;

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        mouse.x = (int) (mouseEvent.getX() / zoom);
        mouse.y = (int) (mouseEvent.getY() / zoom);
        if (menu != null) {
            int add = menu.get(mouse);
            if (add != -1) {
                NodeType nodeType = language.getNodeTypes()[add];
                Node node = new Node(mouse.x + viewportOffset.x, mouse.y + viewportOffset.y, nodeType.width, nodeType.height, nodeType);
                topNodes.add(node);
                nodeToDrag = node;
            }
            menu = null;
            return;
        }
        if (nodeToDrag != null) {
            lastNodeToDrag = nodeToDrag;
        }
        nodeToDrag = null;
        LinkedList<Node> nodes = getNodes();
        for (Node node : nodes) {
            if (node.bound.contains(mouse.x + viewportOffset.x, mouse.y + viewportOffset.y)) {
                nodeToDrag = node;
                if (Math.sqrt(Math.pow(mouse.x - node.getInputPoint().x + viewportOffset.x, 2) + Math.pow(mouse.y - node.getInputPoint().y + viewportOffset.y, 2)) < OVAL_SIZE * 2) {
                    node.moveToTop(this);
                    if (nodeWithActiveOut != null && nodeWithActiveOut != node) {
                        topNodes.remove(node);
                        if (nodeWithActiveOut.outputNodes[nodeWithActiveOut.activeOut] != null)
                            nodeWithActiveOut.outputNodes[nodeWithActiveOut.activeOut].moveToTop(this);
                        nodeWithActiveOut.outputNodes[nodeWithActiveOut.activeOut] = node;
                        nodeWithActiveOut.activeOut = -1;
                        nodeWithActiveOut = null;
                    }
                }
                node.activeOut = -1;
                nodeWithActiveOut = null;
                for (int i = 0; i < node.outputNodes.length; i++) {
                    if (Math.sqrt(Math.pow(mouse.x - node.getOutputPoints()[i].x + viewportOffset.x, 2) + Math.pow(mouse.y - node.getOutputPoints()[i].y + viewportOffset.y, 2)) < OVAL_SIZE * 2) {
                        node.activeOut = i;
                        nodeWithActiveOut = node;
                    }
                }
                return;
            }
        }
        if (nodeWithActiveOut != null) {
            nodeWithActiveOut.activeOut = -1;
            nodeWithActiveOut = null;
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        if (nodeToDrag != null) {
            nodeToDrag.bound.x += (int) (mouseEvent.getX() / zoom) - mouse.x;
            nodeToDrag.bound.y += (int) (mouseEvent.getY() / zoom) - mouse.y;
        } else {
            viewportOffset.x -= (int) (mouseEvent.getX() / zoom) - mouse.x;
            viewportOffset.y -= (int) (mouseEvent.getY() / zoom) - mouse.y;
        }
        mouse.x = (int) (mouseEvent.getX() / zoom);
        mouse.y = (int) (mouseEvent.getY() / zoom);
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        mouse.x = (int) (mouseEvent.getX() / zoom);
        mouse.y = (int) (mouseEvent.getY() / zoom);
    }

    public LinkedList<Node> getNodes() {
        LinkedList<Node> nodes = new LinkedList<Node>();
        for (Node node : topNodes) {
            node.getSubNodes(nodes);
        }
        return nodes;
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            if (nodeToDrag != null) {
                nodeToDrag.keyTyped(keyEvent);
            } else {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_I:
                        nodeToDrag = lastNodeToDrag;
                        break;
                    case KeyEvent.VK_W:
                        for (Node node : topNodes) {
                            System.out.println(node.parse());
                        }
                        break;
                    case KeyEvent.VK_A:
                        menu = new Menu() {
                            {
                                options = new String[language.getNodeTypes().length];
                                for (int i = 0; i < options.length; i++) {
                                    options[i] = language.getNodeTypes()[i].name;
                                }
                                x = mouse.x;
                                y = mouse.y;
                            }
                        };
                        break;
                    case KeyEvent.VK_Z:
                        if (deletedNode != null) {
                            deletedNode.moveToTop(this);
                            deletedNode = null;
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        lastNodeToDrag.moveToTop(this);
                        topNodes.remove(lastNodeToDrag);
                        deletedNode = lastNodeToDrag;
                        lastNodeToDrag = null;
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (lastNodeToDrag != null)
                            lastNodeToDrag.bound.width += RESCALE_SPEED;
                        break;
                    case KeyEvent.VK_LEFT:
                        if (lastNodeToDrag != null)
                            lastNodeToDrag.bound.width -= RESCALE_SPEED;
                        break;
                    case KeyEvent.VK_UP:
                        if (lastNodeToDrag != null)
                            lastNodeToDrag.bound.height -= RESCALE_SPEED;
                        break;
                    case KeyEvent.VK_DOWN:
                        if (lastNodeToDrag != null)
                            lastNodeToDrag.bound.height += RESCALE_SPEED;
                        break;
                    default:
                        System.out.println("UNKNOWN KEY BINDING!!!");
                }
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (nodeToDrag != null)
                    lastNodeToDrag = nodeToDrag;
                nodeToDrag = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    public static float zoom = 1;

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        float oldZoom = zoom;
        zoom *= Math.pow(2, -mouseWheelEvent.getWheelRotation() / 5f);
        viewportOffset.x += (int) (((canvas.getWidth() / 100d)) / (zoom - oldZoom));
        viewportOffset.y += (int) (((canvas.getHeight() / 100d)) / (zoom - oldZoom));
    }

    public static class Node {
        public static final int WIDTH = 256, HEIGHT = 128;
        public Rectangle bound;
        public NodeType type;
        public String meta;
        public Node[] outputNodes;
        public int curserLoc = 0;
        public char curserChar = 0;
        public boolean showCurser = true;
        public double curserTime = 0;
        public int activeOut = -1;

        public Node(Rectangle bound, NodeType type, String meta) {
            this.bound = bound;
            this.type = type;
            outputNodes = new Node[type.outputNum];
            System.out.println("Created Node of type " + type.name + " with " + type.outputNum + " outputs");
            this.meta = meta;
        }

        public Node(int x, int y, int w, int h, NodeType type, String meta) {
            this(new Rectangle(x, y, w, h), type, meta);
        }

        public Node(Rectangle bound, NodeType type) {
            this(bound, type, "");
        }

        public Node(int x, int y, int w, int h, NodeType type) {
            this(x, y, w, h, type, "");
        }

        public String parse() {
            return type.parse(meta.trim(), outputNodes);
        }

        public Point[] getOutputPoints() {
            Point[] points = new Point[outputNodes.length];
            for (int i = 0; i < outputNodes.length; i++) {
                points[i] = new Point(bound.x + bound.width - OVAL_SIZE, bound.y + (bound.height / (points.length + 1)) * (i + 1));
            }
            return points;
        }

        public Point getInputPoint() {
            return new Point(bound.x + OVAL_SIZE, bound.y + bound.height / 3);
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
            if (nodeToDrag == this) {
                g.setColor(Color.WHITE);
                curserTime += delta;
                if (curserTime > 0.5) {
                    showCurser ^= true;
                    curserTime -= 0.5;
                }
            } else {
                g.setColor(Color.LIGHT_GRAY);
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
            g.drawLine(bound.x - offset.x, bound.y + HEADER_SIZE - offset.y, bound.x + bound.width - offset.x, bound.y + HEADER_SIZE - offset.y);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, HEADER_SIZE / 2));
            g.setColor(type.syntaxColor);
            g.drawString(type.name, bound.x - offset.x + HEADER_SIZE * (1f / 3f), bound.y + HEADER_SIZE * (2f / 3f) - offset.y);
            g.setColor(Color.LIGHT_GRAY);
            String[] metas = meta.split("\n");
            for (int i = 0; i < metas.length; i++) {
                g.drawString(metas[i], bound.x - offset.x + HEADER_SIZE * (1f / 3f), bound.y + HEADER_SIZE * (5f / 3f) - offset.y + i * (HEADER_SIZE * (2f / 3f)));
            }
            Point[] outputPoints = getOutputPoints();
            Point myInputPoint = getInputPoint();
            g.setColor(Color.WHITE);
            g.fillOval(myInputPoint.x - OVAL_SIZE - offset.x, myInputPoint.y - OVAL_SIZE - offset.y, OVAL_SIZE * 2, OVAL_SIZE * 2);
            for (int i = 0; i < outputNodes.length; i++) {
                Node node = outputNodes[i];
                Point outputPoint = outputPoints[i];
                if (i == activeOut) {
                    g.setColor(Color.ORANGE);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillOval(outputPoint.x - OVAL_SIZE - offset.x, outputPoint.y - OVAL_SIZE - offset.y, OVAL_SIZE * 2, OVAL_SIZE * 2);
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
                g.fillOval(outputPoint.x - OVAL_SIZE - offset.x, outputPoint.y - OVAL_SIZE - offset.y, OVAL_SIZE * 2, OVAL_SIZE * 2);
                node.render(g, offset, delta);
            }
        }

        public void keyTyped(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE && curserLoc > 0) {
                char[] charray = meta.toCharArray();
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

    public interface Language {
        public NodeType[] getNodeTypes();

        public String getName();
        public String getExtension();
        public String getComment();

        public LinkedList<Node> loadFromSource(String source);
    }

    public static abstract class NodeType {
        public String name;
        public int outputNum;
        public boolean isTextEditor;
        public Color syntaxColor = Color.GRAY;
        public int width = 256, height = 64;

        public abstract String parse(String meta, Node[] outputNodes);
    }

}

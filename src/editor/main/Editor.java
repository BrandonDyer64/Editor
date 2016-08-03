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

    public Editor(Language language, LinkedList<Node> nodes) {
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

        if (nodes == null) {
            NodeType type = language.getNodeTypes()[0];
            Node myNode = new Node(64, 64, 512, 256, type);
            myNode.meta = " Click on a node to slect or drag it.\n" +
                    "If a node is selected type to edit its data.\n" +
                    "To deselect a node, press escape, or click \noutside of it.\n" +
                    "When a node is deselected, you may use these \nkeyboard commands:\n" +
                    "  A - Add a node\n" +
                    "  W - Save your work\n" +
                    "  ESCAPE - Delete previously selected node\n" +
                    "  Z - Undelete last deleted node\n" +
                    "  Arrow Keys - Resize previously selected node";
            topNodes.add(myNode);
            lastNodeToDrag = myNode;
        } else {
            topNodes = nodes;
        }

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
            nodeToDrag.move((int) (mouseEvent.getX() / zoom) - mouse.x, (int) (mouseEvent.getY() / zoom) - mouse.y);
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
                        Driver.saveNodes(topNodes);
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

    public interface Language {
        public NodeType[] getNodeTypes();

        public String getName();

        public String getExtension();

        public String getComment();

        public LinkedList<Node> loadFromSource(String source);
    }

    public static abstract class NodeType implements Serializable {
        public String name;
        public int outputNum;
        public boolean isTextEditor;
        public Color syntaxColor = Color.GRAY;
        public int width = 256, height = 64;

        public abstract String parse(String meta, Node[] outputNodes);
    }

}

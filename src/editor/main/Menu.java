package editor.main;

import java.awt.*;
import java.io.Serializable;

/**
 * Created by brandon on 8/1/16.
 */
public abstract class Menu implements Serializable {

    public String[] options;
    public int x,y;

    public int get(Point mouse) {
        int out = (mouse.y - y) / Editor.HEADER_SIZE;
        if (out < 0) {
            out = -1;
        }
        if (out > options.length) {
            out = -1;
        }
        if (mouse.x < x) {
            out = -1;
        }
        if (mouse.x > x + 128) {
            out = -1;
        }
        return out;
    }

    public void render(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x,y,128,options.length * Editor.HEADER_SIZE);
        g.setColor(Color.WHITE);
        g.drawRect(x,y,128,options.length * Editor.HEADER_SIZE);
        for (int i = 0; i < options.length; i++) {
            g.drawLine(x,y + Editor.HEADER_SIZE * i, x + 128, y + Editor.HEADER_SIZE * i);
            g.drawString(options[i], x + Editor.HEADER_SIZE * (1f / 3f), y + Editor.HEADER_SIZE * (2f / 3f) + i * (Editor.HEADER_SIZE * (3f / 3f)));
        }
    }

}

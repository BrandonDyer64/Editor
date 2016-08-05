package editor.main;

import java.awt.*;
import java.io.Serializable;

/**
 * Created by brandon on 8/3/16.
 */
public class ErrorMenu implements Serializable {

    public String number = "";
    public int x, y;

    public Node get() {
        String code = Driver.code;
        int number = Integer.parseInt(this.number);
        String codes[] = code.split("\n");
        int index = number-1;
        System.out.println("THERE ARE " + codes.length + " LINES OF CODE");
        while (codes[index].length() < Driver.lang.getComment().length() + 2 || !codes[index].trim().substring(0, Driver.lang.getComment().length() + 2).equals(Driver.lang.getComment() + "@$"))
            index--;
        return Editor.getNodes().get(Integer.parseInt(codes[index].trim().substring(Driver.lang.getComment().length() + 2)));
    }

    public void ketTyped(char num) {
        if (Character.isDigit(num)) {
            number += num;
        }
    }

    public void render(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, 128, 32);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(x, y, 128, 32);
        g.drawString(number, x + Editor.HEADER_SIZE * (1f / 3f), y + Editor.HEADER_SIZE * (2f / 3f));
    }

}

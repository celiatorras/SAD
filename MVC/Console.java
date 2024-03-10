/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mcv;

/**
 *
 * @author celia
 */
import java.util.Observable;
import java.util.Observer;

public class Console implements Observer {
private Line line;

    public Console(Line line) {
        this.line = line;
        line.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.flush();
        
        // Print the line text
        //System.out.print(line.getText());

        // Move cursor to current position
        System.out.print("\033[" + (line.getCursorPosition() + 1) + "G");
    }
}

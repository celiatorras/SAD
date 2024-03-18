package sad_p1_scanner;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author celia
 */
public class Keys {
    public static final int HOME = -7;
    public static final int INS = -6;
    public static final int DEL = -5;
    public static final int END = -4;
    public static final int RIGHT = -3;
    public static final int LEFT = -2;
}
/*
parse:
    HOME: ESC O H
    END: ESC O F
    RIGHT: ESC [ C
    LEFT: ESC [ D
    HOME: ESC [ 1 ~ (home que esta a la tecla del num 7)
    INS: ESC [ 2 ~
    DEL: ESC [ 3 ~ 
    END: ESC [ 4 ~ (fn que esta a la tecla del num 1)
*/
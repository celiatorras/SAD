/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author celia
 */
public class Line {
    private StringBuilder text; // Almacenar el texto de la línea
    private int cursorPosition; // Almacenar la posición del cursor

    public Line() {
        text = new StringBuilder();
        cursorPosition = 0;
    }

    // Insertar un carácter en la posición del cursor
    public void insertChar(char c) {
        text.insert(cursorPosition, c);
        cursorPosition++;
    }

    // Eliminar el carácter a la izquierda del cursor
    public void borraChar() {
        if (cursorPosition > 0) {
            text.deleteCharAt(cursorPosition - 1);
            cursorPosition--;
        }
    }
    
    public void suprChar() {
        if (cursorPosition < text.length()) {
            text.deleteCharAt(cursorPosition + 1);
        }
    }
    
    public void overwrite(char c) {
        if (cursorPosition < text.length()) {
            text.setCharAt(cursorPosition, c);
            moveCursorRight();
        } else {
            insertChar(c);
        }
    }
    // Mover el cursor a la izquierda
    public void moveCursorLeft() {
        if (cursorPosition > 0) {
            cursorPosition--;
        }
    }

    // Mover el cursor a la derecha
    public void moveCursorRight() {
        if (cursorPosition < text.length()) {
            cursorPosition++;
        }
    }

    // Obtener el texto completo de la línea
    public String getText() {
        return text.toString();
    }

    // Obtener la posición actual del cursor
    public int getCursorPosition() {
        return cursorPosition;
    }

    // Establecer la posición del cursor
    public void setCursorPosition(int position) {
        if (position >= 0 && position <= text.length()) {
            cursorPosition = position;
        }
    }

    // Método para obtener la longitud de la línea
    public int length() {
        return text.length();
    }
}


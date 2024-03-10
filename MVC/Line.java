package mcv;

import java.io.*;
import java.util.Observable;

public class Line extends Observable {
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
        setChanged(); // Marcar que ha habido un cambio en el estado
        notifyObservers(); // Notificar a los observadores
    }

    // Eliminar el carácter a la izquierda del cursor
    public void borraChar() {
        if (cursorPosition > 0) {
            text.deleteCharAt(cursorPosition - 1);
            cursorPosition--;
            setChanged();
            notifyObservers();
        }
    }
    
    public void suprChar() {
        if (cursorPosition < text.length()) {
            text.deleteCharAt(cursorPosition);
            setChanged();
            notifyObservers();
        }
    }
    
    public void overwrite(char c) {
        if (cursorPosition < text.length()) {
            text.setCharAt(cursorPosition, c);
            cursorPosition++;
            setChanged();
            notifyObservers();
        } else {
            insertChar(c);
        }
    }
    
    // Mover el cursor a la izquierda
    public void moveCursorLeft() {
        if (cursorPosition > 0) {
            cursorPosition--;
            setChanged();
            notifyObservers();
        }
    }

    // Mover el cursor a la derecha
    public void moveCursorRight() {
        if (cursorPosition < text.length()) {
            cursorPosition++;
            setChanged();
            notifyObservers();
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
            setChanged();
            notifyObservers();
        }
    }

    // Método para obtener la longitud de la línea
    public int length() {
        return text.length();
    }
}

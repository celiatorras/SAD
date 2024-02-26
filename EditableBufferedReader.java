import java.io.*;

public class EditableBufferedReader extends BufferedReader {
    
    public EditableBufferedReader(Reader reader) {
        super(reader);
    }

    // Método para establecer el modo raw en la consola
    public void setRaw() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("/bin/stty", "-echo", "raw");
        Process process = pb.start();
        int exitCode = process.waitFor();
        
        /*if (exitCode == 0) {
            System.out.println("La consola está ahora en modo raw.");
        } else {
            System.err.println("Error al cambiar a modo raw.");
        }*/
    }

    // Método para volver al modo cooked en la consola
    public void unsetRaw() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("/bin/stty", "echo", "cooked");
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("La consola está ahora en modo cooked.");
        } else {
            System.err.println("Error al cambiar a modo cooked.");
        }
    }

    @Override
    public int read() {
        try {
            while (true) {
                int firstByte = super.read();
                int fourthByte;

                switch (firstByte) {
                    case 'b': //borrar
                        System.out.println("Tecla backspace");
                        return 8;
                    case 27: // Caracter de escape
                        int secondByte = super.read();
                        if (secondByte == '[') {
                            int thirdByte = super.read();
                            switch (thirdByte) {
                                case 'H': //home
                                    System.out.println("Tecla Home");
                                    return 1;
                                case 'F': //fin
                                    System.out.println("Tecla Fin");
                                    return 4;
                                case 'C': //fletxa dreta
                                    System.out.println("Tecla Derecha");
                                    return 67;
                                case 'D': //fletxa esquerra
                                    System.out.println("Tecla Izquierda");
                                    return 68;
                                case '2': //insert
                                    fourthByte = super.read();
                                    if (fourthByte == '~') {
                                        System.out.println("Tecla Insert");
                                        return 2;
                                    } else break;
                                case '3': //delete o suprimir
                                    fourthByte = super.read();
                                    if (fourthByte == '~') {
                                        System.out.println("Tecla Delete");
                                        return 3;
                                    } else break;
                            }
                        } else {
                            System.out.println("Altra tecla especial");
                        }
                        break;
                    default:
                        // Si no es una tecla especial, procesar como un carácter simple
                        char character = (char) firstByte;
                        System.out.println("Caracter: " + character);
                        return firstByte;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public String readLine() {
        Line line = new Line(); // Creamos una nueva instancia de Line
        int input;
        while ((input = read()) != '\n') {
            switch (input) {
                case 8: // Tecla de retroceso/borrar normal
                    line.borraChar();
                    break;
                case 1: // Tecla Home
                    line.setCursorPosition(0);
                    break;
                case 4: // Tecla Fin
                    line.setCursorPosition(line.length());
                    break;
                case 67: // Tecla Derecha
                    line.moveCursorRight();
                    break;
                case 68: // Tecla Izquierda
                    line.moveCursorLeft();
                    break;
                case 2: // Tecla Ins
                    line.overwrite((char) input);
                    break;
                case 3: //Tecla suprimir
                    line.suprChar();
                    break;
                default:
                    line.insertChar((char) input);
                    break;
            }
        }
        return line.getText();
    }
}

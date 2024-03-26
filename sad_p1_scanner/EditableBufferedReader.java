package practica1_scanner;
import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import practica1_scanner.Keys;

public class EditableBufferedReader extends BufferedReader {
    
    private Scanner sc;

    public EditableBufferedReader(Reader reader) {
        super(reader);
        sc = new Scanner(reader);
    }

    // Método para establecer el modo raw en la consola
    public void setRaw() throws IOException{
        //ProcessBuilder pb = new ProcessBuilder("/bin/stty", "-echo", "raw");
        //pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
        //POSAR (+ elegant!!!!!!!!!!!):
        ProcessBuilder pb = new ProcessBuilder("/bin/stty", "-echo", "raw").inheritIO();
        Process process = pb.start();
        try {
            int exitCode = process.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(EditableBufferedReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Método para volver al modo cooked en la consola
    public void unsetRaw() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("/bin/stty", "echo", "cooked").inheritIO();
        Process process = pb.start();
        try {
            int exitCode = process.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(EditableBufferedReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int read() throws IOException {
        int b;
        if (match("\033\\[H"))
            return Keys.HOME;
        if (match("\033\\[F"))
            return Keys.END;
        if (match("\033\\[([CD])"))  //[ -> vol dir corxet esquerra literal
            return Keys.RIGHT + group(1).charAt(0)-'C'; //SI EL INDEX DE GROUP ÉS TOTA LA SUBEXPRESSIÓ REGULAR, SI ÉS 1 SERA LA PRIMERA...
        if (match("\033\\[([1234])~"))
            return Keys.RIGHT + group(1).charAt(0)-'1';
        b = get();//sino caza con ninguna expresion regular o seq de esc, posem el mètode get()
        //es podria fer en comptes de un get() un (char) System.in.read(); pero no podem barrejar read amb scanner
    return b;
    }

    int get(){
        //1ra Alternativa
        //sc.skip("(?s).");
        //return sc.match().group().charAt(0);
        //2na Alternativa:
        return sc.findWithinHorizon("(?s).", 1).charAt(0); //fa una cerca i li hem de dir la profunditat de la cerca, no hace una caza
        //3ra Alternativa:
        //return sc.useDelimiter("").next().charAt(0);
    }
    
    
    public boolean match(String s) throws IOException {
//        boolean res = true;
//        try{
//            sc.skip(s);
//        }catch(NoSuchElementException e){//si falla llença 
//            res=false;
//    
//        }
//        return res;

        //SOLUCIÓ:
        return sc.skip("(?:" + s + ")?").match().group().length() > 0; 
    }

    String group(int index){
        return sc.match().group(index);
    }
    
     //  "\\033[4h" ---> INS 
    @Override
    public String readLine() throws IOException {
        
        try{
            setRaw();
            Line line = new Line(); // Creamos una nueva instancia de Line
            int input;
            while ((input = read()) != '\r') {// ESTEM A RAW, no podem posar \n ---> 10 (ascii)
                // \r ---> en raw 13 (ascii)
                switch (input) {
                    case Keys.HOME: //Tecla Home
                        line.setCursorPosition(0);
                        break;
                    case Keys.END: //Tecla Fin
                        line.setCursorPosition(line.length());
                        break;
                    case Keys.RIGHT: //Tecla Derecha
                        line.moveCursorRight();
                        break;
                    case Keys.LEFT: //Tecla Izquierda
                        line.moveCursorLeft();
                        break;
                    case Keys.INS: //Tecla Ins
                        line.overwrite((char) input);
                        break;
                    case Keys.DEL: //Tecla suprimir
                        line.suprChar();
                        break;
                    default: //en qualsevol altre cas imprimim el caràcter
                        line.insertChar((char) input);
                        break;
                }
            }
            return line.getText(); //NO USAR getText, usar directament toString !!!!!!!!
        }finally{
                unsetRaw();
        }
    }
}

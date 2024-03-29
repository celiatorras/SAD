package EditableBufferedReader;
import java.io.*;

public class EditableBufferedReader extends BufferedReader {
    
    public EditableBufferedReader(Reader reader) {
        super(reader);
    }

    // Método para establecer el modo raw en la consola
    public void setRaw() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("/bin/stty", "-echo", "raw");
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        int exitCode = process.waitFor();
    }

    // Método para volver al modo cooked en la consola
    public void unsetRaw() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("/bin/stty", "echo", "cooked");
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT); 
        Process process = pb.start();
        int exitCode = process.waitFor();
    }

    @Override
    public int read() throws IOException {
        int b;
        int aux;
        if((b = super.read()) != '\033') //mirem que el primer caràcter és ESC
            return b;
        if((b = super.read())!='[')
            return b;
            switch(b = super.read()){
            case 'H': return Keys.HOME;
            case 'F': return Keys.END;
            case 'C': return Keys.RIGHT;
            case 'D': return Keys.LEFT;
            case '2':
            case '3':
                if((aux = super.read()) != '~')
                    return b;
                return Keys.HOME + b - '1';
            default: return b;
            }  
    }

    @Override
    public String readLine() throws IOException {
        Line line = new Line(); // Creamos una nueva instancia de Line
        int input;
        while ((input = read()) != '\n') {
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
        return line.getText();
    }
}

//pensar gestió d'errors que no sigui enviar cap missatge i per tant la solució més neta és per exemple
//les tecles de dalt i baix que no volem tronem una A i una B pq ja és el que fa el programa pq 
//és directament el que llegeix de la seq d'sacpe que no tienim en compte

//simbol el guardem a keys, classe general on tenim definides els caracters
//si ho necessitéssim tambe possibles combinacions de caracters, ex: control shift
//HOME | ALT | SHIFT

/* a la classe keys:
public final int HOME = "num negatiu";
public final int HOME = "num negatiu+1";
public final int HOME = "num negatiu+2";
public final int HOME = "num negatiu+3";

despres dels case: //foto
if((ch1=super.read())!='~')
    return ch1;
   return Keys.HOME + ch -'1';

//foto exercici de classe
MÈTODE MATCH -> retorna true si son iguals les seqüències o false sino
un cop llegir match he de retrocedir la lectura per "posar cursor" al principi i poder 
llegir la següent seqüència d'scape. no és llarg de fer. llegint caracter per caracter i prgramar el 
backtranquing amb els metodes de la classe read.

match |true si casa amb el string
      |false si no (i fa backtraking)
tenim 2 maneres de fer-ho
1) el metode te cariable local head. amb string prefix que guarda el que ha llegit i ja ha coincidit
2) amb mètodes de la classe Reader
    
public int read() throws IOException{
    int ch;
    if (match("\0330H"))
        return KEY.HOME;
    if (match("\0330F"))
        return KEY.END;
    if (match("\033\\[([CD])"))  \\[ -> vol dir corxet esquerra literal
        return KEY.RIGHT + ch -'C';
    if (match("\033\\[([1234])~"))
        return KEY.RIGHT + ch -'1';
    ch = get();
    return ch;
}

EXPRESIONS REGULARS i METACARACTERS:
[..] seleccio (un char)
a* (-,a,aa,aaa,aaaa...)
a+ (a,aa,aaa,aaaa...)
a? , ab? (a, ab)
A|B , a*|b* (-,a,aa,aaa...,b,bb,bbb...)
\\d és com posar [0-9] és un digit
?: és per agrupar sense capturar
() és per capturar

exemple amb un num amb notacio científica:
[+-]?\\d*\\.?\\d+(?:[eE][+-]?\\d+)
signe opcional, digit, part sencera opcional, \\. és un punt literal...

Les expressions reculars estan a la classe Pattern.java
*/

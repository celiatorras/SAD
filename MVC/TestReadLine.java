package mcv;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author celia
 */
import java.io.*;

class TestReadLine{
  public static void main(String[] args) {
    Line line = new Line();
    Console console = new Console(line);
    BufferedReader in = new EditableBufferedReader(line, new InputStreamReader(System.in));
    String str = null;
    try {
      str = in.readLine();
    } catch (IOException e) { e.printStackTrace(); }
    System.out.println("\nline is: " + str);
  }
}
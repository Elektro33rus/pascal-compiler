package test2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public final class Emulator {
    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<Token> tokenArrayList = TokenScanner.scan(new File("example.pas"));
        Parser.setTokenArrayListIterator(tokenArrayList);
        ArrayList<String> command = Parser.parse();
        for (int i=0; i< command.size(); i++)
        	System.out.println(command.get(i));
        //Simulator.setInstructions("");
        //Simulator.simulate();
    }
}
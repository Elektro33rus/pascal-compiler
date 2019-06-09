package mycompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public final class Emulator {
    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<Token> tokenArrayList = TokenScanner.scan(new File("example.pas"));
        for (int i=0; i<tokenArrayList.size(); i++) {
        	System.out.println(tokenArrayList.get(i).getTokenType());
        }
        //Parser.setTokenArrayListIterator(tokenArrayList);
        //Byte[] instructions = Parser.parse();
        //Simulator.setInstructions(instructions);
        //Simulator.simulate();
    }
}
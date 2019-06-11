package mycompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public final class Main {
    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<Token> tokenArrayList = TokenScanner.scan(new File("example.pas"));
        Parser.setTokenArrayListIterator(tokenArrayList);
        Byte[] instructions = Parser.parse();
        ArrayList<Symbol> vars = Parser.arraySymbols;
        Generator.setVars(vars);
        Generator.setInstructions(instructions);
        Generator.generate();
    }
}
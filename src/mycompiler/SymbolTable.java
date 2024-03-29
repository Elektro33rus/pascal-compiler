package mycompiler;

public final class SymbolTable {

    static class Scope {
        Symbol[] symbolTable = new Symbol[HASH_TABLE_SIZE];
        Scope next = null;
    }

    private static final int HASH_TABLE_SIZE = 256;
    private static Scope headerScope = new Scope();
    
    public static void insert(Symbol symbol) {
        int hashValueName = hash(symbol.getName());
        int hashValueRegion = hash(symbol.getRegion());
        int hash = hashValueName + hashValueRegion;
        Symbol bucketCursor = headerScope.symbolTable[hash];
        if (bucketCursor == null)
            headerScope.symbolTable[hash] = symbol;
        else {
            while (bucketCursor.next != null)
                bucketCursor = bucketCursor.next;
            bucketCursor.next = symbol;
        }
    }

    public static Symbol lookup(String symbolName, String regionName) {
        int hashValueName = hash(symbolName);
        int hashValueRegion = hash(regionName);
        int hash = hashValueName + hashValueRegion;
        Symbol bucketCursor = headerScope.symbolTable[hash];
        Scope scopeCursor = headerScope;
        while (scopeCursor != null) {
            while (bucketCursor != null) {
                if (bucketCursor.getName().equals(symbolName) && bucketCursor.getRegion().equals(regionName))
                    return bucketCursor;
                bucketCursor = bucketCursor.next;
            }
            scopeCursor = scopeCursor.next;
        }
        return null;
    }

    public static int hash(String symbolName) {
        int h = 0;
        for (int i = 0; i < symbolName.length(); i++)
            h = h + h  + symbolName.charAt(i);
        h*=197;
        return h = h % HASH_TABLE_SIZE/2;
    }

    public static void openScope() {
        Scope innerScope = new Scope();
        innerScope.next = headerScope;
        headerScope = innerScope;
    }

    public static void closeScope() {
        headerScope = headerScope.next;
    }

    public static Scope getHeaderScope() {
        return headerScope;
    }
}
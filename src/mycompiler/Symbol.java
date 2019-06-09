package mycompiler;

public class Symbol {
    private String name = "";
    private String tokenType = "";
    private Parser.TYPE dataType = null;
    private int address;
    private int returnAddress;
    private String Region = "";
    private int number;
    private int amount;
    private boolean result = false;
    
    private Parser.TYPE indexType;
    private Parser.TYPE valueType;

    Symbol next;

    public Symbol(String name, String tokenType, String Region, Parser.TYPE dataType, int address){
        this.name = name;
        this.tokenType = tokenType;
        this.Region = Region;
        this.dataType = dataType;
        this.address = address;
    }
    
    public void setResult(boolean result) {
    	this.result = result;
    }
    
    public boolean getResult() {
    	return this.result;
    }
    
    public void setAmount(int amount) {
    	this.amount = amount;
    }
    
    public int getAmount() {
    	return this.amount;
    }
    
    public void setNumber(int number) {
    	this.number = number;
    }
    
    public int getNumber() {
    	return this.number;
    }

    public String getName() {
        return name;
    }
    
    public String getRegion() {
        return Region;
    }
    
    public void setRegion(String Region) {
        this.Region = Region;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Parser.TYPE getDataType() {
        return dataType;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getReturnAddress() {
        return returnAddress;
    }

    public void setReturnAddress(int returnAddress) {
        this.returnAddress = returnAddress;
    }

    public Parser.TYPE getIndexType() {
        return indexType;
    }

    public void setIndexType(Parser.TYPE indexType) {
        this.indexType = indexType;
    }

    public Parser.TYPE getValueType() {
        return valueType;
    }

    public void setValueType(Parser.TYPE valueType) {
        this.valueType = valueType;
    }
}
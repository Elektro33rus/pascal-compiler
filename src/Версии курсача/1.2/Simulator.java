package test2;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Stack;

public class Simulator {

    private static int ip = 0;
    private static int dp = 0;

    private static Stack<Object> stack = new Stack<>();
    private static Stack<Object> stackNumber = new Stack<>();

    private static Byte[] dataArray = new Byte[1000];

    private static Byte[] instructions;

    public static void simulate() {
        Parser.OP_CODE opCode;
        do {
            opCode = getOpCode();
            System.out.println(opCode);
            switch (opCode) {
                case PUSH:
                    push();
                    break;
                case PUSHI:
                    pushi();
                    break;
                case PUSHF:
                    pushf();
                    break;
                case POP:
                    pop();
                    break;
                case GET:
                    get();
                    break;
                case PUT:
                    put();
                    break;
                case CVR:
                    cvr();
                    break;
                case XCHG:
                    xchg();
                    break;
                case JMP:
                    jmp();
                    break;
                case JMPF:
                    jmpf();
                    break;
                case PRINT_REAL:
                    printReal();
                    break;
                case PRINT_INT:
                    printInt();
                    break;
                case PRINT_NEWLINE:
                    System.out.println();
                    break;
                case HALT:
                    halt();
                    break;
                case EQL:
                    eql();
                    break;
                case NEQL:
                    neql();
                    break;
                case LSS:
                    less();
                    break;
                case LEQ:
                    lessEql();
                    break;
                case GTR:
                    greater();
                    break;
                case GEQ:
                    greaterEql();
                    break;
                case JFALSE:
                    jfalse();
                    break;
                case JTRUE:
                    jtrue();
                    break;
                case IFTHEN:
                	ifthen();
                    break;
                case IFELSE:
                	ifelse();
                    break;
                case IFEND:
                	ifend();
                    break;
                case ADD:
                    add();
                    break;
                case FADD:
                    fadd();
                    break;
                case SUB:
                    sub();
                    break;
                case FSUB:
                    fsub();
                    break;
                case MULT:
                    mult();
                    break;
                case FMULT:
                    fmult();
                    break;
                case DIV:
                    div();
                    break;
                case FDIV:
                    fdiv();
                    break;
                default:
                    throw new Error(String.format("Unhandled case: %s", opCode));
            }
        }
        while (opCode != Parser.OP_CODE.HALT);
    }
    
    static class iffen{
    	String ifen;
    	String number;
    	iffen (String Ifen, String Number){
    		this.ifen = Ifen;
    		this.number = Number;
    	}
    }
    
    private static boolean isIfthen = false;
    private static boolean isIfelse = false;
    private static ArrayList<iffen> forIfthen = new ArrayList<iffen>();
    private static ArrayList<iffen> forIfelse = new ArrayList<iffen>();
    private static int schet = -1;
    
    private static void ifthen() {
    	String temp = (String) stackNumber.pop();
    	schet++;
    	forIfthen.add(new iffen("", ""));
		forIfthen.set(schet, new iffen("\n;Label %"+temp+" ifthen\n", forIfthen.get(schet).number));
		isIfthen = true;
    }
    
    private static void ifelse() {
    	isIfthen = false;
    	isIfelse = true;
    	String temp = (String) giveMeNumberVar();
    	forIfthen.set(schet, new iffen(forIfthen.get(schet).ifen+"br label %", temp));
    	forIfelse.add(new iffen("", ""));
    	forIfelse.set(schet, new iffen("\n;Label %"+temp+" ifelse\n", temp));
    }
    
    private static void ifend() {
    	isIfelse = false;
    	AllProgram+=forIfthen.get(schet).number+"\n";
    	String temp = (String) giveMeNumberVar();
    	forIfthen.set(schet, new iffen(forIfthen.get(schet).ifen+temp+"\n", forIfthen.get(schet).number));
    	AllProgram+=forIfthen.get(schet).ifen+forIfelse.get(schet).ifen+"br label %"+temp+"\n\n;Label %"+temp+" ifend\n";
    }

    private static void pushf() {
        float val = getFloatValue();
        stack.push(val);
    }

    private static void get() {
        dp = (int)stack.pop();
        stack.push(getData(dp));
    }

    private static Object put() {
        Object val = stack.pop();
        dp = (int)stack.pop();
        byte[] valBytes;
        if (val instanceof Integer) {
            valBytes = ByteBuffer.allocate(4).putInt((int) val).array();
        } else {
            valBytes = ByteBuffer.allocate(4).putFloat((float) val).array();
        }
        for (byte b: valBytes) {
            dataArray[dp++] = b;
        }
        return val;
    }

    private static void jtrue() {
        if (stack.pop().toString().equals("true")){
            ip = getAddressValue();
        } else {
            getAddressValue();
        }
    }

    private static void jfalse() {
        getAddressValue();
    }

    private static void eql() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
        stack.push(val1.equals(val2));
    }

    private static void neql() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
        stack.push(!val1.equals(val2));
    }

    private static void less() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
        
        String t1 = (String) stackNumber.pop();
        String t2 = (String) stackNumber.pop();
        String temp = giveMeNumberVar();
        String alloca = giveMeNumberVar();
        input("%"+temp+" = icmp sgt i32 %"+t1+", %"+t2+"\n"
            		+"br i1 %"+temp+", label %"+alloca+", label %");
            		
        stackNumber.push(alloca);
        stack.push(val1 < val2);
    }
    
    private static void input(String text) {
    	if (!isIfthen && !isIfelse) {
    		AllProgram+=text;
    	}
    	else
    		if (isIfthen) {
    			forIfthen.set(schet, new iffen(forIfthen.get(schet).ifen+text, forIfthen.get(schet).number));
    		}
    		else {
    			forIfelse.set(schet, new iffen(forIfelse.get(schet).ifen+text, forIfelse.get(schet).number));
    		}
    }

    private static void greater() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
        stack.push(val1 > val2);
    }

    private static void lessEql() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
        stack.push(val1 <= val2);
    }

    private static void greaterEql() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
        stack.push(val1 >= val2);
    }

    private static void printReal() {
        Object val = stack.pop();
        if (val instanceof Integer) {
            byte[] valArray = ByteBuffer.allocate(4).putInt((int) val).array();
            System.out.print(ByteBuffer.wrap(valArray).getFloat());
        } else {
            System.out.print(val);
        }
    }

    public static void printInt(){
    	String temp = giveMeNumberVar();
    	String test = (String) stackNumber.pop();
        input("%"+temp+" = tail call i32 (i8*, ...) "
				+ "@printf(i8* getelementptr inbounds ([3 x i8], "
				+ "[3 x i8]* @.str, i64 0, i64 0), i32 %"+test+")\n");
    	System.out.println(stack.pop());
    }

    public static void add(){
        int val1 = (int) stack.pop();
        int val2 = (int) stack.pop();
        
        String t1 = (String) stackNumber.pop();
        String t2 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        input("%"+alloca+" = add i32 %"+t1+", %"+t2+"\n");
        stackNumber.push(alloca);
        
        stack.push(val1 + val2);
    }

    private static void fadd() {
        float val1 = (float) stack.pop();
        float val2 = (float) stack.pop();
        stack.push(val1 + val2);
    }

    public static void sub(){
        int val2 = (int) stack.pop();
        int val1 = (int) stack.pop();
        
        String t2 = (String) stackNumber.pop();
        String t1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        input("%"+alloca+" = sub i32 %"+t1+", %"+t2+"\n");
        stackNumber.push(alloca);
        
        stack.push(val1 - val2);
    }

    public static void fsub(){
        float val1 = (float) stack.pop();
        float val2 = (float) stack.pop();
        stack.push(val1 - val2);
    }

    public static void mult(){
        int val1 = (int) stack.pop();
        int val2 = (int) stack.pop();
        
        String t1 = (String) stackNumber.pop();
        String t2 = (String) stackNumber.pop();
        String alloca = (String) giveMeNumberVar();
        input("%"+alloca+" = mul i32 %"+t1+", %"+t2+"\n");
        stackNumber.push(alloca);
        
        stack.push(val1 * val2);
    }

    public static void fmult(){
        float val1 = (float) stack.pop();
        float val2 = (float) stack.pop();
        stack.push(val1 * val2);
    }

    public static void fdiv(){
        float val2 = (float) stack.pop();
        float val1 = (float) stack.pop();
        stack.push(val1 / val2);
    }

    public static void div(){
        int val2 = (int) stack.pop();
        int val1 = (int) stack.pop();
        
        String t1 = (String) stackNumber.pop();
        String t2 = (String) stackNumber.pop();
        String alloca = (String) giveMeNumberVar();
        input("%"+alloca+" = div i32 %"+t1+", %"+t2+"\n");
        stackNumber.push(alloca);
        
        stack.push(val1 / val2);
    }

    public static void cvr(){
        float val = Float.valueOf(String.valueOf(stack.pop()));
        stack.push(val);
    }

    public static void xchg(){
        Object val1 = stack.pop();
        Object val2 = stack.pop();
        stack.push(val1);
        stack.push(val2);
    }
    
    private static int KolvoVar=0;
    private static int KolvoIfElse=0;
    private static String AllProgram="@.str = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1\n\n"
    		+ "declare i32 @printf(i8* nocapture readonly, ...)\n\n"
    		+ "define i32 @main() {\n";
    
    private static String giveMeNumberVar() {
    	String temp="";
    	KolvoVar++;
    	temp=Integer.toString(KolvoVar);
    	return temp;
    }

    public static void pushi(){
        int val = getAddressValue();
        
        String alloca = giveMeNumberVar();
        String load = giveMeNumberVar();
        input("%"+alloca+" = alloca i32\n"
    			+ "store i32 " + val +", i32* %"+alloca+"\n"
    			+ "%"+load+" = load i32, i32* %"+alloca+"\n");
        stackNumber.push(load);
        
        stack.push(val);
    }

    public static void push(){
        dp = getAddressValue();
        
        String alloca = giveMeNumberVar();
        String load = giveMeNumberVar();
        String value = (String) stackNumber.pop();
        input("%"+alloca+" = alloca i32\n"
        		+ "store i32 %" + value +", i32* %"+alloca+"\n"
				+ "%"+load+" = load i32, i32* %"+alloca+"\n");
        stackNumber.push(load);
        
        stack.push(getData(dp));
    }

    public static Object pop(){
        Object val = stack.pop();
        //stackNumber.pop();
        dp = getAddressValue();
        byte[] valBytes;
        if (val instanceof Integer) {
            valBytes = ByteBuffer.allocate(4).putInt((int) val).array();
        } else {
            valBytes = ByteBuffer.allocate(4).putFloat((float) val).array();
        }
        for (byte b: valBytes) {
            dataArray[dp++] = b;
        }   
        return val;
    }

    public static void jmp(){
        ip = getAddressValue();
    }
    
    public static void jmpf(){
        ip = getAddressValue();
    }

    public static void halt() {
    	AllProgram+="ret i32 0\n}";
        System.out.println("\nProgram finished with exit code 0");
        System.out.println(AllProgram);
        System.exit(0);
    }

    public static int getAddressValue() {
        byte[] valArray = new byte[4];
        for (int i = 0; i < 4; i++) {
            valArray[i] = instructions[ip++];
        }
        return ByteBuffer.wrap(valArray).getInt();
    }

    public static float getFloatValue() {
        byte[] valArray = new byte[4];
        for (int i = 0; i < 4; i++) {
            valArray[i] = instructions[ip++];
        }
        return ByteBuffer.wrap(valArray).getFloat();
    }

    public static int getData(int dp) {
    	byte[] valArray = new byte[4];
		for (int i = 0; i < 4; i++) {
			valArray[i] = dataArray[dp++];
		}
        return ByteBuffer.wrap(valArray).getInt();
    }

    public static Parser.OP_CODE getOpCode(){
        return Parser.OP_CODE.values()[instructions[ip++]];
    }

    public static void setInstructions(Byte[] instructions) {
        Simulator.instructions = instructions;
    }
}
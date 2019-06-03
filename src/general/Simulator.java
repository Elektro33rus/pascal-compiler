package general;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Stack;

public class Simulator {

    private static int ip = 0;
    private static int dp = 0;

    private static Stack<Object> stack = new Stack<>();
    private static Stack<Object> stackNumber = new Stack<>();
    private static String[] dataArrayTemp = new String[1000];

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
                case PUSHREAL:
                	pushreal();
                	break;
                case PUSHINTLIT:
                    pushi();
                    break;
                case PUSHFLOATLIT:
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
                case JMPTOFUNCTION:
                    jmpf();
                    break;
                case JMPOUTFUNCTION:
                    jmpz();
                    break;
                case PRINT_REAL:
                    printReal();
                    break;
                case PRINT_INT:
                    printInt();
                    break;
                case PRINT_NEWLINE:
                    printLn();
                    break;
                case HALT:
                    halt();
                    break;
                case EQLIF:
                    eqlIf();
                	break;
                case NEQLIF:
                    neqlIf();
                    break;
                case LSSIF:
                    lessIf();
                    break;
                case LSS:
                	less();
                	break;
                case LEQIF:
                    lessEqlIf();
                    break;
                case LEQFOR:
                    lessEqlfor();
                    break;
                case GTRIF:
                    greaterIf();
                    break;
                case GEQIF:
                    greaterEqlIf();
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
                case AND:
                	and();
                	break;
                case OR:
                	or();
                	break;
                default:
                    throw new Error(String.format("Unhandled case: %s", opCode));
            }
        }
        while (opCode != Parser.OP_CODE.HALT);
    }

    private static void lessEqlfor() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
        stack.push(val1 <= val2);
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
    private static ArrayList<Boolean> isIfelse = new ArrayList<Boolean>();
    private static int ifEnd = 0;
    private static ArrayList<iffen> forIfthen = new ArrayList<iffen>();
    private static ArrayList<iffen> forIfelse = new ArrayList<iffen>();
    private static ArrayList<String> poryadok = new ArrayList<String>();
    
    private static void ifthen() {
    	poryadok.add("then");
    	andIf=false;
    	String temp = (String) stackNumber.pop();
		forIfthen.add(new iffen("\n;Label %"+temp+" ifthen\n", ""));
		isIfthen = true;
    }
    
    private static void ifelse() {
    	poryadok.add("else");
    	isIfthen = false;
    	isIfelse.add(true);
    	String temp = (String) giveMeNumberVar();
    	stackNumber.push(temp);
    	forIfelse.add(new iffen("\n;Label %"+temp+" ifelse\n", ""));
    }
    
    private static void ifend() {
    	ifEnd++;
    	
    	if (!forandIf.isEmpty()) {
    		String temp = (String) giveMeNumberVar();
    		for (int i=0; i<forandIf.size(); i++)
    			AllProgram+=temp+"\n"+forandIf.get(i);
    		schetAnd=-1;
    		forandIf.clear();
    	}
    	
    	if (ifEnd == forIfthen.size()) {
    		String temp = (String) giveMeNumberVar();
    	while (!poryadok.isEmpty()) {
    		if (poryadok.get(0).equals("then")) {
    			boolean temp12 = true;
    			if (!isIfelse.isEmpty()) {
    				AllProgram+=stackNumber.pop()+"\n";
    				isIfelse.remove(0);
    				temp12 = false;
    			}
    			else
    				AllProgram+=temp+"\n";
        		AllProgram+=forIfthen.get(0).ifen;
        		if (forIfthen.size()==1)
        			AllProgram+="br label %"+temp+"\n";
        		if (isIfelse.isEmpty() && temp12 && forIfelse.isEmpty()) {
        			AllProgram+="\n;Label %"+temp+" ifend\n";
        		}
        		forIfthen.remove(0);
    		}
    		else {
    			AllProgram+=forIfelse.get(0).ifen+"br label %"+temp+"\n";
    			if (forIfelse.size()==1)
    				AllProgram+="\n;Label %"+temp+" ifend\n";
        		forIfelse.remove(0);
    		}
    		poryadok.remove(0);
    	}
    	}
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
    	if (stack.pop().toString().equals("false")){
            ip = getAddressValue();
        } else {
            getAddressValue();
        }
    }

    private static void eqlIf() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
        
        String t2 = (String) stackNumber.pop();
        String t1 = (String) stackNumber.pop();
        String temp = giveMeNumberVar();
        String alloca = giveMeNumberVar();
        input("%"+temp+" = icmp eq i32 %"+t1+", %"+t2+"\n"
            		+"br i1 %"+temp+", label %"+alloca+", label %");
        stackNumber.push(alloca);
        
        stack.push(val1.equals(val2));
    }

    private static void neqlIf() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
        
        String t2 = (String) stackNumber.pop();
        String t1 = (String) stackNumber.pop();
        String temp = giveMeNumberVar();
        String alloca = giveMeNumberVar();
        input("%"+temp+" = icmp ne i32 %"+t1+", %"+t2+"\n"
            		+"br i1 %"+temp+", label %"+alloca+", label %");
        stackNumber.push(alloca);
        getAddressValue();
        
        stack.push(!val1.equals(val2));
    }
    
    private static int schetAnd=-1;
    private static ArrayList<String> forandIf = new ArrayList<String>();
    private static boolean andIf = false;
    
    private static void and() {
    	schetAnd++;
    	forandIf.add("");
    	andIf = true;
		String alloca = (String) stackNumber.pop();
    	input("\n;Label %"+alloca+"\n");
		stackNumber.push(alloca);
	}
    
    private static int schetOr = -1;
    private static ArrayList<String> fororIf = new ArrayList<String>();
    private static boolean orIf = false;
    
    private static void or() {
		schetOr++;
    	fororIf.add("");
    	orIf = true;
		String alloca = (String) stackNumber.pop();
    	input("\n;Label %"+alloca+"\n");
		stackNumber.push(alloca);
	}
    
    private static void less() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
      
        stack.push(val1 < val2);
    }

    private static void lessIf() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
        
        String t2 = (String) stackNumber.pop();
        String t1 = (String) stackNumber.pop();
        String temp = giveMeNumberVar();
        String alloca = giveMeNumberVar();
        input("%"+temp+" = icmp slt i32 %"+t1+", %"+t2+"\n"
            		+"br i1 %"+temp+", label %"+alloca+", label %");
        
        stackNumber.push(alloca);
        
        stack.push(val1 < val2);
    }
   
    private static void greaterIf() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
        
        String t2 = (String) stackNumber.pop();
        String t1 = (String) stackNumber.pop();
        String temp = giveMeNumberVar();
        String alloca = giveMeNumberVar();
        input("%"+temp+" = icmp sgt i32 %"+t1+", %"+t2+"\n"
            		+"br i1 %"+temp+", label %"+alloca+", label %");
        stackNumber.push(alloca);
        
        stack.push(val1 > val2);
    }

    private static void lessEqlIf() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
        
        String t2 = (String) stackNumber.pop();
        String t1 = (String) stackNumber.pop();
        String temp = giveMeNumberVar();
        String alloca = giveMeNumberVar();
        input("%"+temp+" = icmp sle i32 %"+t1+", %"+t2+"\n"
            		+"br i1 %"+temp+", label %"+alloca+", label %");
        stackNumber.push(alloca);
        
        stack.push(val1 <= val2);
    }

    private static void greaterEqlIf() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
        
        String t2 = (String) stackNumber.pop();
        String t1 = (String) stackNumber.pop();
        String temp = giveMeNumberVar();
        String alloca = giveMeNumberVar();
        input("%"+temp+" = icmp sge i32 %"+t1+", %"+t2+"\n"
            		+"br i1 %"+temp+", label %"+alloca+", label %");
        stackNumber.push(alloca);
        
        stack.push(val1 >= val2);
    }
   
    private static void input(String text) {
    	if (!isIfthen && isIfelse.isEmpty() && !andIf && !orIf) {
    		AllProgram+=text;
    	}
    	else
    		if (isIfthen) {
    			forIfthen.set(forIfthen.size()-1, new iffen(forIfthen.get(forIfthen.size()-1).ifen+text, forIfthen.get(forIfthen.size()-1).number));
    		}
    		else if (!isIfelse.isEmpty()){
    			forIfelse.set(forIfelse.size()-1, new iffen(forIfelse.get(forIfelse.size()-1).ifen+text, ""));
    		} else if (andIf){
    			forandIf.set(schetAnd, forandIf.get(schetAnd)+text);
    		} else if (orIf) {
    			fororIf.set(schetOr, fororIf.get(schetOr)+text);
    		}
    }

    private static void printReal() {
        Object val = stack.pop();
        if (val instanceof Integer) {
            byte[] valArray = ByteBuffer.allocate(4).putInt((int) val).array();
            
            String temp = giveMeNumberVar();
        	String test = (String) stackNumber.pop();
        	
            input("%"+temp+" = call i32 (i8*, ...) "
    				+ "@printf(i8* getelementptr inbounds ([4 x i8], "
    				+ "[4 x i8]* @.strfloat, i32 0, i32 0), double %"+test+")\n");
            
            System.out.println(ByteBuffer.wrap(valArray).getFloat());
        } else {
            System.out.println(val);
        }
    }
    
    private static void printLn() {
    	String alloca = giveMeNumberVar();
    	input("%"+alloca+" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.strln, i32 0, i32 0))\n");
    }

    public static void printInt(){
    	String temp = giveMeNumberVar();
    	String test = (String) stackNumber.pop();
    	
        input("%"+temp+" = call i32 (i8*, ...) "
				+ "@printf(i8* getelementptr inbounds ([4 x i8], "
				+ "[4 x i8]* @.strint, i32 0, i32 0), i32 %"+test+")\n");
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
        
        String t1 = (String) stackNumber.pop();
        String t2 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        input("%"+alloca+" = fadd double %"+t1+", %"+t2+"\n");
        stackNumber.push(alloca);
        
        
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
        float val2 = (float) stack.pop();
        float val1 = (float) stack.pop();
        
        String t2 = (String) stackNumber.pop();
        String t1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        input("%"+alloca+" = fsub double %"+t1+", %"+t2+"\n");
        stackNumber.push(alloca);
        
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
        
        String t1 = (String) stackNumber.pop();
        String t2 = (String) stackNumber.pop();
        String alloca = (String) giveMeNumberVar();
        input("%"+alloca+" = fmul double %"+t1+", %"+t2+"\n");
        stackNumber.push(alloca);
        
        stack.push(val1 * val2);
    }

    public static void div(){
        int val2 = (int) stack.pop();
        int val1 = (int) stack.pop();
        
        String t2 = (String) stackNumber.pop();
        String t1 = (String) stackNumber.pop();
        String alloca = (String) giveMeNumberVar();
        input("%"+alloca+" = div i32 %"+t1+", %"+t2+"\n");
        stackNumber.push(alloca);
        
        stack.push(val1 / val2);
    }
    
    public static void fdiv(){
        float val2 = (float) stack.pop();
        float val1 = (float) stack.pop();
        
        String t2 = (String) stackNumber.pop();
        String t1 = (String) stackNumber.pop();
        String alloca = (String) giveMeNumberVar();
        input("%"+alloca+" = fdiv double %"+t1+", %"+t2+"\n");
        stackNumber.push(alloca);
        
        stack.push(val1 / val2);
    }

    public static void cvr(){
        float val = Float.valueOf(String.valueOf(stack.pop()));
        
        String temp = (String) stackNumber.pop();
        String alloca = (String) giveMeNumberVar();
        input("%"+alloca+" = sitofp i32 %"+temp+" to double\n");
        stackNumber.push(alloca);

        stack.push(val);
    }

    public static void xchg(){
        Object val1 = stack.pop();
        Object val2 = stack.pop();
        
        Object t1 = stackNumber.pop();
        Object t2 = stackNumber.pop();
        
        stackNumber.push(t1);
        stackNumber.push(t2);
        
        stack.push(val1);
        stack.push(val2);
    }
    
    private static int KolvoVar=0;
    
    private static String AllProgram="@.strln = private unnamed_addr constant [2 x i8] c\"\\0A\\00\"\n\n"
    		+ "@.strint = private unnamed_addr constant [4 x i8] c\"%i\\0A\\00\"\n\n"
    		+ "@.strfloat = private unnamed_addr constant [4 x i8] c\"%f\\0A\\00\"\n\n"
    		+ "declare i32 @printf(i8*, ...)\n\n"
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
    
    private static void pushf() {
        float val = getFloatValue();
        
        String alloca = giveMeNumberVar();
        String load = giveMeNumberVar();
        input("%"+alloca+" = alloca double\n"
    			+ "store double " + val +", double* %"+alloca+"\n"
    			+ "%"+load+" = load double, double* %"+alloca+"\n");
        stackNumber.push(load);
        
        stack.push(val);
    }

    public static void push(){
        dp = getAddressValue();
        
        String alloca = giveMeNumberVar();
        String load = giveMeNumberVar();
        input("%"+alloca+" = alloca i32\n"
        		+ "store i32 %" + dataArrayTemp[dp] +", i32* %"+alloca+"\n"
				+ "%"+load+" = load i32, i32* %"+alloca+"\n");
        stackNumber.push(load);
        
        stack.push(getData(dp));
    }
    
    public static void pushreal(){
        dp = getAddressValue();
        
        String alloca = giveMeNumberVar();
        String load = giveMeNumberVar();
        input("%"+alloca+" = alloca double\n"
        		+ "store double %" + dataArrayTemp[dp] +", double* %"+alloca+"\n"
				+ "%"+load+" = load double, double* %"+alloca+"\n");
        stackNumber.push(load);
        
        stack.push(getData(dp));
    }

    public static Object pop(){
        Object val = stack.pop();
        String alloca = (String) stackNumber.pop();
        dp = getAddressValue();
        dataArrayTemp[dp] = alloca;
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
    
    private static Stack<Integer> repeatJumpFunc = new Stack<Integer>();
    
    public static void jmpf(){
    	repeatJumpFunc.push(ip);
        ip = getAddressValue();
    }
    
    public static void jmpz(){
    	ip = repeatJumpFunc.pop()+4;
    }
    
    public static void halt() {
    	AllProgram+="ret i32 0\n}";
        System.out.println("\nProgram finished with exit code 0");
        System.out.println(AllProgram);
        try(FileWriter writer = new FileWriter("output.ll", false))
        {
            writer.write(AllProgram);  
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
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
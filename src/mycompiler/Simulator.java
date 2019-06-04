package mycompiler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Stack;

public class Simulator {

    private static int ip = 0;
    private static int dp = 0;

    private static Stack<Object> stack = new Stack<>();
    private static Stack<String> stackNumber = new Stack<>();
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
                case ISCALLREAL:
                	callint = false;
                	break;
                case ISCALLINT:
                	callint = true;
                	break;
                case PUSHINT:
                	pushint();
                	break;
                case PUSHREAL:
                	pushreal();
                	break;
                case PUSHVARFUNC:
                	pushvarfunc();
                	break;
                case PUSHINTLIT:
                    pushi();
                    break;
                case PUSHFLOATLIT:
                    pushf();
                    break;
                case PUSHVARFROMDECL:
                	pushvarfromdecl();
                	break;
                case REPLACERESULT:
                	replaceresult();
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
                case FUNCTIONSTARTINT:
                	startint = true;
                	funcstartint();
                	vardecl="";
                	break;
                case FUNCTIONSTARTREAL:
                	startint = false;
                	funcstartreal();
                	break;	
                case FUNCTIONCALL:
                	funccall();
                	break;
                case FUNCRETURN:
                	funcreturn = true;
                	break;
                case FUNCTIONENDINT:
                	funcendint();
                	break;
                case FUNCTIONENDREAL:
                	funcendreal();
                	break;
                case STARTPROGRAM:
                	start();
                	break;
                case INTVAR:
                	vardecl+="i32";
                	stackNumber.push(String.valueOf(KolvoVar));
                	KolvoVar++;
                	break;
                case COMMA:
                	vardecl+=", ";
                	break;
                case REALVAR:
                	KolvoVar++;
                	stackNumber.push(String.valueOf(KolvoVar));
                	vardecl+="double";
                	break;
                case STARTVARDECL:
                	KolvoVar=0;
                	break;
                case ENDVARDECL:
                	vardecl+="){\n";
                	break;
                case FORSTART:
                	dp = getAddressValue();
                	String load = giveMeNumberVar();
                	String alloca = giveMeNumberVar();
                	String popStack = dataArrayTemp[dp];
                	String label = giveMeNumberVar();
                	input("\n;ForStart\n%"+load + " = load i32, i32* %"+popStack+"\n"
                			+ "%"+alloca+" = alloca i32\n"
                			+ "store i32 %"+load+", i32* %"+alloca+"\n"
                					+ "br label %"+label+"\n\n"
                							+ ";label (forcmp) %"+label+"\n");
                	dataArrayTemp[dp]=alloca;
                	tested.push(alloca);
                	stackNumber.push(label);
                	break;
                case FORTO:
                	String load1 = giveMeNumberVar();
                	String load2 = giveMeNumberVar();
                	String icmp = giveMeNumberVar();
                	String forto = stackNumber.pop();
                	String label1 = giveMeNumberVar();
                	String replaceLabel = getFor();
                	
                	String probuu2 = tested.pop();
                	
                	input("%"+load1+" = load i32, i32* %"+probuu2+"\n"
                			+ "%"+load2+" = load i32, i32* %"+forto+"\n"
                			+ "%"+icmp+" = icmp sle i32 %"+load1+", %"+load2+"\n"
                					+ "br i1 %"+icmp+", label %"+label1+", label %"+replaceLabel+"\n\n"
                							+ ";Label (fordo) %"+label1+"\n");
                	StackforTo.push(replaceLabel);
                	tested.push(probuu2);
                	break;
                case FORBEGIN:
                	break;
                case FOREND:
                	String label2 = giveMeNumberVar();
                	input("br label %"+label2+"\n\n"
                			+ ";Label (+1) %"+label2+"\n");
                	String load3 = giveMeNumberVar();
                	String add = giveMeNumberVar();
                	String label3 = stackNumber.pop();
                	String label4 = giveMeNumberVar();
                	String replaceLabel2 = StackforTo.pop();
                	AllProgram=AllProgram.replace(replaceLabel2, label4);
                	String probuu = tested.pop();
                	input("%"+load3+" = load i32, i32* %"+probuu+"\n"
                			+ "%"+add+" = add nsw i32 %"+load3+", 1\n"
                					+ "store i32 %"+add+", i32* %"+probuu+"\n"
                							+ "br label %"+label3+"\n\n"
                									+ ";label (forend) %"+label4+"\n");
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
                default:
                    throw new Error(String.format("Unhandled case: %s", opCode));
            }
        }
        while (opCode != Parser.OP_CODE.HALT);
    }
    
    private static Stack<String> tested = new Stack<String>();
    
	static class iffen{
    	String ifen;
    	String number;
    	iffen (String Ifen, String Number){
    		this.ifen = Ifen;
    		this.number = Number;
    	}
    }
    
    private static Stack<String> StackforTo = new Stack<String>();
    private static int forToo = 0;
    private static String getFor() {
    	String get = "$$"+forToo+"$$";
    	forToo++;
    	return get;
    }
    
    private static ArrayList<Boolean> isIfelse = new ArrayList<Boolean>();
    private static ArrayList<String> poryadok = new ArrayList<String>();
    private static Stack<String[]> te2 = new Stack<String[]>();
    
    private static void ifthen() {
    	poryadok.add("then");
    	if (!te2.isEmpty()) {
        	dataArrayTemp = te2.pop();
        	te2.push(dataArrayTemp.clone());
    	}
    	else
    		te2.push(dataArrayTemp.clone());
    	
    	andIf=false;
    	String temp = (String) stackNumber.pop();
    	input("\n;Label %"+temp+" ifthen\n");
    }
    
    private static void ifelse() {
    	dataArrayTemp = te2.pop();
    	te2.push(dataArrayTemp.clone());
    	poryadok.add("else");
    	String temp = (String) giveMeNumberVar();
    	stackNumber.push(temp);
    	String labelunknown = getIf();
    	input("br label %"+labelunknown+"\n");
    	input("\n;Label %"+temp+" ifelse\n");
    	iff.push(labelunknown);
    }
    
    private static void ifend() {
    	if (!forandIf.isEmpty()) {
    		String temp = (String) giveMeNumberVar();
    		for (int i=0; i<forandIf.size(); i++)
    			AllProgram+=temp+"\n"+forandIf.get(i);
    		schetAnd=-1;
    		forandIf.clear();
    	}
    	String temp = (String) giveMeNumberVar();
    	input("br label %"+temp+"\n");
    	input("\n;Label %"+temp+" ifend\n");
    	String prob="";
    	String prob1="";
    	if (!stackNumber.isEmpty()) {
    		prob = iff.pop();
    		prob1 = stackNumber.pop();
    		AllProgram=AllProgram.replace(prob, temp);
    	}
    	prob = iff.pop();
    	AllProgram=AllProgram.replace(prob, prob1);
    	dataArrayTemp = te2.pop();
    	te2.push(dataArrayTemp.clone());
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
    
    private static void replaceresult() {
		String alloca = (String) stackNumber.pop();
		dp = getAddressValue();
		dataArrayTemp[dp]=alloca;
	}

	private static String vardecl = "";

	private static void start() {
		KolvoVar=0;
		AllProgram+="\ndefine i32 @main() {\n";
	}

    private static int sizefunc = 0;
    private static String giveNameFunction() {
    	String nameFunc = "Func"+sizefunc;
    	sizefunc++;
    	return nameFunc;
    }
    
    private static boolean callint = true;
    
	private static void funccall() {
		String kolvoparametrov = (String) stackNumber.pop();
		String numberFunc = (String) stackNumber.pop();
		String alloca = giveMeNumberVar();
		String type;
		if (callint) type="i32";
		else type="double";
		
		input("%"+alloca+" = call "+type+" @Func"+numberFunc+"(");
		for (int i=0; i<Integer.parseInt(kolvoparametrov);i++) {
			String parametr = (String) stackNumber.firstElement();
			stackNumber.remove(0);
			input("i32 %"+parametr);
			if (Integer.parseInt(kolvoparametrov)-1>i)
				input(", ");
		}
		input(")\n");
		stackNumber.push(alloca);
	}
    
	private static boolean funcreturn = false;
    private static void funcendint() {
    	dp = getAddressValue();
    	if (funcreturn) AllProgram+="ret i32 %"+dataArrayTemp[dp]+"\n}\n";
    	else AllProgram+="ret i32 "+0+"\n}\n";
	}
    
    private static void funcendreal() {
    	dp = getAddressValue();
    	if (funcreturn) AllProgram+="ret double %"+dataArrayTemp[dp]+"\n}\n";
    	else AllProgram+="ret double "+0+"\n}\n";
    	
	}
    
    private static void funcstartint() {
		AllProgram+="define i32 @"+giveNameFunction()+"("+vardecl;
	}
    
    private static void funcstartreal() {
		AllProgram+="define double @"+giveNameFunction()+"("+vardecl;
	}

	private static void lessEqlfor() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
        stack.push(val1 <= val2);
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
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        input("%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = icmp eq i32 %"+alloca+", %"+alloca2+"\n"
        						+ "br i1 %"+alloca3+", label %"+alloca4+", label %");
        stackNumber.push(alloca4);
    }

    private static void neqlIf() {
        String t2 = (String) stackNumber.pop();
        String t1 = (String) stackNumber.pop();
        String temp = giveMeNumberVar();
        String alloca = giveMeNumberVar();
        input("%"+temp+" = icmp ne i32 %"+t1+", %"+t2+"\n"
            		+"br i1 %"+temp+", label %"+alloca+", label %");
        stackNumber.push(alloca);
        getAddressValue();
    }
    
    private static void less() {
        Integer intVal2 = (Integer) stack.pop();
        Float val2 = (float) intVal2;
        Integer intVal1 = (Integer) stack.pop();
        Float val1 = (float) intVal1;
      
        stack.push(val1 < val2);
    }

    private static void lessIf() {
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        String alloca5 = getIf();
        input("%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = icmp slt i32 %"+alloca+", %"+alloca2+"\n"
        						+ "br i1 %"+alloca3+", label %"+alloca4+", label %"+alloca5+"\n");
        iff.push(alloca5);
        stackNumber.push(alloca4);
    }
    
    private static Stack<String> iff = new Stack<String>();
    private static int forIf = 0;
    private static String getIf() {
    	String get = "$&"+forIf+"$&";
    	forIf++;
    	return get;
    }
   
    private static void greaterIf() {
        String t2 = (String) stackNumber.pop();
        String t1 = (String) stackNumber.pop();
        String temp = giveMeNumberVar();
        String alloca = giveMeNumberVar();
        input("%"+temp+" = icmp sgt i32 %"+t1+", %"+t2+"\n"
            		+"br i1 %"+temp+", label %"+alloca+", label %");
        stackNumber.push(alloca);
    }

    private static void lessEqlIf() {
        String t2 = (String) stackNumber.pop();
        String t1 = (String) stackNumber.pop();
        String temp = giveMeNumberVar();
        String alloca = giveMeNumberVar();
        input("%"+temp+" = icmp sle i32 %"+t1+", %"+t2+"\n"
            		+"br i1 %"+temp+", label %"+alloca+", label %");
        stackNumber.push(alloca);
    }

    private static void greaterEqlIf() {
        String t2 = (String) stackNumber.pop();
        String t1 = (String) stackNumber.pop();
        String temp = giveMeNumberVar();
        String alloca = giveMeNumberVar();
        input("%"+temp+" = icmp sge i32 %"+t1+", %"+t2+"\n"
            		+"br i1 %"+temp+", label %"+alloca+", label %");
        stackNumber.push(alloca);
    }
   
    private static String input(String text) {
    	if (isIfelse.isEmpty() && !andIf && !orIf) {
    		AllProgram+=text;
    		return "AllProgram";
    	}
    	else if (andIf){
    			forandIf.set(schetAnd, forandIf.get(schetAnd)+text);
    			return "andIf";
    		} else if (orIf) {
    			fororIf.set(schetOr, fororIf.get(schetOr)+text);
    			return "orIf";
    		}
    		else return null;
    }

    private static void printReal() {
    	String alloca = giveMeNumberVar();
    	String intVar = (String) stackNumber.pop();
    	String alloca2 = giveMeNumberVar();
        input("%"+alloca+" = load double, double* %"+intVar+"\n"
        		+ "%"+alloca2+" = call i32 (i8*, ...) "
				+ "@printf(i8* getelementptr inbounds ([4 x i8], "
				+ "[4 x i8]* @.strfloat, i32 0, i32 0), i32 %"+alloca+")\n");
    }
    
    private static void printLn() {
    	String alloca = giveMeNumberVar();
    	input("%"+alloca+" = call i32 (i8*, ...) "
    			+ "@printf(i8* getelementptr inbounds "
    			+ "([2 x i8], [2 x i8]* @.strln, i32 0, i32 0))\n");
    }

    public static void printInt(){
    	String alloca = giveMeNumberVar();
    	String intVar = (String) stackNumber.pop();
    	String alloca2 = giveMeNumberVar();
        input("%"+alloca+" = load i32, i32* %"+intVar+"\n"
        		+ "%"+alloca2+" = call i32 (i8*, ...) "
				+ "@printf(i8* getelementptr inbounds ([4 x i8], "
				+ "[4 x i8]* @.strint, i32 0, i32 0), i32 %"+alloca+")\n");
    }

    public static void add(){
        String var1 = (String) stackNumber.pop();
        String var2 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        input("%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = add i32 %"+alloca+", %"+alloca2+"\n"
        		+ "%"+alloca4+" = alloca i32\n"
				+ "store i32 %"+alloca3+", i32* %"+alloca4+"\n");
        
        stackNumber.push(alloca4);
    }

    private static void fadd() {
        String var1 = (String) stackNumber.pop();
        String var2 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        input("%"+alloca+" = fadd double %"+var1+", %"+var2+"\n");
        stackNumber.push(alloca);
    }

    public static void sub(){
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        input("%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = sub i32 %"+alloca+", %"+alloca2+"\n"
        				+ "%"+alloca4+" = alloca i32\n"
        						+ "store i32 %"+alloca3+", i32* %"+alloca4+"\n");
        stackNumber.push(alloca4);
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
        String var1 = (String) stackNumber.pop();
        String var2 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        input("%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = mul i32 %"+alloca+", %"+alloca2+"\n"
        				+ "%"+alloca4+" = alloca i32\n"
        						+ "store i32 %"+alloca3+", i32* %"+alloca4+"\n");
        stackNumber.push(alloca4);
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
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        input("%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = div i32 %"+alloca+", %"+alloca2+"\n"
        				+ "%"+alloca4+" = alloca i32\n"
        						+ "store i32 %"+alloca3+", i32* %"+alloca4+"\n");
        stackNumber.push(alloca4);
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
        
        String t1 = stackNumber.pop();
        String t2 = stackNumber.pop();
        
        stackNumber.push(t1);
        stackNumber.push(t2);
        
        stack.push(val1);
        stack.push(val2);
    }
    
    private static int KolvoVar=0;
    
    private static String AllProgram="@.strln = private unnamed_addr constant [2 x i8] c\"\\0A\\00\"\n\n"
    		+ "@.strint = private unnamed_addr constant [4 x i8] c\"%i\\0A\\00\"\n\n"
    		+ "@.strfloat = private unnamed_addr constant [4 x i8] c\"%f\\0A\\00\"\n\n"
    		+ "declare i32 @printf(i8*, ...)\n\n";
    
    private static String giveMeNumberVar() {
    	String temp="";
    	KolvoVar++;
    	temp=Integer.toString(KolvoVar);
    	return temp;
    }

    private static boolean startint = true;
    public static void pushi(){
        int val = getAddressValue();
		String alloca = giveMeNumberVar();
		//String temp = giveMeNumberVar();
    	if (startint) {
	        input("%"+alloca+" = alloca i32\n"
	    			+ "store i32 " + val +", i32* %"+alloca+"\n");
	        //dataArrayTemp[dp] = alloca;
	        stackNumber.push(alloca);
    	} else {
	        input("%"+alloca+" = alloca double\n"
	    			+ "store double " + val +", double* %"+alloca+"\n");
	        stackNumber.push(alloca);
    	}
        stack.push(val);
    }
    
    public static void pushint(){
        int val = getAddressValue();
        stackNumber.push(String.valueOf(val));
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

    public static void pushvarfromdecl(){
        dp = getAddressValue();
        String temp = (String) stackNumber.firstElement();
        stackNumber.remove(0);
        String alloca = giveMeNumberVar();

        vardecl+="%"+alloca+" = alloca i32\n"
        		+ "store i32 %"+temp+", i32* %"+alloca+"\n";
        dataArrayTemp[dp] = alloca;
    }
    
    public static void pushvarfunc(){
        dp = getAddressValue();
        
        String alloca = giveMeNumberVar();
        String temp = (String) dataArrayTemp[dp];
        String load = giveMeNumberVar();
        
        input("%"+alloca+" = alloca i32\n"
        		+ "store i32 %" + temp +", i32* %"+alloca+"\n"
				+ "%"+load+" = load i32, i32* %"+alloca+"\n");
        
        stackNumber.push(load);
    }

    public static void push() {
    	dp = getAddressValue();
        stackNumber.push(dataArrayTemp[dp]);
    }
    
    public static void pushreal(){
        dp = getAddressValue();
        String alloca = giveMeNumberVar();
        String load = giveMeNumberVar();
        input("%"+alloca+" = alloca double\n"
        		+ "store double %" + dataArrayTemp[dp] +", double* %"+alloca+"\n"
				+ "%"+load+" = load double, double* %"+alloca+"\n");
        stackNumber.push(load);
    }
    
    public static void pop(){
        String alloca = (String) stackNumber.pop();
        dp = getAddressValue();
        String alloca1 = giveMeNumberVar();
        if (dataArrayTemp[dp]==null)
        	dataArrayTemp[dp] = alloca;
        input("%"+alloca1+" = load i32, i32* %"+alloca+"\n");
        input("store i32 %"+alloca1+", i32* %"+dataArrayTemp[dp]+"\n");
    }

    public static void halt() {
    	AllProgram+="ret i32 0\n}";
        System.out.println("\nProgram finished with exit code 0");
        System.out.println(AllProgram);
        try(FileWriter writer = new FileWriter("output.ll", false))
        {
            //writer.write(AllProgram);  
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
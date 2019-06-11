package mycompiler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Stack;

import javax.sound.sampled.LineEvent.Type;

public class Generator {

    private static int ip = 0;
    private static int dp = 0;

    private static Stack<String> stackNumber = new Stack<>();
    private static String[] dataArrayTemp = new String[1000];
    private static String[] dataArrayType = new String[1000];
    private static Byte[] dataArray = new Byte[1000];
    private static Byte[] instructions;

    public static void generate() {
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
                case EQL:
                    eql(IsIf);
                	break;
                case NEQL:
                    neql(IsIf);
                    break;
                case LSS:
                    less(IsIf);
                    break;
                case LEQ:
                    lessEql(IsIf);
                    break;
                case GTR:
                    greater(IsIf);
                    break;
                case GEQ:
                    greaterEql(IsIf);
                    break;
                case WHILECMP:
                	whilecmp();
                	break;
                case WHILEBEGIN:
                	String label6 = stackNumber.pop();
                	AllProgram+=";Label %"+label6+"\n";
                	break;
                case WHILEEND:
                	whileend();
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
                	funcstartint();
                	vardecl="";
                	break;
                case FUNCTIONSTARTREAL:
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
                	forstart();
                	break;
                case FORTO:
                	forto();
                	break;
                case FORBEGIN:
                	break;
                case FOREND:
                	forend();
                	break;
                case IFCMP:
                	IsIf=true;
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
                case BREAK:
                	breaK();
                	break;
                case CONTINUE:
                	continuE();
                	break;
                case AND:
                	String tempest = stackNumber.pop();
                	AllProgram+=";Label "+tempest+" AND\n";
                	stackNumber.push(tempest);
                	break;
                case OR:
                	String tempest2 = stackNumber.pop();
                	AllProgram+=";Label "+tempest2+" OR\n";
                	stackNumber.push(tempest2);
                	break;
                case STARTGLOBALVARS:
                	while (!vars.isEmpty()) {
                		if (vars.get(0).getName().equals("Stop") && vars.get(0).getDataType().equals(Parser.TYPE.STOP)) {
                			vars.remove(0);
                			break;
                		}
                		AllProgram+="@\""+vars.get(0).getName()+"\" = internal global i32 undef\n";
                		dataArrayTemp[vars.get(0).getAddress()] = "@\""+vars.get(0).getName()+"\"";
                		vars.remove(0);
                	}
                	break;
                case STARTFUNCVARS:
                	while (!vars.isEmpty()) {
                		if (vars.get(0).getName().equals("Stop") && vars.get(0).getDataType().equals(Parser.TYPE.STOP)) {
                			vars.remove(0);
                			break;
                		}
                		AllProgram+="@\""+vars.get(0).getName()+"\" = alloca i32\n";
                		dataArrayTemp[vars.get(0).getAddress()] = "@\""+vars.get(0).getName()+"\"";
                		vars.remove(0);
                	}
                	break;
                default:
                    throw new Error(String.format("Unhandled case: %s", opCode));
            }
        }
        while (opCode != Parser.OP_CODE.HALT);
    }
    
    private static void whilecmp() {
    	IsIf=false;
    	String label5 = giveMeNumberVar();
    	AllProgram+="br label %"+label5+"\n\n"
    			+ ";Label %"+label5+"\n";
    	stackNumber.push(label5);
    }
    
    private static void continuE() {
    	IsNotContinue = false;
    	String label10 = getContinue();
    	AllProgram+="br label %"+label10+"\n";
        continueStackLabels.push(label10);
    }
    
    private static void breaK() {
    	IsNotBreak = false;
    	String label9 = getBreak();
    	AllProgram+="br label %"+label9+"\n";
        breakStackLabels.push(label9);
    }
    
    private static void forstart() {
    	dp = getAddressValue();
    	String load = giveMeNumberVar();
    	String alloca = giveMeNumberVar();
    	String popStack = dataArrayTemp[dp];
    	String label = giveMeNumberVar();
    	AllProgram+="\n;ForStart\n%"+load + " = load i32, i32* %"+popStack+"\n"
    			+ "%"+alloca+" = alloca i32\n"
    			+ "store i32 %"+load+", i32* %"+alloca+"\n"
    					+ "br label %"+label+"\n\n"
    							+ ";label (forcmp) %"+label+"\n";
    	dataArrayTemp[dp]=alloca;
    	tested.push(alloca);
    	stackNumber.push(label);
    }
    
    private static void forend() {
    	String label2 = giveMeNumberVar();
    	AllProgram+="br label %"+label2+"\n\n"
    			+ ";Label (+1) %"+label2+"\n";
    	String load3 = giveMeNumberVar();
    	String add = giveMeNumberVar();
    	String label3 = stackNumber.pop();
    	String label4 = giveMeNumberVar();
    	String replaceLabel2 = forStackLabels.pop();
    	AllProgram=AllProgram.replace(replaceLabel2, label4);
    	String probuu = tested.pop();
    	AllProgram+="%"+load3+" = load i32, i32* %"+probuu+"\n"
    			+ "%"+add+" = add nsw i32 %"+load3+", 1\n"
    					+ "store i32 %"+add+", i32* %"+probuu+"\n"
    							+ "br label %"+label3+"\n\n"
    									+ ";label (forend) %"+label4+"\n";
    }
    
    private static void forto() {
    	String load1 = giveMeNumberVar();
    	String load2 = giveMeNumberVar();
    	String icmp = giveMeNumberVar();
    	String forto = stackNumber.pop();
    	String label1 = giveMeNumberVar();
    	String replaceLabel = getFor();
    	String probuu2 = tested.pop();
    	AllProgram+="%"+load1+" = load i32, i32* %"+probuu2+"\n"
    			+ "%"+load2+" = load i32, i32* %"+forto+"\n"
    			+ "%"+icmp+" = icmp sle i32 %"+load1+", %"+load2+"\n"
    					+ "br i1 %"+icmp+", label %"+label1+", label %"+replaceLabel+"\n\n"
    							+ ";Label (fordo) %"+label1+"\n";
    	forStackLabels.push(replaceLabel);
    	tested.push(probuu2);
    }
    
    private static void whileend(){
    	String label7 = giveMeNumberVar();
    	String label8 = stackNumber.pop();
    	
    	AllProgram+="br label %"+label8+"\n";
    	if (!continueStackLabels.isEmpty()) {
    		String replaceLabel = continueStackLabels.pop();
    		AllProgram = AllProgram.replace(replaceLabel, label8);
    	}
    	
    	AllProgram+="\n;Label %"+label7+"\n";
    	String replaceLabel3 = whileStackLabels.pop();
    	AllProgram = AllProgram.replace(replaceLabel3, label7);
    	if (!breakStackLabels.isEmpty()) {
    		String replaceLabel = breakStackLabels.pop();
    		AllProgram = AllProgram.replace(replaceLabel, label7);
    	}
    }
    
    private static Stack<String> tested = new Stack<String>();
    private static boolean IsIf = true;
    
    private static Stack<String> continueStackLabels = new Stack<String>();
    private static int continueInt = 0;
    private static String getContinue() {
    	String get = "$CONTINUE"+continueInt+"CONTINUE$";
    	continueInt++;
    	return get;
    }
    
    private static Stack<String> breakStackLabels = new Stack<String>();
    private static int breakInt = 0;
    private static String getBreak() {
    	String get = "$BREAK"+breakInt+"BREAK$";
    	breakInt++;
    	return get;
    }
    
    private static Stack<String> forStackLabels = new Stack<String>();
    private static int forInt = 0;
    private static String getFor() {
    	String get = "$$"+forInt+"$$";
    	forInt++;
    	return get;
    }
    
    private static ArrayList<String> poryadok = new ArrayList<String>();
    private static Stack<String[]> dataArrayIf = new Stack<String[]>();
    
    private static void ifthen() {
    	poryadok.add("then");
    	if (!dataArrayIf.isEmpty()) {
        	dataArrayTemp = dataArrayIf.pop();
        	dataArrayIf.push(dataArrayTemp.clone());
    	}
    	else
    		dataArrayIf.push(dataArrayTemp.clone());
    	String temp = (String) stackNumber.pop();
    	AllProgram+=";Label %"+temp+" ifthen\n";
    }
    
    private static Stack<Boolean> elseOn = new Stack<Boolean>();
    private static void ifelse() {
    	dataArrayTemp = dataArrayIf.pop();
    	dataArrayIf.push(dataArrayTemp.clone());
    	poryadok.add("else");
    	String temp = (String) giveMeNumberVar();
    	ifStackLabels.push(temp);
    	String labelunknown = getIfLabel();
    	if (IsNotBreak && IsNotContinue) {
    		AllProgram+="br label %"+labelunknown+"\n";
    	}
    	else {
    		if (!IsNotBreak)
    			IsNotBreak = true;
    		if (!IsNotContinue)
    			IsNotContinue = true;
    	}
    	AllProgram+="\n;Label %"+temp+" ifelse\n";
    	ifStackLabels.push(labelunknown);
    	elseOn.push(true);
    }
    
    private static void ifend() {
    	String labelexit = (String) giveMeNumberVar();
    	String criptoExitStart=ifStackLabels.pop();
    	if (ifStackLabels.isEmpty()) {
    		AllProgram=AllProgram.replace(criptoExitStart, labelexit);
    	}
    	else {
    		if (!elseOn.isEmpty() && ifStackLabels.size()==3) {
	    		String labelElse = ifStackLabels.pop();
	    		String criptoExitThen = ifStackLabels.pop();
	    		String criptoExitElse = ifStackLabels.pop();
	    		AllProgram=AllProgram.replace(criptoExitStart, labelexit);
	    		AllProgram=AllProgram.replace(criptoExitThen, labelElse);
	    		AllProgram=AllProgram.replace(criptoExitElse, labelexit);
	    		elseOn.pop();
    		} else {
	    		String labelElse = ifStackLabels.pop();
	    		String criptoExitThen = ifStackLabels.pop();
	    		AllProgram=AllProgram.replace(criptoExitStart, labelexit);
	    		AllProgram=AllProgram.replace(criptoExitThen, labelElse);
    		}
    	}
    	if (IsNotBreak && IsNotContinue) {
    		AllProgram+="br label %"+labelexit+"\n";
    	}
    	else {
    		if (!IsNotBreak)
    			IsNotBreak = true;
    		if (!IsNotContinue)
    			IsNotContinue = true;
    	}

    	AllProgram+="\n;Label %"+labelexit+" ifend\n";
    	dataArrayTemp = dataArrayIf.pop();
    	//dataArrayIf.push(dataArrayTemp.clone());
    }
    
    private static boolean IsNotBreak = true;
    private static boolean IsNotContinue = true;
    
    private static void replaceresult() {
		String alloca = (String) stackNumber.pop();
		dp = getAddressValue();
		dataArrayTemp[dp]=alloca;
	}

	private static String vardecl = "";
	private static void start() {
		KolvoVar=0;
		

		
		AllProgram+=""
				+ "\ndefine i32 @main() {\n";
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
		String type;
		if (callint) type="i32";
		else type="double";
		for (int i=0; i<Integer.parseInt(kolvoparametrov);i++) {
			String alloca = giveMeNumberVar();
			String parametr = stackNumber.firstElement();
			AllProgram+="%"+alloca+" = load i32, i32* %"+parametr+"\n";
			stackNumber.remove(0);
			stackNumber.push(alloca);
		}
		String alloca2 = giveMeNumberVar();
		AllProgram+="%"+alloca2+" = call "+type+" @Func"+numberFunc+"(";
		for (int i=0; i<Integer.parseInt(kolvoparametrov);i++) {
			String parametr = stackNumber.firstElement();
			stackNumber.remove(0);
			AllProgram+="i32 %"+parametr;
			if (Integer.parseInt(kolvoparametrov)-1>i)
				AllProgram+=", ";
		}
		String alloca3 = giveMeNumberVar();
		AllProgram+=")\n"
				+ "%"+alloca3+" = alloca i32\n"
						+ "store i32 %"+alloca2+", i32* %"+alloca3+"\n";
		stackNumber.push(alloca3);
	}
    
	private static boolean funcreturn = false;
    private static void funcendint() {
    	dp = getAddressValue();
    	String alloca = giveMeNumberVar();
    	if (funcreturn) AllProgram+="%"+alloca+" = load i32, i32* "+dataArrayTemp[dp]+"\n"
    			+ "ret i32 %"+alloca+"\n}\n\n";
    	else AllProgram+="ret i32 "+0+"\n}\n\n";
	}
    
    private static void funcendreal() {
    	dp = getAddressValue();
    	String alloca = giveMeNumberVar();
    	if (funcreturn) AllProgram+="%"+alloca+" = load double, double* "+dataArrayTemp[dp]+"\n"
    			+ "ret double %"+alloca+"\n}\n";
    	else AllProgram+="ret double "+0+"\n}\n";
	}
    
    private static void funcstartint() {
    	
    	Symbol symbol = vars.get(0);
    	
		AllProgram+="define i32 @"+giveNameFunction()+"("+vardecl
				+"@\""+symbol.getName()+"\" = alloca i32\n";
		dataArrayTemp[symbol.getAddress()]="@\""+symbol.getName()+"\"";
		vars.remove(0);
		if (vars.get(0).getName().equals("Stop"))
			vars.remove(0);
	}
    
    private static void funcstartreal() {
		AllProgram+="define double @"+giveNameFunction()+"("+vardecl;
	}

    private static void eql(boolean IsIf) {
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        String alloca5;
        if (IsIf)
        	alloca5 = getIfLabel();
        else
        	alloca5 = getWhileLabel();
        AllProgram+="%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = icmp eq i32 %"+alloca+", %"+alloca2+"\n"
        						+ "br i1 %"+alloca3+", label %"+alloca4+", label %"+alloca5+"\n\n";
        if (IsIf)
        	ifStackLabels.push(alloca5);
        else
        	whileStackLabels.push(alloca5);
        stackNumber.push(alloca4);
    }

    private static void neql(boolean IsIf) {
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        String alloca5;
        if (IsIf)
        	alloca5 = getIfLabel();
        else
        	alloca5 = getWhileLabel();
        AllProgram+="%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = icmp ne i32 %"+alloca+", %"+alloca2+"\n"
        						+ "br i1 %"+alloca3+", label %"+alloca4+", label %"+alloca5+"\n\n";
        if (IsIf)
        	ifStackLabels.push(alloca5);
        else
        	whileStackLabels.push(alloca5);
        stackNumber.push(alloca4);
    }
    
    private static Stack<String> whileStackLabels = new Stack<String>();
    private static int WhileInt = 0;
    private static String getWhileLabel() {
    	String get = "$WHILE"+WhileInt+"WHILE$";
    	WhileInt++;
    	return get;
    }

    private static void less(boolean IsIf) {
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        String alloca5;
        if (IsIf)
        	alloca5 = getIfLabel();
        else
        	alloca5 = getWhileLabel();
        AllProgram+="%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = icmp slt i32 %"+alloca+", %"+alloca2+"\n"
        						+ "br i1 %"+alloca3+", label %"+alloca4+", label %"+alloca5+"\n\n";
        if (IsIf)
        	ifStackLabels.push(alloca5);
        else
        	whileStackLabels.push(alloca5);
        stackNumber.push(alloca4);
    }
    
    private static Stack<String> ifStackLabels = new Stack<String>();
    private static int IfInt = 0;
    private static String getIfLabel() {
    	String get = "$IF"+IfInt+"IF&";
    	IfInt++;
    	return get;
    }
   
    private static void greater(boolean IsIf) {
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        String alloca5;
        if (IsIf)
        	alloca5 = getIfLabel();
        else
        	alloca5 = getWhileLabel();
        AllProgram+="%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = icmp sgt i32 %"+alloca+", %"+alloca2+"\n"
        						+ "br i1 %"+alloca3+", label %"+alloca4+", label %"+alloca5+"\n\n";
        if (IsIf)
        	ifStackLabels.push(alloca5);
        else
        	whileStackLabels.push(alloca5);
        stackNumber.push(alloca4);
    }

    private static void lessEql(boolean IsIf) {
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        String alloca5;
        if (IsIf)
        	alloca5 = getIfLabel();
        else
        	alloca5 = getWhileLabel();
        AllProgram+="%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = icmp sle i32 %"+alloca+", %"+alloca2+"\n"
        						+ "br i1 %"+alloca3+", label %"+alloca4+", label %"+alloca5+"\n\n";
        if (IsIf)
        	ifStackLabels.push(alloca5);
        else
        	whileStackLabels.push(alloca5);
        stackNumber.push(alloca4);
    }

    private static void greaterEql(boolean IsIf) {
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        String alloca5;
        if (IsIf)
        	alloca5 = getIfLabel();
        else
        	alloca5 = getWhileLabel();
        AllProgram+="%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = icmp sge i32 %"+alloca+", %"+alloca2+"\n"
        						+ "br i1 %"+alloca3+", label %"+alloca4+", label %"+alloca5+"\n\n";
        if (IsIf)
        	ifStackLabels.push(alloca5);
        else
        	whileStackLabels.push(alloca5);
        stackNumber.push(alloca4);
    }

    private static void printReal() {
    	String alloca = giveMeNumberVar();
    	String floatVar = (String) stackNumber.pop();
    	String alloca2 = giveMeNumberVar();
        AllProgram+="%"+alloca+" = load double, double* %"+floatVar.replace("float", "")+"\n"
        		+ "%"+alloca2+" = call i32 (i8*, ...) "
				+ "@printf(i8* getelementptr inbounds ([4 x i8], "
				+ "[4 x i8]* @.strfloat, i32 0, i32 0), double %"+alloca+")\n";
    }
    
    private static void printLn() {
    	String alloca = giveMeNumberVar();
    	AllProgram+="%"+alloca+" = call i32 (i8*, ...) "
    			+ "@printf(i8* getelementptr inbounds "
    			+ "([2 x i8], [2 x i8]* @.strln, i32 0, i32 0))\n";
    }

    public static void printInt(){
    	String alloca = giveMeNumberVar();
    	String intVar = (String) stackNumber.pop();
    	String alloca2 = giveMeNumberVar();
        AllProgram+="%"+alloca+" = load i32, i32* "+intVar+"\n"
        		+ "%"+alloca2+" = call i32 (i8*, ...) "
				+ "@printf(i8* getelementptr inbounds ([4 x i8], "
				+ "[4 x i8]* @.strint, i32 0, i32 0), i32 %"+alloca+")\n";
    }

    public static void add(){
        String var1 = (String) stackNumber.pop();
        String var2 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        AllProgram+="%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = add i32 %"+alloca+", %"+alloca2+"\n"
        		+ "%"+alloca4+" = alloca i32\n"
				+ "store i32 %"+alloca3+", i32* %"+alloca4+"\n";
        
        stackNumber.push(alloca4);
    }

    private static void fadd() {
        String var1 = (String) stackNumber.pop();
        String var2 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        AllProgram+="%"+alloca+" = load double, double* %"+var1.replace("float", "")+"\n"
        		+ "%"+alloca2+" = load double, double* %"+var2.replace("float", "")+"\n"
        		+ "%"+alloca3+" = fadd double %"+alloca+", %"+alloca2+"\n"
        		+ "%"+alloca4+" = alloca double\n"
				+ "store double %"+alloca3+", double* %"+alloca4+"\n";
        
        stackNumber.push(alloca4+"float");
    }

    public static void sub(){
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        AllProgram+="%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = sub i32 %"+alloca+", %"+alloca2+"\n"
        				+ "%"+alloca4+" = alloca i32\n"
        						+ "store i32 %"+alloca3+", i32* %"+alloca4+"\n";
        stackNumber.push(alloca4);
    }

    public static void fsub(){
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        AllProgram+="%"+alloca+" = load double, double* %"+var1.replace("float", "")+"\n"
        		+ "%"+alloca2+" = load double, double* %"+var2.replace("float", "")+"\n"
        		+ "%"+alloca3+" = fsub double %"+alloca+", %"+alloca2+"\n"
        				+ "%"+alloca4+" = alloca double\n"
        						+ "store double %"+alloca3+", double* %"+alloca4+"\n";
        stackNumber.push(alloca4+"float");
    }

    public static void mult(){
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        AllProgram+="%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = mul i32 %"+alloca+", %"+alloca2+"\n"
        				+ "%"+alloca4+" = alloca i32\n"
        						+ "store i32 %"+alloca3+", i32* %"+alloca4+"\n";
        stackNumber.push(alloca4);
    }

    public static void fmult(){
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        AllProgram+="%"+alloca+" = load double, double* %"+var1.replace("float", "")+"\n"
        		+ "%"+alloca2+" = load double, double* %"+var2.replace("float", "")+"\n"
        		+ "%"+alloca3+" = fmul double %"+alloca+", %"+alloca2+"\n"
        				+ "%"+alloca4+" = alloca double\n"
        						+ "store double %"+alloca3+", double* %"+alloca4+"\n";
        stackNumber.push(alloca4+"float");
    }

    public static void div(){
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        AllProgram+="%"+alloca+" = load i32, i32* %"+var1+"\n"
        		+ "%"+alloca2+" = load i32, i32* %"+var2+"\n"
        		+ "%"+alloca3+" = div i32 %"+alloca+", %"+alloca2+"\n"
        				+ "%"+alloca4+" = alloca i32\n"
        						+ "store i32 %"+alloca3+", i32* %"+alloca4+"\n";
        stackNumber.push(alloca4);
    }
    
    public static void fdiv(){
        String var2 = (String) stackNumber.pop();
        String var1 = (String) stackNumber.pop();
        String alloca = giveMeNumberVar();
        String alloca2 = giveMeNumberVar();
        String alloca3 = giveMeNumberVar();
        String alloca4 = giveMeNumberVar();
        AllProgram+="%"+alloca+" = load double, double* %"+var1.replace("float", "")+"\n"
        		+ "%"+alloca2+" = load double, double* %"+var2.replace("float", "")+"\n"
        		+ "%"+alloca3+" = fdiv double %"+alloca+", %"+alloca2+"\n"
        				+ "%"+alloca4+" = alloca double\n"
        						+ "store double %"+alloca3+", double* %"+alloca4+"\n";
        stackNumber.push(alloca4+"float");
    }

    public static void cvr(){
        String intVar = (String) stackNumber.pop();
        String alloca1 = (String) giveMeNumberVar();
        String alloca2 = (String) giveMeNumberVar();
        String alloca3 = (String) giveMeNumberVar();
        
        AllProgram+="%"+alloca1+" = load i32, i32* %"+intVar+"\n"
        		+ "%"+alloca2+" = sitofp i32 %"+alloca1+" to double\n"
        				+ "%"+alloca3+" = alloca double\n" + 
        				"store double %"+alloca2+", double* %"+alloca3+"\n";
        
        stackNumber.push(alloca3+"float");
    }

    public static void xchg(){
        String t1 = stackNumber.pop();
        String t2 = stackNumber.pop();
        stackNumber.push(t1);
        stackNumber.push(t2);
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

    public static void pushi(){
        int val = getAddressValue();
		String alloca = giveMeNumberVar();
    	AllProgram+="%"+alloca+" = alloca i32\n"
	    		+ "store i32 " + val +", i32* %"+alloca+"\n";
	    stackNumber.push(alloca);
    }
    
    public static void pushint(){
        int val = getAddressValue();
        stackNumber.push(String.valueOf(val));
    }
    
    private static void pushf() {
        float val = getFloatValue();
		String alloca = giveMeNumberVar();

    	AllProgram+="%"+alloca+" = alloca double\n"
	    		+ "store double " + val +", double* %"+alloca+"\n";
    	
	    stackNumber.push(alloca+"float");
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
        
        AllProgram+="%"+alloca+" = alloca i32\n"
        		+ "store i32 %" + temp +", i32* %"+alloca+"\n"
				+ "%"+load+" = load i32, i32* %"+alloca+"\n";
        
        stackNumber.push(load);
    }

    public static void push() {
    	dp = getAddressValue();
        stackNumber.push(dataArrayTemp[dp]);
    }
    
    public static void pushreal(){
    	dp = getAddressValue();
        stackNumber.push(dataArrayTemp[dp]);
    }
    
    public static void pop(){
    	dp = getAddressValue();
    	

    	
        String alloca = (String) stackNumber.pop();
        String alloca1 = giveMeNumberVar();
        boolean integer=true;
        if (alloca.contains("float")) {
        	integer=false;
        }
        if (dataArrayTemp[dp]==null)
        	dataArrayTemp[dp] = alloca;

        if (integer) {
        	AllProgram+="%"+alloca1+" = load i32, i32* %"+alloca+"\n";
        	AllProgram+="store i32 %"+alloca1+", i32* "+dataArrayTemp[dp]+"\n";
        } else {
        	AllProgram+="%"+alloca1+" = load double, double* %"+alloca.replace("float", "")+"\n";
        	AllProgram+="store double %"+alloca1+", double* %"+dataArrayTemp[dp].replace("float", "")+"\n";
        }
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
        for (int i = 0; i < 4; i++)
            valArray[i] = instructions[ip++];
        return ByteBuffer.wrap(valArray).getInt();
    }

    public static float getFloatValue() {
        byte[] valArray = new byte[4];
        for (int i = 0; i < 4; i++)
            valArray[i] = instructions[ip++];
        return ByteBuffer.wrap(valArray).getFloat();
    }

    public static int getData(int dp) {
    	byte[] valArray = new byte[4];
		for (int i = 0; i < 4; i++)
			valArray[i] = dataArray[dp++];
        return ByteBuffer.wrap(valArray).getInt();
    }

    public static Parser.OP_CODE getOpCode(){
        return Parser.OP_CODE.values()[instructions[ip++]];
    }

    public static void setInstructions(Byte[] instructions) {
        Generator.instructions = instructions;
    }
    
    private static Stack<Symbol> vars = new Stack<Symbol>();
    public static void setVars(Stack<Symbol> vars) {
        Generator.vars = vars;
    }
}
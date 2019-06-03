package general;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public final class Parser {
    enum TYPE {
        I, R, B, LN, S, P, F     // integer, real, boolean, string, procedure, function
    }

    private static int dp = 0; // data pointer for vars

    private static final HashMap<String, TYPE> STRING_TYPE_HASH_MAP;
    static {
        STRING_TYPE_HASH_MAP = new HashMap<>();
        STRING_TYPE_HASH_MAP.put("integer", TYPE.I);
        STRING_TYPE_HASH_MAP.put("real", TYPE.R);
        STRING_TYPE_HASH_MAP.put("boolean", TYPE.B);
        STRING_TYPE_HASH_MAP.put("string", TYPE.S);
    }

    enum OP_CODE {
    PUSHINTLIT, //0
    PUSH, //1
    POP, //2
    PUSHFLOATLIT, //3 
    JMP, //4
    JFALSE, //5
    JTRUE, //6
    CVR, //7
    CVI, //8
    DUP, //9
    XCHG, //10
    REMOVE, //11
    ADD, //12
    SUB, //13
    MULT, //14
    DIV, //15
    NEG, //16
    OR, //17
    AND, //18
    FADD,  //19
    FSUB, //20
    FMULT, //21 
    FDIV, //22
    FNEG, //23
    EQL,  //24
    NEQL,  //25
    GEQ,  //26
    LEQ, //27
    GTR, //28
    LSS, //29
    FGTR, //30
    FLSS, //31
    HALT, //32
    PRINT_INT, //33
    PRINT_CHAR, //34
    PRINT_BOOL, //35
    PRINT_REAL, //36
    PRINT_NEWLINE, //37
    GET, //38
    PUT, //39
    JMPTOFUNCTION, //40
    IFTHEN, //41
    IFELSE, //42
    IFEND, //43
    JMPOUTFUNCTION, //44 
    FOREND, // 45
    LEQFOR, // 46
    EQLIF,  // 47
    NEQLIF,  // 48
    GEQIF,  //49
    LEQIF, //50
    GTRIF, //51
    LSSIF, //52
    FGTRIF, //53
    FLSSIF //54
, PUSHREAL //55
    }

    private static final int ADDRESS_SIZE = 4;

    private static Token currentToken;
    private static Iterator<Token> it;
    private static String Region;
    private static final int INSTRUCTION_SIZE = 1000;
    private static Byte[] byteArray = new Byte[INSTRUCTION_SIZE];
    private static int ip = 0;
    public static Byte[] parse() {
        getToken(); // Get initial token
        match("TK_PROGRAM");
        match("TK_IDENTIFIER");
        match("TK_SEMI_COLON");
        program();
        return byteArray;
    }

    public static void program() {
    	Region = "Global";
        declarations();
        begin();
    }

    public static void declarations() {
        while (true) {
            switch (currentToken.getTokenType()) {
                case "TK_VAR":
                    varDeclarations();
                    break;
                case "TK_FUNCTION":
                    funcDeclaration();
                    break;
                case "TK_CLOSE_PARENTHESIS":
                	return;
                case "TK_BEGIN":
                    return;
                case "TK_IDENTIFIER":
                	throw new Error(String.format("TK_IDENTIFIER '"+currentToken.getTokenValue()+"' sintax error"));
            }
        }
    }
    
    private static void funcDeclaration() {
        if (currentToken.getTokenType().equals("TK_FUNCTION")) {
            match("TK_FUNCTION");
            currentToken.setTokenType("TK_A_FUNC");
            String function = currentToken.getTokenValue();
            String functionResult = currentToken.getTokenValue()+"result";
            match("TK_A_FUNC");
            match("TK_OPEN_PARENTHESIS");
            Region = function;
            genOpCode(OP_CODE.JMP);
            int hole = ip;
            genAddress(0);
            varDeclarationsFunc();
            match("TK_CLOSE_PARENTHESIS");
            match("TK_COLON");
            String TK_RESULT = currentToken.getTokenType();
            if (!TK_RESULT.equals("TK_INTEGER") && !TK_RESULT.equals("TK_REAL"))
            	throw new Error("unknown type result function '"+function+"'");
            getToken();
            Region = function;
            Symbol varFunctionResult = new Symbol(functionResult,
                    "TK_A_VAR", Region,
                    STRING_TYPE_HASH_MAP.get(TK_RESULT.toLowerCase().substring(3)),
                    dp);
            dp += 4;
            if (SymbolTable.lookup(functionResult, varFunctionResult.getRegion()) == null) {
                SymbolTable.insert(varFunctionResult);
            } 
            match("TK_SEMI_COLON");
            Symbol symbol = new Symbol(function,
                    "TK_A_FUNC", "Global",
                    TYPE.I,
                   ip);
            Region = function;
            declarations();
            if (SymbolTable.lookup(function, "Global") == null) {
                SymbolTable.insert(symbol);
            }
            else {
            	throw new Error(String.format("function '"+function + "' already is defined!"));
            }
            match("TK_BEGIN");
            Region = function;
            statements();
            match("TK_END");
            match("TK_SEMI_COLON");
            genOpCode(OP_CODE.JMPOUTFUNCTION);
            symbol.setReturnAddress(ip);
            genAddress(0);
            int save = ip;
            ip = hole;
            genAddress(save);
            ip = save;
        }
    }

    private static final HashMap<String, Integer> ter = new HashMap<String, Integer>();
    private static ArrayList<ArrayList<String>> nameVarFunc = new ArrayList<ArrayList<String>>();
    
    public static void varDeclarationsFunc() {
    	Region="FUNCVAR"+Region;
    	ter.put(Region, nameVarFuncNow);
    	nameVarFunc.add(new ArrayList<String>());
        while(true) {
            ArrayList<Token> variablesArrayList = new ArrayList<>();
            while ("TK_IDENTIFIER".equals(currentToken.getTokenType())) {
                currentToken.setTokenType("TK_A_VAR");
                variablesArrayList.add(currentToken);
                nameVarFunc.get(nameVarFuncNow).add(currentToken.getTokenValue());
                match("TK_A_VAR");
                if ("TK_COMMA".equals(currentToken.getTokenType())) {
                    match("TK_COMMA");
                }
            }
            nameVarFuncNow++;
            if (currentToken.getTokenType().equals("TK_CLOSE_PARENTHESIS")) {
            	return;
            }
            match("TK_COLON");
            String dataType = currentToken.getTokenType();
            match(dataType);
            for (Token var : variablesArrayList) {
                Symbol symbol = new Symbol(var.getTokenValue(),
                        "TK_A_VAR", Region,
                        STRING_TYPE_HASH_MAP.get(dataType.toLowerCase().substring(3)),
                        dp);
                dp += 4;
                if (SymbolTable.lookup(var.getTokenValue(), symbol.getRegion()) == null) {
                    SymbolTable.insert(symbol);
                }
            }
        	if ("TK_VAR".equals(currentToken.getTokenType())) {
                match("TK_VAR");
            } else {
            	if ("TK_SEMI_COLON".equals(currentToken.getTokenType())) {
                    match("TK_SEMI_COLON");
                    }
               else {
                    break;
               }
            }
        }
    }
    
    public static void assignmentStatFunc(Symbol curr) {
    	Symbol	symbol =  SymbolTable.lookup(currentToken.getTokenValue(), "FUNCVAR"+Region);
        if (symbol==null)
        	symbol = SymbolTable.lookup(currentToken.getTokenValue(), Region);
        if (symbol==null)
        	symbol = SymbolTable.lookup(currentToken.getTokenValue(), "Global");
        if (symbol != null) {
            TYPE lhsType = symbol.getDataType();
            match("TK_A_VAR");
            TYPE rhsType = curr.getDataType();
            int lhsAddress2 = curr.getAddress();
            if (lhsType == rhsType) {
                genOpCode(OP_CODE.POP);
                genAddress(lhsAddress2);
            } else {
                throw new Error(String.format("LHS type (%s) is not equal to RHS type: (%s)", lhsType, rhsType));
            }
        }
    }
    
    public static void assignmentStatFunc2(Symbol curr, TYPE rhsType) {
            match("TK_INTLIT");
            TYPE lhsType = curr.getDataType();
            int lhsAddress2 = curr.getAddress();
            if (lhsType == rhsType) {
                genOpCode(OP_CODE.POP);
                genAddress(lhsAddress2);
            } else {
            	if (lhsType == TYPE.R && rhsType == TYPE.I) {
            		genOpCode(OP_CODE.CVR);
            		genOpCode(OP_CODE.POP);
                    genAddress(lhsAddress2);
            	}
            	else
            		throw new Error(String.format("Cannot convert from (%s) type to (%s) type:", lhsType, rhsType));
            }
    }
    
    public static void varDeclarations() {
        while(true) {
            if ("TK_VAR".equals(currentToken.getTokenType())) {
                match("TK_VAR");
            } else {
                break;
            }
            ArrayList<Token> variablesArrayList = new ArrayList<>();
            while ("TK_IDENTIFIER".equals(currentToken.getTokenType())) {
                currentToken.setTokenType("TK_A_VAR");
                variablesArrayList.add(currentToken);
                match("TK_A_VAR");
                if ("TK_COMMA".equals(currentToken.getTokenType())) {
                    match("TK_COMMA");
                }
            }
            match("TK_COLON");
            String dataType = currentToken.getTokenType();
            match(dataType);
            for (Token var : variablesArrayList) {
                Symbol symbol = new Symbol(var.getTokenValue(),
                        "TK_A_VAR", Region,
                        STRING_TYPE_HASH_MAP.get(dataType.toLowerCase().substring(3)),
                        dp);
                dp += 4;
                if (SymbolTable.lookup(var.getTokenValue(), symbol.getRegion()) == null) {
                    SymbolTable.insert(symbol);
                }
            }
            match("TK_SEMI_COLON");
        }
    }

    public static void begin(){
        match("TK_BEGIN");
        Region = "Global";
        statements();
        match("TK_END");
        match("TK_DOT");
        match("TK_EOF");
        genOpCode(OP_CODE.HALT);
    }


    private static Stack<String> forRegion = new Stack<String>();
    
    public static void statements(){
        while(!currentToken.getTokenType().equals("TK_END")) {
            switch (currentToken.getTokenType()) {
                case "TK_WHILE":
                    whileStat();
                    break;
                case "TK_IF":
                    ifStat();
                    break;
                case "TK_FOR":
                    forStat();
                    break;
                case "TK_WRITELN":
                    writeStat();
                    break;
                case "TK_IDENTIFIER":
                	Symbol symbol = null;
                	symbol = SymbolTable.lookup(currentToken.getTokenValue(), "FUNCVAR"+Region);
                    if (symbol == null)
                    	symbol = SymbolTable.lookup(currentToken.getTokenValue(), Region);
                    if (symbol == null)
                    	symbol = SymbolTable.lookup(currentToken.getTokenValue(), "Global");
                    if (symbol != null && (symbol.getRegion().equals(Region) || symbol.getRegion().equals("Global") || symbol.getRegion().equals("FUNCVAR"+Region))) {
                        currentToken.setTokenType(symbol.getTokenType());
                    }
                    else {
                    	throw new Error(String.format("TK_IDENTIFIER '"+currentToken.getTokenValue()+"' is undefined"));
                    }
                    break;
                case "TK_A_VAR":
                    assignmentStat();
                    break;
                case "TK_A_FUNC":
                	forRegion.push(Region);
                    functionStat();
                    break;
                case "TK_SEMI_COLON":
                    match("TK_SEMI_COLON");
                    break;
                default:
                    return;
            }
        }
    }
    
    private static int nameVarFuncNow=0;
    
    private static int adder() {
    	int index = 0;
        int nameVarNow = ter.get("FUNCVAR"+Region);
    	while (currentToken.getTokenType().equals("TK_IDENTIFIER") || currentToken.getTokenType().equals("TK_INTLIT")) {
    		Symbol	symbol21 =  SymbolTable.lookup(currentToken.getTokenValue(), "FUNCVAR"+Region);
    		
    		if (symbol21 == null)
    			symbol21 =  SymbolTable.lookup(currentToken.getTokenValue(), Region);
    		if (symbol21 == null) {
    			symbol21 =  SymbolTable.lookup(currentToken.getTokenValue(), "Global");
    		} 
    		TYPE t;
    		if (symbol21 != null) {
                currentToken.setTokenType("TK_A_VAR");
                genOpCode(OP_CODE.PUSH);
                Symbol t1 = SymbolTable.lookup(nameVarFunc.get(nameVarNow).get(index), "FUNCVAR"+Region);
                index++;
                genAddress(symbol21.getAddress());
                assignmentStatFunc(t1);
    		} else {
    			t = getLitType(currentToken.getTokenType());
    			assert t != null;
    			switch (t) {
                	case R:
                		genOpCode(OP_CODE.PUSHFLOATLIT);
                		genAddress(Float.valueOf(currentToken.getTokenValue()));
                		break;
                	case I:
                		genOpCode(OP_CODE.PUSHINTLIT);
                		Symbol t1=null;
                    	try {
                    		t1 = SymbolTable.lookup(nameVarFunc.get(nameVarNow).get(index), "FUNCVAR"+Region);
                    	}
                    	catch (Exception ss) {
                    		throw new Error(String.format("Function mnogo argumentov: '"+Region+"'"));
                    	}
                    	index++;
                    	genAddress(Integer.valueOf(currentToken.getTokenValue()));
                    	assignmentStatFunc2(t1, TYPE.I);
                    	break;
                	case B:
                		genOpCode(OP_CODE.PUSHINTLIT);
                		if (currentToken.getTokenValue().equals("true")) {
                			genAddress(1);
                		} else {
                			genAddress(0);
                		}
                		break;
    			}
    		}
        if (currentToken.getTokenType().equals("TK_COMMA")) {
        	getToken();
        }
    	}
    	if (index<nameVarFunc.get(nameVarNow).size()) {
    		throw new Error(String.format("Function malo argumentov: '"+Region+"'"));
    	}
    	return index;
    }
    
    private static void functionStat() {
        Symbol symbol = SymbolTable.lookup(currentToken.getTokenValue(), "Global");
        if (symbol != null) {
            int address = symbol.getAddress();
            match("TK_A_FUNC");
            match("TK_OPEN_PARENTHESIS");
            Region = symbol.getName();
            adder();
            match("TK_CLOSE_PARENTHESIS");
            match("TK_SEMI_COLON");
            genOpCode(OP_CODE.JMPTOFUNCTION);
            genAddress(address);
            int restore = ip;
            ip = symbol.getReturnAddress();
            genAddress(restore);
            ip = restore;
        }
        Region = forRegion.pop();
    }
    
    private static void forStat() {
        match("TK_FOR");
        String varName = currentToken.getTokenValue();
        currentToken.setTokenType("TK_A_VAR");
        assignmentStat();
        int target = ip;
        Symbol symbol = SymbolTable.lookup(varName, Region);
        if (symbol != null) {
            int address = symbol.getAddress();
            match("TK_TO");
            genOpCode(OP_CODE.PUSH);
            genAddress(address);
            genOpCode(OP_CODE.PUSHINTLIT);
            genAddress(Integer.valueOf(currentToken.getTokenValue()));
            genOpCode(OP_CODE.LEQFOR);
            match("TK_INTLIT");
            match("TK_DO");
            genOpCode(OP_CODE.JFALSE);
            int hole = ip;
            genAddress(0);
            match("TK_BEGIN");
            statements();
            match("TK_END");
            match("TK_SEMI_COLON");
            genOpCode(OP_CODE.PUSH);
            genAddress(address);
            genOpCode(OP_CODE.PUSHINTLIT);
            genAddress(1);
            genOpCode(OP_CODE.ADD);
            genOpCode(OP_CODE.POP);
            genAddress(address);
            genOpCode(OP_CODE.JMP);
            genAddress(target);
            int save = ip;
            ip = hole;
            genAddress(save);
            ip = save;
        }
    }

    private static void whileStat() {
        match("TK_WHILE");
        int target = ip;
        match("TK_OPEN_PARENTHESIS");
        C();
        match("TK_CLOSE_PARENTHESIS");
        match("TK_DO");
        genOpCode(OP_CODE.JFALSE);
        int hole = ip;
        genAddress(0);
        match("TK_BEGIN");
        statements();
        match("TK_END");
        match("TK_SEMI_COLON");
        genOpCode(OP_CODE.JMP);
        genAddress(target);
        int save = ip;
        ip = hole;
        genAddress(save);
        ip = save;
    }

    public static void ifStat(){
        match("TK_IF");
        match("TK_OPEN_PARENTHESIS");
        CIF();
        match("TK_CLOSE_PARENTHESIS");
        match("TK_THEN");
        match("TK_BEGIN");
        genOpCode(OP_CODE.IFTHEN);
        statements();
        match("TK_END");
        match("TK_SEMI_COLON");
        if(currentToken.getTokenType().equals("TK_ELSE")) {
            genOpCode(OP_CODE.IFELSE);
            int save = ip;
            genAddress(ip);
            ip = save;
            match("TK_ELSE");
            match("TK_BEGIN");
            statements();
            match("TK_END");
        }
        genOpCode(OP_CODE.IFEND);
    }

    public static void writeStat(){
        match("TK_WRITELN");
        match("TK_OPEN_PARENTHESIS");
        while (true) {
        	Symbol symbol = SymbolTable.lookup(currentToken.getTokenValue(), "FUNCVAR"+Region);
        	if (symbol == null) {
        		symbol = SymbolTable.lookup(currentToken.getTokenValue(), Region);
        	}
            if (symbol == null) {
            	symbol =  SymbolTable.lookup(currentToken.getTokenValue(), "Global");
            }
            TYPE t;
            if (symbol != null) {
                    currentToken.setTokenType("TK_A_VAR");
                    t = symbol.getDataType();
                    if (t==TYPE.R) {
                    	genOpCode(OP_CODE.PUSHREAL);
                    	genAddress(symbol.getAddress());
                    }
                    else {
                    	genOpCode(OP_CODE.PUSH);
                    	genAddress(symbol.getAddress());
                    }
                                
                    match("TK_A_VAR");
            } else {
                t = getLitType(currentToken.getTokenType());
                if (t == null) {
                	throw new Error("'"+currentToken.getTokenValue()+"' is not defined");
                }
                assert t != null;
                switch (t) {
                    case R:
                        genOpCode(OP_CODE.PUSHFLOATLIT);
                        genAddress(Float.valueOf(currentToken.getTokenValue()));
                        break;
                    case I:
                        genOpCode(OP_CODE.PUSHINTLIT);
                        genAddress(Integer.valueOf(currentToken.getTokenValue()));
                        break;
                    case B:
                        genOpCode(OP_CODE.PUSHINTLIT);
                        if (currentToken.getTokenValue().equals("true")) {
                            genAddress(1);
                        } else {
                            genAddress(0);
                        }
                        break;
                    case LN:
                    	break;
                    default:
                        throw new Error("Cannot write unknown type");
                }
                match(currentToken.getTokenType());
            }
            assert t != null;
            switch (t) {
                case I:
                    genOpCode(OP_CODE.PRINT_INT);
                    break;
                case R:
                    genOpCode(OP_CODE.PRINT_REAL);
                    break;
                case B:
                    genOpCode(OP_CODE.PRINT_BOOL);
                    break;
                case LN:
                	genOpCode(OP_CODE.PRINT_NEWLINE);
                	break;
                default:
                    throw new Error("Cannot write unknown type");
            }
            switch (currentToken.getTokenType()) {
                case "TK_COMMA":
                    match("TK_COMMA");
                    break;
                case "TK_CLOSE_PARENTHESIS":
                    match("TK_CLOSE_PARENTHESIS");
                    return;
                case "TK_SEMI_COLON":
                	return;
                default:
                    throw new Error(String.format("Current token type (%s) is neither TK_COMMA nor TK_CLOSE_PARENTHESIS", currentToken.getTokenType()));
            }
        }
    }

    public static void assignmentStat() {
    	Symbol symbol = SymbolTable.lookup(currentToken.getTokenValue(), "FUNCVAR"+Region);
    	if (symbol == null)
        	symbol = SymbolTable.lookup(currentToken.getTokenValue(), Region);
        if (symbol==null)
        	symbol = SymbolTable.lookup(currentToken.getTokenValue(), "Global");
        if (symbol != null) {
            TYPE lhsType = symbol.getDataType();
            int lhsAddress = symbol.getAddress();
            match("TK_A_VAR");
            match("TK_ASSIGNMENT");
            TYPE rhsType = E();
            if (lhsType == rhsType) {
                genOpCode(OP_CODE.POP);
                genAddress(lhsAddress);
            } else {
            	if (lhsType == TYPE.R && rhsType == TYPE.I) {
            		genOpCode(OP_CODE.CVR);
            		genOpCode(OP_CODE.POP);
                    genAddress(lhsAddress);
            	}
            	else
            		throw new Error(String.format("Cannot convert from (%s) type to (%s) type:", lhsType, rhsType));
            }
        }
    }
    
    public static ArrayList<TYPE> CIF(){
    	ArrayList<TYPE> type = new ArrayList<TYPE>();
        do {
        TYPE e1 = E();
        while (currentToken.getTokenType().equals("TK_LESS_THAN") ||
                currentToken.getTokenType().equals("TK_GREATER_THAN") ||
                currentToken.getTokenType().equals("TK_LESS_THAN_EQUAL") ||
                currentToken.getTokenType().equals("TK_GREATER_THAN_EQUAL") ||
                currentToken.getTokenType().equals("TK_EQUAL") ||
                currentToken.getTokenType().equals("TK_NOT_EQUAL")) {
            String pred = currentToken.getTokenType();
            match(pred);
            TYPE e2 = T();
            e1 = emitIf(pred, e1, e2);
            type.add(e1);
        }
        if (currentToken.getTokenType().equals("TK_AND")) {
        	genOpCode(OP_CODE.AND);
        	getToken();
        	continue;
        } else if (currentToken.getTokenType().equals("TK_OR")) {
        	genOpCode(OP_CODE.OR);
        	getToken();
        	continue;
        }
        else {
        	break;
        }
        } while (true);
        return type;
    }

    public static ArrayList<TYPE> C(){
    	ArrayList<TYPE> type = new ArrayList<TYPE>();
        do {
        TYPE e1 = E();
        while (currentToken.getTokenType().equals("TK_LESS_THAN") ||
                currentToken.getTokenType().equals("TK_GREATER_THAN") ||
                currentToken.getTokenType().equals("TK_LESS_THAN_EQUAL") ||
                currentToken.getTokenType().equals("TK_GREATER_THAN_EQUAL") ||
                currentToken.getTokenType().equals("TK_EQUAL") ||
                currentToken.getTokenType().equals("TK_NOT_EQUAL")) {
            String pred = currentToken.getTokenType();
            match(pred);
            TYPE e2 = T();
            e1 = emit(pred, e1, e2);
            type.add(e1);
        }
        if (currentToken.getTokenType().equals("TK_AND")) {
        	genOpCode(OP_CODE.AND);
        	getToken();
        	continue;
        } else if (currentToken.getTokenType().equals("TK_OR")) {
        	genOpCode(OP_CODE.OR);
        	getToken();
        	continue;
        }
        else {
        	break;
        }
        } while (true);
        return type;
    }

    public static TYPE E(){
        TYPE t1 = T();
        while (currentToken.getTokenType().equals("TK_PLUS") || currentToken.getTokenType().equals("TK_MINUS")) {
            String op = currentToken.getTokenType();
            match(op);
            TYPE t2 = T();
            t1 = emit(op, t1, t2);
        }
        return t1;
    }

    public static TYPE T() {
        TYPE f1 = F();
        while (currentToken.getTokenType().equals("TK_MULTIPLY") ||
                currentToken.getTokenType().equals("TK_DIVIDE") ||
                currentToken.getTokenType().equals("TK_DIV")) {
            String op = currentToken.getTokenType();
            match(op);
            TYPE f2 = F();
            f1 = emit(op, f1, f2);
        }
        return f1;
    }

    public static TYPE F() {
        switch (currentToken.getTokenType()) {
            case "TK_IDENTIFIER":
                Symbol symbol = SymbolTable.lookup(currentToken.getTokenValue(), "FUNCVAR"+Region);
                if (symbol == null)
                	symbol = SymbolTable.lookup(currentToken.getTokenValue(), Region);
                if (symbol == null)
                	symbol = SymbolTable.lookup(currentToken.getTokenValue(), "Global");
                if (symbol != null) {
                    if (symbol.getTokenType().equals("TK_A_VAR")) {
                        currentToken.setTokenType("TK_A_VAR");
                        genOpCode(OP_CODE.PUSH);
                        genAddress(symbol.getAddress());
                        match("TK_A_VAR");
                        return symbol.getDataType();
                    }
                    else if (symbol.getTokenType().equals("TK_A_FUNC")) {
                    	currentToken.setTokenType("TK_A_FUNC");
                    	forRegion.push(Region);
                    	functionStat();
                    	Symbol varFunctionResult = SymbolTable.lookup(symbol.getName()+"result", symbol.getName());
                    	if (varFunctionResult.getDataType().equals(TYPE.I))
                    		genOpCode(OP_CODE.PUSH);
                    	else if (varFunctionResult.getDataType().equals(TYPE.R))
                    		genOpCode(OP_CODE.PUSHREAL);
                    	
                    	genAddress(varFunctionResult.getAddress());
                        return varFunctionResult.getDataType();
                    }
                }  
                else {
                    throw new Error(String.format("Symbol not found (%s)", currentToken.getTokenValue()));
                }
            case "TK_INTLIT":
                genOpCode(OP_CODE.PUSHINTLIT);
                genAddress(Integer.valueOf(currentToken.getTokenValue()));
                match("TK_INTLIT");
                return TYPE.I;
            case "TK_FLOATLIT":
                genOpCode(OP_CODE.PUSHFLOATLIT);
                genAddress(Float.valueOf(currentToken.getTokenValue()));
                match("TK_FLOATLIT");
                return TYPE.R;
            case "TK_BOOLLIT":
                genOpCode(OP_CODE.PUSHINTLIT);
                genAddress(Boolean.valueOf(currentToken.getTokenValue()) ? 1 : 0);
                match("TK_BOOLLIT");
                return TYPE.B;
            case "TK_STRLIT":
                for (char c: currentToken.getTokenType().toCharArray()) {
                    genOpCode(OP_CODE.PUSHINTLIT);
                    genAddress(c);
                }
                match("TK_STRLIT");
                return TYPE.S;
            case "TK_NOT":
                match("TK_NOT");
                return F();
            case "TK_OPEN_PARENTHESIS":
                match("TK_OPEN_PARENTHESIS");
                TYPE t = E();
                match("TK_CLOSE_PARENTHESIS");
                return t;
            default:
                throw new Error("Unknown data type");
        }
    }
    
    public static TYPE emitIf(String op, TYPE t1, TYPE t2){
        switch (op) {
            case "TK_PLUS":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpCode(OP_CODE.ADD);
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FADD);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FADD);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    genOpCode(OP_CODE.FADD);
                    return TYPE.R;
                }
            case "TK_MINUS":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpCode(OP_CODE.SUB);
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FSUB);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FSUB);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    genOpCode(OP_CODE.FSUB);
                    return TYPE.R;
                }
            case "TK_MULTIPLY":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpCode(OP_CODE.MULT);
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FMULT);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FMULT);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    genOpCode(OP_CODE.FMULT);
                    return TYPE.R;
                }
            case "TK_DIVIDE":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.FDIV);
                    return TYPE.R;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FDIV);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FDIV);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    genOpCode(OP_CODE.FDIV);
                    return TYPE.R;
                }
            case "TK_DIV":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpCode(OP_CODE.DIV);
                    return TYPE.I;
                }
            case "TK_LESS_THAN":
                return emitBool(OP_CODE.LSSIF, t1, t2);
            case "TK_GREATER_THAN":
                return emitBool(OP_CODE.GTRIF, t1, t2);
            case "TK_LESS_THAN_EQUAL":
                return emitBool(OP_CODE.LEQIF, t1, t2);
            case "TK_GREATER_THAN_EQUAL":
                return emitBool(OP_CODE.GEQIF, t1, t2);
            case "TK_EQUAL":
                return emitBool(OP_CODE.EQLIF, t1, t2);
            case "TK_NOT_EQUAL":
                return emitBool(OP_CODE.NEQLIF, t1, t2);
        }
        return null;
    }

    public static TYPE emit(String op, TYPE t1, TYPE t2){
        switch (op) {
            case "TK_PLUS":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpCode(OP_CODE.ADD);
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FADD);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FADD);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    genOpCode(OP_CODE.FADD);
                    return TYPE.R;
                }
            case "TK_MINUS":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpCode(OP_CODE.SUB);
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FSUB);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FSUB);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    genOpCode(OP_CODE.FSUB);
                    return TYPE.R;
                }
            case "TK_MULTIPLY":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpCode(OP_CODE.MULT);
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FMULT);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FMULT);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    genOpCode(OP_CODE.FMULT);
                    return TYPE.R;
                }
            case "TK_DIVIDE":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.FDIV);
                    return TYPE.R;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    genOpCode(OP_CODE.XCHG);
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FDIV);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    genOpCode(OP_CODE.CVR);
                    genOpCode(OP_CODE.FDIV);
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    genOpCode(OP_CODE.FDIV);
                    return TYPE.R;
                }
            case "TK_DIV":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    genOpCode(OP_CODE.DIV);
                    return TYPE.I;
                }
            case "TK_LESS_THAN":
                return emitBool(OP_CODE.LSS, t1, t2);
            case "TK_GREATER_THAN":
                return emitBool(OP_CODE.GTR, t1, t2);
            case "TK_LESS_THAN_EQUAL":
                return emitBool(OP_CODE.LEQ, t1, t2);
            case "TK_GREATER_THAN_EQUAL":
                return emitBool(OP_CODE.GEQ, t1, t2);
            case "TK_EQUAL":
                return emitBool(OP_CODE.EQL, t1, t2);
            case "TK_NOT_EQUAL":
                return emitBool(OP_CODE.NEQL, t1, t2);
        }
        return null;
    }

    public static TYPE emitBool(OP_CODE pred, TYPE t1, TYPE t2) {
        if (t1 == t2) {
            genOpCode(pred);
            return TYPE.B;
        } else if (t1 == TYPE.I && t2 == TYPE.R) {
            genOpCode(OP_CODE.XCHG);
            genOpCode(OP_CODE.CVR);
            genOpCode(pred);
            return TYPE.B;
        } else if (t1 == TYPE.R && t2 == TYPE.I) {
            genOpCode(OP_CODE.CVR);
            genOpCode(pred);
            return TYPE.B;
        }
        return null;
    }

    public static void genOpCode(OP_CODE b){
        System.out.println(String.format("OP_CODE: %s", b));
        byteArray[ip++] = (byte)(b.ordinal());
    }

    public static void genAddress(int a){
//        System.out.println(String.format("ADDRESS_VALUE: %s", a));
        byte[] intBytes = ByteBuffer.allocate(ADDRESS_SIZE).putInt(a).array();
        for (byte b: intBytes) {
            byteArray[ip++] = b;
        }
    }

    public static void genAddress(float a){
//        System.out.println(String.format("ADDRESS_VALUE: %s", a));
        byte[] intBytes = ByteBuffer.allocate(ADDRESS_SIZE).putFloat(a).array();
        for (byte b: intBytes) {
            byteArray[ip++] = b;
        }
    }

    public static void getToken() {
        if (it.hasNext()) {
            currentToken =  it.next();
        }
    }

    public static void match(String tokenType) {
        if (!tokenType.equals(currentToken.getTokenType())) {
            throw new Error(String.format("Token type (%s) does not match current token type (%s)", tokenType, currentToken.getTokenType()));
        } else {
//            System.out.println(String.format("matched: %s", currentToken.getTokenType()));
            getToken();
        }
    }

    public static TYPE getLitType(String tokenType) {
        switch (tokenType) {
            case "TK_INTLIT":
                return TYPE.I;
            case "TK_FLOATLIT":
                return TYPE.R;
            case "TK_BOOLLIT":
                return TYPE.B;
            case "TK_CLOSE_PARENTHESIS":
            	return TYPE.LN;
            default:
                return null;
        }
    }

    public static void setTokenArrayListIterator(ArrayList<Token> tokenArrayList) {
        it = tokenArrayList.iterator();
    }
}
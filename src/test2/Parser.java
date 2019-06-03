package test2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public final class Parser {
    enum TYPE {
        I, R, B, S, P, F     // integer, real, boolean, string, procedure, function
    }

    private static final HashMap<String, TYPE> STRING_TYPE_HASH_MAP;
    
    static {
        STRING_TYPE_HASH_MAP = new HashMap<>();
        STRING_TYPE_HASH_MAP.put("integer", TYPE.I);
        STRING_TYPE_HASH_MAP.put("real", TYPE.R);
        STRING_TYPE_HASH_MAP.put("boolean", TYPE.B);
        STRING_TYPE_HASH_MAP.put("string", TYPE.S);
    }

    private static Token currentToken;
    private static Iterator<Token> it;
    private static String Region;
    public static ArrayList<String> parse() {
        getToken();
        match("TK_PROGRAM");
        match("TK_IDENTIFIER");
        match("TK_SEMI_COLON");
        program();
        return MASSIV;
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
            String functionIdent = currentToken.getTokenValue()+"result";
            match("TK_A_FUNC");
            match("TK_OPEN_PARENTHESIS");
            Region = function;
            varDeclarationsFunc();
            match("TK_CLOSE_PARENTHESIS");
            match("TK_COLON");
            match("TK_INTEGER");
            Region = function;
            Symbol symbol21 = new Symbol(functionIdent,
                    "TK_A_VAR", Region,
                    STRING_TYPE_HASH_MAP.get("TK_INTEGER".toLowerCase().substring(3)), 0);

            if (SymbolTable.lookup(functionIdent, symbol21.getRegion()) == null) {
                SymbolTable.insert(symbol21);
            }
            match("TK_SEMI_COLON");
            Symbol symbol = new Symbol(function,
                    "TK_A_FUNC", "Global",
                    TYPE.I, 0);
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
                        STRING_TYPE_HASH_MAP.get(dataType.toLowerCase().substring(3)), 0);
                if (SymbolTable.lookup(var.getTokenValue(), symbol.getRegion()) == null) {
                    SymbolTable.insert(symbol);
                }
            }
        	if ("TK_VAR".equals(currentToken.getTokenType())) {
                match("TK_VAR");
            } else {
                break;
            }
        }
    }
    
    public static void assignmentStatFunc(Symbol curr) {
        Symbol	symbol = SymbolTable.lookup(currentToken.getTokenValue(), Region);
        if (symbol==null)
        	symbol = SymbolTable.lookup(currentToken.getTokenValue(), "Global");
        if (symbol != null) {
            TYPE lhsType = symbol.getDataType();
            match("TK_A_VAR");
            TYPE rhsType = curr.getDataType();
            if (lhsType == rhsType) {
               
            } else {
                throw new Error(String.format("LHS type (%s) is not equal to RHS type: (%s)", lhsType, rhsType));
            }
        }
    }
    
    public static void assignmentStatFunc2(Symbol curr, TYPE lhsType) {
            match("TK_INTLIT");
            TYPE rhsType = curr.getDataType();
            if (lhsType == rhsType) {

            } else {
                throw new Error(String.format("LHS type (%s) is not equal to RHS type: (%s)", lhsType, rhsType));
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
                        STRING_TYPE_HASH_MAP.get(dataType.toLowerCase().substring(3)), 0);
                if (SymbolTable.lookup(var.getTokenValue(), symbol.getRegion()) == null) {
                    SymbolTable.insert(symbol);
                }
            }
            match("TK_SEMI_COLON");
        }
    }

    public static void begin(){
    	Region = "Global";
        match("TK_BEGIN");
        statements();
        match("TK_END");
        match("TK_DOT");
        match("TK_EOF");
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
    		Symbol	symbol21 =  SymbolTable.lookup(currentToken.getTokenValue(), Region);
    		if (symbol21 == null) {
    			symbol21 =  SymbolTable.lookup(currentToken.getTokenValue(), "Global");
    		} 
    		TYPE t;
    		if (symbol21 != null) {
                // variable
                currentToken.setTokenType("TK_A_VAR");
                Symbol t1 = SymbolTable.lookup(nameVarFunc.get(nameVarNow).get(index), "FUNCVAR"+Region);
                index++;
                assignmentStatFunc(t1);
    		} else {
    			// literal
    			t = getLitType(currentToken.getTokenType());
    			assert t != null;
    			switch (t) {
                	case R:

                		break;
                	case I:
                		Symbol t1=null;
                    	try {
                    		t1 = SymbolTable.lookup(nameVarFunc.get(nameVarNow).get(index), "FUNCVAR"+Region);
                    	}
                    	catch (Exception ss) {
                    		throw new Error(String.format("Function mnogo argumentov: '"+Region+"'"));
                    	}
                    	index++;
                    	assignmentStatFunc2(t1, TYPE.I);
                    	break;
                	case B:
                		if (currentToken.getTokenValue().equals("true")) {
                			
                		} else {
                			
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
            match("TK_A_FUNC");
            match("TK_OPEN_PARENTHESIS");
            Region = symbol.getName();
            
            adder();
            
            match("TK_CLOSE_PARENTHESIS");
            match("TK_SEMI_COLON");
        }
        Region = forRegion.pop();
    }

    private static void forStat() {
        match("TK_FOR");
        String varName = currentToken.getTokenValue();
        currentToken.setTokenType("TK_A_VAR");
        assignmentStat();
        Symbol symbol = SymbolTable.lookup(varName, Region);
        if (symbol != null) {
            match("TK_TO");
            match("TK_INTLIT");
            match("TK_DO");
            match("TK_BEGIN");
            statements();
            match("TK_END");
            match("TK_SEMI_COLON");
        }
    }

    private static void whileStat() {
        match("TK_WHILE");
        C();
        match("TK_DO");
        match("TK_BEGIN");
        statements();
        match("TK_END");
        match("TK_SEMI_COLON");
    }

    public static void ifStat(){
        match("TK_IF");
        match("TK_OPEN_PARENTHESIS");
        C(); //
        match("TK_CLOSE_PARENTHESIS");
        match("TK_THEN");
        match("TK_BEGIN");
        statements();
        match("TK_END");
        match("TK_SEMI_COLON");
        if(currentToken.getTokenType().equals("TK_ELSE")) {
            match("TK_ELSE");
            match("TK_BEGIN");
            statements();
            match("TK_END");
        }
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
                    // variable
                    currentToken.setTokenType("TK_A_VAR");
                    t = symbol.getDataType();
                    match("TK_A_VAR");
            } else {
                // literal
                t = getLitType(currentToken.getTokenType());
                if (t == null) {
                	throw new Error("'"+currentToken.getTokenValue()+"' is not defined");
                }
                assert t != null;
                switch (t) {
                    case R:

                        break;
                    case I:
                    	
                        break;
                    case B:
                        if (currentToken.getTokenValue().equals("true")) {
                            
                        } else {
                            
                        }
                        break;
                        
                    default:
                        throw new Error("Cannot write unknown type");
                }
                match(currentToken.getTokenType());
            }
            assert t != null;
            switch (t) {
                case I:
                	putinstruction("writelninteger");
                	putinstruction(symbol.getName());
                    break;
                case R:
                	putinstruction("writelnreal");
                	putinstruction(symbol.getName());
                    break;
                case B:
                	putinstruction("writelnbool");
                	putinstruction(symbol.getName());
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
    
    private static ArrayList<String> MASSIV = new ArrayList<String>();

    private static void putinarray(int mesto, String command) {
    	ArrayList<String> temp1 = new ArrayList<String>();
    	temp1.addAll(MASSIV.subList(0, mesto));
    	ArrayList<String> temp2 = new ArrayList<String>();
    	if (MASSIV.size()>mesto)
    		temp2.addAll(MASSIV.subList(mesto, MASSIV.size()-1));
    	
    	temp1.add(command);
    	MASSIV.clear();
    	MASSIV.addAll(temp1);
    	MASSIV.addAll(temp2);
    }
    
    private static void putinarray(int mesto, int data) {
    	ArrayList<String> temp1 = new ArrayList<String>();
    	temp1.addAll(MASSIV.subList(0, mesto));
    	ArrayList<String> temp2 = new ArrayList<String>();
    	if (MASSIV.size()>mesto)
    		temp2.addAll(MASSIV.subList(mesto, MASSIV.size()-1));
    	
    	temp1.add(String.valueOf(data));
    	MASSIV.clear();
    	MASSIV.addAll(temp1);
    	MASSIV.addAll(temp2);
    }
    
    private static void putinstruction (String instr) {
    	MASSIV.add(instr);
    }

    public static void assignmentStat() {
    	Symbol symbol = SymbolTable.lookup(currentToken.getTokenValue(), "FUNCVAR"+Region);
    	if (symbol == null)
        	symbol = SymbolTable.lookup(currentToken.getTokenValue(), Region);
        if (symbol==null)
        	symbol = SymbolTable.lookup(currentToken.getTokenValue(), "Global");
        if (symbol != null) {
            TYPE lhsType = symbol.getDataType();
            //int lhsAddress = symbol.getAddress();
            match("TK_A_VAR");
            match("TK_ASSIGNMENT");
            TYPE rhsType = E();
            if (lhsType == rhsType) {
            	putinstruction(symbol.getName());
            	putinstruction("prisv");
            	putinstruction("5");
            } else {
                throw new Error(String.format("LHS type (%s) is not equal to RHS type: (%s)", lhsType, rhsType));
            }
        }
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
        	getToken();
        	continue;
        } else if (currentToken.getTokenType().equals("TK_OR")) {
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
                        // variable
                        currentToken.setTokenType("TK_A_VAR");
                        match("TK_A_VAR");
                        return symbol.getDataType();
                    }
                    else if (symbol.getTokenType().equals("TK_A_FUNC")) {
                    	currentToken.setTokenType("TK_A_FUNC");
                    	forRegion.push(Region);
                    	functionStat();
                    	Symbol symbol21 = SymbolTable.lookup(symbol.getName()+"result", symbol.getName());
                    	
                        return symbol.getDataType();
                    }
                }  
                else {
                    throw new Error(String.format("Symbol not found (%s)", currentToken.getTokenValue()));
                }
            case "TK_INTLIT":
                match("TK_INTLIT");
                
                return TYPE.I;
            case "TK_FLOATLIT":
                match("TK_FLOATLIT");
                return TYPE.R;
            case "TK_BOOLLIT":
                match("TK_BOOLLIT");
                return TYPE.B;
            case "TK_STRLIT":
                for (char c: currentToken.getTokenType().toCharArray()) {
                	//
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
    
    public static TYPE emit(String op, TYPE t1, TYPE t2){
        switch (op) {
            case "TK_PLUS":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    return TYPE.R;
                }
            case "TK_MINUS":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    return TYPE.R;
                }
            case "TK_MULTIPLY":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    return TYPE.I;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    return TYPE.R;
                }
            case "TK_DIVIDE":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    return TYPE.R;
                } else if (t1 == TYPE.I && t2 == TYPE.R) {
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.I) {
                    return TYPE.R;
                } else if (t1 == TYPE.R && t2 == TYPE.R) {
                    return TYPE.R;
                }
            case "TK_DIV":
                if (t1 == TYPE.I && t2 == TYPE.I) {
                    return TYPE.I;
                }
            case "TK_LESS_THAN":
                return emitBool(t1, t2);
            case "TK_GREATER_THAN":
                return emitBool(t1, t2);
            case "TK_LESS_THAN_EQUAL":
                return emitBool(t1, t2);
            case "TK_GREATER_THAN_EQUAL":
                return emitBool(t1, t2);
            case "TK_EQUAL":
                return emitBool(t1, t2);
            case "TK_NOT_EQUAL":
                return emitBool(t1, t2);
        }
        return null;
    }

    public static TYPE emitBool(TYPE t1, TYPE t2) {
        if (t1 == t2) {
            return TYPE.B;
        } else if (t1 == TYPE.I && t2 == TYPE.R) {
            return TYPE.B;
        } else if (t1 == TYPE.R && t2 == TYPE.I) {
            return TYPE.B;
        }
        return null;
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
            default:
                return null;
        }
    }

    public static void setTokenArrayListIterator(ArrayList<Token> tokenArrayList) {
        it = tokenArrayList.iterator();
    }
}
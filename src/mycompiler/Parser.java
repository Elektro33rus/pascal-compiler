package mycompiler;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public final class Parser {
    enum TYPE {
        I, R, B, LN, S, P, F, STOP
    }

    private static final HashMap<String, TYPE> STRING_TYPE_HASH_MAP;
    static {
        STRING_TYPE_HASH_MAP = new HashMap<>();
        STRING_TYPE_HASH_MAP.put("integer", TYPE.I);
        STRING_TYPE_HASH_MAP.put("real", TYPE.R);
        STRING_TYPE_HASH_MAP.put("boolean", TYPE.B);
        STRING_TYPE_HASH_MAP.put("string", TYPE.S);
    }

    enum OP_CODE {
		STARTPROGRAM, FUNCTIONSTARTINT, FUNCTIONSTARTREAL, FUNCTIONENDINT, FUNCTIONENDREAL, 
		STARTVARDECL, INTVAR, REALVAR, COMMA, ENDVARDECL, PUSHVARFROMDECL, HALT, BREAK, 
		CONTINUE, PUSHREAL, PUSH, PUSHFLOATLIT, PUSHINTLIT, PUSHINT, FUNCTIONCALL, 
		FORSTART, FORTO, FORBEGIN, FOREND, WHILECMP, WHILEBEGIN, WHILEEND, IFCMP, 
		IFTHEN, IFELSE, IFEND, PRINT_INT, PRINT_REAL, PRINT_NEWLINE, FUNCRETURN, POP, 
		AND, OR, PUSHVARFUNC, ISCALLINT, ISCALLREAL, REPLACERESULT, ADD, XCHG, CVR, 
		FADD, SUB, FSUB, MULT, FMULT, FDIV, DIV, LSSIF, LSS, GTR, LEQ, GEQ, EQL, NEQL, STARTGLOBALVARS, STARTFUNCVARS
    }

    private static int dp = 0;
    private static final int ADDRESS_SIZE = 4;
    public static Stack<Symbol> arraySymbols = new Stack<Symbol>();
    private static Token currentToken;
    private static Iterator<Token> it;
    private static String Region;
    private static final int INSTRUCTION_SIZE = 1000;
    private static Byte[] byteArray = new Byte[INSTRUCTION_SIZE];
    private static int ip = 0;
    public static Byte[] parse() {
        getToken();
        match("TK_PROGRAM");
        match("TK_IDENTIFIER");
        match("TK_SEMI_COLON");
        program();
        return byteArray;
    }

    public static void program() {
    	Region = "Global";
        declarations();
        genOpCode(OP_CODE.STARTPROGRAM);
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
                {
        			String Row =  String.valueOf(currentToken.getLineRow()+1);
        			String Col = String.valueOf(currentToken.getLineCol()+1);
        			throw new Error(String.format("'"+currentToken.getTokenValue()+"' �������������� ������ ("+Row+" ������, "+Col+" �������)"));
        		}
            }
        }
    }
    
    private static int numberfunc = -1; 
    private static void funcDeclaration() {
        if (currentToken.getTokenType().equals("TK_FUNCTION")) {
        	numberfunc++;
            match("TK_FUNCTION");
            currentToken.setTokenType("TK_A_FUNC");
            String function = currentToken.getTokenValue();
            String functionResult = currentToken.getTokenValue()+"result";
            match("TK_A_FUNC");
            match("TK_OPEN_PARENTHESIS");
            Region = function;
            int kolvoparametrov = varDeclarationsFunc();
            match("TK_CLOSE_PARENTHESIS");
            match("TK_COLON");
            String TK_RESULT = currentToken.getTokenType();
            if (TK_RESULT.equals("TK_INTEGER"))
            	genOpCode(OP_CODE.FUNCTIONSTARTINT);
            else
            	if (TK_RESULT.equals("TK_REAL"))
                	genOpCode(OP_CODE.FUNCTIONSTARTREAL);
            	else {
        			String Row =  String.valueOf(currentToken.getLineRow()+1);
        			String Col = String.valueOf(currentToken.getLineCol()+1);
        			throw new Error(String.format("������� '"+function+"' ���������� ����������� ��������� (����� ���� int ��� real)"+ "("+Row+" ������, "+Col+" �������)"));
        		}
            getToken();
            Region = function;
            Symbol symbolFunctionResult = new Symbol(functionResult,
                    "TK_A_RESULT_VAR", Region,
                    STRING_TYPE_HASH_MAP.get(TK_RESULT.toLowerCase().substring(3)),
                    dp);
            dp += 4;
            if (SymbolTable.lookup(functionResult, symbolFunctionResult.getRegion()) == null) {
                SymbolTable.insert(symbolFunctionResult);
                arraySymbols.add(symbolFunctionResult);
                arraySymbols.add(new Symbol("Stop", null, null, TYPE.STOP, 0));
            }
            match("TK_SEMI_COLON");
            Symbol symbolFunction = new Symbol(function,
                    "TK_A_FUNC", "Global",
                    STRING_TYPE_HASH_MAP.get(TK_RESULT.toLowerCase().substring(3)),
                   ip);
            symbolFunction.setNumber(numberfunc);
            symbolFunction.setAmount(kolvoparametrov);
            Region = function;
            declarations();
            if (SymbolTable.lookup(function, "Global") == null)
                SymbolTable.insert(symbolFunction);
            else {
    			String Row =  String.valueOf(currentToken.getLineRow()+1);
    			String Col = String.valueOf(currentToken.getLineCol()+1);
    			throw new Error(String.format("������� '"+function + "' ��� ���������!"+ "("+Row+" ������, "+Col+" �������)"));
    		}
            match("TK_BEGIN");
            Region = function;
            statements();
            match("TK_END");
            match("TK_SEMI_COLON");
            if (TK_RESULT.equals("TK_INTEGER")) {
            	genOpCode(OP_CODE.FUNCTIONENDINT);
            	genAddress(symbolFunctionResult.getAddress());
            }
            else
            	if (TK_RESULT.equals("TK_REAL"))
                	genOpCode(OP_CODE.FUNCTIONENDREAL);
        }
    }

    public static int varDeclarationsFunc() {
    	Region="FUNCVAR"+Region;
    	ArrayList<Symbol> forvar = new ArrayList<Symbol>();
    	genOpCode(OP_CODE.STARTVARDECL);
    	while(true) {
            ArrayList<Token> variablesArrayList = new ArrayList<>();
            while ("TK_IDENTIFIER".equals(currentToken.getTokenType())) {
                currentToken.setTokenType("TK_A_FUNC_VAR");
                variablesArrayList.add(currentToken);
                match("TK_A_FUNC_VAR");
                if ("TK_COMMA".equals(currentToken.getTokenType()))
                    match("TK_COMMA");
            }
            if (variablesArrayList.size()==0)
            	break;
            match("TK_COLON");
            String dataType = currentToken.getTokenType();
            match(dataType);
            int kolvoVarFunc=0;
            for (Token var : variablesArrayList) {
            	kolvoVarFunc++;
            	if (dataType.equals("TK_INTEGER"))
            		genOpCode(OP_CODE.INTVAR);
            	else
                	if (dataType.equals("TK_REAL"))
                		genOpCode(OP_CODE.REALVAR);
                Symbol symbol = new Symbol(var.getTokenValue(),
                		var.getTokenType(), Region,
                        STRING_TYPE_HASH_MAP.get(dataType.toLowerCase().substring(3)),
                        dp);
                dp += 4;
                if (SymbolTable.lookup(var.getTokenValue(), symbol.getRegion()) == null) {
                    SymbolTable.insert(symbol);
                    forvar.add(symbol);
                }
                if (variablesArrayList.size()!=kolvoVarFunc)
                	genOpCode(OP_CODE.COMMA);
            }
            if (!currentToken.getTokenValue().equals("TK_CLOSE_PARENTHESIS"))
            	break;
        }
        genOpCode(OP_CODE.ENDVARDECL);
    	for (int i=0; i<forvar.size();i++) {
    		Symbol symbol = forvar.get(i);
    		genOpCode(OP_CODE.PUSHVARFROMDECL);
    		genAddress(symbol.getAddress());
        }
    	return forvar.size();
    }
    
    public static void varDeclarations() {
        while(true) {
            if ("TK_VAR".equals(currentToken.getTokenType()))
                match("TK_VAR");
            else
                break;
            ArrayList<Token> variablesArrayList = new ArrayList<>();
            while ("TK_IDENTIFIER".equals(currentToken.getTokenType())) {
                currentToken.setTokenType("TK_A_VAR");
                variablesArrayList.add(currentToken);
                match("TK_A_VAR");
                if ("TK_COMMA".equals(currentToken.getTokenType()))
                    match("TK_COMMA");
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
                    arraySymbols.add(symbol);
                }
            }
            match("TK_SEMI_COLON");
        }
    	arraySymbols.add(new Symbol("Stop", null, null, TYPE.STOP, 0));
    	if (Region.equals("Global"))
    		genOpCode(OP_CODE.STARTGLOBALVARS);
    	else 
    		genOpCode(OP_CODE.STARTFUNCVARS);
    	 
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
                case "TK_A_FUNC":
                	callfunc();
                	break;
                case "TK_IDENTIFIER":
                	Symbol symbol = findSymbol();
                    if (symbol != null && (symbol.getRegion().equals(Region) || symbol.getRegion().equals("Global") || symbol.getRegion().equals("FUNCVAR"+Region)))
                        currentToken.setTokenType(symbol.getTokenType());
                    else {
                    	if (currentToken.getTokenValue().equals("break")) {
                    		genOpCode(OP_CODE.BREAK);
                    		match("TK_IDENTIFIER");
                    	}
                    	else
                    		if (currentToken.getTokenValue().equals("continue")) {
                    			genOpCode(OP_CODE.CONTINUE);
                    			match("TK_IDENTIFIER");
                    		}
                    		else {
                    			String Row =  String.valueOf(currentToken.getLineRow()+1);
                    			String Col = String.valueOf(currentToken.getLineCol()+1);
                    			throw new Error(String.format("'"+currentToken.getTokenValue()+"' �� �������� ("+Row+" ������, "+Col+" �������)"));
                    		}
                    }
                    break;
                case "TK_A_FUNC_VAR":
                    assignmentStat();
                    break;
                case "TK_A_VAR":
                    assignmentStat();
                    break;
                case "TK_A_RESULT_VAR":
                	assignmentStat();
                	break;
                case "TK_SEMI_COLON":
                    match("TK_SEMI_COLON");
                    break;
                default:
                    return;
            }
        }
    }
    
    private static void callfunc() {
    	Symbol symbol = SymbolTable.lookup(currentToken.getTokenValue(), "Global");
    	int callnumber = symbol.getNumber();
    	match("TK_A_FUNC");
        match("TK_OPEN_PARENTHESIS");
        int kolvo=0;
        while (currentToken.getTokenType().equals("TK_IDENTIFIER") || 
        		currentToken.getTokenType().equals("TK_INTLIT") ||
        		currentToken.getTokenType().equals("TK_REALLIT")) {
        	kolvo++;
	    	Symbol parametr = findSymbol();
	        TYPE t;
	        if (parametr != null) {
	                currentToken.setTokenType("TK_A_VAR");
	                t = parametr.getDataType();
	                if (t==TYPE.R) {
	                	genOpCode(OP_CODE.PUSHREAL);
	                	genAddress(parametr.getAddress());
	                }
	                else {
	                	genOpCode(OP_CODE.PUSH);
	                	genAddress(parametr.getAddress());
	                }
	                match("TK_A_VAR");
		            if (currentToken.getTokenType().equals("TK_COMMA"))
		            	match(currentToken.getTokenType());
	        } else {
	            t = getLitType(currentToken.getTokenType());
	            if (t == null) {
        			String Row =  String.valueOf(currentToken.getLineRow()+1);
        			String Col = String.valueOf(currentToken.getLineCol()+1);
        			throw new Error(String.format("'"+currentToken.getTokenValue()+"' �� �������� ("+Row+" ������, "+Col+" �������)"));
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
	                case LN:
	                	break;
	                default:
	                {
            			String Row =  String.valueOf(currentToken.getLineRow()+1);
            			String Col = String.valueOf(currentToken.getLineCol()+1);
            			throw new Error(String.format("�� ���� ������� ����������� ��� ������" + "("+Row+ " ������, "+Col+" �������)"));
            		}
	            }
	            match(currentToken.getTokenType());
	            if (currentToken.getTokenType().equals("TK_COMMA"))
	            	match(currentToken.getTokenType());
	        }
        }
        match("TK_CLOSE_PARENTHESIS");
        if (kolvo < symbol.getAmount()) {
			String Row =  String.valueOf(currentToken.getLineRow()+1);
			String Col = String.valueOf(currentToken.getLineCol()+1);
			throw new Error(String.format("������������ ���������� ��� ������ ������� '" + symbol.getName()
			+ "' (������ " + kolvo + " ����� " + symbol.getAmount() + ")"+ "("+Row+" ������, "+Col+" �������)"));
		}
        if (kolvo > symbol.getAmount()) {
			String Row =  String.valueOf(currentToken.getLineRow()+1);
			String Col = String.valueOf(currentToken.getLineCol()+1);
			throw new Error(String.format("������� ����� ���������� ��� ������ ������� '" + symbol.getName()
			+ "' (������ " + kolvo + " ����� " + symbol.getAmount() + ")"+ "("+Row+" ������, "+Col+" �������)"));
		}  	
    	genOpCode(OP_CODE.PUSHINT);
    	genAddress(callnumber);
    	genOpCode(OP_CODE.PUSHINT);
    	genAddress(kolvo);
        genOpCode(OP_CODE.FUNCTIONCALL);
	}

	private static void forStat() {
        match("TK_FOR");
        String varName = currentToken.getTokenValue();
        currentToken.setTokenType("TK_A_VAR");
        Token token = currentToken;
        assignmentStat();
        Symbol symb = findSymbol(token);
        genOpCode(OP_CODE.FORSTART);
        genAddress(symb.getAddress());
        Symbol symbol = SymbolTable.lookup(varName, Region);
        if (symbol != null) {
        	match("TK_TO");
        	if (currentToken.getTokenType().equals("TK_INTLIT")) {
                genOpCode(OP_CODE.PUSHINTLIT);
                genAddress(Integer.valueOf(currentToken.getTokenValue()));
                genOpCode(OP_CODE.FORTO);
                match("TK_INTLIT");
        	}
        	else if (currentToken.getTokenType().equals("TK_IDENTIFIER")) {
        		genOpCode(OP_CODE.PUSH);
        		genAddress(findSymbol(currentToken).getAddress());
        		genOpCode(OP_CODE.FORTO);
        		match("TK_IDENTIFIER");
        	}
        	else {
    			String Row =  String.valueOf(currentToken.getLineRow()+1);
    			String Col = String.valueOf(currentToken.getLineCol()+1);
    			throw new Error(String.format("�������� ��� ������ ��� ����� for: "+currentToken.getTokenType() + "("+Row+" ������, "+Col+" �������)"));
    		} 
        	match("TK_DO");
        	match("TK_BEGIN");
        	genOpCode(OP_CODE.FORBEGIN);
        	statements();
        	match("TK_END");
        	genOpCode(OP_CODE.FOREND);
        	match("TK_SEMI_COLON");
        }
    }

    private static void whileStat() {
        match("TK_WHILE");
        match("TK_OPEN_PARENTHESIS");
        genOpCode(OP_CODE.WHILECMP);
        C();
        match("TK_CLOSE_PARENTHESIS");
        match("TK_DO");
        match("TK_BEGIN");
        genOpCode(OP_CODE.WHILEBEGIN);
        statements();
        match("TK_END");
        genOpCode(OP_CODE.WHILEEND);
        match("TK_SEMI_COLON");
    }

    public static void ifStat(){
        match("TK_IF");
        match("TK_OPEN_PARENTHESIS");
        genOpCode(OP_CODE.IFCMP);
        C();
        match("TK_CLOSE_PARENTHESIS");
        match("TK_THEN");
        match("TK_BEGIN");
        genOpCode(OP_CODE.IFTHEN);
        statements();
        match("TK_END");
        match("TK_SEMI_COLON");
        if(currentToken.getTokenType().equals("TK_ELSE")) {
            genOpCode(OP_CODE.IFELSE);
            match("TK_ELSE");
            match("TK_BEGIN");
            statements();
            match("TK_END");
        }
        genOpCode(OP_CODE.IFEND);
    }
    
    private static Symbol findSymbol() {
    	Symbol symbol = SymbolTable.lookup(currentToken.getTokenValue(), "FUNCVAR"+Region);
    	if (symbol == null)
    		symbol = SymbolTable.lookup(currentToken.getTokenValue(), Region);
        if (symbol == null)
        	symbol =  SymbolTable.lookup(currentToken.getTokenValue(), "Global");
        return symbol;
    }
    
    private static Symbol findSymbol(Token token) {
    	Symbol symbol = SymbolTable.lookup(token.getTokenValue(), "FUNCVAR"+Region);
    	if (symbol == null)
    		symbol = SymbolTable.lookup(token.getTokenValue(), Region);
        if (symbol == null)
        	symbol =  SymbolTable.lookup(token.getTokenValue(), "Global");
        return symbol;
    }

    public static void writeStat(){
        match("TK_WRITELN");
        match("TK_OPEN_PARENTHESIS");
        while (true) {
        	Symbol symbol = findSymbol();
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
        			String Row =  String.valueOf(currentToken.getLineRow()+1);
        			String Col = String.valueOf(currentToken.getLineCol()+1);
        			throw new Error(String.format("'"+currentToken.getTokenValue()+"' �� �������� ("+Row+" ������, "+Col+" �������)"));
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
                        if (currentToken.getTokenValue().equals("true"))
                            genAddress(1);
                        else
                            genAddress(0);
                        break;
                    case LN:
                    	break;
                    default:
            			String Row =  String.valueOf(currentToken.getLineRow()+1);
            			String Col = String.valueOf(currentToken.getLineCol()+1);
            			throw new Error(String.format("�� ���� ������� ����������� ��� ������" + "("+Row+ " ������, "+Col+" �������)"));
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
                case LN:
                	genOpCode(OP_CODE.PRINT_NEWLINE);
                	break;
                default:
        			String Row =  String.valueOf(currentToken.getLineRow()+1);
        			String Col = String.valueOf(currentToken.getLineCol()+1);
        			throw new Error(String.format("�� ���� ������� ����������� ��� ������" + "("+Row+ " ������, "+Col+" �������)"));
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
        			String Row =  String.valueOf(currentToken.getLineRow()+1);
        			String Col = String.valueOf(currentToken.getLineCol()+1);
        			throw new Error(String.format("������� ��� ������ (%s) �� �������� �� TK_COMMA, �� TK_CLOSE_PARENTHESIS"+currentToken.getTokenType()+" ("+Row+" ������, "+Col+" �������)"));
            }
        }
    }

    public static void assignmentStat() {
    	Symbol symbol = findSymbol();
        if (symbol != null) {
            TYPE lhsType = symbol.getDataType();
            int lhsAddress = symbol.getAddress();
            if (currentToken.getTokenType().equals("TK_A_VAR"))
            	match("TK_A_VAR");
            else
            	if (currentToken.getTokenType().equals("TK_A_RESULT_VAR")) {
            		match("TK_A_RESULT_VAR");
            		genOpCode(OP_CODE.FUNCRETURN);
            		symbol.setResult(true);
            	}
            	else 
            		if (currentToken.getTokenType().equals("TK_A_FUNC_VAR")) 
            			match("TK_A_FUNC_VAR");
            match("TK_ASSIGNMENT");
            TYPE rhsType = E();
            if (lhsType == rhsType) {
                genOpCode(OP_CODE.POP);
                genAddress(lhsAddress);
            }
            else if (lhsType == TYPE.R && rhsType == TYPE.I) {
            	genOpCode(OP_CODE.CVR);
                genOpCode(OP_CODE.POP);
                genAddress(lhsAddress);
            } 
            else {
    			String Row =  String.valueOf(currentToken.getLineRow()+1);
    			String Col = String.valueOf(currentToken.getLineCol()+1);
    			throw new Error(String.format("���������� ������������� ��� (%s) � ��� (%s ", lhsType, rhsType + ") ("+Row+" ������, "+Col+" �������)"));
    		}
        } else {
			String Row =  String.valueOf(currentToken.getLineRow()+1);
			String Col = String.valueOf(currentToken.getLineCol()+1);
			throw new Error(String.format("����������� ���������� '%s'", currentToken.getTokenValue()+ "("+Row+" ������, "+Col+" �������)"));
		}
    }
    
    public static TYPE C(){
    	TYPE type=null;
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
            type = e1;
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
        else
        	break;
        }
        while (true);
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
                Symbol symbol = findSymbol();
                if (symbol != null) {
                	if (symbol.getTokenType().equals("TK_A_FUNC_VAR")) {
                        currentToken.setTokenType("TK_A_FUNC_VAR");
                        genOpCode(OP_CODE.PUSH);
                        genAddress(symbol.getAddress());
                        match("TK_A_FUNC_VAR");
                        return symbol.getDataType();
                	}
                	else
                	if (symbol.getTokenType().equals("TK_A_RESULT_VAR")) {
                         currentToken.setTokenType("TK_A_RESULT_VAR");
                         genOpCode(OP_CODE.PUSHVARFUNC);
                         genAddress(symbol.getAddress());
                         match("TK_A_RESULT_VAR");
                         return symbol.getDataType();
                    }
                	else
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
                    	Symbol varFunctionResult = SymbolTable.lookup(symbol.getName()+"result", symbol.getName());
                    	if (varFunctionResult.getDataType()==TYPE.I)
                    		genOpCode(OP_CODE.ISCALLINT);
                    	else
                    		if (varFunctionResult.getDataType()==TYPE.R)
                    			genOpCode(OP_CODE.ISCALLREAL);
                    	callfunc();
                    	genOpCode(OP_CODE.REPLACERESULT);
                    	genAddress(varFunctionResult.getAddress());
                    	if (varFunctionResult.getDataType().equals(TYPE.I))
                    		genOpCode(OP_CODE.PUSH);
                    	else if (varFunctionResult.getDataType().equals(TYPE.R))
                    		genOpCode(OP_CODE.PUSHREAL);
                    	genAddress(varFunctionResult.getAddress());
                        return varFunctionResult.getDataType();
                    }
                }
                else {
        			String Row =  String.valueOf(currentToken.getLineRow()+1);
        			String Col = String.valueOf(currentToken.getLineCol()+1);
        			throw new Error(String.format("����������� ���������� '%s'", currentToken.getTokenValue()+ "("+Row+" ������, "+Col+" �������)"));
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
    			String Row =  String.valueOf(currentToken.getLineRow()+1);
    			String Col = String.valueOf(currentToken.getLineCol()+1);
    			throw new Error(String.format("����������� ��� ������ " + "("+Row+ " ������, "+Col+" �������)"));
        }
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
        byte[] intBytes = ByteBuffer.allocate(ADDRESS_SIZE).putInt(a).array();
        for (byte b: intBytes)
            byteArray[ip++] = b;
    }

    public static void genAddress(float a){
        byte[] intBytes = ByteBuffer.allocate(ADDRESS_SIZE).putFloat(a).array();
        for (byte b: intBytes)
            byteArray[ip++] = b;
    }

    public static void getToken() {
        if (it.hasNext())
            currentToken =  it.next();
    }

    public static void match(String tokenType) {
        if (!tokenType.equals(currentToken.getTokenType())) {
			String Row =  String.valueOf(currentToken.getLineRow()+1);
			String Col = String.valueOf(currentToken.getLineCol()+1);
			throw new Error(String.format("��� ������ "+tokenType+" �� ������������� �������� ������ "+currentToken.getTokenType()+" ("+Row+" ������, "+Col+" �������)"));
		}
        else
            getToken();
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
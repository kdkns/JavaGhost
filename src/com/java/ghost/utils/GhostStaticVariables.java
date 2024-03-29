package com.java.ghost.utils;

import com.java.ghost.QueryConstructors.Bless;
import com.java.ghost.Variable.Possess;

@SuppressWarnings("rawtypes")
public class GhostStaticVariables {
	
	public static final Class[] oracleCallableParams = { oracle.jdbc.OracleCallableStatement.class,
                                                         com.java.ghost.utils.GhostParameterArray.class};
	
	public static final Class[] oraclePreparedParams = { oracle.jdbc.OraclePreparedStatement.class,
                                                         com.java.ghost.utils.GhostParameterArray.class}; 

    public static final Class[] oracleResultSetParams = { oracle.jdbc.OracleResultSet.class,
                                                          com.java.ghost.utils.GhostParameterArray.class};

	public static final String CHAR_QUESTION_MARK = "?";    
    public static final String OPERATION_TOKEN = "<op>";
    public static final String CHAR_PLUS = "+";
    public static final String CHAR_MINUS = "-";
    public static final String CHAR_MULTIPLY = "*";
    public static final String CHAR_DIVIDE = "/";
	public static final String CHAR_COLAN =":";
    
	public static final Possess POSSESS = new Possess();
	public static final Bless BLESS = new Bless();
}


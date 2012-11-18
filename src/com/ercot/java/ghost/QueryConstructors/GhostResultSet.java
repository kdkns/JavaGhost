package com.ercot.java.ghost.QueryConstructors;

import java.math.BigDecimal;
import java.sql.SQLException;

import oracle.jdbc.OracleResultSet;

import org.apache.log4j.Logger;

public class GhostResultSet {
	private static Logger logger = Logger.getLogger("GhostResultSet");
	protected OracleResultSet _orset;
	
    
    
	public GhostResultSet(OracleResultSet orset) {
		_orset = orset;
	}

	public GhostResultSet() {
	}

	public boolean next(){
		try {
			return _orset.next();
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
		return false;
	}
	
	public void setResultSet(OracleResultSet orset) {
        try {
			if(_orset!=null){
				_orset.close();
			}
			_orset = orset;
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public void close(){
		 try {
				if(_orset!=null){
					_orset.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
	}

	public BigDecimal getColumnByName(String name) throws SQLException{
		return _orset.getBigDecimal(name);
	}
	
	public BigDecimal getColumnByPosition(int pos) throws SQLException{
		return _orset.getBigDecimal(pos);
	}

}

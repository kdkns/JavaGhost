package com.ercot.java.ghost.QueryConstructors;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ercot.java.ghost.DBStatementExecutor.DBStatementExecutor;
import com.ercot.java.ghost.Exceptions.GhostRuntimeException;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.utils.GDate;
import com.ercot.java.ghost.utils.IGDate;

public abstract class AbstractGhostQuery extends DBStatementExecutor{

	private Connection _conn = null;
	private PreparedStatement _opc = null;
	private ResultSet _grset = null;
	private boolean _canCallNext = false;
	
	protected Connection getConn() {
		return _conn;
	}

	protected PreparedStatement getOpc() {
		return _opc;
	}

	protected ResultSet getGrset() {
		return _grset;
	}

	protected boolean isCanCallNext() {
		return _canCallNext;
	}

	protected void setConn(Connection conn) {
		_conn = conn;
	}

	protected void setOpc(PreparedStatement opc) {
		_opc = opc;
	}

	protected void setGrset(ResultSet grset) {
		_grset = grset;
	}

	protected void setCanCallNext(boolean canCallNext) {
		_canCallNext = canCallNext;
	}
	
	protected boolean getCanCallNext() {
		return _canCallNext;
	}
		
	protected AbstractGhostQuery(Connection conn, PreparedStatement opc, ResultSet rs){
		setConn(conn);
		setOpc(opc);
		setGrset(rs);
		setCanCallNext(true);
	}
	
	protected AbstractGhostQuery() {};
	
	public void close(){
		setCanCallNext(false);
		try {
			if(getOpc()!= null){
				getOpc().close();
			}
			if(getGrset()!=null){
				getGrset().close();
		    }
			if(getConn()!=null){
				getConn().close();
				setConn(null);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
		
	}
	
	public boolean next(){
		if(getCanCallNext()){
			try {
				 return getGrset().next();
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			}
		}else{
			throw new GhostRuntimeException("Please check if you have executed the query first and that this query is of a Select Query type.");
		}
	}
	
//	public BigDecimal getNumberColumnByName(IMetaField columnName) {
//		try {
//			return _grset.getBigDecimal(columnName.getAlias());
//		} catch (SQLException e) {
//			logger.error(e.getMessage(),e);
//		}
//		return null;
//	}
//	
//	public String getStringColumnByName(IMetaField columnName) {
//		try {
//			return _grset.getString(columnName.getAlias());
//		} catch (SQLException e) {
//			logger.error(e.getMessage(),e);
//		}
//		return null;
//	}
//
//	public IGDate getDateColumnByName(IMetaField columnName) {
//		try {
//			return new GDate(_grset.getDate(columnName.getAlias()));
//		} catch (SQLException e) {
//			logger.error(e.getMessage(),e);
//		}
//		return null;
//	}
	
	public BigDecimal getNumberColumnByName(IMetaField columnName) {
		try {
			return getGrset().getBigDecimal(columnName.getAlias());
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	public String getStringColumnByName(IMetaField columnName) {
		try {
			return getGrset().getString(columnName.getAlias());
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}

	public IGDate getDateColumnByName(IMetaField columnName) {
		try {
			return new GDate(_grset.getTimestamp(columnName.getAlias()));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	public Object getObjectColumnByName(IMetaField columnName) {
		try {
			return _grset.getObject(columnName.getAlias());
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	public BigDecimal getNumberColumnByPosition(int position) {
		try {
			return _grset.getBigDecimal(position);
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	public String getStringColumnByPosition(int position) {
		try {
			return getGrset().getString(position);
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	public Object getObjectColumnByPosition(int position) {
		try {
			return getGrset().getObject(position);
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
		
}

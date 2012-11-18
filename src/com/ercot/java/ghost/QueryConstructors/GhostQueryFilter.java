package com.ercot.java.ghost.QueryConstructors;

import java.util.HashMap;
import java.util.Set;

import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.MetaField;
import com.ercot.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.ercot.java.ghost.Variable.IGhostCollection;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;
import com.ercot.java.ghost.utils.GhostPair;

public class GhostQueryFilter {
//	protected HashMap<IMetaField,String> _filterSQL;
	protected HashMap<Integer,GhostPair<IMetaField,String>> _filterSQL;
	protected static HashMap<FilterOperationTypes,String> _operationMap = new HashMap<FilterOperationTypes,String>();
	protected static HashMap<FilterUnaryOperationTypes,String> _operationUnaryMap = new HashMap<FilterUnaryOperationTypes,String>();
	protected static HashMap<FilterNaryOperationTypes,String> _operationNaryMap = new HashMap<FilterNaryOperationTypes,String>();
	
	public static enum FilterOperationTypes{
		EQUALS, LESSTHAN, GREATERTHAN, LESSTHANEQUALS, GREATERTHANEQUALS,
		NOTEQUALS, INCLAUSE, BETWEENCLAUSE, LIKE, NOTINCLAUSE
	}
	
	public static enum FilterUnaryOperationTypes{
		ROWNUM, ISNULL, EXISTS, NOTEXISTS, ISNOTNULL,COLLECTIONJOIN
	}
	
	public static enum FilterNaryOperationTypes{
		BETWEENDATES
	}
	
	static {	
		_operationMap.put(FilterOperationTypes.EQUALS," = ");
		_operationMap.put(FilterOperationTypes.GREATERTHAN," > ");
		_operationMap.put(FilterOperationTypes.GREATERTHANEQUALS," >= ");
		_operationMap.put(FilterOperationTypes.LESSTHAN," < ");
		_operationMap.put(FilterOperationTypes.LESSTHANEQUALS," <= ");
		_operationMap.put(FilterOperationTypes.NOTEQUALS," != ");		
		_operationMap.put(FilterOperationTypes.INCLAUSE," IN ");
		_operationMap.put(FilterOperationTypes.NOTINCLAUSE," NOT IN ");
		_operationMap.put(FilterOperationTypes.BETWEENCLAUSE," BETWEEN ");
		_operationMap.put(FilterOperationTypes.LIKE," LIKE ");
		
		_operationUnaryMap.put(FilterUnaryOperationTypes.COLLECTIONJOIN,"BITAND" + 
						                      GhostDBStaticVariables.OPEN_PARENTHESES +
						                      MetaTables.GHOST_VM.getGhostCollectionId() 
						                      + ", <CID>) = <CID>");
		
		
				
		_operationUnaryMap.put(FilterUnaryOperationTypes.ROWNUM," ROWNUM <= ");
		_operationUnaryMap.put(FilterUnaryOperationTypes.ISNULL," IS NULL ");
		_operationUnaryMap.put(FilterUnaryOperationTypes.ISNOTNULL," IS NOT NULL ");
		_operationUnaryMap.put(FilterUnaryOperationTypes.EXISTS," EXISTS ");
		_operationUnaryMap.put(FilterUnaryOperationTypes.NOTEXISTS," NOT EXISTS ");
		
		_operationNaryMap.put(FilterNaryOperationTypes.BETWEENDATES," BETWEEN ");
	}
	
	{	
		_filterSQL = new HashMap<Integer,GhostPair<IMetaField,String>>();
	}
	
	protected GhostPair<IMetaField, String> helperSQLConstructor(int pos, IMetaField field, String value, String clause, FilterOperationTypes fot){		
		return _filterSQL.put(pos, new GhostPair<IMetaField, String>(field,clause + field.getFullyQualifiedTableAliaisWithColumnName() +  _operationMap.get(fot) +  value ));
	}
	
	protected GhostPair<IMetaField, String> helperSQLConstructor(int pos, IMetaField field, java.lang.Number value, String clause, FilterOperationTypes fot){        
		return _filterSQL.put(pos, new GhostPair<IMetaField, String>(field,clause + field.getFullyQualifiedTableAliaisWithColumnName() +  _operationMap.get(fot) +  value ));
	}

    protected GhostPair<IMetaField, String> helperSQLConstructor(String leftWrap, String rightWrap, int pos, IMetaField field, String value, String clause, FilterOperationTypes fot){
		return _filterSQL.put(pos, new GhostPair<IMetaField, String>(field,clause + leftWrap + field.getFullyQualifiedTableAliaisWithColumnName() + rightWrap +  _operationMap.get(fot) +  value ));
	}
	
	protected GhostPair<IMetaField, String> helperSQLConstructor(int pos, MetaField field, MetaField field2,
		    String clause, FilterOperationTypes fot) {
			return _filterSQL.put(pos, new GhostPair<IMetaField, String>(field,clause + field.getFullyQualifiedTableAliaisWithColumnName() +  
					              _operationMap.get(fot) + field2.getFullyQualifiedTableAliaisWithColumnName()));
		}
	
	protected GhostPair<IMetaField, String> helperUnarySQLConstructorCollectionId(int pos, IMetaField field, IGhostCollection<?> collection, String clause){
		return _filterSQL.put(pos, new GhostPair<IMetaField, String>(field,clause + _operationUnaryMap.get(FilterUnaryOperationTypes.COLLECTIONJOIN).replaceAll("<CID>",collection.getCollectionId()+"")) );
	}
	
	protected GhostPair<IMetaField, String> helperUnarySQLConstructor(int pos, IMetaField field, java.lang.Number value, String clause, FilterUnaryOperationTypes fot){
		return _filterSQL.put(pos, new GhostPair<IMetaField, String>(field,clause + _operationUnaryMap.get(fot) +  value ));
	}
	
	protected GhostPair<IMetaField, String> helperUnarySQLConstructor(int pos, IMetaField field,String clause, FilterUnaryOperationTypes fot){
		return _filterSQL.put(pos, new GhostPair<IMetaField, String>(field,clause + field.getFullyQualifiedTableAliaisWithColumnName() + _operationUnaryMap.get(fot) ));
	}
	
	protected GhostPair<IMetaField, String> helperUnarySQLConstructor(int pos, String value ,String clause, FilterUnaryOperationTypes fot){
		return _filterSQL.put(pos, new GhostPair<IMetaField, String>(null,clause +  _operationUnaryMap.get(fot) + value ));
	}
	
	protected GhostPair<IMetaField, String> helperGroupSQLConstructor(int pos, String clause, String value){
		return _filterSQL.put(pos, new GhostPair<IMetaField, String>(null,clause + value ));
	}
	
	
	protected GhostPair<IMetaField, String> helperSQLConstructor(int pos,String value, String clause) {
		return _filterSQL.put(pos, new GhostPair<IMetaField, String>(null,clause + value )); 
	}

	
	public GhostPair<IMetaField, String> removeField(int pos){
		return _filterSQL.remove(pos);
	}
	
	public String getFilterSQL(){
		StringBuilder result = new StringBuilder();
		Set<Integer> s = _filterSQL.keySet();
		for(Integer i : s){
			result.append(_filterSQL.get(i));
		}
		return result.toString();
	}

	public void clear() {
		_filterSQL.clear();		
	}


}

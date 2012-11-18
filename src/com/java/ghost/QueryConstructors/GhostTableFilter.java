package com.java.ghost.QueryConstructors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oracle.sql.NUMBER;

import com.java.ghost.Exceptions.GhostQueryBuilderException;
import com.java.ghost.GhostFieldMaps.GhostBindFieldMap;
import com.java.ghost.GhostFieldMaps.GhostFieldMapObject;
import com.java.ghost.MetaTableTypes.GhostQueryTable;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.IMetaTable;
import com.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.java.ghost.Variable.IGhostCollection;
import com.java.ghost.utils.ClausableList;
import com.java.ghost.utils.GhostDBStaticVariables;
import com.java.ghost.utils.GhostListString;
import com.java.ghost.utils.GhostPair;
import com.java.ghost.utils.GhostStaticVariables;
import com.java.ghost.utils.IGDate;

public class GhostTableFilter extends GhostQueryFilter {
//	private static Logger logger = Logger.getLogger("GhostTableFilter");
//	private List<IMetaField> _bindList = new ArrayList<IMetaField>();
//	private List<IMetaField> _bindOrder = new Vector<IMetaField>();
	private HashMap<Integer,String> _statmentClauseType = new HashMap<Integer,String>(); 
	private HashMap<Integer,GhostPair<Integer,Integer>> _groupingMap = new HashMap<Integer,GhostPair<Integer,Integer>>();
	private HashMap<Integer,String> _groupingClauseMap = new HashMap<Integer,String>();
	
	private Set<IMetaField> _listOfCriteriaFields = new HashSet<IMetaField>();
	
	private GhostBindFieldMap _gbfm = new GhostBindFieldMap();
	private boolean _isBindMapSet = false;
	
	public boolean isBindMapSet(){
		return _isBindMapSet;
	}
	
	public GhostBindFieldMap getGhostBindFieldMap(){
		return _gbfm;
	}
	
	protected GhostPair<IMetaField, String> addAndBindFieldProtectedMethod(int pos, FilterOperationTypes fot, IMetaField field, String value){
		return helperSQLConstructor(pos, field, value, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, fot);
	}
	
	protected GhostPair<IMetaField, String> addBindField(int pos, String clause, FilterOperationTypes fot, IMetaField field, Object value) {
		_listOfCriteriaFields.add(field);
		if(value instanceof IMetaField){
			_listOfCriteriaFields.add((IMetaField)value);
		}
		_isBindMapSet = true;
//		_bindList.add(field);
		String rightValue = GhostStaticVariables.CHAR_QUESTION_MARK;//GhostStaticVariables.CHAR_COLAN + field.getColumnName();
		if(value instanceof IGDate){
			_gbfm.set(pos, field, new GhostFieldMapObject(GhostDBStaticVariables.wrapBindDate((IGDate)value)));
		}else{
			_gbfm.set(pos, field, new GhostFieldMapObject(value));
		}
		if(field.getType() == GhostDBStaticVariables.DBTypes.DATE){
			rightValue = GhostDBStaticVariables.wrapBindToDate(rightValue);
		}
		return helperSQLConstructor(pos, field, rightValue, clause, fot);
	}
	//TODO: Fix this to work with any type of function on a meta field
	protected GhostPair<IMetaField, String> addBindFieldCollectionId(int pos, String clause, FilterOperationTypes fot, IMetaField field, Object value) {
		_isBindMapSet = true;
		String rightValue = GhostStaticVariables.CHAR_QUESTION_MARK;//GhostStaticVariables.CHAR_COLAN + field.getColumnName();
		_gbfm.set(pos, field, new GhostFieldMapObject(value));
		_gbfm.set(pos+1, field, new GhostFieldMapObject(value));
		return helperSQLConstructor("BITAND(", GhostDBStaticVariables.COMMA + rightValue + GhostDBStaticVariables.CLOSE_PARENTHESES,
				                    pos, field, rightValue, clause, fot);
	}

	protected GhostPair<IMetaField, String> addAndBindFieldCollectionId(int pos, FilterOperationTypes fot, IMetaField field, Object value) {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return addBindFieldCollectionId(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, fot, field, value);
	}
	
	public GhostPair<IMetaField, String> addAndBindField(int pos, FilterOperationTypes fot, IMetaField field, Object value) {
//		_listOfCriteriaFields.add(field);
//		if(value instanceof IMetaField){
//			_listOfCriteriaFields.add((IMetaField)value);
//		}
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return addBindField(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, fot, field, value);
	}
	
	public GhostPair<IMetaField, String> addOrBindField(int pos, FilterOperationTypes fot, IMetaField field, Object value) {
//		_listOfCriteriaFields.add(field);
//		if(value instanceof IMetaField){
//			_listOfCriteriaFields.add((IMetaField)value);
//		}
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return addBindField(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE, fot, field, value);
	}
	
	public GhostPair<IMetaField, String> addAndBindField(int pos, FilterOperationTypes fot, IMetaField field) {
		_listOfCriteriaFields.add(field);
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return addBindField(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, fot, field, null);
	}
	
	public GhostPair<IMetaField, String> addOrBindField(int pos, FilterOperationTypes fot, IMetaField field)  {
		_listOfCriteriaFields.add(field);
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return addBindField(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE, fot, field, null);
	}
	
	public GhostPair<IMetaField, String> addBetweenBindField(int pos, IMetaField field, String clause, Object start, Object end) {
		_listOfCriteriaFields.add(field);
		
		_isBindMapSet = true;
		String correctedDateClause = GhostStaticVariables.CHAR_QUESTION_MARK;//GhostStaticVariables.CHAR_COLAN + field.getColumnName();
		
		if(start instanceof IGDate){
			_gbfm.set(pos, field, new GhostFieldMapObject(GhostDBStaticVariables.wrapBindDate((IGDate)start)));
		}else{
			_gbfm.set(pos, field, new GhostFieldMapObject(start));
		}
		
		if(start instanceof IGDate){
			_gbfm.set(pos+1, field, new GhostFieldMapObject(GhostDBStaticVariables.wrapBindDate((IGDate)end)));
		}else{
			_gbfm.set(pos+1, field, new GhostFieldMapObject(end));
		}
		
		if(field.getType() == GhostDBStaticVariables.DBTypes.DATE){
			correctedDateClause = GhostDBStaticVariables.wrapBindToDate(correctedDateClause);
		}
//		_bindList.add(field);
		return helperSQLConstructor(pos,
				                    field, 
				                    correctedDateClause + 
				                    GhostDBStaticVariables.GHOST_DB_ANDCLAUSE +
				                    correctedDateClause,
				                    clause,
				                    FilterOperationTypes.BETWEENCLAUSE);
	}
	
	public GhostPair<IMetaField, String> addAndBetweenBindField(int pos, IMetaField field, Object start, Object end)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return addBetweenBindField(pos, field, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, start, end);
	}
	
	public GhostPair<IMetaField, String> addOrBetweenBindField(int pos, IMetaField field, Object start, Object end)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return addBetweenBindField(pos, field, GhostDBStaticVariables.GHOST_DB_ORCLAUSE, start, end);
	}
	
	protected GhostPair<IMetaField, String> addField(int pos, String clause,FilterOperationTypes fot, IMetaField field, Object obj) {
		_listOfCriteriaFields.add(field);
		if(obj instanceof IMetaField){
			_listOfCriteriaFields.add((IMetaField)obj);
		}
		return helperSQLConstructor(pos, field, GhostDBStaticVariables.wrapObject(obj), clause, fot);
	}
	
	public GhostPair<IMetaField, String> addAndField(int pos, FilterOperationTypes fot, IMetaField field, Object obj) {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return addField(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, fot, field, obj);
	}
	
	public GhostPair<IMetaField, String> addOrField(int pos, FilterOperationTypes fot, IMetaField field, Object obj) {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return addField(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE, fot, field, obj);
	}
	
	//Special case to take NUMBER field for uidvm where clause
	public GhostPair<IMetaField, String> addAndField(int pos, FilterOperationTypes fot, IMetaField field, NUMBER key) {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return helperSQLConstructor(pos, field, GhostDBStaticVariables.wrapString(key.stringValue()), GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, fot);
		
	}
	
	//Unary methods
	public GhostPair<IMetaField, String> addAndRownumField(int pos, java.lang.Number value){
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return helperUnarySQLConstructor(pos, GhostDBStaticVariables.ROWNUMFIELD, value, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, FilterUnaryOperationTypes.ROWNUM);
	}
	
	public GhostPair<IMetaField, String> addOrRownumField(int pos, java.lang.Number value){
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return helperUnarySQLConstructor(pos, GhostDBStaticVariables.ROWNUMFIELD, value, GhostDBStaticVariables.GHOST_DB_ORCLAUSE, FilterUnaryOperationTypes.ROWNUM);
	}
	
	public GhostPair<IMetaField, String> addAndIsNullField(int pos, IMetaField field){
		_listOfCriteriaFields.add(field);
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return helperUnarySQLConstructor(pos, field, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, FilterUnaryOperationTypes.ISNULL);
	}
	
	public GhostPair<IMetaField, String> addOrIsNullField(int pos, IMetaField field){
		_listOfCriteriaFields.add(field);
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return helperUnarySQLConstructor(pos, field, GhostDBStaticVariables.GHOST_DB_ORCLAUSE, FilterUnaryOperationTypes.ISNULL);
	}
	
	public GhostPair<IMetaField, String> addAndIsNotNullField(int pos, IMetaField field){
		_listOfCriteriaFields.add(field);
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return helperUnarySQLConstructor(pos, field, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, FilterUnaryOperationTypes.ISNOTNULL);
	}
	
	public GhostPair<IMetaField, String> addOrIsNotNullField(int pos, IMetaField field){
		_listOfCriteriaFields.add(field);
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return helperUnarySQLConstructor(pos, field, GhostDBStaticVariables.GHOST_DB_ORCLAUSE, FilterUnaryOperationTypes.ISNOTNULL);
	}
	
	//Specific IGDate Filter methods
//	public boolean addAndDateField(filterOperationTypes fot,IMetaField field, Calendar c){
//	    return helperSQLConstructor(field, GhostDbStaticVariables.wrapDate(c), GhostDbStaticVariables.GHOST_DB_ANDCLAUSE, fot);
//    }
	
	public GhostPair<IMetaField, String> addAndDateFieldToCurrentTime(int pos, FilterOperationTypes fot,IMetaField field){
		_listOfCriteriaFields.add(field);
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
	    return helperSQLConstructor(pos, field, GhostDBStaticVariables.GHOST_DB_CURRENT_TIME, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, fot);
    }

	public GhostPair<IMetaField, String> addOrDateFieldToCurrentTime(int pos, FilterOperationTypes fot,IMetaField field){
		_listOfCriteriaFields.add(field);
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
	    return helperSQLConstructor(pos, field, GhostDBStaticVariables.GHOST_DB_CURRENT_TIME, GhostDBStaticVariables.GHOST_DB_ORCLAUSE, fot);
    }
	
	// In Clause
	protected GhostPair<IMetaField, String> addInField(int pos, String clause, IMetaField field, ClausableList<?> ghostList, FilterOperationTypes fot) {
		_listOfCriteriaFields.add(field);
		String inClause = GhostDBStaticVariables.EMPTY_STR;
		if(ghostList.getClass() == GhostListString.class){
			inClause = ghostList.getStringForm(GhostDBStaticVariables.COMMA, GhostDBStaticVariables.SINGLE_QUOTE, GhostDBStaticVariables.SINGLE_QUOTE);
		}else{
			inClause = ghostList.getStringForm(GhostDBStaticVariables.COMMA);
		}	
		return helperSQLConstructor(pos,
				                    field, 
				                    GhostDBStaticVariables.OPEN_PARENTHESES+
				                    inClause+
				                    GhostDBStaticVariables.CLOSE_PARENTHESES,
				                    clause,
				                    fot);
	}

	public GhostPair<IMetaField, String> addAndInField(int pos, IMetaField field, ClausableList<?> ghostList) {
		_listOfCriteriaFields.add(field);
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return addInField(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, field, ghostList,FilterOperationTypes.INCLAUSE);
	}
	
	public GhostPair<IMetaField, String> addOrInField(int pos, IMetaField field, ClausableList<?> ghostList) {
		_listOfCriteriaFields.add(field);
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return addInField(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE, field, ghostList,FilterOperationTypes.INCLAUSE);
	}
	
	public GhostPair<IMetaField, String> addAndNotInField(int pos, IMetaField field, ClausableList<?> ghostList) {
		_listOfCriteriaFields.add(field);
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return addInField(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, field, ghostList,FilterOperationTypes.NOTINCLAUSE);
	}
	
	public GhostPair<IMetaField, String> addOrNotInField(int pos, IMetaField field, ClausableList<?> ghostList) {
		_listOfCriteriaFields.add(field);
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return addInField(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE, field, ghostList,FilterOperationTypes.NOTINCLAUSE);
	}
	
	protected GhostPair<IMetaField, String> addInField(int pos, String clause, IMetaField field, GhostQueryTable gqt, FilterOperationTypes fot)  {
		_listOfCriteriaFields.add(field);
		if(gqt.numberOfColumns() == 1){
			 IMetaField queryColumn = gqt.getAllColumns().get(0);
			 if( (field.getType() == GhostDBStaticVariables.DBTypes.DATE) &&
			    (queryColumn.getType() != GhostDBStaticVariables.DBTypes.DATE) ){
				 throw new GhostQueryBuilderException("In clause field is of database date type, but field being compared to is of : " + queryColumn.getType());
			 }
			 //This is in-case there are bind values in use in the in subquery clause.
			 GhostBindFieldMap gbfm = this.getGhostBindFieldMap();
			 if(this.isBindMapSet()){
				 gbfm.mergeAfterMap(gqt.getTableFilter().getGhostBindFieldMap());
			 }else{
				 if(!gqt.getTableFilter().getGhostBindFieldMap().isEmpty()){
					 gbfm.mergeBeforeMap(gqt.getTableFilter().getGhostBindFieldMap());
				 }
			 }
			 
			 return helperSQLConstructor(pos,
	                field, 
	                GhostDBStaticVariables.OPEN_PARENTHESES +
	                gqt.getGhostQuery().getQuery() +
	                GhostDBStaticVariables.CLOSE_PARENTHESES,
	                clause,
	                fot);
		}else{
			throw new GhostQueryBuilderException("In clause field can only accept one field to compare to, your sub query has : "  + gqt.numberOfColumns());
		}
		
	}	
	
	public GhostPair<IMetaField, String> addAndInField(int pos, IMetaField field, GhostQueryTable gqt)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return addInField(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, field, gqt, FilterOperationTypes.INCLAUSE);
	}
	
	public GhostPair<IMetaField, String> addOrInField(int pos, IMetaField field, GhostQueryTable gqt)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return addInField(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE, field, gqt, FilterOperationTypes.INCLAUSE);
	}
	
	public GhostPair<IMetaField, String> addAndNotInField(int pos, IMetaField field, GhostQueryTable gqt)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return addInField(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, field, gqt, FilterOperationTypes.NOTINCLAUSE);
	}
	
	public GhostPair<IMetaField, String> addOrNotInField(int pos, IMetaField field, GhostQueryTable gqt)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return addInField(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE, field, gqt, FilterOperationTypes.NOTINCLAUSE);
	}
	
	//Exists and Not Exists
	protected GhostPair<IMetaField, String> addExistsFieldHelper(int pos, FilterUnaryOperationTypes fuot, String clause, List<IMetaTable> tables, GhostTableFilter gtf)  {
		
		return helperUnarySQLConstructor(pos,
	                GhostDBStaticVariables.OPEN_PARENTHESES +
	                GhostDBStaticVariables.SELECT_SELECT + " 0 " +
	                GhostDBStaticVariables.SELECT_FROM + GhostDBStaticVariables.getConcatTableString(tables, GhostDBStaticVariables.COMMA) +
	                GhostDBStaticVariables.SELECT_WHERE1E1 + 
	                gtf.getFilterSQL() +
	                GhostDBStaticVariables.CLOSE_PARENTHESES,
	                clause,
	                fuot);
	}	
	
	public GhostPair<IMetaField, String> addAndExistsField(int pos, List<IMetaTable> tables, GhostTableFilter gtf)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return addExistsFieldHelper(pos, FilterUnaryOperationTypes.EXISTS,GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, tables, gtf);
	}
	
	public GhostPair<IMetaField, String> addOrExistsField(int pos, List<IMetaTable> tables, GhostTableFilter gtf)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return addExistsFieldHelper(pos, FilterUnaryOperationTypes.EXISTS,GhostDBStaticVariables.GHOST_DB_ORCLAUSE, tables, gtf);
	}
	
	public GhostPair<IMetaField, String> addAndNotExistsField(int pos, List<IMetaTable> tables, GhostTableFilter gtf)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return addExistsFieldHelper(pos, FilterUnaryOperationTypes.NOTEXISTS,GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, tables, gtf);
	}
	
	public GhostPair<IMetaField, String> addOrNotExistsField(int pos, List<IMetaTable> tables, GhostTableFilter gtf)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return addExistsFieldHelper(pos, FilterUnaryOperationTypes.NOTEXISTS,GhostDBStaticVariables.GHOST_DB_ORCLAUSE, tables, gtf);
	}
	
	// Between
	protected GhostPair<IMetaField, String> addBetweenField(int pos, String clause, IMetaField field, Object left, Object right)  {
		_listOfCriteriaFields.add(field);
		return helperSQLConstructor(pos,
				                    field, 
				                    GhostDBStaticVariables.wrapObject(left) + 
				                    GhostDBStaticVariables.GHOST_DB_ANDCLAUSE +
				                    GhostDBStaticVariables.wrapObject(right),
				                    clause,
				                    FilterOperationTypes.BETWEENCLAUSE);
	}
	
	public GhostPair<IMetaField, String> addAndBetweenField(int pos, IMetaField field, Object left, Object right)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return addBetweenField(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, field, left, right);
	}
	
	public GhostPair<IMetaField, String> addOrBetweenField(int pos, IMetaField field, Object left, Object right)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return addBetweenField(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE, field, left, right);
	}
	
	
	//Nary methods
	protected GhostPair<IMetaField, String> addBetweenDatesField(int pos, String clause, IMetaField start, IMetaField stop, Object value)  {
		_listOfCriteriaFields.add(start);
		_listOfCriteriaFields.add(stop);
		return helperSQLConstructor(pos,
				                    GhostDBStaticVariables.wrapObject(value) +
				                    _operationNaryMap.get(FilterNaryOperationTypes.BETWEENDATES) +
				                    start.getFullyQualifiedTableAliasWithAliasName() +
				                    GhostDBStaticVariables.GHOST_DB_ANDCLAUSE +
				                    GhostDBStaticVariables.FUNCTION_NVL + GhostDBStaticVariables.OPEN_PARENTHESES + 
				                    stop.getFullyQualifiedTableAliasWithAliasName() + GhostDBStaticVariables.COMMA +
				                    GhostDBStaticVariables.wrapObject(value) + GhostDBStaticVariables.CLOSE_PARENTHESES,
				                    clause);
	}
	
	public GhostPair<IMetaField, String> addAndBetweenDatesField(int pos, IMetaField start, IMetaField stop, Object value)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return addBetweenDatesField(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE, start, stop, value);
	}
	
	public GhostPair<IMetaField, String> addOrBetweenDatesField(int pos, IMetaField start, IMetaField stop, Object value)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE);
		return addBetweenDatesField(pos, GhostDBStaticVariables.GHOST_DB_ORCLAUSE, start, stop, value);
	}
	
	
	public GhostPair<IMetaField, String> addAndCollectionJoin(int pos,  IGhostCollection<?> collection)  {
		_statmentClauseType.put(pos, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
		return helperUnarySQLConstructorCollectionId(pos, MetaTables.GHOST_VM.getGhostCollectionId(), collection, GhostDBStaticVariables.GHOST_DB_ANDCLAUSE);
	}
	
	
//	public List<IMetaField> getBindFields() {
//		return _bindList;
//	}
	
	public String getFilterSQL(){
		StringBuilder result = new StringBuilder();
		Set<Integer> s = _filterSQL.keySet();
		List<Integer> keys = new ArrayList<Integer>(s);
        Collections.sort(keys);

		for(Integer i : keys){
//			if(_bindList.contains(i)){
//			   _bindOrder.add(i,_gbfm.get(i).getLeft());
////			   x++;
//			}
			//TODO: Fix grouping to not use substring for removing AND or OR clause
			if(_groupingMap.containsKey(i)){
				 if(_groupingClauseMap.get(i)!= null){
					 result.append(_groupingClauseMap.get(i) + writeGroupString(i,true) + _filterSQL.get(i).getRight().substring(4) + writeGroupString(i,false));
				 }else{
					 result.append(writeGroupString(i,true) + _filterSQL.get(i).getRight() + writeGroupString(i,false));
				 }
			}else{
				result.append(_filterSQL.get(i).getRight());
			}
			
		}
		return result.toString();
	}
	
//	public List<IMetaField> getBindOrder(){
//		return _bindOrder;
//	}

	public void setBindValue(int i, Object value) {
		_gbfm.updateValue(i, value);
	}

	protected String writeGroupString(int pos, boolean start){
		StringBuilder result = new StringBuilder();
		GhostPair<Integer,Integer> gp = _groupingMap.get(pos);
		
		int length = gp.getLeft();
		String token = GhostDBStaticVariables.OPEN_PARENTHESES;
		
		if(!start){
			length = gp.getRight();
			token = GhostDBStaticVariables.CLOSE_PARENTHESES;
		}
		
		for(int x =0;x<length; x++){
			result.append(token);
		}
		return result.toString();
	}
	
	public void groupFilterCriteriaAnd(int start, int end)  {
		groupFilterCriteria(GhostDBStaticVariables.GHOST_DB_ANDCLAUSE,start,end);
	}
	
	public void groupFilterCriteriaOr(int start, int end)  {
		groupFilterCriteria(GhostDBStaticVariables.GHOST_DB_ORCLAUSE,start,end);
	}
	
	public void groupFilterCriteria(String clause, int start, int end)  {
		if(!_statmentClauseType.get(start).equalsIgnoreCase(GhostDBStaticVariables.GHOST_DB_ANDCLAUSE)){
			throw new GhostQueryBuilderException("You tried to group a set of critera where the first statment in the group is not an AND clause. Please update your statments position.");
		}
		_groupingClauseMap.put(start,clause);
		GhostPair<Integer,Integer> startGp = _groupingMap.get(start);
		GhostPair<Integer,Integer> endGp = _groupingMap.get(end);
		
		if(startGp!=null){
			startGp.setLeft(startGp.getLeft() + 1);
		}else{
			_groupingMap.put(start, new GhostPair<Integer,Integer>(1,0));
		}
		
		if(endGp!=null){
			endGp.setRight(endGp.getRight() + 1);
		}else{
			_groupingMap.put(end, new GhostPair<Integer,Integer>(0,1));
		}
	}
	
	public void removeGroupFilterCriteria(int start, int end)  {
		GhostPair<Integer,Integer> startGp = _groupingMap.get(start);
		GhostPair<Integer,Integer> endGp = _groupingMap.get(end);
		
		if(startGp!=null){
			startGp.setLeft(startGp.getLeft() - 1);
		}else{
			throw new GhostQueryBuilderException("You tried to remove a grouping that didn't exist.");
		}
		
		if(endGp!=null){
			endGp.setRight(endGp.getRight() - 1);
		}else{
			throw new GhostQueryBuilderException("You tried to remove a grouping that didn't exist.");
		}
	}

	public void clear() {
		super.clear();
		_groupingMap.clear();
		_statmentClauseType.clear();		
		_gbfm.clear();
		_isBindMapSet = false;
	}
	
	public GhostPair<IMetaField, String> removeField(int pos){
		GhostPair<IMetaField,GhostFieldMapObject> gPair = _gbfm.remove(pos);
		if(_gbfm.size()==0){
			_isBindMapSet = false;
		}else{
			if(gPair != null){
				_listOfCriteriaFields.remove(gPair.getLeft());
			}else{
				Object[] objArray = _listOfCriteriaFields.toArray();
				if(pos<objArray.length){
					_listOfCriteriaFields.remove((IMetaField)objArray[pos]);	
				}
			}
		}
		_groupingMap.remove(pos);
        _statmentClauseType.remove(pos);
        
		return _filterSQL.remove(pos);
	}

	public Set<IMetaField> getFields() {
		return _listOfCriteriaFields;
	}

	public GhostPair<IMetaField, String> removeBindFieldCollectionId(int pos) {
		//Collection ID Bind takes TWO spots! Pos and Pos + 1
		_gbfm.remove(pos+1);
		return this.removeField(pos);
	}

	public void setIsBindMapSet(boolean b) {
		_isBindMapSet = b;
	}
	
		
////		_bindList.addAll(gtf.getBindFields());
//		helperGroupSQLConstructor(i, GhostDbStaticVariables.GHOST_DB_ANDCLAUSE, gtf.getFilterSQL());
//		
//	}
		
}

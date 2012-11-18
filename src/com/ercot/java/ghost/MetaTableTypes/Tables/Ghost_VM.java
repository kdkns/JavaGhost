package com.ercot.java.ghost.MetaTableTypes.Tables;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.ercot.java.ghost.Annotations.CustomColumnMethod;
import com.ercot.java.ghost.Annotations.GetColumnMethod;
import com.ercot.java.ghost.Annotations.MetaTableInfo;
import com.ercot.java.ghost.Annotations.MetaTablePrimaryKeyColumn;
import com.ercot.java.ghost.Exceptions.GhostRuntimeException;
import com.ercot.java.ghost.GhostAttributes.GhostCustomAttributeEnum;
import com.ercot.java.ghost.GhostAttributes.IGhostCustomAttribute;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.ercot.java.ghost.MetaTableTypes.MetaField;
import com.ercot.java.ghost.MetaTableTypes.MetaFieldCustomColumn;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;
import com.ercot.java.ghost.utils.GhostDBStaticVariables.DBOwnerList;

@MetaTableInfo(unqualifiedTableName ="GHOST_VM", schema = DBOwnerList.OWNER)
public class Ghost_VM extends AbstractMetaTable implements IMetaGhostVariableTable{
	private static Logger logger = Logger.getLogger("Ghost_VM");
//	private List<IMetaField> _customColumnFieldList = new ArrayList<IMetaField>();
	private Map<GhostCustomAttributeEnum,IMetaField> _customColumnMap = new TreeMap<GhostCustomAttributeEnum,IMetaField>();
	
	public final MetaField _stringValue = new MetaField(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_STRING_INSERT,1024,0,false);
	public final MetaField _numberValue = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,GhostDBStaticVariables.GHOST_VM_NUMBER_INSERT,22,0,false);
	public final MetaField _blobValue = new MetaField(this,GhostDBStaticVariables.DBTypes.BLOB,GhostDBStaticVariables.GHOST_VM_BLOB_INSERT,4000,0,false);
	
	@MetaTablePrimaryKeyColumn
	protected final MetaField _uidvm = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,"UIDVM",22,0,true);
	
	public final MetaField _ghostPointer = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,GhostDBStaticVariables.GHOST_VM_POINTER_COLUMN,22,0,false);
	public final MetaField _ghostDataColumn = new MetaField(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_DATA_COLUMN,128,0,false);
	public final MetaField _ghostPointer_table = new MetaField(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_POINTER_TABLE_COLUMN,128,0,false);
	public final MetaField _ghostCollectionId = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,GhostDBStaticVariables.GHOST_CIDVM_COLUMN,22,0,false);
	public final MetaField _ghostPossessionId = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,GhostDBStaticVariables.GHOST_PIDVM_COLUMN,22,0,false);
	public final MetaField _ghostBulkDriveField = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,GhostDBStaticVariables.GHOST_VM_BULK_DRIVE_FIELD_COLUMN,128,0,false);
	public final MetaField _lastUpdateDate = new MetaField(this,GhostDBStaticVariables.DBTypes.DATE,"LAST_UPDATE_DATE",7,0,false);
	public final MetaField _rangeId = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,"RANGE_ID",22,0,false);
	public final MetaField _bulkId = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,"BULK_ID",22,0,false);
	public final MetaField _bulkInsertId = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,GhostDBStaticVariables.GHOST_VM_BULK_INSERT_ID_COLUMN,22,0,false);
	public final MetaField _ghostPointerPartitionDate = new MetaField(this,GhostDBStaticVariables.DBTypes.DATE,GhostDBStaticVariables.GHOST_VM_POINTER_PARTITION_COLUMN,7,0,false);
	public final MetaField _ghostPointer_field = new MetaField(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_POINTER_FIELD_COLUMN,128,0,false);
	public final MetaField _ghost_transactionId = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,GhostDBStaticVariables.GHOST_TIDVM_COLUMN,22,0,true);
	public final MetaFieldCustomColumn _custom1 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_1,32,0,false);
	public final MetaFieldCustomColumn _custom2 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_2,32,0,false);
	public final MetaFieldCustomColumn _custom3 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_3,32,0,false);
	public final MetaFieldCustomColumn _custom4 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_4,32,0,false);
	public final MetaFieldCustomColumn _custom5 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_5,32,0,false);
	public final MetaFieldCustomColumn _custom6 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_6,32,0,false);
	public final MetaFieldCustomColumn _custom7 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_7,32,0,false);
	public final MetaFieldCustomColumn _custom8 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_8,32,0,false);
	public final MetaFieldCustomColumn _custom9 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_9,32,0,false);
	public final MetaFieldCustomColumn _custom10 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_10,32,0,false);
	public final MetaFieldCustomColumn _custom11 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_11,32,0,false);
	public final MetaFieldCustomColumn _custom12 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_12,32,0,false);
	public final MetaFieldCustomColumn _custom13 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_13,32,0,false);
	public final MetaFieldCustomColumn _custom14 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_14,32,0,false);
	public final MetaFieldCustomColumn _custom15 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_15,32,0,false);
	public final MetaFieldCustomColumn _custom16 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_16,32,0,false);
	public final MetaFieldCustomColumn _custom17 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_17,32,0,false);
	public final MetaFieldCustomColumn _custom18 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_18,32,0,false);
	public final MetaFieldCustomColumn _custom19 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_19,32,0,false);
	public final MetaFieldCustomColumn _custom20 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.VARCHAR,GhostDBStaticVariables.GHOST_VM_CUSTOM_20,32,0,false);
	public final MetaFieldCustomColumn _customDate1 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.DATE,GhostDBStaticVariables.GHOST_VM_CUSTOM_DATE_1,7,0,false);
	public final MetaFieldCustomColumn _customDate2 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.DATE,GhostDBStaticVariables.GHOST_VM_CUSTOM_DATE_2,7,0,false);
	public final MetaFieldCustomColumn _customDate3 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.DATE,GhostDBStaticVariables.GHOST_VM_CUSTOM_DATE_3,7,0,false);
	public final MetaFieldCustomColumn _customDate4 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.DATE,GhostDBStaticVariables.GHOST_VM_CUSTOM_DATE_4,7,0,false);
	public final MetaFieldCustomColumn _customDate5 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.DATE,GhostDBStaticVariables.GHOST_VM_CUSTOM_DATE_5,7,0,false);
	public final MetaFieldCustomColumn _customDate6 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.DATE,GhostDBStaticVariables.GHOST_VM_CUSTOM_DATE_6,7,0,false);
	public final MetaFieldCustomColumn _customDate7 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.DATE,GhostDBStaticVariables.GHOST_VM_CUSTOM_DATE_7,7,0,false);
	public final MetaFieldCustomColumn _customDate8 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.DATE,GhostDBStaticVariables.GHOST_VM_CUSTOM_DATE_8,7,0,false);
	public final MetaFieldCustomColumn _customDate9 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.DATE,GhostDBStaticVariables.GHOST_VM_CUSTOM_DATE_9,7,0,false);
	public final MetaFieldCustomColumn _customDate10 =  new MetaFieldCustomColumn(this,GhostDBStaticVariables.DBTypes.DATE,GhostDBStaticVariables.GHOST_VM_CUSTOM_DATE_10,7,0,false);

	protected Ghost_VM(){
		super.initMetaTableObject();
		initCustomColumns();
	}
	
	public Ghost_VM(String tableAlias){
		super.initMetaTableObject();
		super.setTableAliasforFields(tableAlias);
		initCustomColumns();
	}

	
	public Ghost_VM(Class<Ghost_VM> classOfObject){
		super(classOfObject);
		initCustomColumns();
	}
	
	private Map<GhostCustomAttributeEnum, IMetaField> getCustomColumnMap(){
		return _customColumnMap;
	}
	
	public Collection<IMetaField> getCustomColumnFieldList(){
		return getCustomColumnMap().values();
	}
	
	private void initCustomColumns(){
		Method[] methodList = Ghost_VM.class.getDeclaredMethods();
		Object o;
		for(int x=0;x<methodList.length;x++){
			try {
				if(methodList[x].isAnnotationPresent(CustomColumnMethod.class)){
				    o = methodList[x].invoke(this);
				    getCustomColumnMap().put(methodList[x].getAnnotation(CustomColumnMethod.class).attribute(), (IMetaField) o);
				}
			} catch (IllegalArgumentException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			} catch (IllegalAccessException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			} catch (InvocationTargetException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			}
		}
	}

	/**
	 * @return the StringValue
	 */@GetColumnMethod
	public MetaField getStringValue() {
		return _stringValue;
	}
	
	/**
	 * @return the _numberValue
	 */@GetColumnMethod
	public MetaField getNumberValue() {
		return _numberValue;
	}

	/**
	 * @return the _blobValue
	 */@GetColumnMethod
	public MetaField getBlobValue() {
		return _blobValue;
	}

	/**
	 * @return the Uidvm
	 */@GetColumnMethod
	public MetaField getUidvm() {
		return _uidvm;
	}

	/**
	 * @return the _ghostPointer
	 */@GetColumnMethod
	public MetaField getGhostPointer() {
		return _ghostPointer;
	}

	/**
	 * @return the _ghostDataColumn
	 */@GetColumnMethod
	public MetaField getGhostDataColumn() {
		return _ghostDataColumn;
	}

	/**
	 * @return the _ghostPointer_table
	 */@GetColumnMethod
	public MetaField getGhostPointerTable() {
		return _ghostPointer_table;
	}

	/**
	 * @return the _ghostCollectionId
	 */@GetColumnMethod
	public MetaField getGhostCollectionId() {
		return _ghostCollectionId;
	}

	/**
	 * @return the _ghostPossessionId
	 */@GetColumnMethod
	public MetaField getGhostPossessionId() {
		return _ghostPossessionId;
	}

	/**
	 * @return the _lastUpdateDate
	 */@GetColumnMethod
	public MetaField getLastUpdateDate() {
		return _lastUpdateDate;
	}

	/**
	 * @return the _rangeId
	 */@GetColumnMethod
	public MetaField getRangeId() {
		return _rangeId;
	}

	/**
	 * @return the _bulkId
	 */@GetColumnMethod
	public MetaField getBulkId() {
		return _bulkId;
	}

	/**
	 * @return the _ghostPointerPartitionDate
	 */@GetColumnMethod
	public MetaField getGhostPointerPartitionDate() {
		return _ghostPointerPartitionDate;
	}

	/**
	 * @return the _ghostPointer_field
	 */@GetColumnMethod
	public MetaField getGhostPointerField() {
		return _ghostPointer_field;
	}

	/**
	 * @return the _ghost_transactionId
	 */@GetColumnMethod
	public MetaField getGhostTransactionId() {
		return _ghost_transactionId;
	}
 
	/**
	 * @return the _ghost_transactionId
	 */@GetColumnMethod
	public MetaField getGhostBulkDriveField() {
		return _ghostBulkDriveField;
	}

	/**
	 * @return the _bulkInsertId
	 */@GetColumnMethod
	public MetaField getBulkInsertId() {
		return _bulkInsertId;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_1)
	public MetaField getCustom1() {
		return _custom1;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_2)
	public MetaField getCustom2() {
		return _custom2;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_3)
	public MetaField getCustom3() {
		return _custom3;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_4)
	public MetaField getCustom4() {
		return _custom4;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_5)
	public MetaField getCustom5() {
		return _custom5;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_6)
	public MetaField getCustom6() {
		return _custom6;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_7)
	public MetaField getCustom7() {
		return _custom7;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_8)
	public MetaField getCustom8() {
		return _custom8;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_9)
	public MetaField getCustom9() {
		return _custom9;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_10)
	public MetaField getCustom10() {
		return _custom10;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_11)
	public MetaField getCustom11() {
		return _custom11;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_12)
	public MetaField getCustom12() {
		return _custom12;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_13)
	public MetaField getCustom13() {
		return _custom13;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_14)
	public MetaField getCustom14() {
		return _custom14;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_15)
	public MetaField getCustom15() {
		return _custom15;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_16)
	public MetaField getCustom16() {
		return _custom16;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_17)
	public MetaField getCustom17() {
		return _custom17;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_18)
	public MetaField getCustom18() {
		return _custom18;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_19)
	public MetaField getCustom19() {
		return _custom19;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_20)
	public MetaField getCustom20() {
		return _custom20;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_date_1)
	public MetaField getCustomDate1() {
		return _customDate1;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_date_2)
	public MetaField getCustomDate2() {
		return _customDate2;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_date_3)
	public MetaField getCustomDate3() {
		return _customDate3;
	}

	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_date_4)
	public MetaField getCustomDate4() {
		return _customDate4;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_date_5)
	public MetaField getCustomDate5() {
		return _customDate5;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_date_6)
	public MetaField getCustomDate6() {
		return _customDate6;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_date_7)
	public MetaField getCustomDate7() {
		return _customDate7;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_date_8)
	public MetaField getCustomDate8() {
		return _customDate8;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_date_9)
	public MetaField getCustomDate9() {
		return _customDate9;
	}
	
	@GetColumnMethod
	@CustomColumnMethod(attribute = GhostCustomAttributeEnum.custom_date_10)
	public MetaField getCustomDate10() {
		return _customDate10;
	}
	
	
	public IMetaField getCustomAttributeField(IGhostCustomAttribute attribute) {
		return getCustomColumnMap().get(attribute);
	}

	@Override
	public IMetaField getIMGVTValueColumn() {//TODO: Make this return the correct type based on where data is stored? Or use same column for everything?
		//return getBlobValue();
		throw new GhostRuntimeException("Ghost_VM table cannot return a value column as it is dependant on datatype of objects!");
	}
	
}

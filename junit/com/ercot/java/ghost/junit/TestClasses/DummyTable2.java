package com.ercot.java.ghost.junit.TestClasses;


import com.ercot.java.ghost.Annotations.AttributeMapping;
import com.ercot.java.ghost.Annotations.GetColumnMethod;
import com.ercot.java.ghost.Annotations.MetaTableInfo;
import com.ercot.java.ghost.Annotations.MetaTablePrimaryKeyColumn;
import com.ercot.java.ghost.GhostAttributes.GhostAttributeEnum;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.ercot.java.ghost.MetaTableTypes.MetaField;
import com.ercot.java.ghost.MetaTableTypes.Tables.AbstractMetaTable;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;
import com.ercot.java.ghost.utils.GhostDBStaticVariables.DBOwnerList;

@MetaTableInfo(unqualifiedTableName ="DUMMYTABLE2", schema = DBOwnerList.OWNER)
public final class DummyTable2 extends AbstractMetaTable implements IMetaGhostVariableTable{
	@MetaTablePrimaryKeyColumn
	protected final MetaField _uidcolumn = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,"UIDCOLUMN",22,0,true);
	
//	@MetaTablePrimaryKeyColumn
	protected final MetaField _total = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,"TOTAL",22,0,false);
	
//	@MetaTablePartitionColumn
	protected final MetaField _stoptime = new MetaField(this,GhostDBStaticVariables.DBTypes.DATE,"STOPTIME",7,0,false);
	
	private final MetaField _starttime = new MetaField(this,GhostDBStaticVariables.DBTypes.DATE,"STARTTIME",7,0,false);
	private final MetaField _spi = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,"SPI",22,0,false);
	private final MetaField _min = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,"MIN",22,0,false);
	private final MetaField _max = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,"MAX",22,0,false);
	private final MetaField _intervalcount = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,"INTERVALCOUNT",22,0,false);
	private final MetaField _iamstringcolumn = new MetaField(this,GhostDBStaticVariables.DBTypes.VARCHAR,"IAMSTRINGCOLUMN",25,0,false);
	private final MetaField _iamnumbercolumn = new MetaField(this,GhostDBStaticVariables.DBTypes.NUMBER,"IAMNUMBERCOLUMN",22,0,false);
	private final MetaField _iamdatecolumn = new MetaField(this,GhostDBStaticVariables.DBTypes.DATE,"IAMDATECOLUMN",7,0,false);
	private final MetaField _iamblobcolumn = new MetaField(this,GhostDBStaticVariables.DBTypes.BLOB,"IAMBLOBCOLUMN",4000,0,false);
  
	protected DummyTable2() {
		super.initMetaTableObject();
	}
	
	public DummyTable2(String tableAlias){
		super.initMetaTableObject();
		super.setTableAliasforFields(tableAlias);
	}
     	
    @GetColumnMethod
	public IMetaField getUidcolumn() { return _uidcolumn;}
    
    @GetColumnMethod
	public IMetaField getTotal() { return _total;}
    
    @GetColumnMethod
    @AttributeMapping(GhostAttributeEnum.stoptime)
	public IMetaField getStoptime() { return _stoptime;}
    
    @GetColumnMethod
    @AttributeMapping(GhostAttributeEnum.starttime)
	public IMetaField getStarttime() { return _starttime;}
    
    @GetColumnMethod
	public IMetaField getSpi() { return _spi;}
    
    @GetColumnMethod
	public IMetaField getMin() { return _min;}
    
    @GetColumnMethod
	public IMetaField getMax() { return _max;}
    
    @GetColumnMethod
	public IMetaField getIntervalcount() { return _intervalcount;}
    
    @GetColumnMethod
	public IMetaField getIamstringcolumn() { return _iamstringcolumn;}
    
    @GetColumnMethod
	public IMetaField getIamnumbercolumn() { return _iamnumbercolumn;}
    
    @GetColumnMethod
	public IMetaField getIamdatecolumn() { return _iamdatecolumn;}
    
    @GetColumnMethod
	public IMetaField getIamblobcolumn() { return _iamblobcolumn;}

	@Override
	public IMetaField getIMGVTValueColumn() {
		return getIamblobcolumn();
	}
}

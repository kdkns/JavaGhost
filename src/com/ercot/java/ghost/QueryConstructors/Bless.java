package com.ercot.java.ghost.QueryConstructors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ercot.java.ghost.Exceptions.GhostRuntimeException;
import com.ercot.java.ghost.GhostAttributes.GhostAttributeEnum;
import com.ercot.java.ghost.GhostFieldMaps.GhostFieldMap;
import com.ercot.java.ghost.GhostFieldMaps.GhostFieldMapObject;
import com.ercot.java.ghost.GhostFieldMaps.GhostHeaderMapping;
import com.ercot.java.ghost.MetaTableTypes.GhostQueryTable;
import com.ercot.java.ghost.MetaTableTypes.IMetaChildTable;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaFieldConstant;
import com.ercot.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.ercot.java.ghost.MetaTableTypes.IMetaParentTable;
import com.ercot.java.ghost.MetaTableTypes.IMetaTable;
import com.ercot.java.ghost.MetaTableTypes.Tables.MetaFieldConstant;
import com.ercot.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.ercot.java.ghost.Variable.IGhostCollection;
import com.ercot.java.ghost.Variable.IGhostVariable;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;
import com.ercot.java.ghost.utils.GhostPair;
import com.ercot.java.ghost.utils.GhostVariableWrapper;

public class Bless {
	private static Logger logger = Logger.getLogger("Bless");
		
		
//public static void save(IGhostVariable<?,?> g, IMetaGhostVariableTable table, GhostFieldMap ghostFieldMap, GhostHeaderMapping ghm,  int saveChannel, GhostCustomAttributeEnum headerCheckAttribute){
//		
//		GhostQuery insertQuery = null;
//		GhostQuery selectQuery = null;
//		List<IMetaTable> tableList = new ArrayList<IMetaTable>();
//		GhostTableFilter tf = new GhostTableFilter();
//		
//		tableList.add(MetaTables.GHOST_VM);
//			int sflSize = 0;
//			//In case there are no additional columns to map to destination table
//			if(ghostFieldMap!=null){
//				sflSize = ghostFieldMap.size();
//			}else{
//			    ghostFieldMap = new GhostFieldMap();
//			}
//			
//			if(table instanceof IMetaChildTable){
//				//In case you have no header mappings to do and are a child table
//				if(ghm==null){
//					throw new GhostRuntimeException("Ghost header mapping object is null while destination table is a child table");
//				}
//				IMetaChildTable childTable = (IMetaChildTable) table;
//				IMetaParentTable parentTable = childTable.getParentTable();
//				
//				List<IMetaTable> existsTableList = new ArrayList<IMetaTable>();				
//				existsTableList.add(childTable.getParentTable());
//				
//				GhostTableFilter existsTf = new GhostTableFilter();
//				
//				existsTf.addAndField(0,GhostTableFilter.FilterOperationTypes.EQUALS, childTable.getParentTable().getHeaderCheckColumn(), MetaTables.GHOST_VM.getCustomAttributeField(headerCheckAttribute));
//				existsTf.addAndField(1, GhostTableFilter.FilterOperationTypes.EQUALS,childTable.getParentTable().getAttributeField(GhostAttributeEnum.savechannel), saveChannel);
//				
//				tf.addAndField(0,GhostTableFilter.FilterOperationTypes.EQUALS, MetaTables.GHOST_VM.getUidvm(), g.getDBKey());
//				tf.addAndNotExistsField(1, existsTableList, existsTf);
//				
//				//Add the two main columns needed for header table key
//				ghm.addMappingToColumn(GhostAttributeEnum.saverecorder, headerCheckAttribute);
//				ghm.addMappingToColumn(GhostAttributeEnum.savechannel, saveChannel);
//				ghm.addMappingToColumn(parentTable.getUidSequenceColumn());
//				
//				GhostQuery errorRecordsQuery = GhostQueryConstructor.selectQuery(false, tableList, ghm.getGhostFieldMap(parentTable), tf);
//				GhostQuery headerInsertQuery = null;
//				//errorRecordsQuery.executeQuery();
//				//TODO: Do something for bad header records
//				
//					headerInsertQuery = GhostQueryConstructor.insertUsingSelectQuery(errorRecordsQuery, parentTable);
//					logger.debug("BlessGhost.Save: Header Insert Query:" + GhostVariableWrapper.wrapVariable(headerInsertQuery.getQuery()));
//					headerInsertQuery.executeQuery();
//					
//				
//					if(headerInsertQuery != null){
//						headerInsertQuery.close();
//					}
//				
//				
//				//Overwrite old field to now only get records where header exists.
//				tf.addAndField(1,GhostTableFilter.FilterOperationTypes.EQUALS, childTable.getParentTable().getHeaderCheckColumn(), MetaTables.GHOST_VM.getCustomAttributeField(headerCheckAttribute));
//				tableList.add(childTable.getParentTable());
//				//TODO: Support Multiple Parents
//				ghostFieldMap.put(sflSize+7, childTable.getKeyField(), childTable.getParentTable().getPrimaryKey());
//			}
//			
//			//Add in standard MetaBlob Fields
////			ghostFieldMap.put(sflSize, table.getIRVTStarttimeColumn(), MetaTables.GHOST_VM.getMetablobStarttime());
////			ghostFieldMap.put(sflSize+1, table.getIRVTStoptimeColumn(), MetaTables.GHOST_VM.getMetablobStopime());
////			ghostFieldMap.put(sflSize+2, table.getIRBTSpiColumn(), MetaTables.GHOST_VM.getMetablobSpi());
////			ghostFieldMap.put(sflSize+3, table.getIRBTTotalColumn(), MetaTables.GHOST_VM.getMetablobTotal());
////			ghostFieldMap.put(sflSize+4, table.getIRBTMaxColumn(), MetaTables.GHOST_VM.getMetablobMax());
////			ghostFieldMap.put(sflSize+5, table.getIRBTMinColumn(), MetaTables.GHOST_VM.getMetablobMin());
////			ghostFieldMap.put(sflSize+6, table.getIRBTIntervalCountColumn(), MetaTables.GHOST_VM.getMetablobIntervalCount());
//			g.blessColumnMappingToVMTable(sflSize,ghostFieldMap,table);
//			
////			selectFieldList.put(sflSize+7, table.getPrimaryKey(), table.getUidSequenceColumn());
//			
//			selectQuery = GhostQueryConstructor.selectQueryBlessSave(false, tableList, ghostFieldMap, tf, g, table.getIMGVTValueColumn(), table);
//			logger.debug("BlessGhost.Save: Select Query:" + GhostVariableWrapper.wrapVariable(selectQuery.getQuery()));
//			
//				insertQuery = GhostQueryConstructor.insertUsingSelectQuery(selectQuery, table);
//				logger.debug("BlessGhost.Save: Insert Query:" + GhostVariableWrapper.wrapVariable(insertQuery.getQuery()));
//				insertQuery.executeQuery();
//			
//			
//	            if(insertQuery!=null){
//	            	insertQuery.close();
//	            }
//		ghostFieldMap.remove(ghostFieldMap.size()-1); //TODO: This is stupid, fix this
//		
//			
//	}
	
public static void save(IGhostVariable<?,?> g, IMetaGhostVariableTable table, GhostFieldMap ghostFieldMap){
	save(g,table,ghostFieldMap,null);
}
	
public static void save(IGhostVariable<?,?> g, IMetaGhostVariableTable table, GhostHeaderMapping ghostHeaderMapping){
	 save(g,table,null,ghostHeaderMapping);
}

public static void save(IGhostVariable<?,?> g, IMetaGhostVariableTable table, GhostFieldMap ghostFieldMapConst, GhostHeaderMapping ghostHeaderMappingConst){
		
		GhostQuery insertQuery = null;
		GhostQuery selectQuery = null;
		List<IMetaTable> tableList = new ArrayList<IMetaTable>();
		GhostTableFilter tf = new GhostTableFilter();
		tableList.add(MetaTables.GHOST_VM);
		
		int sflSize = 0;
		GhostFieldMap ghostFieldMap = new GhostFieldMap();
		//In case there are no additional columns to map to destination table
		if(ghostFieldMapConst != null){
			ghostFieldMap.addAll(ghostFieldMapConst);
			ghostFieldMap.resortMapWithNoGaps();
			sflSize = ghostFieldMap.size();
		}
		GhostHeaderMapping ghostHeaderMapping = null;
		if(ghostHeaderMappingConst != null){
			ghostHeaderMapping = new GhostHeaderMapping();
			ghostHeaderMapping.addAll(ghostHeaderMappingConst);			
		}
		tf.addAndField(0,GhostTableFilter.FilterOperationTypes.EQUALS, MetaTables.GHOST_VM.getUidvm(), g.getKey());
		
			if(table instanceof IMetaChildTable){
				//In case you have no header mappings to do and are a child table
				if(ghostHeaderMapping==null){
					throw new GhostRuntimeException("Ghost header mapping object is null while destination table is a child table");
				}
				
				IMetaField headerCheckColumn = ghostHeaderMapping.getMappedCustomAttributeForAttribute(GhostAttributeEnum.saverecorder);
				IMetaField saveChannelColumn = ghostHeaderMapping.getMappedCustomAttributeForAttribute(GhostAttributeEnum.savechannel);
				
				IMetaChildTable childTable = (IMetaChildTable) table;
				IMetaParentTable parentTable = childTable.getParentTable();
				
				List<IMetaTable> existsTableList = new ArrayList<IMetaTable>();				
				existsTableList.add(childTable.getParentTable());
				
				GhostTableFilter existsTf = new GhostTableFilter();
				
				existsTf.addAndField(0,GhostTableFilter.FilterOperationTypes.EQUALS, childTable.getParentTable().getHeaderCheckColumn(),headerCheckColumn);
				existsTf.addAndField(1, GhostTableFilter.FilterOperationTypes.EQUALS,childTable.getParentTable().getAttributeField(GhostAttributeEnum.savechannel),saveChannelColumn);
				
				tf.addAndNotExistsField(1, existsTableList, existsTf);
				
				//Add the column needed for header table key
				ghostHeaderMapping.addMappingToColumn(parentTable.getUidSequenceColumn());
				
				GhostQuery errorRecordsQuery = GhostQueryConstructor.selectQuery(false, tableList, ghostHeaderMapping.getGhostFieldMap(parentTable), tf);
				GhostQuery headerInsertQuery = null;
				//errorRecordsQuery.executeQuery();
				//TODO: Do something for bad header records
				
					headerInsertQuery = GhostQueryConstructor.insertUsingSelectQuery(errorRecordsQuery, parentTable);
					logger.debug("BlessGhost.Save: Header Insert Query:" + GhostVariableWrapper.wrapVariable(headerInsertQuery.getQuery()));
					headerInsertQuery.executeQuery();
				
					if(headerInsertQuery != null){
						headerInsertQuery.close();
					}
				
				
				//Overwrite old field to now only get records where header exists.
				tf.addAndField(1,GhostTableFilter.FilterOperationTypes.EQUALS, childTable.getParentTable().getHeaderCheckColumn(), headerCheckColumn);
				tableList.add(childTable.getParentTable());
				//TODO: Support Multiple Parents
				ghostFieldMap.put(sflSize+g.blessNumberOfColumnsForMappingToVMTable(), childTable.getKeyField(), childTable.getParentTable().getPrimaryKey());
			}
			
			//Add in standard MetaBlob Fields
			g.blessColumnMappingToVMTable(sflSize,ghostFieldMap,table);
						
			selectQuery = GhostQueryConstructor.selectQueryBlessSave(false, tableList, ghostFieldMap, tf, g, table.getIMGVTValueColumn(), table);
			logger.debug("BlessGhost.Save: Select Query:" + GhostVariableWrapper.wrapVariable(selectQuery.getQuery()));
			
			insertQuery = GhostQueryConstructor.insertUsingSelectQuery(selectQuery, table);
			logger.debug("BlessGhost.Save: Insert Query:" + GhostVariableWrapper.wrapVariable(insertQuery.getQuery()));
			insertQuery.executeQuery();
		
		
            if(insertQuery!=null){
            	insertQuery.close();
            }
		ghostFieldMap.remove(ghostFieldMap.size()-1); //TODO: This is stupid, fix this
			
	}
	




	
//	public static void save(IGhostCollection<?> gmbc, IMetaGhostVariableTable table, GhostFieldMap ghostFieldMap, GhostHeaderMapping ghostHeaderMapping, int saveChannel, GhostCustomAttributeEnum headerCheckAttribute) {
//	//TODO: Update header insert query
//		if(!gmbc.isEmpty()){
//			IGhostVariable<?, ?> igv = null;
//			igv = gmbc.getGhostVariable(0);
//			
//			//Used to call blessColumnMappingToVMTable
//			GhostQuery gq = null;
//			GhostQuery selectQuery = null;
//			List<IMetaTable> tableList = new ArrayList<IMetaTable>();
//			//List<IMetaField> selectFieldListHeader = new ArrayList<IMetaField>();
//			GhostFieldMap ghostFieldMapRemapping = new GhostFieldMap();
//			GhostTableFilter tf = new GhostTableFilter();
//			
//			logger.debug("GhostMetaBlobCollection ID: " + GhostVariableWrapper.wrapVariable(gmbc.getCollectionId()));
//			try {			
//				int sflSize = ghostFieldMap.size();//TODO: Bad to use size incase numbers out of order, use max position instead
//				String baseJoinString = GhostDBStaticVariables.EMPTRY_STR;
//				
//				if(table instanceof IMetaChildTable){
//					IMetaChildTable childTable = (IMetaChildTable) table;
//					tableList.add(MetaTables.GHOST_VM);		
//				    //tableList.add(childTable.getParentTable());
//					
//				    
//	                IMetaParentTable parentTable = childTable.getParentTable();
//	                
//					List<IMetaTable> existsTableList = new ArrayList<IMetaTable>();				
//					existsTableList.add(childTable.getParentTable());
//					
//					GhostTableFilter existsTf = new GhostTableFilter();				
//					existsTf.addAndField(0,GhostTableFilter.FilterOperationTypes.EQUALS, childTable.getParentTable().getHeaderCheckColumn(), MetaTables.GHOST_VM.getCustomAttributeField(headerCheckAttribute));
//					existsTf.addAndField(1, GhostTableFilter.FilterOperationTypes.EQUALS,childTable.getParentTable().getAttributeField(GhostAttributeEnum.savechannel), saveChannel);
//					
//					//tf.addAndField(0,GhostTableFilter.filterOperationTypes.equals, MetaTables.GHOST_VM.getUidvm(), g.getKey());
//					
////					tf.addAndBindFieldCollectionId(0,GhostTableFilter.FilterOperationTypes., MetaTables.GHOST_VM.getGhostCollectionId(), gmbc.getDBCollectionId());
//                    tf.addAndCollectionJoin(0, gmbc);
//					tf.addAndNotExistsField(1, existsTableList, existsTf);
//					
//					
//					List<IMetaField> errorSelectFields = new ArrayList<IMetaField>();
//					errorSelectFields.add(MetaTables.GHOST_VM.getCustomAttributeField(headerCheckAttribute));
//					errorSelectFields.addAll(ghostHeaderMapping.getFieldList());
//					
//					//Add the two main columns needed for header table key
//					ghostHeaderMapping.addMappingToColumn(GhostAttributeEnum.saverecorder, headerCheckAttribute);
//					ghostHeaderMapping.addMappingToColumn(GhostAttributeEnum.savechannel, saveChannel);
//					ghostHeaderMapping.addMappingToColumn(parentTable.getUidSequenceColumn());
//					
//					
//					GhostQuery errorSubQuery = GhostQueryConstructor.selectQuery(true, errorSelectFields, tableList, tf);
//					GhostQuery headerInsertQuery = null;
//					
//					
//						GhostQueryTable subTable = new GhostQueryTable(errorSubQuery, MetaTables.GHOST_VM.getAlias());
//						
//					
//					GhostQuery errorRecordsQuery = GhostQueryConstructor.selectQuery(false, subTable, ghostHeaderMapping.getGhostFieldMap(parentTable), null);
//					
//					//errorRecordsQuery.executeQuery();
//					
//						headerInsertQuery = GhostQueryConstructor.insertUsingSelectQuery(errorRecordsQuery, parentTable);
//						logger.debug("BlessGhost.Save: Header Insert Query:" + GhostVariableWrapper.wrapVariable(headerInsertQuery.getQuery()));
//						headerInsertQuery.executeQuery();
//						
//					
//						if(headerInsertQuery != null){
//							headerInsertQuery.close();
//						}
//					
//	
//					ghostFieldMap.put(sflSize, childTable.getKeyField(), childTable.getParentTable().getPrimaryKey());	
//					//tf.addAndField(0,GhostTableFilter.filterOperationTypes.equals, childTable.getParentTable().getHeaderCheckColumn(), MetaTables.GHOST_VM.getAttributeField(headerCheckAttribute));
//					tf.addAndField(1, GhostTableFilter.FilterOperationTypes.EQUALS,childTable.getParentTable().getAttributeField(GhostAttributeEnum.savechannel), saveChannel);
//					baseJoinString = MetaTables.GHOST_VM.getCustomAttributeField(headerCheckAttribute) + " = " + childTable.getParentTable().getAlias() + GhostDBStaticVariables.PERIOD + childTable.getParentTable().getHeaderCheckColumn();
//					tableList.remove(MetaTables.GHOST_VM);
//					tableList.add(childTable.getParentTable());
//					sflSize = ghostFieldMap.size();
//				}
//				
//				
//	//			selectFieldListRemapping.put(sflSize, table.getIRVTStarttimeColumn(), MetaTables.GHOST_VM.getMetablobStarttime().toString());
//	//			//selectFieldListRemapping.put(sflSize, table.getIMTStarttimeColumn(), "starttime");
//	//			selectFieldListRemapping.put(sflSize+1, table.getIRVTStoptimeColumn(), MetaTables.GHOST_VM.getMetablobStopime().toString());
//	//			selectFieldListRemapping.put(sflSize+2, table.getIRBTSpiColumn(), MetaTables.GHOST_VM.getMetablobSpi().toString());
//	//			//selectFieldListRemapping.put(sflSize+2, table.getIMBTSpiColumn(), "spi");
//	//			selectFieldListRemapping.put(sflSize+3, table.getIRBTTotalColumn(), MetaTables.GHOST_VM.getMetablobTotal().toString());
//	//			selectFieldListRemapping.put(sflSize+4, table.getIRBTMaxColumn(), MetaTables.GHOST_VM.getMetablobMax().toString());
//	//			selectFieldListRemapping.put(sflSize+5, table.getIRBTMinColumn(), MetaTables.GHOST_VM.getMetablobMin().toString());
//	//			selectFieldListRemapping.put(sflSize+6, table.getIRBTIntervalCountColumn(), MetaTables.GHOST_VM.getMetablobIntervalCount().toString());
//				
//				igv.blessColumnMappingToVMTable(sflSize,ghostFieldMapRemapping,table);
//					
//	//			tf.addAndBindFieldCollectionId(0,GhostTableFilter.filterOperationTypes.equals, MetaTables.GHOST_VM.getGhostCollectionId(), gmbc.getKey());
//				//Remove filter from above if exists for header check
//				tf.removeBindFieldCollectionId(0);
//				
//				List<String> additionalFields = igv.getBlessAdditionalColumns();
////				additionalFields.add(MetaTables.GHOST_VM.getIRVTStarttimeColumn().toString());
////				additionalFields.add(MetaTables.GHOST_VM.getIRVTStoptimeColumn().toString());
////				additionalFields.add(MetaTables.GHOST_VM.getIRBTSpiColumn().toString());
////				additionalFields.add(MetaTables.GHOST_VM.getIRBTTotalColumn().toString());
////				additionalFields.add(MetaTables.GHOST_VM.getIRBTMaxColumn().toString());
////				additionalFields.add(MetaTables.GHOST_VM.getIRBTMinColumn().toString());
////				additionalFields.add(MetaTables.GHOST_VM.getIRBTIntervalCountColumn().toString());			
//				
//				
//				String topLevelRemappingColumns = GhostDBStaticVariables.COMMA + GhostDBStaticVariables.getConcatStringList(additionalFields, GhostDBStaticVariables.COMMA);
//				
//				//Remove because I now include spi and starttime? This was here because it was part of the origional query in MetaQueryInfo
////				additionalFields.remove(MetaTables.GHOST_VM.getIRBTSpiColumn().toString());
////				additionalFields.remove(MetaTables.GHOST_VM.getIRVTStarttimeColumn().toString());
//				
//	//			additionalFields.remove("spi");
//	//			additionalFields.remove("starttime");
//				additionalFields.add(MetaTables.GHOST_VM.getCustomAttributeField(headerCheckAttribute).toString());
//				String baseAdditionalColumns = GhostDBStaticVariables.COMMA + GhostDBStaticVariables.getConcatStringList(additionalFields, GhostDBStaticVariables.COMMA);
//				
//				
//				
//				selectQuery = GhostQueryConstructor.selectQueryBlessSave(false, tableList, ghostFieldMap, tf, gmbc, table.getIMGVTValueColumn(),
//						                                        baseAdditionalColumns,
//						                                        baseJoinString,
//						                                        ghostFieldMapRemapping,
//						                                        topLevelRemappingColumns,
//						                                        table
//						                                        );
//				
//				logger.debug("BlessGhost.Save: Select Query:" + GhostVariableWrapper.wrapVariable(selectQuery.getQuery()));
//				
//				gq = GhostQueryConstructor.insertUsingSelectQuery(selectQuery, table);
//				logger.debug("BlessGhost.constructBulkSave: Insert Query:" + GhostVariableWrapper.wrapVariable(gq.getQuery()));
//				gq.executeQuery();
//			}finally{
//				if(gq != null){
//					gq.close();
//				}
//			}
//			ghostFieldMap.remove(ghostFieldMap.size()-1); //TODO: This is stupid, fix this	
//		}
//	}
	
public static void save(IGhostCollection<?> gmbc, IMetaGhostVariableTable table, GhostFieldMap ghostFieldMap){
	save(gmbc,table,ghostFieldMap,null);
}
	
public static void save(IGhostCollection<?> gmbc, IMetaGhostVariableTable table, GhostHeaderMapping ghostHeaderMapping){
	 save(gmbc,table,null,ghostHeaderMapping);
}

public static void save(IGhostCollection<?> gmbc, IMetaGhostVariableTable table, final GhostFieldMap ghostFieldMapConst, final GhostHeaderMapping ghostHeaderMappingConst) {	
		if(gmbc.size()!=0){
			IGhostVariable<?, ?> igv = gmbc.getIGVariableObject();
			
			//Used to call blessColumnMappingToVMTable
			GhostQuery gq = null;
			GhostQuery selectQuery = null;
			List<IMetaTable> tableList = new ArrayList<IMetaTable>();

			GhostFieldMap ghostFieldMapRemapping = new GhostFieldMap();
			GhostTableFilter tf = new GhostTableFilter();
			
			int sflSize = 0;
			GhostFieldMap ghostFieldMap = new GhostFieldMap();
			//In case there are no additional columns to map to destination table
			if(ghostFieldMapConst != null){
				ghostFieldMap.addAll(ghostFieldMapConst);
				ghostFieldMap.resortMapWithNoGaps();
				sflSize = ghostFieldMap.size();
			}
			GhostHeaderMapping ghostHeaderMapping = null;
			if(ghostHeaderMappingConst != null){
				ghostHeaderMapping = new GhostHeaderMapping();
				ghostHeaderMapping.addAll(ghostHeaderMappingConst);
			}
			
			List<String> additionalFields = igv.getBlessAdditionalColumns();
			List<String> headerAdditionalFields = new ArrayList<String>();
			
			logger.debug("GhostMetaBlobCollection ID: " + GhostVariableWrapper.wrapVariable(gmbc.getCollectionId()));
			try {			
//				int sflSize = 0;//TODO: Bad to use size incase numbers out of order, use max position instead
				List<String> baseJoinString = new ArrayList<String>();
				
				if(table instanceof IMetaChildTable){
					if(ghostHeaderMapping == null){
						throw new GhostRuntimeException("Ghost header mapping object is null while destination table is a child table");
					}
					IMetaField headerCheckColumn = ghostHeaderMapping.getMappedCustomAttributeForAttribute(GhostAttributeEnum.saverecorder);
					IMetaField saveChannelColumn = ghostHeaderMapping.getMappedCustomAttributeForAttribute(GhostAttributeEnum.savechannel);
					
					IMetaChildTable childTable = (IMetaChildTable) table;
					
				    //tableList.add(childTable.getParentTable());
					tableList.add(MetaTables.GHOST_VM);
				    
	                IMetaParentTable parentTable = childTable.getParentTable();
	                
					List<IMetaTable> existsTableList = new ArrayList<IMetaTable>();				
					existsTableList.add(childTable.getParentTable());
					
					 
					GhostTableFilter existsTfNotQualified = new GhostTableFilter();			
					existsTfNotQualified.addAndField(0,GhostTableFilter.FilterOperationTypes.EQUALS, childTable.getParentTable().getHeaderCheckColumn(),headerCheckColumn);
					existsTfNotQualified.addAndField(1, GhostTableFilter.FilterOperationTypes.EQUALS,childTable.getParentTable().getAttributeField(GhostAttributeEnum.savechannel), saveChannelColumn);

										
                    tf.addAndCollectionJoin(0, gmbc);
					tf.addAndNotExistsField(1, existsTableList, existsTfNotQualified);
					
					
					List<IMetaField> errorSelectFields = new ArrayList<IMetaField>();
//					errorSelectFields.add(headerCheckColumn);
					errorSelectFields.addAll(ghostHeaderMapping.getFieldSet());
					
					//Add the two main columns needed for header table key
//					ghostHeaderMapping.addMappingToColumn(GhostAttributeEnum.saverecorder, headerCheckColumn);
//					ghostHeaderMapping.addMappingToColumn(GhostAttributeEnum.savechannel, saveChannelColumn);
					ghostHeaderMapping.addMappingToColumn(parentTable.getUidSequenceColumn());
					
					
					GhostQuery errorSubQuery = GhostQueryConstructor.selectQuery(true, errorSelectFields, tableList, tf);
					GhostQuery headerInsertQuery = null;
					
					
					GhostQueryTable subTable = new GhostQueryTable(errorSubQuery, MetaTables.GHOST_VM.getAlias());						
					
					GhostQuery errorRecordsQuery = GhostQueryConstructor.selectQuery(false, subTable, ghostHeaderMapping.getGhostFieldMap(parentTable), null);
					
					//errorRecordsQuery.executeQuery();
				
					headerInsertQuery = GhostQueryConstructor.insertUsingSelectQuery(errorRecordsQuery, parentTable);
					logger.debug("BlessGhost.Save: Header Insert Query:" + GhostVariableWrapper.wrapVariable(headerInsertQuery.getQuery()));
					headerInsertQuery.executeQuery();
					
				
					if(headerInsertQuery != null){
						headerInsertQuery.close();
					}
					
					//Remove Not Exists Clause
					tf.removeField(1);
										
	
					ghostFieldMap.put(sflSize, childTable.getKeyField(), childTable.getParentTable().getPrimaryKey());	
					//tf.addAndField(0,GhostTableFilter.filterOperationTypes.equals, childTable.getParentTable().getHeaderCheckColumn(), MetaTables.GHOST_VM.getAttributeField(headerCheckAttribute));
					//tf.addAndField(1, GhostTableFilter.FilterOperationTypes.EQUALS,childTable.getParentTable().getAttributeField(GhostAttributeEnum.savechannel), saveChannelColumn);
					//Adding joins from the child table query to the parent table. These joins sync the two together to get the correct header columns
					//for the insert. 
					IMetaFieldConstant hcc = new MetaFieldConstant(headerCheckColumn);
					IMetaFieldConstant scc = new MetaFieldConstant(saveChannelColumn);
					baseJoinString.add(hcc.getAlias() + " = " + childTable.getParentTable().getAlias() + GhostDBStaticVariables.PERIOD + childTable.getParentTable().getHeaderCheckColumn());
					baseJoinString.add(scc.getAlias() + " = " + childTable.getParentTable().getAlias() + GhostDBStaticVariables.PERIOD + childTable.getParentTable().getAttributeField(GhostAttributeEnum.savechannel));
					tableList.remove(MetaTables.GHOST_VM);
					tableList.add(childTable.getParentTable());
//					sflSize = ghostFieldMap.size();
					headerAdditionalFields.add(hcc.getColumnNameAndAlias());
					headerAdditionalFields.add(scc.getColumnNameAndAlias());
					
				}
				
				igv.blessColumnMappingToVMTable(sflSize,ghostFieldMapRemapping,table);
					
	//			tf.addAndBindFieldCollectionId(0,GhostTableFilter.filterOperationTypes.equals, MetaTables.GHOST_VM.getGhostCollectionId(), gmbc.getKey());
				//Remove filter from above if exists for header check
				tf.removeBindFieldCollectionId(0);
				
				
				//Do this BEFORE adding in any IMetaFieldCustomColumns! Otherwise you add it twice in the Select
				String topLevelRemappingColumns = GhostDBStaticVariables.getConcatStringList(additionalFields, GhostDBStaticVariables.COMMA);
				
				for(Integer i : ghostFieldMap.getKeySet()){
					GhostPair<IMetaField,GhostFieldMapObject> gp = ghostFieldMap.get(i);
					if(gp.getRight().getType() == GhostFieldMapObject.GhostFieldMapObjectTypes.IMetaFieldCustomColumn){
						IMetaField imf = (IMetaField) gp.getRight().getValue();
						//If you happen to want to put the same data in the Parent table AND Child table, 
						//Then we need make a check as otherwise it will be included twice in the query
//						if(ghostHeaderMapping!=null){
//							if(!ghostHeaderMapping.contains(imf)){
//								additionalFields.add(imf.getColumnNameAndAlias());
//							}
//						}else{
							additionalFields.add(imf.getColumnNameAndAlias());
//						}
					}
				}
				
				
				additionalFields.addAll(headerAdditionalFields);
				
				String baseAdditionalColumns = GhostDBStaticVariables.COMMA + GhostDBStaticVariables.getConcatStringList(additionalFields, GhostDBStaticVariables.COMMA);
				
				selectQuery = GhostQueryConstructor.selectQueryBlessSave(false, tableList, ghostFieldMap, tf, gmbc, table.getIMGVTValueColumn(),
						                                        baseAdditionalColumns,
						                                        baseJoinString,
						                                        ghostFieldMapRemapping,
						                                        topLevelRemappingColumns,
						                                        table
						                                        );
				
				logger.debug("BlessGhost.Save: Select Query:" + GhostVariableWrapper.wrapVariable(selectQuery.getQuery()));
				
				gq = GhostQueryConstructor.insertUsingSelectQuery(selectQuery, table);
				logger.debug("BlessGhost.constructBulkSave: Insert Query:" + GhostVariableWrapper.wrapVariable(gq.getQuery()));
				gq.executeQuery();
			}finally{
				if(gq != null){
					gq.close();
				}
			}
			ghostFieldMap.remove(ghostFieldMap.size()-1); //TODO: This is stupid, fix this	
		}
	}


	
	public static GhostQuery constructBulkSave(IMetaTable table, GhostFieldMap selectFieldList){
		GhostQuery gq = null;
		GhostQuery selectQuery = null;
		List<IMetaTable> tableList = new ArrayList<IMetaTable>();
		GhostTableFilter tf = new GhostTableFilter();
		tableList.add(MetaTables.GHOST_VM);
		tf.addAndBindFieldProtectedMethod(1,GhostTableFilter.FilterOperationTypes.EQUALS, MetaTables.GHOST_VM.getBulkInsertId(), ":p_bid");
		
		selectQuery = GhostQueryConstructor.selectQuery(false, tableList, selectFieldList, tf);
		logger.debug("BlessGhost.Save: Select Query:" + GhostVariableWrapper.wrapVariable(selectQuery.getQuery()));
		
		
		gq = GhostQueryConstructor.insertUsingSelectQuery(selectQuery, table);
		
		logger.debug("BlessGhost.constructBulkSave: Insert Query:" + GhostVariableWrapper.wrapVariable(gq.getQuery()));
		return gq;
	}

	
	
}

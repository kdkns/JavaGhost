CREATE OR REPLACE PACKAGE GHOST_BLOB_UTIL AS

   TYPE t_refcursor IS REF CURSOR;

   SUBTYPE status_code_varchar IS VARCHAR2(1000);
   SUBTYPE vm_insert_table IS ghost_vm%ROWTYPE;

   CONST_INTERVAL_VALUE_SEP CONSTANT CHAR := ',';
   CONST_INTERVAL_BYTE_SIZE CONSTANT NUMBER := 8;
   CONST_INTERVAL_BLOB_MARKER CONSTANT NUMBER := 0;
   CONST_INTERVAL_BLOB_MARKER_RAW CONSTANT RAW(8) := utl_RAW.REVERSE(utl_raw.cast_from_binary_double(CONST_INTERVAL_BLOB_MARKER));
   SUBTYPE raw_interval IS RAW(8);
   CONST_STATUS_BYTE_SIZE CONSTANT NUMBER := 1;

   CONST_CUSTOMCOL_VALUE_SEP CONSTANT CHAR := ',';
   CONST_VALUE_SEP CONSTANT CHAR := ',';

   CONST_BLOB_INSERT_COLUMN CONSTANT VARCHAR2(25) := 'BLOB_VALUE';

   CONST_BIND_VAR_PREFIX CONSTANT VARCHAR2(3) := ':b_';
   CONSTANT_BINARY_OP_ADD CONSTANT NUMBER := 0;
   CONSTANT_BINARY_OP_SUBTRACT CONSTANT NUMBER := 1;
   CONSTANT_BINARY_OP_MULTIPLY CONSTANT NUMBER := 2;
   CONSTANT_BINARY_OP_DIVIDE CONSTANT NUMBER := 3;

   CONST_FLAG_TIME_DEFAULT CONSTANT NUMBER := 0;
   CONST_FLAG_TIME_OPTION1 CONSTANT NUMBER := 1;

   CONST_FLAG_STATUS_DEFAULT CONSTANT NUMBER := 0;
   CONST_FLAG_STATUS_OPTION1 CONSTANT NUMBER := 1;
   CONST_FLAG_STATUS_OPTION2 CONSTANT NUMBER := 2;

   CONST_FLAG_SPIDST_DEFAULT CONSTANT NUMBER := 0;
   CONST_FLAG_SPIDST_OPTION1 CONSTANT NUMBER := 1;

   CONST_FLAG_EXIST_DEFAULT CONSTANT NUMBER := 0;
   CONST_FLAG_EXIST_OPTION1 CONSTANT NUMBER := 1;
   CONST_FLAG_EXIST_OPTION2 CONSTANT NUMBER := 2;

   CONST_FLAG_ATTRIBUTES_DEFAULT CONSTANT NUMBER := 0;
   CONST_FLAG_ATTRIBUTES_OPTION1 CONSTANT NUMBER := 1;

   CONST_FLAG_DIVIDE_DEFAULT CONSTANT NUMBER := 0;
   CONST_FLAG_DIVIDE_OPTION1 CONSTANT NUMBER := 1;

   CONST_FLAG_MULTIPLY_DEFAULT CONSTANT NUMBER := 0;
   CONST_FLAG_MULTIPLY_OPTION1 CONSTANT NUMBER := 1;

   CONST_BLOB_EMPTY_STATUS_CODE CONSTANT VARCHAR2(1) := '9';
   CONST_BLOB_GOOD_STATUS_CODE CONSTANT VARCHAR2(1) := ' ';
   CONST_BLOB_MIX_STATUS_CODE CONSTANT VARCHAR2(1) := '7';

   CONST_MT_ARG_SEPERATOR CONSTANT VARCHAR2(20) := ' || '','' || ';
   CONST_MT_CHILD_SEQUENCE_FIELD CONSTANT VARCHAR2(25) := 'v_message_correleation_id';
   CONST_MT_DRIVER_FIELD CONSTANT VARCHAR2(25) := 'v_driver_id(z)';
   CONST_MT_LAST_SIZE_FIELD CONSTANT VARCHAR2(25) := 'v_last_size';
   CONST_MT_CURRENT_SIZE_FIELD CONSTANT VARCHAR2(25) := 'v_size(z)';


   NUMBER_OF_CUSTOM_COLUMNS CONSTANT NUMBER := 20;
   ALL_CUSTOM_COLUMNS CONSTANT VARCHAR2(500) := 'custom_1,custom_2,custom_3,custom_4,custom_5,custom_6,custom_7,custom_8,custom_9,custom_10,custom_11,custom_12,custom_13,custom_14,custom_15,custom_16,custom_17,custom_18,custom_19,custom_20,custom_date_1,custom_date_2,custom_date_3,custom_date_4,custom_date_5,custom_date_6,custom_date_7,custom_date_8,custom_date_9,custom_date_10';
   ALL_CUSTOM_COLUMNS_BIND CONSTANT VARCHAR2(500) := ':custom_1,:custom_2,:custom_3,:custom_4,:custom_5,:custom_6,:custom_7,:custom_8,:custom_9,:custom_10,:custom_11,:custom_12,:custom_13,:custom_14,:custom_15,:custom_16,:custom_17,:custom_18,:custom_19,:custom_20,:custom_date_1,:custom_date_2,:custom_date_3,:custom_date_4,:custom_date_5,:custom_date_6,:custom_date_7,:custom_date_8,:custom_date_9,:custom_date_10';

   CONST_POSSESS_COLUMNS CONSTANT VARCHAR(1000) := 'uidvm,
                                                  ghost_pointer,
                                                  ghost_pointer_partition_date,
                                                  ghost_pointer_table,
                                                  ghost_pointer_field,
                                                  ghost_data_column,
                                                  ghost_collection_id,
                                                  ghost_possession_id';--,ghost_bulk_drive_field

   CONST_POSSESS_COLUMNS_BULK CONSTANT VARCHAR(200) := ',range_id';

   CONST_CUSTOM_COLUMN_PREFIX CONSTANT VARCHAR(50) := 'custom_';
   CONST_CUSTOM_DCOLUMN_PREFIX CONSTANT VARCHAR(50) := 'custom_date_';

   e_blob_is_null EXCEPTION;
   e_no_records_match EXCEPTION;
   e_blob_intervalcount_mismatch EXCEPTION;
   e_blob_time_mismatch EXCEPTION;
   e_blob_status_missing_code EXCEPTION;
   e_blob_spidst_mismatch EXCEPTION;
   e_blob_divide_byzero EXCEPTION;
   e_blob_exist_missing EXCEPTION;
   e_unknown_flag_option EXCEPTION;
   e_outofbounds_blob_interval EXCEPTION;


   PRAGMA EXCEPTION_INIT(e_blob_is_null, -20001);
   PRAGMA EXCEPTION_INIT(e_no_records_match, -20002);
   PRAGMA EXCEPTION_INIT(e_unknown_flag_option, -20003);
   PRAGMA EXCEPTION_INIT(e_blob_intervalcount_mismatch, -20004);
   PRAGMA EXCEPTION_INIT(e_blob_time_mismatch, -20005);
   PRAGMA EXCEPTION_INIT(e_blob_status_missing_code, -20006);
   PRAGMA EXCEPTION_INIT(e_blob_spidst_mismatch, -20007);
   PRAGMA EXCEPTION_INIT(e_blob_divide_byzero, -20008);
   PRAGMA EXCEPTION_INIT(e_blob_exist_missing, -20009);
   PRAGMA EXCEPTION_INIT(e_outofbounds_blob_interval, -20010);



   FUNCTION is_equal_blob(p_uidvm NUMBER,
                          p_uidvm_compare NUMBER) RETURN NUMBER;

   FUNCTION possess(p_select_query VARCHAR2,
                    p_collection_id NUMBER,
                    p_sql_open_values IN VARCHAR2,
                    p_vm_columns IN VARCHAR2,
                    p_custom_columns IN VARCHAR2) RETURN NUMBER;

   FUNCTION possess_return_count(p_select_query VARCHAR2,
                                 p_collection_id NUMBER,
                                 p_sql_open_values IN VARCHAR2,
                                 p_vm_columns IN VARCHAR2,
                                 p_custom_columns IN VARCHAR2)RETURN NUMBER;

   FUNCTION batch_process(p_driver_query IN VARCHAR2,
                          p_sql_query IN VARCHAR2,
                          p_save_query IN VARCHAR2,
                          p_possession_id IN NUMBER,
                          p_timeout IN NUMBER,
                          p_child_timeout IN NUMBER,
                          p_transaction_id IN NUMBER) RETURN NUMBER;

   PROCEDURE create_empty_blob_data (p_uidvm NUMBER,
                                     p_starttime IN DATE,
                                     p_stoptime IN DATE,
                                     p_spi NUMBER,
                                     p_value NUMBER,
                                     p_dst_participant CHAR);

   FUNCTION blob_get_interval_value(p_uidvm NUMBER,
                                    p_pos NUMBER,
                                    p_sql_query VARCHAR2) RETURN BINARY_DOUBLE;

   PROCEDURE blob_operation_toint(p_uidvm NUMBER,
                                  p_pos NUMBER,
                                  p_value NUMBER,
                                  p_operation NUMBER,
                                  p_sql_query VARCHAR2);

   PROCEDURE blob_operation_toallint(p_uidvm NUMBER,
                                    p_value NUMBER,
                                    p_operation NUMBER,
                                    p_sql_query VARCHAR2);


   PROCEDURE blob_round_allint(p_uidvm NUMBER,
                               p_num_decimals INTEGER,
                               p_sql_query VARCHAR2);


   PROCEDURE blob_operation_uidvm(p_uidvm_left IN NUMBER,
                                  p_uidvm_right IN NUMBER,
                                  p_uidvm_save IN NUMBER,
                                  p_operation IN NUMBER,
                                  p_sql_query_left VARCHAR2,
                                  p_sql_query_right VARCHAR2,
                                  p_flag_time IN NUMBER,
                                  p_flag_status IN NUMBER,
                                  p_flag_spidst IN NUMBER,
                                  p_flag_attributes IN NUMBER,
                                  p_flag_divide IN NUMBER,
                                  p_flag_multiply IN NUMBER,
                                  p_flag_existence IN NUMBER,
                                  p_existence_default_value IN NUMBER);

   FUNCTION blob_operation_pair(p_sql_query IN VARCHAR2,
                                p_operation IN NUMBER,
                                p_dest_cid IN NUMBER,
                                p_custom_columns IN VARCHAR2,
                                p_flag_time IN NUMBER,
                                p_flag_status IN NUMBER,
                                p_flag_spidst IN NUMBER,
                                p_flag_attributes IN NUMBER,
                                p_flag_divide IN NUMBER,
                                p_flag_multiply IN NUMBER,
                                p_flag_existence IN NUMBER,
                                p_existence_default_value IN NUMBER) RETURN NUMBER;

   FUNCTION blob_operation_pair_scalar(p_sql_query IN VARCHAR2,
                                    p_operation IN NUMBER,
                                    p_dest_cid IN NUMBER,
                                    p_custom_columns IN VARCHAR2,
                                    p_flag_time IN NUMBER,
                                    p_flag_status IN NUMBER,
                                    p_flag_spidst IN NUMBER,
                                    p_flag_attributes IN NUMBER,
                                    p_flag_divide IN NUMBER,
                                    p_flag_multiply IN NUMBER,
                                    p_flag_existence IN NUMBER,
                                    p_existence_default_value IN NUMBER) RETURN NUMBER;

   PROCEDURE blob_collection_operation(p_sql_query IN VARCHAR2,
                                       p_operation IN NUMBER,
                                       p_cid IN NUMBER,
                                       p_value IN NUMBER);

   PROCEDURE blob_collection_round(p_sql_query IN VARCHAR2,
                                   p_collection_id IN NUMBER,
                                   p_num_decimals IN INTEGER);

   PROCEDURE blob_collection_status_code(p_collection_id IN NUMBER,
                                         p_pos IN NUMBER,
                                         p_status IN VARCHAR2,
                                         p_sql_query IN VARCHAR2);

   PROCEDURE blob_scale(p_uidvm IN NUMBER,
                        p_dest_spi IN NUMBER,
                        p_sql_query IN VARCHAR2);

   PROCEDURE blob_collection_scale(p_collection_id IN NUMBER,
                                   p_dest_spi IN NUMBER,
                                   p_sql_query IN VARCHAR2);
   PROCEDURE blob_copy_from (p_uidvm_to NUMBER,
                             p_uidvm_from NUMBER);

   PROCEDURE aggregate_by_batch(p_sql_query IN VARCHAR2,
                                p_sql_args IN VARCHAR2,
                                p_collection_id IN NUMBER,
                                p_thread_timeout IN NUMBER,
                                p_transaction_id IN NUMBER,
                                p_new_collection_id IN NUMBER,
                                p_last_index IN NUMBER,
                                p_size IN NUMBER,
                                p_custom_columns IN VARCHAR2,
                                p_custom_column_values IN VARCHAR2,
                                p_cjp_seq IN NUMBER);

   FUNCTION aggregate_blob(p_sql_query IN VARCHAR2,
                           p_size IN NUMBER,
                           p_collection IN NUMBER,
                           p_timeout IN NUMBER,
                           p_transaction_id IN NUMBER) RETURN NUMBER;



  FUNCTION aggregate_by(p_driver_query IN VARCHAR2,
                        p_driver_args IN VARCHAR2,
                        p_update_driver_query IN VARCHAR2,
                        p_update_driver_args IN VARCHAR2,
                        p_sql_query IN VARCHAR2,
                        p_sql_args IN VARCHAR2,
                        p_collection_id IN NUMBER,
                        p_new_collection_id IN NUMBER,
                        p_num_processed_rows IN NUMBER,
                        p_timeout IN NUMBER,
                        p_thread_timeout IN NUMBER,
                        p_transaction_id IN NUMBER,
                        p_custom_columns IN VARCHAR2) RETURN NUMBER;

   PROCEDURE aggregate_thread(p_query IN VARCHAR2,
                              p_query_args IN VARCHAR2,
                              p_new_collection_id IN NUMBER,
                              p_thread_timeout IN NUMBER,
                              p_transaction_id IN NUMBER,
                              p_start_index IN NUMBER,
                              p_end_index IN NUMBER,
                              p_cjp_seq IN NUMBER,
                              p_bulk_id IN NUMBER);

    PROCEDURE aggregate_by_thread(p_query IN VARCHAR2,
                                  p_query_args IN VARCHAR2,
                                  p_new_collection_id IN NUMBER,
                                  p_thread_timeout IN NUMBER,
                                  p_transaction_id IN NUMBER,
                                  p_custom_columns IN VARCHAR2,
                                  p_custom_column_values IN VARCHAR2,
                                  p_start_index IN NUMBER,
                                  p_end_index IN NUMBER,
                                  p_cjp_seq IN NUMBER,
                                  p_bulk_id IN NUMBER);

    PROCEDURE pfm (p_cjp_seq IN NUMBER,
                   p_bulk_id IN NUMBER,
                   p_transaction_id IN NUMBER,
                   p_bulk_insert_id IN NUMBER);

    PROCEDURE plsql_dyn_return_value(p_in IN NUMBER,
                                     p_out OUT NUMBER);

    PROCEDURE blob_set_interval_value(p_uidvm NUMBER,
                                      p_pos NUMBER,
                                      p_value NUMBER,
                                      p_sql_query VARCHAR2);

    PROCEDURE blob_set_status_code(p_uidvm NUMBER,
                               p_pos NUMBER,
                               p_value CHAR,
                               p_sql_query VARCHAR2);

    FUNCTION blob_get_status_code(p_uidvm NUMBER,
                                  p_pos NUMBER,
                                  p_sql_query VARCHAR2) RETURN VARCHAR2;

    PROCEDURE blob_set_status_code_helper(p_blob IN OUT BLOB,
                                          p_pos NUMBER,
                                          p_value CHAR);
    -- For Rounding Interval Value
    FUNCTION binary_double_round(p_value IN BINARY_DOUBLE,
                                 p_num_decimals IN INTEGER) RETURN BINARY_DOUBLE;

    -- For Rounding Scalar Value
    FUNCTION number_round(p_value IN NUMBER,
                          p_num_decimals IN INTEGER) RETURN NUMBER;

    FUNCTION blob_comparison_pair(p_sql_query IN VARCHAR2,
                                  p_dest_cid IN NUMBER) RETURN NUMBER;
    FUNCTION blob_comparison_uidvm(p_uidvm_left IN NUMBER,
                                   p_uidvm_right IN NUMBER,
                                   p_sql_query_left VARCHAR2,
                                   p_sql_query_right VARCHAR2) RETURN NUMBER;

    PROCEDURE blob_createflag_uidvm(p_uidvm IN NUMBER,
                                    p_uidvm_save IN NUMBER,
                                    p_sql_query IN VARCHAR2,
                                    p_rules_case IN VARCHAR2);
                              
    FUNCTION get_blob(o_blob IN OUT BLOB,
                      p_sql_query IN VARCHAR2,
                      p_uidvm IN NUMBER) RETURN BOOLEAN;
                
    FUNCTION get_blob_length(p_blob BLOB) RETURN NUMBER;
    
    FUNCTION get_metablob_interval(p_blob BLOB,
                                   p_pos IN NUMBER) RETURN BINARY_DOUBLE;
                                   
    PROCEDURE write_binarydouble_to_metablob(o_blob IN OUT BLOB,
                                             p_pos IN NUMBER,
                                             p_binary_number IN BINARY_DOUBLE);    
    
    PROCEDURE build_custom_outputs(p_ghost_data ghost_data_obj,
                                   p_custom_columns IN VARCHAR2,
                                   o_custom_column_values IN OUT VARCHAR2);

    PROCEDURE insert_into_ghost_vm( p_insert_column IN VARCHAR2,
                                    p_custom_columns IN VARCHAR2,
                                    p_custom_column_values IN VARCHAR2,
                                    p_ghost_vm_row IN vm_insert_table,
                                    p_use_all_custom_columns BOOLEAN := FALSE);                            
                                                                         
    FUNCTION get_status_code(p_blob IN BLOB,
                             p_num_intervals IN NUMBER,
                             p_pos IN NUMBER) RETURN VARCHAR2;

    PROCEDURE set_init_metablob_attributes(p_status_code IN VARCHAR2,
                                           p_value IN  BINARY_DOUBLE,
                                           o_total IN OUT BINARY_DOUBLE,
                                           o_max IN OUT BINARY_DOUBLE,
                                           o_min IN OUT BINARY_DOUBLE);

    PROCEDURE set_metablob_attributes(p_status_code IN VARCHAR2,
                                      p_value IN BINARY_DOUBLE,
                                      o_total IN OUT BINARY_DOUBLE,
                                      o_max IN OUT BINARY_DOUBLE,
                                      o_min IN OUT BINARY_DOUBLE);                                           

  FUNCTION blob_collection_createflags(p_dest_cid IN NUMBER,
                                       p_sql_query IN VARCHAR2,
                                       p_rules_case IN VARCHAR2) RETURN NUMBER;

END GHOST_BLOB_UTIL;
/
CREATE OR REPLACE PACKAGE BODY GHOST_BLOB_UTIL AS

FUNCTION get_blob_byte_length(v_number_of_intervals IN NUMBER) RETURN INTEGER AS
BEGIN
   RETURN ((CONST_STATUS_BYTE_SIZE + CONST_INTERVAL_BYTE_SIZE)*v_number_of_intervals) + CONST_INTERVAL_BYTE_SIZE;
END get_blob_byte_length;

FUNCTION get_blob_length(p_blob BLOB) RETURN NUMBER IS
  v_number_of_intervals NUMBER;
  v_int_version PLS_INTEGER;
  e_not_an_int EXCEPTION;
  v_length PLS_INTEGER;
BEGIN
  v_length := dbms_lob.getlength(p_blob);
  v_number_of_intervals := (v_length - CONST_INTERVAL_BYTE_SIZE)/(CONST_STATUS_BYTE_SIZE + CONST_INTERVAL_BYTE_SIZE);
  v_int_version := v_number_of_intervals;

  IF v_number_of_intervals != v_int_version THEN
     v_number_of_intervals := ((v_length-1) - CONST_INTERVAL_BYTE_SIZE)/(CONST_STATUS_BYTE_SIZE + CONST_INTERVAL_BYTE_SIZE);
     v_int_version := v_number_of_intervals;
     IF v_number_of_intervals != v_int_version THEN
         RAISE e_not_an_int;
     END IF;
  END IF;
  RETURN v_number_of_intervals;
  EXCEPTION
         WHEN e_not_an_int THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'GET_BLOB_LENGTH : Value created is not an integer! Check blob! ' ||
                                           ' v_number_of_intervals:' || ghost_util.wrap_error_params(v_number_of_intervals),
                                           SQLERRM);
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'GET_BLOB_LENGTH ',
                                           SQLERRM);
END get_blob_length;


FUNCTION get_status_code_pos(p_num_intervals IN NUMBER,
                             p_pos IN NUMBER) RETURN VARCHAR2 IS
BEGIN
 RETURN (CONST_INTERVAL_BYTE_SIZE*(p_num_intervals+1))+p_pos;
 EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'GET_STATUS_CODE_POS '                                  ||
                                           ' p_num_intervals:' || ghost_util.wrap_error_params(p_num_intervals) ||
                                           ' p_pos:' , --|| ghost_util.wrap_error_params(p_pos)
                                           SQLERRM);
END get_status_code_pos;

FUNCTION get_status_code(p_blob IN BLOB,
                         p_num_intervals IN NUMBER,
                         p_pos IN NUMBER) RETURN VARCHAR2 IS
BEGIN
 RETURN utl_raw.cast_to_varchar2(utl_raw.substr(p_blob, get_status_code_pos(p_num_intervals,p_pos), CONST_STATUS_BYTE_SIZE));
 EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                             ||
                                           'GET_STATUS_CODE  '                                    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos)      ||
                                           ' p_num_intervals' || ghost_util.wrap_error_params(p_num_intervals),
                                           SQLERRM);
END get_status_code;


FUNCTION get_metablob_number_position(p_interval IN PLS_INTEGER) RETURN NUMBER IS
BEGIN
 RETURN (CONST_INTERVAL_BYTE_SIZE*(p_interval-1))+1;
 EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'GET_METABLOB_NUMBER_POSITION  '                             ||
                                           ' p_interval:' || ghost_util.wrap_error_params(p_interval),
                                           SQLERRM);
END get_metablob_number_position;

FUNCTION get_metablob_interval(p_blob BLOB,
                               p_pos IN NUMBER) RETURN BINARY_DOUBLE IS
BEGIN
 RETURN utl_raw.cast_to_binary_double(utl_raw.reverse(dbms_lob.substr(p_blob, CONST_INTERVAL_BYTE_SIZE, get_metablob_number_position(p_pos))));
 EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'GET_METABLOB_INTERVAL  '                       ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);
END get_metablob_interval;


PROCEDURE set_metablob_interval(p_blob IN OUT BLOB,
                                p_pos IN NUMBER,
                                p_value IN NUMBER) IS
BEGIN
  ghost_util.g_write_binarydouble_to_blob(p_blob, p_value, get_metablob_number_position(p_pos));
 EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'SET_METABLOB_INTERVAL  '                       ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos) ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value),
                                           SQLERRM);
END set_metablob_interval;


PROCEDURE set_metablob_status(p_blob IN OUT BLOB,
                              p_number_of_intervals IN NUMBER,
                              p_pos IN NUMBER,
                              p_value IN CHAR) IS
BEGIN
  ghost_util.ghost_write_varchar2_to_blob(p_blob, p_value, get_status_code_pos(p_number_of_intervals,p_pos));
 EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'SET_METABLOB_STATUS  '                       ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos) ||
                                           ' p_number_of_intervals:' || ghost_util.wrap_error_params(p_number_of_intervals) ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value),
                                           SQLERRM);
END set_metablob_status;


PROCEDURE write_binarydouble_to_metablob(o_blob IN OUT BLOB,
                                         p_pos IN NUMBER,
                                         p_binary_number IN BINARY_DOUBLE) IS
BEGIN
 ghost_util.ghost_write_raw_to_blob(o_blob, utl_RAW.REVERSE(utl_raw.cast_from_binary_double(p_binary_number)), get_metablob_number_position(p_pos));
 EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                             ||
                                           'WRITE_BINARYDOUBLE_TO_METABLOB  '                     ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos)       ||
                                           ' p_binary_number' || ghost_util.wrap_error_params(p_binary_number),
                                           SQLERRM);
END write_binarydouble_to_metablob;


PROCEDURE load_remote_blobs_into_vm(p_sql_query IN VARCHAR2) AS
       v_blob_table ghost_tab_blob;
  BEGIN
       EXECUTE IMMEDIATE 'MERGE INTO ghost_vm g USING(' || p_sql_query || ' AND blob_value IS NULL) a ON ( g.uidvm = a.uidvm ) WHEN MATCHED THEN UPDATE SET g.blob_value = a.b_value';
      EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                 ||
                                           'LOAD_REMOTE_BLOBS_INTO_VM '                               ||
                                           ' p_sql_query:' || ghost_util.wrap_error_params(p_sql_query),
                                           SQLERRM);
  END load_remote_blobs_into_vm;

FUNCTION get_blob(o_blob IN OUT BLOB,
                  p_sql_query IN VARCHAR2,
                  p_uidvm IN NUMBER) RETURN BOOLEAN AS

        v_remote BOOLEAN := false;
BEGIN
        IF p_sql_query IS NULL THEN
             SELECT blob_value
                INTO o_blob
                FROM ghost_vm
               WHERE uidvm = p_uidvm FOR UPDATE;
                                                 
             
         ELSE
            EXECUTE IMMEDIATE p_sql_query INTO o_blob USING p_uidvm;
            v_remote := true;
         END IF;

         RETURN v_remote;

         EXCEPTION
             WHEN OTHERS THEN
                 ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                               ghost_util.CONST_ERROR_MSG                             ||
                                               'GET_BLOB  '                     ||
                                               ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm),
                                               SQLERRM);
END get_blob;

FUNCTION get_blob_updatable(p_sql_query IN VARCHAR2,
                            p_uidvm IN NUMBER) RETURN BLOB AS
    v_blob BLOB;
    v_remote BOOLEAN := false;
    v_ghost_data_obj GHOST_DATA_OBJ;
BEGIN
         v_remote := get_blob(v_blob,
                              p_sql_query,
                              p_uidvm);                              

         IF(v_blob IS NULL) THEN
            RAISE e_blob_is_null;
         END IF;

         IF v_remote THEN
           UPDATE ghost_vm
              SET blob_value = v_blob
            WHERE uidvm = p_uidvm;

           SELECT blob_value
             INTO v_blob
             FROM ghost_vm
            WHERE uidvm = p_uidvm FOR UPDATE;
          END IF;

          RETURN v_blob;

          EXCEPTION
           WHEN e_blob_is_null THEN
               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG                               ||
                                             'GET_BLOB_UPDATABLE: Blob is null! '                     ||
                                             ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm),
                                             SQLERRM);
           WHEN OTHERS THEN
               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG                             ||
                                             'GET_BLOB_UPDATABLE  '                     ||
                                             ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm),
                                             SQLERRM);
END get_blob_updatable;


FUNCTION get_collection_blobs_updatable (p_sql_query VARCHAR2,
                                         p_collection_id NUMBER) RETURN ghost_tab_blob AS
  v_ghost_bulk_obj ghost_tab_blob;
  BEGIN
      load_remote_blobs_into_vm(p_sql_query);
      EXECUTE IMMEDIATE 'SELECT blob_value FROM ghost_vm WHERE BITAND (ghost_collection_id, :p_collection_id) = :p_collection_id FOR UPDATE'
      BULK COLLECT INTO v_ghost_bulk_obj USING p_collection_id, p_collection_id;

      IF v_ghost_bulk_obj.COUNT = 0 THEN
         RAISE e_no_records_match;
      END IF;

      RETURN v_ghost_bulk_obj;

      EXCEPTION
           WHEN e_no_records_match THEN
               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG                             ||
                                             'GET_COLLECITON_BLOBS_UPDATABLE: No records found!  '                     ||
                                             ' p_collection_id:' || ghost_util.wrap_error_params(p_collection_id),
                                             SQLERRM);
           WHEN OTHERS THEN
               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG                             ||
                                             'GET_COLLECITON_BLOBS_UPDATABLE  '                     ||
                                             ' p_collection_id:' || ghost_util.wrap_error_params(p_collection_id),
                                             SQLERRM);
END get_collection_blobs_updatable;





PROCEDURE load_remote_blobs_into_vm_pair(p_sql_query IN VARCHAR2) AS
       v_blob_table ghost_tab_blob;
  BEGIN
       EXECUTE IMMEDIATE 'MERGE INTO ghost_vm g USING(' || p_sql_query || ' AND a.gvm_insert_column IS NULL ) x ON ( g.uidvm = x.uidvmx ) WHEN MATCHED THEN UPDATE SET g.blob_value = x.b_valuex';
      EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                 ||
                                           'LOAD_REMOTE_BLOBS_INTO_VM_PAIR ',
                                           --' p_sql_query:' || ghost_util.wrap_error_params(p_sql_query),
                                           SQLERRM);
  END load_remote_blobs_into_vm_pair;


PROCEDURE set_init_metablob_attributes(p_status_code IN VARCHAR2,
                                       p_value IN  BINARY_DOUBLE,
                                       o_total IN OUT BINARY_DOUBLE,
                                       o_max IN OUT BINARY_DOUBLE,
                                       o_min IN OUT BINARY_DOUBLE) IS
BEGIN
  --o_total :=  utl_raw.cast_to_binary_double(utl_raw.reverse(utl_raw.substr(p_blob,1,8)));
  o_total := p_value;
  IF (p_status_code != CONST_BLOB_EMPTY_STATUS_CODE) THEN
      o_max := p_value;
      o_min := p_value;
  END IF;
  EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                  ||
                                           'SET_INIT_METABLOB_ATTRIBUTES '                             ||
                                           ' p_status_code' || ghost_util.wrap_error_params(p_status_code)      ||
                                           ' p_value' || ghost_util.wrap_error_params(p_value)     ||
                                           ' o_total' || ghost_util.wrap_error_params(o_total) ||
                                           ' o_max' || ghost_util.wrap_error_params(o_max) ||
                                           ' o_min' || ghost_util.wrap_error_params(o_min),
                                           SQLERRM);
END set_init_metablob_attributes;

PROCEDURE set_metablob_attributes(p_status_code IN VARCHAR2,
                                  p_value IN BINARY_DOUBLE,
                                  o_total IN OUT BINARY_DOUBLE,
                                  o_max IN OUT BINARY_DOUBLE,
                                  o_min IN OUT BINARY_DOUBLE) IS
BEGIN
  IF (p_status_code != CONST_BLOB_EMPTY_STATUS_CODE) THEN
         --This is incase first value in set_init function was a status 9
         -- the value never get set and stay null
         IF (o_max IS NULL) AND (o_min IS NULL) THEN
             o_max := p_value;
             o_min := p_value;
         END IF;
         IF(o_max < p_value) THEN
            o_max := p_value;
         END IF;

         IF(o_min > p_value) THEN
            o_min := p_value;
         END IF;
   END IF;
   o_total := o_total + p_value;

   EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                  ||
                                           'SET_METABLOB_ATTRIBUTES '                             ||
                                           ' p_status_code' || ghost_util.wrap_error_params(p_status_code)      ||
                                           ' p_value' || ghost_util.wrap_error_params(p_value)     ||
                                           ' o_total' || ghost_util.wrap_error_params(o_total) ||
                                           ' o_max' || ghost_util.wrap_error_params(o_max) ||
                                           ' o_min' || ghost_util.wrap_error_params(o_min),
                                           SQLERRM);
END set_metablob_attributes;

 /*
    SUB   : make_bind_params
            Takes in a string of values and stores it in a string array for values.
            Also creates a string array containing an array of generic bind vars.
    PARAM : p_params - String of bind values.
            p_seperator - Seperator used for bind values.
            o_array_of_bind_values - Array storing the bind var values.
    RETURN: None
    */
    PROCEDURE make_bind_params(p_params IN VARCHAR2,
                               p_seperator IN VARCHAR2,
                               o_string_of_binds IN OUT VARCHAR2,
                               o_array_of_bind_values IN OUT ghost_util.array_of_strings
                               ) IS
         --v_element VARCHAR2(300);
         v_count NUMBER;
         v_orig_count NUMBER;
         v_index NUMBER;

    BEGIN

         v_count:= ghost_util.split_params(p_params, p_seperator, o_array_of_bind_values);
         v_orig_count := v_count;
--         dbms_output.put_line('Helper Count: ' || v_count);
--         dbms_output.put_line('Helper Array count: ' || o_array_of_bind_values.COUNT);

         -- Creates String in format : b_1,b_2,b_3,...
         --
         WHILE (v_count!=0) LOOP

                  v_count := v_count - 1;
                  v_index := v_orig_count - v_count;

                  IF v_index>1 THEN
                     o_string_of_binds:= o_string_of_binds || ',';
                  END IF;
                  o_string_of_binds:= o_string_of_binds || CONST_BIND_VAR_PREFIX || v_index;
         END LOOP;

         EXCEPTION
                  WHEN OTHERS THEN
                       ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                                 ghost_util.CONST_ERROR_MSG                                                   ||
                                                 'MAKE_BIND_PARAMS '                                                        ||
                                                 ghost_util.CONST_ERROR_MSG_PARAM                                             ||
                                                 ' p_params:' || ghost_util.wrap_error_params(p_params)                       ||
                                                 ' p_seperator:' || ghost_util.wrap_error_params(p_params),
                                                 SQLERRM);
    END make_bind_params;


    /*
    SUB   : make_bind_params_mt
            Takes in a string of values and stores it in a string array for values.
            Also creates a string array containing an array of generic bind vars.
    PARAM : p_params - String of bind values.
            p_seperator - Seperator used for bind values.
            o_array_of_bind_values - Array storing the bind var values.
    RETURN: None
    */
    PROCEDURE make_bind_params_mt(p_params IN VARCHAR2,
                                  p_seperator IN VARCHAR2,
                                  o_string_of_binds IN OUT VARCHAR2,
                                  o_array_of_bind_values IN OUT ghost_util.array_of_strings
                                  ) IS
         --v_element VARCHAR2(300);
         v_count NUMBER;
         v_orig_count NUMBER;
         v_index NUMBER;

    BEGIN

         v_count:= ghost_util.split_params(p_params, p_seperator, o_array_of_bind_values);
         v_orig_count := v_count;
--         dbms_output.put_line('Helper Count: ' || v_count);
--         dbms_output.put_line('Helper Array count: ' || o_array_of_bind_values.COUNT);

         -- Creates String in format : b_1,b_2,b_3,...
         --
         WHILE (v_count!=0) LOOP

                  v_count := v_count - 1;
                  v_index := v_orig_count - v_count;

                  IF v_index>1 THEN
                     o_string_of_binds:= o_string_of_binds || ' || '',''|| ';
                  END IF;
                  o_string_of_binds:= o_string_of_binds || CONST_BIND_VAR_PREFIX || v_index;
         END LOOP;

         EXCEPTION
                  WHEN OTHERS THEN
                       ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                                 ghost_util.CONST_ERROR_MSG                                                   ||
                                                 'MAKE_BIND_PARAMS_MT '                                                        ||
                                                 ghost_util.CONST_ERROR_MSG_PARAM                                             ||
                                                 ' p_params:' || ghost_util.wrap_error_params(p_params)                       ||
                                                 ' p_seperator:' || ghost_util.wrap_error_params(p_params),
                                                 SQLERRM);
    END make_bind_params_mt;



PROCEDURE insert_into_ghost_vm( p_insert_column IN VARCHAR2,
                                p_custom_columns IN VARCHAR2,
                                p_custom_column_values IN VARCHAR2,
                                p_ghost_vm_row IN vm_insert_table,
                                p_use_all_custom_columns BOOLEAN := FALSE) AS
         v_array_of_bind_values ghost_util.array_of_strings;
         v_dynamic_custom_column_vars VARCHAR2(4000) := '';

         v_sql_block VARCHAR2(32000);
         v_cursor INTEGER;
         v_rows INTEGER;
         v_custom_columns VARCHAR2(1000);
         v_custom_columns_bind_list VARCHAR2(4000) := '';
    BEGIN

        v_custom_columns_bind_list := v_dynamic_custom_column_vars;
        IF NOT p_use_all_custom_columns THEN
           v_array_of_bind_values := ghost_util.array_of_strings();
           v_custom_columns := p_custom_columns;
           IF p_custom_columns IS NOT NULL THEN
           make_bind_params(p_custom_column_values,
                            CONST_VALUE_SEP,
                            v_dynamic_custom_column_vars,
                            v_array_of_bind_values);
            v_dynamic_custom_column_vars := CONST_VALUE_SEP || v_dynamic_custom_column_vars;
            v_custom_columns := CONST_VALUE_SEP || v_custom_columns;
            v_custom_columns_bind_list := v_dynamic_custom_column_vars;
           END IF;
         ELSE
            v_custom_columns := CONST_VALUE_SEP || ALL_CUSTOM_COLUMNS;
            v_custom_columns_bind_list := CONST_VALUE_SEP || ALL_CUSTOM_COLUMNS_BIND;
         END IF;
   v_sql_block := 'INSERT INTO ghost_vm (uidvm,
                                    '|| p_insert_column ||',
                                    ghost_transaction_id,
                                    ghost_collection_id,
                                    bulk_id,
                                    bulk_insert_id,
                                    meta_starttime,
                                    meta_stoptime,
                                    metablob_spi,
                                    metablob_dst_participant,
                                    metablob_total,
                                    metablob_max,
                                    metablob_min,
                                    metablob_intervalcount' || chr(10) ||
                                    v_custom_columns || ')
                                  VALUES (:v_uidvm_sequence,
                                           :v_insert_column,
                                           :p_transaction_id,
                                           :p_collection_id,
                                           :p_bulk_id,
                                           :p_bulk_insert_id,
                                           :v_starttime,
                                           :v_stoptime,
                                           :v_metablob_spi,
                                           :v_metablob_dst_participant,
                                           :v_metablob_total,
                                           :v_metablob_max,
                                           :v_metablob_min,
                                           :v_metablob_intervalcount' || chr(10) ||
                                           v_custom_columns_bind_list || '
                                           )';

         v_cursor:= dbms_sql.open_cursor;
         dbms_sql.parse(v_cursor, v_sql_block, dbms_sql.native);

         dbms_sql.bind_variable(v_cursor, ':v_uidvm_sequence', p_ghost_vm_row.uidvm);
         dbms_sql.bind_variable(v_cursor, ':v_insert_column', p_ghost_vm_row.blob_value );
         dbms_sql.bind_variable(v_cursor, ':p_transaction_id', p_ghost_vm_row.ghost_transaction_id);
         dbms_sql.bind_variable(v_cursor, ':p_collection_id', p_ghost_vm_row.ghost_collection_id);
         dbms_sql.bind_variable(v_cursor, ':p_bulk_id', p_ghost_vm_row.bulk_id);
         dbms_sql.bind_variable(v_cursor, ':p_bulk_insert_id', p_ghost_vm_row.bulk_insert_id);
         dbms_sql.bind_variable(v_cursor, ':v_starttime', p_ghost_vm_row.meta_starttime);
         dbms_sql.bind_variable(v_cursor, ':v_stoptime', p_ghost_vm_row.meta_stoptime);
         dbms_sql.bind_variable(v_cursor, ':v_metablob_spi', p_ghost_vm_row.metablob_spi);
         dbms_sql.bind_variable(v_cursor, ':v_metablob_dst_participant', p_ghost_vm_row.metablob_dst_participant);
         dbms_sql.bind_variable(v_cursor, ':v_metablob_total', p_ghost_vm_row.metablob_total);
         dbms_sql.bind_variable(v_cursor, ':v_metablob_max', p_ghost_vm_row.metablob_max);
         dbms_sql.bind_variable(v_cursor, ':v_metablob_min', p_ghost_vm_row.metablob_min);
         dbms_sql.bind_variable(v_cursor, ':v_metablob_intervalcount', p_ghost_vm_row.metablob_intervalcount);



           -- Using the generated string: b_1, b_2, b_3 ...  included in the dynamic sql
           -- bind the values with the param values given.
           --
        IF (NOT p_use_all_custom_columns) THEN
          IF (v_dynamic_custom_column_vars IS NOT NULL) THEN
             FOR i IN v_array_of_bind_values.FIRST..v_array_of_bind_values.LAST LOOP
               dbms_sql.bind_variable(v_cursor, CONST_BIND_VAR_PREFIX || i, v_array_of_bind_values(i));
        --         dbms_output.put_line('Bind Value: ' || v_array_of_bind_values(i));
             END LOOP;
           END IF;
         ELSE
           dbms_sql.bind_variable(v_cursor, ':custom_1', p_ghost_vm_row.custom_1);
           dbms_sql.bind_variable(v_cursor, ':custom_2', p_ghost_vm_row.custom_2);
           dbms_sql.bind_variable(v_cursor, ':custom_3', p_ghost_vm_row.custom_3);
           dbms_sql.bind_variable(v_cursor, ':custom_4', p_ghost_vm_row.custom_4);
           dbms_sql.bind_variable(v_cursor, ':custom_5', p_ghost_vm_row.custom_5);
           dbms_sql.bind_variable(v_cursor, ':custom_6', p_ghost_vm_row.custom_6);
           dbms_sql.bind_variable(v_cursor, ':custom_7', p_ghost_vm_row.custom_7);
           dbms_sql.bind_variable(v_cursor, ':custom_8', p_ghost_vm_row.custom_8);
           dbms_sql.bind_variable(v_cursor, ':custom_9', p_ghost_vm_row.custom_9);
           dbms_sql.bind_variable(v_cursor, ':custom_10', p_ghost_vm_row.custom_10);
           dbms_sql.bind_variable(v_cursor, ':custom_date_1', p_ghost_vm_row.custom_date_1);
           dbms_sql.bind_variable(v_cursor, ':custom_date_2', p_ghost_vm_row.custom_date_2);
           dbms_sql.bind_variable(v_cursor, ':custom_date_3', p_ghost_vm_row.custom_date_3);
           dbms_sql.bind_variable(v_cursor, ':custom_date_4', p_ghost_vm_row.custom_date_4);
           dbms_sql.bind_variable(v_cursor, ':custom_date_5', p_ghost_vm_row.custom_date_5);
           dbms_sql.bind_variable(v_cursor, ':custom_date_6', p_ghost_vm_row.custom_date_6);
           dbms_sql.bind_variable(v_cursor, ':custom_date_7', p_ghost_vm_row.custom_date_7);
           dbms_sql.bind_variable(v_cursor, ':custom_date_8', p_ghost_vm_row.custom_date_8);
           dbms_sql.bind_variable(v_cursor, ':custom_date_9', p_ghost_vm_row.custom_date_9);
           dbms_sql.bind_variable(v_cursor, ':custom_date_10', p_ghost_vm_row.custom_date_10);
         END IF;

         v_rows := dbms_sql.execute(v_cursor);

         dbms_sql.close_cursor(v_cursor);

         EXCEPTION
                  WHEN OTHERS THEN
                       IF v_cursor!=0 THEN
                          dbms_sql.close_cursor(v_cursor);
                       END IF;
                       ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                                 ghost_util.CONST_ERROR_MSG                                                   ||
                                                 'INSERT_INTO_GHOST_VM '                                                      ||
                                                 ghost_util.CONST_ERROR_MSG_PARAM                                             ||
                                                 ' p_insert_column:' || ghost_util.wrap_error_params(p_insert_column) ||
                                                 ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                                 ' p_custom_column_values:' || ghost_util.wrap_error_params(p_custom_column_values) ||
                                                 ' v_dynamic_custom_column_vars:' || ghost_util.wrap_error_params(v_dynamic_custom_column_vars),
                                                 SQLERRM);
    END insert_into_ghost_vm;

PROCEDURE insert_into_ghost_vm( p_insert_column IN VARCHAR2,
                                p_ghost_vm_row IN vm_insert_table) AS
BEGIN
   insert_into_ghost_vm(p_insert_column, NULL, NULL, p_ghost_vm_row, TRUE);
   EXCEPTION
        WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                       ghost_util.CONST_ERROR_MSG                                                   ||
                                       'INSERT_INTO_GHOST_VM '                                                      ||
                                       ghost_util.CONST_ERROR_MSG_PARAM                                             ||
                                       ' p_insert_column:' || ghost_util.wrap_error_params(p_insert_column),
                                       SQLERRM);

END insert_into_ghost_vm;


FUNCTION binary_double_operation(p_left IN BINARY_DOUBLE,
                                 p_right IN BINARY_DOUBLE,
                                 p_operation IN NUMBER) RETURN BINARY_DOUBLE AS
    exception_op_not_found EXCEPTION;

BEGIN
    CASE p_operation
         WHEN CONSTANT_BINARY_OP_ADD THEN
             RETURN p_left + p_right;

         WHEN CONSTANT_BINARY_OP_SUBTRACT THEN
             RETURN p_left - p_right;

         WHEN CONSTANT_BINARY_OP_MULTIPLY THEN
             RETURN p_left * p_right;

         WHEN CONSTANT_BINARY_OP_DIVIDE THEN
             RETURN p_left / p_right;

         ELSE
             RAISE exception_op_not_found;

    END CASE;

    RETURN 0;
    EXCEPTION
         WHEN exception_op_not_found THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BINARY_DOUBLE_OPERATION  : Operation not found!'       ||
                                           ' p_left:' || ghost_util.wrap_error_params(p_left)      ||
                                           ' p_right' || ghost_util.wrap_error_params(p_right)     ||
                                           ' p_operation' || ghost_util.wrap_error_params(p_operation),
                                           SQLERRM);
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BINARY_DOUBLE_OPERATION  '                             ||
                                           ' p_left:' || ghost_util.wrap_error_params(p_left)      ||
                                           ' p_right' || ghost_util.wrap_error_params(p_right)     ||
                                           ' p_operation' || ghost_util.wrap_error_params(p_operation),
                                           SQLERRM);

END binary_double_operation;



FUNCTION binary_double_round(p_value IN BINARY_DOUBLE,
                             p_num_decimals IN INTEGER) RETURN BINARY_DOUBLE AS
--    l_multiplier INTEGER;
--    l_fraction NUMBER;
BEGIN
    return ROUND(p_value, p_num_decimals);

--    return ROUND(TRUNC(p_value, (p_num_decimals+1)), p_num_decimals);

--    l_fraction := power(10, (p_num_decimals * -1));
--    l_multiplier := cast((p_value / l_fraction) as INTEGER);
--    RETURN cast((l_fraction * l_multiplier ) as BINARY_DOUBLE);

END binary_double_round;



FUNCTION number_round(p_value IN NUMBER,
                      p_num_decimals IN INTEGER) RETURN NUMBER AS
BEGIN
    return ROUND(p_value, p_num_decimals);
END number_round;


/*
FUNCTION get_driver_id(p_custom_column_values IN VARCHAR2) RETURN VARCHAR2 AS
    BEGIN
         RETURN REPLACE(p_custom_column_values,CONST_CUSTOMCOL_VALUE_SEP,'');
         EXCEPTION
            WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'GET_DRIVER_ID  '                             ||
                                           ' p_custom_column_values:' || ghost_util.wrap_error_params(p_custom_column_values),
                                           SQLERRM);
    END get_driver_id;
*/

FUNCTION blob_bulk_operation(v_blob_table IN ghost_tab_blob,
                             p_transaction_id IN NUMBER,
                             p_bulk_id IN NUMBER,
                             p_new_collection_id IN NUMBER,
                             p_bulk_insert_id IN NUMBER,
                             p_custom_columns IN VARCHAR2,
                             p_custom_column_values IN VARCHAR2) RETURN NUMBER AS
--  TYPE v_bd_tab IS TABLE OF BINARY_DOUBLE;
--  v_bd_table v_bd_tab;
  --v_blob_table v_blob_tab;

  v_blob BLOB;
--  v_raw_number RAW(8);
  --v_status_buffer VARCHAR2(32760);
--  v_binary_number BINARY_DOUBLE;
--  v_raw_end_of_interval_match RAW(8) := utl_raw.cast_from_binary_double(0);

--  v_raw_buffer_left RAW(32760); --3640  * ( 8 + 1)
--  v_raw_buffer_right RAW(32760); --3640  * ( 8 + 1)
  v_raw_buffer RAW(32760);

--  v_blob_length_left NUMBER;
--  v_blob_length_right NUMBER;

  v_num_intervals_left NUMBER;
  v_num_intervals_right NUMBER;

  v_num_iterations NUMBER;

  v_blob_left RAW(32760);
  v_blob_right BLOB;

  v_pos NUMBER;
  v_pos_statuscode NUMBER;
  v_uidvm_sequence NUMBER;

  v_binary_number BINARY_DOUBLE;

  --v_metablob_starttime DATE;
  --v_metablob_stoptime DATE;
  --v_metablob_spi NUMBER;
  v_metablob_total NUMBER;
  v_metablob_max NUMBER;
  v_metablob_min NUMBER;
  v_metablob_intervalcount NUMBER;
  v_status_code VARCHAR2(1);

  v_ghost_vm_row vm_insert_table;

  BEGIN

--  dbms_output.put_line(SYSTIMESTAMP);
   v_raw_buffer := NULL;
       /*
        SELECT blob_data
  BULK COLLECT INTO v_blob_table
          FROM CSI2_BDINTDHEADER
         WHERE uidbdintdheader BETWEEN 1 AND 20000;
         */

--  dbms_output.put_line(SYSTIMESTAMP);
  --dbms_output.put_line('After select');
--  dbms_output.put_line(v_blob_table.COUNT);

   --v_blob_left := v_blob_table(1);
--   dbms_output.put_line('Timestamp Before get sequence:' || SYSTIMESTAMP);

   v_uidvm_sequence := Seq_GHOSTUIDVM.NEXTVAL;

--   dbms_output.put_line('Timestamp After squequence:' || SYSTIMESTAMP);



   IF (v_blob_table.COUNT=1)THEN
       v_metablob_intervalcount := get_blob_length(v_blob_table(1));

       v_blob := dbms_lob.substr(v_blob_table(1),LENGTH(v_blob_table(1)));

--       v_pos:= 1;
       --v_pos_statuscode := (CONST_INTERVAL_BYTE_SIZE*(v_metablob_intervalcount+1))+2;
       --v_status_code := utl_raw.cast_to_varchar2(utl_raw.substr(v_blob,v_pos_statuscode , CONST_STATUS_BYTE_SIZE));
       v_status_code := get_status_code(v_blob, v_metablob_intervalcount, 1);

--       v_metablob_total :=  utl_raw.cast_to_binary_double(utl_raw.reverse(utl_raw.substr(v_blob,v_pos , CONST_INTERVAL_BYTE_SIZE)));
/*
       v_metablob_total := get_metablob_interval(v_blob, 1);

       --dbms_output.put_line('Status Code: ' || v_status_code || ' Pos:' || v_pos_statuscode);

       IF (v_status_code != CONST_BLOB_EMPTY_STATUS_CODE) THEN
         v_metablob_max := v_metablob_total;
         v_metablob_min := v_metablob_total;
       END IF;
       */
       
       set_init_metablob_attributes(v_status_code,
                                    get_metablob_interval(v_blob, 1),
                                    v_metablob_total,
                                    v_metablob_max,
                                    v_metablob_min);


       FOR p in 2..v_metablob_intervalcount LOOP
           --v_pos:= (CONST_INTERVAL_BYTE_SIZE*(p-1))+1;
           --v_pos_statuscode := (CONST_INTERVAL_BYTE_SIZE*(v_metablob_intervalcount+1)) + p;
           --v_binary_number :=  utl_raw.cast_to_binary_double(utl_raw.reverse(utl_raw.substr(v_blob,v_pos , CONST_INTERVAL_BYTE_SIZE)));
           v_binary_number := get_metablob_interval(v_blob, p);
           --v_status_code := utl_raw.cast_to_varchar2(utl_raw.substr(v_blob,v_pos_statuscode , 1));
           v_status_code := get_status_code(v_blob, v_metablob_intervalcount, p);

           --dbms_output.put_line('Status Code: ' || v_status_code || ' Pos:' || v_pos_statuscode);
           /*
           IF (v_status_code != CONST_BLOB_EMPTY_STATUS_CODE) THEN
             IF(v_metablob_max < v_binary_number) THEN
                v_metablob_max := v_binary_number;
             END IF;


             IF(v_metablob_min > v_binary_number) THEN
                v_metablob_min := v_binary_number;
             END IF;

           END IF;


           v_metablob_total := v_metablob_total + v_binary_number;
           */
           set_metablob_attributes(v_status_code,
                                   v_binary_number,
                                   v_metablob_total,
                                   v_metablob_max,
                                   v_metablob_min);
       END LOOP;


      v_ghost_vm_row.uidvm := v_uidvm_sequence;
      v_ghost_vm_row.blob_value := v_blob;
      v_ghost_vm_row.ghost_transaction_id := p_transaction_id;
      v_ghost_vm_row.ghost_collection_id := p_new_collection_id;
      v_ghost_vm_row.bulk_id := p_bulk_id;
      v_ghost_vm_row.bulk_insert_id := p_bulk_insert_id;
      v_ghost_vm_row.meta_starttime := TRUNC(SYSDATE);
      v_ghost_vm_row.meta_stoptime := TRUNC(SYSDATE)+ 86399/86400;
      v_ghost_vm_row.metablob_spi := 86400/v_metablob_intervalcount;
      v_ghost_vm_row.metablob_dst_participant := NULL;
      v_ghost_vm_row.metablob_total := v_metablob_total;
      v_ghost_vm_row.metablob_max := v_metablob_max;
      v_ghost_vm_row.metablob_min := v_metablob_min;
      v_ghost_vm_row.metablob_intervalcount := v_metablob_intervalcount;

       insert_into_ghost_vm(CONST_BLOB_INSERT_COLUMN,
                            p_custom_columns,
                            p_custom_column_values,
                            v_ghost_vm_row);

       RETURN v_uidvm_sequence;
   END IF;

  -- dbms_output.put_line('Timestamp After IF:' || SYSTIMESTAMP);

   --v_blob_right := v_blob_table(2);
  --dbms_output.put_line('After assigning blobs');
  /*
    SELECT blob_data, blob_data
      INTO v_blob_left, v_blob_right
      FROM CSI2_BDINTDHEADER
     WHERE uidbdintdheader = 5;
    */

--dbms_output.put_line('Timestamp Before Substring:' || SYSTIMESTAMP);
   v_blob_left := dbms_lob.substr(v_blob_table(1),LENGTH(v_blob_table(1)));
--   dbms_output.put_line('Timestamp After Substring:' || SYSTIMESTAMP);

   --dbms_output.put_line(v_blob_left);
--   dbms_output.put_line('Testing Outer Loop '||SYSTIMESTAMP);
   FOR i IN 2..v_blob_table.COUNT LOOP
         v_blob_right := v_blob_table(i);
         --dbms_output.put_line(utl_raw.LENGTH(v_blob_left));
--         dbms_output.put_line('Before setting lengths '||SYSTIMESTAMP);
         v_num_intervals_left := (utl_raw.LENGTH(v_blob_left) - CONST_INTERVAL_BYTE_SIZE)/9;
         v_num_intervals_right := get_blob_length(v_blob_right);
--         dbms_output.put_line('After setting Lengths '||SYSTIMESTAMP);
--         dbms_output.put_line('Setting lenghts');
--         dbms_output.put_line(v_num_intervals_left);
--         dbms_output.put_line(v_num_intervals_right);

--         dbms_output.put_line('Size of blob left: ' || v_num_intervals_left);
--         dbms_output.put_line('Size of blob right: ' || v_num_intervals_right);
         v_num_iterations := v_num_intervals_left;

--         IF v_num_iterations > v_num_intervals_right THEN
--            v_num_iterations := v_num_intervals_right;
--         END IF;

--         dbms_output.put_line('Number of iterations: ' || v_num_iterations);
--         dbms_output.put_line('On Blob : ' || i);

         --dbms_output.put_line('Setting iterations');
--         dbms_output.put_line('Inner Loop'||SYSTIMESTAMP);
--         v_pos := 1;
--         v_bd_table := v_bd_tab();
--         v_bd_table.extend(v_num_iterations);
         /*
         FOR a in 1..v_num_iterations LOOP
           v_bd_table(a) := utl_raw.cast_to_binary_double(utl_raw.reverse(utl_raw.substr(v_blob_left,(8*(a-1))+1, 8)));
         END LOOP;
         */


         FOR j in 1..v_num_iterations LOOP
         /*
             --dbms_output.put_line(v_pos);
             --dbms_output.put_line('Buffer outptu of LEft : ' || v_blob_left);
             dbms_output.put_line('Set Raw Buffers Start'||SYSTIMESTAMP);
             v_raw_buffer_left := utl_raw.substr(v_blob_left,v_pos , 8);
             --dbms_output.put_line('Setting LEFT buffer:' || v_raw_buffer_left);
             v_raw_buffer_right := dbms_lob.substr(v_blob_right,8,v_pos);
             --dbms_output.put_line('Setting RIGHT buffer:' || v_raw_buffer_right);
             dbms_output.put_line('Set Raw Buffers END'||SYSTIMESTAMP);

             v_binary_number := utl_raw.cast_to_binary_double(utl_raw.reverse(v_raw_buffer_left));
             --dbms_output.put_line('Setting LEFT binary number:' || v_binary_number);
             v_binary_number := v_binary_number + (utl_raw.cast_to_binary_double(utl_raw.reverse(v_raw_buffer_right)));
             dbms_output.put_line('End binary addition'||SYSTIMESTAMP);
             v_status_buffer := v_status_buffer || ' ';
             v_raw_number := utl_raw.cast_from_binary_double(v_binary_number);
             v_raw_buffer := v_raw_buffer || utl_RAW.REVERSE(v_raw_number);
             v_pos:= (8*j)+1;
             dbms_output.put_line('End resetting and setting buffer'||SYSTIMESTAMP);
             */

             v_pos:= (CONST_INTERVAL_BYTE_SIZE*(j-1))+1;
--             v_status_buffer := v_status_buffer || ' ';
--             dbms_output.put_line('Before adding blob number '||SYSTIMESTAMP);
             --v_raw_number := utl_RAW.REVERSE(utl_raw.cast_from_binary_double(utl_raw.cast_to_binary_double(utl_raw.reverse(utl_raw.substr(v_blob_left,v_pos , 8))) + (utl_raw.cast_to_binary_double(utl_raw.reverse(dbms_lob.substr(v_blob_right,8,v_pos))))));
             --dbms_output.put_line('After adding blob number: ' || v_pos || '     ' || j);
             --dbms_output.put_line('After adding blob number '||SYSTIMESTAMP);


             v_raw_buffer := v_raw_buffer || utl_RAW.REVERSE(utl_raw.cast_from_binary_double(utl_raw.cast_to_binary_double(utl_raw.reverse(utl_raw.substr(v_blob_left,v_pos , CONST_INTERVAL_BYTE_SIZE))) + (utl_raw.cast_to_binary_double(utl_raw.reverse(dbms_lob.substr(v_blob_right,CONST_INTERVAL_BYTE_SIZE,v_pos))))));

--             v_bd_table(j) :=  v_bd_table(j) + (utl_raw.cast_to_binary_double(utl_raw.reverse(dbms_lob.substr(v_blob_right,8,v_pos))));
--dbms_output.put_line(v_bd_table(j));
             --v_raw_buffer := v_raw_buffer || utl_RAW.REVERSE(utl_raw.cast_from_binary_double(utl_raw.cast_to_binary_double(utl_raw.reverse(utl_raw.substr(v_blob_left,v_pos , 8))) + (utl_raw.cast_to_binary_double(utl_raw.reverse(dbms_lob.substr(v_blob_right,8,v_pos))))));

         END LOOP;

--         dbms_output.put_line(v_bd_table.COUNT);
--         FOR k in 1..v_num_iterations LOOP
--         dbms_output.put_line(v_bd_table(k));
--         dbms_output.put_line(utl_raw.cast_from_binary_double(v_bd_table(k)));
--          v_raw_buffer := v_raw_buffer || utl_RAW.REVERSE(utl_raw.cast_from_binary_double(v_bd_table(k)));
--         END LOOP;
--         dbms_output.put_line('Exit Inner Loop'||SYSTIMESTAMP);
         --dbms_output.put_line(v_blob_left);
--         dbms_output.put_line(v_raw_buffer);
         --dbms_output.put_line('AFter initla loop:' || i );
         --v_raw_number := utl_raw.cast_from_binary_double(0);
         --v_raw_buffer := v_raw_buffer || utl_RAW.REVERSE(v_raw_number);
         --dbms_output.put_line(v_raw_buffer);
--         v_status_buffer := v_status_buffer || ' ' ;
         --dbms_output.put_line('Last Substr Start'||SYSTIMESTAMP);
--         dbms_output.put_line('Before setting status buffer '||SYSTIMESTAMP);
--dbms_output.put_line(v_blob_left);

         v_blob_left := v_raw_buffer || utl_raw.substr(v_blob_left,(v_num_iterations*CONST_INTERVAL_BYTE_SIZE)+1);
--         v_blob_left := v_raw_buffer || utl_raw.substr(v_blob_left,get_status_code_pos(v_num_iterations,1));

--         dbms_output.put_line(v_blob_left);
--         dbms_output.put_line('After setting status buffer '||SYSTIMESTAMP);
         --dbms_output.put_line('Last Substr END'||SYSTIMESTAMP);

         v_raw_buffer := NULL;
--         v_status_buffer := NULL;
         --dbms_output.put_line('Nulling out busdfdsaffers' || i );
         --dbms_output.put_line('Nulling out bufsdfsdadasfdsfasfsdsdafasdffers' || i );
         --dbms_output.put_line(v_blob_left);
     END LOOP;

--     dbms_output.put_line('Testing End of Loop ' || SYSTIMESTAMP);
     dbms_lob.createtemporary(v_blob,TRUE);
     dbms_lob.open(v_blob, dbms_lob.lob_readwrite);
     ghost_util.ghost_write_raw_to_blob(v_blob, v_blob_left);
     --ghost_util.ghost_write_varchar2_to_blob(v_blob, v_status_buffer);
     dbms_lob.close(v_blob);
--     dbms_output.put_line('Timestamp Finishec creating Blob:' || SYSTIMESTAMP);


       --v_blob_meta := dbms_lob.substr(v_blob,LENGTH(v_blob));

--       v_pos:= 1;
--       v_pos_statuscode := (CONST_INTERVAL_BYTE_SIZE*(v_metablob_intervalcount+1))+2;
       v_status_code := get_status_code(v_blob,v_num_iterations,1);
--       dbms_output.put_line('get status code :' || SYSTIMESTAMP);
--
       set_init_metablob_attributes(v_status_code,
                                    get_metablob_interval(v_blob, 1),
                                    v_metablob_total,
                                    v_metablob_max,
                                    v_metablob_min);

       --v_metablob_intervalcount := get_blob_length(v_blob_table(1));
--       dbms_output.put_line('get intervalcount :' || SYSTIMESTAMP);
       FOR p in 2..v_num_iterations LOOP
--           dbms_output.put_line('pos :' || v_pos);
           v_binary_number :=  get_metablob_interval(v_blob, p);
           v_status_code := get_status_code(v_blob,v_num_iterations,p);

           set_metablob_attributes(v_status_code,
                                   v_binary_number,
                                   v_metablob_total,
                                   v_metablob_max,
                                   v_metablob_min);
       END LOOP;

/*
      INSERT INTO ghost_vm (uidvm,
                   blob_value,
                   ghost_transaction_id,
                   ghost_collection_id,
                   bulk_id,
                   bulk_insert_id,
                   meta_starttime,
                   meta_stoptime,
                   metablob_spi,
                   metablob_total,
                   metablob_max,
                   metablob_min,
                   metablob_intervalcount,
                   custom_1)
           VALUES (v_uidvm_sequence,
                    v_blob,
                    p_transaction_id,
                    p_new_collection_id,
                    p_bulk_id,
                    p_bulk_insert_id,
                    TRUNC(SYSDATE),
                    TRUNC(SYSDATE) + 86399/86400,
                    86400/v_metablob_intervalcount,
                    v_metablob_total,
                    v_metablob_max,
                    v_metablob_min,
                    v_metablob_intervalcount,
                    p_drive_id);
                    */



      v_ghost_vm_row.uidvm := v_uidvm_sequence;
      v_ghost_vm_row.blob_value := v_blob;
      v_ghost_vm_row.ghost_transaction_id := p_transaction_id;
      v_ghost_vm_row.ghost_collection_id := p_new_collection_id;
      v_ghost_vm_row.bulk_id := p_bulk_id;
      v_ghost_vm_row.bulk_insert_id := p_bulk_insert_id;
      v_ghost_vm_row.meta_starttime := TRUNC(SYSDATE);
      v_ghost_vm_row.meta_stoptime := TRUNC(SYSDATE)+ 86399/86400;
      v_ghost_vm_row.metablob_spi := 86400/v_num_iterations;
      v_ghost_vm_row.metablob_total := v_metablob_total;
      v_ghost_vm_row.metablob_max := v_metablob_max;
      v_ghost_vm_row.metablob_min := v_metablob_min;
      v_ghost_vm_row.metablob_intervalcount := v_num_iterations;

      insert_into_ghost_vm(CONST_BLOB_INSERT_COLUMN,
                           p_custom_columns,
                           p_custom_column_values,
                           v_ghost_vm_row);


     --dbms_output.put_line(SYSTIMESTAMP);
     --dbms_output.put_line('Timestamp After Blob Insert:' || SYSTIMESTAMP);

     RETURN v_uidvm_sequence;
     -- + utl_raw.cast_to_binary_double((utl_raw.reverse((v_raw_buffer_right)))));
    -- dbms_output.put_line(SYSTIMESTAMP);
     --dbms_output.put_line('Finished');

     EXCEPTION
        WHEN OTHERS THEN
            IF(v_blob IS NOT NULL) THEN
               IF(dbms_lob.isopen(v_blob) <> 0 ) THEN
                   dbms_lob.close(v_blob);
               END IF;

             END IF;

            ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                          ghost_util.CONST_ERROR_MSG                                             ||
                                          'BLOB_BULK_OPERATION '                                                ||
                                          ' p_transaction_id:' || ghost_util.wrap_error_params(p_transaction_id) ||
                                          ' p_bulk_id:' || ghost_util.wrap_error_params(p_bulk_id) ||
                                          ' p_new_collection_id:' || ghost_util.wrap_error_params(p_new_collection_id) ||
                                          ' p_bulk_insert_id:' || ghost_util.wrap_error_params(p_bulk_insert_id) ||
                                          --' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                          --' p_custom_column_values:' || ghost_util.wrap_error_params(p_custom_column_values) ||
                                          ' v_blob_table.COUNT:' || ghost_util.wrap_error_params(v_blob_table.COUNT),
                                          SQLERRM);

  END blob_bulk_operation;



FUNCTION is_equal_blob(p_uidvm NUMBER,
                  p_uidvm_compare NUMBER) RETURN NUMBER IS
       v_result NUMBER;

    BEGIN
         SELECT dbms_lob.compare(a.blob_value,b.blob_value,32768,1,1)
           INTO v_result
           FROM ghost_vm a,
                ghost_vm b
          WHERE a.uidvm = p_uidvm
            AND b.uidvm = p_uidvm_compare;

          IF v_result IS NULL THEN
             v_result := -1;
          END IF;

          return v_result;
    END is_equal_blob;



FUNCTION possess_bulk_meta_blob(p_select_query VARCHAR2,
                                p_collection_id NUMBER) RETURN NUMBER IS
        v_possession_id NUMBER;
--        v_count NUMBER;

    BEGIN

      SELECT Seq_GHOSTPIDVM.Nextval
          INTO v_possession_id
         FROM DUAL;


      EXECUTE IMMEDIATE 'INSERT /*+ APPEND */ INTO ghost_vm(' ||
                               CONST_POSSESS_COLUMNS ||
                               CONST_POSSESS_COLUMNS_BULK ||
                               ') ' ||
                                p_select_query USING p_collection_id, v_possession_id;

        COMMIT;

        RETURN v_possession_id;

        EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                       ghost_util.CONST_ERROR_MSG                                          ||
                                       'POSSESS_BULK_META_BLOB  '                                               ||
                                       ' p_collection_id:' || ghost_util.wrap_error_params(p_collection_id),
                                       SQLERRM);

    END possess_bulk_meta_blob;




FUNCTION possess_helper(p_select_query IN VARCHAR2,
                             p_collection_id IN  NUMBER,
                             p_sql_open_values IN VARCHAR2,
                             p_vm_columns IN VARCHAR2,
                             p_custom_columns IN VARCHAR2,
                             p_header_columns IN VARCHAR2) RETURN NUMBER IS
        v_possession_id NUMBER;
        v_sql_open_using_vars VARCHAR2(1500);
        v_array_of_bind_values ghost_util.array_of_strings;
        v_cursor integer;
        v_rows integer;
        v_sql VARCHAR2(10000);

    BEGIN

      SELECT Seq_GHOSTPIDVM.Nextval
          INTO v_possession_id
         FROM DUAL;


      v_array_of_bind_values := ghost_util.array_of_strings();
      v_sql_open_using_vars := '';
      IF (p_sql_open_values != 'NULL') THEN
        make_bind_params(p_sql_open_values,
                         CONST_INTERVAL_VALUE_SEP,
                         v_sql_open_using_vars,
                         v_array_of_bind_values);
                         v_sql_open_using_vars := ',' || v_sql_open_using_vars;
      END IF;


      --dbms_output.put_line(v_sql_open_using_vars);
      v_sql := 'BEGIN ' ||
      'EXECUTE IMMEDIATE ''INSERT /*+ APPEND */ INTO ghost_vm(' ||
                                  p_header_columns ||
                                  p_vm_columns ||
                                  p_custom_columns||
                                  ') '' || ''' || REPLACE(p_select_query,'''','''''') || ''' USING :CID, :PID '||
                                  v_sql_open_using_vars ||';END;';

       --dbms_output.put_line(v_sql);

        v_cursor:= dbms_sql.open_cursor;
        dbms_sql.parse(v_cursor, v_sql, dbms_sql.native);

        -- Using the generated string: b_1, b_2, b_3 ...  included in the dynamic sql
        -- bind the values with the param values given.
        --
        dbms_sql.bind_variable(v_cursor,':CID',p_collection_id);
        dbms_sql.bind_variable(v_cursor,':PID',v_possession_id);

        IF (p_sql_open_values != 'NULL') THEN
          FOR i IN 1..v_array_of_bind_values.COUNT LOOP
            dbms_sql.bind_variable(v_cursor, CONST_BIND_VAR_PREFIX || i, v_array_of_bind_values(i));
          END LOOP;

        END IF;


        v_rows := dbms_sql.execute(v_cursor);

        dbms_sql.close_cursor(v_cursor);

        COMMIT;

        RETURN v_possession_id;

        EXCEPTION
         WHEN OTHERS THEN
             IF v_cursor!=0 THEN
                 dbms_sql.close_cursor(v_cursor);
              END IF;

              ROLLBACK;
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                       ghost_util.CONST_ERROR_MSG                                          ||
                                       'POSSESS_HELPER  '                                               ||
                                       ' p_collection_id:' || ghost_util.wrap_error_params(p_collection_id) ||
                                       ' p_sql_open_values:' || ghost_util.wrap_error_params(p_sql_open_values) ||
                                       ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns),
                                       SQLERRM);

    END possess_helper;



FUNCTION possess(p_select_query VARCHAR2,
                 p_collection_id NUMBER,
                 p_sql_open_values IN VARCHAR2,
                 p_vm_columns IN VARCHAR2,
                 p_custom_columns IN VARCHAR2) RETURN NUMBER IS
    BEGIN
        RETURN possess_helper(p_select_query,
                              p_collection_id,
                              p_sql_open_values,
                              p_vm_columns,
                              p_custom_columns,
                              CONST_POSSESS_COLUMNS);
        EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                          ||
                                           'POSSESS  '                                               ||
                                           ' p_collection_id:' || ghost_util.wrap_error_params(p_collection_id),
                                           SQLERRM);

    END possess;


FUNCTION possess_return_count(p_select_query VARCHAR2,
                              p_collection_id NUMBER,
                              p_sql_open_values IN VARCHAR2,
                              p_vm_columns IN VARCHAR2,
                              p_custom_columns IN VARCHAR2) RETURN NUMBER IS
        v_count NUMBER;
        v_possession_id NUMBER;

    BEGIN

     v_possession_id := possess_helper(p_select_query,
                                       p_collection_id,
                                       p_sql_open_values,
                                       p_vm_columns,
                                       p_custom_columns,
                                       CONST_POSSESS_COLUMNS);
       COMMIT;

        SELECT COUNT(0)
          INTO v_count
          FROM ghost_vm
         WHERE ghost_possession_id = v_possession_id;


        RETURN v_count;

        EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                          ||
                                           'POSSESS_RETURN_COUNT  '                                               ||
                                           ' p_collection_id:' || ghost_util.wrap_error_params(p_collection_id),
                                           SQLERRM);

    END possess_return_count;



FUNCTION create_blob_data (p_spi IN NUMBER,
                           p_value IN NUMBER,
                           p_status_code IN VARCHAR2) RETURN BLOB IS
  v_blob BLOB;
  v_raw_number raw_interval;

  v_pos NUMBER;
  v_num_intervals NUMBER;
  v_statuscodes status_code_varchar;

    BEGIN

         v_num_intervals := 86400/p_spi;

         dbms_lob.createtemporary(v_blob,TRUE);
         dbms_lob.open(v_blob, dbms_lob.lob_readwrite);
         v_raw_number := utl_RAW.REVERSE(utl_raw.cast_from_binary_double(p_value));

         FOR j in 1..v_num_intervals LOOP
             v_pos:= get_metablob_number_position(j);--(CONST_INTERVAL_BYTE_SIZE*(j-1))+1;
             ghost_util.ghost_write_raw_to_blob(v_blob, v_raw_number, v_pos);
             v_statuscodes := v_statuscodes || p_status_code;
         END LOOP;


         --v_pos:= get_metablob_number_position(v_num_intervals);--(CONST_INTERVAL_BYTE_SIZE*(v_num_intervals))+1;
         ghost_util.ghost_write_raw_to_blob(v_blob, CONST_INTERVAL_BLOB_MARKER_RAW);
         ghost_util.ghost_write_varchar2_to_blob(v_blob, v_statuscodes);

         dbms_lob.close(v_blob);

         RETURN v_blob;
    EXCEPTION
         WHEN OTHERS THEN
             IF(v_blob IS NOT NULL) THEN
               IF(dbms_lob.isopen(v_blob) <> 0 ) THEN
                   dbms_lob.close(v_blob);
               END IF;

             END IF;

             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                 ||
                                           'CREATE_BLOB_DATA  '                                               ||
                                           ' p_spi:' || ghost_util.wrap_error_params(p_spi) ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value),
                                           SQLERRM);

    END create_blob_data;



PROCEDURE create_empty_blob_data (p_uidvm IN NUMBER,
                                  p_starttime IN DATE,
                                  p_stoptime IN DATE,
                                  p_spi IN NUMBER,
                                  p_value IN NUMBER,
                                  p_dst_participant IN CHAR) IS
  v_blob BLOB;
  v_raw_number raw_interval;

  v_pos NUMBER;
  v_num_intervals NUMBER;
  v_metablob_total NUMBER;
  v_metablob_max NUMBER;
  v_metablob_min NUMBER;

  v_statuscodes status_code_varchar;

    BEGIN
         --Start setting up variables to calcualte Total,Min,Max etc...
         v_blob := create_blob_data(p_spi, p_value, CONST_BLOB_GOOD_STATUS_CODE);
         v_num_intervals := get_blob_length(v_blob);
         v_metablob_total := p_value * v_num_intervals;
         v_metablob_max := p_value;
         v_metablob_min := p_value;


         UPDATE ghost_vm
            SET blob_value = v_blob,
                meta_starttime = p_starttime,
                meta_stoptime = p_stoptime,
                metablob_total = v_metablob_total,
                metablob_max = v_metablob_max,
                metablob_min = v_metablob_min,
                metablob_intervalcount = v_num_intervals,
                metablob_spi = p_spi,
                metablob_dst_participant = p_dst_participant
          WHERE uidvm = p_uidvm;


         COMMIT;
    EXCEPTION
         WHEN OTHERS THEN
             IF(v_blob IS NOT NULL) THEN
               IF(dbms_lob.isopen(v_blob) <> 0 ) THEN
                   dbms_lob.close(v_blob);
               END IF;

             END IF;

             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                 ||
                                           'CREATE_EMPTY_BLOB_DATA  '                                               ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                           ' p_starttime:' || ghost_util.wrap_error_params(ghost_util.getDateToF(p_starttime)) ||
                                           ' p_stoptime:' || ghost_util.wrap_error_params(ghost_util.getDateToF(p_stoptime)) ||
                                           ' p_spi:' || ghost_util.wrap_error_params(p_spi) ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value) ||
                                           ' p_dst_participant:' || ghost_util.wrap_error_params(p_dst_participant),
                                           SQLERRM);

    END create_empty_blob_data;


PROCEDURE blob_copy_from (p_uidvm_to NUMBER,
                          p_uidvm_from NUMBER) IS
        --v_ghost_row ghost_vm%ROWTYPE;
        v_ghost_pointer ghost_vm.ghost_pointer%TYPE;
        v_ghost_pointer_table ghost_vm.ghost_pointer_table%TYPE;
        v_ghost_pointer_field ghost_vm.ghost_pointer_field%TYPE;
        v_ghost_column ghost_vm.ghost_data_column%TYPE;
        v_blob BLOB;

    BEGIN
          SELECT --UIDVM,
                 GHOST_POINTER,
                 GHOST_POINTER_TABLE,
                 GHOST_POINTER_FIELD,
                 GHOST_DATA_COLUMN,
                 --LAST_UPDATE_DATE,
                 BLOB_VALUE
                 --NUMBER_VALUE,
                 --STRING_VALUE
            INTO v_ghost_pointer, v_ghost_pointer_table, v_ghost_pointer_field, v_ghost_column, v_blob
            FROM ghost_vm
           WHERE uidvm = p_uidvm_from;


           IF v_blob IS NOT NULL THEN
              UPDATE ghost_vm
                 SET blob_value = (SELECT blob_value FROM ghost_vm WHERE uidvm = p_uidvm_from)
               WHERE uidvm = p_uidvm_to;

           ELSE
               EXECUTE IMMEDIATE 'UPDATE ghost_vm ' ||
                                 'SET blob_value = (SELECT ' || v_ghost_column ||
                                       ' FROM ' || v_ghost_pointer_table ||
                                      ' WHERE ' || v_ghost_pointer_field || '  = :ghost_pointer)' ||
                                 'WHERE uidvm = :p_uidvm_to' USING v_ghost_pointer, p_uidvm_to;

           END IF;


        COMMIT;
        EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                 ||
                                           'BLOB_COPY_FROM  '                                         ||
                                           ' p_uidvm_to:' || ghost_util.wrap_error_params(p_uidvm_to) ||
                                           ' p_uidvm_from:' || ghost_util.wrap_error_params(p_uidvm_from),
                                           SQLERRM);

    END blob_copy_from;




FUNCTION blob_get_interval_value(p_uidvm NUMBER,
                                 p_pos NUMBER,
                                 p_sql_query VARCHAR2) RETURN BINARY_DOUBLE AS
  v_remote BOOLEAN;
  v_blob BLOB;
  v_num_intervals NUMBER;
  e_blob_is_null EXCEPTION;
  e_outofbounds_blob_interval EXCEPTION;

  BEGIN
         v_remote := get_blob(v_blob, p_sql_query, p_uidvm);

         IF(v_blob IS NOT NULL) THEN
           v_num_intervals := get_blob_length(v_blob);
           IF(v_num_intervals<p_pos) THEN
             RAISE e_outofbounds_blob_interval;
           END IF;


           COMMIT;
           RETURN get_metablob_interval(v_blob, p_pos);
         ELSE
           RAISE e_blob_is_null;

         END IF;


    EXCEPTION
         WHEN e_outofbounds_blob_interval THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_GET_INTERVAL_VALUE  Interval position is out of bounds! Max interval pos is: ' || v_num_intervals ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);
         WHEN e_blob_is_null THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_GET_INTERVAL_VALUE  Blob value is NULL!'                             ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);

         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_GET_INTERVAL_VALUE  '                             ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);

  END blob_get_interval_value;




PROCEDURE blob_set_interval_value(p_uidvm NUMBER,
                                  p_pos NUMBER,
                                  p_value NUMBER,
                                  p_sql_query VARCHAR2) AS
  v_blob BLOB;
  v_num_intervals NUMBER;
  v_remote BOOLEAN := false;
  v_status_code CHAR;
  v_metablob_total NUMBER;
  v_metablob_max NUMBER;
  v_metablob_min NUMBER;
  v_binary_number BINARY_DOUBLE;

  BEGIN
         v_blob := get_blob_updatable(p_sql_query, p_uidvm);

         v_num_intervals := get_blob_length(v_blob);
         IF(v_num_intervals<p_pos) THEN
           RAISE e_outofbounds_blob_interval;
         END IF;


         set_metablob_interval(v_blob, p_pos, p_value);

         v_status_code := get_status_code(v_blob,v_num_intervals,1);
         set_init_metablob_attributes(v_status_code,
                                      get_metablob_interval(v_blob, 1),
                                      v_metablob_total,
                                      v_metablob_max,
                                      v_metablob_min);
         --Update min,max,etc...
         FOR j in 2..v_num_intervals LOOP
             v_binary_number := get_metablob_interval(v_blob,j);
             v_status_code := get_status_code(v_blob,v_num_intervals,j);
             set_metablob_attributes(v_status_code,
                                     v_binary_number,
                                     v_metablob_total,
                                     v_metablob_max,
                                     v_metablob_min);
         END LOOP;




         UPDATE ghost_vm
            SET metablob_total = v_metablob_total,
                metablob_max = v_metablob_max,
                metablob_min = v_metablob_min,
                last_update_date = SYSDATE
          WHERE uidvm = p_uidvm;


         COMMIT;



    EXCEPTION
         WHEN e_outofbounds_blob_interval THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_SET_INTERVAL_VALUE  Interval position is out of bounds! Max interval pos is: ' || v_num_intervals ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);
         WHEN e_blob_is_null THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_SET_INTERVAL_VALUE  Blob value is NULL!'                             ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);

         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_SET_INTERVAL_VALUE  '                             ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);

  END blob_set_interval_value;



FUNCTION blob_get_status_code(p_uidvm NUMBER,
                              p_pos NUMBER,
                              p_sql_query VARCHAR2) RETURN VARCHAR2 AS
  v_blob BLOB;
  v_remote BOOLEAN;
  v_num_intervals NUMBER;
  e_blob_is_null EXCEPTION;
  e_outofbounds_blob_interval EXCEPTION;

  BEGIN
         v_remote := get_blob(v_blob, p_sql_query, p_uidvm);

         IF(v_blob IS NOT NULL) THEN
           v_num_intervals := get_blob_length(v_blob);
           IF(v_num_intervals<p_pos) THEN
             RAISE e_outofbounds_blob_interval;
           END IF;


           COMMIT;
           RETURN get_status_code(v_blob, v_num_intervals, p_pos);

         ELSE
           RAISE e_blob_is_null;

         END IF;


    EXCEPTION
         WHEN e_outofbounds_blob_interval THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_GET_STATUS_CODE  Interval position is out of bounds! Max interval pos is: ' || v_num_intervals ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);
         WHEN e_blob_is_null THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_GET_STATUS_CODE  Blob value is NULL!'                             ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);

         WHEN OTHERS THEN
             dbms_lob.close(v_blob);
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_GET_STATUS_CODE  '                             ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);

  END blob_get_status_code;



PROCEDURE blob_set_status_code_helper(p_blob IN OUT BLOB,
                                      p_pos NUMBER,
                                      p_value CHAR) AS
  v_num_intervals NUMBER;
  e_blob_is_null EXCEPTION;
  e_outofbounds_blob_interval EXCEPTION;

  BEGIN

         v_num_intervals := get_blob_length(p_blob);
         IF(v_num_intervals<p_pos) THEN
           RAISE e_outofbounds_blob_interval;
         END IF;


         set_metablob_status(p_blob, v_num_intervals, p_pos, p_value);

    EXCEPTION
         WHEN e_outofbounds_blob_interval THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_SET_STATUS_CODE_HELPER  Interval position is out of bounds! Max interval pos is: ' || v_num_intervals ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_SET_STATUS_CODE_HELPER  '                             ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);

  END blob_set_status_code_helper;



PROCEDURE blob_set_status_code(p_uidvm NUMBER,
                               p_pos NUMBER,
                               p_value CHAR,
                               p_sql_query VARCHAR2) AS
  v_blob BLOB;
  v_num_intervals NUMBER;
  e_blob_is_null EXCEPTION;
  e_outofbounds_blob_interval EXCEPTION;

  BEGIN
         v_blob := get_blob_updatable(p_sql_query, p_uidvm);
         blob_set_status_code_helper(v_blob,p_pos,p_value);

    EXCEPTION
         WHEN e_outofbounds_blob_interval THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_SET_STATUS_CODE  Interval position is out of bounds! Max interval pos is: ' || v_num_intervals ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);
         WHEN e_blob_is_null THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_SET_STATUS_CODE  Blob value is NULL!'                             ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);

         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_SET_STATUS_CODE  '                             ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);

  END blob_set_status_code;



PROCEDURE blob_operation_toint(p_uidvm NUMBER,
                               p_pos NUMBER,
                               p_value NUMBER,
                               p_operation NUMBER,
                               p_sql_query VARCHAR2) AS

  v_blob BLOB;
  v_raw_number raw_interval;
  v_num_intervals NUMBER;
  v_pos NUMBER;
  v_status_code CHAR;
  v_binary_number BINARY_DOUBLE;
  v_metablob_total NUMBER;
  v_metablob_max NUMBER;
  v_metablob_min NUMBER;
  v_updated_vm BOOLEAN := false;

  BEGIN

         v_blob := get_blob_updatable(p_sql_query, p_uidvm);

         v_num_intervals := get_blob_length(v_blob);
         IF(v_num_intervals<p_pos) THEN
           RAISE e_outofbounds_blob_interval;
         END IF;


         v_pos := get_metablob_number_position(p_pos);

         dbms_lob.open(v_blob, dbms_lob.lob_readwrite);
         v_raw_number := utl_RAW.REVERSE(utl_raw.cast_from_binary_double(
                         binary_double_operation(get_metablob_interval(v_blob,p_pos),
                                                 p_value,
                                                 p_operation)));
         ghost_util.ghost_write_raw_to_blob(v_blob, v_raw_number, v_pos);

         v_num_intervals := get_blob_length(v_blob);

         v_status_code := get_status_code(v_blob,v_num_intervals,1);
         set_init_metablob_attributes(v_status_code,
                                      get_metablob_interval(v_blob, 1),
                                      v_metablob_total,
                                      v_metablob_max,
                                      v_metablob_min);
         --Update min,max,etc...
         FOR j in 2..v_num_intervals LOOP
             v_binary_number := get_metablob_interval(v_blob,j);
             v_status_code := get_status_code(v_blob,v_num_intervals,j);
             set_metablob_attributes(v_status_code,
                                     v_binary_number,
                                     v_metablob_total,
                                     v_metablob_max,
                                     v_metablob_min);
         END LOOP;


         dbms_lob.close(v_blob);

         UPDATE ghost_vm
            SET blob_value = v_blob,
                metablob_total = v_metablob_total,
                metablob_max = v_metablob_max,
                metablob_min = v_metablob_min,
                last_update_date = SYSDATE
          WHERE uidvm = p_uidvm;


         COMMIT;
    EXCEPTION
         WHEN e_outofbounds_blob_interval THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_OPERATION_TOINT  Interval position is out of bounds! Max interval pos is: ' || v_num_intervals ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos),
                                           SQLERRM);

         WHEN e_blob_is_null THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_OPERATION_TOINT Blob value is Null! '                                ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos)        ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation)  ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value),
                                           SQLERRM);

         WHEN OTHERS THEN
             IF(v_blob IS NOT NULL) THEN
               IF(dbms_lob.isopen(v_blob) <> 0 ) THEN
                   dbms_lob.close(v_blob);
               END IF;

             END IF;

             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                              ||
                                           'BLOB_OPERATION_TOINT '                                ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)    ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos)        ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation)  ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value),
                                           SQLERRM);

  END blob_operation_toint;





  PROCEDURE blob_operation_toallint(p_uidvm NUMBER,
                                    p_value NUMBER,
                                    p_operation NUMBER,
                                    p_sql_query VARCHAR2) AS
  v_blob BLOB;
  v_num_iterations NUMBER;
  v_status_code CHAR;
  v_binary_number BINARY_DOUBLE;
  v_metablob_total NUMBER;
  v_metablob_max NUMBER;
  v_metablob_min NUMBER;
  v_updated_vm BOOLEAN := false;

  BEGIN
         v_blob := get_blob_updatable(p_sql_query, p_uidvm);


         v_num_iterations := get_blob_length(v_blob);

         IF v_num_iterations > 3640 THEN
            RAISE NO_DATA_FOUND;
         END IF;


         dbms_lob.open(v_blob, dbms_lob.lob_readwrite);


         v_binary_number := binary_double_operation(get_metablob_interval(v_blob,1),
                                                    p_value,
                                                    p_operation);

         write_binarydouble_to_metablob(v_blob,1, v_binary_number);
         v_status_code := get_status_code(v_blob,v_num_iterations,1);

         set_init_metablob_attributes(v_status_code,
                                      v_binary_number,
                                      v_metablob_total,
                                      v_metablob_max,
                                      v_metablob_min);




         FOR j in 2..v_num_iterations LOOP
             v_binary_number := binary_double_operation(get_metablob_interval(v_blob,j),
                                                        p_value,
                                                        p_operation);

             write_binarydouble_to_metablob(v_blob,j, v_binary_number);
             v_status_code := get_status_code(v_blob,v_num_iterations,j);
             set_metablob_attributes(v_status_code,
                                     v_binary_number,
                                     v_metablob_total,
                                     v_metablob_max,
                                     v_metablob_min);

         END LOOP;

         dbms_lob.close(v_blob);
         UPDATE ghost_vm
            SET -- blob_value = v_blob,
                metablob_total = v_metablob_total,
                metablob_max = v_metablob_max,
                metablob_min = v_metablob_min,
                last_update_date = SYSDATE
          WHERE uidvm = p_uidvm;


         COMMIT;
    EXCEPTION
         WHEN e_blob_is_null THEN
         ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                 ||
                                           'BLOB_OPERATION_TOALLINT Blob is NULL!'                                               ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value) ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                           SQLERRM);
         WHEN OTHERS THEN
             IF(v_blob IS NOT NULL) THEN
               IF(dbms_lob.isopen(v_blob) <> 0 ) THEN
                   dbms_lob.close(v_blob);
               END IF;

             END IF;

             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                 ||
                                           'BLOB_OPERATION_TOALLINT '                                               ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value) ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                           SQLERRM);

  END blob_operation_toallint;



  PROCEDURE blob_round_allint_helper(p_blob IN OUT BLOB,
                                     p_uidvm IN NUMBER,
                                     p_num_decimals IN INTEGER) AS

    v_num_iterations NUMBER;
    v_status_code CHAR;
    v_binary_number BINARY_DOUBLE;
    v_metablob_total NUMBER;
    v_metablob_max NUMBER;
    v_metablob_min NUMBER;

  BEGIN
         v_num_iterations := get_blob_length(p_blob);

         IF v_num_iterations > 3640 THEN
            RAISE NO_DATA_FOUND;
         END IF;


         dbms_lob.open(p_blob, dbms_lob.lob_readwrite);

         v_binary_number := binary_double_round(get_metablob_interval(p_blob,1), p_num_decimals);

         write_binarydouble_to_metablob(p_blob,1, v_binary_number);
         v_status_code := get_status_code(p_blob,v_num_iterations,1);

         set_init_metablob_attributes(v_status_code,
                                      v_binary_number,
                                      v_metablob_total,
                                      v_metablob_max,
                                      v_metablob_min);


         FOR j in 2..v_num_iterations LOOP
             v_binary_number := binary_double_round(get_metablob_interval(p_blob,j), p_num_decimals);

             write_binarydouble_to_metablob(p_blob,j, v_binary_number);
             v_status_code := get_status_code(p_blob,v_num_iterations,j);
             set_metablob_attributes(v_status_code,
                                     v_binary_number,
                                     v_metablob_total,
                                     v_metablob_max,
                                     v_metablob_min);

         END LOOP;


         dbms_lob.close(p_blob);

         UPDATE ghost_vm
            SET --blob_value = v_blob,
                metablob_total = v_metablob_total,
                metablob_max = v_metablob_max,
                metablob_min = v_metablob_min,
                last_update_date = SYSDATE
          WHERE uidvm = p_uidvm;



         EXCEPTION
           WHEN OTHERS THEN
               IF(p_blob IS NOT NULL) THEN
                 IF(dbms_lob.isopen(p_blob) <> 0 ) THEN
                     dbms_lob.close(p_blob);
                 END IF;

               END IF;

               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG                                 ||
                                             'BLOB_ROUND_TOALLINT_HELPER '                                               ||
                                             ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                             ' p_num_decimals:' || ghost_util.wrap_error_params(p_num_decimals),
                                             SQLERRM);

  END blob_round_allint_helper;



  PROCEDURE blob_round_allint(p_uidvm NUMBER,
                              p_num_decimals INTEGER,
                              p_sql_query VARCHAR2) AS
  v_blob BLOB;

  BEGIN
         v_blob := get_blob_updatable(p_sql_query, p_uidvm);
         blob_round_allint_helper(v_blob,p_uidvm,p_num_decimals);

         COMMIT;
    EXCEPTION
         WHEN e_blob_is_null THEN
         ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                 ||
                                           'BLOB_ROUND_TOALLINT Blob is NULL!'                                               ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                           ' p_num_decimals:' || ghost_util.wrap_error_params(p_num_decimals),
                                           SQLERRM);
         WHEN OTHERS THEN
             IF(v_blob IS NOT NULL) THEN
               IF(dbms_lob.isopen(v_blob) <> 0 ) THEN
                   dbms_lob.close(v_blob);
               END IF;

             END IF;

             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                 ||
                                           'BLOB_ROUND_TOALLINT '                                               ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                           ' p_num_decimals:' || ghost_util.wrap_error_params(p_num_decimals),
                                           SQLERRM);

  END blob_round_allint;



/*
PROCEDURE blob_subtract_toallint(p_uidvm NUMBER,
                                 p_value NUMBER) AS
  v_blob BLOB;
  v_raw_number RAW(8);
  v_num_iterations NUMBER;
  v_pos NUMBER;

  BEGIN
        SELECT blob_value
          INTO  v_blob
          FROM ghost_vm
         WHERE uidvm = p_uidvm FOR UPDATE;

         v_num_iterations := (dbms_lob.getlength(v_blob) - 2)/9;

         IF v_num_iterations > 3640 THEN
            RAISE NO_DATA_FOUND;
         END IF;

         dbms_lob.open(v_blob, dbms_lob.lob_readwrite);
         v_pos := 1;
         FOR j in 1..v_num_iterations LOOP
             v_pos:= (8*(j-1))+1;
             v_raw_number := utl_RAW.REVERSE(utl_raw.cast_from_binary_double(utl_raw.cast_to_binary_double(utl_raw.reverse(dbms_lob.substr(v_blob, 8, v_pos))) - p_value));
             ghost_util.ghost_write_raw_to_blob(v_blob, v_raw_number, v_pos);
         END LOOP;
         dbms_lob.close(v_blob);
         COMMIT;
    EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                           ||
                                           'BLOB_SUBTRACT_TOALLINT  '                           ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value),
                                           SQLERRM);
  END blob_subtract_toallint;


PROCEDURE blob_multiply_toallint(p_uidvm NUMBER,
                                 p_value NUMBER) AS
  v_blob BLOB;
  v_raw_number RAW(8);
  v_num_iterations NUMBER;
  v_pos NUMBER;

  BEGIN
        SELECT blob_value
          INTO  v_blob
          FROM ghost_vm
         WHERE uidvm = p_uidvm FOR UPDATE;

         v_num_iterations := (dbms_lob.getlength(v_blob) - 2)/9;

         IF v_num_iterations > 3640 THEN
            RAISE NO_DATA_FOUND;
         END IF;

         dbms_lob.open(v_blob, dbms_lob.lob_readwrite);
         v_pos := 1;
         FOR j in 1..v_num_iterations LOOP
             v_pos:= (8*(j-1))+1;
             v_raw_number := utl_RAW.REVERSE(utl_raw.cast_from_binary_double(utl_raw.cast_to_binary_double(utl_raw.reverse(dbms_lob.substr(v_blob, 8, v_pos))) * p_value));
             ghost_util.ghost_write_raw_to_blob(v_blob, v_raw_number, v_pos);
         END LOOP;
         dbms_lob.close(v_blob);
         COMMIT;
    EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                           ||
                                           'BLOB_MULTIPLY_TOALLINT  '                           ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value),
                                           SQLERRM);
  END blob_multiply_toallint;


PROCEDURE blob_divide_toallint(p_uidvm NUMBER,
                               p_value NUMBER) AS
  v_blob BLOB;
  v_raw_number RAW(8);
  v_num_iterations NUMBER;
  v_pos NUMBER;

  BEGIN
        SELECT blob_value
          INTO  v_blob
          FROM ghost_vm
         WHERE uidvm = p_uidvm FOR UPDATE;

         v_num_iterations := (dbms_lob.getlength(v_blob) - 2)/9;

         IF v_num_iterations > 3640 THEN
            RAISE NO_DATA_FOUND;
         END IF;

         dbms_lob.open(v_blob, dbms_lob.lob_readwrite);
         v_pos := 1;
         FOR j in 1..v_num_iterations LOOP
             v_pos:= (8*(j-1))+1;
             v_raw_number := utl_RAW.REVERSE(utl_raw.cast_from_binary_double(utl_raw.cast_to_binary_double(utl_raw.reverse(dbms_lob.substr(v_blob, 8, v_pos))) / p_value));
             ghost_util.ghost_write_raw_to_blob(v_blob, v_raw_number, v_pos);
         END LOOP;
         dbms_lob.close(v_blob);
         COMMIT;
    EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                           ||
                                           'BLOB_DIVIDE_TOALLINT  '                           ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                           ' p_value:' || ghost_util.wrap_error_params(p_value),
                                           SQLERRM);
  END blob_divide_toallint;
*/



 FUNCTION build_custom_col_helper(p_value IN VARCHAR2,
                                  p_custom_column_values IN VARCHAR2) RETURN VARCHAR2 AS
    v_temp VARCHAR2(200);

 BEGIN
     IF p_value IS NOT NULL THEN
        v_temp := p_value;
     ELSE
       v_temp := NULL;

     END IF;

     RETURN p_custom_column_values ||v_temp || ',';

     EXCEPTION
            WHEN OTHERS THEN
               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                     ghost_util.CONST_ERROR_MSG                                             ||
                                     'BUILD_CUSTOM_COL_HELPER '                                                ||
                                     ' p_value:' || ghost_util.wrap_error_params(p_value) ||
                                     ' p_custom_column_values:' || ghost_util.wrap_error_params(p_custom_column_values),
                                     SQLERRM);

 END build_custom_col_helper;



 PROCEDURE build_custom_outputs(p_ghost_data ghost_data_obj,
                                p_custom_columns IN VARCHAR2,
                                o_custom_column_values IN OUT VARCHAR2) AS
     v_query VARCHAR2(5000);
     v_cursor INTEGER;
     v_rows NUMBER;
     v_custom_col_array ghost_util.array_of_strings;
     v_count NUMBER;

     v_temp VARCHAR2(200);

 BEGIN
     IF p_custom_columns IS NOT NULL THEN
    -- Yes this is not the most efficent way but I couldn't find a way to do it dynamically because of issues
        v_custom_col_array := ghost_util.array_of_strings();

        v_count := ghost_util.split_params(p_custom_columns,
                                           CONST_CUSTOMCOL_VALUE_SEP,
                                           v_custom_col_array);

        FOR x IN v_custom_col_array.FIRST..v_custom_col_array.LAST LOOP
            CASE v_custom_col_array(x)
                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 1 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_1,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 2 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_2,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 3 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_3,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 4 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_4,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 5 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_5,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 6 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_6,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 7 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_7,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 8 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_8,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 9 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_9,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 10 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_10,o_custom_column_values);
                      
                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 11 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_11,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 12 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_12,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 13 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_13,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 14 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_14,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 15 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_15,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 16 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_16,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 17 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_17,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 18 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_18,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 19 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_19,o_custom_column_values);

                  WHEN CONST_CUSTOM_COLUMN_PREFIX || 20 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_20,o_custom_column_values);

                  WHEN CONST_CUSTOM_DCOLUMN_PREFIX || 1 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_date_1,o_custom_column_values);

                  WHEN CONST_CUSTOM_DCOLUMN_PREFIX || 2 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_date_2,o_custom_column_values);

                  WHEN CONST_CUSTOM_DCOLUMN_PREFIX || 3 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_date_3,o_custom_column_values);

                  WHEN CONST_CUSTOM_DCOLUMN_PREFIX || 4 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_date_4,o_custom_column_values);

                  WHEN CONST_CUSTOM_DCOLUMN_PREFIX || 5 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_date_5,o_custom_column_values);

                  WHEN CONST_CUSTOM_DCOLUMN_PREFIX || 6 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_date_6,o_custom_column_values);

                  WHEN CONST_CUSTOM_DCOLUMN_PREFIX || 7 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_date_7,o_custom_column_values);

                  WHEN CONST_CUSTOM_DCOLUMN_PREFIX || 8 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_date_8,o_custom_column_values);

                  WHEN CONST_CUSTOM_DCOLUMN_PREFIX || 9 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_date_9,o_custom_column_values);

                  WHEN CONST_CUSTOM_DCOLUMN_PREFIX || 10 THEN
                      o_custom_column_values := build_custom_col_helper(p_ghost_data.custom_date_10,o_custom_column_values);

            END CASE;

        END LOOP;

       END IF;


       EXCEPTION
            WHEN OTHERS THEN
               IF v_cursor!=0 THEN
                  dbms_sql.close_cursor(v_cursor);
               END IF;

               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                     ghost_util.CONST_ERROR_MSG                                             ||
                                     'BUILD_CUSTOM_OUTPUTS '                                                ||
                                     ' o_custom_column_values:' || ghost_util.wrap_error_params(o_custom_column_values) ||
                                     ' v_count:' || ghost_util.wrap_error_params(v_count) ||
                                     ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns),
                                     SQLERRM);

 END build_custom_outputs;



 FUNCTION blob_operation_helper(p_uidvm IN NUMBER,
                                p_ghost_data_left IN ghost_data_obj,
                                p_blob_right IN BLOB,
                                p_operation IN NUMBER,
                                p_dest_cid IN NUMBER,
                                p_custom_columns IN VARCHAR2,
                                p_flag_status IN NUMBER,
                                p_flag_spidst IN NUMBER,
                                p_flag_existence IN NUMBER,
                                p_existence_default_value IN NUMBER,
                                p_flag_divide_adj IN NUMBER,
                                p_flag_multiply_adj IN BOOLEAN,
                                o_uidvm_save IN OUT NUMBER) RETURN BLOB AS

        v_blob BLOB;
        v_num_intervals_left NUMBER;
        v_num_intervals_right NUMBER;

        v_num_iterations NUMBER;

        v_pos NUMBER;
        v_status_code_left CHAR;
        v_status_code_right CHAR;
        v_determined_status_code CHAR;

        v_binary_number BINARY_DOUBLE;

        v_metablob_total NUMBER;
        v_metablob_max NUMBER;
        v_metablob_min NUMBER;
        v_interval_count NUMBER;

        v_new_status_codes VARCHAR2(32767); --This could fail if number of intervals is greather then this amount
        v_right_interval_value NUMBER;

        v_ghost_vm_row vm_insert_table;

        v_uidvm_save NUMBER;
        v_custom_column_values VARCHAR2(3000);

  BEGIN
         IF( (p_ghost_data_left.blob_value IS NULL) OR
             ( (p_blob_right IS NULL)  AND (p_flag_existence = CONST_FLAG_EXIST_OPTION1)) ) THEN
            RAISE e_blob_is_null;
         END IF;


         IF (p_flag_existence IN (CONST_FLAG_EXIST_DEFAULT, CONST_FLAG_EXIST_OPTION2) ) AND
            (p_blob_right IS NULL) THEN
             v_num_intervals_left := get_blob_length(p_ghost_data_left.blob_value);
             v_num_iterations := v_num_intervals_left;


             dbms_lob.createtemporary(v_blob,TRUE);
             dbms_lob.open(v_blob, dbms_lob.lob_readwrite);
--             ghost_util.ghost_write_varchar2_to_blob(v_blob,0); -- Bug fix for Oracle!

             v_pos := 1;

             v_status_code_left := get_status_code(p_ghost_data_left.blob_value,v_num_intervals_left,v_pos);

             IF CONST_FLAG_STATUS_OPTION1 = p_flag_status THEN
                IF (v_status_code_left = CONST_BLOB_EMPTY_STATUS_CODE) THEN
                   RAISE e_blob_status_missing_code;
                END IF;

             END IF;


             v_determined_status_code := v_status_code_left;
             v_new_status_codes := v_new_status_codes || v_determined_status_code;
             v_right_interval_value := p_existence_default_value;

             IF p_flag_multiply_adj AND (v_right_interval_value = 0 ) THEN
                v_binary_number := get_metablob_interval(p_ghost_data_left.blob_value, v_pos);
             ELSE
               IF (p_flag_divide_adj = CONST_FLAG_DIVIDE_DEFAULT) AND
                  (v_right_interval_value = 0) THEN
                    v_binary_number := 0;
                ELSE
                  IF (p_flag_divide_adj = CONST_FLAG_DIVIDE_OPTION1) AND
                     (v_right_interval_value = 0) THEN
                      RAISE e_blob_divide_byzero;
                  ELSE
                      v_binary_number := binary_double_operation(get_metablob_interval(p_ghost_data_left.blob_value, v_pos),
                                                                 v_right_interval_value,
                                                                 p_operation);

                  END IF;


               END IF;

             END IF;


           set_init_metablob_attributes(v_determined_status_code,
                                        v_binary_number,
                                        v_metablob_total,
                                        v_metablob_max,
                                        v_metablob_min);


           write_binarydouble_to_metablob(v_blob,v_pos,v_binary_number);

           FOR j in 2..v_num_iterations LOOP

           IF p_flag_multiply_adj AND (v_right_interval_value = 0 )  THEN
              v_binary_number := get_metablob_interval(v_blob, j);
           ELSE
             IF (p_flag_divide_adj = CONST_FLAG_DIVIDE_DEFAULT) AND
                (v_right_interval_value = 0) THEN
                  v_binary_number := 0;
              ELSE
                IF (p_flag_divide_adj = CONST_FLAG_DIVIDE_OPTION1) AND
                   (v_right_interval_value = 0) THEN
                    RAISE e_blob_divide_byzero;
                ELSE
                    v_binary_number := binary_double_operation(get_metablob_interval(p_ghost_data_left.blob_value, j),
                                                               v_right_interval_value,
                                                               p_operation);

                END IF;


             END IF;

           END IF;



           write_binarydouble_to_metablob(v_blob,j,v_binary_number);

           v_status_code_left := get_status_code(p_ghost_data_left.blob_value,v_num_intervals_left,j);

           IF CONST_FLAG_STATUS_OPTION1 = p_flag_status THEN
              IF (v_status_code_left = CONST_BLOB_EMPTY_STATUS_CODE) THEN
                 RAISE e_blob_status_missing_code;
              END IF;

           END IF;


           v_determined_status_code := v_status_code_left;
           v_new_status_codes := v_new_status_codes || v_determined_status_code;
           set_metablob_attributes(v_determined_status_code,
                                   v_binary_number,
                                   v_metablob_total,
                                   v_metablob_max,
                                   v_metablob_min);


           END LOOP;


           ghost_util.ghost_write_raw_to_blob(v_blob, CONST_INTERVAL_BLOB_MARKER_RAW, (CONST_INTERVAL_BYTE_SIZE*(v_num_iterations))+1);
           ghost_util.ghost_write_varchar2_to_blob(v_blob, v_new_status_codes);

--           dbms_lob.trim(v_blob,get_blob_byte_length(v_num_iterations));
           v_interval_count := get_blob_length(v_blob);
           dbms_lob.close(v_blob);
         ELSE

           v_num_intervals_left := get_blob_length(p_ghost_data_left.blob_value);
           v_num_intervals_right := get_blob_length(p_blob_right);

--           IF v_num_intervals_left != v_num_intervals_right THEN
--             RAISE e_blob_intervalcount_mismatch;
--           END IF;


           v_num_iterations := v_num_intervals_left;

           IF v_num_iterations > v_num_intervals_right THEN
              v_num_iterations := v_num_intervals_right;
           END IF;


           dbms_lob.createtemporary(v_blob,TRUE);
           dbms_lob.open(v_blob, dbms_lob.lob_readwrite);

--           ghost_util.ghost_write_varchar2_to_blob(v_blob,0); -- Bug fix for Oracle!

           v_pos := 1;

           v_status_code_left := get_status_code(p_ghost_data_left.blob_value,v_num_intervals_left,v_pos);
           v_status_code_right := get_status_code(p_blob_right,v_num_intervals_right,v_pos);

  --         dbms_output.put_line('left:' || v_status_code_left);
  --         dbms_output.put_line('right:' || v_status_code_right);

           IF CONST_FLAG_STATUS_OPTION1 = p_flag_status THEN
              IF (v_status_code_left = CONST_BLOB_EMPTY_STATUS_CODE) OR
                 (v_status_code_right = CONST_BLOB_EMPTY_STATUS_CODE) THEN
                 RAISE e_blob_status_missing_code;
              END IF;

           END IF;


           IF CONST_FLAG_STATUS_OPTION2 = p_flag_status THEN
              IF ((v_status_code_left = CONST_BLOB_EMPTY_STATUS_CODE) AND
                  (v_status_code_right = CONST_BLOB_GOOD_STATUS_CODE))     OR
                  ((v_status_code_left = CONST_BLOB_GOOD_STATUS_CODE) AND
                  (v_status_code_right = CONST_BLOB_EMPTY_STATUS_CODE))    OR
                  ((v_status_code_left = CONST_BLOB_MIX_STATUS_CODE) AND
                  (v_status_code_right = CONST_BLOB_MIX_STATUS_CODE))         THEN
                  v_determined_status_code := CONST_BLOB_MIX_STATUS_CODE;
              ELSE
                v_determined_status_code := v_status_code_left;

              END IF;

           ELSE
                v_determined_status_code := v_status_code_left;

           END IF;


           v_new_status_codes := v_new_status_codes || v_determined_status_code;
--           dbms_output.put_line('<' || v_new_status_codes || '>');

           v_right_interval_value := get_metablob_interval(p_blob_right, v_pos);

           IF p_flag_multiply_adj AND (v_right_interval_value = 0 ) THEN
              v_binary_number := get_metablob_interval(p_ghost_data_left.blob_value, v_pos);
           ELSE
             IF (p_flag_divide_adj = CONST_FLAG_DIVIDE_DEFAULT) AND
                (v_right_interval_value = 0) THEN
                  v_binary_number := 0;
              ELSE
                IF (p_flag_divide_adj = CONST_FLAG_DIVIDE_OPTION1) AND
                   (v_right_interval_value = 0) THEN
                    RAISE e_blob_divide_byzero;
                ELSE
                    v_binary_number := binary_double_operation(get_metablob_interval(p_ghost_data_left.blob_value, v_pos),
                                                               v_right_interval_value,
                                                               p_operation);

                END IF;


             END IF;

           END IF;


           set_init_metablob_attributes(v_determined_status_code,
                                        v_binary_number,
                                        v_metablob_total,
                                        v_metablob_max,
                                        v_metablob_min);



           write_binarydouble_to_metablob(v_blob,v_pos,v_binary_number);

--           v_new_status_codes := v_new_status_codes || CONST_BLOB_GOOD_STATUS_CODE;

  --         dbms_output.put_line(v_new_status_codes);

           FOR j in 2..v_num_iterations LOOP

             v_right_interval_value := get_metablob_interval(p_blob_right, j);

             IF p_flag_multiply_adj AND (v_right_interval_value = 0 )  THEN
                v_binary_number := get_metablob_interval(p_ghost_data_left.blob_value, j);
             ELSE
               IF (p_flag_divide_adj = CONST_FLAG_DIVIDE_DEFAULT) AND
                  (v_right_interval_value = 0) THEN
                    v_binary_number := 0;
                ELSE
                  IF (p_flag_divide_adj = CONST_FLAG_DIVIDE_OPTION1) AND
                     (v_right_interval_value = 0) THEN
                      RAISE e_blob_divide_byzero;
                  ELSE
                      v_binary_number := binary_double_operation(get_metablob_interval(p_ghost_data_left.blob_value, j),
                                                                 v_right_interval_value,
                                                                 p_operation);

                  END IF;


               END IF;

             END IF;



               write_binarydouble_to_metablob(v_blob,j,v_binary_number);

               v_status_code_left := get_status_code(p_ghost_data_left.blob_value,v_num_intervals_left,j);
               v_status_code_right := get_status_code(p_blob_right,v_num_intervals_right,j);

  --             dbms_output.put_line('left:' || v_status_code_left);
  --             dbms_output.put_line('right:' || v_status_code_right);

               IF CONST_FLAG_STATUS_OPTION1 = p_flag_status THEN
                  IF (v_status_code_left = CONST_BLOB_EMPTY_STATUS_CODE) OR
                     (v_status_code_right = CONST_BLOB_EMPTY_STATUS_CODE) THEN
                     RAISE e_blob_status_missing_code;
                  END IF;

               END IF;


               IF CONST_FLAG_STATUS_OPTION2 = p_flag_status THEN
                  IF ((v_status_code_left = CONST_BLOB_EMPTY_STATUS_CODE) AND
                      (v_status_code_right = CONST_BLOB_GOOD_STATUS_CODE))     OR
                      ((v_status_code_left = CONST_BLOB_GOOD_STATUS_CODE) AND
                      (v_status_code_right = CONST_BLOB_EMPTY_STATUS_CODE))    OR
                      ((v_status_code_left = CONST_BLOB_MIX_STATUS_CODE) AND
                      (v_status_code_right = CONST_BLOB_MIX_STATUS_CODE))         THEN
                      v_determined_status_code := CONST_BLOB_MIX_STATUS_CODE;
                  ELSE
                      v_determined_status_code := v_status_code_left;

                  END IF;

              ELSE
                  v_determined_status_code := v_status_code_left;

              END IF;


              v_new_status_codes := v_new_status_codes || v_determined_status_code;
--             dbms_output.put_line(j||'<' || v_new_status_codes || '>');
             set_metablob_attributes(v_determined_status_code,
                                     v_binary_number,
                                     v_metablob_total,
                                     v_metablob_max,
                                     v_metablob_min);


           END LOOP;

--dbms_output.put_line('< DONE >');
  --         v_pos:= (CONST_INTERVAL_BYTE_SIZE*(v_num_iterations))+1;
  --         dbms_output.put_line(v_pos);
  --         dbms_output.put_line(v_num_iterations);

  --         ghost_util.ghost_write_raw_to_blob(v_blob, dbms_lob.substr(p_blob_left, CONST_INTERVAL_BYTE_SIZE + (v_num_iterations +1),v_pos), v_pos);

  --         ghost_util.ghost_write_raw_to_blob(v_blob, dbms_lob.substr(p_blob_left, get_status_code_pos(v_num_iterations,v_num_iterations),v_pos), v_pos);

  --         dbms_output.put_line(v_new_status_codes);
  --         dbms_output.put_line(get_status_code_pos(v_num_iterations,1));

           ghost_util.ghost_write_raw_to_blob(v_blob, CONST_INTERVAL_BLOB_MARKER_RAW, get_metablob_number_position(v_num_iterations+1));
           ghost_util.ghost_write_varchar2_to_blob(v_blob,v_new_status_codes,get_status_code_pos(v_num_iterations,1));

--           dbms_lob.trim(v_blob,get_blob_byte_length(v_num_iterations));
           v_interval_count :=  get_blob_length(v_blob);

           dbms_lob.close(v_blob);

         END IF;


         v_uidvm_save := p_uidvm;

         IF p_dest_cid IS NOT NULL THEN

            v_uidvm_save := Seq_GHOSTUIDVM.NEXTVAL;

            v_ghost_vm_row.uidvm := v_uidvm_save;
            v_ghost_vm_row.blob_value := v_blob;
--            v_ghost_vm_row.ghost_transaction_id := p_transaction_id;
            v_ghost_vm_row.ghost_collection_id := p_dest_cid;
            v_ghost_vm_row.meta_starttime := p_ghost_data_left.starttime;
            v_ghost_vm_row.meta_stoptime := p_ghost_data_left.stoptime;
            v_ghost_vm_row.metablob_spi := 86400/v_interval_count;
            v_ghost_vm_row.metablob_dst_participant := p_ghost_data_left.dst_participant;
            v_ghost_vm_row.metablob_total := v_metablob_total;
            v_ghost_vm_row.metablob_max := v_metablob_max;
            v_ghost_vm_row.metablob_min := v_metablob_min;
            v_ghost_vm_row.metablob_intervalcount := v_interval_count;

            build_custom_outputs(p_ghost_data_left,
                                 p_custom_columns,
                                 v_custom_column_values);

            insert_into_ghost_vm(CONST_BLOB_INSERT_COLUMN,
                                 p_custom_columns,
                                 v_custom_column_values,
                                 v_ghost_vm_row);

         ELSE
            UPDATE ghost_vm
              SET blob_value = v_blob,
                  metablob_total = v_metablob_total,
                  metablob_max = v_metablob_max,
                  metablob_min = v_metablob_min,
                  metablob_intervalcount = v_interval_count,
                  meta_starttime = p_ghost_data_left.starttime,
                  meta_stoptime = p_ghost_data_left.stoptime,
                  metablob_spi = p_ghost_data_left.spi,
                  metablob_dst_participant = p_ghost_data_left.dst_participant,
                  last_update_date = SYSDATE
            WHERE uidvm = v_uidvm_save;

         END IF;


         o_uidvm_save := v_uidvm_save;
         RETURN v_blob;

         EXCEPTION

           WHEN e_blob_is_null THEN
            IF(v_blob IS NOT NULL) THEN
               IF(dbms_lob.isopen(v_blob) <> 0 ) THEN
                   dbms_lob.close(v_blob);
               END IF;

             END IF;

            ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                       ||
                                           'BLOB_OPERATION_HELPER:  Left or Right Blob value is Null! '            ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                           ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                           SQLERRM);


           WHEN e_blob_exist_missing THEN
              IF(v_blob IS NOT NULL) THEN
                 IF(dbms_lob.isopen(v_blob) <> 0 ) THEN
                     dbms_lob.close(v_blob);
                 END IF;

               END IF;

              ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG                                       ||
                                             'BLOB_OPERATION_HELPER: Right Blob doesn''t exist!! '                                 ||
                                             ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                             ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                             ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                             SQLERRM);

           WHEN e_blob_intervalcount_mismatch THEN
              IF(v_blob IS NOT NULL) THEN
                 IF(dbms_lob.isopen(v_blob) <> 0 ) THEN
                     dbms_lob.close(v_blob);
                 END IF;

               END IF;

               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG                                       ||
                                             'BLOB_OPERATION_HELPER: The number of intervals don''t match! '   ||
                                             ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                             ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                             ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                             SQLERRM);

           WHEN e_blob_status_missing_code THEN
               IF(v_blob IS NOT NULL) THEN
                 IF(dbms_lob.isopen(v_blob) <> 0 ) THEN
                     dbms_lob.close(v_blob);
                 END IF;

               END IF;

               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG                                       ||
                                             'BLOB_OPERATION_HELPER: Status code <' || CONST_BLOB_EMPTY_STATUS_CODE || '>, a missing status code was encountered! '   ||
                                             ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                             ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                             ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                             SQLERRM);

           WHEN e_blob_divide_byzero THEN
               IF(v_blob IS NOT NULL) THEN
                 IF(dbms_lob.isopen(v_blob) <> 0 ) THEN
                     dbms_lob.close(v_blob);
                 END IF;

               END IF;

               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG                                       ||
                                             'BLOB_OPERATION_HELPER: Division by zero! '                      ||
                                             ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                             ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                             ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                             SQLERRM);

           WHEN OTHERS THEN
               IF(v_blob IS NOT NULL) THEN
                 IF(dbms_lob.isopen(v_blob) <> 0 ) THEN
                     dbms_lob.close(v_blob);
                 END IF;

               END IF;

               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG                           ||
                                             'BLOB_OPERATION_HELPER '                             ||
                                             ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)         ||
                                             ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                             ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                             SQLERRM);

  END blob_operation_helper;



FUNCTION blob_operation(p_ghost_data_left IN ghost_data_obj,
                        p_ghost_data_right IN ghost_data_obj,
                        p_uidvm_save IN NUMBER,
                        p_operation IN NUMBER,
                        p_dest_cid IN NUMBER,
                        p_custom_columns IN VARCHAR2,
                        p_existence_default_value IN NUMBER,
                        p_op_flag_obj ghost_operation_flag_obj,
                        o_uidvm_save OUT NUMBER
                        ) RETURN BLOB AS

  v_flag_exist_default_value NUMBER;

  v_select_blob_query_local VARCHAR2(200);
  v_select_blob_query_ext VARCHAR2(200);
  v_flag_divide_adj NUMBER;
  v_flag_multiply_adj BOOLEAN := FALSE;

  BEGIN

         IF p_op_flag_obj.flag_time = CONST_FLAG_TIME_OPTION1 THEN
            IF (p_ghost_data_left.starttime != p_ghost_data_right.starttime) OR
               (p_ghost_data_left.stoptime != p_ghost_data_right.stoptime) OR
               (p_ghost_data_left.intervalcount != p_ghost_data_right.intervalcount) THEN
                RAISE e_blob_time_mismatch;
            END IF;

         END IF;


         IF p_op_flag_obj.flag_spidst = CONST_FLAG_SPIDST_OPTION1 THEN
            IF (p_ghost_data_left.spi != p_ghost_data_right.spi) OR
               (p_ghost_data_left.dst_participant != p_ghost_data_right.dst_participant) THEN
               RAISE e_blob_spidst_mismatch;
            END IF;

         END IF;


         IF (p_operation = CONSTANT_BINARY_OP_DIVIDE) THEN
             v_flag_divide_adj := p_op_flag_obj.flag_divide;
         ELSE
             v_flag_divide_adj := -1;

         END IF;


         IF (p_operation = CONSTANT_BINARY_OP_MULTIPLY) AND
            (p_op_flag_obj.flag_multiply = CONST_FLAG_MULTIPLY_OPTION1) THEN
             v_flag_multiply_adj := TRUE;
         END IF;




         RETURN  blob_operation_helper(p_uidvm_save,
                                       p_ghost_data_left,
                                       p_ghost_data_right.blob_value,
                                       p_operation,
                                       p_dest_cid,
                                       p_custom_columns,
                                       p_op_flag_obj.flag_status,
                                       p_op_flag_obj.flag_spidst,
                                       p_op_flag_obj.flag_existence,
                                       p_existence_default_value,
                                       v_flag_divide_adj,
                                       v_flag_multiply_adj,
                                       o_uidvm_save);

     EXCEPTION
         WHEN e_blob_time_mismatch THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                       ||
                                           'BLOB_OPERATION : The stoptime, starttime or interval count don''t match! '   ||
                                           ' p_uidvm_save:' || ghost_util.wrap_error_params(p_uidvm_save) ||
                                           ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                           SQLERRM);

         WHEN e_blob_spidst_mismatch THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                       ||
                                           'BLOB_OPERATION : The SPI or DST Paricipant don''t match! '   ||
                                           ' p_uidvm_save:' || ghost_util.wrap_error_params(p_uidvm_save) ||
                                           ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                           SQLERRM);

         WHEN e_unknown_flag_option THEN
            ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                       ||
                                           'BLOB_OPERATION: A flag option was passed that isn''t recognized! '          ||
                                           ' p_uidvm_save:' || ghost_util.wrap_error_params(p_uidvm_save) ||
                                           ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                           SQLERRM);

         WHEN OTHERS THEN
         /*
            IF(p_blob_left IS NOT NULL) THEN
               IF(dbms_lob.isopen(v_blob_left) <> 0 ) THEN
                   dbms_lob.close(v_blob_left);
               END IF;
             END IF;
             */

             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                       ||
                                           'BLOB_OPERATION '                                               ||
                                           ' p_uidvm_save:' || ghost_util.wrap_error_params(p_uidvm_save) ||
                                           ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                           SQLERRM);

  END blob_operation;



PROCEDURE blob_operation_uidvm(p_uidvm_left IN NUMBER,
                               p_uidvm_right IN NUMBER,
                               p_uidvm_save IN NUMBER,
                               p_operation IN NUMBER,
                               p_sql_query_left VARCHAR2,
                               p_sql_query_right VARCHAR2,
                               p_flag_time IN NUMBER,
                               p_flag_status IN NUMBER,
                               p_flag_spidst IN NUMBER,
                               p_flag_attributes IN NUMBER,
                               p_flag_divide IN NUMBER,
                               p_flag_multiply IN NUMBER,
                               p_flag_existence IN NUMBER,
                               p_existence_default_value IN NUMBER
                               ) AS

  v_blob_left BLOB;
  v_blob_right BLOB;

  v_starttime_left DATE;
  v_stoptime_left DATE;
  v_spi_left NUMBER;
  v_intervalcount_left NUMBER;
  v_dst_participant_left CHAR;

  v_starttime_right DATE;
  v_stoptime_right DATE;
  v_spi_right NUMBER;
  v_intervalcount_right NUMBER;
  v_dst_participant_right CHAR;

  v_flag_exist_default_value NUMBER;

  v_select_blob_query_local VARCHAR2(200);
  v_select_blob_query_ext VARCHAR2(200);
  v_flag_divide_adj NUMBER;
  v_flag_multiply_adj BOOLEAN := FALSE;

  v_uidvm_save NUMBER;

  v_ghost_data_left ghost_data_obj;
  v_ghost_data_right ghost_data_obj;
  v_op_flag_obj ghost_operation_flag_obj;
  v_overwrite BOOLEAN := false;
  v_remote BOOLEAN;
  v_temp BLOB;

  BEGIN
        v_op_flag_obj := ghost_operation_flag_obj();
        v_ghost_data_left := ghost_data_obj();
        v_ghost_data_right := ghost_data_obj();
        /*
        IF p_uidvm_left = p_uidvm_save THEN
           v_overwrite := true;
        END IF;
*/

/*
        v_select_blob_query_local := 'SELECT blob_value, meta_starttime, meta_stoptime, metablob_spi, metablob_intervalcount, metablob_dst_participant' ||chr(10)||
                                     ' FROM ghost_vm WHERE uidvm = :p_uidvm';

        v_select_blob_query_ext := 'SELECT meta_starttime, meta_stoptime, metablob_spi, metablob_intervalcount, metablob_dst_participant' ||chr(10)||
                                   ' FROM ghost_vm WHERE uidvm = :p_uidvm';
*/

--        v_select_blob_query_local := 'SELECT blob_value FROM ghost_vm WHERE uidvm = :p_uidvm';
        v_select_blob_query_ext := 'SELECT meta_starttime, meta_stoptime, metablob_spi, metablob_intervalcount, metablob_dst_participant' ||chr(10)||
                                   ' FROM ghost_vm WHERE uidvm = :p_uidvm';

        v_remote := get_blob(v_blob_left, p_sql_query_left, p_uidvm_left);
        v_remote := get_blob(v_blob_right, p_sql_query_right, p_uidvm_right);

        EXECUTE IMMEDIATE v_select_blob_query_ext INTO v_starttime_left,
                                                       v_stoptime_left,
                                                       v_spi_left,
                                                       v_intervalcount_left,
                                                       v_dst_participant_left USING p_uidvm_left;

        EXECUTE IMMEDIATE v_select_blob_query_ext INTO v_starttime_right,
                                                       v_stoptime_right,
                                                       v_spi_right,
                                                       v_intervalcount_right,
                                                       v_dst_participant_right USING p_uidvm_right;

/*
        IF p_sql_query_left IS NULL THEN
            EXECUTE IMMEDIATE v_select_blob_query_ext INTO
                                                       v_starttime_left,
                                                       v_stoptime_left,
                                                       v_spi_left,
                                                       v_intervalcount_left,
                                                       v_dst_participant_left USING p_uidvm_left;
         ELSE
            EXECUTE IMMEDIATE p_sql_query_left INTO v_blob_left USING p_uidvm_left;

            EXECUTE IMMEDIATE v_select_blob_query_ext INTO v_starttime_left,
                                                           v_stoptime_left,
                                                           v_spi_left,
                                                           v_intervalcount_left,
                                                           v_dst_participant_left USING p_uidvm_left;
         END IF;

         IF p_sql_query_right IS NULL THEN
            EXECUTE IMMEDIATE v_select_blob_query_local INTO v_blob_right,
                                                       v_starttime_right,
                                                       v_stoptime_right,
                                                       v_spi_right,
                                                       v_intervalcount_right,
                                                       v_dst_participant_right USING p_uidvm_right;
         ELSE
            EXECUTE IMMEDIATE p_sql_query_right INTO v_blob_right USING p_uidvm_right;

            EXECUTE IMMEDIATE v_select_blob_query_ext INTO v_starttime_right,
                                                           v_stoptime_right,
                                                           v_spi_right,
                                                           v_intervalcount_right,
                                                           v_dst_participant_right USING p_uidvm_right;
         END IF;
*/


        v_ghost_data_left.blob_value := v_blob_left;
        v_ghost_data_left.starttime := v_starttime_left;
        v_ghost_data_left.stoptime := v_stoptime_left;
        v_ghost_data_left.spi := v_spi_left;
        v_ghost_data_left.intervalcount := v_intervalcount_left;
        v_ghost_data_left.dst_participant := v_dst_participant_left;

        v_ghost_data_right.blob_value := v_blob_right;
        v_ghost_data_right.starttime := v_starttime_right;
        v_ghost_data_right.stoptime := v_stoptime_right;
        v_ghost_data_right.spi := v_spi_right;
        v_ghost_data_right.intervalcount := v_intervalcount_right;
        v_ghost_data_right.dst_participant := v_dst_participant_right;

        v_op_flag_obj.flag_time := p_flag_time;
        v_op_flag_obj.flag_status := p_flag_status;
        v_op_flag_obj.flag_spidst := p_flag_spidst;
        v_op_flag_obj.flag_attributes := p_flag_attributes;
        v_op_flag_obj.flag_divide := p_flag_divide;
        v_op_flag_obj.flag_multiply := p_flag_multiply;
        v_op_flag_obj.flag_existence := p_flag_existence;

        v_temp := blob_operation(v_ghost_data_left,
                                       v_ghost_data_right,
                                       p_uidvm_save,
                                       p_operation,
                                       NULL,
                                       NULL,
                                       p_existence_default_value,
                                       v_op_flag_obj,
                                       v_uidvm_save);
        EXCEPTION
          WHEN OTHERS THEN
               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG                                       ||
                                             'BLOB_OPERATION_UIDVM '                                               ||
                                             ' p_uidvm_left:' || ghost_util.wrap_error_params(p_uidvm_left) ||
                                             ' p_uidvm_right:' || ghost_util.wrap_error_params(p_uidvm_right) ||
                                             ' p_uidvm_save:' || ghost_util.wrap_error_params(p_uidvm_save) ||
                                             ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                             SQLERRM);

  END blob_operation_uidvm;



FUNCTION blob_operation_pair_helper(p_data_left IN ghost_bulk_data_obj,
                                    p_data_right IN ghost_bulk_data_obj,
                                    p_operation IN NUMBER,
                                    p_dest_cid IN NUMBER,
                                    p_custom_columns IN VARCHAR2,
                                    p_flag_time IN NUMBER,
                                    p_flag_status IN NUMBER,
                                    p_flag_spidst IN NUMBER,
                                    p_flag_attributes IN NUMBER,
                                    p_flag_divide IN NUMBER,
                                    p_flag_multiply IN NUMBER,
                                    p_flag_existence IN NUMBER,
                                    p_existence_default_value IN NUMBER) RETURN NUMBER AS

  v_raw_number raw_interval;

  v_num_intervals_left NUMBER;
  v_num_intervals_right NUMBER;

  v_num_iterations NUMBER;

  v_pos NUMBER;
  v_pos_statuscode NUMBER;
  v_status_code CHAR;

  v_binary_number BINARY_DOUBLE;

  v_uidvm_save NUMBER;

  v_cursor INTEGER;

  v_ghost_data_left ghost_data_obj;
  v_ghost_data_right ghost_data_obj;
  v_custom_columns VARCHAR2(500);

  v_op_flag_obj ghost_operation_flag_obj;

  v_overwrite BOOLEAN := false;
  v_result_blob BLOB;
  v_last_uidvm NUMBER;

  BEGIN

     v_op_flag_obj := ghost_operation_flag_obj();
     v_ghost_data_left := ghost_data_obj();
     v_ghost_data_right := ghost_data_obj();

     IF p_data_left.blob_value.COUNT = 0 THEN
            RAISE e_no_records_match;
     END IF;


     v_custom_columns := ALL_CUSTOM_COLUMNS;

     IF p_flag_attributes = CONST_FLAG_ATTRIBUTES_DEFAULT THEN
        v_custom_columns := p_custom_columns;
     END IF;


     FOR x IN p_data_left.blob_value.FIRST..p_data_left.blob_value.LAST LOOP
       v_ghost_data_left.blob_value := p_data_left.blob_value(x);
       v_ghost_data_left.uidvm := p_data_left.uidvm(x);
       v_ghost_data_left.starttime := p_data_left.starttime(x);
       v_ghost_data_left.stoptime := p_data_left.stoptime(x);
       v_ghost_data_left.spi := p_data_left.spi(x);
       v_ghost_data_left.intervalcount := p_data_left.intervalcount(x);
       v_ghost_data_left.dst_participant := p_data_left.dst_participant(x);

       v_ghost_data_left.custom_1 := p_data_left.custom_1(x);
       v_ghost_data_left.custom_2 := p_data_left.custom_2(x);
       v_ghost_data_left.custom_3 := p_data_left.custom_3(x);
       v_ghost_data_left.custom_4 := p_data_left.custom_4(x);
       v_ghost_data_left.custom_5 := p_data_left.custom_5(x);
       v_ghost_data_left.custom_6 := p_data_left.custom_6(x);
       v_ghost_data_left.custom_7 := p_data_left.custom_7(x);
       v_ghost_data_left.custom_8 := p_data_left.custom_8(x);
       v_ghost_data_left.custom_9 := p_data_left.custom_9(x);
       v_ghost_data_left.custom_10 := p_data_left.custom_10(x);

       v_ghost_data_left.custom_date_1 := p_data_left.custom_date_1(x);
       v_ghost_data_left.custom_date_2 := p_data_left.custom_date_2(x);
       v_ghost_data_left.custom_date_3 := p_data_left.custom_date_3(x);
       v_ghost_data_left.custom_date_4 := p_data_left.custom_date_4(x);
       v_ghost_data_left.custom_date_5 := p_data_left.custom_date_5(x);
       v_ghost_data_left.custom_date_6 := p_data_left.custom_date_6(x);
       v_ghost_data_left.custom_date_7 := p_data_left.custom_date_7(x);
       v_ghost_data_left.custom_date_8 := p_data_left.custom_date_8(x);
       v_ghost_data_left.custom_date_9 := p_data_left.custom_date_9(x);
       v_ghost_data_left.custom_date_10 := p_data_left.custom_date_10(x);


       v_ghost_data_right.blob_value := p_data_right.blob_value(x);
       v_ghost_data_right.uidvm := p_data_right.uidvm(x);
       v_ghost_data_right.starttime := p_data_right.starttime(x);
       v_ghost_data_right.stoptime := p_data_right.stoptime(x);
       v_ghost_data_right.spi := p_data_right.spi(x);
       v_ghost_data_right.intervalcount := p_data_right.intervalcount(x);
       v_ghost_data_right.dst_participant := p_data_right.dst_participant(x);
       v_ghost_data_right.custom_1 := p_data_right.custom_1(x);
       v_ghost_data_right.custom_2 := p_data_right.custom_2(x);
       v_ghost_data_right.custom_3 := p_data_right.custom_3(x);
       v_ghost_data_right.custom_4 := p_data_right.custom_4(x);
       v_ghost_data_right.custom_5 := p_data_right.custom_5(x);
       v_ghost_data_right.custom_6 := p_data_right.custom_6(x);
       v_ghost_data_right.custom_7 := p_data_right.custom_7(x);
       v_ghost_data_right.custom_8 := p_data_right.custom_8(x);
       v_ghost_data_right.custom_9 := p_data_right.custom_9(x);
       v_ghost_data_right.custom_10 := p_data_right.custom_10(x);
       v_ghost_data_right.custom_date_1 := p_data_right.custom_date_1(x);
       v_ghost_data_right.custom_date_2 := p_data_right.custom_date_2(x);
       v_ghost_data_right.custom_date_3 := p_data_right.custom_date_3(x);
       v_ghost_data_right.custom_date_4 := p_data_right.custom_date_4(x);
       v_ghost_data_right.custom_date_5 := p_data_right.custom_date_5(x);
       v_ghost_data_right.custom_date_6 := p_data_right.custom_date_6(x);
       v_ghost_data_right.custom_date_7 := p_data_right.custom_date_7(x);
       v_ghost_data_right.custom_date_8 := p_data_right.custom_date_8(x);
       v_ghost_data_right.custom_date_9 := p_data_right.custom_date_9(x);
       v_ghost_data_right.custom_date_10 := p_data_right.custom_date_10(x);


       v_op_flag_obj.flag_time := p_flag_time;
       v_op_flag_obj.flag_status := p_flag_status;
       v_op_flag_obj.flag_spidst := p_flag_spidst;
       v_op_flag_obj.flag_attributes := p_flag_attributes;
       v_op_flag_obj.flag_divide := p_flag_divide;
       v_op_flag_obj.flag_multiply := p_flag_multiply;
       v_op_flag_obj.flag_existence := p_flag_existence;

/*
       IF p_dest_cid IS NOT NULL THEN
          v_overwrite := TRUE;
       END IF;
       */


       --IF dbms_lob.compare( v_result_blob, v_ghost_data_left.blob_value ) = 0 THEN
       IF v_last_uidvm = v_ghost_data_left.uidvm THEN
          v_ghost_data_left.blob_value := v_result_blob;
       ELSE
         IF v_result_blob IS NOT NULL THEN
            dbms_lob.freeTemporary(v_result_blob);
         END IF;

       END IF;


       v_result_blob := blob_operation(v_ghost_data_left,
                                      v_ghost_data_right,
                                      v_ghost_data_left.uidvm,
                                      p_operation,
                                      p_dest_cid,
                                      v_custom_columns,
                                      p_existence_default_value,
                                      v_op_flag_obj,
                                      v_uidvm_save);
       v_last_uidvm:= v_ghost_data_left.uidvm  ;

       END LOOP;


       RETURN p_data_left.blob_value.COUNT;
     EXCEPTION
         WHEN e_no_records_match THEN
/*
            IF cur_header%ISOPEN THEN
                CLOSE cur_header;
             END IF;
             IF v_cursor!=0 THEN
                  dbms_sql.close_cursor(v_cursor);
             END IF;
             */

             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                   ||
                                           'BLOB_OPERATION_PAIR_HELPER: No records matched! '           ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation) ||
                                           ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                           ' p_dest_cid:' || ghost_util.wrap_error_params(p_dest_cid),
                                           SQLERRM);
         WHEN OTHERS THEN
             --Need to close open blobs in same transaction!
             /*
             IF cur_header%ISOPEN THEN
                CLOSE cur_header;
             END IF;
             IF v_cursor!=0 THEN
                  dbms_sql.close_cursor(v_cursor);
             END IF;
             */

             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                   ||
                                           'BLOB_OPERATION_PAIR_HELPER  '                               ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation) ||
                                           ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                           ' p_dest_cid:' || ghost_util.wrap_error_params(p_dest_cid),
                                           SQLERRM);

  END blob_operation_pair_helper;



FUNCTION blob_operation_pair(p_sql_query IN VARCHAR2,
                             p_operation IN NUMBER,
                             p_dest_cid IN NUMBER,
                             p_custom_columns IN VARCHAR2,
                             p_flag_time IN NUMBER,
                             p_flag_status IN NUMBER,
                             p_flag_spidst IN NUMBER,
                             p_flag_attributes IN NUMBER,
                             p_flag_divide IN NUMBER,
                             p_flag_multiply IN NUMBER,
                             p_flag_existence IN NUMBER,
                             p_existence_default_value IN NUMBER) RETURN NUMBER AS


  v_data_left ghost_bulk_data_obj;
  v_data_right ghost_bulk_data_obj;
  v_save_uidvm ghost_tab_number;
  v_count NUMBER;

  BEGIN

     v_data_left := ghost_bulk_data_obj();
     v_data_right := ghost_bulk_data_obj();


              EXECUTE IMMEDIATE p_sql_query
                               BULK COLLECT INTO v_data_left.blob_value,
                                                 v_data_right.blob_value,
                                                 v_data_left.uidvm,
                                                 v_data_left.starttime,
                                                 v_data_left.stoptime,
                                                 v_data_left.spi,
                                                 v_data_left.intervalcount,
                                                 v_data_left.dst_participant,
                                                 v_data_left.custom_1,
                                                 v_data_left.custom_2,
                                                 v_data_left.custom_3,
                                                 v_data_left.custom_4,
                                                 v_data_left.custom_5,
                                                 v_data_left.custom_6,
                                                 v_data_left.custom_7,
                                                 v_data_left.custom_8,
                                                 v_data_left.custom_9,
                                                 v_data_left.custom_10,
                                                 v_data_left.custom_11,
                                                 v_data_left.custom_12,
                                                 v_data_left.custom_13,
                                                 v_data_left.custom_14,
                                                 v_data_left.custom_15,
                                                 v_data_left.custom_16,
                                                 v_data_left.custom_17,
                                                 v_data_left.custom_18,
                                                 v_data_left.custom_19,
                                                 v_data_left.custom_20,
                                                 v_data_left.custom_date_1,
                                                 v_data_left.custom_date_2,
                                                 v_data_left.custom_date_3,
                                                 v_data_left.custom_date_4,
                                                 v_data_left.custom_date_5,
                                                 v_data_left.custom_date_6,
                                                 v_data_left.custom_date_7,
                                                 v_data_left.custom_date_8,
                                                 v_data_left.custom_date_9,
                                                 v_data_left.custom_date_10,
                                                 v_data_right.uidvm,
                                                 v_data_right.starttime,
                                                 v_data_right.stoptime,
                                                 v_data_right.spi,
                                                 v_data_right.intervalcount,
                                                 v_data_right.dst_participant,
                                                 v_data_right.custom_1,
                                                 v_data_right.custom_2,
                                                 v_data_right.custom_3,
                                                 v_data_right.custom_4,
                                                 v_data_right.custom_5,
                                                 v_data_right.custom_6,
                                                 v_data_right.custom_7,
                                                 v_data_right.custom_8,
                                                 v_data_right.custom_9,
                                                 v_data_right.custom_10,
                                                 v_data_right.custom_11,
                                                 v_data_right.custom_12,
                                                 v_data_right.custom_13,
                                                 v_data_right.custom_14,
                                                 v_data_right.custom_15,
                                                 v_data_right.custom_16,
                                                 v_data_right.custom_17,
                                                 v_data_right.custom_18,
                                                 v_data_right.custom_19,
                                                 v_data_right.custom_20,
                                                 v_data_right.custom_date_1,
                                                 v_data_right.custom_date_2,
                                                 v_data_right.custom_date_3,
                                                 v_data_right.custom_date_4,
                                                 v_data_right.custom_date_5,
                                                 v_data_right.custom_date_6,
                                                 v_data_right.custom_date_7,
                                                 v_data_right.custom_date_8,
                                                 v_data_right.custom_date_9,
                                                 v_data_right.custom_date_10;

     v_count := blob_operation_pair_helper(v_data_left,
                                           v_data_right,
                                           p_operation,
                                           p_dest_cid,
                                           p_custom_columns,
                                           p_flag_time,
                                           p_flag_status,
                                           p_flag_spidst,
                                           p_flag_attributes,
                                           p_flag_divide,
                                           p_flag_multiply,
                                           p_flag_existence,
                                           p_existence_default_value);

     COMMIT;
     RETURN v_count;
     EXCEPTION
         WHEN e_no_records_match THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                       ||
                                           'BLOB_OPERATION_PAIR No records matched! '                       ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation) ||
                                           ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                           ' p_dest_cid:' || ghost_util.wrap_error_params(p_dest_cid),
                                           SQLERRM);
         WHEN OTHERS THEN
             --Need to close open blobs in same transaction!
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                       ||
                                           'BLOB_OPERATION_PAIR  '                                          ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation) ||
                                           ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                           ' p_dest_cid:' || ghost_util.wrap_error_params(p_dest_cid),
                                           SQLERRM);

  END blob_operation_pair;



FUNCTION blob_operation_pair_scalar(p_sql_query IN VARCHAR2,
                                    p_operation IN NUMBER,
                                    p_dest_cid IN NUMBER,
                                    p_custom_columns IN VARCHAR2,
                                    p_flag_time IN NUMBER,
                                    p_flag_status IN NUMBER,
                                    p_flag_spidst IN NUMBER,
                                    p_flag_attributes IN NUMBER,
                                    p_flag_divide IN NUMBER,
                                    p_flag_multiply IN NUMBER,
                                    p_flag_existence IN NUMBER,
                                    p_existence_default_value IN NUMBER) RETURN NUMBER AS

  cur_header t_refcursor;
  v_data_left ghost_bulk_data_obj;
  v_data_right ghost_bulk_data_obj;

  v_spi NUMBER;
  v_existence_value NUMBEr;

  BEGIN

     v_data_left := ghost_bulk_data_obj();
     v_data_right := ghost_bulk_data_obj();

     IF NOT cur_header%ISOPEN THEN
         OPEN cur_header FOR p_sql_query;-- || ' FOR UPDATE';
              FETCH cur_header BULK COLLECT INTO v_data_left.blob_value,
                                                 v_data_right.scalar_value,
                                                 v_data_left.uidvm,
                                                 v_data_left.starttime,
                                                 v_data_left.stoptime,
                                                 v_data_left.spi,
                                                 v_data_left.intervalcount,
                                                 v_data_left.dst_participant,
                                                 v_data_left.custom_1,
                                                 v_data_left.custom_2,
                                                 v_data_left.custom_3,
                                                 v_data_left.custom_4,
                                                 v_data_left.custom_5,
                                                 v_data_left.custom_6,
                                                 v_data_left.custom_7,
                                                 v_data_left.custom_8,
                                                 v_data_left.custom_9,
                                                 v_data_left.custom_10,
                                                 v_data_left.custom_11,
                                                 v_data_left.custom_12,
                                                 v_data_left.custom_13,
                                                 v_data_left.custom_14,
                                                 v_data_left.custom_15,
                                                 v_data_left.custom_16,
                                                 v_data_left.custom_17,
                                                 v_data_left.custom_18,
                                                 v_data_left.custom_19,
                                                 v_data_left.custom_20,
                                                 v_data_left.custom_date_1,
                                                 v_data_left.custom_date_2,
                                                 v_data_left.custom_date_3,
                                                 v_data_left.custom_date_4,
                                                 v_data_left.custom_date_5,
                                                 v_data_left.custom_date_6,
                                                 v_data_left.custom_date_7,
                                                 v_data_left.custom_date_8,
                                                 v_data_left.custom_date_9,
                                                 v_data_left.custom_date_10,
                                                 v_data_right.uidvm,
                                                 v_data_right.starttime,
                                                 v_data_right.stoptime,
                                                 v_data_right.spi,
                                                 v_data_right.intervalcount,
                                                 v_data_right.dst_participant,
                                                 v_data_right.custom_1,
                                                 v_data_right.custom_2,
                                                 v_data_right.custom_3,
                                                 v_data_right.custom_4,
                                                 v_data_right.custom_5,
                                                 v_data_right.custom_6,
                                                 v_data_right.custom_7,
                                                 v_data_right.custom_8,
                                                 v_data_right.custom_9,
                                                 v_data_right.custom_10,
                                                 v_data_right.custom_11,
                                                 v_data_right.custom_12,
                                                 v_data_right.custom_13,
                                                 v_data_right.custom_14,
                                                 v_data_right.custom_15,
                                                 v_data_right.custom_16,
                                                 v_data_right.custom_17,
                                                 v_data_right.custom_18,
                                                 v_data_right.custom_19,
                                                 v_data_right.custom_20,
                                                 v_data_right.custom_date_1,
                                                 v_data_right.custom_date_2,
                                                 v_data_right.custom_date_3,
                                                 v_data_right.custom_date_4,
                                                 v_data_right.custom_date_5,
                                                 v_data_right.custom_date_6,
                                                 v_data_right.custom_date_7,
                                                 v_data_right.custom_date_8,
                                                 v_data_right.custom_date_9,
                                                 v_data_right.custom_date_10;

         CLOSE cur_header;
     END IF;


     IF v_data_left.blob_value.COUNT = 0 THEN
            RAISE e_no_records_match;
     END IF;


     v_spi := v_data_left.spi(1);
     v_data_right.blob_value := ghost_tab_blob();
     v_data_right.blob_value.EXTEND(v_data_right.scalar_value.COUNT);

     FOR x IN v_data_right.scalar_value.FIRST..v_data_right.scalar_value.LAST LOOP
       IF v_data_right.scalar_value(x) IS NOT NULL THEN
           v_data_right.blob_value(x) := create_blob_data(v_spi,
                                                          v_data_right.scalar_value(x),
                                                          CONST_BLOB_GOOD_STATUS_CODE);
       ELSE
           v_data_right.blob_value(x) := NULL;

       END IF;

     END LOOP;


     RETURN blob_operation_pair_helper(v_data_left,
                                       v_data_right,
                                       p_operation,
                                       p_dest_cid,
                                       p_custom_columns,
                                       p_flag_time,
                                       p_flag_status,
                                       p_flag_spidst,
                                       p_flag_attributes,
                                       p_flag_divide,
                                       p_flag_multiply,
                                       p_flag_existence,
                                       p_existence_default_value);
     EXCEPTION
         WHEN e_no_records_match THEN
            IF cur_header%ISOPEN THEN
                CLOSE cur_header;
             END IF;

             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                       ||
                                           'BLOB_OPERATION_PAIR_SCALAR No records matched! '                       ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                           SQLERRM);
         WHEN OTHERS THEN
             --Need to close open blobs in same transaction!
             IF cur_header%ISOPEN THEN
                CLOSE cur_header;
             END IF;

             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                       ||
                                           'BLOB_OPERATION_PAIR_SCALAR  '                                          ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                           SQLERRM);

  END blob_operation_pair_scalar;




  PROCEDURE blob_collection_operation(p_sql_query IN VARCHAR2,
                                      p_operation IN NUMBER,
                                      p_cid IN NUMBER,
                                      p_value IN NUMBER) AS

--    v_uidvm_tab ghost_tab_number;
    v_raw_number raw_interval;
    v_binary_number BINARY_DOUBLE;
--    v_blob BLOB;

    v_num_intervals NUMBER;

    v_blob_table ghost_tab_blob;
    v_blob_save ghost_tab_blob;
    v_uidvm ghost_tab_number;

    cur_header t_refcursor;
    v_num_iterations NUMBER;
    v_pos NUMBER;

    v_metablob_total NUMBER;
    v_metablob_max NUMBER;
    v_metablob_min NUMBER;
    v_interval_count NUMBER;

  BEGIN

/*
     UPDATE ghost_vm
        SET blob_value = empty_blob()
      WHERE  BITAND(ghost_collection_id, p_cid) = p_cid
        AND blob_value IS NULL;

     IF NOT cur_header%ISOPEN THEN
         OPEN cur_header FOR 'SELECT b_value, blob_value, uidvm FROM (' || p_sql_query || ') FOR UPDATE';
              FETCH cur_header BULK COLLECT INTO v_blob_table, v_blob_save, v_uidvm;
         CLOSE cur_header;
     END IF;
     */

     load_remote_blobs_into_vm(p_sql_query);

     EXECUTE IMMEDIATE 'SELECT blob_value, uidvm FROM (' || p_sql_query || ') FOR UPDATE'
     BULK COLLECT INTO v_blob_table, v_uidvm;


     FOR x IN v_blob_table.FIRST..v_blob_table.LAST LOOP

       v_num_intervals := get_blob_length(v_blob_table(x));
       v_num_iterations := v_num_intervals;

       dbms_lob.open(v_blob_table(x), dbms_lob.lob_readwrite);

       v_pos := 1;
       v_binary_number := binary_double_operation(utl_raw.cast_to_binary_double(utl_raw.reverse(dbms_lob.substr(v_blob_table(x), CONST_INTERVAL_BYTE_SIZE,v_pos))),
                                                  p_value,
                                                  p_operation);

       --Set to good status code to ignore an issues when setting max and min
       set_init_metablob_attributes(CONST_BLOB_GOOD_STATUS_CODE,
                                    v_binary_number,
                                    v_metablob_total,
                                    v_metablob_max,
                                    v_metablob_min);

       v_raw_number := utl_RAW.REVERSE(utl_raw.cast_from_binary_double(v_binary_number));
       ghost_util.ghost_write_raw_to_blob(v_blob_table(x), v_raw_number, v_pos);

       FOR j in 2..v_num_iterations LOOP
           v_pos:= (CONST_INTERVAL_BYTE_SIZE*(j-1))+1;
           v_binary_number := binary_double_operation(utl_raw.cast_to_binary_double(utl_raw.reverse(dbms_lob.substr(v_blob_table(x), CONST_INTERVAL_BYTE_SIZE,v_pos))),
                                                      p_value,
                                                      p_operation);
           v_raw_number := utl_RAW.REVERSE(utl_raw.cast_from_binary_double(v_binary_number));
           ghost_util.ghost_write_raw_to_blob(v_blob_table(x), v_raw_number, v_pos);

           set_metablob_attributes(CONST_BLOB_GOOD_STATUS_CODE,
                                   v_binary_number,
                                   v_metablob_total,
                                   v_metablob_max,
                                   v_metablob_min);

       END LOOP;

--       v_pos:= get_metablob_number_position(v_num_iterations+1);
       --ghost_util.ghost_write_raw_to_blob(v_blob_save(x),utl_raw.substr(v_blob_table(x),v_pos));
       dbms_lob.close(v_blob_table(x));

       --FORALL x in v_uidvm.FIRST..v_uidvm.LAST
       UPDATE ghost_vm
          SET metablob_total = v_metablob_total,
              metablob_max = v_metablob_max,
              metablob_min = v_metablob_min,
              last_update_date = SYSDATE
        WHERE uidvm = v_uidvm(x);


     END LOOP;


     COMMIT;
     EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                       ||
                                           'BLOB_COLLECTION_OPERATION  '                                          ||
                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                           SQLERRM);

  END blob_collection_operation;




  PROCEDURE blob_collection_round(p_sql_query IN VARCHAR2,
                                  p_collection_id IN NUMBER,
                                  p_num_decimals IN INTEGER) AS

    v_blob_table ghost_tab_blob;
    v_uidvm_table ghost_tab_number;

  BEGIN
--     v_blob_table := get_collection_blobs_updatable(p_sql_query,p_collection_id);
     load_remote_blobs_into_vm(p_sql_query);

     EXECUTE IMMEDIATE 'SELECT blob_value, uidvm FROM ghost_vm WHERE BITAND (ghost_collection_id, :p_collection_id) = :p_collection_id FOR UPDATE'
     BULK COLLECT INTO v_blob_table, v_uidvm_table USING p_collection_id, p_collection_id;
     FOR x IN v_blob_table.FIRST..v_blob_table.LAST LOOP
         blob_round_allint_helper(v_blob_table(x),
                                  v_uidvm_table(x),
                                  p_num_decimals);
     END LOOP;


     COMMIT;
     EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                   ||
                                           'BLOB_COLLECTION_ROUND  '                                               ||
                                           ' p_collection_id:' || ghost_util.wrap_error_params(p_collection_id)         ||
                                           ' p_num_decimals:' || ghost_util.wrap_error_params(p_num_decimals)    ,
                                           SQLERRM);

  END blob_collection_round;



PROCEDURE blob_collection_status_code(p_collection_id IN NUMBER,
                                      p_pos IN NUMBER,
                                      p_status IN VARCHAR2,
                                      p_sql_query IN VARCHAR2) AS
      v_blob_table ghost_tab_blob;

  BEGIN
/*
      load_remote_blobs_into_vm(p_sql_query);
      --EXECUTE IMMEDIATE 'SELECT b_value FROM (' || p_sql_query || ') FOR UPDATE'
      EXECUTE IMMEDIATE 'SELECT blob_value FROM ghost_vm WHERE BITAND (ghost_collection_id, :p_collection_id) = :p_collection_id FOR UPDATE'
      BULK COLLECT INTO v_blob_table USING p_collection_id, p_collection_id;
*/

      v_blob_table := get_collection_blobs_updatable(p_sql_query,p_collection_id);
      FOR x IN v_blob_table.FIRST..v_blob_table.LAST LOOP
          blob_set_status_code_helper(v_blob_table(x),
                                      p_pos,
                                      p_status);
      END LOOP;


      COMMIT;
     EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                   ||
                                           'BLOB_COLLECTION_STATUS_CODE  '                                              ||
                                           ' p_collection_id:' || ghost_util.wrap_error_params(p_collection_id)   ||
                                           ' p_pos:' || ghost_util.wrap_error_params(p_pos)                       ||
                                           ' p_status:' || ghost_util.wrap_error_params(p_status),
                                           SQLERRM);

END blob_collection_status_code;



PROCEDURE blob_compress(p_uidvm IN NUMBER,
                        p_spi IN NUMBER,
                        p_blob IN BLOB,
                        p_scale IN NUMBER,
                        p_scale_loop IN NUMBER,
                        p_num_intervals IN NUMBER) AS
     v_blob BLOB;
     v_binary_number BINARY_DOUBLE;
     v_prev_binary_number BINARY_DOUBLE;
     v_dest_num_intervals NUMBER;

     v_statuscodes status_code_varchar;
     v_status_code VARCHAR2(1);
     v_metablob_total NUMBER;
     v_metablob_max NUMBER;
     v_metablob_min NUMBER;

BEGIN

     v_dest_num_intervals := p_scale_loop;
     --v_pos_statuscode := get_status_code_pos(p_num_intervals,1);
     --v_status_code := get_status_code(p_blob,v_pos_statuscode);
     v_status_code := get_status_code(p_blob,p_num_intervals,1);
     v_prev_binary_number := 0;

     FOR x IN 1..p_scale LOOP
       --v_pos_source:= get_metablob_number_position(x);
       v_binary_number := binary_double_operation(get_metablob_interval(p_blob,x),
                                                  v_prev_binary_number,
                                                  CONSTANT_BINARY_OP_ADD);
       v_prev_binary_number := v_binary_number;
     END LOOP;


     v_binary_number := v_binary_number / p_scale;

     set_init_metablob_attributes(v_status_code,
                                  v_binary_number,
                                  v_metablob_total,
                                  v_metablob_max,
                                  v_metablob_min);

     --v_pos_dest := 1;

     dbms_lob.createtemporary(v_blob,TRUE);
     dbms_lob.open(v_blob, dbms_lob.lob_readwrite);

     --v_raw_number := utl_RAW.REVERSE(utl_raw.cast_from_binary_double(v_binary_number));
     --ghost_util.ghost_write_raw_to_blob(v_blob, v_raw_number, v_pos_dest);
     write_binarydouble_to_metablob(v_blob,1,v_binary_number);


     v_statuscodes := v_statuscodes || v_status_code;

     FOR destPosition IN 2..v_dest_num_intervals LOOP
         --v_pos_dest := get_metablob_number_position(destPosition);
         v_prev_binary_number := 0;

         FOR x IN 1..p_scale LOOP
           --v_pos_source:= get_metablob_number_position( (p_scale*(destPosition-1))+x);
           v_binary_number := binary_double_operation(get_metablob_interval(p_blob, (p_scale*(destPosition-1))+x),
                                                      v_prev_binary_number,
                                                      CONSTANT_BINARY_OP_ADD);
           v_prev_binary_number := v_binary_number;
         END LOOP;


         v_binary_number := v_binary_number / p_scale;

         --v_pos_statuscode := get_status_code_pos(p_num_intervals,1);
         --v_status_code := get_status_code(p_blob,v_pos_statuscode);
         v_status_code := get_status_code(p_blob, p_num_intervals,1);

         set_metablob_attributes(v_status_code,
                                 v_binary_number,
                                 v_metablob_total,
                                 v_metablob_max,
                                 v_metablob_min);

         --v_pos_dest := get_metablob_number_position(destPosition);

         --ghost_util.ghost_write_raw_to_blob(v_blob, v_raw_number, v_pos_dest);
         --v_raw_number := utl_RAW.REVERSE(utl_raw.cast_from_binary_double(v_binary_number));
         write_binarydouble_to_metablob(v_blob,destPosition,v_binary_number);

         v_statuscodes := v_statuscodes || v_status_code;

     END LOOP;

     --Write Zero value
--     v_pos_dest := get_metablob_number_position(v_dest_num_intervals+1);

     --v_raw_number := utl_RAW.REVERSE(utl_raw.cast_from_binary_double(0));
     --ghost_util.ghost_write_raw_to_blob(v_blob, v_raw_number, v_pos_dest);
     write_binarydouble_to_metablob(v_blob,v_dest_num_intervals+1,0);

     -- Write status codes
     ghost_util.ghost_write_varchar2_to_blob(v_blob, v_statuscodes);

     dbms_lob.close(v_blob);
     UPDATE ghost_vm
        SET blob_value = v_blob,
            metablob_total = v_metablob_total,
            metablob_max = v_metablob_max,
            metablob_min = v_metablob_min,
            metablob_intervalcount = v_dest_num_intervals,
            metablob_spi = p_spi
      WHERE uidvm = p_uidvm;

      COMMIT;
     EXCEPTION
       WHEN OTHERS THEN
              IF(v_blob IS NOT NULL) THEN
                 IF(dbms_lob.isopen(v_blob) <> 0 ) THEN
                     dbms_lob.close(v_blob);
                 END IF;

               END IF;

               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG                                      ||
                                             'BLOB_COMPRESS  '                                               ||
                                             ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)            ||
                                             ' p_scale_loop:' || ghost_util.wrap_error_params(p_scale_loop)  ||
                                             ' p_num_intervals:' || ghost_util.wrap_error_params(p_num_intervals),
                                             SQLERRM);

END blob_compress;



PROCEDURE blob_expand(p_uidvm IN NUMBER,
                      p_spi IN NUMBER,
                      p_blob IN BLOB,
                      p_scale IN NUMBER,
                      p_dest_num_intervals IN NUMBER,
                      p_num_intervals IN NUMBER) AS
     v_blob BLOB;
     --v_pos_source NUMBER;
     --v_pos_dest NUMBER;
     --v_pos_statuscode NUMBER;
     v_binary_number BINARY_DOUBLE;
     v_raw_number raw_interval;
     v_source_num_intervals NUMBER;

     v_statuscodes status_code_varchar;
     v_status_code VARCHAR2(1);
     v_metablob_total NUMBER;
     v_metablob_max NUMBER;
     v_metablob_min NUMBER;

BEGIN

     --v_pos_statuscode := get_status_code_pos(p_num_intervals,1);
     --v_status_code := get_status_code(p_blob,v_pos_statuscode);
     v_status_code := get_status_code(p_blob,p_num_intervals,1);

     --v_pos_source:= get_metablob_number_position(1);
     v_binary_number := get_metablob_interval(p_blob, 1);

     dbms_lob.createtemporary(v_blob,TRUE);
     dbms_lob.open(v_blob, dbms_lob.lob_readwrite);

     FOR x IN 1..p_scale LOOP
       --v_pos_dest := get_metablob_number_position(x);
       write_binarydouble_to_metablob(v_blob,x,v_binary_number);
       v_statuscodes := v_statuscodes || v_status_code;
     END LOOP;


     --v_metablob_total:= v_binary_number * p_scale;
     set_init_metablob_attributes(v_status_code,
                                  v_binary_number,
                                  v_metablob_total,
                                  v_metablob_max,
                                  v_metablob_min);

     v_metablob_total:= v_metablob_total * p_scale;

     FOR sourcePosition IN 2..p_num_intervals LOOP
         --v_pos_source := get_metablob_number_position(sourcePosition);
         v_binary_number := get_metablob_interval(p_blob, sourcePosition);
         v_status_code := get_status_code(p_blob,p_num_intervals,sourcePosition);

         FOR x IN 1..p_scale LOOP
           --v_pos_dest := get_metablob_number_position( (p_scale*(sourcePosition-1))+x);
           write_binarydouble_to_metablob(v_blob,(p_scale*(sourcePosition-1))+x,v_binary_number);
           v_statuscodes := v_statuscodes || v_status_code;
           set_metablob_attributes(v_status_code,
                                 v_binary_number,
                                 v_metablob_total,
                                 v_metablob_max,
                                 v_metablob_min);
         END LOOP;


     END LOOP;


     --Write Zero value
     --v_pos_dest := get_metablob_number_position(p_scale_loop+1);
     --v_raw_number := utl_RAW.REVERSE(utl_raw.cast_from_binary_double(0));
     --ghost_util.ghost_write_raw_to_blob(v_blob, v_raw_number, v_pos_dest);
     write_binarydouble_to_metablob(v_blob,p_dest_num_intervals+1,0);

     -- Write status codes
     ghost_util.ghost_write_varchar2_to_blob(v_blob, v_statuscodes);

     dbms_lob.close(v_blob);

     UPDATE ghost_vm
        SET blob_value = v_blob,
            metablob_total = v_metablob_total,
            metablob_max = v_metablob_max,
            metablob_min = v_metablob_min,
            metablob_intervalcount = p_dest_num_intervals,
            metablob_spi = p_spi
      WHERE uidvm = p_uidvm;


      COMMIT;
     EXCEPTION
       WHEN OTHERS THEN
              IF(v_blob IS NOT NULL) THEN
                 IF(dbms_lob.isopen(v_blob) <> 0 ) THEN
                     dbms_lob.close(v_blob);
                 END IF;

               END IF;

               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG                                      ||
                                             'BLOB_EXPAND  '                                                 ||
                                             ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)            ||
                                             ' p_dest_num_intervals:' || ghost_util.wrap_error_params(p_dest_num_intervals)  ||
                                             ' p_num_intervals:' || ghost_util.wrap_error_params(p_num_intervals),
                                             SQLERRM);

END blob_expand;



PROCEDURE blob_scale_helper(p_uidvm IN NUMBER,
                            p_dest_spi IN NUMBER,
                            p_blob IN BLOB,
                            p_spi IN NUMBER) AS
      v_num_intervals NUMBER;
      v_scale_loop NUMBER;
      v_scale BINARY_DOUBLE;

  BEGIN
       v_num_intervals := get_blob_length(p_blob);
       IF (p_dest_spi != p_spi) THEN
         IF (p_dest_spi>p_spi) THEN
             v_scale := FLOOR(p_dest_spi/p_spi);
             v_scale_loop := v_num_intervals / v_scale;
             blob_compress(p_uidvm,
                           p_dest_spi,
                           p_blob,
                           v_scale,
                           v_scale_loop,
                           v_num_intervals);
         ELSE
             v_scale := FLOOR(p_spi/p_dest_spi);
             v_scale_loop := v_num_intervals * v_scale;
             blob_expand(p_uidvm,
                         p_dest_spi,
                         p_blob,
                         v_scale,
                         v_scale_loop,
                         v_num_intervals);

         END IF;

       END IF;

  EXCEPTION
     WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                   ||
                                           'BLOB_SCALE_HELPER  '                                               ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)         ||
                                           ' p_dest_spi:' || ghost_util.wrap_error_params(p_dest_spi)    ,
                                           SQLERRM);

  END blob_scale_helper;



PROCEDURE blob_scale(p_uidvm IN NUMBER,
                     p_dest_spi IN NUMBER,
                     p_sql_query IN VARCHAR2) AS
  v_spi NUMBER;
  v_blob BLOB;

  BEGIN
         IF p_sql_query IS NULL THEN
            SELECT blob_value, metablob_spi
              INTO v_blob, v_spi
              FROM ghost_vm
             WHERE uidvm = p_uidvm;

         ELSE
            EXECUTE IMMEDIATE p_sql_query INTO v_blob, v_spi USING p_uidvm;

         END IF;


         IF(v_blob IS NULL) THEN
            RAISE e_blob_is_null;
         END IF;




         blob_scale_helper(p_uidvm,
                           p_dest_spi,
                           v_blob,
                           v_spi);


     EXCEPTION
         WHEN e_blob_is_null THEN
            ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                       ||
                                           'BLOB_SCALE  Left or Right Blob value is Null! '                 ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)             ||
                                           ' p_dest_spi:' || ghost_util.wrap_error_params(p_dest_spi)        ,
                                           SQLERRM);
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                   ||
                                           'BLOB_SCALE  '                                               ||
                                           ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm)         ||
                                           ' p_dest_spi:' || ghost_util.wrap_error_params(p_dest_spi)    ,
                                           SQLERRM);

  END blob_scale;



PROCEDURE blob_collection_scale(p_collection_id IN NUMBER,
                                p_dest_spi IN NUMBER,
                                p_sql_query IN VARCHAR2) AS
    v_blob_table ghost_tab_blob;
    v_spi_table ghost_tab_number;
    v_uidvm_table ghost_tab_number;

  BEGIN

     load_remote_blobs_into_vm(p_sql_query);

     --EXECUTE IMMEDIATE 'SELECT b_value, metablob_spi, uidvm FROM (' || p_sql_query || ') FOR UPDATE'
     EXECUTE IMMEDIATE 'SELECT blob_value, metablob_spi, uidvm FROM ghost_vm WHERE BITAND (ghost_collection_id, :p_collection_id) = :p_collection_id FOR UPDATE'
     BULK COLLECT INTO v_blob_table, v_spi_table, v_uidvm_table USING p_collection_id, p_collection_id;

     FOR x IN v_blob_table.FIRST..v_blob_table.LAST LOOP
         blob_scale_helper(v_uidvm_table(x),
                           p_dest_spi,
                           v_blob_table(x),
                           v_spi_table(x));
     END LOOP;


     EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                   ||
                                           'BLOB_COLLECTION_SCALE  '                                               ||
                                           ' p_collection_id:' || ghost_util.wrap_error_params(p_collection_id)         ||
                                           ' p_dest_spi:' || ghost_util.wrap_error_params(p_dest_spi)    ,
                                           SQLERRM);

  END blob_collection_scale;



PROCEDURE put_bulk_id( o_bulk_ids IN OUT ghost_tab_number,
                         p_value IN NUMBER) AS
      v_not_found boolean := true;
      v_size NUMBER;

  BEGIN
       v_size:= o_bulk_ids.COUNT;
       FOR x IN 1..v_size LOOP
          IF(o_bulk_ids(x)=p_value) THEN
             v_not_found := false;
          END IF;

       END LOOP;

       IF v_not_found THEN
          o_bulk_ids.EXTEND;
          o_bulk_ids(v_size+1) := p_value;
       END IF;


       EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                       ghost_util.CONST_ERROR_MSG                                             ||
                                       'PUT_BULK_ID '                                               ||
                                       ' p_value:' || ghost_util.wrap_error_params(p_value),
                                       SQLERRM);

  END put_bulk_id;


  PROCEDURE poll_ghost_batch_child_jobs(p_num_processed_rows IN NUMBER,
                                        p_dequeue_options IN OUT dbms_aq.dequeue_options_t,
                                        p_message_properties IN OUT dbms_aq.message_properties_t,
                                        p_message IN OUT ghost_job_child_obj,
                                        p_message_handle IN OUT RAW,
                                        p_timeout IN NUMBER,
                                        p_bulk_ids IN OUT ghost_tab_number) AS
      v_num_processed_rows NUMBER;
      --v_num_messages NUMBER := 1;
--      v_next_slot NUMBER:=1;
      v_child_job_error BOOLEAN := FALSE;
      v_error_id NUMBER;
      v_starttime TIMESTAMP;
      v_stoptime TIMESTAMP;
      e_empty_dequeue_exception EXCEPTION;
      e_timeout_exception EXCEPTION;
      e_too_many_processed_exception EXCEPTION;
      PRAGMA EXCEPTION_INIT ( e_empty_dequeue_exception, -25228);
      PRAGMA EXCEPTION_INIT ( e_timeout_exception, -25227);
      PRAGMA AUTONOMOUS_TRANSACTION;

    BEGIN
     SELECT SYSTIMESTAMP
       INTO v_starttime
       FROM DUAL;


     <<poll_jobs>>
     BEGIN
        LOOP
             SELECT SYSTIMESTAMP
               INTO v_stoptime
               FROM DUAL;


             DBMS_AQ.DEQUEUE(queue_name => 'ghost_queue',
                     dequeue_options    => p_dequeue_options,
                     message_properties => p_message_properties,
                     payload            => p_message,
                     msgid              => p_message_handle);

             v_num_processed_rows := p_num_processed_rows - p_message.record_count;

             IF v_num_processed_rows < 0 THEN
                RAISE e_too_many_processed_exception;
             END IF;

             --v_num_messages := v_num_messages + 1;
--             p_bulk_ids(hash_bulk_id_map(p_iterations,p_message.bulk_id)):= p_message.bulk_id;
             IF p_message.bulk_id IS NOT NULL THEN
               put_bulk_id(p_bulk_ids, p_message.bulk_id);
             END IF;

             --p_bulk_ids(v_num_messages):= p_message.bulk_id;

             IF p_message.status != 'SUCCESS' THEN
                  IF v_child_job_error = FALSE THEN
                     v_error_id := ghost_util.getSequence(ghost_util.CONST_SEQ_GHOSTUIDERROR);
                     v_child_job_error := TRUE;
                  END IF;

                  ghost_util.write_ghost_detail_error_cjob(v_error_id,
                                                       NULL,
                                                       ghost_util.CONST_PCF_ERROR_CJERROR,
                                                       p_message.process || ' failure',
                                                       p_message.message,
                                                       p_message.start_range || '-' || p_message.end_range
                                                       );
             END IF;


             EXIT WHEN v_num_processed_rows = 0;

        END LOOP;


        EXCEPTION
         WHEN e_empty_dequeue_exception THEN
              IF ghost_util.get_seconds_from_interval(v_stoptime  - v_starttime) > p_timeout THEN
                 RAISE e_timeout_exception;
              END IF;

              IF v_num_processed_rows != 0 THEN
                 GOTO poll_jobs;
              END IF;

         WHEN OTHERS THEN
           RAISE;

       END;

       IF v_child_job_error = TRUE THEN
          ghost_util.raise_ghost_error_cjob (v_error_id,
                                         ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                         ghost_util.CONST_ERROR_MSG                               ||
                                         'POLL_GHOST_BATCH__CHILD_JOBS : Child job failure, check log. ',
                                         SQLERRM);
       END IF;

       EXCEPTION
         WHEN e_too_many_processed_exception THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                       ghost_util.CONST_ERROR_MSG                                             ||
                                       'POLL_GHOST_BATCH_CHILD_JOBS : Too many records processed for batch!'                                               ||
                                       ' p_num_processed_rows:' || ghost_util.wrap_error_params(p_num_processed_rows)||
                                       ' v_num_processed_rows:' || ghost_util.wrap_error_params(v_num_processed_rows)||
                                       ' p_message.record_count:' || ghost_util.wrap_error_params( p_message.record_count),
                                       SQLERRM);
         WHEN e_timeout_exception THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                       ghost_util.CONST_ERROR_MSG                                             ||
                                       'POLL_GHOST_BATCH_CHILD_JOBS : Process excceded Timeout'                                               ||
                                       ' p_num_processed_rows:' || ghost_util.wrap_error_params(p_num_processed_rows),
                                       SQLERRM);

         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                       ghost_util.CONST_ERROR_MSG                                             ||
                                       'POLL_GHOST_BATCH_CHILD_JOBS '                                               ||
                                       ' p_num_processed_rows:' || ghost_util.wrap_error_params(p_num_processed_rows),
                                       SQLERRM);

    END poll_ghost_batch_child_jobs;



  PROCEDURE poll_ghost_child_jobs(p_num_messages IN NUMBER,
                                  p_dequeue_options IN OUT dbms_aq.dequeue_options_t,
                                  p_message_properties IN OUT dbms_aq.message_properties_t,
                                  p_message IN OUT ghost_job_child_obj,
                                  p_message_handle IN OUT RAW,
                                  p_timeout IN NUMBER) AS
      v_num_messages NUMBER := 0;
      v_child_job_error BOOLEAN := FALSE;
      v_error_id NUMBER;
      v_starttime TIMESTAMP;
      v_stoptime TIMESTAMP;
      e_empty_dequeue_exception EXCEPTION;
      e_timeout_exception EXCEPTION;
      PRAGMA EXCEPTION_INIT ( e_empty_dequeue_exception, -25228);
      PRAGMA AUTONOMOUS_TRANSACTION;

    BEGIN
     SELECT SYSTIMESTAMP
       INTO v_starttime
       FROM DUAL;


     <<poll_jobs>>
     BEGIN
        LOOP
             SELECT SYSTIMESTAMP
               INTO v_stoptime
               FROM DUAL;


             DBMS_AQ.DEQUEUE(queue_name => 'ghost_queue',
                     dequeue_options    => p_dequeue_options,
                     message_properties => p_message_properties,
                     payload            => p_message,
                     msgid              => p_message_handle);

             v_num_messages := v_num_messages + 1;

             IF p_message.status != 'SUCCESS' THEN
                  IF v_child_job_error = FALSE THEN
                     v_error_id := ghost_util.getSequence(ghost_util.CONST_SEQ_GHOSTUIDERROR);
                     v_child_job_error := TRUE;
                  END IF;

                  ghost_util.write_ghost_detail_error_cjob(v_error_id,
                                                       NULL,
                                                       ghost_util.CONST_PCF_ERROR_CJERROR,
                                                       p_message.process || ' failure',
                                                       p_message.message,
                                                       p_message.driver_id || ':' || p_message.start_range || '-' || p_message.end_range
                                                       );
             END IF;


             EXIT WHEN v_num_messages = p_num_messages;

        END LOOP;


        EXCEPTION
         WHEN e_empty_dequeue_exception THEN
              IF ghost_util.get_seconds_from_interval(v_stoptime  - v_starttime) > p_timeout THEN
                 RAISE e_timeout_exception;
              END IF;

              IF v_num_messages < p_num_messages THEN
                 GOTO poll_jobs;
              END IF;

         WHEN OTHERS THEN
           RAISE;

       END;

       IF v_child_job_error = TRUE THEN
          ghost_util.raise_ghost_error_cjob (v_error_id,
                                         ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                         ghost_util.CONST_ERROR_MSG                               ||
                                         'POLL_GHOST_CHILD_JOBS : Child job failure, check log. ',
                                         SQLERRM);
       END IF;

       EXCEPTION
         WHEN e_timeout_exception THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                       ghost_util.CONST_ERROR_MSG                                             ||
                                       'POLL_GHOST_CHILD_JOBS : Process excceded Timeout'                                               ||
                                       ' p_num_messages:' || ghost_util.wrap_error_params(p_num_messages),
                                       SQLERRM);
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                       ghost_util.CONST_ERROR_MSG                                             ||
                                       'POLL_GHOST_CHILD_JOBS '                                               ||
                                       ' p_num_messages:' || ghost_util.wrap_error_params(p_num_messages),
                                       SQLERRM);

    END poll_ghost_child_jobs;



FUNCTION escape_string(p_string IN VARCHAR2) RETURN VARCHAR2 IS
       BEGIN
         RETURN REPLACE(p_string,'','''');

         EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                       ghost_util.CONST_ERROR_MSG                                     ||
                                       'ESCAPE_STRING '                                               ||
                                       ' p_string:' || ghost_util.wrap_error_params(p_string),
                                       SQLERRM);

END escape_string;


/*
 This procedure is a workaround to PL/SQL not supporting return values
 using dbms_sql from a dynamic PL/SQL block
*/

PROCEDURE plsql_dyn_return_value(p_in IN NUMBER,
                                 p_out OUT NUMBER) AS
BEGIN
   p_out := p_in;
   EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                       ghost_util.CONST_ERROR_MSG                                     ||
                                       'PLSQL_DYN_RETURN_VALUE '                                               ||
                                       ' p_in:' || ghost_util.wrap_error_params(p_in),
                                       SQLERRM);

END plsql_dyn_return_value;


/*
 This procedure is a workaround to PL/SQL not supporting return values
 using dbms_sql from a dynamic PL/SQL block
*/

PROCEDURE plsql_dyn_return_value_varchar(p_in IN VARCHAR2,
                                         p_out OUT VARCHAR2) AS
BEGIN
   p_out := p_in;
   EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                       ghost_util.CONST_ERROR_MSG                                     ||
                                       'PLSQL_DYN_RETURN_VALUE_VARCHAR '                                               ||
                                       ' p_in:' || ghost_util.wrap_error_params(p_in),
                                       SQLERRM);

END plsql_dyn_return_value_varchar;



FUNCTION multi_thread_process(p_ghost_mt_args IN ghost_mt_arguments,
                              p_message IN OUT ghost_job_child_obj) RETURN NUMBER IS

        v_sql_block VARCHAR2(32000);
        v_sql_update_using VARCHAR2(500);
        v_sql_driver_using VARCHAR2(500);

        v_cursor INTEGER;
        v_rows INTEGER;
        v_array_of_bind_values ghost_util.array_of_strings;
        v_dynamic_batch_vars VARCHAR2(4000) := '';

        v_dequeue_options     dbms_aq.dequeue_options_t;
        v_message_properties  dbms_aq.message_properties_t;
        v_message_handle      RAW(16);
        v_message             ghost_job_child_obj;

        v_iterations NUMBER;
        v_message_correleation_id NUMBER;

  BEGIN

       v_array_of_bind_values := ghost_util.array_of_strings();

       IF p_ghost_mt_args.update_query IS NOT NULL THEN
          IF p_ghost_mt_args.update_query.sql_query IS NOT NULL THEN
            v_sql_update_using := p_ghost_mt_args.update_query.arguments;

            IF p_ghost_mt_args.update_query.arguments IS NOT NULL THEN
               v_sql_update_using := ' USING '|| v_sql_update_using;
            END IF;


--           dbms_output.put_line('BEGIN EXECUTE IMMEDIATE '''|| REPLACE(p_ghost_mt_args.update_query.sql_query,'''','''''') ||
--                              v_sql_update_using || '''' || ';END;');

           EXECUTE IMMEDIATE 'BEGIN EXECUTE IMMEDIATE '''|| REPLACE(p_ghost_mt_args.update_query.sql_query,'''','''''') ||
                              v_sql_update_using || '''' || ';END;';
           COMMIT;
          END IF;

       END IF;



       IF p_ghost_mt_args.driver_query IS NOT NULL THEN
         IF p_ghost_mt_args.driver_query.arguments IS NOT NULL THEN
          v_sql_driver_using := ' USING '|| p_ghost_mt_args.driver_query.arguments;
         END IF;

       END IF;


       IF p_ghost_mt_args.batch_function_bind_args IS NOT NULL THEN
         make_bind_params_mt(p_ghost_mt_args.batch_function_bind_args,
                             CONST_MT_ARG_SEPERATOR,
                             v_dynamic_batch_vars,
                             v_array_of_bind_values);
          v_dynamic_batch_vars := '|| '','' ||' || v_dynamic_batch_vars;
       END IF;


       v_message_correleation_id := Seq_GHOSTCORELATIONID.NEXTVAL;

       v_sql_block := 'DECLARE'                                              || chr(10) ||
                      'v_driver_id ghost_tab_varchar2;'            || chr(10) ||
                      'v_size ghost_tab_number;'                   || chr(10) ||
                      'v_last_size NUMBER :=0;'                              || chr(10) ||
                      'v_iterations NUMBER := 0;'                            || chr(10) ||
                      --'v_sql_query VARCHAR2(4000);'                          || chr(10) ||
                      'v_message_correleation_id NUMBER := :v_message_correleation_id;'   || chr(10) ||
                      'v_cjp_seq NUMBER;'                                    || chr(10) ||
                      'v_jobs ghost_jobs.tArrayJob;'                         || chr(10) ||
                      'v_args ghost_jobs.tArrayJob;'                         || chr(10) ||
                      'cur_header ghost_blob_util.t_refcursor;'              || chr(10) ||
                       p_ghost_mt_args.custom_declare_block                  || chr(10) ||
                      'BEGIN '                                               || chr(10) ||
                      'v_iterations := :p_num_processed_rows;'|| chr(10) ||
                      'IF :p_driver_query IS NOT NULL THEN'                  || chr(10) ||
                      'IF NOT cur_header%ISOPEN THEN'                                        || chr(10) ||
                      '  OPEN cur_header FOR :p_driver_query '
                      || v_sql_driver_using || ';' || chr(10) ||
                      '       FETCH cur_header BULK COLLECT INTO v_driver_id, v_size;'       || chr(10) ||
                      '  CLOSE cur_header;'                                                  || chr(10) ||
                      '  v_iterations := v_size.COUNT();'|| chr(10) ||
                      'END IF;'                                                              || chr(10) ||
                      'END IF;'                                                              || chr(10) ||
--                      'v_iterations := v_size.COUNT();'|| chr(10) ||
                      --'v_sql_query := REPLACE(:p_sql_query,'''','''''''');'|| chr(10) ||
                      ' SELECT Seq_GHOSTCJP.NEXTVAL'|| chr(10) ||
                      '   INTO v_cjp_seq'|| chr(10) ||
                      '   FROM DUAL;'|| chr(10) ||
                      'v_last_size := 0;'|| chr(10) ||
                      'FOR z in 1..v_iterations LOOP'      || chr(10) ||
                      p_ghost_mt_args.custom_loop_block                  || chr(10) ||
                      '   v_jobs(z) := :p_batch_job_name || :p_batch_job_modifiers || :p_transaction_id || v_message_correleation_id || ''_'' || v_cjp_seq;'|| chr(10) ||
                      '   v_args(z) := ' || ' :p_query || '','' || :p_query_args || '','' || :p_collection_id || '','' || :p_thread_timeout || '','' || :p_transaction_id '  ||
                      v_dynamic_batch_vars || p_ghost_mt_args.batch_function_args || ';' || chr(10) ||
                      --'   v_args(z) := '''''''''''''''' || v_sql_query || '''''''''''' || '','' || ''NULL'' || '','' ||  v_last_size || '','' || v_size(z) || '',''|| '''' || v_driver_id(z) || '''' || '','' ||' || chr(10) ||
                      --'               v_cjp_seq || '','' || :p_collection_id || '','' || :p_new_collection_id || '','' || :p_child_timeout || '','' || :p_transaction_id || '''''''';'|| chr(10) ||
                      '   IF v_size IS NOT NULL THEN v_last_size := v_last_size + v_size(z); END IF;'|| chr(10) ||
--                      'dbms_output.put_line(v_args(z));'|| chr(10) ||
                      ' SELECT Seq_GHOSTCJP.NEXTVAL'|| chr(10) ||
                      '   INTO v_cjp_seq'|| chr(10) ||
                      '   FROM DUAL;'|| chr(10) ||
                      'END LOOP;'|| chr(10) ||
                      'ghost_jobs.execute_batch(v_jobs,v_args,:p_batch_program);' || chr(10) ||
                      'ghost_blob_util.plsql_dyn_return_value(v_iterations,:v_iterations);' || chr(10) ||
                      'EXCEPTION' || chr(10) ||
                      '  WHEN OTHERS THEN' || chr(10) ||
                      '  ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,' || chr(10) ||
                      '                                ghost_util.CONST_ERROR_MSG                                             ||'|| chr(10) ||
                      '                                ''MULTI_THREAD_PROCESS DYNAMIC_SQL '','|| chr(10) ||
                      '                                SQLERRM);' || chr(10) ||
                      'END;';

--       dbms_output.put_line(SUBSTR(v_sql_block,1,1000));
--       dbms_output.put_line(SUBSTR(v_sql_block,1001,2000));
       v_cursor:= dbms_sql.open_cursor;
       dbms_sql.parse(v_cursor, v_sql_block, dbms_sql.native);

       --Begin binding all the variables to the SQL
       --
       --dbms_sql.bind_variable(v_cursor, ':p_sql_query', p_ghost_mt_args.thread_sql_query.sql_query);
       IF p_ghost_mt_args.driver_query IS NOT NULL THEN
          IF p_ghost_mt_args.driver_query.sql_query IS NOT NULL THEN
             dbms_sql.bind_variable(v_cursor, ':p_driver_query', p_ghost_mt_args.driver_query.sql_query);
          ELSE
             dbms_sql.bind_variable(v_cursor, ':p_driver_query', '');

          END IF;

       ELSE
         dbms_sql.bind_variable(v_cursor, ':p_driver_query', '');

       END IF;


       dbms_sql.bind_variable(v_cursor, ':p_transaction_id', p_ghost_mt_args.transaction_id);
       dbms_sql.bind_variable(v_cursor, ':p_collection_id', p_ghost_mt_args.collection_id);
       dbms_sql.bind_variable(v_cursor, ':p_thread_timeout', p_ghost_mt_args.thread_timeout);
       dbms_sql.bind_variable(v_cursor, ':p_batch_job_name', p_ghost_mt_args.batch_job_name);
       dbms_sql.bind_variable(v_cursor, ':p_batch_job_modifiers', p_ghost_mt_args.batch_job_modifiers);
       dbms_sql.bind_variable(v_cursor, ':p_batch_program', p_ghost_mt_args.batch_function_name);
       dbms_sql.bind_variable(v_cursor, ':p_num_processed_rows', p_ghost_mt_args.num_processed_rows);
       dbms_sql.bind_variable(v_cursor, ':v_message_correleation_id', v_message_correleation_id);

       dbms_sql.bind_variable(v_cursor, ':p_query', ''''|| escape_string(p_ghost_mt_args.thread_sql_query.sql_query) || '''');
       dbms_sql.bind_variable(v_cursor, ':p_query_args', p_ghost_mt_args.thread_sql_query.arguments);

       --dbms_sql.bind_variable(v_cursor, ':v_cjp_seq', 0);
--       dbms_sql.bind_variable(v_cursor, ':v_uidvm', v_uidvm);
       dbms_sql.bind_variable(v_cursor, ':v_iterations', 0);

       -- Using the generated string: b_1, b_2, b_3 ...  included in the dynamic sql
       -- bind the values with the param values given.
       --
       IF v_dynamic_batch_vars IS NOT NULL THEN
         FOR i IN v_array_of_bind_values.FIRST..v_array_of_bind_values.LAST LOOP
           dbms_sql.bind_variable(v_cursor, CONST_BIND_VAR_PREFIX || i, v_array_of_bind_values(i));
    --         dbms_output.put_line('Bind Value: ' || v_array_of_bind_values(i));
         END LOOP;

       END IF;


       IF p_ghost_mt_args.custom_bind_args_names IS NOT NULL THEN
         ghost_util.bind_anydata_to_cursor(v_cursor,
                                           p_ghost_mt_args.custom_bind_args_names,
                                           p_ghost_mt_args.custom_bind_args_values);
       END IF;


       v_rows := dbms_sql.execute(v_cursor);

       DBMS_SQL.VARIABLE_VALUE(v_cursor, 'v_iterations', v_iterations);
--       DBMS_SQL.VARIABLE_VALUE(v_cursor, ':v_uidvm', v_uidvm);

       dbms_sql.close_cursor(v_cursor);

        v_dequeue_options.correlation := v_message_correleation_id;
        v_dequeue_options.wait := dbms_aq.no_wait;
        v_dequeue_options.visibility := dbms_aq.IMMEDIATE;
        v_dequeue_options.navigation := dbms_aq.first_message;

        ghost_blob_util.poll_ghost_child_jobs(v_iterations,
                                              v_dequeue_options,
                                              v_message_properties,
                                              v_message,
                                              v_message_handle,
                                              p_ghost_mt_args.timeout);
       p_message := v_message;

       RETURN v_iterations;
       EXCEPTION
            WHEN OTHERS THEN
               IF v_cursor!=0 THEN
                  dbms_sql.close_cursor(v_cursor);
               END IF;

               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                     ghost_util.CONST_ERROR_MSG                                             ||
                                     'MULTI_THREAD_PROCESS '                                                ||
                                     ' batch_job_name:' || ghost_util.wrap_error_params(p_ghost_mt_args.batch_job_name) ||
                                     ' batch_function_name:' || ghost_util.wrap_error_params(p_ghost_mt_args.batch_function_name),
                                     SQLERRM);

END multi_thread_process;



FUNCTION wrap_in_quote_mt(p_string VARCHAR2) RETURN VARCHAR2 IS
BEGIN
  RETURN ' '''''''' || ' || p_string ||  ' || '''''''' ';
  EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                       ghost_util.CONST_ERROR_MSG                                     ||
                                       'WRAP_IN_QUOTE_MT '                                            ||
                                       ' p_string:' || ghost_util.wrap_error_params(p_string),
                                       SQLERRM);

END wrap_in_quote_mt;



FUNCTION aggregate_by(p_driver_query IN VARCHAR2,
                      p_driver_args IN VARCHAR2,
                      p_update_driver_query IN VARCHAR2,
                      p_update_driver_args IN VARCHAR2,
                      p_sql_query IN VARCHAR2,
                      p_sql_args IN VARCHAR2,
                      p_collection_id IN NUMBER,
                      p_new_collection_id IN NUMBER,
                      p_num_processed_rows IN NUMBER,
                      p_timeout IN NUMBER,
                      p_thread_timeout IN NUMBER,
                      p_transaction_id IN NUMBER,
                      p_custom_columns IN VARCHAR2) RETURN NUMBER IS

        v_mt_args ghost_mt_arguments;
        v_job_count NUMBER;
        v_message ghost_job_child_obj;

 BEGIN

        v_mt_args := ghost_mt_arguments();

        v_mt_args.driver_query.sql_query := p_driver_query;
        v_mt_args.driver_query.arguments := p_driver_args;
        v_mt_args.update_query.sql_query := p_update_driver_query;
        v_mt_args.update_query.arguments := p_update_driver_args;
        v_mt_args.thread_sql_query.sql_query := p_sql_query;
        v_mt_args.thread_sql_query.arguments := NVL(p_sql_args,'''''');
        v_mt_args.collection_id := p_collection_id;
        v_mt_args.new_collection_id := p_new_collection_id;
        v_mt_args.batch_job_name := 'GB_AGGBY_';
        v_mt_args.batch_function_name := 'GHOST_BLOB_UTIL.AGGREGATE_BY_BATCH';
        v_mt_args.batch_function_bind_args := p_new_collection_id;

        v_mt_args.batch_function_args := CONST_MT_ARG_SEPERATOR ||
                                         CONST_MT_LAST_SIZE_FIELD ||
                                         CONST_MT_ARG_SEPERATOR ||
                                         CONST_MT_CURRENT_SIZE_FIELD ||
                                         CONST_MT_ARG_SEPERATOR ||
                                         wrap_in_quote_mt('''' ||p_custom_columns || '''') ||
                                         CONST_MT_ARG_SEPERATOR ||
                                         wrap_in_quote_mt(CONST_MT_DRIVER_FIELD) ||
                                         CONST_MT_ARG_SEPERATOR ||
                                         CONST_MT_CHILD_SEQUENCE_FIELD;
        v_mt_args.num_processed_rows := p_num_processed_rows;
        v_mt_args.timeout := p_timeout;
        v_mt_args.thread_timeout := p_thread_timeout;
        v_mt_args.transaction_id := p_transaction_id;

        v_job_count:= multi_thread_process(v_mt_args, v_message);

        RETURN v_job_count;
        EXCEPTION
                  WHEN OTHERS THEN
                    ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                          ghost_util.CONST_ERROR_MSG                                             ||
                                          'AGGREGATE_BY '                                                ||
                                          --' p_driver_args:' || ghost_util.wrap_error_params(p_driver_args) ||
                                          --' p_update_driver_query:' || ghost_util.wrap_error_params(p_update_driver_query) ||
                                          --' p_update_driver_args:' || ghost_util.wrap_error_params(p_update_driver_args) ||
                                          --' p_sql_query:' || ghost_util.wrap_error_params(p_sql_query) ||
                                          --' p_sql_args:' || ghost_util.wrap_error_params(p_sql_args) ||
                                          ' p_collection_id:' || ghost_util.wrap_error_params(p_collection_id) ||
                                          ' p_new_collection_id:' || ghost_util.wrap_error_params(p_new_collection_id) ||
                                          ' p_num_processed_rows:' || ghost_util.wrap_error_params(p_num_processed_rows) ||
                                          ' p_timeout:' || ghost_util.wrap_error_params(p_timeout) ||
                                          ' p_thread_timeout:' || ghost_util.wrap_error_params(p_thread_timeout) ||
                                          ' p_transaction_id:' || ghost_util.wrap_error_params(p_transaction_id) ,
                                          --' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns),
                                          SQLERRM);

 END aggregate_by;



/*
FUNCTION aggregate_by(p_driver_query IN VARCHAR2,
                      p_update_driver_query IN VARCHAR2,
                      p_sql_query IN VARCHAR2,
                      p_collection_id IN NUMBER,
                      p_new_collection_id IN NUMBER,
                      p_num_processed_rows IN NUMBER,
                      p_timeout IN NUMBER,
                      p_child_timeout IN NUMBER,
                      p_transaction_id IN NUMBER) RETURN NUMBER IS
--        v_start_pos NUMBER;
--        v_end_pos NUMBER;

        vJobs ghost_jobs.tArrayJob;
        vArgs ghost_jobs.tArrayJob;

        vJobsFinal ghost_jobs.tArrayJob;
        vArgsFinal ghost_jobs.tArrayJob;

        v_dequeue_options     dbms_aq.dequeue_options_t;
        v_message_properties  dbms_aq.message_properties_t;
        v_message_handle      RAW(16);
        v_message             ghost_job_child_obj;
--        v_num_messages NUMBER :=0;

        v_iterations NUMBER := 0;
--        p_batchsize NUMBER := 0;
--        v_max_job_size NUMBER := 0;
        v_cjp_seq NUMBER;

        v_sql_query VARCHAR2(4000);
        v_save_query VARCHAR2(4000);

        v_driver_id tab_varchar2;
        v_size ghost_tab_number;
--        v_offset ghost_tab_number;
        cur_header t_refcursor;

        v_bulk_ids ghost_tab_number;

        v_last_size NUMBER;
        v_bulk_insert_id NUMBER;

 BEGIN
--       dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);

       EXECUTE IMMEDIATE p_update_driver_query;
       COMMIT;

       IF NOT cur_header%ISOPEN THEN
         OPEN cur_header FOR p_driver_query;
              FETCH cur_header BULK COLLECT INTO v_driver_id, v_size;
         CLOSE cur_header;
       END IF;
--      dbms_output.put_line('driver size :' || v_size.COUNT);
--      dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);

-- dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);

       v_iterations := v_size.COUNT();
--       dbms_output.put_line('Num of processed rows :' || v_num_processed_rows);

       SELECT Seq_GHOSTCJP.NEXTVAL, Seq_GHOSTBIID.NEXTVAL
         INTO v_cjp_seq, v_bulk_insert_id
         FROM DUAL;

       v_sql_query := REPLACE(p_sql_query,'''','''''');
      -- v_save_query := REPLACE(p_save_query,'''','''''');

       v_last_size := 0;
       --v_iterations := 102;
--       dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);
       FOR z in 1..v_iterations LOOP

       --dbms_output.put_line('Offset:' || v_offset(z));
           vJobs(z) := 'ghost_batch_' || p_transaction_id ||'_' || v_cjp_seq;-- || '_' || v_driver_id(z);
           vArgs(z) := '''' || v_sql_query || '''' || ',' || 'NULL' || ',' ||  v_last_size || ',' || v_size(z) || ','|| '''' || v_driver_id(z) || '''' || ',' ||
                       v_cjp_seq || ',' || p_collection_id || ',' || p_new_collection_id || ',' || p_child_timeout || ',' || p_transaction_id || ',' || v_bulk_insert_id;
           v_last_size := v_last_size + v_size(z);
           --dbms_output.put_line('Iterations:' || vJobs(z));
           --dbms_output.put_line('Iterations:' || vArgs(z));
         SELECT Seq_GHOSTCJP.NEXTVAL
         INTO v_cjp_seq
         FROM DUAL;
        END LOOP;

--dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);
        ghost_jobs.ExecuteBatch(vJobs,vArgs);
--dbms_output.put_line('After execute jobs Timestamp:' || SYSTIMESTAMP);
--        dbms_output.put_line('Iterations:' || v_iterations);

        v_dequeue_options.correlation := v_cjp_seq;
        v_dequeue_options.wait := dbms_aq.no_wait;
        v_dequeue_options.visibility := dbms_aq.IMMEDIATE;
        v_dequeue_options.navigation := dbms_aq.first_message;

        v_bulk_ids := ghost_tab_number();
--        v_bulk_ids.extend(v_iterations);
--  dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);
        poll_ghost_batch_child_jobs(p_num_processed_rows,
                                    v_dequeue_options,
                                    v_message_properties,
                                    v_message,
                                    v_message_handle,
                                    p_timeout,
                                    v_bulk_ids);

--dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);

        SELECT Seq_GHOSTCJP.NEXTVAL
         INTO v_cjp_seq
         FROM DUAL;
/*
dbms_output.put_line('Bulk IDs:' || v_bulk_ids.COUNT);
FOR k in 1..v_bulk_ids.COUNT LOOP
 dbms_output.put_line('Bulk ID ' || k || ':' || v_bulk_ids(k));
END LOOP;
*/

/*
 --      dbms_output.put_line('Bulk IDs:' || v_bulk_ids.COUNT);
--dbms_output.put_line('Schedule final jobs Timestamp:' || SYSTIMESTAMP);
        FOR k in 1..v_bulk_ids.COUNT LOOP
--           dbms_output.put_line('K:' || k);
           vJobsFinal(k) := 'ghost_final_agg_' || p_transaction_id ||'_' || v_cjp_seq || '_' || v_bulk_ids(k);
           vArgsFinal(k) := v_cjp_seq || ',' || v_bulk_ids(k) || ',' || p_transaction_id || ',' || v_bulk_insert_id;
        END LOOP;
--        dbms_output.put_line('Final Job size:' || vJobsFinal.COUNT);
--        dbms_output.put_line('Before execute Timestamp:' || SYSTIMESTAMP);
        ghost_jobs.Execute(vJobsFinal,vArgsFinal);
--        dbms_output.put_line('After final jobs:' || SYSTIMESTAMP);

        v_dequeue_options.correlation := v_cjp_seq;
        v_dequeue_options.wait := dbms_aq.no_wait;
        v_dequeue_options.visibility := dbms_aq.IMMEDIATE;
        v_dequeue_options.navigation := dbms_aq.first_message;

        poll_ghost_child_jobs(v_bulk_ids.COUNT,
                              v_dequeue_options,
                              v_message_properties,
                              v_message,
                              v_message_handle,
                              300);
--        dbms_output.put_line('AFter poll before insert:' || SYSTIMESTAMP);

        COMMIT;
--        dbms_output.put_line('After insert Timestamp:' || SYSTIMESTAMP);
        RETURN v_bulk_ids.COUNT;
        EXCEPTION
                  WHEN OTHERS THEN
                    IF cur_header%ISOPEN THEN
                       CLOSE cur_header;
                    END IF;
                    ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                          ghost_util.CONST_ERROR_MSG                                             ||
                                          'AGGREGATE_BY '                                                ||
                                          ' p_transaction_id:' || ghost_util.wrap_error_params(p_transaction_id),
                                          SQLERRM);
 END aggregate_by;
*/


FUNCTION batch_process(p_driver_query IN VARCHAR2,
                       p_sql_query IN VARCHAR2,
                       p_save_query IN VARCHAR2,
                       p_possession_id IN NUMBER,
                       p_timeout IN NUMBER,
                       p_child_timeout IN NUMBER,
                       p_transaction_id IN NUMBER) RETURN NUMBER IS
--        v_start_pos NUMBER;
--        v_end_pos NUMBER;

        vJobs ghost_jobs.tArrayJob;
        vArgs ghost_jobs.tArrayJob;

        vJobsFinal ghost_jobs.tArrayJob;
        vArgsFinal ghost_jobs.tArrayJob;

        v_dequeue_options     dbms_aq.dequeue_options_t;
        v_message_properties  dbms_aq.message_properties_t;
        v_message_handle      RAW(16);
        v_message             ghost_job_child_obj;
--        v_num_messages NUMBER :=0;

        v_iterations NUMBER := 0;
--        p_batchsize NUMBER := 0;
--        v_max_job_size NUMBER := 0;
        v_cjp_seq NUMBER;

        v_sql_query VARCHAR2(4000);
        v_save_query VARCHAR2(4000);

        v_driver_id ghost_tab_varchar2;
        v_size ghost_tab_number;
--        v_offset ghost_tab_number;
        cur_header t_refcursor;

        v_bulk_ids ghost_tab_number;

        v_last_size NUMBER;
        v_num_processed_rows NUMBER;
        v_bulk_insert_id NUMBER;

 BEGIN
--       dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);

       IF NOT cur_header%ISOPEN THEN
         OPEN cur_header FOR p_driver_query;
              FETCH cur_header BULK COLLECT INTO v_driver_id, v_size;
         CLOSE cur_header;
       END IF;

--      dbms_output.put_line('driver size :' || v_size.COUNT);
--      dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);

        SELECT COUNT(0)
          INTO v_num_processed_rows
          FROM ghost_vm
         WHERE GHOST_POSSESSION_ID = p_possession_id;

-- dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);

       v_iterations := v_size.COUNT();
--       dbms_output.put_line('Num of processed rows :' || v_num_processed_rows);

       SELECT Seq_GHOSTCJP.NEXTVAL, Seq_GHOSTBIID.NEXTVAL
         INTO v_cjp_seq, v_bulk_insert_id
         FROM DUAL;


       v_sql_query := REPLACE(p_sql_query,'''','''''');
       v_save_query := REPLACE(p_save_query,'''','''''');

       v_last_size := 0;
       --v_iterations := 102;
--       dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);
       FOR z in 1..v_iterations LOOP
       --dbms_output.put_line('Offset:' || v_offset(z));
           vJobs(z) := 'ghost_batch_' || p_transaction_id ||'_' || v_cjp_seq || '_' || v_driver_id(z);
           vArgs(z) := '''' || v_sql_query || '''' || ',' || '''' || v_save_query || '''' || ',' || v_last_size || ',' || v_size(z) || ',' || v_driver_id(z) || ',' ||
                       v_cjp_seq || ',' || p_possession_id || ',' || p_child_timeout || ',' || p_transaction_id || ',' || v_bulk_insert_id;
           v_last_size := v_last_size + v_size(z);
--           dbms_output.put_line('Iterations:' || vJobs(z));
--           dbms_output.put_line('Iterations:' || vArgs(z));
        END LOOP;

--dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);
--        ghost_jobs.ExecuteBatch(vJobs,vArgs);
--dbms_output.put_line('After execute jobs Timestamp:' || SYSTIMESTAMP);
--        dbms_output.put_line('Iterations:' || v_iterations);

        v_dequeue_options.correlation := v_cjp_seq;
        v_dequeue_options.wait := dbms_aq.no_wait;
        v_dequeue_options.visibility := dbms_aq.IMMEDIATE;
        v_dequeue_options.navigation := dbms_aq.first_message;

        v_bulk_ids := ghost_tab_number();
--        v_bulk_ids.extend(v_iterations);
--  dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);
        poll_ghost_batch_child_jobs(v_num_processed_rows,
                                    v_dequeue_options,
                                    v_message_properties,
                                    v_message,
                                    v_message_handle,
                                    p_timeout,
                                    v_bulk_ids);

--dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);

        SELECT Seq_GHOSTCJP.NEXTVAL
         INTO v_cjp_seq
         FROM DUAL;

/*
dbms_output.put_line('Bulk IDs:' || v_bulk_ids.COUNT);
FOR k in 1..v_bulk_ids.COUNT LOOP
 dbms_output.put_line('Bulk ID ' || k || ':' || v_bulk_ids(k));
END LOOP;
*/


 --      dbms_output.put_line('Bulk IDs:' || v_bulk_ids.COUNT);
--dbms_output.put_line('Schedule final jobs Timestamp:' || SYSTIMESTAMP);
        FOR k in 1..v_bulk_ids.COUNT LOOP
--           dbms_output.put_line('K:' || k);
           vJobsFinal(k) := 'ghost_final_agg_' || p_transaction_id ||'_' || v_cjp_seq || '_' || v_bulk_ids(k);
           vArgsFinal(k) := v_cjp_seq || ',' || v_bulk_ids(k) || ',' || p_transaction_id || ',' || v_bulk_insert_id;
        END LOOP;

--        dbms_output.put_line('Final Job size:' || vJobsFinal.COUNT);
--        dbms_output.put_line('Before execute Timestamp:' || SYSTIMESTAMP);
--        ghost_jobs.Execute(vJobsFinal,vArgsFinal);
--        dbms_output.put_line('After final jobs:' || SYSTIMESTAMP);

        v_dequeue_options.correlation := v_cjp_seq;
        v_dequeue_options.wait := dbms_aq.no_wait;
        v_dequeue_options.visibility := dbms_aq.IMMEDIATE;
        v_dequeue_options.navigation := dbms_aq.first_message;

        poll_ghost_child_jobs(v_bulk_ids.COUNT,
                              v_dequeue_options,
                              v_message_properties,
                              v_message,
                              v_message_handle,
                              300);
--        dbms_output.put_line('AFter poll before insert:' || SYSTIMESTAMP);

        EXECUTE IMMEDIATE p_save_query USING v_bulk_insert_id;
        COMMIT;
--        dbms_output.put_line('After insert Timestamp:' || SYSTIMESTAMP);
        RETURN v_bulk_insert_id;
        EXCEPTION
                  WHEN OTHERS THEN
                    IF cur_header%ISOPEN THEN
                       CLOSE cur_header;
                    END IF;

                    ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                          ghost_util.CONST_ERROR_MSG                                             ||
                                          'BATCH_PROCESS '                                                ||
                                          ' p_transaction_id:' || ghost_util.wrap_error_params(p_transaction_id) ||
                                          ' p_possession_id:' || ghost_util.wrap_error_params(p_possession_id),
                                          SQLERRM);

 END batch_process;



FUNCTION aggregate_blob_small(p_sql_query IN VARCHAR2,
                              p_size IN NUMBER,
                              p_transaction_id IN NUMBER) RETURN NUMBER IS
        cur_header t_refcursor;
        v_uidvm NUMBER;
        v_blob_table ghost_tab_blob;
        v_sql_query VARCHAR2(32000);

   BEGIN
         v_sql_query := REPLACE(p_sql_query,'''','''''');
--     dbms_output.put_line('Timestamp Open cursor:' || SYSTIMESTAMP);
      IF NOT cur_header%ISOPEN THEN
           OPEN cur_header FOR 'SELECT b_value FROM (' || v_sql_query || ')';
            FETCH cur_header BULK COLLECT INTO v_blob_table;
            CLOSE cur_header;
       END IF;

--       dbms_output.put_line('Timestamp Start adding:' || SYSTIMESTAMP);
      v_uidvm := blob_bulk_operation(v_blob_table,p_transaction_id,NULL,NULL, NULL,NULL,NULL);
--      dbms_output.put_line('Timestamp End adding:' || SYSTIMESTAMP);
      COMMIT;
      RETURN v_uidvm;

      EXCEPTION
         WHEN OTHERS THEN
            IF cur_header%ISOPEN THEN
               CLOSE cur_header;
            END IF;

            ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                          ghost_util.CONST_ERROR_MSG                                             ||
                                          'AGGREGATE_BLOB_SMALL '                                                ||
                                          ' p_transaction_id:' || ghost_util.wrap_error_params(p_transaction_id) ||
                                          ' p_size:' || ghost_util.wrap_error_params(p_size) ||
                                          ' p_sql_query:' || ghost_util.wrap_error_params(p_sql_query),
                                          SQLERRM);

   END aggregate_blob_small;


/*
FUNCTION get_driver_id(p_params IN VARCHAR2) RETURN VARCHAR2 AS
       v_temp VARCHAR2(1000);
       v_element VARCHAR2(1000);
       v_seperator_count NUMBER:=1;
       v_count NUMBER:=0;
       v_result VARCHAR2(5000);
    BEGIN

       v_temp:= p_params;

       WHILE (v_seperator_count!=0) LOOP
                v_seperator_count:= INSTR(v_temp,CONST_CUSTOMCOL_VALUE_SEP);

                IF (v_seperator_count!=0) THEN
                   v_element := SUBSTR(v_temp,1,v_seperator_count - 1);
                   v_count := v_count + 1;
                ELSE
                   IF  (LENGTH(v_temp)>0) THEN
                       v_element:= v_temp;
                       v_count := v_count + 1;
                   ELSE
                       RETURN 0;
                   END IF;
                END IF;

                v_result := v_result || v_element;
                v_temp:=SUBSTR(v_temp,INSTR(v_temp,CONST_CUSTOMCOL_VALUE_SEP)+1);

                IF v_temp IS NULL THEN
                   v_seperator_count:=0; --Exit Condition
                END IF;
       END LOOP;

       RETURN v_result;

       EXCEPTION
          WHEN OTHERS THEN
           ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                              ghost_util.CONST_ERROR_MSG                                             ||
                                              'GET_DRIVER_ID '                                          ||
                                              ' p_params:' || ghost_util.wrap_error_params(p_params),
                                              SQLERRM);

   END get_driver_id;
   */


FUNCTION aggregate_batch_blob_small(p_cjp_seq IN NUMBER,
                                    p_sql_query IN VARCHAR2,
                                    p_sql_query_args IN VARCHAR2,
                                    p_size IN NUMBER,
                                    p_transaction_id IN NUMBER,
                                    p_new_collection_id IN NUMBER,
                                    p_custom_columns IN VARCHAR2,
                                    p_custom_column_values IN VARCHAR2) RETURN NUMBER IS
        v_uidvm NUMBER;
        v_blob_table ghost_tab_blob;
        v_blob_insert_blob ghost_tab_blob;
        v_range_table ghost_tab_number;
        v_uidvm_table ghost_tab_number;
        enqueue_options     dbms_aq.enqueue_options_t;
        message_properties  dbms_aq.message_properties_t;
        message_handle      RAW(16);
        v_sql_block VARCHAR2(4000);
        v_cursor INTEGER;
        v_rows NUMBER;
        v_ref_cursor   SYS_REFCURSOR;
        v_driver_id VARCHAR2(1000);
        v_ref_cursor_closed BOOLEAN := FALSE;
        v_sql_query VARCHAR2(32000);

   BEGIN

         v_cursor := dbms_sql.open_cursor;
         v_sql_query := REPLACE(p_sql_query,'''','''''');
         dbms_sql.parse(v_cursor,v_sql_query,dbms_sql.native);

         --Binds here
--         v_driver_id := get_driver_id(p_custom_column_values);
         dbms_sql.bind_variable(v_cursor, ':b', p_custom_column_values);

         v_rows := dbms_sql.execute(v_cursor);


         v_ref_cursor := dbms_sql.to_refcursor(v_cursor);
         FETCH v_ref_cursor BULK COLLECT INTO v_range_table, v_blob_table, v_uidvm_table, v_blob_insert_blob;
         CLOSE v_ref_cursor;
         v_ref_cursor_closed := TRUE;

         IF v_cursor !=0 THEN
               dbms_sql.close_cursor(v_cursor);
         END IF;


         IF v_blob_table.COUNT =0 THEN
            RAISE e_no_records_match;
         END IF;


         v_uidvm := ghost_blob_util.blob_bulk_operation(v_blob_table,p_transaction_id, NULL,p_new_collection_id,NULL, p_custom_columns,p_custom_column_values);

         message_properties.correlation := p_cjp_seq;


         dbms_aq.enqueue(queue_name => 'ghost_queue',
                         enqueue_options      => enqueue_options,
                         message_properties   => message_properties,
                         payload              => ghost_job_child_obj('AGGBY_SMALL','SUCCESS',v_uidvm,1,p_size, p_size, NULL, p_custom_column_values,SQLERRM),
                         msgid                => message_handle);

         RETURN v_uidvm;

      EXCEPTION
         WHEN e_no_records_match THEN
            IF v_cursor !=0 THEN
               dbms_sql.close_cursor(v_cursor);
            END IF;

            IF v_ref_cursor IS NOT NULL AND NOT v_ref_cursor_closed THEN
               CLOSE v_ref_cursor;
            END IF;

            message_properties.correlation := p_cjp_seq;
            dbms_aq.enqueue(queue_name => 'ghost_queue',
                           enqueue_options      => enqueue_options,
                           message_properties   => message_properties,
                           payload              => ghost_job_child_obj('AGGBY_SMALL','ERROR',v_uidvm,1,p_size, p_size, NULL, p_custom_column_values,'No records found!' ),
                           msgid                => message_handle);
/*
            ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                          ghost_util.CONST_ERROR_MSG                                             ||
                                          'AGGREGATE_BATCH_BLOB_SMALL : No Records Found! '                                          ||
                                          ' p_transaction_id:' || ghost_util.wrap_error_params(p_transaction_id) ||
                                          ' p_size:' || ghost_util.wrap_error_params(p_size)                     ||
                                          ' p_driver_id:' || ghost_util.wrap_error_params(v_driver_id),
                                          SQLERRM);
                                          */

         WHEN OTHERS THEN
            IF v_cursor !=0 THEN
               dbms_sql.close_cursor(v_cursor);
            END IF;

            IF v_ref_cursor IS NOT NULL AND NOT v_ref_cursor_closed THEN
               CLOSE v_ref_cursor;
            END IF;

            message_properties.correlation := p_cjp_seq;
            dbms_aq.enqueue(queue_name => 'ghost_queue',
                           enqueue_options      => enqueue_options,
                           message_properties   => message_properties,
                           payload              => ghost_job_child_obj('AGGBY_SMALL','ERROR',v_uidvm,1,p_size, p_size, NULL, p_custom_column_values,SQLERRM),
                           msgid                => message_handle);
/*
            ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                          ghost_util.CONST_ERROR_MSG                                             ||
                                          'AGGREGATE_BATCH_BLOB_SMALL '                                          ||
                                          ' p_transaction_id:' || ghost_util.wrap_error_params(p_transaction_id) ||
                                          ' p_size:' || ghost_util.wrap_error_params(p_size)                     ||
                                          ' p_driver_id:' || ghost_util.wrap_error_params(v_driver_id),
                                          SQLERRM);
                                          */

   END aggregate_batch_blob_small;



PROCEDURE aggregate_by_batch(p_sql_query IN VARCHAR2,
                              p_sql_args IN VARCHAR2,
                              p_collection_id IN NUMBER,
                              p_thread_timeout IN NUMBER,
                              p_transaction_id IN NUMBER,
                              p_new_collection_id IN NUMBER,
                              p_last_index IN NUMBER,
                              p_size IN NUMBER,
                              p_custom_columns IN VARCHAR2,
                              p_custom_column_values IN VARCHAR2,
                              p_cjp_seq IN NUMBER) IS

        enqueue_options     dbms_aq.enqueue_options_t;
        message_properties  dbms_aq.message_properties_t;
        message_handle      RAW(16);

        v_bulk_id NUMBER;
        v_final_uidvm NUMBER;
        v_blob_table ghost_tab_blob;

        v_iterations NUMBER := 0;
        v_batchsize NUMBER := 0;
        v_max_job_size NUMBER := 0;
        v_uidvm NUMBER;

        v_mt_args ghost_mt_arguments;
        v_job_count NUMBER;
        v_message ghost_job_child_obj;

 BEGIN

--       v_driver_id := get_driver_id(p_custom_column_values);
       IF(p_size <= 100) THEN
           BEGIN
             v_uidvm:=aggregate_batch_blob_small(p_cjp_seq,
                                                 p_sql_query,
                                                 p_sql_args,
                                                 p_size,
                                                 p_transaction_id,
                                                 p_new_collection_id,
                                                 p_custom_columns,
                                                 p_custom_column_values);
                  EXCEPTION
                 WHEN OTHERS THEN
                      NULL; --Let the sub fucntion handle it's own error handling by sending the correct message out.

            END;

  --           COMMIT;
       ELSE
          SELECT Seq_GHOSTBIDVM.NEXTVAL
            INTO v_bulk_id
            FROM DUAL;


           CASE
              WHEN p_size<=10000 THEN v_max_job_size := p_size * 0.02;

              WHEN p_size<=20000 THEN v_max_job_size := p_size * 0.01;

              WHEN p_size<=50000 THEN v_max_job_size := p_size * 0.005;

              ELSE
                   v_max_job_size := p_size * 0.005;

           END CASE;


           v_max_job_size := CEIL(v_max_job_size);

           v_batchsize := CEIL(p_size/ v_max_job_size);
           --dbms_output.put_line('BatchSize:' || p_batchsize);
           IF (v_batchsize <=1) THEN
               v_batchsize := p_size;
           END IF;

           --dbms_output.put_line('AFter BatchSize:' || p_batchsize);
           --v_iterations := CEIL(v_size(x)/ p_batchsize);
           v_iterations := CEIL(p_size/ v_batchsize);


           v_mt_args := ghost_mt_arguments();

--        v_mt_args.driver_query.sql_query := p_driver_query;

--        v_mt_args.driver_query.arguments := p_driver_args;
--        v_mt_args.update_query.sql_query := p_update_driver_query;
--        v_mt_args.update_query.arguments := p_update_driver_args;
        v_mt_args.thread_sql_query.sql_query := p_sql_query;
        v_mt_args.thread_sql_query.arguments := NVL(p_sql_args,'''''');
        --v_mt_args.collection_id := p_new_collection_id;
        --v_mt_args.new_collection_id := p_new_collection_id;
        v_mt_args.batch_job_name := 'G_AGGBY_T_';
        v_mt_args.batch_function_name := 'GHOST_BLOB_UTIL.AGGREGATE_BY_THREAD';
        --v_mt_args.batch_function_bind_args := p_new_collection_id;
        /*
        p_drive_id IN VARCHAR2,
                                  p_start_index IN NUMBER,
                                  p_end_index IN NUMBER,
                                  p_cjp_seq IN NUMBER,
                                  p_bulk_id
                                  */


        v_mt_args.batch_function_args := CONST_MT_ARG_SEPERATOR ||
                                         wrap_in_quote_mt('''' || p_custom_columns || '''') ||
                                         CONST_MT_ARG_SEPERATOR ||
                                         wrap_in_quote_mt('''' || p_custom_column_values || '''') ||
                                         CONST_MT_ARG_SEPERATOR ||
                                         'v_start_pos' ||
                                         CONST_MT_ARG_SEPERATOR ||
                                         'v_end_pos' ||
                                         CONST_MT_ARG_SEPERATOR ||
                                         CONST_MT_CHILD_SEQUENCE_FIELD ||
                                         CONST_MT_ARG_SEPERATOR ||
                                         v_bulk_id;

        v_mt_args.num_processed_rows := v_iterations;
--        v_mt_args.timeout := p_timeout;
        v_mt_args.thread_timeout := p_thread_timeout;
        v_mt_args.transaction_id := p_transaction_id;

        v_mt_args.custom_declare_block := 'v_start_pos NUMBER; v_end_pos NUMBER;';
        v_mt_args.custom_loop_block := 'v_start_pos := :p_offset + ((z-1)*:p_batchsize) + 1;' || chr(10) ||
                                       'v_end_pos := :p_offset + (z*:p_batchsize);';

        v_mt_args.custom_bind_args_names.extend(2);
        v_mt_args.custom_bind_args_names(1):= ':p_offset';
        v_mt_args.custom_bind_args_names(2):= ':p_batchsize';

        v_mt_args.custom_bind_args_values.extend(2);
        v_mt_args.custom_bind_args_values(1):= ghost_util.convert_number_to_anydata(p_last_index);
        v_mt_args.custom_bind_args_values(2):= ghost_util.convert_varchar_to_anydata(v_batchsize);

        v_job_count := multi_thread_process(v_mt_args, v_message);


        --Start of final aggregation. Aggregate all sub block into one final agg

                SELECT blob_value
     BULK COLLECT INTO v_blob_table
                  FROM ghost_vm
                 WHERE bulk_id = v_bulk_id;


        v_final_uidvm := blob_bulk_operation(v_blob_table,p_transaction_id,NULL,p_new_collection_id,NULL,NULL,NULL);

        DELETE FROM ghost_vm
              WHERE bulk_id = v_bulk_id;


        COMMIT;

        message_properties.correlation := p_cjp_seq;
        dbms_aq.enqueue(queue_name => 'ghost_queue',
                        enqueue_options      => enqueue_options,
                        message_properties   => message_properties,
                        payload              => ghost_job_child_obj('AGGBY_B','SUCCESS',NULL,NULL,NULL,p_size, v_bulk_id, p_custom_column_values,SQLERRM),
                        msgid                => message_handle);

        END IF;

/*
           FOR z in 1..v_iterations LOOP
               v_start_pos := p_offset + ((z-1)*p_batchsize) + 1;
               v_end_pos := p_offset + (z*p_batchsize);
               vJobs(z) := 'ghost_' || p_transaction_id ||'_' || p_cjp_seq || '_' || v_start_pos || v_end_pos;
               vArgs(z) := '''' || p_sql_query || '''' || ',' || '''' || v_driver_id || '''' || ',' || p_possession_id || ',' || p_new_collection_id || ',' ||
                           v_start_pos || ',' || v_end_pos || ',' || p_cjp_seq || ',' || p_transaction_id || ',' || v_bulk_id;
            END LOOP;
            */



     COMMIT;

    EXCEPTION
      WHEN OTHERS THEN
          message_properties.correlation := p_cjp_seq;
          dbms_aq.enqueue(queue_name => 'ghost_queue',
                          enqueue_options      => enqueue_options,
                          message_properties   => message_properties,
                          payload              => ghost_job_child_obj('AGGBY_B','ERROR',NULL,NULL,NULL,p_size, v_bulk_id, p_custom_column_values,SQLERRM),
                          msgid                => message_handle);
          COMMIT;

 END aggregate_by_batch;



FUNCTION aggregate_blob(p_sql_query IN VARCHAR2,
                        p_size IN NUMBER,
                        p_collection IN NUMBER,
                        p_timeout IN NUMBER,
                        p_transaction_id IN NUMBER) RETURN NUMBER IS
        v_final_uidvm NUMBER;
        v_bulk_id NUMBER;

        v_blob_table ghost_tab_blob;

        v_iterations NUMBER := 0;
        v_batchsize NUMBER := 0;
        v_max_job_size NUMBER := 0;
        v_size NUMBER;
        v_cjp_seq NUMBER;

        v_mt_args ghost_mt_arguments;
        v_job_count NUMBER;
        v_message ghost_job_child_obj;

 BEGIN
--       dbms_output.put_line('Timestamp:' || SYSTIMESTAMP);
--       IF(p_size <= 100) THEN
--           RETURN aggregate_blob_small(p_sql_query,p_size,p_transaction_id);
--       END IF;

        SELECT Seq_GHOSTBIDVM.NEXTVAL
          INTO v_bulk_id
          FROM DUAL;


       CASE
          WHEN p_size<=10000 THEN v_max_job_size := p_size * 0.02;

          WHEN p_size<=20000 THEN v_max_job_size := p_size * 0.01;

          WHEN p_size<=50000 THEN v_max_job_size := p_size * 0.005;

          ELSE
               v_max_job_size := p_size * 0.005;
               IF v_max_job_size > 1000 THEN
                  v_max_job_size := 750;
               END IF;

       END CASE;


       v_max_job_size := CEIL(v_max_job_size);

       v_batchsize := CEIL(p_size/ v_max_job_size);
       --dbms_output.put_line('BatchSize:' || p_batchsize);
       IF (v_batchsize <=1) THEN
           v_batchsize := p_size;
       END IF;

       --dbms_output.put_line('AFter BatchSize:' || p_batchsize);
       --v_iterations := CEIL(v_size(x)/ p_batchsize);
       v_iterations := CEIL(p_size/ v_batchsize);

       UPDATE ghost_vm
          SET range_id = ROWNUM
        WHERE BITAND(ghost_collection_id,p_collection) = p_collection;


       --dbms_output.put_line('Iterations:' || v_iterations);

        v_mt_args := ghost_mt_arguments();

        v_mt_args.thread_sql_query.sql_query := p_sql_query;
        v_mt_args.thread_sql_query.arguments := '''''';
        v_mt_args.collection_id := p_collection;
        v_mt_args.batch_job_name := 'G_AGG_T_';
        v_mt_args.batch_job_modifiers := p_transaction_id;
        v_mt_args.batch_function_name := 'GHOST_BLOB_UTIL.AGGREGATE_THREAD';

        v_mt_args.batch_function_args := CONST_MT_ARG_SEPERATOR ||
                                         'v_start_pos' ||
                                         CONST_MT_ARG_SEPERATOR ||
                                         'v_end_pos' ||
                                         CONST_MT_ARG_SEPERATOR ||
                                         CONST_MT_CHILD_SEQUENCE_FIELD ||
                                         CONST_MT_ARG_SEPERATOR ||
                                         v_bulk_id;

        v_mt_args.num_processed_rows := v_iterations;
        v_mt_args.timeout := p_timeout;
        v_mt_args.thread_timeout := CEIL(p_timeout/v_iterations);
        v_mt_args.transaction_id := p_transaction_id;

        v_mt_args.custom_declare_block := 'v_start_pos NUMBER; v_end_pos NUMBER;';
        v_mt_args.custom_loop_block := 'v_start_pos := ((z-1)*:p_batchsize) + 1;' || chr(10) ||
                                       'v_end_pos := (z*:p_batchsize);';

        v_mt_args.custom_bind_args_names.extend(1);
        v_mt_args.custom_bind_args_names(1):= ':p_batchsize';

        v_mt_args.custom_bind_args_values.extend(1);
        v_mt_args.custom_bind_args_values(1):= ghost_util.convert_varchar_to_anydata(v_batchsize);

        v_job_count := multi_thread_process(v_mt_args, v_message);


         IF(v_job_count = 1) THEN
           RETURN v_message.uidvm;
         END IF;


                    SELECT blob_value
         BULK COLLECT INTO v_blob_table
                      FROM ghost_vm
                     WHERE bulk_id = v_bulk_id;


      v_final_uidvm := blob_bulk_operation(v_blob_table,p_transaction_id,NULL,NULL,NULL,NULL,NULL);

      DELETE FROM ghost_vm
            WHERE bulk_id = v_bulk_id;


      COMMIT;

      RETURN v_final_uidvm;

      EXCEPTION
        WHEN OTHERS THEN
            ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                          ghost_util.CONST_ERROR_MSG                                             ||
                                          'AGGREGATE_BLOB '                                                ||
                                          ' p_transaction_id:' || ghost_util.wrap_error_params(p_transaction_id) ||
                                          ' p_collection:' || ghost_util.wrap_error_params(p_collection) ||
                                          ' p_size:' || ghost_util.wrap_error_params(p_size),
                                          SQLERRM);

 END aggregate_blob;



 PROCEDURE aggregate_thread(p_query IN VARCHAR2,
                            p_query_args IN VARCHAR2,
                            p_new_collection_id IN NUMBER,
                            p_thread_timeout IN NUMBER,
                            p_transaction_id IN NUMBER,
                            p_start_index IN NUMBER,
                            p_end_index IN NUMBER,
                            p_cjp_seq IN NUMBER,
                            p_bulk_id IN NUMBER) IS
    v_uidvm NUMBER;
    v_blob_table ghost_tab_blob;
    cur_header t_refcursor;
    enqueue_options     dbms_aq.enqueue_options_t;
    message_properties  dbms_aq.message_properties_t;
    message_handle      RAW(16);

   v_sql_query VARCHAR2(32000);

   BEGIN
         v_sql_query := REPLACE(p_query,'''','''''');
    --dbms_output.put_line('SELECT b_value FROM (' || p_query || ') WHERE r BETWEEN :s AND :e');

                IF NOT cur_header%ISOPEN THEN
--                dbms_output.put_line('In IF');
                  OPEN cur_header FOR 'SELECT b_value FROM (' || v_sql_query || ') WHERE r BETWEEN :s AND :e'
                           USING p_start_index, p_end_index;
                       FETCH cur_header BULK COLLECT INTO v_blob_table;
                  CLOSE cur_header;
                END IF;

                --dbms_output.put_line('Bulk collected');

                IF (v_blob_table.COUNT = 0 ) THEN
                    RAISE e_no_records_match;
                END IF;

    /*
      SELECT NVL(blob_value,valuecodes) blobobj
      BULK COLLECT INTO v_blob_table
  FROM perf_test p,
       ghost_vm s,
       idrange i
 WHERE gcid = 1
   AND s.ghost_pointer = p.uidmktinputinterval
   AND p.uidmktinputinterval = i.id
   AND BITAND(s.ghost_collection_id,gcid) = gcid
   AND i.range BETWEEN p_start_index AND p_end_index;
   */

--   dbms_output.put_line(v_blob_table.COUNT);

                v_uidvm := blob_bulk_operation(v_blob_table,p_transaction_id,p_bulk_id, NULL, NULL, NULL, NULL);
                --dbms_output.put_line(v_uidvm);

                message_properties.correlation := p_cjp_seq;
                dbms_aq.enqueue(queue_name => 'ghost_queue',
                                enqueue_options      => enqueue_options,
                                message_properties   => message_properties,
                                payload              => ghost_job_child_obj('PFM','SUCCESS',v_uidvm,p_start_index,p_end_index,v_blob_table.COUNT, p_bulk_id, NULL,SQLERRM),
                                msgid                => message_handle);

                --COMMIT;
                EXCEPTION
                  WHEN e_no_records_match THEN
                    dbms_aq.enqueue(queue_name => 'ghost_queue',
                                    enqueue_options      => enqueue_options,
                                    message_properties   => message_properties,
                                    payload              => ghost_job_child_obj('PFM','ERROR',v_uidvm,p_start_index,p_end_index,v_blob_table.COUNT, p_bulk_id, NULL,' No records found!'),
                                    msgid                => message_handle);
                  WHEN OTHERS THEN
                    IF cur_header%ISOPEN THEN
                       CLOSE cur_header;
                    END IF;

                    message_properties.correlation := p_cjp_seq;
                    dbms_aq.enqueue(queue_name => 'ghost_queue',
                                    enqueue_options      => enqueue_options,
                                    message_properties   => message_properties,
                                    payload              => ghost_job_child_obj('PFM','ERROR',v_uidvm,p_start_index,p_end_index,v_blob_table.COUNT, p_bulk_id, NULL,SQLERRM),
                                    msgid                => message_handle);
                 --COMMIT;

    END aggregate_thread;


--Drive PFM
    PROCEDURE aggregate_by_thread(p_query IN VARCHAR2,
                                  p_query_args IN VARCHAR2,
                                  p_new_collection_id IN NUMBER,
                                  p_thread_timeout IN NUMBER,
                                  p_transaction_id IN NUMBER,
                                  p_custom_columns IN VARCHAR2,
                                  p_custom_column_values IN VARCHAR2,
                                  p_start_index IN NUMBER,
                                  p_end_index IN NUMBER,
                                  p_cjp_seq IN NUMBER,
                                  p_bulk_id IN NUMBER

    /*p_query IN VARCHAR2,
                                  p_drive_id IN VARCHAR2,
                                  p_possession_id IN NUMBER,
                                  p_new_collection_id IN NUMBER,
                                  p_start_index IN NUMBER,
                                  p_end_index IN NUMBER,
                                  p_cjp_seq IN NUMBER,
                                  p_transaction_id IN NUMBER,
                                  p_bulk_id IN NUMBER*/

                                  ) IS
    v_uidvm NUMBER;
    v_blob_table ghost_tab_blob;
    cur_header t_refcursor;
    enqueue_options     dbms_aq.enqueue_options_t;
    message_properties  dbms_aq.message_properties_t;
    message_handle      RAW(16);

--    v_drive_id VARCHAR2(500);

    v_sql_query VARCHAR2(32000);

   BEGIN
         v_sql_query := REPLACE(p_query,'''','''''');
--                v_drive_id := get_driver_id(p_custom_column_values);
                IF NOT cur_header%ISOPEN THEN
                --dbms_output.put_line('In IF');
                  OPEN cur_header FOR 'SELECT b_value FROM (' || v_sql_query || ') WHERE r BETWEEN :s AND :e'
                           USING p_custom_column_values, p_start_index, p_end_index;
                       FETCH cur_header BULK COLLECT INTO v_blob_table;
                  CLOSE cur_header;
                END IF;


                IF (v_blob_table.COUNT = 0 ) THEN
                    RAISE e_no_records_match;
                END IF;


                v_uidvm := blob_bulk_operation(v_blob_table,p_transaction_id,p_bulk_id, p_new_collection_id, NULL, p_custom_columns, p_custom_column_values);
                --dbms_output.put_line(v_uidvm);

                message_properties.correlation := p_cjp_seq;
                dbms_aq.enqueue(queue_name => 'ghost_queue',
                                enqueue_options      => enqueue_options,
                                message_properties   => message_properties,
                                payload              => ghost_job_child_obj('AGGBY_T: ','SUCCESS',v_uidvm,p_start_index,p_end_index,v_blob_table.COUNT, p_bulk_id, p_custom_column_values,SQLERRM),
                                msgid                => message_handle);

                --COMMIT;
                EXCEPTION
                  WHEN e_no_records_match THEN
                    message_properties.correlation := p_cjp_seq;
                    dbms_aq.enqueue(queue_name => 'ghost_queue',
                                    enqueue_options      => enqueue_options,
                                    message_properties   => message_properties,
                                    payload              => ghost_job_child_obj('AGGBY_T','ERROR',v_uidvm,p_start_index,p_end_index,v_blob_table.COUNT, p_bulk_id, p_custom_column_values,' No records found! :' || p_custom_column_values || p_start_index || p_end_index),
                                    msgid                => message_handle);
                  WHEN OTHERS THEN
                    IF cur_header%ISOPEN THEN
                       CLOSE cur_header;
                    END IF;

                    message_properties.correlation := p_cjp_seq;
                    dbms_aq.enqueue(queue_name => 'ghost_queue',
                                    enqueue_options      => enqueue_options,
                                    message_properties   => message_properties,
                                    payload              => ghost_job_child_obj('AGGBY_T','ERROR',v_uidvm,p_start_index,p_end_index,v_blob_table.COUNT, p_bulk_id, p_custom_column_values,SQLERRM),
                                    msgid                => message_handle);
                 --COMMIT;

    END aggregate_by_thread;


    PROCEDURE pfm (p_cjp_seq IN NUMBER,
                   p_bulk_id IN NUMBER,
                   p_transaction_id IN NUMBER,
                   p_bulk_insert_id IN NUMBER) AS
        v_uidvm NUMBER;
        v_blob_table ghost_tab_blob;
        enqueue_options     dbms_aq.enqueue_options_t;
        message_properties  dbms_aq.message_properties_t;
        message_handle      RAW(16);

    BEGIN
              SELECT blob_value
   BULK COLLECT INTO v_blob_table
                FROM ghost_vm
               WHERE bulk_id = p_bulk_id;


             v_uidvm := blob_bulk_operation(v_blob_table,p_transaction_id,NULL, p_bulk_insert_id, NULL,NULL,NULL);

                message_properties.correlation := p_cjp_seq;
                dbms_aq.enqueue(queue_name => 'ghost_queue',
                                enqueue_options      => enqueue_options,
                                message_properties   => message_properties,
                                payload              => ghost_job_child_obj('PFM: ' || NULL,'SUCCESS',v_uidvm,NULL,NULL,v_blob_table.COUNT, NULL, NULL,SQLERRM),
                                msgid                => message_handle);

                --COMMIT;
                EXCEPTION
                  WHEN OTHERS THEN
                    message_properties.correlation := p_cjp_seq;
                    dbms_aq.enqueue(queue_name => 'ghost_queue',
                                    enqueue_options      => enqueue_options,
                                    message_properties   => message_properties,
                                    payload              => ghost_job_child_obj('PFM' || NULL,'ERROR',v_uidvm,NULL,NULL,v_blob_table.COUNT, NULL, NULL,SQLERRM),
                                    msgid                => message_handle);
                 --COMMIT;

    END pfm;



FUNCTION blob_compare_interval_values(p_ghost_data_left IN ghost_data_obj,
                                      p_ghost_data_right IN ghost_data_obj)
  RETURN NUMBER AS
    v_num_intervals_left NUMBER;
    v_num_intervals_right NUMBER;
    v_left_interval_value BINARY_DOUBLE;
    v_right_interval_value BINARY_DOUBLE;
    v_comparison_result NUMBER;
    v_num_iterations NUMBER;

  BEGIN
    IF( (p_ghost_data_left.blob_value IS NULL) OR (p_ghost_data_right.blob_value IS NULL) ) THEN
      v_comparison_result := 1;
      RETURN v_comparison_result;
    END IF;

    IF (p_ghost_data_left.intervalcount != p_ghost_data_right.intervalcount) THEN
      v_comparison_result := 1;
      RETURN v_comparison_result;
    END IF;

    v_num_intervals_left := get_blob_length(p_ghost_data_left.blob_value);
    v_num_intervals_right := get_blob_length(p_ghost_data_right.blob_value);
    IF (v_num_intervals_left != v_num_intervals_right) THEN
      v_comparison_result := 1;
      RETURN v_comparison_result;
    END IF;

    v_num_iterations := v_num_intervals_left;
    FOR j in 1..v_num_iterations LOOP
      v_left_interval_value := get_metablob_interval(p_ghost_data_left.blob_value, j);
      v_right_interval_value := get_metablob_interval(p_ghost_data_right.blob_value, j);
      IF (v_left_interval_value != v_right_interval_value) THEN
        v_comparison_result := 1;
      ELSE
        v_comparison_result := 0;

      END IF;

      EXIT WHEN (v_comparison_result = 1);
    END LOOP;

    RETURN v_comparison_result;
    EXCEPTION
           WHEN OTHERS THEN
               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG ||
                                             'BLOB_COMPARE_INTERVAL_VALUES ' ||
                                             ' p_ghost_data_left.uidvm: ' || ghost_util.wrap_error_params(p_ghost_data_left.uidvm) ||
                                             ' p_ghost_data_right.uidvm: ' || ghost_util.wrap_error_params(p_ghost_data_right.uidvm),
                                             SQLERRM);

  END blob_compare_interval_values;

FUNCTION blob_comparison(p_ghost_data_left IN ghost_data_obj,
                         p_ghost_data_right IN ghost_data_obj,
                         p_uidvm_save IN NUMBER,
                         p_dest_cid IN NUMBER) RETURN NUMBER AS
  v_comparison_result NUMBER;

  BEGIN
    v_comparison_result := blob_compare_interval_values(p_ghost_data_left,
                                                        p_ghost_data_right);
    IF (v_comparison_result = 1) THEN
      UPDATE GHOST_VM
        SET GHOST_COLLECTION_ID = GHOST_COLLECTION_ID + p_dest_cid
      WHERE GHOST_VM.UIDVM = p_uidvm_save;

    END IF;

    RETURN v_comparison_result;
    EXCEPTION
      WHEN OTHERS THEN
      ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                    ghost_util.CONST_ERROR_MSG ||
                                    'BLOB_COMPARISON ' ||
                                    ' p_uidvm_save: ' || ghost_util.wrap_error_params(p_uidvm_save) ||
                                    ' p_ghost_data_left.uidvm: ' || ghost_util.wrap_error_params(p_ghost_data_left.uidvm) ||
                                    ' p_ghost_data_right.uidvm: ' || ghost_util.wrap_error_params(p_ghost_data_right.uidvm),
                                    SQLERRM);

  END blob_comparison;

FUNCTION blob_comparison_pair_helper(p_data_left IN ghost_bulk_data_obj,
                                    p_data_right IN ghost_bulk_data_obj,
                                    p_dest_cid IN NUMBER) RETURN NUMBER AS
  v_ghost_data_left ghost_data_obj;
  v_ghost_data_right ghost_data_obj;
  v_comparison_result NUMBER;
  v_comparison_mismatch_count NUMBER;

  BEGIN
     v_ghost_data_left := ghost_data_obj();
     v_ghost_data_right := ghost_data_obj();
     IF p_data_left.blob_value.COUNT = 0 THEN
            RAISE e_no_records_match;
     END IF;

     v_comparison_result := 0;
     v_comparison_mismatch_count := 0;
     FOR x IN p_data_left.blob_value.FIRST..p_data_left.blob_value.LAST LOOP
       v_ghost_data_left.blob_value := p_data_left.blob_value(x);
       v_ghost_data_left.uidvm := p_data_left.uidvm(x);
       v_ghost_data_left.starttime := p_data_left.starttime(x);
       v_ghost_data_left.stoptime := p_data_left.stoptime(x);
       v_ghost_data_left.spi := p_data_left.spi(x);
       v_ghost_data_left.intervalcount := p_data_left.intervalcount(x);
       v_ghost_data_left.dst_participant := p_data_left.dst_participant(x);
       v_ghost_data_left.custom_1 := p_data_left.custom_1(x);
       v_ghost_data_left.custom_2 := p_data_left.custom_2(x);
       v_ghost_data_left.custom_3 := p_data_left.custom_3(x);
       v_ghost_data_left.custom_4 := p_data_left.custom_4(x);
       v_ghost_data_left.custom_5 := p_data_left.custom_5(x);
       v_ghost_data_left.custom_6 := p_data_left.custom_6(x);
       v_ghost_data_left.custom_7 := p_data_left.custom_7(x);
       v_ghost_data_left.custom_8 := p_data_left.custom_8(x);
       v_ghost_data_left.custom_9 := p_data_left.custom_9(x);
       v_ghost_data_left.custom_10 := p_data_left.custom_10(x);
       v_ghost_data_left.custom_date_1 := p_data_left.custom_date_1(x);
       v_ghost_data_left.custom_date_2 := p_data_left.custom_date_2(x);
       v_ghost_data_left.custom_date_3 := p_data_left.custom_date_3(x);
       v_ghost_data_left.custom_date_4 := p_data_left.custom_date_4(x);
       v_ghost_data_left.custom_date_5 := p_data_left.custom_date_5(x);
       v_ghost_data_left.custom_date_6 := p_data_left.custom_date_6(x);
       v_ghost_data_left.custom_date_7 := p_data_left.custom_date_7(x);
       v_ghost_data_left.custom_date_8 := p_data_left.custom_date_8(x);
       v_ghost_data_left.custom_date_9 := p_data_left.custom_date_9(x);
       v_ghost_data_left.custom_date_10 := p_data_left.custom_date_10(x);
       v_ghost_data_right.blob_value := p_data_right.blob_value(x);
       v_ghost_data_right.uidvm := p_data_right.uidvm(x);
       v_ghost_data_right.starttime := p_data_right.starttime(x);
       v_ghost_data_right.stoptime := p_data_right.stoptime(x);
       v_ghost_data_right.spi := p_data_right.spi(x);
       v_ghost_data_right.intervalcount := p_data_right.intervalcount(x);
       v_ghost_data_right.dst_participant := p_data_right.dst_participant(x);
       v_ghost_data_right.custom_1 := p_data_right.custom_1(x);
       v_ghost_data_right.custom_2 := p_data_right.custom_2(x);
       v_ghost_data_right.custom_3 := p_data_right.custom_3(x);
       v_ghost_data_right.custom_4 := p_data_right.custom_4(x);
       v_ghost_data_right.custom_5 := p_data_right.custom_5(x);
       v_ghost_data_right.custom_6 := p_data_right.custom_6(x);
       v_ghost_data_right.custom_7 := p_data_right.custom_7(x);
       v_ghost_data_right.custom_8 := p_data_right.custom_8(x);
       v_ghost_data_right.custom_9 := p_data_right.custom_9(x);
       v_ghost_data_right.custom_10 := p_data_right.custom_10(x);
       v_ghost_data_right.custom_date_1 := p_data_right.custom_date_1(x);
       v_ghost_data_right.custom_date_2 := p_data_right.custom_date_2(x);
       v_ghost_data_right.custom_date_3 := p_data_right.custom_date_3(x);
       v_ghost_data_right.custom_date_4 := p_data_right.custom_date_4(x);
       v_ghost_data_right.custom_date_5 := p_data_right.custom_date_5(x);
       v_ghost_data_right.custom_date_6 := p_data_right.custom_date_6(x);
       v_ghost_data_right.custom_date_7 := p_data_right.custom_date_7(x);
       v_ghost_data_right.custom_date_8 := p_data_right.custom_date_8(x);
       v_ghost_data_right.custom_date_9 := p_data_right.custom_date_9(x);
       v_ghost_data_right.custom_date_10 := p_data_right.custom_date_10(x);
       v_comparison_result := blob_comparison(v_ghost_data_left,
                                              v_ghost_data_right,
                                              v_ghost_data_left.uidvm,
                                              p_dest_cid);
       IF (v_comparison_result = 1) THEN
         v_comparison_mismatch_count := v_comparison_mismatch_count + 1;
       END IF;

     END LOOP;

     COMMIT;
     RETURN v_comparison_mismatch_count;
     EXCEPTION
         WHEN e_no_records_match THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG ||
                                           'BLOB_COMPARISON_PAIR_HELPER: No records matched! ' ||
                                           ' p_dest_cid:' || ghost_util.wrap_error_params(p_dest_cid),
                                           SQLERRM);
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG ||
                                           'BLOB_COMPARISON_PAIR_HELPER  ' ||
                                           ' p_dest_cid:' || ghost_util.wrap_error_params(p_dest_cid),
                                           SQLERRM);

  END blob_comparison_pair_helper;

FUNCTION blob_comparison_pair(p_sql_query IN VARCHAR2,
                             p_dest_cid IN NUMBER)
  RETURN NUMBER AS
  cur_header t_refcursor;
  v_data_left ghost_bulk_data_obj;
  v_data_right ghost_bulk_data_obj;

  BEGIN
     v_data_left := ghost_bulk_data_obj();
     v_data_right := ghost_bulk_data_obj();
     IF NOT cur_header%ISOPEN THEN
         OPEN cur_header FOR p_sql_query;-- || ' FOR UPDATE';
              FETCH cur_header BULK COLLECT INTO v_data_left.blob_value,
                                                 v_data_right.blob_value,
                                                 v_data_left.uidvm,
                                                 v_data_left.starttime,
                                                 v_data_left.stoptime,
                                                 v_data_left.spi,
                                                 v_data_left.intervalcount,
                                                 v_data_left.dst_participant,
                                                 v_data_left.custom_1,
                                                 v_data_left.custom_2,
                                                 v_data_left.custom_3,
                                                 v_data_left.custom_4,
                                                 v_data_left.custom_5,
                                                 v_data_left.custom_6,
                                                 v_data_left.custom_7,
                                                 v_data_left.custom_8,
                                                 v_data_left.custom_9,
                                                 v_data_left.custom_10,
                                                 v_data_left.custom_11,
                                                 v_data_left.custom_12,
                                                 v_data_left.custom_13,
                                                 v_data_left.custom_14,
                                                 v_data_left.custom_15,
                                                 v_data_left.custom_16,
                                                 v_data_left.custom_17,
                                                 v_data_left.custom_18,
                                                 v_data_left.custom_19,
                                                 v_data_left.custom_20,
                                                 v_data_left.custom_date_1,
                                                 v_data_left.custom_date_2,
                                                 v_data_left.custom_date_3,
                                                 v_data_left.custom_date_4,
                                                 v_data_left.custom_date_5,
                                                 v_data_left.custom_date_6,
                                                 v_data_left.custom_date_7,
                                                 v_data_left.custom_date_8,
                                                 v_data_left.custom_date_9,
                                                 v_data_left.custom_date_10,
                                                 v_data_right.uidvm,
                                                 v_data_right.starttime,
                                                 v_data_right.stoptime,
                                                 v_data_right.spi,
                                                 v_data_right.intervalcount,
                                                 v_data_right.dst_participant,
                                                 v_data_right.custom_1,
                                                 v_data_right.custom_2,
                                                 v_data_right.custom_3,
                                                 v_data_right.custom_4,
                                                 v_data_right.custom_5,
                                                 v_data_right.custom_6,
                                                 v_data_right.custom_7,
                                                 v_data_right.custom_8,
                                                 v_data_right.custom_9,
                                                 v_data_right.custom_10,
                                                 v_data_right.custom_11,
                                                 v_data_right.custom_12,
                                                 v_data_right.custom_13,
                                                 v_data_right.custom_14,
                                                 v_data_right.custom_15,
                                                 v_data_right.custom_16,
                                                 v_data_right.custom_17,
                                                 v_data_right.custom_18,
                                                 v_data_right.custom_19,
                                                 v_data_right.custom_20,
                                                 v_data_right.custom_date_1,
                                                 v_data_right.custom_date_2,
                                                 v_data_right.custom_date_3,
                                                 v_data_right.custom_date_4,
                                                 v_data_right.custom_date_5,
                                                 v_data_right.custom_date_6,
                                                 v_data_right.custom_date_7,
                                                 v_data_right.custom_date_8,
                                                 v_data_right.custom_date_9,
                                                 v_data_right.custom_date_10;
         CLOSE cur_header;
     END IF;

     RETURN blob_comparison_pair_helper(v_data_left,
                                        v_data_right,
                                        p_dest_cid);
     EXCEPTION
         WHEN e_no_records_match THEN
            IF cur_header%ISOPEN THEN
                CLOSE cur_header;
             END IF;

             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG ||
                                           'BLOB_COMPARISON_PAIR: No records matched! ' ||
                                           ' p_dest_cid:' || ghost_util.wrap_error_params(p_dest_cid),
                                           SQLERRM);
         WHEN OTHERS THEN
             IF cur_header%ISOPEN THEN
                CLOSE cur_header;
             END IF;

             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG ||
                                           'BLOB_COMPARISON_PAIR ' ||
                                           ' p_dest_cid:' || ghost_util.wrap_error_params(p_dest_cid),
                                           SQLERRM);

  END blob_comparison_pair;

FUNCTION blob_comparison_uidvm(p_uidvm_left IN NUMBER,
                               p_uidvm_right IN NUMBER,
                               p_sql_query_left VARCHAR2,
                               p_sql_query_right VARCHAR2)
  RETURN NUMBER AS
  v_blob_left BLOB;
  v_blob_right BLOB;
  v_starttime_left DATE;
  v_stoptime_left DATE;
  v_spi_left NUMBER;
  v_intervalcount_left NUMBER;
  v_dst_participant_left CHAR;
  v_starttime_right DATE;
  v_stoptime_right DATE;
  v_spi_right NUMBER;
  v_intervalcount_right NUMBER;
  v_dst_participant_right CHAR;
  v_select_blob_query_local VARCHAR2(200);
  v_select_blob_query_ext VARCHAR2(200);
  v_ghost_data_left ghost_data_obj;
  v_ghost_data_right ghost_data_obj;
  v_comparison_result NUMBER;

  BEGIN
        v_ghost_data_left := ghost_data_obj();
        v_ghost_data_right := ghost_data_obj();
        v_select_blob_query_local := 'SELECT blob_value, meta_starttime, meta_stoptime, metablob_spi, metablob_intervalcount, metablob_dst_participant' ||chr(10)||
                                     ' FROM ghost_vm WHERE uidvm = :p_uidvm';
        v_select_blob_query_ext := 'SELECT meta_starttime, meta_stoptime, metablob_spi, metablob_intervalcount, metablob_dst_participant' ||chr(10)||
                                   ' FROM ghost_vm WHERE uidvm = :p_uidvm';
        IF p_sql_query_left IS NULL THEN
            EXECUTE IMMEDIATE v_select_blob_query_local INTO v_blob_left,
                                                       v_starttime_left,
                                                       v_stoptime_left,
                                                       v_spi_left,
                                                       v_intervalcount_left,
                                                       v_dst_participant_left USING p_uidvm_left;
         ELSE
            EXECUTE IMMEDIATE p_sql_query_left INTO v_blob_left USING p_uidvm_left;
            EXECUTE IMMEDIATE v_select_blob_query_ext INTO v_starttime_left,
                                                           v_stoptime_left,
                                                           v_spi_left,
                                                           v_intervalcount_left,
                                                           v_dst_participant_left USING p_uidvm_left;

         END IF;

         IF p_sql_query_right IS NULL THEN
            EXECUTE IMMEDIATE v_select_blob_query_local INTO v_blob_right,
                                                       v_starttime_right,
                                                       v_stoptime_right,
                                                       v_spi_right,
                                                       v_intervalcount_right,
                                                       v_dst_participant_right USING p_uidvm_right;
         ELSE
            EXECUTE IMMEDIATE p_sql_query_right INTO v_blob_right USING p_uidvm_right;
            EXECUTE IMMEDIATE v_select_blob_query_ext INTO v_starttime_right,
                                                           v_stoptime_right,
                                                           v_spi_right,
                                                           v_intervalcount_right,
                                                           v_dst_participant_right USING p_uidvm_right;

         END IF;

        v_ghost_data_left.blob_value := v_blob_left;
        v_ghost_data_left.starttime := v_starttime_left;
        v_ghost_data_left.stoptime := v_stoptime_left;
        v_ghost_data_left.spi := v_spi_left;
        v_ghost_data_left.intervalcount := v_intervalcount_left;
        v_ghost_data_left.dst_participant := v_dst_participant_left;
        v_ghost_data_right.blob_value := v_blob_right;
        v_ghost_data_right.starttime := v_starttime_right;
        v_ghost_data_right.stoptime := v_stoptime_right;
        v_ghost_data_right.spi := v_spi_right;
        v_ghost_data_right.intervalcount := v_intervalcount_right;
        v_ghost_data_right.dst_participant := v_dst_participant_right;
        v_comparison_result := blob_compare_interval_values(v_ghost_data_left,
                                                            v_ghost_data_right);
        RETURN v_comparison_result;
     EXCEPTION
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG ||
                                           'BLOB_COMPARISON_UIDVM ' ||
                                           ' p_uidvm_left: ' || ghost_util.wrap_error_params(p_uidvm_left) ||
                                           ' p_uidvm_right: ' || ghost_util.wrap_error_params(p_uidvm_right) ||
                                           ' p_sql_query_left: ' || ghost_util.wrap_error_params(p_sql_query_left) ||
                                           ' p_sql_query_right: ' || ghost_util.wrap_error_params(p_sql_query_right) ||
                                           SQLERRM);

  END blob_comparison_uidvm;



  PROCEDURE blob_createflag_helper(p_ghost_data_obj IN ghost_data_obj,
                                   p_uidvm_save IN NUMBER,                                   
                                   p_rules_case IN VARCHAR2,
                                   p_dest_cid IN NUMBER,
                                   p_custom_columns IN VARCHAR2) AS
                            
  v_sql VARCHAR2(10000);
  
  BEGIN
         
         
         v_sql := 'DECLARE' || chr(10) ||
                  '  v_blob_source BLOB;'|| chr(10) ||
                  '  v_blob_save BLOB;'|| chr(10) ||
                  '  v_num_iterations NUMBER;'|| chr(10) ||
                  '  v_status_code CHAR;'|| chr(10) ||
                  '  v_binary_number BINARY_DOUBLE;'|| chr(10) ||
                  '  v_metablob_total NUMBER;'|| chr(10) ||
                  '  v_metablob_max NUMBER;'|| chr(10) ||
                  '  v_metablob_min NUMBER;'|| chr(10) ||
                  '  v_ghost_vm_row ghost_blob_util.vm_insert_table;' || chr(10) ||
                  '  v_uidvm_save NUMBER;' || chr(10) ||
                  '  v_custom_column_values VARCHAR2(3000);' || chr(10) ||
--                  '  v_metablob_starttime DATE;'|| chr(10) ||
--                  '  v_metablob_stoptime DATE;'|| chr(10) ||
--                  '  v_metablob_spi NUMBER;'|| chr(10) ||
--                  '  v_metablob_dst_participant CHAR;'|| chr(10) ||
                  '  v_ghost_data_obj GHOST_DATA_OBJ := :p_ghost_data_obj;'|| chr(10) ||
                  '  v_updated_vm BOOLEAN := false;'|| chr(10) ||
                  '  v_remote_blob BOOLEAN;'|| chr(10) ||                  
                  '  BEGIN'|| chr(10) ||
--                  '         v_ghost_data_obj := ghost_data_obj();' || chr(10) ||
--                  '         v_remote_blob := ghost_blob_util.get_blob(v_blob_source, :p_sql_query, :p_uidvm);'|| chr(10) ||
                  '         v_blob_source := v_ghost_data_obj.blob_value;' ||chr(10)||
                  '         v_num_iterations := ghost_blob_util.get_blob_length(v_blob_source);'|| chr(10) ||
                  '         dbms_lob.createtemporary(v_blob_save,TRUE);'|| chr(10) ||
                  '         dbms_lob.open(v_blob_save, dbms_lob.lob_readwrite);'|| chr(10) ||
                  '         v_binary_number := ghost_blob_util.get_metablob_interval(v_blob_source,1); '|| chr(10) ||
                  p_rules_case || chr(10) ||
                  '         ghost_blob_util.write_binarydouble_to_metablob(v_blob_save,1, v_binary_number);'|| chr(10) ||
                  '         v_status_code := ghost_blob_util.get_status_code(v_blob_source,v_num_iterations,1);'|| chr(10) ||
                  '         ghost_blob_util.set_init_metablob_attributes(v_status_code,'|| chr(10) ||
                  '							                                        v_binary_number,'|| chr(10) ||
                  '							                                        v_metablob_total,'|| chr(10) ||
                  '							                                        v_metablob_max,'|| chr(10) ||
                  '							                                        v_metablob_min);'|| chr(10) ||
                  '         FOR j in 2..v_num_iterations LOOP'|| chr(10) ||
                  '             v_binary_number := ghost_blob_util.get_metablob_interval(v_blob_source,j); '|| chr(10) ||
                  p_rules_case || chr(10) ||
                  '             ghost_blob_util.write_binarydouble_to_metablob(v_blob_save,j, v_binary_number);'|| chr(10) ||
                  '             v_status_code := ghost_blob_util.get_status_code(v_blob_source,v_num_iterations,j);'|| chr(10) ||
                  '             ghost_blob_util.set_metablob_attributes(v_status_code,'|| chr(10) ||
                  '								                                     v_binary_number,'|| chr(10) ||
                  '								                                     v_metablob_total,'|| chr(10) ||
                  '								                                     v_metablob_max,'|| chr(10) ||
                  '								                                     v_metablob_min);'|| chr(10) ||
                  '         END LOOP;'|| chr(10) ||
                  '         ghost_util.ghost_write_raw_to_blob(v_blob_save, utl_raw.substr(v_blob_source,(ghost_blob_util.CONST_INTERVAL_BYTE_SIZE*(v_num_iterations))+1));' || chr(10) ||
                  '         dbms_lob.close(v_blob_save);'|| chr(10) ||
                  'v_uidvm_save := :p_uidvm_save;' ||chr(10)||
                           'IF :p_dest_cid IS NOT NULL THEN' ||chr(10)||
                              'v_uidvm_save := Seq_GHOSTUIDVM.NEXTVAL;' ||chr(10)||
                  '            v_ghost_vm_row.uidvm := v_uidvm_save;' ||chr(10)||
                              'v_ghost_vm_row.blob_value := v_blob_save;' ||chr(10)||
--                  '            v_ghost_vm_row.ghost_transaction_id := p_transaction_id;' ||chr(10)||
                              'v_ghost_vm_row.ghost_collection_id := :p_dest_cid;' ||chr(10)||
                  '            v_ghost_vm_row.meta_starttime := v_ghost_data_obj.starttime;' ||chr(10)||
                              'v_ghost_vm_row.meta_stoptime := v_ghost_data_obj.stoptime;' ||chr(10)||
                  '            v_ghost_vm_row.metablob_spi := 86400/v_num_iterations;' ||chr(10)||
                              'v_ghost_vm_row.metablob_dst_participant := v_ghost_data_obj.dst_participant;' ||chr(10)||
                  '            v_ghost_vm_row.metablob_total := v_metablob_total;' ||chr(10)||
                              'v_ghost_vm_row.metablob_max := v_metablob_max;' ||chr(10)||
                  '            v_ghost_vm_row.metablob_min := v_metablob_min;' ||chr(10)||
                              'v_ghost_vm_row.metablob_intervalcount := v_num_iterations;' ||chr(10)||
                              'ghost_blob_util.build_custom_outputs(v_ghost_data_obj,' ||chr(10)||
                                                   ':p_custom_columns,' ||chr(10)||
                  '                                 v_custom_column_values);' ||chr(10)||
                              'ghost_blob_util.insert_into_ghost_vm(ghost_blob_util.CONST_BLOB_INSERT_COLUMN,' ||chr(10)||
                                                   ':p_custom_columns,' ||chr(10)||
                  '                                 v_custom_column_values,' ||chr(10)||
                                                   'v_ghost_vm_row);' ||chr(10)||
                           'ELSE' ||chr(10)||
                              'UPDATE ghost_vm' ||chr(10)||
                                'SET blob_value = v_blob_save,' ||chr(10)||
                                    'metablob_total = v_metablob_total,' ||chr(10)||
                  '                  metablob_max = v_metablob_max,' ||chr(10)||
                                    'metablob_min = v_metablob_min,' ||chr(10)||
                  '                  metablob_intervalcount = v_num_iterations,' ||chr(10)||
                                    'meta_starttime = v_ghost_data_obj.starttime,' ||chr(10)||
                  '                  meta_stoptime = v_ghost_data_obj.stoptime,' ||chr(10)||
                                    'metablob_spi = v_ghost_data_obj.spi,' ||chr(10)||
                  '                  metablob_dst_participant = v_ghost_data_obj.dst_participant,' ||chr(10)||
                                    'CUSTOM_1 = v_ghost_data_obj.CUSTOM_1,' || chr(10) ||
                                    'CUSTOM_2 = v_ghost_data_obj.CUSTOM_2,' || chr(10) ||
                                    'CUSTOM_3 = v_ghost_data_obj.CUSTOM_3,' || chr(10) ||
                                    'CUSTOM_4 = v_ghost_data_obj.CUSTOM_4,' || chr(10) ||
                                    'CUSTOM_5 = v_ghost_data_obj.CUSTOM_5,' || chr(10) ||
                                    'CUSTOM_6 = v_ghost_data_obj.CUSTOM_6,' || chr(10) ||
                                    'CUSTOM_7 = v_ghost_data_obj.CUSTOM_7,' || chr(10) ||
                                    'CUSTOM_8 = v_ghost_data_obj.CUSTOM_8,' || chr(10) ||
                                    'CUSTOM_9 = v_ghost_data_obj.CUSTOM_9,' || chr(10) ||
                                    'CUSTOM_10 = v_ghost_data_obj.CUSTOM_10,' || chr(10) ||
                                    'CUSTOM_11 = v_ghost_data_obj.CUSTOM_11,' || chr(10) ||
                                    'CUSTOM_12 = v_ghost_data_obj.CUSTOM_12,' || chr(10) ||
                                    'CUSTOM_13 = v_ghost_data_obj.CUSTOM_13,' || chr(10) ||
                                    'CUSTOM_14 = v_ghost_data_obj.CUSTOM_14,' || chr(10) ||
                                    'CUSTOM_15 = v_ghost_data_obj.CUSTOM_15,' || chr(10) ||
                                    'CUSTOM_16 = v_ghost_data_obj.CUSTOM_16,' || chr(10) ||
                                    'CUSTOM_17 = v_ghost_data_obj.CUSTOM_17,' || chr(10) ||
                                    'CUSTOM_18 = v_ghost_data_obj.CUSTOM_18,' || chr(10) ||
                                    'CUSTOM_19 = v_ghost_data_obj.CUSTOM_19,' || chr(10) ||
                                    'CUSTOM_20 = v_ghost_data_obj.CUSTOM_20,' || chr(10) ||
                                    'CUSTOM_DATE_1 = v_ghost_data_obj.CUSTOM_DATE_1,' || chr(10) ||
                                    'CUSTOM_DATE_2 = v_ghost_data_obj.CUSTOM_DATE_2,' || chr(10) ||
                                    'CUSTOM_DATE_3 = v_ghost_data_obj.CUSTOM_DATE_3,' || chr(10) ||
                                    'CUSTOM_DATE_4 = v_ghost_data_obj.CUSTOM_DATE_4,' || chr(10) ||
                                    'CUSTOM_DATE_5 = v_ghost_data_obj.CUSTOM_DATE_5,' || chr(10) ||
                                    'CUSTOM_DATE_6 = v_ghost_data_obj.CUSTOM_DATE_6,' || chr(10) ||
                                    'CUSTOM_DATE_7 = v_ghost_data_obj.CUSTOM_DATE_7,' || chr(10) ||
                                    'CUSTOM_DATE_8 = v_ghost_data_obj.CUSTOM_DATE_8,' || chr(10) ||
                                    'CUSTOM_DATE_9 = v_ghost_data_obj.CUSTOM_DATE_9,' || chr(10) ||
                                    'CUSTOM_DATE_10 = v_ghost_data_obj.CUSTOM_DATE_10,' || chr(10) ||
                                    'last_update_date = SYSDATE' ||chr(10)||
                              'WHERE uidvm = v_uidvm_save;' ||chr(10)||
                  '         END IF;' ||chr(10)||

/*
                  'SELECT' ||chr(10)||
                 '     meta_starttime,' ||chr(10)||
                 '     meta_stoptime,' ||chr(10)||
                 '     metablob_spi,' ||chr(10)||
--                 '     metablob_intervalcount,' ||chr(10)||
                 '     metablob_dst_participant,' ||chr(10)||
                 '     custom_1,' ||chr(10)||
                 '     custom_2,' ||chr(10)||
                 '     custom_3,' ||chr(10)||
                 '     custom_4,' ||chr(10)||
                 '     custom_5,' ||chr(10)||
                 '     custom_6,' ||chr(10)||
                 '     custom_7,' ||chr(10)||
                 '     custom_8,' ||chr(10)||
                 '     custom_9,' ||chr(10)||
                 '     custom_10,' ||chr(10)||
                 '     custom_11,' ||chr(10)||
                 '     custom_12,' ||chr(10)||
                 '     custom_13,' ||chr(10)||
                 '     custom_14,' ||chr(10)||
                 '     custom_15,' ||chr(10)||
                 '     custom_16,' ||chr(10)||
                 '     custom_17,' ||chr(10)||
                 '     custom_18,' ||chr(10)||
                 '     custom_19,' ||chr(10)||
                 '     custom_20,' ||chr(10)||
                 '     custom_date_1,' ||chr(10)||
                 '     custom_date_2,' ||chr(10)||
                 '     custom_date_3,' ||chr(10)||
                 '     custom_date_4,' ||chr(10)||
                 '     custom_date_5,' ||chr(10)||
                 '     custom_date_6,' ||chr(10)||
                 '     custom_date_7,' ||chr(10)||
                 '     custom_date_8,' ||chr(10)||
                 '     custom_date_9,' ||chr(10)||
                 '     custom_date_10' ||chr(10)||
                 ' INTO ' || chr(10) ||
                 '      v_ghost_data_obj.starttime,' ||chr(10)||
                 '      v_ghost_data_obj.stoptime,' ||chr(10)||
                 '      v_ghost_data_obj.spi,' ||chr(10)||
--                 '      v_ghost_data_obj.intervalcount,' ||chr(10)||
                 '      v_ghost_data_obj.dst_participant,' ||chr(10)||
                 '      v_ghost_data_obj.custom_1,' ||chr(10)||
                 '      v_ghost_data_obj.custom_2,' ||chr(10)||
                 '      v_ghost_data_obj.custom_3,' ||chr(10)||
                 '      v_ghost_data_obj.custom_4,' ||chr(10)||
                 '      v_ghost_data_obj.custom_5,' ||chr(10)||
                 '      v_ghost_data_obj.custom_6,' ||chr(10)||
                 '      v_ghost_data_obj.custom_7,' ||chr(10)||
                 '      v_ghost_data_obj.custom_8,' ||chr(10)||
                 '      v_ghost_data_obj.custom_9,' ||chr(10)||
                 '      v_ghost_data_obj.custom_10,' ||chr(10)||
                 '      v_ghost_data_obj.custom_11,' ||chr(10)||
                 '      v_ghost_data_obj.custom_12,' ||chr(10)||
                 '      v_ghost_data_obj.custom_13,' ||chr(10)||
                 '      v_ghost_data_obj.custom_14,' ||chr(10)||
                 '      v_ghost_data_obj.custom_15,' ||chr(10)||
                 '      v_ghost_data_obj.custom_16,' ||chr(10)||
                 '      v_ghost_data_obj.custom_17,' ||chr(10)||
                 '      v_ghost_data_obj.custom_18,' ||chr(10)||
                 '      v_ghost_data_obj.custom_19,' ||chr(10)||
                 '      v_ghost_data_obj.custom_20,' ||chr(10)||
                 '      v_ghost_data_obj.custom_date_1,' ||chr(10)||
                 '      v_ghost_data_obj.custom_date_2,' ||chr(10)||
                 '      v_ghost_data_obj.custom_date_3,' ||chr(10)||
                 '      v_ghost_data_obj.custom_date_4,' ||chr(10)||
                 '      v_ghost_data_obj.custom_date_5,' ||chr(10)||
                 '      v_ghost_data_obj.custom_date_6,' ||chr(10)||
                 '      v_ghost_data_obj.custom_date_7,' ||chr(10)||
                 '      v_ghost_data_obj.custom_date_8,' ||chr(10)||
                 '      v_ghost_data_obj.custom_date_9,' ||chr(10)||
                 '      v_ghost_data_obj.custom_date_10' ||chr(10)||
                 ' FROM ghost_vm' ||chr(10)||
                 'WHERE uidvm = :p_uidvm;' ||chr(10)||
                  '         UPDATE ghost_vm'|| chr(10) ||
                  '            SET blob_value = v_blob_save,'|| chr(10) ||
                  '                metablob_total = v_metablob_total,'|| chr(10) ||
                  '                metablob_max = v_metablob_max,'|| chr(10) ||
                  '                metablob_min = v_metablob_min,'|| chr(10) ||
                  '                metablob_intervalcount = v_num_iterations,'|| chr(10) ||
                  '                meta_starttime = v_ghost_data_obj.starttime,'|| chr(10) ||
                  '                meta_stoptime = v_ghost_data_obj.stoptime,'|| chr(10) ||
                  '                metablob_spi = v_ghost_data_obj.spi,'|| chr(10) ||
                  '                metablob_dst_participant = v_ghost_data_obj.dst_participant,'|| chr(10) ||
                  'CUSTOM_1 = v_ghost_data_obj.CUSTOM_1,' || chr(10) ||
                  'CUSTOM_2 = v_ghost_data_obj.CUSTOM_2,' || chr(10) ||
                  'CUSTOM_3 = v_ghost_data_obj.CUSTOM_3,' || chr(10) ||
                  'CUSTOM_4 = v_ghost_data_obj.CUSTOM_4,' || chr(10) ||
                  'CUSTOM_5 = v_ghost_data_obj.CUSTOM_5,' || chr(10) ||
                  'CUSTOM_6 = v_ghost_data_obj.CUSTOM_6,' || chr(10) ||
                  'CUSTOM_7 = v_ghost_data_obj.CUSTOM_7,' || chr(10) ||
                  'CUSTOM_8 = v_ghost_data_obj.CUSTOM_8,' || chr(10) ||
                  'CUSTOM_9 = v_ghost_data_obj.CUSTOM_9,' || chr(10) ||
                  'CUSTOM_10 = v_ghost_data_obj.CUSTOM_10,' || chr(10) ||
                  'CUSTOM_11 = v_ghost_data_obj.CUSTOM_11,' || chr(10) ||
                  'CUSTOM_12 = v_ghost_data_obj.CUSTOM_12,' || chr(10) ||
                  'CUSTOM_13 = v_ghost_data_obj.CUSTOM_13,' || chr(10) ||
                  'CUSTOM_14 = v_ghost_data_obj.CUSTOM_14,' || chr(10) ||
                  'CUSTOM_15 = v_ghost_data_obj.CUSTOM_15,' || chr(10) ||
                  'CUSTOM_16 = v_ghost_data_obj.CUSTOM_16,' || chr(10) ||
                  'CUSTOM_17 = v_ghost_data_obj.CUSTOM_17,' || chr(10) ||
                  'CUSTOM_18 = v_ghost_data_obj.CUSTOM_18,' || chr(10) ||
                  'CUSTOM_19 = v_ghost_data_obj.CUSTOM_19,' || chr(10) ||
                  'CUSTOM_20 = v_ghost_data_obj.CUSTOM_20,' || chr(10) ||
                  'CUSTOM_DATE_1 = v_ghost_data_obj.CUSTOM_DATE_1,' || chr(10) ||
                  'CUSTOM_DATE_2 = v_ghost_data_obj.CUSTOM_DATE_2,' || chr(10) ||
                  'CUSTOM_DATE_3 = v_ghost_data_obj.CUSTOM_DATE_3,' || chr(10) ||
                  'CUSTOM_DATE_4 = v_ghost_data_obj.CUSTOM_DATE_4,' || chr(10) ||
                  'CUSTOM_DATE_5 = v_ghost_data_obj.CUSTOM_DATE_5,' || chr(10) ||
                  'CUSTOM_DATE_6 = v_ghost_data_obj.CUSTOM_DATE_6,' || chr(10) ||
                  'CUSTOM_DATE_7 = v_ghost_data_obj.CUSTOM_DATE_7,' || chr(10) ||
                  'CUSTOM_DATE_8 = v_ghost_data_obj.CUSTOM_DATE_8,' || chr(10) ||
                  'CUSTOM_DATE_9 = v_ghost_data_obj.CUSTOM_DATE_9,' || chr(10) ||
                  'CUSTOM_DATE_10 = v_ghost_data_obj.CUSTOM_DATE_10,' || chr(10) ||
                  '                last_update_date = SYSDATE'|| chr(10) ||
                  '          WHERE uidvm = :p_uidvm_save;'|| chr(10) ||
                  '         COMMIT;' || chr(10) ||
                  */
                  'END;';
                  
                  /*
                  
         v_uidvm_save := p_uidvm;

         IF p_dest_cid IS NOT NULL THEN

            v_uidvm_save := Seq_GHOSTUIDVM.NEXTVAL;
            v_ghost_vm_row.uidvm := v_uidvm_save;
            v_ghost_vm_row.blob_value := v_blob;
--            v_ghost_vm_row.ghost_transaction_id := p_transaction_id;
            v_ghost_vm_row.ghost_collection_id := p_dest_cid;
            v_ghost_vm_row.meta_starttime := p_ghost_data_left.starttime;
            v_ghost_vm_row.meta_stoptime := p_ghost_data_left.stoptime;
            v_ghost_vm_row.metablob_spi := 86400/v_interval_count;
            v_ghost_vm_row.metablob_dst_participant := p_ghost_data_left.dst_participant;
            v_ghost_vm_row.metablob_total := v_metablob_total;
            v_ghost_vm_row.metablob_max := v_metablob_max;
            v_ghost_vm_row.metablob_min := v_metablob_min;
            v_ghost_vm_row.metablob_intervalcount := v_interval_count;

            build_custom_outputs(p_ghost_data_left,
                                 p_custom_columns,
                                 v_custom_column_values);

            insert_into_ghost_vm(CONST_BLOB_INSERT_COLUMN,
                                 p_custom_columns,
                                 v_custom_column_values,
                                 v_ghost_vm_row);

         ELSE
            UPDATE ghost_vm
              SET blob_value = v_blob,
                  metablob_total = v_metablob_total,
                  metablob_max = v_metablob_max,
                  metablob_min = v_metablob_min,
                  metablob_intervalcount = v_interval_count,
                  meta_starttime = p_ghost_data_left.starttime,
                  meta_stoptime = p_ghost_data_left.stoptime,
                  metablob_spi = p_ghost_data_left.spi,
                  metablob_dst_participant = p_ghost_data_left.dst_participant,
                  last_update_date = SYSDATE
            WHERE uidvm = v_uidvm_save;

         END IF;
                  */
                  
         EXECUTE IMMEDIATE v_sql USING p_ghost_data_obj, p_uidvm_save, p_dest_cid, p_custom_columns;--, p_uidvm, p_uidvm_save;
--         INSERT INTO del_me VALUES(v_sql);
         
    EXCEPTION
         WHEN e_blob_is_null THEN
         ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                 ||
                                           'BLOB_CREATEFLAG_HELPER ' ||
--                                           ' p_uidvm: ' || ghost_util.wrap_error_params(p_uidvm) ||
--                                           ' p_uidvm_save: ' || ghost_util.wrap_error_params(p_uidvm_save) ||
                                           ' p_rules_case: ' || ghost_util.wrap_error_params(p_rules_case),
                                           SQLERRM);
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                 ||
                                           'BLOB_CREATEFLAG_HELPER ' ||
--                                           ' p_uidvm: ' || ghost_util.wrap_error_params(p_uidvm) ||
--                                           ' p_uidvm_save: ' || ghost_util.wrap_error_params(p_uidvm_save) ||
                                           ' p_rules_case: ' || ghost_util.wrap_error_params(p_rules_case),
                                           SQLERRM);


  END blob_createflag_helper;


  PROCEDURE blob_createflag_uidvm(p_uidvm IN NUMBER,
                                  p_uidvm_save IN NUMBER,
                                  p_sql_query IN VARCHAR2,
                                  p_rules_case IN VARCHAR2) AS
  v_blob BLOB;
  
  v_select_blob_query_local VARCHAR2(200);
  v_select_blob_query_ext VARCHAR2(200);
  v_uidvm_save NUMBER;

  v_ghost_data ghost_data_obj;

  v_overwrite BOOLEAN := false;
  v_remote BOOLEAN;
  v_temp BLOB;

  BEGIN
        v_ghost_data := ghost_data_obj();
                                                       
        SELECT meta_starttime, 
               meta_stoptime, 
               metablob_spi, 
               metablob_dst_participant, 
               custom_1, 
               custom_2, 
               custom_3, 
               custom_4, 
               custom_5, 
               custom_6, 
               custom_7, 
               custom_8, 
               custom_9, 
               custom_10, 
               custom_11, 
               custom_12, 
               custom_13, 
               custom_14, 
               custom_15, 
               custom_16, 
               custom_17, 
               custom_18, 
               custom_19, 
               custom_20, 
               custom_date_1, 
               custom_date_2, 
               custom_date_3, 
               custom_date_4, 
               custom_date_5, 
               custom_date_6, 
               custom_date_7, 
               custom_date_8, 
               custom_date_9, 
               custom_date_10
          INTO  v_ghost_data.STARTTIME,
                v_ghost_data.STOPTIME,
                v_ghost_data.SPI,
                v_ghost_data.DST_PARTICIPANT,
                v_ghost_data.CUSTOM_1,
                v_ghost_data.CUSTOM_2,
                v_ghost_data.CUSTOM_3,
                v_ghost_data.CUSTOM_4,
                v_ghost_data.CUSTOM_5,
                v_ghost_data.CUSTOM_6,
                v_ghost_data.CUSTOM_7,
                v_ghost_data.CUSTOM_8,
                v_ghost_data.CUSTOM_9,
                v_ghost_data.CUSTOM_10,
                v_ghost_data.CUSTOM_11,
                v_ghost_data.CUSTOM_12,
                v_ghost_data.CUSTOM_13,
                v_ghost_data.CUSTOM_14,
                v_ghost_data.CUSTOM_15,
                v_ghost_data.CUSTOM_16,
                v_ghost_data.CUSTOM_17,
                v_ghost_data.CUSTOM_18,
                v_ghost_data.CUSTOM_19,
                v_ghost_data.CUSTOM_20,
                v_ghost_data.CUSTOM_DATE_1,
                v_ghost_data.CUSTOM_DATE_2,
                v_ghost_data.CUSTOM_DATE_3,
                v_ghost_data.CUSTOM_DATE_4,
                v_ghost_data.CUSTOM_DATE_5,
                v_ghost_data.CUSTOM_DATE_6,
                v_ghost_data.CUSTOM_DATE_7,
                v_ghost_data.CUSTOM_DATE_8,
                v_ghost_data.CUSTOM_DATE_9,
                v_ghost_data.CUSTOM_DATE_10
           FROM ghost_vm WHERE uidvm = p_uidvm;                                                       

        v_remote := get_blob(v_blob, p_sql_query, p_uidvm);
        v_ghost_data.blob_value := v_blob;

        blob_createflag_helper(v_ghost_data,
                               p_uidvm_save,                                   
                               p_rules_case,
                               NULL,
                               ALL_CUSTOM_COLUMNS);
        EXCEPTION
          WHEN OTHERS THEN
               ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                             ghost_util.CONST_ERROR_MSG                                       ||
                                             'BLOB_CREATEFLAG_UIDVM '                                               ||
                                             ' p_uidvm:' || ghost_util.wrap_error_params(p_uidvm) ||
                                             ' p_uidvm_save:' || ghost_util.wrap_error_params(p_uidvm_save) ||
                                             ' p_sql_query:' || ghost_util.wrap_error_params(p_sql_query),
--                                             ' p_operation:' || ghost_util.wrap_error_params(p_operation),
                                             SQLERRM);
  END blob_createflag_uidvm;  
  
  
  FUNCTION blob_collect_createflag_helper(p_data_left IN ghost_bulk_data_obj,
                                              p_dest_cid IN NUMBER,
                                              p_rules_case IN VARCHAR2) RETURN NUMBER AS

  v_ghost_data_left ghost_data_obj;
  v_custom_columns VARCHAR2(500);
  v_result_blob BLOB;

  BEGIN

     v_ghost_data_left := ghost_data_obj();

     IF p_data_left.blob_value.COUNT = 0 THEN
            RAISE e_no_records_match;
     END IF;


     v_custom_columns := ALL_CUSTOM_COLUMNS;


     FOR x IN p_data_left.blob_value.FIRST..p_data_left.blob_value.LAST LOOP
       v_ghost_data_left.blob_value := p_data_left.blob_value(x);
       v_ghost_data_left.uidvm := p_data_left.uidvm(x);
       v_ghost_data_left.starttime := p_data_left.starttime(x);
       v_ghost_data_left.stoptime := p_data_left.stoptime(x);
       v_ghost_data_left.spi := p_data_left.spi(x);
       v_ghost_data_left.intervalcount := p_data_left.intervalcount(x);
       v_ghost_data_left.dst_participant := p_data_left.dst_participant(x);

       v_ghost_data_left.custom_1 := p_data_left.custom_1(x);
       v_ghost_data_left.custom_2 := p_data_left.custom_2(x);
       v_ghost_data_left.custom_3 := p_data_left.custom_3(x);
       v_ghost_data_left.custom_4 := p_data_left.custom_4(x);
       v_ghost_data_left.custom_5 := p_data_left.custom_5(x);
       v_ghost_data_left.custom_6 := p_data_left.custom_6(x);
       v_ghost_data_left.custom_7 := p_data_left.custom_7(x);
       v_ghost_data_left.custom_8 := p_data_left.custom_8(x);
       v_ghost_data_left.custom_9 := p_data_left.custom_9(x);
       v_ghost_data_left.custom_10 := p_data_left.custom_10(x);

       v_ghost_data_left.custom_date_1 := p_data_left.custom_date_1(x);
       v_ghost_data_left.custom_date_2 := p_data_left.custom_date_2(x);
       v_ghost_data_left.custom_date_3 := p_data_left.custom_date_3(x);
       v_ghost_data_left.custom_date_4 := p_data_left.custom_date_4(x);
       v_ghost_data_left.custom_date_5 := p_data_left.custom_date_5(x);
       v_ghost_data_left.custom_date_6 := p_data_left.custom_date_6(x);
       v_ghost_data_left.custom_date_7 := p_data_left.custom_date_7(x);
       v_ghost_data_left.custom_date_8 := p_data_left.custom_date_8(x);
       v_ghost_data_left.custom_date_9 := p_data_left.custom_date_9(x);
       v_ghost_data_left.custom_date_10 := p_data_left.custom_date_10(x);


       blob_createflag_helper(v_ghost_data_left,
                              NULL,
                              p_rules_case,
                              p_dest_cid,
                              ALL_CUSTOM_COLUMNS);
       END LOOP;


       RETURN p_data_left.blob_value.COUNT;
     EXCEPTION
         WHEN e_no_records_match THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                   ||
                                           'BLOB_COLLECT_CREATEFLAG_HELPER: No records matched! '           ||
--                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation) ||
--                                           ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                           ' p_dest_cid:' || ghost_util.wrap_error_params(p_dest_cid),
                                           SQLERRM);
         WHEN OTHERS THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                   ||
                                           'BLOB_COLLECT_CREATEFLAG_HELPER  '                               ||
--                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation) ||
--                                           ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                           ' p_dest_cid:' || ghost_util.wrap_error_params(p_dest_cid),
                                           SQLERRM);

  END blob_collect_createflag_helper;
  
  
  FUNCTION blob_collection_createflags(p_dest_cid IN NUMBER,
                                       p_sql_query IN VARCHAR2,
                                       p_rules_case IN VARCHAR2) RETURN NUMBER AS


  v_data_left ghost_bulk_data_obj;
  v_save_uidvm ghost_tab_number;
  v_count NUMBER;

  BEGIN

     v_data_left := ghost_bulk_data_obj();


              EXECUTE IMMEDIATE p_sql_query
                               BULK COLLECT INTO v_data_left.blob_value,
                                                 v_data_left.uidvm,
                                                 v_data_left.starttime,
                                                 v_data_left.stoptime,
                                                 v_data_left.spi,
                                                 v_data_left.intervalcount,
                                                 v_data_left.dst_participant,
                                                 v_data_left.custom_1,
                                                 v_data_left.custom_2,
                                                 v_data_left.custom_3,
                                                 v_data_left.custom_4,
                                                 v_data_left.custom_5,
                                                 v_data_left.custom_6,
                                                 v_data_left.custom_7,
                                                 v_data_left.custom_8,
                                                 v_data_left.custom_9,
                                                 v_data_left.custom_10,
                                                 v_data_left.custom_11,
                                                 v_data_left.custom_12,
                                                 v_data_left.custom_13,
                                                 v_data_left.custom_14,
                                                 v_data_left.custom_15,
                                                 v_data_left.custom_16,
                                                 v_data_left.custom_17,
                                                 v_data_left.custom_18,
                                                 v_data_left.custom_19,
                                                 v_data_left.custom_20,
                                                 v_data_left.custom_date_1,
                                                 v_data_left.custom_date_2,
                                                 v_data_left.custom_date_3,
                                                 v_data_left.custom_date_4,
                                                 v_data_left.custom_date_5,
                                                 v_data_left.custom_date_6,
                                                 v_data_left.custom_date_7,
                                                 v_data_left.custom_date_8,
                                                 v_data_left.custom_date_9,
                                                 v_data_left.custom_date_10;

     v_count := blob_collect_createflag_helper(v_data_left,
                                               p_dest_cid,
                                               p_rules_case);

     COMMIT;
     RETURN v_count;
     EXCEPTION
         WHEN e_no_records_match THEN
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                       ||
                                           'BLOB_COLLECTION_CREATEFLAGS No records matched! '                       ||
--                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation) ||
--                                           ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                           ' p_dest_cid:' || ghost_util.wrap_error_params(p_dest_cid),
                                           SQLERRM);
         WHEN OTHERS THEN
             --Need to close open blobs in same transaction!
             ghost_util.raise_ghost_error (ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                                           ghost_util.CONST_ERROR_MSG                                       ||
                                           'BLOB_COLLECTION_CREATEFLAGS  '                                          ||
--                                           ' p_operation:' || ghost_util.wrap_error_params(p_operation) ||
--                                           ' p_custom_columns:' || ghost_util.wrap_error_params(p_custom_columns) ||
                                           ' p_dest_cid:' || ghost_util.wrap_error_params(p_dest_cid),
                                           SQLERRM);

  END blob_collection_createflags;



END GHOST_BLOB_UTIL;
/
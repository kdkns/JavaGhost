CREATE OR REPLACE PACKAGE ghost_util AS

-- ==============================================================================
-- $Id: Ghost_Util.sql,v 1.9 2012/09/13 20:33:44 arohatgi Exp $
--
-- Copyright(c) 2001-2012 ERCOT. All rights reserved.
--
-- THIS PROGRAM IS AN UNPUBLISHED  WORK AND TRADE SECRET OF THE COPYRIGHT HOLDER,
-- AND DISTRIBUTED ONLY UNDER RESTRICTION.
--
-- No  part  of  this  program  may be used,  installed,  displayed,  reproduced,
-- distributed or modified  without the express written consent  of the copyright
-- holder.
--
-- EXCEPT AS EXPLICITLY STATED  IN A WRITTEN  AGREEMENT BETWEEN  THE PARTIES, THE
-- SOFTWARE IS PROVIDED AS-IS, WITHOUT WARRANTIES OF ANY KIND, EXPRESS OR IMPLIED,
-- INCLUDING THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A PARTICULAR
-- PURPOSE, NONINFRINGEMENT, PERFORMANCE, AND QUALITY.
--
-- [ERCOT CS7.6],[CIP-003 R4] - ERCOT Restricted
-- ==============================================================================

    TYPE v_blob_tab IS TABLE OF BLOB;

    -- Version
    --
    CONST_VERSION CONSTANT VARCHAR2(20) := '$Revision: 1.9 $';

    -- Constants
    --
    CONST_BATCH_STATUS_START   CONSTANT ghost_pcf_log.status%TYPE := 'STARTED';
    CONST_BATCH_STATUS_OK      CONSTANT ghost_pcf_log.status%TYPE := 'COMPLETE';
    CONST_BATCH_STATUS_WARNING CONSTANT ghost_pcf_log.status%TYPE := 'WARNING';
    CONST_BATCH_STATUS_WARNING_D CONSTANT ghost_pcf_log.status%TYPE := 'WARNING W/ DEFAULT';
    CONST_BATCH_STATUS_ERROR   CONSTANT ghost_pcf_log.status%TYPE := 'ERROR';
    CONST_BATCH_STATUS_FAILED  CONSTANT ghost_pcf_log.status%TYPE := 'FAILED';

    CONST_TOKEN CONSTANT VARCHAR2(5) := ' - ';

    -- This is the error level contract parameters with Tibco ( Orchestrator Framework )
    -- To change the actual build of the format look in : build_error_level_contract
    --
    CONST_ERROR_CONTRACT_TOKEN     CONSTANT VARCHAR2(5)  := ' [^] ';
    CONST_ERROR_CONTRACT_BRACKET_L CONSTANT VARCHAR2(5)  := '[';
    CONST_ERROR_CONTRACT_BRACKET_R CONSTANT VARCHAR2(5)  := ']';
    CONST_ERROR_CONTRACT_ERRORID   CONSTANT VARCHAR2(25) := 'ErrorId:';
    CONST_ERROR_CONTRACT_NUMRECS   CONSTANT VARCHAR2(25) := 'NumberOfErrors:';

    -- Used for formating the error message and error parameters. Error parameters
    -- are formated using the wrap_error_params function.
    --
    CONST_ERROR_MSG       CONSTANT VARCHAR2(100) := 'Failure in : ';
    CONST_ERROR_MSG_PARAM CONSTANT VARCHAR2(100) := ' with values ';
    CONST_ERROR_PARAM_L   CONSTANT VARCHAR2(10)  := '<';
    CONST_ERROR_PARAM_R   CONSTANT VARCHAR2(10)  := '>';

    -- Error codes to be used througout the framework.
    --
    COSNT_PCF_ERROR_UNKNOWN    CONSTANT NUMBER := -20000; -- Undefined Error
    COSNT_PCF_ERROR_MISSINGSQL CONSTANT NUMBER := -20003; -- Undefined Error
    COSNT_PCF_ERROR_DATA_ERROR CONSTANT NUMBER := -20499; -- User Defined Data Error
    COSNT_PCF_ERROR_APP_ERROR  CONSTANT NUMBER := -20999; -- User Defined Application Error
    CONST_PCF_ERROR_RLERROR    CONSTANT NUMBER := -20399;
    CONST_PCF_ERROR_CJERROR    CONSTANT NUMBER := -20299; -- Job failure Error

    bulk_errors   EXCEPTION;
    PRAGMA EXCEPTION_INIT (bulk_errors, -24381); --TODO: Make constant

    sequence_error  EXCEPTION;
    PRAGMA EXCEPTION_INIT (sequence_error , -24361);

    -- Used throught framework for certain calculations.
    --
    CONST_SEC_IN_HOUR              CONSTANT NUMBER := 3600;
    CONST_ADD_HOUR_TIME            CONSTANT NUMBER := CONST_SEC_IN_HOUR/86400;
    CONST_FRAMEWORK_SPECIAL_OFFSET CONSTANT NUMBER := 0;

    --Constant definitions for getSequence function.
    --
    CONST_SEQ_ERROR_VAL        CONSTANT NUMBER := -999999;
    CONST_SEQ_GHOSTUIDERROR    CONSTANT VARCHAR2(50):= 'GHOSTUIDERROR';
    CONST_SEQ_GHOSTUIDERRORGRP CONSTANT VARCHAR2(50):= 'GHOSTUIDERRORGRP';
    CONST_SEQ_GHOSTLOG         CONSTANT VARCHAR2(50):= 'GHOSTLOG';

    --Constant definitions for DST
    --
    CONST_IN_DST       CONSTANT NUMBER := -1;
    CONST_ON_START_DST CONSTANT NUMBER := 0;
    CONST_ON_END_DST   CONSTANT NUMBER := 1;
    CONST_OUT_DST      CONSTANT NUMBER := 2;
    CONST_DST_ERROR    CONSTANT NUMBER := -999999;
    CONST_NIC_ERROR    CONSTANT NUMBER := -1;

    --Array of Strings type is used for compute logic in functions to build
    -- dynamic bind variables.
    --
    TYPE array_of_strings IS VARRAY (50) OF VARCHAR2(4000);
    TYPE array_of_anydata IS VARRAY (50) OF ANYDATA;

    SUBTYPE message_length IS VARCHAR2(100);

    CONST_FRAMEWORK_INTGROUP CONSTANT NUMBER := 3600;
    CONST_SEC_IN_DAY         CONSTANT NUMBER:= 86400;

    --Token used to wrap error sequence number
    --
    CONST_ERROR_SEQ_TOKEN_L     CONSTANT VARCHAR2(5) := '[$<|';
    CONST_ERROR_SEQ_TOKEN_R     CONSTANT VARCHAR2(5) := '|>$]';
    CONST_ERROR_SEQ_TOKEN_NSEQ  CONSTANT NUMBER := -99;
    CONST_ERROR_SEQ_TOKEN_ERROR CONSTANT NUMBER := -9999;

    --Used by get_token function
    --
    CONST_ERROR_GET_TOKEN_NFOUND CONSTANT NUMBER := -77;
    CONST_ERROR_GET_TOKEN_ERROR  CONSTANT NUMBER := -7777;

    --Defined and used be Execute API
    --
    TYPE PIP IS RECORD (
        pcf_bid    NUMBER,
        pcf_eid    NUMBER,
        pcf_cid    NUMBER,
        pcf_aid    NUMBER,
        pcf_op     VARCHAR(35),
        action_def VARCHAR2(25)
    );

    --Constants for Date conversion
    --
    CONST_FRAMEWORK_DATE_MAP CONSTANT VARCHAR2(50) := 'YYYY-MM-DD HH24:MI:SS';

    --Log Table Names
    --
    CONST_BATCH_TABLE          CONSTANT VARCHAR2(25) := 'ghost_PCF_LOG';
    CONST_BATCH_TABLE_PKCOLUMN CONSTANT VARCHAR2(25) := 'UIDLOG';

    -- PROCEDURE DECLARATION
    --
    FUNCTION write_ghost_error (
        p_pcf_bid IN NUMBER,
        p_pcf_eid   IN NUMBER,
        p_pcf_cid IN NUMBER,
        p_pcf_aid IN NUMBER,
        p_pcf_op  IN VARCHAR2,
        p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
        p_err_msg IN ghost_action_error.message%TYPE,
        p_sqlerrm IN VARCHAR2 := NULL,
        p_parameters IN ghost_action_error.parameters%TYPE := NULL,
        p_table_name ghost_action_error.table_name%TYPE := NULL,
        p_column_name ghost_action_error.column_name%TYPE := NULL,
        p_record_id ghost_action_error.record_id%TYPE := NULL) RETURN NUMBER;

    PROCEDURE write_ghost_error_no_return (p_pcf_bid IN NUMBER,
                                         p_pcf_eid IN NUMBER,
                                         p_pcf_cid IN NUMBER,
                                         p_pcf_aid IN NUMBER,
                                         p_pcf_op IN VARCHAR2,
                                         p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                                         p_err_msg IN ghost_action_error.message%TYPE,
                                         p_sqlerrm IN VARCHAR2 := NULL,
                                         p_parameters IN ghost_action_error.parameters%TYPE := NULL,
                                         p_table_name ghost_action_error.table_name%TYPE := NULL,
                                         p_column_name ghost_action_error.column_name%TYPE := NULL);

    PROCEDURE write_ghost_error (p_uiderror IN ghost_action_error.uiderror%TYPE,
                               p_pcf_bid IN NUMBER,
                               p_pcf_eid IN NUMBER,
                               p_pcf_cid IN NUMBER,
                               p_pcf_aid IN NUMBER,
                               p_pcf_op IN VARCHAR2,
                               p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                               p_err_msg IN ghost_action_error.message%TYPE,
                               p_sqlerrm IN VARCHAR2 := NULL,
                               p_parameters IN ghost_action_error.parameters%TYPE := NULL,
                               p_table_name ghost_action_error.table_name%TYPE := NULL,
                               p_column_name ghost_action_error.column_name%TYPE := NULL,
                               p_record_id ghost_action_error.record_id%TYPE := NULL);

    PROCEDURE write_ghost_detail_error (p_uiderror IN ghost_action_error.uiderror%TYPE,
                                      p_record_id IN ghost_action_error_detail.record_id%TYPE,
                                      p_ghost_error_code IN ghost_action_error_detail.errorcode%TYPE,
                                      p_err_msg IN ghost_action_error_detail.message%TYPE,
                                      p_sqlerrm IN VARCHAR2 := NULL,
                                      p_parameters IN ghost_action_error_detail.parameters%TYPE := NULL,
                                      p_table_name ghost_action_error_detail.table_name%TYPE := NULL,
                                      p_column_name ghost_action_error_detail.column_name%TYPE := NULL);

    PROCEDURE write_ghost_detail_error_cjob (p_uiderror IN ghost_action_error.uiderror%TYPE,
                                           p_record_id IN ghost_action_error_detail.record_id%TYPE,
                                           p_ghost_error_code IN ghost_action_error_detail.errorcode%TYPE,
                                           p_err_msg IN ghost_action_error_detail.message%TYPE,
                                           p_sqlerrm IN VARCHAR2 := NULL,
                                           p_parameters IN ghost_action_error_detail.parameters%TYPE := NULL,
                                           p_table_name ghost_action_error_detail.table_name%TYPE := NULL,
                                           p_column_name ghost_action_error_detail.column_name%TYPE := NULL);

    PROCEDURE write_ghost_validation_error (p_uiderror IN ghost_action_error.uiderror%TYPE,
                                          p_record_id IN ghost_action_error_detail.record_id%TYPE,
                                          p_ghost_error_code IN ghost_action_error_detail.errorcode%TYPE,
                                          p_err_msg IN ghost_action_error_detail.message%TYPE,
                                          p_sqlerrm IN VARCHAR2 := NULL,
                                          p_parameters IN ghost_action_error_detail.parameters%TYPE := NULL,
                                          p_table_name ghost_action_error_detail.table_name%TYPE := NULL,
                                          p_column_name ghost_action_error_detail.column_name%TYPE := NULL);

   PROCEDURE raise_ghost_error_cjob (p_error_id IN NUMBER,
                                   p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                                   p_err_msg IN ghost_action_error.message%TYPE,
                                   p_sqlerrm IN VARCHAR2 := NULL,
                                   p_write_to_detail IN BOOLEAN := TRUE,
                                   p_raise_clean IN BOOLEAN := FALSE);

   PROCEDURE raise_ghost_error (p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                              p_err_msg IN ghost_action_error.message%TYPE,
                              p_sqlerrm IN VARCHAR2 := NULL,
                              p_write_to_detail IN BOOLEAN := TRUE,
                              p_raise_clean IN BOOLEAN := FALSE);

    PROCEDURE raise_ghost_error_write (p_pcf_bid IN NUMBER,
                                     p_pcf_eid IN NUMBER,
                                     p_pcf_cid IN NUMBER,
                                     p_pcf_aid IN NUMBER,
                                     p_pcf_op IN VARCHAR2,
                                     p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                                     p_err_msg IN ghost_action_error.message%TYPE,
                                     p_sqlerrm IN VARCHAR2 := NULL,
                                     p_parameters IN ghost_action_error.parameters%TYPE := NULL,
                                     p_table_name ghost_action_error.table_name%TYPE := NULL,
                                     p_column_name ghost_action_error.column_name%TYPE := NULL,
                                     p_record_id ghost_action_error.record_id%TYPE := NULL);

    PROCEDURE raise_ghost_error_write (p_pip PIP,
                                     p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                                     p_err_msg IN ghost_action_error.message%TYPE,
                                     p_sqlerrm IN VARCHAR2 := NULL,
                                     p_parameters IN ghost_action_error.parameters%TYPE := NULL,
                                     p_table_name ghost_action_error.table_name%TYPE := NULL,
                                     p_column_name ghost_action_error.column_name%TYPE := NULL);


    PROCEDURE update_batch_status (p_uidlog IN NUMBER,
                                   p_status IN ghost_pcf_log.status%TYPE,
                                   p_msg IN ghost_pcf_log.message%TYPE := NULL,
                                   p_record_count IN ghost_pcf_log.record_count%TYPE := NULL);

    FUNCTION insert_batch_status (p_pcf_bid IN NUMBER,
                                  p_pcf_eid IN NUMBER,
                                  p_pcf_cid IN NUMBER,
                                  p_pcf_aid IN NUMBER,
                                  p_pcf_op IN VARCHAR2,
                                  p_status IN ghost_pcf_log.status%TYPE,
                                  p_process_type IN ghost_pcf_log.process_type%TYPE,
                                  p_action IN ghost_pcf_log.action%TYPE,
                                  p_msg IN ghost_pcf_log.message%TYPE := NULL) RETURN NUMBER;

    FUNCTION insert_batch_status (p_pip IN PIP,
                                  p_status IN ghost_pcf_log.status%TYPE,
                                  p_process_type IN ghost_pcf_log.process_type%TYPE,
                                  p_msg IN ghost_pcf_log.message%TYPE := NULL) RETURN NUMBER;

    FUNCTION split_params(p_params IN VARCHAR2,
                          p_seperator IN VARCHAR2,
                          o_array_values IN OUT ghost_util.array_of_strings
                          ) RETURN NUMBER;

    PROCEDURE ghost_write_raw_to_blob (p_blob IN OUT BLOB,
                                     p_raw IN RAW);


    PROCEDURE ghost_write_raw_to_blob(p_blob IN OUT BLOB,
                                      p_raw IN RAW,
                                      p_offset IN INTEGER);

    PROCEDURE ghost_write_varchar2_to_blob (p_blob IN OUT BLOB,
                                          p_varchar2 VARCHAR2);

    PROCEDURE ghost_write_varchar2_to_blob (p_blob IN OUT BLOB,
                                            p_varchar2 IN VARCHAR2,
                                            p_offset IN INTEGER);

   PROCEDURE g_write_binarydouble_to_blob(p_blob IN OUT BLOB,
                                          p_binary_double IN BINARY_DOUBLE,
                                          p_offset IN NUMBER);
  --  FUNCTION get_number_of_hours(p_op_date IN DATE) RETURN NUMBER;


    FUNCTION getSequence(p_seq_name VARCHAR2) RETURN NUMBER;

    FUNCTION getCurrentUser RETURN VARCHAR2;

    FUNCTION wrap_error_params(p_param VARCHAR2) RETURN VARCHAR2;



/*
    FUNCTION check_DST(p_date IN DATE,
                       p_check_hour IN BOOLEAN DEFAULT FALSE) RETURN NUMBER;

    FUNCTION check_DST(p_date IN DATE,
                        o_start_dst IN OUT DATE,
                        o_end_dst IN OUT DATE,
                        p_check_hour IN BOOLEAN DEFAULT FALSE) RETURN NUMBER;

    FUNCTION check_DST(p_date IN DATE,
                        p_check_hour IN NUMBER) RETURN NUMBER;

    FUNCTION get_CPT(p_date IN DATE) RETURN DATE;

    FUNCTION get_CPT(p_date IN DATE,
--                     p_check_dst IN NUMBER,
                     p_start_dst IN DATE,
                     p_end_dst IN DATE) RETURN DATE;

    FUNCTION get_CST(p_date IN DATE,
                     p_repeat_hour IN VARCHAR2) RETURN DATE;

    FUNCTION get_Business_CPT(p_date IN DATE,
                              p_offset IN NUMBER := 0) RETURN DATE;

    FUNCTION get_Business_CPT(p_date IN DATE,
                               p_offset IN NUMBER,
                               o_repeat_hour IN OUT VARCHAR2) RETURN DATE;

    FUNCTION get_Business_CST(p_date IN DATE,
                              p_repeat_hour IN VARCHAR2) RETURN DATE;

    FUNCTION get_repeat_hour(p_date IN DATE,
                             p_offset IN NUMBER := 0) RETURN VARCHAR2;
*/
    FUNCTION get_version RETURN VARCHAR2;

    PROCEDURE update_log_stop_time(p_uidlog IN NUMBER,
                                   p_stop_time IN TIMESTAMP := SYSTIMESTAMP);

    FUNCTION get_error_seq(p_msg IN VARCHAR2) RETURN NUMBER                                   ;

    FUNCTION get_seconds_from_interval(p_interval interval day to second) return number;
/*
    FUNCTION GMTtoCST(p_date IN DATE) RETURN DATE;

    FUNCTION GMTtoCPT(p_date IN DATE) RETURN DATE;
*/
    FUNCTION getFDate(p_vdate IN VARCHAR2) RETURN DATE;
/*
    PROCEDURE getStartEndDSTDates( p_date IN DATE,
                                   o_start_date IN OUT DATE,
                                   o_end_date IN OUT DATE);

    FUNCTION getStartDSTDate(p_date IN DATE) RETURN DATE;

    FUNCTION getEndDSTDate(p_date IN DATE) RETURN DATE;
*/
    FUNCTION getDateToF(p_date IN DATE) RETURN VARCHAR2;

    FUNCTION convert_number_to_anydata(p_number IN NUMBER) RETURN ANYDATA;

    FUNCTION convert_varchar_to_anydata(p_varchar IN VARCHAR2) RETURN ANYDATA;

    PROCEDURE bind_anydata_to_cursor(p_cursor IN INTEGER,
                                     p_names IN ghost_array_of_strings,
                                     p_values IN ghost_array_of_anydata);
END ghost_UTIL;
/

create or replace package body ghost_UTIL as
    /*
    SUB   : build_error_contract
            Builds string containing the errorid in the format of the contract between Orchestrator
            and framework.
    PARAM : p_error_id - The primary key value of the error in the error table.
    RETURN: VARCHAR2
    */
    FUNCTION build_error_contract( p_error_id IN NUMBER) RETURN VARCHAR2 IS
    BEGIN
          RETURN CONST_ERROR_CONTRACT_TOKEN         ||
                 CONST_ERROR_CONTRACT_ERRORID       ||
                 CONST_ERROR_CONTRACT_BRACKET_L     ||
                 p_error_id                         ||
                 CONST_ERROR_CONTRACT_BRACKET_R;

          EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG            ||
                                        'BUILD_ERROR_CONTRACT '    ||
                                        ' p_error_id:' || wrap_error_params(p_error_id),
                                        SQLERRM);
    END build_error_contract;

    /*
    SUB   : get_token
            Extracts a token from a string wrapped by a left and right token.
    PARAM : p_msg - The String that contains the sequence number
            p_left_token - The left token before the item to extract
            p_right_token - The right token before the item to extract
    RETURN: VARCHAR2
    */
    FUNCTION get_token(p_msg IN VARCHAR2,
                       p_left_token  IN VARCHAR2,
                       p_right_token  IN VARCHAR2) RETURN VARCHAR2 IS
           v_token NUMBER;
           v_start_pos NUMBER;
           v_tmp VARCHAR2(4000);
     BEGIN
           IF p_msg IS NULL THEN
              RETURN CONST_ERROR_GET_TOKEN_NFOUND;
           END IF;

           --dbms_output.put_line('Message: ' || p_msg);

           v_start_pos := INSTR(p_msg,p_left_token);

           --dbms_output.put_line('Stat pos: ' || v_start_pos);

           IF v_start_pos != 0 THEN
           --dbms_output.put_line('In IF');
               v_tmp := SUBSTR(p_msg,INSTR(p_msg,p_left_token) + LENGTH(p_left_token));
               v_token :=  SUBSTR(v_tmp, 1, INSTR(v_tmp,p_right_token) - 1);

           --dbms_output.put_line('Seq_num in IF: ' || v_seq_num);
                 IF v_token IS NULL THEN
                    RETURN CONST_ERROR_GET_TOKEN_ERROR;
                 END IF;

              ELSE
               RETURN CONST_ERROR_GET_TOKEN_NFOUND;
            END IF;

          --dbms_output.put_line('Seq_num: ' || v_seq_num);

          RETURN v_token;

           EXCEPTION
               WHEN OTHERS THEN
               RAISE;
               /*
                    raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG                                          ||
                                        ' get_error_seq'                                  ||
                                        ' p_msg:' || wrap_error_params(p_msg),
                                        SQLERRM);*/
     END get_token;

    /*
    SUB   : get_error_seq
            Extracts sequence number form an error.
    PARAM : p_msg - The String that contains the sequence number
    RETURN: NUMBER
    */
    FUNCTION get_error_seq(p_msg IN VARCHAR2) RETURN NUMBER IS
           v_seq_num NUMBER;
     BEGIN

          v_seq_num:=  get_token(p_msg,
                                 CONST_ERROR_SEQ_TOKEN_L,
                                 CONST_ERROR_SEQ_TOKEN_R);
          -- dbms_output.put_line(v_seq_num);
          IF v_seq_num = CONST_ERROR_GET_TOKEN_ERROR THEN
                 RETURN CONST_ERROR_SEQ_TOKEN_ERROR;
          END IF;

          IF v_seq_num = CONST_ERROR_GET_TOKEN_NFOUND THEN
                 RETURN CONST_ERROR_SEQ_TOKEN_NSEQ;
          END IF;

          RETURN v_seq_num;

          EXCEPTION
              WHEN OTHERS THEN
              RAISE;
               /*
                    raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG                                          ||
                                        ' get_error_seq'                                  ||
                                        ' p_msg:' || wrap_error_params(p_msg),
                                        SQLERRM);*/
     END get_error_seq;

    /*
    SUB   : ghost_error_helper
            A helper function used by higher level functions that write or raise errors.
            Inserts data for the different fields of the error table.
    PARAM : p_uiderror - A primary key of the error table.
            p_pcf_bid - Orchestrator Framework batch id.
            p_pcf_cid - Orchestrator Framework control process id.
            p_pcf_aid - Orchestrator Framework action id.
            p_ghost_error_code - Oracle/User Defined error code
            p_err_msg - Error message to write to table.
            p_sqlerrm - SQL error
            p_parameters - Any parameters that were used at the time that need to be recorded into the error table
            p_table_name - The table in which error happend, if any.
            p_column_name - The table column in which error happend, if any.
    RETURN: NUMBER
    */
    FUNCTION ghost_error_helper(p_uiderror IN ghost_action_error.uiderror%TYPE,
                              p_pcf_bid IN NUMBER,
                              p_pcf_eid IN NUMBER,
                              p_pcf_cid IN NUMBER,
                              p_pcf_aid IN NUMBER,
                              p_pcf_op IN VARCHAR2,
                              p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                              p_err_msg IN ghost_action_error.message%TYPE,
                              p_sqlerrm IN VARCHAR2 := NULL,
                              p_parameters IN ghost_action_error.parameters%TYPE := NULL,
                              p_table_name IN ghost_action_error.table_name%TYPE := NULL,
                              p_column_name IN ghost_action_error.column_name%TYPE := NULL,
                              p_record_id ghost_action_error.record_id%TYPE := NULL) RETURN NUMBER IS
        PRAGMA AUTONOMOUS_TRANSACTION;
        v_error_id NUMBER;
        v_err_msg_contract VARCHAR2(100);

        BEGIN

            v_error_id := p_uiderror;
            v_err_msg_contract :=  build_error_contract(v_error_id);

            --dbms_output.put_line(p_pcf_op);

            INSERT INTO ghost_action_error ( UIDERROR,
                                           batch_id,
                                           event_id,
                                           control_id,
                                           action_id,
                                           operating_date,
                                           errorcode,
                                           message,
                                           parameters,
                                           table_name,
                                           column_name,
                                           record_id)
                                    VALUES (v_error_id,
                                            p_pcf_bid,
                                            p_pcf_eid,
                                            p_pcf_cid,
                                            p_pcf_aid,
                                            TO_DATE(p_pcf_op,CONST_FRAMEWORK_DATE_MAP),    --This does not use the function to gurauntee no points of failure.
                                            p_ghost_error_code,
                                            p_err_msg || CONST_TOKEN || p_sqlerrm || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || v_err_msg_contract,
                                            p_parameters,
                                            p_table_name,
                                            p_column_name,
                                            p_record_id);
            COMMIT;

            RETURN v_error_id;

            EXCEPTION
                  WHEN OTHERS THEN
                       RAISE;
                       /*(
                       raise_ghost_error(COSNT_PCF_ERROR_UNKNOWN,
                                      CONST_ERROR_MSG || 'ghost_ERROR_HELPER with ERROR_ID ',
                                       SQLERRM);
                                       */
        END ghost_error_helper;


    /*
    SUB   : ghost_error_helper
            A helper function used by higher level functions that write or raise errors.
            Inserts data for the different fields of the error table.
            This version will get the next unique sequence number for the error table and pass it in
            to the base ghost_error_helper.
    PARAM : p_uiderror - A primary key of the error table.
            p_pcf_bid - Orchestrator Framework batch id.
            p_pcf_cid - Orchestrator Framework control process id.
            p_pcf_aid - Orchestrator Framework action id.
            p_ghost_error_code - Oracle/User Defined error code
            p_err_msg - Error message to write to table.
            p_sqlerrm - SQL error
            p_parameters - Any parameters that were used at the time that need to be recorded into the error table
            p_table_name - The table in which error happend, if any.
            p_column_name - The table column in which error happend, if any.
    RETURN: NUMBER
    */
    FUNCTION ghost_error_helper(p_pcf_bid IN NUMBER,
                              p_pcf_eid IN NUMBER,
                              p_pcf_cid IN NUMBER,
                              p_pcf_aid IN NUMBER,
                              p_pcf_op IN VARCHAR2,
                              p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                              p_err_msg IN ghost_action_error.message%TYPE,
                              p_sqlerrm IN VARCHAR2 := NULL,
                              p_parameters IN ghost_action_error.parameters%TYPE := NULL,
                              p_table_name ghost_action_error.table_name%TYPE := NULL,
                              p_column_name ghost_action_error.column_name%TYPE := NULL,
                              p_record_id ghost_action_error.record_id%TYPE := NULL) RETURN NUMBER IS
          v_error_id NUMBER;
    BEGIN
          v_error_id := getSequence(CONST_SEQ_GHOSTUIDERROR);

          RETURN ghost_error_helper(v_error_id,
                                  p_pcf_bid,
                                  p_pcf_eid,
                                  p_pcf_cid,
                                  p_pcf_aid,
                                  p_pcf_op,
                                  p_ghost_error_code,
                                  p_err_msg,
                                  p_sqlerrm,
                                  p_parameters,
                                  p_table_name,
                                  p_column_name,
                                  p_record_id);
          EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error(COSNT_PCF_ERROR_UNKNOWN,
                                       CONST_ERROR_MSG || 'ghost_ERROR_HELPER ',
                                       SQLERRM);
    END ghost_error_helper;


    /*
    SUB   : ghost_error_helper_detail
            A helper function used by higher level functions that write errors to
            the detail level error table. Inserts data for the different fields of
            the error table.
    PARAM : p_uiderror - A primary key of the error table.
            p_record_id - The unique error id of the parent error table. This joins
                          a error id to the errors in the detail error table.
            p_ghost_error_code - Oracle/User Defined error code
            p_err_msg - Error message to write to table.
            p_sqlerrm - SQL error
            p_parameters - Any parameters that were used at the time that need to be recorded into the error table
            p_table_name - The table in which error happend, if any.
            p_column_name - The table column in which error happend, if any.
    RETURN: None
    */
    PROCEDURE ghost_error_helper_detail(p_uiderror IN ghost_action_error.uiderror%TYPE,
                                      p_record_id IN ghost_action_error_detail.record_id%TYPE,
                                      p_ghost_error_code IN ghost_action_error_detail.errorcode%TYPE,
                                      p_err_msg IN ghost_action_error_detail.message%TYPE,
                                      p_sqlerrm IN VARCHAR2 := NULL,
                                      p_parameters IN ghost_action_error_detail.parameters%TYPE := NULL,
                                      p_table_name ghost_action_error_detail.table_name%TYPE := NULL,
                                      p_column_name ghost_action_error_detail.column_name%TYPE := NULL) IS
            PRAGMA AUTONOMOUS_TRANSACTION;
            v_msg VARCHAR2(4000);
            v_csiuid_grp_error NUMBER;
        BEGIN

            IF p_sqlerrm IS NULL THEN
               v_msg:= p_err_msg;
            ELSE
               v_msg:= p_err_msg || CONST_TOKEN || p_sqlerrm;
            END IF;

            v_csiuid_grp_error := getSequence(CONST_SEQ_GHOSTUIDERRORGRP);

            INSERT INTO ghost_action_error_Detail(UIDERRORDETAIL,
                                                UIDERROR,
                                                record_id,
                                                errorcode,
                                                message,
                                                parameters,
                                                table_name,
                                                column_name)
                                        VALUES (v_csiuid_grp_error,
                                                p_uiderror,
                                                p_record_id,
                                                p_ghost_error_code,
                                                v_msg,
                                                p_parameters,
                                                p_table_name,
                                                p_column_name);
            COMMIT;

            EXCEPTION
                WHEN OTHERS THEN
                  RAISE;
                  /*
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG            ||
                                        'ghost_ERROR_HELPER_DETAIL ',
                                        SQLERRM);
                                        */
        END ghost_error_helper_detail;


    /*
    SUB   : ghost_error_helper_detail_cjob
            A helper function used by higher level functions that write errors to
            the detail level error table. Inserts data for the different fields of
            the error table for child job process.
    PARAM : p_uiderror - A primary key of the error table.
            p_record_id - The unique error id of the parent error table. This joins
                          a error id to the errors in the detail error table.
            p_ghost_error_code - Oracle/User Defined error code
            p_err_msg - Error message to write to table.
            p_sqlerrm - SQL error
            p_parameters - Any parameters that were used at the time that need to be recorded into the error table
            p_table_name - The table in which error happend, if any.
            p_column_name - The table column in which error happend, if any.
    RETURN: None
    */
    PROCEDURE ghost_error_helper_detail_cjob(p_uiderror IN ghost_action_error.uiderror%TYPE,
                                           p_record_id IN ghost_action_error_detail.record_id%TYPE,
                                           p_ghost_error_code IN ghost_action_error_detail.errorcode%TYPE,
                                           p_err_msg IN ghost_action_error_detail.message%TYPE,
                                           p_sqlerrm IN VARCHAR2 := NULL,
                                           p_parameters IN ghost_action_error_detail.parameters%TYPE := NULL,
                                           p_table_name ghost_action_error_detail.table_name%TYPE := NULL,
                                           p_column_name ghost_action_error_detail.column_name%TYPE := NULL) IS
            PRAGMA AUTONOMOUS_TRANSACTION;
            v_msg VARCHAR2(4000);
            v_csiuid_grp_error NUMBER;
            v_uiderror NUMBER;
            v_sqlerrm_clean VARCHAR2(4000);
        BEGIN

            IF p_sqlerrm IS NULL THEN
               v_msg:= p_err_msg;
            ELSE
               v_uiderror:= get_error_seq(p_sqlerrm);
               v_sqlerrm_clean := REPLACE(p_sqlerrm, CONST_ERROR_SEQ_TOKEN_L || v_uiderror || CONST_ERROR_SEQ_TOKEN_R, '');
               v_msg:= p_err_msg || CONST_TOKEN || v_sqlerrm_clean;

               UPDATE ghost_ACTION_ERROR_DETAIL
                  SET uiderror = p_uiderror
                WHERE uiderror = v_uiderror;
               COMMIT;
            END IF;

            v_csiuid_grp_error := getSequence(CONST_SEQ_GHOSTUIDERRORGRP);

            INSERT INTO ghost_action_error_Detail(UIDERRORDETAIL,
                                                UIDERROR,
                                                record_id,
                                                errorcode,
                                                message,
                                                parameters,
                                                table_name,
                                                column_name)
                                        VALUES (v_csiuid_grp_error,
                                                p_uiderror,
                                                p_record_id,
                                                p_ghost_error_code,
                                                v_msg,
                                                p_parameters,
                                                p_table_name,
                                                p_column_name);
            COMMIT;

            EXCEPTION
                WHEN OTHERS THEN
                  RAISE;
                  /*
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG            ||
                                        'ghost_ERROR_HELPER_DETAIL ',
                                        SQLERRM);
                                        */
        END ghost_error_helper_detail_cjob;

    /*
    SUB   : ghost_error_helper_validation
            A helper function used by higher level functions that write errors to
            the detail level error table for validations ONLY. Inserts data for the different fields of
            the error table.
    PARAM : p_uiderror - A primary key of the error table.
            p_record_id - The unique error id of the parent error table. This joins
                          a error id to the errors in the detail error table.
            p_ghost_error_code - Oracle/User Defined error code
            p_err_msg - Error message to write to table.
            p_sqlerrm - SQL error
            p_parameters - Any parameters that were used at the time that need to be recorded into the error table
            p_table_name - The table in which error happend, if any.
            p_column_name - The table column in which error happend, if any.
    RETURN: None
    */
    PROCEDURE ghost_error_helper_validation(p_uiderror IN ghost_action_error.uiderror%TYPE,
                                          p_record_id IN ghost_action_error_detail.record_id%TYPE,
                                          p_ghost_error_code IN ghost_action_error_detail.errorcode%TYPE,
                                          p_err_msg IN ghost_action_error_detail.message%TYPE,
                                          p_sqlerrm IN VARCHAR2 := NULL,
                                          p_parameters IN ghost_action_error_detail.parameters%TYPE := NULL,
                                          p_table_name ghost_action_error_detail.table_name%TYPE := NULL,
                                          p_column_name ghost_action_error_detail.column_name%TYPE := NULL) IS
            PRAGMA AUTONOMOUS_TRANSACTION;
            v_msg VARCHAR2(4000);
            v_csiuid_grp_error NUMBER;
        BEGIN

            IF p_sqlerrm IS NULL THEN
               v_msg:= p_err_msg;
            ELSE
               v_msg:= p_err_msg || CONST_TOKEN || p_sqlerrm;
            END IF;

            v_csiuid_grp_error := getSequence(CONST_SEQ_GHOSTUIDERRORGRP);

            INSERT INTO ghost_action_error_detail(UIDERRORDETAIL,
                                                 UIDERROR,
                                                 record_id,
                                                 errorcode,
                                                 message,
                                                 parameters,
                                                 table_name,
                                                 column_name)
                                         VALUES (v_csiuid_grp_error,
                                                 p_uiderror,
                                                 p_record_id,
                                                 p_ghost_error_code,
                                                 v_msg,
                                                 p_parameters,
                                                 p_table_name,
                                                 p_column_name);
            COMMIT;

            EXCEPTION
                WHEN OTHERS THEN
                  RAISE;
                  /*
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG            ||
                                        'ghost_ERROR_HELPER_DETAIL ',
                                        SQLERRM);
                                        */
        END ghost_error_helper_validation;

    /*
    SUB   : write_ghost_detail_error
            Writes errors to the error detail table for child job process.
    PARAM : p_uiderror - A primary key of the error table.
            p_record_id - The unique error id of the parent error table. This joins
                          a error id to the errors in the detail error table.
            p_ghost_error_code - Oracle/User Defined error code
            p_err_msg - Error message to write to table.
            p_sqlerrm - SQL error
            p_parameters - Any parameters that were used at the time that need to be recorded into the error table
            p_table_name - The table in which error happend, if any.
            p_column_name - The table column in which error happend, if any.
    RETURN: None
    */
    PROCEDURE write_ghost_detail_error_cjob (p_uiderror IN ghost_action_error.uiderror%TYPE,
                                           p_record_id IN ghost_action_error_detail.record_id%TYPE,
                                           p_ghost_error_code IN ghost_action_error_detail.errorcode%TYPE,
                                           p_err_msg IN ghost_action_error_detail.message%TYPE,
                                           p_sqlerrm IN VARCHAR2 := NULL,
                                           p_parameters IN ghost_action_error_detail.parameters%TYPE := NULL,
                                           p_table_name ghost_action_error_detail.table_name%TYPE := NULL,
                                           p_column_name ghost_action_error_detail.column_name%TYPE := NULL) IS

        BEGIN

          ghost_error_helper_detail_cjob(p_uiderror,
                                       p_record_id,
                                       p_ghost_error_code,
                                       p_err_msg,
                                       p_sqlerrm,
                                       p_parameters,
                                       p_table_name,
                                       p_column_name);

    END write_ghost_detail_error_cjob;

    /*
    SUB   : write_ghost_detail_error
            Writes errors to the error detail table.
    PARAM : p_uiderror - A primary key of the error table.
            p_record_id - The unique error id of the parent error table. This joins
                          a error id to the errors in the detail error table.
            p_ghost_error_code - Oracle/User Defined error code
            p_err_msg - Error message to write to table.
            p_sqlerrm - SQL error
            p_parameters - Any parameters that were used at the time that need to be recorded into the error table
            p_table_name - The table in which error happend, if any.
            p_column_name - The table column in which error happend, if any.
    RETURN: None
    */
    PROCEDURE write_ghost_detail_error (p_uiderror IN ghost_action_error.uiderror%TYPE,
                                      p_record_id IN ghost_action_error_detail.record_id%TYPE,
                                      p_ghost_error_code IN ghost_action_error_detail.errorcode%TYPE,
                                      p_err_msg IN ghost_action_error_detail.message%TYPE,
                                      p_sqlerrm IN VARCHAR2 := NULL,
                                      p_parameters IN ghost_action_error_detail.parameters%TYPE := NULL,
                                      p_table_name ghost_action_error_detail.table_name%TYPE := NULL,
                                      p_column_name ghost_action_error_detail.column_name%TYPE := NULL) IS

        BEGIN

          ghost_error_helper_detail(p_uiderror,
                                  p_record_id,
                                  p_ghost_error_code,
                                  p_err_msg,
                                  p_sqlerrm,
                                  p_parameters,
                                  p_table_name,
                                  p_column_name);

    END write_ghost_detail_error;



    /*
    SUB   : write_ghost_validation_error
            Writes errors to the error detail table for validations ONLY.
    PARAM : p_uiderror - A primary key of the error table.
            p_record_id - The unique error id of the parent error table. This joins
                          a error id to the errors in the detail error table.
            p_ghost_error_code - Oracle/User Defined error code
            p_err_msg - Error message to write to table.
            p_sqlerrm - SQL error
            p_parameters - Any parameters that were used at the time that need to be recorded into the error table
            p_table_name - The table in which error happend, if any.
            p_column_name - The table column in which error happend, if any.
    RETURN: None
    */
    PROCEDURE write_ghost_validation_error (p_uiderror IN ghost_action_error.uiderror%TYPE,
                                          p_record_id IN ghost_action_error_detail.record_id%TYPE,
                                          p_ghost_error_code IN ghost_action_error_detail.errorcode%TYPE,
                                          p_err_msg IN ghost_action_error_detail.message%TYPE,
                                          p_sqlerrm IN VARCHAR2 := NULL,
                                          p_parameters IN ghost_action_error_detail.parameters%TYPE := NULL,
                                          p_table_name ghost_action_error_detail.table_name%TYPE := NULL,
                                          p_column_name ghost_action_error_detail.column_name%TYPE := NULL) IS

        BEGIN

          ghost_error_helper_validation(p_uiderror,
                                      p_record_id,
                                      p_ghost_error_code,
                                      p_err_msg,
                                      p_sqlerrm,
                                      p_parameters,
                                      p_table_name,
                                      p_column_name);

    END write_ghost_validation_error;

     /*
    SUB   : write_ghost_error
            Writes error to the error table. Returns the error id of the error.
    PARAM : p_pcf_bid - Orchestrator Framework batch id.
            p_pcf_cid - Orchestrator Framework control process id.
            p_pcf_aid - Orchestrator Framework action id.
            p_ghost_error_code - Oracle/User Defined error code
            p_err_msg - Error message to write to table.
            p_sqlerrm - SQL error
            p_parameters - Any parameters that were used at the time that need to be recorded into the error table
            p_table_name - The table in which error happend, if any.
            p_column_name - The table column in which error happend, if any.
    RETURN: NUMBER
    */
    FUNCTION write_ghost_error (p_pcf_bid IN NUMBER,
                              p_pcf_eid IN NUMBER,
                              p_pcf_cid IN NUMBER,
                              p_pcf_aid IN NUMBER,
                              p_pcf_op IN VARCHAR2,
                              p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                              p_err_msg IN ghost_action_error.message%TYPE,
                              p_sqlerrm IN VARCHAR2 := NULL,
                              p_parameters IN ghost_action_error.parameters%TYPE := NULL,
                              p_table_name ghost_action_error.table_name%TYPE := NULL,
                              p_column_name ghost_action_error.column_name%TYPE := NULL,
                              p_record_id ghost_action_error.record_id%TYPE := NULL) RETURN NUMBER IS
        BEGIN

          RETURN ghost_error_helper(p_pcf_bid,
                                  p_pcf_eid,
                                  p_pcf_cid,
                                  p_pcf_aid,
                                  p_pcf_op,
                                  p_ghost_error_code,
                                  p_err_msg,
                                  p_sqlerrm,
                                  p_parameters,
                                  p_table_name,
                                  p_column_name,
                                  p_record_id);

        END write_ghost_error;


       /*
       SUB   : write_ghost_error_no_return
               Writes error to the error table.
               p_pcf_cid - Orchestrator Framework control process id.
               p_pcf_aid - Orchestrator Framework action id.
               p_ghost_error_code - Oracle/User Defined error code
               p_err_msg - Error message to write to table.
               p_sqlerrm - SQL error
               p_parameters - Any parameters that were used at the time that need to be recorded into the error table
               p_table_name - The table in which error happend, if any.
               p_column_name - The table column in which error happend, if any.
       RETURN: None
       */
       PROCEDURE write_ghost_error_no_return (p_pcf_bid IN NUMBER,
                                            p_pcf_eid IN NUMBER,
                                            p_pcf_cid IN NUMBER,
                                            p_pcf_aid IN NUMBER,
                                            p_pcf_op IN VARCHAR2,
                                            p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                                            p_err_msg IN ghost_action_error.message%TYPE,
                                            p_sqlerrm IN VARCHAR2 := NULL,
                                            p_parameters IN ghost_action_error.parameters%TYPE := NULL,
                                            p_table_name ghost_action_error.table_name%TYPE := NULL,
                                            p_column_name ghost_action_error.column_name%TYPE := NULL) IS
          v_dummy NUMBER;
        BEGIN

          v_dummy:= ghost_error_helper(p_pcf_bid,
                                     p_pcf_eid,
                                     p_pcf_cid,
                                     p_pcf_aid,
                                     p_pcf_op,
                                     p_ghost_error_code,
                                     p_err_msg,
                                     p_sqlerrm,
                                     p_parameters,
                                     p_table_name,
                                     p_column_name);

        END write_ghost_error_no_return;

        /*
        SUB   : write_ghost_error
                Writes error to the error table. Takes in a error id parameter as the value
                to be used for the unique error id in the error table.
        PARAM : p_uiderror - A primary key of the error table.
                p_pcf_bid - Orchestrator Framework batch id.
                p_pcf_cid - Orchestrator Framework control process id.
                p_pcf_aid - Orchestrator Framework action id.
                p_ghost_error_code - Oracle/User Defined error code
                p_err_msg - Error message to write to table.
                p_sqlerrm - SQL error
                p_parameters - Any parameters that were used at the time that need to be recorded into the error table
                p_table_name - The table in which error happend, if any.
                p_column_name - The table column in which error happend, if any.
        RETURN: None
        */
        PROCEDURE write_ghost_error (p_uiderror IN ghost_action_error.uiderror%TYPE,
                                   p_pcf_bid IN NUMBER,
                                   p_pcf_eid IN NUMBER,
                                   p_pcf_cid IN NUMBER,
                                   p_pcf_aid IN NUMBER,
                                   p_pcf_op IN VARCHAR2,
                                   p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                                   p_err_msg IN ghost_action_error.message%TYPE,
                                   p_sqlerrm IN VARCHAR2 := NULL,
                                   p_parameters IN ghost_action_error.parameters%TYPE := NULL,
                                   p_table_name ghost_action_error.table_name%TYPE := NULL,
                                   p_column_name ghost_action_error.column_name%TYPE := NULL,
                                   p_record_id ghost_action_error.record_id%TYPE := NULL) IS
          v_dummy NUMBER;
        BEGIN

          v_dummy:= ghost_error_helper(p_uiderror,
                                     p_pcf_bid,
                                     p_pcf_eid,
                                     p_pcf_cid,
                                     p_pcf_aid,
                                     p_pcf_op,
                                     p_ghost_error_code,
                                     p_err_msg,
                                     p_sqlerrm,
                                     p_parameters,
                                     p_table_name,
                                     p_column_name,
                                     p_record_id);

        END write_ghost_error;

    /*
    SUB   : raise_ghost_error
            Raise a user defiend application error.
    PARAM : p_err_msg - Error message to write to table.
            p_sqlerrm - SQL error
    RETURN: None
    */
    PROCEDURE raise_ghost_error_cjob (p_error_id IN NUMBER,
                                    p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                                    p_err_msg IN ghost_action_error.message%TYPE,
                                    p_sqlerrm IN VARCHAR2 := NULL,
                                    p_write_to_detail IN BOOLEAN := TRUE,
                                    p_raise_clean IN BOOLEAN := FALSE) IS
         v_uiderror NUMBER;
         v_sqlerrm_clean VARCHAR2(4000);
         v_raise_msg VARCHAR2(4000);
    BEGIN
         v_uiderror := p_error_id;
         v_sqlerrm_clean := NULL;
         v_raise_msg := p_err_msg;

         v_raise_msg := CONST_ERROR_SEQ_TOKEN_L || v_uiderror || CONST_ERROR_SEQ_TOKEN_R || v_raise_msg;

            v_sqlerrm_clean := REPLACE(p_sqlerrm, CONST_ERROR_SEQ_TOKEN_L || v_uiderror || CONST_ERROR_SEQ_TOKEN_R, '');
--            v_raise_msg := CONST_ERROR_SEQ_TOKEN_L || v_uiderror || CONST_ERROR_SEQ_TOKEN_R || v_raise_msg;

         IF(p_write_to_detail) THEN
           write_ghost_detail_error(v_uiderror,
                                  NULL,
                                  p_ghost_error_code,
                                  p_err_msg,
                                  v_sqlerrm_clean,
                                  NULL,
                                  NULL,
                                  NULL);
          END IF;

         raise_application_error (p_ghost_error_code, v_raise_msg || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
    END raise_ghost_error_cjob;

    /*
    SUB   : raise_ghost_error
            Raise a user defiend application error.
    PARAM : p_err_msg - Error message to write to table.
            p_sqlerrm - SQL error
    RETURN: None
    */
    PROCEDURE raise_ghost_error (p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                               p_err_msg IN ghost_action_error.message%TYPE,
                               p_sqlerrm IN VARCHAR2 := NULL,
                               p_write_to_detail IN BOOLEAN := TRUE,
                               p_raise_clean IN BOOLEAN := FALSE) IS
         v_uiderror NUMBER;
--         v_sqlerrm VARCHAR2(4000);
         v_sqlerrm_clean VARCHAR2(4000);
         v_is_first_error BOOLEAN := FALSE;
         v_raise_msg VARCHAR2(4000);
    BEGIN

         v_uiderror := get_error_seq(p_sqlerrm);
--         v_sqlerrm := p_sqlerrm;
         v_sqlerrm_clean := NULL;
         v_raise_msg := p_err_msg;

--         dbms_output.put_line('uiderror: ' || v_uiderror);
--         dbms_output.put_line('Error message SQL BEFORE : ' || p_sqlerrm);
--         dbms_output.put_line('Error message: ' || p_err_msg);

         IF v_uiderror = CONST_ERROR_SEQ_TOKEN_NSEQ THEN
            v_uiderror := getSequence(CONST_SEQ_GHOSTUIDERROR);
            ---v_sqlerrm := CONST_ERROR_SEQ_TOKEN_L || v_uiderror || CONST_ERROR_SEQ_TOKEN_R || p_sqlerrm;
            --v_raise_msg := v_sqlerrm;
            v_raise_msg := CONST_ERROR_SEQ_TOKEN_L || v_uiderror || CONST_ERROR_SEQ_TOKEN_R || v_raise_msg;
            v_is_first_error := TRUE;

         ELSE IF v_uiderror = CONST_ERROR_SEQ_TOKEN_ERROR THEN
                raise_application_error (-20132, 'Failure in : raise_ghost_error - Error in getSequence function!: ' || p_sqlerrm);
              END IF;
         END IF;

         IF v_is_first_error THEN
            v_sqlerrm_clean := REPLACE(p_sqlerrm, CONST_ERROR_SEQ_TOKEN_L || v_uiderror || CONST_ERROR_SEQ_TOKEN_R, '');
         ELSE
            v_raise_msg := CONST_ERROR_SEQ_TOKEN_L || v_uiderror || CONST_ERROR_SEQ_TOKEN_R || v_raise_msg;
         END IF;

--         dbms_output.put_line('Error message SQL AFTER : ' || v_sqlerrm);

         IF(p_write_to_detail) THEN
           write_ghost_detail_error(v_uiderror,
                                  NULL,
                                  p_ghost_error_code,
                                  p_err_msg,
                                  v_sqlerrm_clean,
                                  NULL,
                                  NULL,
                                  NULL);
          END IF;

         IF p_raise_clean THEN
            v_raise_msg := p_err_msg || ' ' || v_sqlerrm_clean;
         END IF;

         --dbms_output.put_line('Error message Sent to next program: ' || p_err_msg || CONST_TOKEN || v_raise_msg || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
--         dbms_output.put_line('Error message Sent to next program: ' || v_raise_msg|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
--         raise_application_error (p_ghost_error_code, p_err_msg || CONST_TOKEN || v_raise_msg || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
         raise_application_error (p_ghost_error_code, v_raise_msg || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);

    END raise_ghost_error;


    /*
    SUB   : raise_ghost_error_write
            Raise a user defiend application error and also write the error to the error table.
    PARAM : p_pcf_bid - Orchestrator Framework batch id.
            p_pcf_cid - Orchestrator Framework control process id.
            p_pcf_aid - Orchestrator Framework action id.
            p_ghost_error_code - Oracle/User Defined error code
            p_err_msg - Error message to write to table.
            p_sqlerrm - SQL error
            p_parameters - Any parameters that were used at the time that need to be recorded into the error table
            p_table_name - The table in which error happend, if any.
            p_column_name - The table column in which error happend, if any.
    RETURN: None
    */
    PROCEDURE raise_ghost_error_write (p_pcf_bid IN NUMBER,
                                     p_pcf_eid IN NUMBER,
                                     p_pcf_cid IN NUMBER,
                                     p_pcf_aid IN NUMBER,
                                     p_pcf_op IN VARCHAR2,
                                     p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                                     p_err_msg IN ghost_action_error.message%TYPE,
                                     p_sqlerrm IN VARCHAR2 := NULL,
                                     p_parameters IN ghost_action_error.parameters%TYPE := NULL,
                                     p_table_name ghost_action_error.table_name%TYPE := NULL,
                                     p_column_name ghost_action_error.column_name%TYPE := NULL,
                                     p_record_id ghost_action_error.record_id%TYPE := NULL) IS

--          v_error_id NUMBER;
          v_err_msg_contract VARCHAR2(100);
          v_uiderror NUMBER;
          v_sqlerrm VARCHAR2(4000);
          v_sqlerrm_clean VARCHAR2(4000);
    BEGIN

         v_uiderror := get_error_seq(p_sqlerrm);
         v_sqlerrm := p_sqlerrm;

--         dbms_output.put_line('WRITE uiderror: ' || v_uiderror);
--         dbms_output.put_line('WRITE Error message SQL BEFORE : ' || p_sqlerrm);
--         dbms_output.put_line('WRITE Error message: ' || p_err_msg);

         IF v_uiderror = CONST_ERROR_SEQ_TOKEN_NSEQ THEN
            v_uiderror := getSequence(CONST_SEQ_GHOSTUIDERROR);
            v_sqlerrm_clean := p_sqlerrm;
         ELSE IF v_uiderror = CONST_ERROR_SEQ_TOKEN_ERROR THEN
                raise_application_error (-20132, 'Failure in : raise_ghost_error - Error in getSequence function!: ' || p_sqlerrm);

              ELSE v_sqlerrm_clean := ''; --v_sqlerrm_clean := REPLACE(p_sqlerrm, CONST_ERROR_SEQ_TOKEN_L || v_uiderror || CONST_ERROR_SEQ_TOKEN_R, '');
              END IF;
         END IF;

--         dbms_output.put_line('WRITE Error message SQL AFTER : ' || v_sqlerrm);

--          v_sqlerrm_clean := REPLACE(p_sqlerrm, CONST_ERROR_SEQ_TOKEN_L || v_uiderror || CONST_ERROR_SEQ_TOKEN_R, '');

          --dbms_output.put_line(v_sqlerrm_clean);

          v_err_msg_contract := build_error_contract(v_uiderror);

          write_ghost_error(v_uiderror,
                          p_pcf_bid,
                          p_pcf_eid,
                          p_pcf_cid,
                          p_pcf_aid,
                          p_pcf_op,
                          p_ghost_error_code,
                          p_err_msg,
                          v_sqlerrm_clean || v_err_msg_contract,
                          p_parameters,
                          p_table_name,
                          p_column_name,
                          p_record_id);

          raise_ghost_error (p_ghost_error_code,
                           p_err_msg,
                           v_sqlerrm || v_err_msg_contract,
                           FALSE,
                           TRUE);


    END raise_ghost_error_write;


   /*
    SUB   : raise_ghost_error_write
            Raise a user defiend application error and also write the error to the error table.
    PARAM : p_pip - passed in parameters
            p_ghost_error_code - Oracle/User Defined error code
            p_err_msg - Error message to write to table.
            p_sqlerrm - SQL error
            p_parameters - Any parameters that were used at the time that need to be recorded into the error table
            p_table_name - The table in which error happend, if any.
            p_column_name - The table column in which error happend, if any.
    RETURN: None
    */
   PROCEDURE raise_ghost_error_write (p_pip PIP,
                                    p_ghost_error_code IN ghost_action_error.errorcode%TYPE,
                                    p_err_msg IN ghost_action_error.message%TYPE,
                                    p_sqlerrm IN VARCHAR2 := NULL,
                                    p_parameters IN ghost_action_error.parameters%TYPE := NULL,
                                    p_table_name ghost_action_error.table_name%TYPE := NULL,
                                    p_column_name ghost_action_error.column_name%TYPE := NULL) IS
   BEGIN
        raise_ghost_error_write (p_pip.pcf_bid,
                               p_pip.pcf_eid,
                               p_pip.pcf_cid,
                               p_pip.pcf_aid,
                               p_pip.pcf_op,
                               p_ghost_error_code,
                               p_err_msg,
                               p_sqlerrm,
                               p_parameters,
                               p_table_name,
                               p_column_name);

   END raise_ghost_error_write;


    /*
    SUB   : update_batch_status
            Raise a user defiend application error and also write the error to the error table.
    PARAM : p_pcf_bid - Orchestrator Framework batch id.
            p_pcf_cid - Orchestrator Framework control process id.
            p_pcf_aid - Orchestrator Framework action id.
            p_status - The status to be inserted.
            p_validation_type - The type of status update.
            p_msg - The message of the batch status.
    RETURN: None
    */
    PROCEDURE update_batch_status (p_uidlog IN NUMBER,
                                   p_status IN ghost_pcf_log.status%TYPE,
                                   p_msg IN ghost_pcf_log.message%TYPE := NULL,
                                   p_record_count IN ghost_pcf_log.record_count%TYPE := NULL) IS
        PRAGMA AUTONOMOUS_TRANSACTION;
    BEGIN

        update ghost_pcf_log
           set status  = p_status,
               message = p_msg,
               record_count = p_record_count
         WHERE uidlog = p_uidlog;

        COMMIT;

        -- TODO: Need something Unique here

        EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error(COSNT_PCF_ERROR_UNKNOWN,
                                       CONST_ERROR_MSG || 'UPDATE_BATCH_STATUS',
                                       SQLERRM);
    END update_batch_status;


    /*
    SUB   : insert_batch_status
            Inserts a record into batch status table.
    PARAM : p_pcf_bid - Orchestrator Framework batch id.
            p_pcf_cid - Orchestrator Framework control process id.
            p_pcf_aid - Orchestrator Framework action id.
            p_status - The status to be inserted.
            p_validation_type - The type of status update.
            p_msg - The message of the batch status.
    RETURN: NUMBER
    */
    FUNCTION insert_batch_status (p_pcf_bid IN NUMBER,
                                  p_pcf_eid IN NUMBER,
                                  p_pcf_cid IN NUMBER,
                                  p_pcf_aid IN NUMBER,
                                  p_pcf_op IN VARCHAR2,
                                  p_status IN ghost_pcf_log.status%TYPE,
                                  p_process_type IN ghost_pcf_log.process_type%TYPE,
                                  p_action IN ghost_pcf_log.action%TYPE,
                                  p_msg IN ghost_pcf_log.message%TYPE := NULL) RETURN NUMBER IS
        PRAGMA AUTONOMOUS_TRANSACTION;
        v_seq_num NUMBER;
    BEGIN

        v_seq_num := getSequence(CONST_SEQ_GHOSTLOG);

        INSERT INTO ghost_pcf_log (uidlog,
                                 pcf_bid,
                                 pcf_eid,
                                 pcf_cid,
                                 pcf_aid,
                                 pcf_op,
                                 status,
                                 process_type,
                                 action,
                                 message,
                                 starttime)
                         VALUES (v_seq_num,
                                 p_pcf_bid,
                                 p_pcf_eid,
                                 p_pcf_cid,
                                 p_pcf_aid,
                                 TO_DATE(p_pcf_op, CONST_FRAMEWORK_DATE_MAP),  --This does not use the function to gurauntee no points of failure.
                                 p_status,
                                 p_process_type,
                                 p_action,
                                 p_msg,
                                 SYSTIMESTAMP);
        COMMIT;

        RETURN v_seq_num;

        EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error(COSNT_PCF_ERROR_UNKNOWN,
                                       CONST_ERROR_MSG || 'UPDATE_BATCH_STATUS',
                                       SQLERRM);
    END insert_batch_status;


    /*
    SUB   : insert_batch_status
            Inserts a record into batch status table.
    PARAM : p_pip - passed in parameters
            p_status - The status to be inserted.
            p_validation_type - The type of status update.
            p_msg - The message of the batch status.
    RETURN: NUMBER
    */
    FUNCTION insert_batch_status (p_pip IN PIP,
                                  p_status IN ghost_pcf_log.status%TYPE,
                                  p_process_type IN ghost_pcf_log.process_type%TYPE,
                                  p_msg IN ghost_pcf_log.message%TYPE := NULL) RETURN NUMBER IS

    BEGIN
         return  insert_batch_status (p_pip.pcf_bid,
                                      p_pip.pcf_eid,
                                      p_pip.pcf_cid,
                                      p_pip.pcf_aid,
                                      p_pip.pcf_op,
                                      p_status,
                                      p_process_type,
                                      p_pip.action_def,
                                      p_msg);

    END insert_batch_status;

    /*
    SUB   : split_params

    PARAM : p_params - The String of parameters that need to be split.
            p_seperator - The String that is the seperator between values
            o_array_values - The variable that will store the values and be passed back out.
    RETURN: NUMBER
    */
    FUNCTION split_params(p_params IN VARCHAR2,
                          p_seperator IN VARCHAR2,
                          o_array_values IN OUT ghost_util.array_of_strings
                          ) RETURN NUMBER IS

       v_temp VARCHAR2(1000);
       v_element VARCHAR2(1000);
       v_seperator_count NUMBER:=1;
       v_count NUMBER:=0;

    BEGIN
       -- Takes a string and splits it by a seperator then stores it into a String array.
       --
       v_temp:= p_params;

       WHILE (v_seperator_count!=0) LOOP
                v_seperator_count:= INSTR(v_temp,p_seperator);

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

                o_array_values.EXTEND(1);
                o_array_values(v_count) := v_element;
                v_temp:=SUBSTR(v_temp,INSTR(v_temp,p_seperator)+1);

                IF v_temp IS NULL THEN
                   v_seperator_count:=0; --Exit Condition
                END IF;
       END LOOP;

       RETURN v_count;

       EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG                                    ||
                                        'SPLIT_PARAMS '                                    ||
                                        CONST_ERROR_MSG_PARAM                              ||
                                        ' p_params:' || wrap_error_params(p_params)        ||
                                        ' p_seperator:' || wrap_error_params(p_seperator),
                                        SQLERRM);

    END split_params;


    /*
    SUB   : ghost_write_raw_to_blob
            Takes RAW data and appends it to a BLOB.
    PARAM : p_blob - The blob that will have data written to it and be passed back out.
            p_raw - The raw information that needs to be written to the blob.
    RETURN: None
    */
    PROCEDURE ghost_write_raw_to_blob(p_blob IN OUT BLOB,
                                    p_raw IN RAW) IS

        BEGIN

            --dbms_lob.writeAppend(p_blob, dbms_lob.getlength(p_raw), p_raw);
            dbms_lob.writeAppend(p_blob, utl_raw.length(p_raw), p_raw);

        EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG          ||
                                        'ghost_WRITE_RAW_TO_BLOB',
                                        SQLERRM);

        END ghost_write_raw_to_blob;

/*
    SUB   : ghost_write_raw_to_blob
            Takes RAW data and appends it to a BLOB with specified offset.
    PARAM : p_blob - The blob that will have data written to it and be passed back out.
            p_raw - The raw information that needs to be written to the blob.
    RETURN: None
    */
    PROCEDURE ghost_write_raw_to_blob(p_blob IN OUT BLOB,
                                      p_raw IN RAW,
                                      p_offset IN INTEGER) IS

        BEGIN
            dbms_lob.write(p_blob, utl_raw.length(p_raw), p_offset, p_raw);
        EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG          ||
                                        'ghost_WRITE_RAW_TO_BLOB',
                                        SQLERRM);

        END ghost_write_raw_to_blob;

    /*
    SUB   : ghost_write_binarydouble_to_blob
            Takes BINARYDOUBLE data and appends it to a BLOB.
    PARAM : p_blob - The blob that will have data written to it and be passed back out.
            p_binary_double - The binarydouble tat needs to be written to the blob.
    RETURN: None
    */
    PROCEDURE g_write_binarydouble_to_blob(p_blob IN OUT BLOB,
                                             p_binary_double IN BINARY_DOUBLE) IS

        v_blob_raw RAW(8);

        BEGIN

            v_blob_raw := utl_raw.cast_from_binary_double(p_binary_double);
            --DBMS_OUTPUT.PUT_LINE('RAW  : >>' || v_blob_raw);
            v_blob_raw := utl_RAW.REVERSE(v_BLOB_RAW);
            ghost_write_raw_to_blob(p_blob, v_blob_raw);

        EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG                   ||
                                        'ghost_WRITE_BINARYDOUBLE_TO_BLOB ',
                                        SQLERRM);
        END g_write_binarydouble_to_blob;

     /*
    SUB   : ghost_write_binarydouble_to_blob
            Takes BINARYDOUBLE data and appends it to a BLOB.
    PARAM : p_blob - The blob that will have data written to it and be passed back out.
            p_binary_double - The binarydouble tat needs to be written to the blob.
    RETURN: None
    */
    PROCEDURE g_write_binarydouble_to_blob(p_blob IN OUT BLOB,     
                                           p_binary_double IN BINARY_DOUBLE,
                                           p_offset IN NUMBER) IS

        v_blob_raw RAW(8);

        BEGIN

            v_blob_raw := utl_raw.cast_from_binary_double(p_binary_double);
            --DBMS_OUTPUT.PUT_LINE('RAW  : >>' || v_blob_raw);
            v_blob_raw := utl_RAW.REVERSE(v_BLOB_RAW);
            ghost_write_raw_to_blob(p_blob, v_blob_raw, p_offset);

        EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG                   ||
                                        'ghost_WRITE_BINARYDOUBLE_TO_BLOB ',
                                        SQLERRM);
        END g_write_binarydouble_to_blob;

    /*
    SUB   : ghost_write_varchar2_to_blob
            Takes BINARYDOUBLE data and appends it to a BLOB.
    PARAM : p_blob - The blob that will have data written to it and be passed back out.
            p_varchar2 - The varchar2 tat needs to be written to the blob.
    RETURN: None
    */
    PROCEDURE ghost_write_varchar2_to_blob (p_blob IN OUT BLOB,
                                          p_varchar2 IN VARCHAR2) IS

        BEGIN
            --DBMS_OUTPUT.PUT_LINE(' VARCHAR2 : >>  <' || p_varchar2 || '>');
            --DBMS_OUTPUT.PUT_LINE(' VARCHAR2 Length  : >>' || LENGTH(p_varchar2));
            --DBMS_OUTPUT.PUT_LINE(' Status RAW  : >>' || v_blob_char);
            ghost_write_raw_to_blob(p_blob, utl_raw.cast_to_raw(p_varchar2));

        EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG ||
                                        'ghost_WRITE_VARCHAR2_TO_BLOB ',
                                        SQLERRM);
        END ghost_write_varchar2_to_blob;


   /*
    SUB   : ghost_write_varchar2_to_blob
            Takes BINARYDOUBLE data and appends it to a BLOB.
    PARAM : p_blob - The blob that will have data written to it and be passed back out.
            p_varchar2 - The varchar2 tat needs to be written to the blob.
            p_offset - Takes aa offset to write data to
    RETURN: None
    */
    PROCEDURE ghost_write_varchar2_to_blob (p_blob IN OUT BLOB,
                                            p_varchar2 IN VARCHAR2,
                                            p_offset IN INTEGER) IS

        BEGIN
            ghost_write_raw_to_blob(p_blob, utl_raw.cast_to_raw(p_varchar2), p_offset);

        EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG ||
                                        'ghost_WRITE_VARCHAR2_TO_BLOB ',
                                        SQLERRM);
        END ghost_write_varchar2_to_blob;
        
   /*
   SUB   : get_number_of_hours
           Returns the number of intervals that should exist based on the SPI, or
           Seconds Per Interval.
   PARAM : p_spi - The Seconds Per Interval.
   RETURN: NUMBER
   *//*
   FUNCTION get_number_of_hours(p_op_date IN DATE) RETURN NUMBER IS
        PRAGMA AUTONOMOUS_TRANSACTION;
        err_hourcheck_failure EXCEPTION;
        PRAGMA EXCEPTION_INIT ( err_hourcheck_failure, -20009);

        v_dst_day NUMBER;
   BEGIN

       v_dst_day := check_DST(p_op_date);

       CASE v_dst_day
                WHEN CONST_ON_START_DST THEN
                     RETURN 23;
                WHEN CONST_IN_DST THEN
                     RETURN 24;
                WHEN CONST_OUT_DST THEN
                     RETURN 24;
                WHEN CONST_ON_END_DST THEN
                     RETURN 25;
                WHEN CONST_DST_ERROR THEN
                     RAISE err_hourcheck_failure;
                ELSE RAISE err_hourcheck_failure;
        END CASE;

        EXCEPTION
                  WHEN err_hourcheck_failure THEN
                       raise_ghost_error (SQLCODE,
                                        CONST_ERROR_MSG            ||
                                        'GET_NUMBER_OF_HOURS - Verifying what DST Day failed '     ||
                                        CONST_ERROR_MSG_PARAM      ||
                                        ' p_op_date:' || wrap_error_params(getfdate(p_op_date)),
                                        SQLERRM);
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG            ||
                                        'GET_NUMBER_OF_HOURS '     ||
                                        CONST_ERROR_MSG_PARAM      ||
                                        ' p_op_date:' || wrap_error_params(getfdate(p_op_date)),
                                        SQLERRM);
   END get_number_of_hours;
*/
/*
   SUB   : get_number_of_hours
           Returns the number of intervals that should exist based on the SPI, or
           Seconds Per Interval.
   PARAM : p_spi - The Seconds Per Interval.
   RETURN: NUMBER
   */
   FUNCTION get_number_of_hours(p_check_dst IN NUMBER) RETURN NUMBER IS
        PRAGMA AUTONOMOUS_TRANSACTION;
        err_hourcheck_failure EXCEPTION;
        PRAGMA EXCEPTION_INIT ( err_hourcheck_failure, -20009);
   BEGIN
       CASE p_check_dst
                WHEN CONST_ON_START_DST THEN
                     RETURN 23;
                WHEN CONST_IN_DST THEN
                     RETURN 24;
                WHEN CONST_OUT_DST THEN
                     RETURN 24;
                WHEN CONST_ON_END_DST THEN
                     RETURN 25;
                WHEN CONST_DST_ERROR THEN
                     RAISE err_hourcheck_failure;
                ELSE RAISE err_hourcheck_failure;
        END CASE;

        EXCEPTION
                  WHEN err_hourcheck_failure THEN
                       raise_ghost_error (SQLCODE,
                                        CONST_ERROR_MSG            ||
                                        'GET_NUMBER_OF_HOURS - Verifying what DST Day failed '     ||
                                        CONST_ERROR_MSG_PARAM      ||
                                        ' p_check_dst:' || wrap_error_params(p_check_dst),
                                        SQLERRM);
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG            ||
                                        'GET_NUMBER_OF_HOURS '     ||
                                        CONST_ERROR_MSG_PARAM      ||
                                        ' p_op_date:' || wrap_error_params(p_check_dst),
                                        SQLERRM);
   END get_number_of_hours;


   /*
   SUB   : get_number_of_intervals
           Returns the number of intervals that should exist based on the SPI, or
           Seconds Per Interval.
   PARAM : p_spi - The Seconds Per Interval.
   RETURN: NUMBER
   *//*
   FUNCTION get_number_of_intervals(p_spi NUMBER,
                                    p_op_date IN DATE,
                                    p_offset IN NUMBER := 0) RETURN NUMBER IS
        PRAGMA AUTONOMOUS_TRANSACTION;
        v_num_of_hours NUMBER;
        v_date DATE;
   BEGIN
        v_date := p_op_date;

        IF p_offset = ghost_util.CONST_FRAMEWORK_SPECIAL_OFFSET THEN
           v_date := v_date - 1/ghost_util.CONST_SEC_IN_DAY;
        END IF;

        v_num_of_hours := get_number_of_hours(v_date);

        IF(p_spi = -9999) THEN
           RETURN 1;
        ELSE
           RETURN (v_num_of_hours * CONST_SEC_IN_HOUR) / p_spi; --TODO: What about fractions  CEIL or FLOOR
        END IF;

        EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG            ||
                                        'GET_NUMBER_OF_INTERVALS ' ||
                                        CONST_ERROR_MSG_PARAM      ||
                                        ' p_spi:' || wrap_error_params(p_spi) ||
                                        ' p_op_date:' || wrap_error_params(getfdate(p_op_date)),
                                        SQLERRM);
   END get_number_of_intervals;
*/
   /*
   SUB   : get_number_of_intervals
           Returns the number of intervals that should exist based on the SPI, or
           Seconds Per Interval.
   PARAM : p_spi - The Seconds Per Interval.
   RETURN: NUMBER
   */
   FUNCTION get_number_of_intervals(p_spi NUMBER,
                                    p_check_dst IN NUMBER) RETURN NUMBER IS
        PRAGMA AUTONOMOUS_TRANSACTION;
        v_num_of_hours NUMBER;
   BEGIN
        v_num_of_hours := get_number_of_hours(p_check_dst);

        IF(p_spi = -9999) THEN
           RETURN 1;
        ELSE
           RETURN (v_num_of_hours * CONST_SEC_IN_HOUR) / p_spi; --TODO: What about fractions  CEIL or FLOOR
        END IF;

        EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG            ||
                                        'GET_NUMBER_OF_INTERVALS ' ||
                                        CONST_ERROR_MSG_PARAM      ||
                                        ' p_spi:' || wrap_error_params(p_spi) ||
                                        ' p_check_dst:' || wrap_error_params(p_check_dst),
                                        SQLERRM);
   END get_number_of_intervals;

   /*
   SUB   : getSequence
           Returns the next sequence value for a specified sequence.
   PARAM : p_seq_name - The name of the sequence to get the next value for.
   RETURN: NUMBER
   */
   FUNCTION getSequence(p_seq_name VARCHAR2
                           ) RETURN NUMBER IS
         PRAGMA AUTONOMOUS_TRANSACTION;
         v_sequence NUMBER;
    BEGIN
         v_sequence := CONST_SEQ_ERROR_VAL;

         CASE p_seq_name
/*              WHEN CONST_SEQ_BDHEADER THEN
                 SELECT SEQ_UIDBDHEADER.NEXTVAL
                     INTO v_sequence
                     FROM DUAL;
              WHEN CONST_SEQ_BDINTDHEADER THEN
                   SELECT SEQ_UIDBDINTDHEADER.NEXTVAL
                     INTO v_sequence
                     FROM DUAL;

             WHEN CONST_SEQ_BDINTDVALUE THEN
                  SELECT SEQ_UIDBDINTVALUE.NEXTVAL
                    INTO v_sequence
                    FROM DUAL;

              WHEN CONST_SEQ_RUNNUMBER THEN
                   SELECT SEQ_CSIRUNNUMBER.NEXTVAL
                     INTO v_sequence
                     FROM DUAL;
*/
              WHEN CONST_SEQ_GHOSTUIDERROR THEN
                   SELECT Seq_GHOSTUIDERROR.NEXTVAL
                     INTO v_sequence
                     FROM DUAL;

              WHEN CONST_SEQ_GHOSTLOG THEN
                   SELECT SEQ_GHOSTLOG.NEXTVAL
                     INTO v_sequence
                     FROM DUAL;

              WHEN CONST_SEQ_GHOSTUIDERRORGRP THEN
                   SELECT Seq_GHOSTUIDGRPERROR.NEXTVAL
                     INTO v_sequence
                     FROM DUAL;

              ELSE
                    RAISE sequence_error;
          END CASE;

          RETURN v_sequence;

          EXCEPTION
                  WHEN OTHERS THEN
                      RAISE;
                      /*
                  WHEN sequence_error THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'GETSEQUENCE: Problem getting sequence value! '          ||
                                        CONST_ERROR_MSG_PARAM   ||
                                        ' p_seq_name:' || wrap_error_params(p_seq_name),
                                        SQLERRM);
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'GETSEQUENCE '          ||
                                        CONST_ERROR_MSG_PARAM   ||
                                        ' p_seq_name:' || wrap_error_params(p_seq_name),
                                        SQLERRM);
                                        */
     END getSequence;


     /*
     SUB   : getCurrentUser
             Returns the string of the current schema/user the code is executing in.
     PARAM : None
     RETURN: VARCHAR2
     */
     FUNCTION getCurrentUser RETURN VARCHAR2 IS
          v_user VARCHAR2(200);
     BEGIN
          SELECT USER
            INTO v_user
            FROM DUAL;

          RETURN v_user;

          EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG   ||
                                        'GETCURRENTUSER ',
                                        SQLERRM);
     END getCurrentUser;


     /*
     SUB   : wrap_error_params
             Wraps a parameter in a specified format.
     PARAM : p_param - A parameter that needs to be wrapped.
     RETURN: VARCHAR2
     */
     FUNCTION wrap_error_params(p_param VARCHAR2) RETURN VARCHAR2 IS
     BEGIN
          RETURN CONST_ERROR_PARAM_L || p_param || CONST_ERROR_PARAM_R;

          EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'WRAP_ERROR_PARAMS '    ||
                                        ' p_param:' || p_param, --Not wrapped
                                        SQLERRM);
     END wrap_error_params;


     /*
     SUB   : getStartEndDSTDates
             Retrives start and end dates for p_dates DST.
     PARAM : p_date - A date value to compare on.
     RETURN: None.
     */
     /*
     PROCEDURE getStartEndDSTDates( p_date IN DATE,
                                    o_start_date IN OUT DATE,
                                    o_end_date IN OUT DATE) IS
                                    PRAGMA AUTONOMOUS_TRANSACTION;
     BEGIN
           SELECT cdst.edate_start,
                  cdst.edate_stop
             INTO o_start_date,
                  o_end_date
             FROM ghost_PCF_DST cdst
            WHERE TO_CHAR(p_date,'YYYY')  = TO_CHAR(cdst.edate_start,'YYYY');

           EXCEPTION
                  WHEN NO_DATA_FOUND THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'GETSTARTENDDSTDATE : NO DST logic found for date'    ||
                                        ' p_date:' || wrap_error_params(p_date),
                                        SQLERRM);
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'GETSTARTENDDSTDATES '    ||
                                        ' p_date:' || wrap_error_params(p_date),
                                        SQLERRM);
     END getStartEndDSTDates;
*/

     /*
     SUB   : getStartDSTDate
             Retrives start date for p_dates DST.
     PARAM : p_date - A date value to compare on.
     RETURN: Date.
     *//*
     FUNCTION getStartDSTDate(p_date IN DATE) RETURN DATE IS
     PRAGMA AUTONOMOUS_TRANSACTION;
           v_start_date DATE;
           v_end_date DATE;
     BEGIN
           getStartEndDSTDates(p_date,
                               v_start_date,
                               v_end_date);
           RETURN v_start_date;

           EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'GETSTARTDSTDATE '    ||
                                        ' p_date:' || wrap_error_params(p_date),
                                        SQLERRM);
     END getStartDSTDate;
*/

     /*
     SUB   : getEndDSTDate
             Retrives start date for p_dates DST.
     PARAM : p_date - A date value to compare on.
     RETURN: Date.
     *//*
     FUNCTION getEndDSTDate(p_date IN DATE) RETURN DATE IS
     PRAGMA AUTONOMOUS_TRANSACTION;
           v_start_date DATE;
           v_end_date DATE;
     BEGIN
           getStartEndDSTDates(p_date,
                               v_start_date,
                               v_end_date);
           RETURN v_end_date;

           EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'GETENDDSTDATE '    ||
                                        ' p_date:' || wrap_error_params(p_date),
                                        SQLERRM);
     END getEndDSTDate;
*/

     /*
     SUB   : check_DST
             Takes a date and checks if it is on start of DST,
             on end of DST, in DST, or out of DST.
     PARAM : p_date - A date value to compare on.
     RETURN: Number
     *//*
     FUNCTION check_DST(p_date IN DATE,
                        p_check_hour IN BOOLEAN DEFAULT FALSE) RETURN NUMBER IS
           PRAGMA AUTONOMOUS_TRANSACTION;
           --v_dst_rec ghost_dst%ROWTYPE;

           v_start_date DATE;
           v_end_date DATE;
           v_start_date_trunc DATE;
           v_end_date_trunc DATE;
           v_op_day DATE;

     BEGIN

           getStartEndDSTDates(p_date,
                               v_start_date,
                               v_end_date);
           --dbms_output.put_line('befire date:' ||TO_CHAR(v_op_day,'DD-MM-YYYY  HH24:MI:SS'));

           v_start_date_trunc := TRUNC(v_start_date);
           v_end_date_trunc := TRUNC(v_end_date);

           IF NOT p_check_hour THEN
             v_op_day := TRUNC(p_date);
             v_start_date := TRUNC(v_start_date);
             v_end_date := TRUNC(v_end_date);

           ELSE
             v_op_day := p_date;
           END IF;

            /*
           dbms_output.put_line('AFter date:' ||TO_CHAR( v_op_day,'DD-MM-YYYY  HH24:MI:SS'));
           dbms_output.put_line('starte date:' ||TO_CHAR( v_start_date,'DD-MM-YYYY  HH24:MI:SS'));
           dbms_output.put_line('end date:' ||TO_CHAR( v_end_date,'DD-MM-YYYY  HH24:MI:SS'));
           *//*

           CASE
                WHEN v_op_day = v_start_date THEN
                     RETURN CONST_ON_START_DST;
                WHEN v_op_day = v_end_date THEN
                     RETURN CONST_ON_END_DST;
                WHEN v_op_day >= v_start_date_trunc AND v_op_day <= v_end_date_trunc THEN
                     RETURN CONST_IN_DST;
                WHEN v_op_day < v_start_date_trunc OR v_op_day > v_end_date_trunc THEN
                     RETURN CONST_OUT_DST;
                ELSE RETURN CONST_DST_ERROR;
           END CASE;

           EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'CHECK_DST '    ||
                                        ' p_date:' || wrap_error_params(getfdate(p_date)),
                                        SQLERRM);

     END check_DST;

     FUNCTION check_DST(p_date IN DATE,
                        p_check_hour IN NUMBER) RETURN NUMBER IS
       v_bool BOOLEAN := FALSE;
     BEGIN
       IF p_check_hour = 1 THEN
          v_bool := TRUE;
       END IF;
       RETURN check_dst(p_date,v_bool);
     END;


     /*
     SUB   : check_DST
             Takes a date and checks if it is on start of DST,
             on end of DST, in DST, or out of DST.
     PARAM : p_date - A date value to compare on.
     RETURN: Number
     *//*
     FUNCTION check_DST(p_date IN DATE,
                        o_start_dst IN OUT DATE,
                        o_end_dst IN OUT DATE,
                        p_check_hour IN BOOLEAN DEFAULT FALSE) RETURN NUMBER IS
           PRAGMA AUTONOMOUS_TRANSACTION;
           --v_dst_rec ghost_dst%ROWTYPE;

           v_start_date DATE;
           v_end_date DATE;
           v_start_date_trunc DATE;
           v_end_date_trunc DATE;
           v_op_day DATE;

     BEGIN

           v_start_date := o_start_dst;
           v_end_date := o_end_dst;

           --dbms_output.put_line('befire date:' ||TO_CHAR(v_op_day,'DD-MM-YYYY  HH24:MI:SS'));

           v_start_date_trunc := TRUNC(v_start_date);
           v_end_date_trunc := TRUNC(v_end_date);

           IF NOT p_check_hour THEN
             v_op_day := TRUNC(p_date);
             v_start_date := TRUNC(v_start_date);
             v_end_date := TRUNC(v_end_date);

           ELSE
             v_op_day := p_date;
           END IF;

            /*
           dbms_output.put_line('AFter date:' ||TO_CHAR( v_op_day,'DD-MM-YYYY  HH24:MI:SS'));
           dbms_output.put_line('starte date:' ||TO_CHAR( v_start_date,'DD-MM-YYYY  HH24:MI:SS'));
           dbms_output.put_line('end date:' ||TO_CHAR( v_end_date,'DD-MM-YYYY  HH24:MI:SS'));
           *//*

           CASE
                WHEN v_op_day = v_start_date THEN
                     RETURN CONST_ON_START_DST;
                WHEN v_op_day = v_end_date THEN
                     RETURN CONST_ON_END_DST;
                WHEN v_op_day >= v_start_date_trunc AND v_op_day <= v_end_date_trunc THEN
                     RETURN CONST_IN_DST;
                WHEN v_op_day < v_start_date_trunc OR v_op_day > v_end_date_trunc THEN
                     RETURN CONST_OUT_DST;
                ELSE RETURN CONST_DST_ERROR;
           END CASE;

           EXCEPTION
                  WHEN NO_DATA_FOUND THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'CHECK_DST : NO DST logic found for date'    ||
                                        ' p_date:' || wrap_error_params(getfdate(p_date)),
                                        SQLERRM);
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'CHECK_DST '    ||
                                        ' p_date:' || wrap_error_params(getfdate(p_date)),
                                        SQLERRM);

     END check_DST;
*/
     /*
     SUB   : GMTtoCST
             Takes a GMT date and converts to CST date
     PARAM : p_date - A date value to compare on.
     RETURN: Date
     */
     FUNCTION GMTtoCST(p_date IN DATE) RETURN DATE IS
          v_date DATE;
     BEGIN
          IF p_date IS NOT NULL THEN
            v_date:= p_date - (6*(1/24)) ;
          END IF;

          RETURN v_date;
          EXCEPTION
          WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'GMTTOCST '    ||
                                        ' p_date:' || wrap_error_params(getfdate(p_date)),
                                        SQLERRM);
     END GMTtoCST;


      /*
     SUB   : GMTtoCPT
             Takes a GMT date and converts to CPT date
     PARAM : p_date - A date value to compare on.
     RETURN: Date
     *//*
     FUNCTION GMTtoCPT(p_date IN DATE) RETURN DATE IS

          v_date DATE;
     BEGIN

          /*
          IF p_date IS NOT NULL THEN
            v_date:= get_CPT(p_date - (6*(1/24))) ;
          END IF;

          RETURN v_date;
          */
/*
          IF p_date IS NULL THEN
             RETURN  NULL;
          END IF;

          v_date:= get_CPT(p_date - (6*(1/24)));

          RETURN v_date;

          EXCEPTION
          WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'GMTTOCPT '    ||
                                        ' p_date:' || wrap_error_params(getfdate(p_date)),
                                        SQLERRM);
     END GMTtoCPT;
*/
     /*
     SUB   : get_CPT
             Takes a date and adds time to date to
             make it Central Prevailing time
     PARAM : p_date - A date value to compare on.
     RETURN: Date
     *//*
     FUNCTION get_CPT(p_date IN DATE) RETURN DATE IS
          v_date DATE := NULL;
          v_start_dst DATE;
          v_end_dst DATE;
     BEGIN

          IF p_date IS NULL THEN
             RETURN  NULL;
          END IF;

          ghost_util.getStartEndDSTDates(p_date,
                                       v_start_dst,
                                       v_end_dst);

--            state := check_DST(p_date,v_start_dst,v_end_dst);
          v_date:= p_date;

          IF (p_date >= v_start_dst) AND (p_date < v_end_dst - CONST_ADD_HOUR_TIME) THEN
               v_date := v_date + CONST_ADD_HOUR_TIME;
          END IF;

          RETURN v_date;
          EXCEPTION
          WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'GET_CPT '    ||
                                        ' p_date:' || wrap_error_params(getfdate(p_date)),
                                        SQLERRM);
     END get_CPT;



     /*
     SUB   : get_CPT
             Takes a date and adds time to date to
             make it Central Prevailing time
     PARAM : p_date - A date value to compare on.
     RETURN: Date
     *//*
     FUNCTION get_CPT(p_date IN DATE,
                      p_start_dst IN DATE,
                      p_end_dst IN DATE) RETURN DATE IS

          v_date DATE := NULL;
          v_start_dst DATE := p_start_dst;
          v_end_dst DATE := p_end_dst;
     BEGIN

          IF p_date IS NULL THEN
             RETURN  NULL;
          END IF;

          IF (p_date >= v_start_dst) AND (p_date < v_end_dst - CONST_ADD_HOUR_TIME) THEN
              v_date := v_date + CONST_ADD_HOUR_TIME;
          END IF;

          RETURN v_date;
          EXCEPTION
          WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'GET_CPT with DST dates '    ||
                                        ' p_date:' || wrap_error_params(getfdate(p_date))             ||
--                                        ' p_check_dst:' || wrap_error_params(p_check_dst)   ||
                                        ' p_start_dst:' || wrap_error_params(p_start_dst)   ||
                                        ' p_end_dst:' || wrap_error_params(p_date),
                                        SQLERRM);
     END get_CPT;

     /*
     SUB   : get_CST
             Takes a CPT date and converts to CST date
     PARAM : p_date - A date value to compare on.
     RETURN: Date
     */
     /*
     FUNCTION get_CST(p_date IN DATE,
                      p_repeat_hour IN VARCHAR2) RETURN DATE IS
          v_date DATE;
          v_start_dst DATE;
          v_end_dst DATE;
     BEGIN
          IF p_date IS NULL THEN
             RETURN  NULL;
          END IF;

          ghost_util.getStartEndDSTDates(p_date,
                                       v_start_dst,
                                       v_end_dst);
/*
           dbms_output.put_line('date:' ||TO_CHAR( p_date,'DD-MM-YYYY  HH24:MI:SS'));
           dbms_output.put_line('starte date:' ||TO_CHAR( v_start_dst,'DD-MM-YYYY  HH24:MI:SS'));
           dbms_output.put_line('end date:' ||TO_CHAR( v_end_dst,'DD-MM-YYYY  HH24:MI:SS'));
*//*
          IF p_date IS NOT NULL THEN
--            state := check_DST(p_date,v_start_dst,v_end_dst);
            v_date:= p_date;


--            dbms_output.put_line(state);
--            dbms_output.put_line('v_date:' ||TO_CHAR( v_date,'DD-MM-YYYY  HH24:MI:SS'));

            IF (p_date >= v_start_dst) AND (p_date < v_end_dst)THEN
                IF NOT ghost_pcf_util.check_If_RepeatHour(p_repeat_hour) THEN
                  v_date := v_date - CONST_ADD_HOUR_TIME;
                 END IF;
            END IF;

          ELSE
            v_date := NULL;
          END IF;

--          dbms_output.put_line('v_after_date:' ||TO_CHAR( v_date,'DD-MM-YYYY  HH24:MI:SS'));

          RETURN v_date;
          EXCEPTION
          WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'GET_CST '    ||
                                        ' p_date:' || wrap_error_params(getfdate(p_date))  ||
                                        ' p_repeat_hour:' || wrap_error_params(p_repeat_hour),
                                        SQLERRM);
     END get_CST;
*/
     /*
     SUB   : get_Business_CPT
             Takes a CST date and converts to Business CPT date
     PARAM : p_date - A date value to compare on.
     RETURN: Date
     *//*
     FUNCTION get_Business_CPT(p_date IN DATE,
                               p_offset IN NUMBER  := 0) RETURN DATE IS
          v_date DATE := NULL;
          v_start_dst DATE;
          v_end_dst DATE;
     BEGIN

          IF p_date IS NULL THEN
             RETURN  NULL;
          END IF;

          ghost_util.getStartEndDSTDates(p_date,
                                       v_start_dst,
                                       v_end_dst);

--            state := check_DST(p_date,v_start_dst,v_end_dst);
          v_date:= p_date;

          --IF (p_date > v_start_dst) AND (p_date <= v_end_dst - CONST_ADD_HOUR_TIME) THEN
          IF (p_date > v_start_dst -(p_offset/86400)) AND (p_date <= v_end_dst - CONST_ADD_HOUR_TIME -(p_offset/86400)) THEN
               v_date := v_date + CONST_ADD_HOUR_TIME;
          END IF;

          RETURN v_date;
          EXCEPTION
             WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'GET_BUSINESS_CPT '     ||
                                        ' p_date:' || wrap_error_params(getfdate(p_date)) ||
                                        ' p_offset:' || wrap_error_params(p_offset),
                                        SQLERRM);
     END;

     /*
     SUB   : get_Business_CPT
             Takes a CST date and converts to Business CPT date
     PARAM : p_date - A date value to compare on.
             p_start_dst - The Start DST date for the p_date year
             p_end_dst - The End DST date for the p_date year
     RETURN: Date
     *//*
     FUNCTION get_Business_CPT(p_date IN DATE,
                               p_offset IN NUMBER,
                               o_repeat_hour IN OUT VARCHAR2) RETURN DATE IS
          v_date DATE := NULL;
          v_start_dst DATE;
          v_end_dst DATE;
     BEGIN
          o_repeat_hour := 'N';
          ghost_util.getStartEndDSTDates(p_date,
                                       v_start_dst,
                                       v_end_dst);

          IF p_date IS NULL THEN
             RETURN  NULL;
          END IF;

          v_date:= p_date;

          IF (p_date > v_start_dst -(p_offset/86400)) AND (p_date <= v_end_dst - CONST_ADD_HOUR_TIME -(p_offset/86400)) THEN
              v_date := v_date + CONST_ADD_HOUR_TIME;
          END IF;

          IF p_date BETWEEN (v_end_dst - CONST_ADD_HOUR_TIME +1/86400)-(p_offset/86400) AND v_end_dst -(p_offset/86400) THEN
          --IF p_date BETWEEN (v_end_dst - CONST_ADD_HOUR_TIME +1/86400) AND v_end_dst THEN
              o_repeat_hour := 'Y';
          END IF;

          RETURN v_date;
          EXCEPTION
             WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'GET_BUSINESS_CPT with DST dates '      ||
                                        ' p_date:' || wrap_error_params(getfdate(p_date)) ||
                                        ' p_offset:' || wrap_error_params(p_offset) ||
                                        ' o_repeat_hour:' || wrap_error_params(o_repeat_hour),
                                        SQLERRM);
     END;

     /*
     SUB   : get_Business_CST
             Takes a CPT date and converts to Business CST date
     PARAM : p_date - A date value to compare on.
             p_repeat_hour - A character that determines wether the hour is repeated
     RETURN: Date
     *//*
     FUNCTION get_Business_CST(p_date IN DATE,
                               p_repeat_hour IN VARCHAR2)RETURN DATE IS
          v_date DATE;
          v_start_dst DATE;
          v_end_dst DATE;
     BEGIN
          IF p_date IS NULL THEN
             RETURN  NULL;
          END IF;

          ghost_util.getStartEndDSTDates(p_date,
                                       v_start_dst,
                                       v_end_dst);
          IF p_date IS NOT NULL THEN
            v_date:= p_date;

            IF (p_date > v_start_dst) AND (p_date <= v_end_dst)THEN
                IF NOT ghost_pcf_util.check_If_RepeatHour(p_repeat_hour) THEN
                  v_date := v_date - CONST_ADD_HOUR_TIME;
                END IF;
            END IF;

          ELSE
            v_date := NULL;
          END IF;

--          dbms_output.put_line('v_after_date:' ||TO_CHAR( v_date,'DD-MM-YYYY  HH24:MI:SS'));

          RETURN v_date;
          EXCEPTION
            WHEN OTHERS THEN
                         raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                          CONST_ERROR_MSG         ||
                                          'GET_BUSINESS_CST '    ||
                                          ' p_date:' || wrap_error_params(getfdate(p_date))  ||
                                          ' p_repeat_hour:' || wrap_error_params(p_repeat_hour),
                                          SQLERRM);
     END get_Business_CST;
*/
     /*
     SUB   : get_repeat_hour
             Takes a date returns 'Y'
             if it is a repeat hour flag.
     PARAM : p__date - A date value to compare on.
     RETURN: VARCHAR2(1)
     *//*
     FUNCTION get_repeat_hour(p_date IN DATE,
                              p_offset IN NUMBER := 0) RETURN VARCHAR2 IS
         v_date DATE;
         v_end_dst_date DATE;
--         v_start_offset NUMBER;
     BEGIN
         v_date := p_date;
         v_end_dst_date := getEndDSTDate(p_date);

--         dbms_output.put_line('vdate:' || getDatetoF(v_date) );
--         dbms_output.put_line('edstdate:' || getDatetoF(v_end_dst_date) );
--         dbms_output.put_line('offset:' || p_offset );

         IF (v_date BETWEEN ((v_end_dst_date - CONST_FRAMEWORK_INTGROUP/86400 ) +  (1/86400)) AND (v_end_dst_date + (p_offset/86400))) THEN
            RETURN  'Y';
         END IF;

         RETURN NULL;
         EXCEPTION
         WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG         ||
                                        'GET_REPEAT_HOUR '    ||
                                        ' p_date:' || wrap_error_params(getfdate(p_date)),
                                        SQLERRM);
     END get_repeat_hour;
*/
    -- FUNC  : get_version
    -- ACCESS: PUBLIC
    -- DESC  : Returns the current CVS revision of this package.
    --
    -- PARAM : None
    -- RETURN: VARCHAR2 - Version number
    --
    FUNCTION get_version RETURN VARCHAR2 IS

        BEGIN
            RETURN CONST_VERSION;
        END get_version;


     /*
     SUB   : update_log_start_time
             updates the ghost_PCF_LOG start/stop time.
     PARAM : None.
     RETURN: None.
     */
     PROCEDURE update_log_stop_time(p_uidlog IN NUMBER,
                                    p_stop_time IN TIMESTAMP := SYSTIMESTAMP) IS
            PRAGMA AUTONOMOUS_TRANSACTION;
     BEGIN
            UPDATE ghost_pcf_log
               SET stoptime = p_stop_time,
                   runtime = get_seconds_from_interval(p_stop_time  - starttime)
             WHERE uidlog = p_uidlog;

             COMMIT;

            EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG                                          ||
                                        ' UPDATE_LOG_STOP_TIME'                                  ||
                                        ' p_uidlog:' || wrap_error_params(p_uidlog)              ||
                                        ' p_stop_time:' || wrap_error_params(p_stop_time),
                                        SQLERRM);
     END update_log_stop_time;

    /*
     SUB   : get_seconds_from_interval
             Returns in second the time.
     PARAM : Interval.
     RETURN: NUMBER.
     */
    FUNCTION get_seconds_from_interval(p_interval interval day to second) return number is
    /*
    return:
    number of seconds & milliseconds (as a fractional part) encoded in interval variable
    */
    BEGIN
          RETURN (extract(day from p_interval) * 86400) + (extract(hour from p_interval) * 3600) + (60 * extract(minute from p_interval)) + extract(second from p_interval);
    EXCEPTION
                  WHEN OTHERS THEN
                       raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                        CONST_ERROR_MSG                                          ||
                                        ' GET_SECONDS_FROM_INTERVAL'                             ||
                                        ' p_interval:' || wrap_error_params(p_interval),
                                        SQLERRM);
    END get_seconds_from_interval;


    /*
     SUB   : getFrameworkDate
             Returns a char framework date as a DATE.
     PARAM : VARCHAR date
     RETURN: DATE.
     */
    FUNCTION getFDate(p_vdate IN VARCHAR2) RETURN DATE IS
    BEGIN
          RETURN   TO_DATE(p_vdate,CONST_FRAMEWORK_DATE_MAP);
          EXCEPTION
             WHEN OTHERS THEN
                  raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                   CONST_ERROR_MSG                                 ||
                                   ' GETFDATE '                             ||
                                   ' p_vdate:' || wrap_error_params(p_vdate),
                                   SQLERRM);
    END getFDate;


     /*
     SUB   : getDateToF
             Returns a DATE as char framework date .
     PARAM : DATE
     RETURN: VARCHAR2.
     */
    FUNCTION getDateToF(p_date IN DATE) RETURN VARCHAR2 IS
    BEGIN
          RETURN   TO_CHAR(p_date,CONST_FRAMEWORK_DATE_MAP);
          EXCEPTION
             WHEN OTHERS THEN
                  raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                   CONST_ERROR_MSG                                 ||
                                   ' GETDATETOF '                             ||
                                   ' p_date:' || wrap_error_params(getfdate(p_date)),
                                   SQLERRM);
    END getDateToF;


     /*
     SUB   : convert_number_to_anydata
             Returns anydata type
     PARAM : NUMBER
     RETURN: ANYDATA
     */
     FUNCTION convert_number_to_anydata(p_number IN NUMBER) RETURN ANYDATA IS
     BEGIN
          RETURN anydata.convertnumber(p_number);
          EXCEPTION
             WHEN OTHERS THEN
                  raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                   CONST_ERROR_MSG                                 ||
                                   ' CONVERT_NUMBER_TO_ANYDATA '                             ||
                                   ' p_number:' || wrap_error_params(p_number),
                                   SQLERRM);
     END convert_number_to_anydata;

     /*
     SUB   : convert_varchar_to_anydata
             Returns anydata type
     PARAM : NUMBER
     RETURN: ANYDATA
     */
     FUNCTION convert_varchar_to_anydata(p_varchar IN VARCHAR2) RETURN ANYDATA IS
     BEGIN
          RETURN anydata.convertvarchar2(p_varchar);
          EXCEPTION
             WHEN OTHERS THEN
                  raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                   CONST_ERROR_MSG                                 ||
                                   ' CONVERT_VARCHAR_TO_ANYDATA '                  ||
                                   ' p_varchar:' || wrap_error_params(p_varchar),
                                   SQLERRM);
     END convert_varchar_to_anydata;


      /*
     SUB   : convert_anydata_to_number
             Returns anydata type
     PARAM : NUMBER
     RETURN: ANYDATA
     */
     FUNCTION convert_anydata_to_number(p_anydata IN ANYDATA) RETURN NUMBER IS
          v_number NUMBER;
          v_temp NUMBER;
     BEGIN
          v_temp := p_anydata.getnumber(v_number);
          RETURN v_number;
          EXCEPTION
             WHEN OTHERS THEN
                  raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                   CONST_ERROR_MSG                                 ||
                                   ' CONVERT_ANYDATATO_NUMBER ' ||
                                   'p_anydata.gettypename:' ||wrap_error_params(p_anydata.gettypename),
                                   SQLERRM);
     END convert_anydata_to_number;

     /*
     SUB   : convert_anydata_to_varchar
             Returns anydata type
     PARAM : NUMBER
     RETURN: ANYDATA
     */
     FUNCTION convert_anydata_to_varchar(p_anydata IN ANYDATA) RETURN VARCHAR2 IS
          v_varchar VARCHAR2(4000);
          v_temp NUMBER;
     BEGIN
          v_temp := p_anydata.getvarchar2(v_varchar);
          RETURN v_varchar;
          EXCEPTION
             WHEN OTHERS THEN
                  raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                   CONST_ERROR_MSG                                 ||
                                   ' CONVERT_ANYDATA_TO_VARCHAR ' ||
                                   'p_anydata.gettypename:' ||wrap_error_params(p_anydata.gettypename),
                                   SQLERRM);
     END convert_anydata_to_varchar;

     /*
     SUB   : bind_anydata_to_cursor
             Returns anydata type
     PARAM : INTEGER, ghost_array_of_strings, ghost_array_of_anydata
     RETURN:
     */
     PROCEDURE bind_anydata_to_cursor(p_cursor IN INTEGER,
                                      p_names IN ghost_array_of_strings,
                                      p_values IN ghost_array_of_anydata) IS
          v_number NUMBER;
          v_varchar VARCHAR2(4000);
          v_type VARCHAR2 (50);
          v_cursor INTEGER;
          e_type_not_found EXCEPTION;
     BEGIN
          v_cursor := p_cursor;
          FOR i IN 1..p_names.COUNT LOOP
                v_type := anydata.gettypename(p_values(i));
                CASE v_type
                     WHEN 'SYS.NUMBER' THEN
                      dbms_sql.bind_variable(v_cursor, p_names(i),
                                                       convert_anydata_to_number(p_values(i)));
                     WHEN 'SYS.VARCHAR2' THEN
                      dbms_sql.bind_variable(v_cursor, p_names(i),
                                                       convert_anydata_to_varchar(p_values(i)));
                     ELSE
                         RAISE e_type_not_found;
                END CASE;


--         dbms_output.put_line('Bind Value: ' || v_array_of_bind_values(i));
       END LOOP;

           EXCEPTION
             WHEN e_type_not_found THEN
                  IF v_cursor!=0 THEN
                     dbms_sql.close_cursor(v_cursor);
                  END IF;
                  raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                   CONST_ERROR_MSG                                 ||
                                   ' BIND_ANYDATA_TO_CURSOR Data Type not found!' ||
                                   ' v_type:' || wrap_error_params(v_type),
                                   SQLERRM);
             WHEN OTHERS THEN
                  IF v_cursor!=0 THEN
                     dbms_sql.close_cursor(v_cursor);
                  END IF;
                  raise_ghost_error (COSNT_PCF_ERROR_UNKNOWN,
                                   CONST_ERROR_MSG                                 ||
                                   ' BIND_ANYDATA_TO_CURSOR ',
                                   SQLERRM);
     END bind_anydata_to_cursor;


END ghost_UTIL;
/

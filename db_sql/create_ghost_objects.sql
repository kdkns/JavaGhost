--
-- $Id: create_ghost_objects.sql,v 1.16 2012/10/02 14:58:28 mackermann Exp $
--

CREATE OR REPLACE TYPE ghost_operation_flag_obj AS OBJECT (
    flag_time           NUMBER(19),
    flag_status         NUMBER(19),
    flag_spidst         NUMBER(19),
    flag_attributes     NUMBER(19),
    flag_divide         NUMBER(19),
    flag_multiply       NUMBER(19),
    flag_existence      NUMBER(19),
    CONSTRUCTOR FUNCTION ghost_operation_flag_obj RETURN SELF AS RESULT
);
/

CREATE OR REPLACE TYPE BODY ghost_operation_flag_obj AS
    CONSTRUCTOR FUNCTION ghost_operation_flag_obj RETURN SELF AS RESULT AS
        BEGIN
            RETURN;
        END;
END;
/

CREATE OR REPLACE TYPE ghost_job_child_obj AS OBJECT (
    process             VARCHAR2(30),
    status              VARCHAR2(30),
    uidvm               NUMBER(19),
    start_range         NUMBER,
    end_range           NUMBER,
    record_count        NUMBER,
    bulk_id             NUMBER,
    driver_id           VARCHAR2(100),
    message             VARCHAR2(1024)
);
/

CREATE OR REPLACE TYPE ghost_array_of_strings IS VARRAY (50) OF VARCHAR2(4000);
/
CREATE OR REPLACE TYPE ghost_array_of_anydata IS VARRAY (50) OF ANYDATA;
/

CREATE OR REPLACE TYPE ghost_query AS OBJECT (
    sql_query           VARCHAR2(4000),
    arguments           VARCHAR2(500),

    CONSTRUCTOR FUNCTION ghost_query RETURN SELF AS RESULT
);
/

CREATE OR REPLACE TYPE BODY ghost_query AS
    CONSTRUCTOR FUNCTION ghost_query RETURN SELF AS RESULT AS
        BEGIN
            RETURN;
        END;
END;
/

CREATE OR REPLACE TYPE ghost_tab_blob     IS TABLE OF BLOB;
/
CREATE OR REPLACE TYPE ghost_tab_varchar2 IS TABLE OF VARCHAR2(75);
/
CREATE OR REPLACE TYPE ghost_tab_date     IS TABLE OF DATE;
/
CREATE OR REPLACE TYPE ghost_tab_number   IS TABLE OF NUMBER;
/
CREATE OR REPLACE TYPE ghost_tab_char     IS TABLE OF CHAR;
/
CREATE OR REPLACE TYPE ghost_tab_rules    IS TABLE OF VARCHAR2(255);
/
CREATE OR REPLACE TYPE ghost_tab_date     IS TABLE OF DATE;
/
CREATE OR REPLACE TYPE ghost_tab_number   IS TABLE OF NUMBER;
/
CREATE OR REPLACE TYPE ghost_tab_char     IS TABLE OF CHAR;
/
CREATE OR REPLACE TYPE ghost_tab_rules    IS TABLE OF VARCHAR2(255);
/

CREATE OR REPLACE TYPE ghost_data_obj AS OBJECT (
    blob_value          BLOB,
    scalar_value        NUMBER,
    uidvm               NUMBER,
    starttime           DATE,
    stoptime            DATE,
    spi                 NUMBER,
    intervalcount       NUMBER,
    max                 NUMBER,
    min                 NUMBER,
    total               NUMBER,
    dst_participant     CHAR,
    custom_1            VARCHAR2(125),
    custom_2            VARCHAR2(125),
    custom_3            VARCHAR2(125),
    custom_4            VARCHAR2(125),
    custom_5            VARCHAR2(125),
    custom_6            VARCHAR2(125),
    custom_7            VARCHAR2(125),
    custom_8            VARCHAR2(125),
    custom_9            VARCHAR2(125),
    custom_10           VARCHAR2(125),
    custom_11            VARCHAR2(125),
    custom_12            VARCHAR2(125),
    custom_13            VARCHAR2(125),
    custom_14            VARCHAR2(125),
    custom_15            VARCHAR2(125),
    custom_16            VARCHAR2(125),
    custom_17            VARCHAR2(125),
    custom_18            VARCHAR2(125),
    custom_19            VARCHAR2(125),
    custom_20           VARCHAR2(125),
    custom_date_1            DATE,
    custom_date_2            DATE,
    custom_date_3            DATE,
    custom_date_4            DATE,
    custom_date_5            DATE,
    custom_date_6            DATE,
    custom_date_7            DATE,
    custom_date_8            DATE,
    custom_date_9            DATE,
    custom_date_10           DATE,

    CONSTRUCTOR FUNCTION ghost_data_obj RETURN SELF AS RESULT
);
/

CREATE OR REPLACE TYPE BODY ghost_data_obj AS
    CONSTRUCTOR FUNCTION ghost_data_obj RETURN SELF AS RESULT AS
        BEGIN
            RETURN;
        END;
END;
/

CREATE OR REPLACE TYPE ghost_bulk_data_obj AS OBJECT (
    blob_value          ghost_tab_blob,
    scalar_value        ghost_tab_number,
    uidvm               ghost_tab_number,
    starttime           ghost_tab_date,
    stoptime            ghost_tab_date,
    spi                 ghost_tab_number,
    intervalcount       ghost_tab_number,
    dst_participant     ghost_tab_char,
    custom_1            ghost_tab_varchar2,
    custom_2            ghost_tab_varchar2,
    custom_3            ghost_tab_varchar2,
    custom_4            ghost_tab_varchar2,
    custom_5            ghost_tab_varchar2,
    custom_6            ghost_tab_varchar2,
    custom_7            ghost_tab_varchar2,
    custom_8            ghost_tab_varchar2,
    custom_9            ghost_tab_varchar2,
    custom_10           ghost_tab_varchar2,
    custom_11            ghost_tab_varchar2,
    custom_12            ghost_tab_varchar2,
    custom_13            ghost_tab_varchar2,
    custom_14            ghost_tab_varchar2,
    custom_15            ghost_tab_varchar2,
    custom_16            ghost_tab_varchar2,
    custom_17            ghost_tab_varchar2,
    custom_18            ghost_tab_varchar2,
    custom_19            ghost_tab_varchar2,
    custom_20           ghost_tab_varchar2,
    custom_date_1            ghost_tab_date,
    custom_date_2            ghost_tab_date,
    custom_date_3            ghost_tab_date,
    custom_date_4            ghost_tab_date,
    custom_date_5            ghost_tab_date,
    custom_date_6            ghost_tab_date,
    custom_date_7            ghost_tab_date,
    custom_date_8            ghost_tab_date,
    custom_date_9            ghost_tab_date,
    custom_date_10           ghost_tab_date,

    CONSTRUCTOR FUNCTION ghost_bulk_data_obj RETURN SELF AS RESULT
);
/

CREATE OR REPLACE TYPE BODY ghost_bulk_data_obj AS
    CONSTRUCTOR FUNCTION ghost_bulk_data_obj RETURN SELF AS RESULT AS
        BEGIN
            RETURN;
        END;
END;
/

CREATE OR REPLACE TYPE ghost_mt_arguments AS OBJECT (
    driver_query            ghost_query,
    update_query            ghost_query,
    thread_sql_query        ghost_query,
    collection_id           NUMBER,
    new_collection_id       NUMBER,
    batch_job_name          VARCHAR2(20),
    batch_job_modifiers     VARCHAR2(30),
    batch_function_name     VARCHAR2(50),
    batch_function_bind_args VARCHAR2(4000),
    batch_function_args     VARCHAR2(4000),
    custom_declare_block    VARCHAR2(4000),
    custom_loop_block       VARCHAR2(4000),
    custom_bind_args_names  ghost_array_of_strings,
    custom_bind_args_values ghost_array_of_anydata,
    num_processed_rows      NUMBER,
    timeout                 NUMBER,
    thread_timeout          NUMBER,
    transaction_id          NUMBER,
    job_priority            NUMBER,

    CONSTRUCTOR FUNCTION ghost_mt_arguments RETURN SELF AS RESULT
);
/

CREATE OR REPLACE TYPE BODY ghost_mt_arguments AS
    CONSTRUCTOR FUNCTION ghost_mt_arguments RETURN SELF AS RESULT AS
        BEGIN
            SELF.update_query            := ghost_query();
            SELF.driver_query            := ghost_query();
            SELF.thread_sql_query        := ghost_query();
            SELF.custom_bind_args_names  := ghost_array_of_strings();
            SELF.custom_bind_args_values := ghost_array_of_anydata();

            RETURN;

            EXCEPTION
                WHEN OTHERS THEN
                    ghost_util.raise_ghost_error (
                        ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                        ghost_util.CONST_ERROR_MSG || 'GHOST_MT_ARGUMENTS CONSTRUCTOR ',
                        SQLERRM);
        END;
END;
/

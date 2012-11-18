--
-- $Id: create_Ghost_VM.sql,v 1.8 2012/09/26 20:33:24 arohatgi Exp $
--

CREATE TABLE ghost_vm (
    uidvm                       NUMBER(19),
    ghost_pointer               VARCHAR2(256),
    ghost_pointer_partition_date DATE,
    ghost_pointer_table         VARCHAR2(128),
    ghost_pointer_field         VARCHAR2(128),
    ghost_data_column           VARCHAR2(128),
    ghost_collection_id         NUMBER(19)      DEFAULT 0,
    ghost_transaction_id        NUMBER(19),
    ghost_possession_id         NUMBER(19),
    ghost_bulk_drive_field      VARCHAR2(128),
    last_update_date            DATE            DEFAULT SYSDATE,
    blob_value                  BLOB            DEFAULT NULL,
    meta_starttime              DATE,
    meta_stoptime               DATE,
    metablob_spi                NUMBER,
    metablob_total              NUMBER,
    metablob_max                NUMBER,
    metablob_min                NUMBER,
    metablob_intervalcount      NUMBER,
    metablob_dst_participant    CHAR,
    number_value                NUMBER,
    string_value                VARCHAR2(1024),
    range_id                    NUMBER,
    bulk_id                     NUMBER,
    bulk_insert_id              NUMBER,
    CUSTOM_1                    VARCHAR2(125 BYTE),
    CUSTOM_2                    VARCHAR2(125 BYTE),
    CUSTOM_3                    VARCHAR2(125 BYTE),
    CUSTOM_4                    VARCHAR2(125 BYTE),
    CUSTOM_5                    VARCHAR2(125 BYTE),
    CUSTOM_6                    VARCHAR2(125 BYTE),
    CUSTOM_7                    VARCHAR2(125 BYTE),
    CUSTOM_8                    VARCHAR2(125 BYTE),
    CUSTOM_9                    VARCHAR2(125 BYTE),
    CUSTOM_10                   VARCHAR2(125 BYTE),
    CUSTOM_11                   VARCHAR2(125 BYTE),
    CUSTOM_12                   VARCHAR2(125 BYTE),
    CUSTOM_13                   VARCHAR2(125 BYTE),
    CUSTOM_14                   VARCHAR2(125 BYTE),
    CUSTOM_15                   VARCHAR2(125 BYTE),
    CUSTOM_16                   VARCHAR2(125 BYTE),
    CUSTOM_17                   VARCHAR2(125 BYTE),
    CUSTOM_18                   VARCHAR2(125 BYTE),
    CUSTOM_19                   VARCHAR2(125 BYTE),
    CUSTOM_20                   VARCHAR2(125 BYTE),
    CUSTOM_DATE_1               DATE,
    CUSTOM_DATE_2               DATE,
    CUSTOM_DATE_3               DATE,
    CUSTOM_DATE_4               DATE,
    CUSTOM_DATE_5               DATE,
    CUSTOM_DATE_6               DATE,
    CUSTOM_DATE_7               DATE,
    CUSTOM_DATE_8               DATE,
    CUSTOM_DATE_9               DATE,
    CUSTOM_DATE_10              DATE
);

CREATE unique index idx_ghost_uid ON ghost_vm(uidvm);

CREATE INDEX idx_ghost_point ON ghost_vm(ghost_pointer,ghost_pointer_partition_date,ghost_collection_id);
CREATE INDEX idx_ghost_cid   ON ghost_vm(ghost_collection_id, ghost_transaction_id);
CREATE INDEX idx_ghost_tid   ON ghost_vm(ghost_transaction_id);
CREATE INDEX idx_ghost_pid   ON ghost_vm(ghost_possession_id);
CREATE INDEX idx_ghost_bdf   ON ghost_vm(ghost_bulk_drive_field, ghost_possession_id);
CREATE INDEX idx_ghost_rid   ON ghost_vm(range_id);
CREATE INDEX idx_ghost_bid   ON ghost_vm(bulk_id);
CREATE INDEX idx_ghost_biid  ON ghost_vm(bulk_insert_id);
CREATE TABLE ghost_dst (
    edate_start                 DATE                            NOT NULL,
    edate_stop                  DATE                            NOT NULL,
    timestamp                   DATE            default SYSDATE
);
CREATE INDEX ix_ghost_dst ON ghost_dst (TO_CHAR(edate_start,'YYYY'));


CREATE TABLE ghost_action_error (
    uiderror                    NUMBER(19)                      NOT NULL,
    batch_id                    NUMBER(9)                       NOT NULL,
    event_id                    NUMBER(9)                       NOT NULL,
    control_id                  NUMBER(9)                       NOT NULL,
    action_id                   NUMBER(9),
    operating_date              DATE,
    table_name                  VARCHAR2(255),
    column_name                 VARCHAR2(255),
    record_id                   NUMBER(19),
    errorcode                   VARCHAR2(255)                   NOT NULL,
    message                     VARCHAR2(2048)                  NOT NULL,
    parameters                  VARCHAR2(2048),
    creation_date               DATE            DEFAULT SYSDATE
);
ALTER TABLE ghost_action_error ADD CONSTRAINT pk_error_detail_uiderror PRIMARY KEY (uiderror) USING INDEX;


CREATE TABLE ghost_action_error_detail (
    uiderror                    NUMBER(19)                      NOT NULL,
    uiderrordetail              NUMBER(19)                      NOT NULL,
    record_id                   VARCHAR2(255),
    table_name                  VARCHAR2(255),
    column_name                 VARCHAR2(255),
    errorcode                   VARCHAR2(255)                   NOT NULL,
    message                     VARCHAR2(2048)                  NOT NULL,
    parameters                  VARCHAR2(2048),
    creation_date               DATE            DEFAULT SYSDATE
);


CREATE TABLE ghost_pcf_log (
    uidlog                      NUMBER(19),
    pcf_bid                     NUMBER(9),
    pcf_eid                     NUMBER(9),
    pcf_cid                     NUMBER(9),
    pcf_aid                     NUMBER(9),
    pcf_op                      DATE,
    process_type                VARCHAR2(50),
    action                      VARCHAR2(50),
    status                      VARCHAR2(50),
    message                     VARCHAR2(3200),
    record_count                NUMBER(18,0),
    starttime                   TIMESTAMP,
    stoptime                    TIMESTAMP,
    runtime                     NUMBER,
    creation_date               TIMESTAMP       DEFAULT SYSTIMESTAMP
);
INSERT INTO ghost_dst (
    edate_start,
    edate_stop) 
WITH
    ODQ AS (
        SELECT ADD_MONTHS(TRUNC(TO_DATE('2007-01-01','YYYY-MM-DD')),(ROWNUM-1)*12) ODay 
          FROM DUAL CONNECT BY LEVEL <= 100
    ),
    DDQ AS (
        SELECT ADD_MONTHS(TRUNC(ODQ.ODay,'YEAR'), 2)  MDAY, 
            ADD_MONTHS(TRUNC(ODQ.ODay,'YEAR'), 10) NDAY
          FROM ODQ
    ) 
SELECT DECODE (TRIM(TO_CHAR(DDQ.MDAY,'DAY')),'SUNDAY', NEXT_DAY(DDQ.MDAY,'SUNDAY')+1/12,NEXT_DAY(DDQ.MDAY,'SUNDAY')+7+1/12) edate_start, 
       DECODE (TRIM(TO_CHAR(DDQ.NDAY,'DAY')),'SUNDAY', DDQ.NDAY+1/12, NEXT_DAY(DDQ.NDAY,'SUNDAY')+1/12) edate_stop
  FROM DDQ;

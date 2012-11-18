--
-- $Id: create_dummy.sql,v 1.6 2012/09/07 15:12:24 arohatgi Exp $
--

CREATE TABLE dummy (
    uidcolumn                   NUMBER                          NOT NULL,
    iamnumbercolumn             NUMBER,
    iamstringcolumn             VARCHAR2(25 BYTE),
    iamdatecolumn               DATE,
    iamblobcolumn               BLOB,
    spi                         NUMBER,
    dst_participant             CHAR,
    intervalcount               NUMBER,
    starttime                   DATE,
    stoptime                    DATE,
    total                       NUMBER,
    max                         NUMBER,
    min                         NUMBER
);

CREATE TABLE dummy2 (
    uidcolumn                   NUMBER                          NOT NULL,
    uidheader                   NUMBER                          NOT NULL,
    iamnumbercolumn             NUMBER,
    iamstringcolumn             VARCHAR2(25 BYTE),
    iamdatecolumn               DATE,
    iamblobcolumn               BLOB,
    spi                         NUMBER,
    dst_participant             CHAR,
    intervalcount               NUMBER,
    starttime                   DATE,
    stoptime                    DATE,
    total                       NUMBER,
    max                         NUMBER,
    min                         NUMBER
);

CREATE TABLE dummy3 (
    uidcolumn                   NUMBER                          NOT NULL,
    iamnumbercolumn             NUMBER,
    iamstringcolumn             VARCHAR2(25 BYTE),
    iamdatecolumn               DATE,
    iamblobcolumn               BLOB,
    spi                         NUMBER,
    dst_participant             CHAR,
    intervalcount               NUMBER,
    starttime                   DATE,
    stoptime                    DATE,
    total                       NUMBER,
    max                         NUMBER,
    min                         NUMBER
);

CREATE TABLE dummyheader (
    uidcolumn                   NUMBER                          NOT NULL,
    saverecorder                VARCHAR2(25)                    NOT NULL,
    savechannel                 NUMBER(19)                      NOT NULL,
    starttime                   DATE,
    stoptime                    DATE,
    qsecode                     VARCHAR2(25 BYTE)
);


CREATE SEQUENCE LS_SEQ_DUMMY1
    START WITH   1
    INCREMENT BY 1
    MAXVALUE     1.0E28
    MINVALUE     1
    CACHE        50
    CYCLE;

CREATE SEQUENCE LS_SEQ_DUMMY2
    START WITH   1
    INCREMENT BY 1
    MAXVALUE     1.0E28
    MINVALUE     1
    CACHE        50
    CYCLE;

CREATE SEQUENCE LS_SEQ_DUMMY3
    START WITH   1
    INCREMENT BY 1
    MAXVALUE     1.0E28
    MINVALUE     1
    CACHE        50
    CYCLE;

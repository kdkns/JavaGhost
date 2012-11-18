--
-- $Id: create_Ghost_Sequence.sql,v 1.3 2012/08/24 16:48:08 mackermann Exp $
--

CREATE SEQUENCE seq_ghostuidvm
    START WITH   1
    INCREMENT BY 1
    MAXVALUE     1.0E28
    MINVALUE     1
    CACHE        50
    CYCLE;

CREATE SEQUENCE seq_ghosttidvm
    START WITH   1
    INCREMENT BY 1
    MAXVALUE     1.0E28
    MINVALUE     1
    CACHE        50
    CYCLE;

CREATE SEQUENCE seq_ghostpidvm
    START WITH   1
    INCREMENT BY 1
    MAXVALUE     1.0E28
    MINVALUE     1
    CACHE        50
    CYCLE;

CREATE SEQUENCE seq_ghostbidvm
    START WITH   1
    INCREMENT BY 1
    MAXVALUE     1.0E28
    MINVALUE     1
    CACHE        50
    CYCLE;

CREATE SEQUENCE seq_ghostuiderror
    START WITH   1
    INCREMENT BY 1
    MAXVALUE     1.0E28
    MINVALUE     1
    CACHE        50
    CYCLE;

CREATE SEQUENCE seq_ghostlog
    START WITH   1
    INCREMENT BY 1
    MAXVALUE     1.0E28
    MINVALUE     1
    CACHE        50
    CYCLE;

CREATE SEQUENCE seq_ghostuidgrperror
    START WITH   1
    INCREMENT BY 1
    MAXVALUE     1.0E28
    MINVALUE     1
    CACHE        50
    CYCLE;

CREATE SEQUENCE seq_ghostcjp
    START WITH   1
    INCREMENT BY 1
    MAXVALUE     1.0E28
    MINVALUE     1
    CACHE        50
    CYCLE;

CREATE SEQUENCE seq_ghostcorelationid
    START WITH   1
    INCREMENT BY 1
    MAXVALUE     1.0E28
    MINVALUE     1
    CACHE        50
    CYCLE;

CREATE SEQUENCE seq_ghostbiid
    START WITH   1
    INCREMENT BY 1
    MAXVALUE     1.0E28
    MINVALUE     1
    CACHE        50
    CYCLE;

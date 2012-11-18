-- ===============================================================================
-- $Id: Ghost_uninstall.sql,v 1.2 2012/09/27 19:32:48 mackermann Exp $
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
-- ===============================================================================

DECLARE

    CURSOR c_tables IS
        SELECT object_name
          FROM user_objects
         WHERE object_type = 'TABLE'
           AND object_name LIKE 'GHOST^_%' ESCAPE '^'
         UNION
        SELECT object_name
          FROM user_objects
         WHERE object_type = 'TABLE'
           AND object_name IN ('DUMMY',
                               'DUMMY2',
                               'DUMMY3',
                               'DUMMYHEADER');

    CURSOR c_views IS
        SELECT object_name
          FROM user_objects
         WHERE object_type = 'VIEW'
           AND object_name LIKE 'GHOST^_%' ESCAPE '^';
                               
    CURSOR c_sequences IS
        SELECT object_name
          FROM user_objects
         WHERE object_type = 'SEQUENCE'
           AND object_name LIKE 'SEQ^_GHOST%' ESCAPE '^'
         UNION
        SELECT object_name
          FROM user_objects
         WHERE object_type = 'SEQUENCE'
           AND object_name LIKE 'LS^_SEQ^_DUMMY%' ESCAPE '^';

    CURSOR c_packages IS
        SELECT object_name
          FROM user_objects
         WHERE object_type = 'PACKAGE'
           AND object_name LIKE 'GHOST^_%' ESCAPE '^';

    CURSOR c_types IS
        SELECT object_name
          FROM user_objects
         WHERE object_type = 'TYPE'
           AND object_name LIKE 'GHOST^_%' ESCAPE '^';

    BEGIN

        BEGIN
            DBMS_AQADM.STOP_QUEUE (queue_name => 'ghost_queue');
            DBMS_AQADM.DROP_QUEUE (queue_name => 'ghost_queue');
            DBMS_AQADM.DROP_QUEUE_TABLE (queue_table => 'ghost_job_queue');
            NULL;
            EXCEPTION WHEN OTHERS THEN NULL;
        END;    

        FOR rec IN c_tables LOOP
            EXECUTE IMMEDIATE 'DROP TABLE ' || rec.object_name || ' CASCADE CONSTRAINTS PURGE';
        END LOOP;

        FOR rec IN c_sequences LOOP
            EXECUTE IMMEDIATE 'DROP SEQUENCE ' || rec.object_name;
        END LOOP;

        FOR rec IN c_packages LOOP
            EXECUTE IMMEDIATE 'DROP PACKAGE  ' || rec.object_name;
        END LOOP;

        FOR rec IN c_views LOOP
            EXECUTE IMMEDIATE 'DROP VIEW ' || rec.object_name;
        END LOOP;

        FOR rec IN c_types LOOP
            EXECUTE IMMEDIATE 'DROP TYPE ' || rec.object_name || ' FORCE';
        END LOOP;

        EXECUTE IMMEDIATE 'PURGE RECYCLEBIN';

END;

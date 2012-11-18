-- ===============================================================================
-- $Id: Ghost_compile_packages.sql,v 1.1 2012/08/24 16:58:36 mackermann Exp $
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

    CURSOR c_packages IS
        SELECT object_name
          FROM user_objects
         WHERE object_type = 'PACKAGE'
           AND object_name LIKE 'GHOST^_%' ESCAPE '^';

BEGIN

    FOR rec IN c_packages LOOP
        BEGIN        
            EXECUTE IMMEDIATE 'ALTER PACKAGE ' || rec.object_name || ' COMPILE PACKAGE';
            EXCEPTION WHEN OTHERS THEN NULL;
        END;
    END LOOP;

END;
/

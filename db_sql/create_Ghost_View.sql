--
-- $Id: create_Ghost_View.sql,v 1.1 2012/08/24 16:47:31 mackermann Exp $
--

CREATE OR REPLACE VIEW ghost_vm_blob_view AS

    WITH 
        iloopq AS (
            SELECT ROWNUM - 1 intdnum
              FROM DUAL CONNECT BY LEVEL <= 100
        )
    SELECT uidvm,
           intdnum + 1 interval_number,
           TO_CHAR(utl_raw.cast_to_binary_double(utl_raw.reverse(utl_raw.substr(blob_value, intdnum * 8 + 1, 8))), 'FM999G999G999G999G999D999999999999999999999999') intdvalue,
           CASE
               WHEN utl_raw.substr(blob_value, intdnum + 1 * 1 + (metablob_intervalcount * 8) + 8, 1) = '39' THEN '9'
               WHEN utl_raw.substr(blob_value, intdnum + 1 * 1 + (metablob_intervalcount * 8) + 8, 1) = '20' THEN ' '
               ELSE utl_raw.cast_to_varchar2(utl_raw.substr(blob_value, intdnum + 1 * 1 + (metablob_intervalcount * 8) + 8, 1))
           END intdstatus,
           GV.uidvm ghost_uidvm
      FROM ghost_vm GV,
           iloopq
     WHERE iloopq.intdnum < metablob_intervalcount;

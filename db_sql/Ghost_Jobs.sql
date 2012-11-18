CREATE OR REPLACE PACKAGE ghost_jobs AS

-- ==============================================================================
-- $Id: Ghost_Jobs.sql,v 1.3 2012/08/24 16:48:08 mackermann Exp $
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

    -- Version
    --
    CONST_VERSION CONSTANT VARCHAR2(20) := '$Revision: 1.3 $';

    -- Types / Subtypes
    --
    SUBTYPE tString   IS VARCHAR2(1024);
    SUBTYPE tJob      IS VARCHAR2(2048);
    SUBTYPE tDBString IS VARCHAR2(255);

    TYPE tArrayJob      IS TABLE OF tJob   INDEX BY pls_integer;
    TYPE tArraySeqByJob IS TABLE OF NUMBER INDEX BY tJob;

    -- Procedure/Function Prototypes
    --
    PROCEDURE execute_batch (
        p_jobs          IN tArrayJob,
        p_args          IN tArrayJob,
        p_batch_program IN VARCHAR2,
        p_commit        IN BOOLEAN := true
    );
    
    FUNCTION get_version RETURN VARCHAR2;

END ghost_jobs;
/


CREATE OR REPLACE PACKAGE BODY ghost_jobs AS

    -- SUB   : execute_batch_helper
    -- ACCESS: PRIVATE
    -- DESC  :
    --
    -- PARAM : p_job           -
    --         p_arg           -
    --         p_batch_program -
    --         p_commit        -
    --
    -- RETURN: None
    --
    PROCEDURE execute_batch_helper(p_job IN tJob, p_arg IN VARCHAR2, p_batch_program IN VARCHAR2, p_commit IN BOOLEAN := true) IS

        v_job tJob := ltrim(rtrim(p_job));

        BEGIN

            dbms_scheduler.create_job (
                job_name   => v_job,
                job_type   => 'PLSQL_BLOCK',
                job_action => 'BEGIN ' || p_batch_program || '('|| p_arg || '); END;',
                enabled    => true,
                auto_drop  => true
            );

            EXCEPTION
                WHEN OTHERS THEN
                    ghost_util.raise_ghost_error (
                        ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                        ghost_util.CONST_ERROR_MSG || 'EXECUTE_BATCH_HELPER' || ghost_util.CONST_ERROR_MSG_PARAM || ' pJob:' || ghost_util.wrap_error_params(p_job),
                        SQLERRM
                    );

        END execute_batch_helper;


    -- SUB   : execute_batch
    -- ACCESS: PUBLIC
    -- DESC  :
    --
    -- PARAM : p_jobs          -
    --         p_args          -
    --         p_batch_program -
    --         p_commit        -
    --
    -- RETURN: None
    --
    PROCEDURE execute_batch (p_jobs IN tArrayJob, p_args IN tArrayJob, p_batch_program IN VARCHAR2, p_commit IN BOOLEAN := true) IS

        BEGIN

            IF p_jobs.count > 0 THEN
                FOR nIndex IN p_jobs.FIRST..p_jobs.LAST LOOP
                    execute_batch_helper(p_jobs(nIndex), p_args(nIndex), p_batch_program, p_commit);
                END LOOP;
            END IF;

            EXCEPTION
                WHEN OTHERS THEN
                    ghost_util.raise_ghost_error (
                        ghost_util.COSNT_PCF_ERROR_UNKNOWN,
                        ghost_util.CONST_ERROR_MSG || 'EXECUTE_BATCH' || ghost_util.CONST_ERROR_MSG_PARAM || ' p_batch_program:' || ghost_util.wrap_error_params(p_batch_program),
                        SQLERRM
                    );

        END execute_batch;


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

END ghost_jobs;
/
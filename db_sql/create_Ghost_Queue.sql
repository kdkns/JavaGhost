--
-- $Id: create_Ghost_Queue.sql,v 1.5 2012/08/24 16:48:08 mackermann Exp $
--

BEGIN

	DBMS_AQADM.CREATE_QUEUE_TABLE (
        queue_table        => 'ghost_job_queue',
        queue_payload_type => 'ghost_job_child_obj'
    );

    DBMS_AQADM.CREATE_QUEUE (
        queue_name  => 'ghost_queue',
        queue_table => 'ghost_job_queue'
    );    
    
    DBMS_AQADM.START_QUEUE (
        queue_name => 'ghost_queue'
    );

END;

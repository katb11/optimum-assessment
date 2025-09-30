CREATE OR REPLACE PROCEDURE calculate_weekly_hours_all_employees(
    p_start_time IN TIMESTAMP,
    p_end_time IN TIMESTAMP DEFAULT NULL
) AS
BEGIN
    DECLARE
        v_end_time      TIMESTAMP;
        v_interval      INTERVAL DAY TO SECOND;
        v_diff_minutes  NUMBER;
        v_total_minutes NUMBER;
        v_last_in_time  TIMESTAMP;
        test            TIMESTAMP;
    BEGIN
        IF p_end_time IS NULL THEN
            v_end_time := p_start_time + INTERVAL '7' DAY;
        ELSE
            v_end_time := p_end_time;
        END IF;

        DECLARE
            CURSOR employee_cursor IS
                SELECT DISTINCT employee_id
                FROM employee_punch_records
                WHERE punch_time >= p_start_time
                  AND punch_time < v_end_time;

        BEGIN
            FOR employee_rec IN employee_cursor
                LOOP
                    v_total_minutes := 0;
                    v_last_in_time := NULL;

                    FOR punch_rec IN (
                        SELECT punch_time, punch_flag
                        FROM employee_punch_records
                        WHERE employee_id = employee_rec.employee_id
                          AND punch_time >= p_start_time
                          AND punch_time < v_end_time
                        ORDER BY punch_time
                        )
                        LOOP
                            IF punch_rec.punch_flag = 1 THEN -- punched in
                                v_last_in_time := punch_rec.punch_time;
                            ELSIF punch_rec.punch_flag = 0 THEN -- punched out
                                IF v_last_in_time IS NOT NULL THEN
                                    v_interval := punch_rec.punch_time - v_last_in_time;
                                    v_diff_minutes := EXTRACT(DAY FROM v_interval) * 24 * 60 +
                                                      EXTRACT(HOUR FROM v_interval) * 60 +
                                                      EXTRACT(MINUTE FROM v_interval) +
                                                      EXTRACT(SECOND FROM v_interval) / 60;
                                    v_total_minutes := v_total_minutes + v_diff_minutes;
                                    v_last_in_time := NULL;
                                END IF;
                            END IF;
                        END LOOP;
                    DBMS_OUTPUT.PUT_LINE('Employee ' || employee_rec.employee_id ||
                                         ' worked ' || ROUND(v_total_minutes / 60, 1) || ' hours');
                END LOOP;
        END;
    END;
END;


BEGIN
    calculate_weekly_hours_all_employees(
            TO_TIMESTAMP('2025-09-28 00:00:00', 'YYYY-MM-DD HH24:MI:SS')
    );
END;


CREATE OR REPLACE PROCEDURE run_employee_weekly_hours_summary IS
    v_start_time TIMESTAMP;
    v_end_time   TIMESTAMP;
BEGIN
    v_start_time := TRUNC(SYSTIMESTAMP, 'IW') - 7;
    v_end_time := TRUNC(SYSTIMESTAMP, 'IW');

    calculate_weekly_hours_all_employees(v_start_time, v_end_time);
END;


BEGIN
    DBMS_SCHEDULER.CREATE_JOB(
            job_name => 'weekly_employee_hours_summary_job',
            job_type => 'STORED_PROCEDURE',
            job_action => 'run_employee_weekly_hours_summary',
            start_date => SYSTIMESTAMP,
            repeat_interval => 'FREQ=WEEKLY;BYDAY=MON;BYHOUR=0;BYMINUTE=0;BYSECOND=0',
            enabled => TRUE,
            comments => 'Runs weekly employee hour summary every Sunday at midnight'
    );
END;
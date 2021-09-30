DROP TABLE IF EXISTS major CASCADE;
DROP TABLE IF EXISTS student CASCADE;
DROP TABLE IF EXISTS project CASCADE;
--DROP SEQUENCE IF EXISTS major_id_seq;
--DROP SEQUENCE IF EXISTS student_id_seq;
--DROP SEQUENCE IF EXISTS project_id_seq;

-- CREATE SEQUENCE major_id_seq START WITH 1;
-- CREATE SEQUENCE student_id_seq START WITH 1;
-- CREATE SEQUENCE project_id_seq START WITH 1;


CREATE TABLE major (
    /*id                INTEGER NOT NULL default nextval('major_id_seq'), */
    id                BIGSERIAL NOT NULL,
    name              VARCHAR(30) not null unique,
    description       VARCHAR(150)
);

ALTER TABLE major ADD CONSTRAINT major_pk PRIMARY KEY ( id );

CREATE TABLE student (
    /*id              INTEGER NOT NULL default nextval('student_id_seq'),*/
    id              BIGSERIAL NOT NULL,
    login_name            VARCHAR(30) not null unique,
    password        VARCHAR(64),
    first_name      VARCHAR(30),
    last_name       VARCHAR(30),
    email           VARCHAR(50),
    address         VARCHAR(150),
    enrolled_date      date default CURRENT_DATE,
    major_id   bigint NOT NULL
);

ALTER TABLE student ADD CONSTRAINT student_pk PRIMARY KEY ( id );

CREATE TABLE project (
    /*id             INTEGER NOT NULL default nextval('project_id_seq'),*/
    id             BIGSERIAL NOT NULL,
    name   VARCHAR(30),
    description       VARCHAR(150),
    create_date    date default CURRENT_DATE
);

ALTER TABLE project ADD CONSTRAINT project_pk PRIMARY KEY ( id );

CREATE TABLE student_project (
    student_id    BIGINT NOT NULL,
    project_id    BIGINT NOT NULL
);

ALTER TABLE student
    ADD CONSTRAINT student_major_fk FOREIGN KEY ( major_id )
        REFERENCES major ( id );

ALTER TABLE student_project
    ADD CONSTRAINT student_fk FOREIGN KEY ( student_id )
        REFERENCES student ( id );

ALTER TABLE student_project
    ADD CONSTRAINT project_fk FOREIGN KEY ( project_id )
        REFERENCES project ( id );

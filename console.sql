drop table if exists qualifications;
drop table if exists experience;
drop table if exists documents;
drop table if exists job_post_activity;
drop table if exists assessment_results;
drop table if exists candidate;
drop table if exists job_post_assessment;
drop table if exists job_post;
drop table if exists requests;
drop table if exists candidate_emails;
drop table if exists candidate_account;
drop table if exists recruiter_emails;
drop table if exists recruiter;

-- Table: recruiter_account
CREATE TABLE recruiter
(
    id                        bigint       NOT NULL PRIMARY KEY,
    firstname                 varchar(100) NOT NULL,
    lastname                  varchar(100) NOT NULL,
    company_name              varchar(100) NOT NULL UNIQUE,
    website_link              varchar(255) NOT NULL UNIQUE,
    role                      varchar(100)  NOT NULL,
    cellphone                 varchar(30)  NOT NULL,
    email                     varchar(255) NOT NULL UNIQUE,
    password                  varchar(100) NOT NULL,
    email_notification_active bool,
    registration_date         timestamp,
    last_login_date           timestamp
);

-- Table: candidate_account
    CREATE TABLE candidate_account
(
    id                        bigint       NOT NULL PRIMARY KEY,
    email                     varchar(255) NOT NULL UNIQUE,
    password                  varchar(100) NOT NULL,
    email_notification_active bool         NOT NULL,
    registration_date         timestamp    NOT NULL,
    last_login_date           timestamp
);


-- Table: candidate
CREATE TABLE candidate
(
    id              bigint PRIMARY KEY,
    title           varchar(50)  NULL,
    first_name      varchar(50)  NULL,
    middle_name     varchar(50),
    last_name       varchar(50)  NULL,
    date_of_birth   varchar(50)  NULL,
    language        varchar(50)  NULL,
    id_number       varchar(13)  NULL,
    gender          varchar(6)   NULL,
    drivers_licence bool         NULL,
    race            varchar(20)  NULL,
    email           varchar(100) NULL,
    telephone       varchar(15)  NULL,
    cellphone       varchar(15)  NULL,
    street_address  varchar(150) NULL,
    suburb          varchar(45)  NULL,
    city            varchar(45)  NULL,
    province        varchar(25)  NULL,
    country         varchar(25)  NULL,
    post_code       varchar(5)   NULL,
    foreign key (email) references candidate_account (email) on DELETE NO ACTION
);


-- Table: qualifications
CREATE TABLE qualifications
(
    id                 bigint PRIMARY KEY,
    institution_name   varchar(100) NULL,
    qualification_name varchar(100) NULL,
    major              varchar(50),
    start_date         date         NULL,
    completion_date    date         NULL,
    aggregate          float8       NULL,
    candidate_email    varchar(100) NOT NULL,
    FOREIGN KEY (candidate_email) REFERENCES candidate_account (email) ON DELETE NO ACTION
);

-- Table: experience
CREATE TABLE experience
(
    id              bigint PRIMARY KEY,
    is_current_job  bool          NULL,
    job_title       varchar(100)  NULL,
    description     varchar(4000) NULL,
    company_name    varchar(100)  NULL,
    start_date      date          NULL,
    end_date        date          NULL,
    notice_period   varchar(45)   NULL,
    candidate_email varchar(100)  NOT NULL,
    FOREIGN KEY (candidate_email) REFERENCES candidate_account (email) ON DELETE NO ACTION
);

-- Table: documents
CREATE TABLE documents
(
    id              bigint PRIMARY KEY,
    name            varchar(255) NOT NULL UNIQUE,
    title           varchar(20)  NOT NULL,
    size            bigint       NOT NULL,
    content         bytea        NOT NULL,
    candidate_email varchar(100) NOT NULL,
    FOREIGN KEY (candidate_email) REFERENCES candidate_account (email) ON DELETE NO ACTION
);

-- Table: job_post
CREATE TABLE job_post
(
    id                     bigint PRIMARY KEY,
    title                  varchar(100)  NOT NULL,
    type                   varchar(50)   NOT NULL,
    purpose                varchar(5000),
    description            varchar(5000) NOT NULL,
    qualification          varchar(1500) NOT NULL,
    requirements           varchar(5000) NOT NULL,
    responsibilities       varchar(5000) NOT NULL,
    location               varchar(255)  NOT NULL,
    created_date           date          NOT NULL,
    closing_date           timestamp     NOT NULL,
    is_assessment_required bool          NOT NULL,
    recruiter_id           bigint,
    FOREIGN KEY (recruiter_id) REFERENCES recruiter (id) ON DELETE NO ACTION
);

-- Table: job_post_activity
CREATE TABLE job_post_activity
(
    id           bigint PRIMARY KEY,
    job_post_id  bigint      NOT NULL,
    candidate_id bigint      NOT NULL,
    apply_date   date        NOT NULL,
    status       varchar(25) NOT NULL,
    FOREIGN KEY (job_post_id) REFERENCES job_post (id) ON DELETE NO ACTION,
    FOREIGN KEY (candidate_id) REFERENCES candidate (id) ON DELETE NO ACTION
);

-- Table: job_post_assessment
CREATE TABLE job_post_assessment
(
    id              bigint PRIMARY KEY,
    assessment_link varchar(255) NOT NULL,
    job_post_id     bigint       NOT NULL,
    FOREIGN KEY (job_post_id) REFERENCES job_post (id) ON DELETE NO ACTION
);

-- Table: assessment_results
CREATE TABLE assessment_results
(
    id                     bigint PRIMARY KEY,
    results                float8 NOT NULL,
    job_post_assessment_id bigint NOT NULL,
    candidate_id           bigint NOT NULL,
    FOREIGN KEY (job_post_assessment_id) REFERENCES job_post_assessment (id) ON DELETE NO ACTION,
    FOREIGN KEY (candidate_id) REFERENCES candidate (id)
);

CREATE TABLE requests
(
  id bigint primary key,
  document_title varchar(20) not null,
  status varchar(25) not null,
  candidate_email varchar(100) not null,
  recruiter_email varchar(100) not null,
  foreign key (candidate_email) references candidate_account (email) on delete no action,
  foreign key (recruiter_email) references recruiter (email) on delete no action
);

CREATE TABLE candidate_emails
(
    id bigint primary key,
    subject varchar(255) not null,
    sender_email varchar(100) not null,
    receiver_email varchar(100) not null,
    message varchar(5000) not null,
    time_sent timestamp not null,
    foreign key (receiver_email) references candidate_account(email) on DELETE no action
);

CREATE TABLE recruiter_emails
(
    id bigint primary key,
    subject varchar(255) not null,
    sender_email varchar(100) not null,
    receiver_email varchar(100) not null,
    message varchar(5000) not null,
    time_sent timestamp not null,
    foreign key (receiver_email) references recruiter(email) on DELETE no action
);

insert into recruiter (id, firstname, lastname, company_name, website_link, role, cellphone, email, password,
                       email_notification_active, registration_date)
values (1, 'Bowie', 'Feeney', 'Kamba', 'http://sun.com', 'Nurse', '+63 889 540 3710', 'bfeeney0@columbia.edu',
        'k4T0OTJ', true, '2021-11-14 20:06:30.000000');
insert into recruiter (id, firstname, lastname, company_name, website_link, role, cellphone, email, password,
                       email_notification_active, registration_date)
values (2, 'Chrotoem', 'Baumer', 'Trudoo', 'http://flickr.com', 'Human Resources Manager', '+7 740 531 4507',
        'cbaumer1@cnet.com', 'up9snhz', false, '2021-11-14 20:06:30.000000');
insert into recruiter (id, firstname, lastname, company_name, website_link, role, cellphone, email, password,
                       email_notification_active, registration_date)
values (3, 'Sean', 'Howat', 'Topicblab', 'https://is.gd', 'Chemical Engineer', '+7 959 863 7309',
        'showat2@clickbank.net', 'MtHq4v6511', true, '2021-11-14 20:06:30.000000');
insert into recruiter (id, firstname, lastname, company_name, website_link, role, cellphone, email, password,
                       email_notification_active, registration_date)
values (4, 'Dodi', 'Lezemore', 'Jabberstorm', 'https://sina.com.cn', 'Staff Scientist', '+86 431 138 6521',
        'dlezemore3@toplist.cz', 'LMGHQYya', false,'2021-11-14 20:06:30.000000');
insert into recruiter (id, firstname, lastname, company_name, website_link, role, cellphone, email, password,
                       email_notification_active, registration_date)
values (5, 'Smith', 'Burnapp', 'Brainbox', 'http://mit.edu', 'Senior Cost Accountant', '+63 345 871 8297',
        'sburnapp4@indiegogo.com', 'BTRQg5', true,'2021-11-14 20:06:30.000000');



insert into candidate_account (id, email, password, email_notification_active, registration_date, last_login_date)
values (1, 'jbeyer0@quantcast.com', 'FlTXMK6yd', false, '2021-11-10 14:53:52', '2021-11-07 23:59:14');
insert into candidate_account (id, email, password, email_notification_active, registration_date, last_login_date)
values (2, 'nkyberd1@github.com', 'w34c5pYHP09', true, '2021-11-07 23:59:14', '2021-11-07 23:59:14');
insert into candidate_account (id, email, password, email_notification_active, registration_date, last_login_date)
values (3, 'feaken4@tiny.cc', 'YeOlIje', false, '2021-11-13 15:10:53', '2021-11-07 23:59:14');
insert into candidate_account (id, email, password, email_notification_active, registration_date, last_login_date)
values (4, 'bshrimpling2@flavors.me', 'euyx1n', false, '2021-11-06 19:38:33', '2021-11-07 23:59:14');
insert into candidate_account (id, email, password, email_notification_active, registration_date, last_login_date)
values (5, 'mpendre3@msu.edu', 'w7uEpvqciPR', true, '2021-11-04 21:05:08', '2021-11-07 23:59:14');

insert into candidate (id, title, first_name, middle_name, last_name, date_of_birth, language, id_number, gender,
                       drivers_licence, race, email, telephone, cellphone, street_address, suburb, city, province,
                       country, post_code)
values (1, 'Ms', 'Bonnibelle', 'Jacky', 'Beyer', '2002-10-23', 'Tsonga', '0257705724', 'Male', false, 'Black African',
        'jbeyer0@quantcast.com', '202 375 5064', '209 522 0958', '5 Waubesa Crossing', 'Pretoria East', 'Carletonville',
        'Western Cape', 'United States', '20409');
insert into candidate (id, title, first_name, middle_name, last_name, date_of_birth, language, id_number, gender,
                       drivers_licence, race, email, telephone, cellphone, street_address, suburb, city, province,
                       country, post_code)
values (2, 'Mrs', 'Leodora', 'Neville', 'Kyberd', '1998-09-22', 'Tswana', '8756460635', 'Female', true, 'Black African',
        'nkyberd1@github.com', '816 526 2142', '148 500 4619', '5 Summer Ridge Center', 'Soshanguve', 'Randburg',
        'Limpopo', 'United States', '64504');
insert into candidate (id, title, first_name, middle_name, last_name, date_of_birth, language, id_number, gender,
                       drivers_licence, race, email, telephone, cellphone, street_address, suburb, city, province,
                       country, post_code)
values (3, 'Mrs', 'Prescott', 'Benjamen', 'Shrimpling', '2002-12-19', 'Xhosa', '9553362184', 'Male', false, 'White',
        'bshrimpling2@flavors.me', '414 245 0980', '286 295 3139', '0 Straubel Hill', 'Pretoria North', 'Springs',
        'Free State', 'United States', '53225');
insert into candidate(id, title, first_name, middle_name, last_name, date_of_birth, language, id_number, gender,
                      drivers_licence, race, email, telephone, cellphone, street_address, suburb, city, province,
                      country, post_code)
values (4, 'Ms', 'Nap', 'Melvin', 'Pendre', '1999-12-27', 'Sotho', '6224249652', 'Male', false, 'Asian',
        'mpendre3@msu.edu', '206 979 6014', '697 526 9623', '0 Dennis Hill', 'Temba', 'Randburg', 'Gauteng',
        'United States', '98185');
insert into candidate (id, title, first_name, middle_name, last_name, date_of_birth, language, id_number, gender,
                       drivers_licence, race, email, telephone, cellphone, street_address, suburb, city, province,
                       country, post_code)
values (5, 'Rev', 'Ray', 'Fanya', 'Eaken', '2002-07-31', 'Venda', '4066987427', 'Male', true, 'Asian',
        'feaken4@tiny.cc',
        '314 860 6130', '236 232 0154', '27 Blaine Circle', 'Brooklyn', 'Carletonville', 'KwaZulu-Natal',
        'United States', '63143');


INSERT INTO job_post (id, title, type, purpose, description, qualification, requirements, responsibilities,
                      location, created_date, closing_date, is_assessment_required, recruiter_id)
VALUES (1, 'Web Designer - Internship', '12 Months Contract',
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Convallis a cras semper auctor neque. Purus semper eget duis at tellus at urna. Nunc lobortis mattis aliquam faucibus. Massa tincidunt nunc pulvinar sapien et ligula. Fames ac turpis egestas sed tempus urna et. Et netus et malesuada fames ac turpis egestas. Felis bibendum ut tristique et egestas. Fermentum iaculis eu non diam phasellus. Et ligula ullamcorper malesuada proin libero nunc consequat interdum. Erat velit scelerisque in dictum.',
        'Commodo quis imperdiet massa tincidunt nunc pulvinar sapien et. Enim nulla aliquet porttitor lacus luctus. Scelerisque varius morbi enim nunc faucibus a pellentesque sit. Sed lectus vestibulum mattis ullamcorper velit sed ullamcorper. Tempor orci dapibus ultrices in. Sollicitudin ac orci phasellus egestas tellus. Ultricies lacus sed turpis tincidunt id. Varius quam quisque id diam vel quam elementum. Sed ullamcorper morbi tincidunt ornare massa eget egestas. Proin nibh nisl condimentum id venenatis a condimentum vitae sapien. Quis viverra nibh cras pulvinar mattis nunc sed. A iaculis at erat pellentesque adipiscing commodo elit at. Id faucibus nisl tincidunt eget nullam non nisi. Turpis massa sed elementum tempus egestas sed sed. Porttitor leo a diam sollicitudin tempor id eu nisl. Aenean sed adipiscing diam donec adipiscing. Sed risus ultricies tristique nulla aliquet enim tortor at auctor. Arcu odio ut sem nulla pharetra diam sit amet. Pellentesque eu tincidunt tortor aliquam.',
        'Commerce (Accounting, Business Management, Finance, Economics etc,Actuarial Science,Maths,Statistics,Computer Science,Information Technology/Systems,Engineering',
        'Be in their final year BSc (Any Field) or BEng (Any Field) Graduates or Postgraduates who wish to pursue an application development career.,At least have a Level 5 or C or 60% for Pure Mathematics and English in their final matric results.,Have matriculated in the last 8 years.,Be willing to relocate to study and stay in the Gauteng Province at their own expense.,Be willing to undergo screening assessments in Gauteng at their own expense.,Have a SA Citizenship, must have no bad credit rating and no criminal record.',
        'Deliver exceptional service that exceeds customers’ expectations through proactive, innovative and appropriate solutions.,Provision of an efficient execution of role responsibilities through careful and timeous planning, reporting and updating of all related information in the relevant role of learning.,Participate in the innovation process in the business and contribute toward new innovations against objectives.,Participation in knowledge sharing and projects to maximise skills required for the role and relevant career.,Participation in knowledge sharing and projects to maximise skills required for the role and relevant career exploration by executing all activities required to be efficient and successful.,Manage graduate development plan by completion of training assignments.,Manage graduate development plan to increase own competencies and skills by attendance of training.,Manage own development to increase own competencies.,Live the values of the business through demonstrating and displaying of the values required in the business.',
        'Pretoria, Johannesburg and Durban', '2021-12-11', '2021-12-31 23:59:00', false, 1);
INSERT INTO job_post (id, title, type, purpose, description, qualification, requirements, responsibilities,
                      location, created_date, closing_date, is_assessment_required, recruiter_id)
VALUES (2, 'Data Engineer', 'Permanent',
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Convallis a cras semper auctor neque. Purus semper eget duis at tellus at urna. Nunc lobortis mattis aliquam faucibus. Massa tincidunt nunc pulvinar sapien et ligula. Fames ac turpis egestas sed tempus urna et. Et netus et malesuada fames ac turpis egestas. Felis bibendum ut tristique et egestas. Fermentum iaculis eu non diam phasellus. Et ligula ullamcorper malesuada proin libero nunc consequat interdum. Erat velit scelerisque in dictum.',
        'Commodo quis imperdiet massa tincidunt nunc pulvinar sapien et. Enim nulla aliquet porttitor lacus luctus. Scelerisque varius morbi enim nunc faucibus a pellentesque sit. Sed lectus vestibulum mattis ullamcorper velit sed ullamcorper. Tempor orci dapibus ultrices in. Sollicitudin ac orci phasellus egestas tellus. Ultricies lacus sed turpis tincidunt id. Varius quam quisque id diam vel quam elementum. Sed ullamcorper morbi tincidunt ornare massa eget egestas. Proin nibh nisl condimentum id venenatis a condimentum vitae sapien. Quis viverra nibh cras pulvinar mattis nunc sed. A iaculis at erat pellentesque adipiscing commodo elit at. Id faucibus nisl tincidunt eget nullam non nisi. Turpis massa sed elementum tempus egestas sed sed. Porttitor leo a diam sollicitudin tempor id eu nisl. Aenean sed adipiscing diam donec adipiscing. Sed risus ultricies tristique nulla aliquet enim tortor at auctor. Arcu odio ut sem nulla pharetra diam sit amet. Pellentesque eu tincidunt tortor aliquam.',
        'Computer Science,Information Technology/Systems, Data Science',
        'Have BSc (Any Field) or BEng (Any Field) Graduates or Postgraduates.,At least have a Level 5 or C or 60% for Pure Mathematics and English in their final matric results.,Have matriculated in the last 8 years.,Be willing to relocate to study and stay in the Gauteng Province at their own expense.,Be willing to undergo screening assessments in Gauteng at their own expense.,Have a SA Citizenship, must have no bad credit rating and no criminal record.',
        'Deliver exceptional service that exceeds customers’ expectations through proactive, innovative and appropriate solutions.,Provision of an efficient execution of role responsibilities through careful and timeous planning, reporting and updating of all related information in the relevant role of learning.,Participate in the innovation process in the business and contribute toward new innovations against objectives.,Participation in knowledge sharing and projects to maximise skills required for the role and relevant career.,Participation in knowledge sharing and projects to maximise skills required for the role and relevant career exploration by executing all activities required to be efficient and successful.,Manage graduate development plan by completion of training assignments.,Manage graduate development plan to increase own competencies and skills by attendance of training.,Manage own development to increase own competencies.,Live the values of the business through demonstrating and displaying of the values required in the business.',
        'Pretoria', '2021-12-11', '2022-01-07 23:59:00', false, 1);



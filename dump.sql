/* DDL */
/*****  *****/
create table if not exists users
(
    id         serial  not null
        constraint users_pk
            primary key,
    first_name varchar,
    last_name  varchar,
    email      varchar not null,
    pass       varchar
);

create unique index if not exists users_email_uindex
    on users (email);

create table if not exists city
(
    id   serial  not null
        constraint city_pk
            primary key,
    name varchar not null
);

create unique index if not exists city_id_uindex
    on city (id);

create unique index if not exists city_name_uindex
    on city (name);

create table if not exists answer
(
    id        serial  not null
        constraint answers_pk
            primary key,
    user_id   integer,
    option_id integer not null,
    city_id   integer,
    date      timestamp default now()
);

create unique index if not exists answers_id_uindex
    on answer (id);

create table if not exists question
(
    id               serial not null
        constraint question_pk
            primary key,
    question         varchar,
    sub_note         varchar,
    quiz_id          integer,
    background_image varchar,
    slide_order      integer
);

create unique index if not exists question_id_uindex
    on question (id);

create unique index if not exists question_slide_order_uindex
    on question (slide_order);

create table if not exists option
(
    id          serial  not null
        constraint option_pk
            primary key,
    name        varchar not null,
    question_id integer not null,
    sentiment   integer
);

comment on column option.sentiment is 'defines the weighted sentiment attached to the option
i.e FALSE = 0;
TRUE and NOT SURE = 1';

create table if not exists setting
(
    name        varchar not null,
    value       varchar not null,
    description varchar
);

create unique index if not exists settings_name_uindex
    on setting (name);

create table if not exists quiz
(
    id     serial not null
        constraint quiz_pk
            primary key,
    "desc" varchar,
    status integer default 1
);

comment on column quiz.status is 'status of the quiz: ACTIVE = 1; INACTIVE = 2';

create unique index if not exists quiz_id_uindex
    on quiz (id);

create table if not exists address
(
    id       serial  not null
        constraint address_pk
            primary key,
    location varchar,
    city_id  integer not null
);

/* DML */
INSERT INTO public.address (id, location, city_id) VALUES (1, 'Culver City Campus West Los Angeles College, 9000 Overland, Ave., Culver City, CA 90230', 1);
INSERT INTO public.address (id, location, city_id) VALUES (2, 'Central Piedmont Community College, 1141 Elizabeth Ave., Charlotte, NC 28204', 3);
INSERT INTO public.address (id, location, city_id) VALUES (3, 'Wall Street Campus, 85 Broad St., New York, NY, 10004', 2);
INSERT INTO public.address (id, location, city_id) VALUES (4, 'Borough of Manhattan Community College Campus in TriBeCa, 70 Murray St., New York, NY, 10007', 2);
INSERT INTO public.address (id, location, city_id) VALUES (5, 'Hudson County Community College Campus, 168 Sip Ave, New Jersey, NY, 07306', 2);


INSERT INTO public.city (id, name) VALUES (1, 'Los Angeles');
INSERT INTO public.city (id, name) VALUES (2, 'New York');
INSERT INTO public.city (id, name) VALUES (3, 'Charlotte');


INSERT INTO public.option (id, name, question_id, sentiment) VALUES (1, 'Select the CITY closest to you', 1, null);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (2, 'START', 2, null);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (32, 'Not sure', 12, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (13, 'NO, not for me', 6, 0);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (12, 'YES, I’m ready to learn', 6, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (15, 'YES, I’m ready to learn', 7, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (14, 'Not sure', 6, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (9, 'YES, that sounds fun', 5, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (8, 'Not sure', 4, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (11, 'Not sure', 5, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (16, 'NO, not for me', 7, 0);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (5, 'Not sure', 3, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (3, 'YES', 3, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (29, 'Not sure', 11, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (30, 'Yes, that can work for me', 12, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (24, 'YES, that sounds helpful', 10, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (27, 'YES, I can make that work', 11, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (26, 'Not sure', 10, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (21, 'Yes, I like how that sounds', 9, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (20, 'Not sure', 8, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (23, 'Not sure', 9, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (17, 'Not sure', 7, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (18, 'Yes, I like how that sounds', 8, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (10, 'NO thanks', 5, 0);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (4, 'NO', 3, 0);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (28, 'NO, not possible', 11, 0);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (25, 'NO, not for me', 10, 0);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (6, 'Yes, I like how that sounds', 4, 1);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (7, 'Nope, not me', 4, 0);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (22, 'Nope, not me', 9, 0);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (19, 'Nope, not me', 8, 0);
INSERT INTO public.option (id, name, question_id, sentiment) VALUES (31, 'Nope, not me', 12, 0);


INSERT INTO public.question (id, question, sub_note, quiz_id, background_image, slide_order) VALUES (13, 'Before we get started, what is your e-mail address?', 'Don’t worry, we won’t send you spam. This is just to make sure you’re a real person!', 1, 'emailGathering.jpg', 3);
INSERT INTO public.question (id, question, sub_note, quiz_id, background_image, slide_order) VALUES (4, 'Are you willing to step outside of your comfort zone?', null, 1, 'stepOutside.jpg', 5);
INSERT INTO public.question (id, question, sub_note, quiz_id, background_image, slide_order) VALUES (5, 'You’ll learn practical skills, like workplace communication, problem solving, and time management.', null, 1, 'learnPracticalSkills.jpg', 6);
INSERT INTO public.question (id, question, sub_note, quiz_id, background_image, slide_order) VALUES (6, 'You’ll be in class at Year Up 5 days a week for 6 months.', '30+ hours / week Monday to Friday 8:30am - 3:30pm
', 1, 'yearUpClass.jpg', 7);
INSERT INTO public.question (id, question, sub_note, quiz_id, background_image, slide_order) VALUES (10, 'You’ll be learning with the support of a Year Up community of your peers, professional coaches, mentors, and social workers.', null, 1, 'learningWithSupport.jpg', 11);
INSERT INTO public.question (id, question, sub_note, quiz_id, background_image, slide_order) VALUES (8, 'After those 6 months of training, you’ll be ready for a corporate internship.', 'Fields include: Information Technology Financial Operations Business Operations Quality Assurance Software Development', 1, 'corporateInternship.jpg', 9);
INSERT INTO public.question (id, question, sub_note, quiz_id, background_image, slide_order) VALUES (9, '6 months of successful career training leads to a 6 month corporate internship', '40+ hours / week Monday to Friday 9:00am to 5:00pm', 1, 'successfulCareer.jpg', 10);
INSERT INTO public.question (id, question, sub_note, quiz_id, background_image, slide_order) VALUES (7, 'You’ll be expected to arrive on time and professionally dressed, every weekday.', null, 1, 'beExpected.jpg', 8);
INSERT INTO public.question (id, question, sub_note, quiz_id, background_image, slide_order) VALUES (11, 'You’ll earn a stipend, but you’ll need to rely on savings, family support, and evening/weekend jobs.', 'Training: up to $150/week Internship: up to $250/week
', 1, 'earnStipend.jpg', 12);
INSERT INTO public.question (id, question, sub_note, quiz_id, background_image, slide_order) VALUES (12, 'You’ll commute to ', 'You can get there on public transit', 1, null, 13);
INSERT INTO public.question (id, question, sub_note, quiz_id, background_image, slide_order) VALUES (1, 'If you’ve made it here, you’re probably wondering whether Year Up is a good fit for you!', null, 1, 'firstSlide.jpg', 1);
INSERT INTO public.question (id, question, sub_note, quiz_id, background_image, slide_order) VALUES (2, 'Find out if Year Up might be right for you In 3 minutes by responding to questions about the program.', null, 1, 'oneButtonSlide.jpg', 2);
INSERT INTO public.question (id, question, sub_note, quiz_id, background_image, slide_order) VALUES (3, 'Are you looking to start, advance, or change your career?', null, 1, 'lookingToStart.jpg', 4);


INSERT INTO public.quiz (id, "desc", status) VALUES (1, 'YU DS Program ', 1);
INSERT INTO public.quiz (id, "desc", status) VALUES (2, 'YU DS Program 2', 2);


INSERT INTO public.setting (name, value, description) VALUES ('VIDEO', 'https://www.youtube.com/watch?v=D43z7kYi55I', 'URL to video to display on the last slide');
INSERT INTO public.setting (name, value, description) VALUES ('RATIO', '60', '%age cutoff of answer sentiments to be deems successful');



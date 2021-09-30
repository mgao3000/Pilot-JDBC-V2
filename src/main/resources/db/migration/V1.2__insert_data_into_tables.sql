insert into major (name, description) values
('Arts', 'Arts Description'),
('Business', 'Business Description'),
('Education', 'Education Description'),
('Health', 'Health Description'),
('Science', 'Science Description'),
('Technology', 'Technology Description'),
('Math', 'Math Description')
;
commit;

insert into student (login_name, password, first_name, last_name, email, address, major_id) values
('dsmith', '123456', 'David', 'Smith', 'david.smith@devmountain.com', '999 Washington Ave, Fairfax, VA 22030', 2),
('fjohnson', '123456', 'Frank', 'Johnson', 'frank.johnson@devmountain.com', '998 fish road, Fairfax, VA 22030', 2),
('edavis', '123456', 'Emily', 'Davis', 'emily.davis@devmountain.com', '997 well pl, Fairfax, VA 22030', 1),
('dmiller', '123456', 'Dave', 'Miller', 'dave.miller@devmountain.com', '996 Washington Ave, Fairfax, VA 22030', 3),
('tbrown', '123456', 'Thomas', 'Brown', 'thomas.brown@devmountain.com', '995 Washington Ave, Fairfax, VA 22030', 3),
('jwilson', '123456', 'Jones', 'Wilson', 'jones.wilson@devmountain.com', '994 Washington Ave, Fairfax, VA 22030', 4),
('aclinton', '123456', 'Anderson', 'Clinton', 'anderson.clinton@devmountain.com', '993 Washington Ave, Fairfax, VA 22030', 3),
('jyoung', '123456', 'Jackson', 'Young', 'jackson.young@devmountain.com', '992 Washington Ave, Fairfax, VA 22030', 4),
('wcook', '123456', 'White', 'Cook', 'white.cook@devmountain.com', '991 Washington Ave, Fairfax, VA 22030', 5),
('hscott', '123456', 'Henry', 'Scott', 'henry.scott@devmountain.com', '990 Washington Ave, Fairfax, VA 22030', 6),
('hcooper', '123456', 'Harris', 'Cooper', 'harris.cooper@devmountain.com', '989 Washington Ave, Fairfax, VA 22030', 7),
('mross', '123456', 'Martin', 'Ross', 'martin.ross@devmountain.com', '988 Washington Ave, Fairfax, VA 22030', 7),
('cwood', '123456', 'Clark', 'Wood', 'clark.wood@devmountain.com', '987 Washington Ave, Fairfax, VA 22030', 5),
('lpowell', '123456', 'Lee', 'Powell', 'lee.powell@devmountain.com', '986 Washington Ave, Fairfax, VA 22030', 7)
;
commit;

insert into project (name, description) values
('project100', 'project100 description'),
('project101', 'project101 description'),
('project102', 'project102 description'),
('project103', 'project103 description'),
('project104', 'project104 description'),
('project105', 'project105 description'),
('project106', 'project106 description'),
('project107', 'project107 description'),
('project108', 'project108 description'),
('project109', 'project109 description')
;
commit;

insert into student_project values
(1, 1),
(1, 1),
(1, 1),
(2, 1),
(2, 2),
(3, 1),
(3, 5),
(4, 3),
(4, 7),
(5, 3),
(5, 9),
(6, 1),
(6, 8),
(7, 10),
(8, 2),
(8, 3),
(8, 6),
(8, 1),
(9, 3),
(9, 2),
(9, 1),
(9, 7),
(10, 2),
(10, 10),
(10, 7),
(10, 7)
;
commit;
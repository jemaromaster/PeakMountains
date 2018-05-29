--this data is insert to initialize the database with privileges data

insert into privilege (name) values ('worker')
insert into privilege (name) values ('manager')


insert into userpv (password,username, email) values ('pass','admin', 'admin@peakvalidation.com')
insert into userpv_privileges (userpv_id,privilege_id) values (1,1)
insert into userpv_privileges (userpv_id,privilege_id) values (1,2)

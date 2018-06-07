--this data is insert to initialize the database with privileges data

insert into privilege (name) values ('worker')
insert into privilege (name) values ('manager')

-- the admin is the encoded form of "pass" with spring encoder
insert into userpv (password,username, email) values ('$2a$11$I/8vhuYXeIlHerg4ef3q4egPnvfGdVNGHdr.PLSvIDc7SbGs.Oa7O','admin', 'admin@peakvalidation.com')
insert into userpv_privileges (userpv_id,privilege_id) values (1,1)
insert into userpv_privileges (userpv_id,privilege_id) values (1,2)

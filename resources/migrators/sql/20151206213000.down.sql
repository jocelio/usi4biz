alter table issue drop column description;
alter table user_account drop column person;
alter table person add user_account char(32) null;

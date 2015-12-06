alter table issue add description text null;
alter table user_account add person char(32) null;
alter table person drop column user_account;

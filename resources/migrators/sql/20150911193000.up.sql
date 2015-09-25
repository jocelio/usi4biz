create table user_account (
  id        char(32)     not null,
  username  varchar(100) not null
) engine = innodb;

alter table user_account add constraint pk_user_account primary key (id);
create unique index idx_username on user_account (username);

create table person (
  id           char(32)     not null,
  first_name   varchar(50)  not null,
  last_name    varchar(50)  not null,
  email        varchar(100) not null,
  user_account char(32)         null
) engine = innodb;

alter table person add constraint pk_person primary key (id);
create unique index idx_person_email on person (email);
create unique index idx_user_account_person on person (user_account);
alter table person add constraint fk_user_account_person foreign key (user_account) references user_account (id) on delete cascade;

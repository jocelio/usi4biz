create table user_account (
  id        char(32)     not null,
  email     varchar(100) not null,
  name      varchar(100) not null,
  github_id varchar(30)      null
) engine = innodb;

alter table user_account add constraint pk_user_account primary key (id);

create table issue_state (
  id          char(32)     not null,
  issue       char(32)     not null,
  state       varchar(20)  not null,
  set_date    timestamp    not null
) engine = innodb;

alter table issue_state add constraint pk_issue_state primary key (id);
alter table issue_state add constraint fk_issue_state foreign key (issue) references issue (id) on delete cascade;

create table product (
  id          char(32)     not null,
  name        varchar(100) not null,
  description text             null,
  acronym     varchar(10)      null
) engine = innodb;

alter table product add constraint pk_product primary key (id);

create table milestone (
  id          char(32)     not null,
  product     char(32)     not null,
  name        varchar(100)     null,
  description text             null,
  due_date    date             null
) engine = innodb;

alter table milestone add constraint pk_milestone primary key (id);
alter table milestone add constraint fk_product_milestone foreign key (product) references milestone (id) on delete cascade;

create table issue (
  id          char(32)     not null,
  product     char(32)     not null,
  name        varchar(255) not null,
  milestone   char(32)         null,
  description text             null,
  assignee    char(32)         null,
  effort      varchar(20)      null,
  priority    varchar(20)      null
) engine = innodb;

alter table issue add constraint pk_issue primary key (id);
alter table issue add constraint fk_assignee_issue foreign key (assignee) references person (id) on delete set null;
alter table issue add constraint fk_product_issue foreign key (product) references product (id) on delete cascade;
alter table issue add constraint fk_milestone_issue foreign key (milestone) references milestone (id) on delete set null;

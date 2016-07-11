create table repository (
  id       char(32)     not null,
  product  char(32)     not null,
  user     varchar(30)  not null,
  name     varchar(30)  not null
) engine = innodb;

alter table repository add constraint pk_repository primary key (id);
alter table repository add constraint fk_product_repository foreign key (product) references product (id) on delete cascade;

create table team (
  id      char(32) not null,
  person  char(32) not null,
  product char(32) not null
) engine = innodb;

alter table team add constraint pk_team primary key (id);
alter table team add constraint fk_person_team foreign key (person) references person (id) on delete cascade;
alter table team add constraint fk_product_team foreign key (product) references product (id) on delete cascade;

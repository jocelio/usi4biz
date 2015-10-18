create unique index idx_issue_state on issue_state (issue, state);

alter table issue add assigning_type varchar(20) null;

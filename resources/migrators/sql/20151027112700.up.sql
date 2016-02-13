create unique index idx_issue_reference on issue (reference);

alter table issue drop column effort;

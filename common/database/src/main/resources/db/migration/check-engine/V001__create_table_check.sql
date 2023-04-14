create table ce_check (
    id serial unique,
    key varchar(50) not null,
    site_id varchar(36) not null,
    page_url varchar(1000) not null,
    start_date timestamp not null,
    end_date timestamp,
    execution_status varchar(50) not null,
    result_status varchar(50),
    primary key (id)
);

create table ce_check_fix (
  id serial unique,
  check_id bigint not null,
  fix varchar(1000),
  primary key (id),
  constraint check_fix_fk foreign key (check_id) references ce_check(id) on delete cascade
);

create index ce_check_index on ce_check (
  site_id,
  page_url,
  execution_status,
  end_date
);


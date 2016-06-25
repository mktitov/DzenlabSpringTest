create table if not exists jdbc_users  (
    id long primary key auto_increment,
    email varchar(128) not null unique,
    pwd varchar(128) not null,
    token varchar(128),
    token_expiration_date timestamp
);

create table if not exists jdbc_token_audit  (
    id long primary key auto_increment,
    user_id long not null references jdbc_users(id) on delete cascade,
    token varchar(128),
    expiration_date timestamp
);

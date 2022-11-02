create table day_data(
                         day_id int auto_increment,
                         date date,
                         weekday int,
                         temp decimal(4,2),
                         rain decimal(15,2),
                         wind decimal(15,2),
                         holiday boolean default false,
                         holiday_name varchar(255),
                         historic boolean default false,
                         created_at timestamp default current_timestamp,
                         primary key(day_id)
);

create table blockfabrik_datapoint(
                                      datapoint_id int auto_increment,
                                      day int,
                                      hour int,
                                      minute int,
                                      value int,
                                      created_at timestamp default current_timestamp,
                                      primary key(datapoint_id),
                                      foreign key (day) references day_data(day_id)
);
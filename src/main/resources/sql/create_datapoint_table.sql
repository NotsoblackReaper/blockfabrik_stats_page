create table datapoint(
    datapoint_id int auto_increment,
    datapoint_act int,
    datapoint_max int,
    datapoint_date date,
    datapoint_day int,
    datapoint_hour int,
    datapoint_minute int,
    created_at timestamp default current_timestamp,
    primary key(datapoint_id)
);
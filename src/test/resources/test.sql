create table car_seller
(
id varchar(32),
sellername varchar(32),
mobile varchar(32),
carType varchar(128),
city varchar(32),
flag int default 0
) default charset=utf8 default collate=utf8_bin;

insert into car_seller values('111', '����', '13912384200', 'bmw x1', '����', 0);
insert into car_seller values('112', '��ѧ��', '13912384201', 'bmw x1', '��ɳ', 0);
insert into car_seller values('113', '��ѧ��', '13912384202', 'bmw x1', '��ɳ', 0);
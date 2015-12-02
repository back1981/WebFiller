create table car_seller
(
id varchar(32),
sellername varchar(32),
mobile varchar(32),
carType varchar(128),
carbrand varchar(128),
carseries varchar(128),
city varchar(32),
flag int default 0
) default charset=utf8 default collate=utf8_bin;

insert into car_seller values('111', '黎明', '13912384231', 'bmw x1', '宝马', '530', '北京', 0);
insert into car_seller values('112', '张学友', '13912384221', 'bmw x1','宝马', '530', '长沙', 0);
insert into car_seller values('113', '张学友', '13912384222', 'bmw x1', '宝马', '530','长沙', 0);
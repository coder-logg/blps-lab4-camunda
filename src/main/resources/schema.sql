create table if not exists _user(
	id serial not null primary key,
	username varchar(30) not null unique,
	password text not null,
	email varchar(50) unique
);

create table if not exists customer(
	id serial not null primary key references _user(id),
	first_name varchar(50) not null,
	last_name varchar(70) not null,
	delivery_address text
);

create table if not exists company(
	id serial not null primary key references _user(id),
	name varchar(128) not null unique,
	description text
);

create table if not exists category(
	id serial primary key,
	name varchar(100) not null unique,
	description text
);

create table if not exists device(
	id serial not null primary key,
	name varchar(50) not null,
	category_id int not null references category(id),
	description text,
	price int not null,
	company_id int not null references company(id),
	unique (name, company_id)
);

create table if not exists basket(
	id serial not null primary key,
	customer_id int not null references customer(id) unique
);

create table if not exists devices_in_basket(
	id serial not null primary key,
	basket_id int references basket(id),
	device_id int references device(id),
	amount int not null default 1,
	unique (basket_id, device_id)
);

create table if not exists devices_in_transaction(
	id serial not null primary key,
	transaction_id int references transaction(id),
	device_id int references device(id),
	amount int not null default 1,
	unique (transaction_id, device_id)
);

create table if not exists transaction(
	id serial not null primary key,
	customer_id int not null references _user(id),
	company_id int not null references company(id),
	discount int check ( discount <= 100 and discount >= 0 ) default 0,
	total_price int not null,
	date_time timestamp not null default current_timestamp
);
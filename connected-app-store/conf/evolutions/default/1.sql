# --- !Ups

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS category (
id UUID primary key DEFAULT uuid_generate_v4(),
category_name varchar(50) unique);

CREATE TABLE IF NOT EXISTS applicationname (
app_name varchar(50) primary key,
app_category UUID references category(id),
description varchar(200),
rating INTEGER,
app_size integer,
image text,
longdescription text);

CREATE TABLE IF NOT EXISTS requirements (
id bigserial,
app_name varchar(50) references applicationname(app_name),
app_requirement varchar(200));


CREATE TABLE IF NOT EXISTS bearer (
id bigserial,
app_name varchar(50) references applicationname(app_name),
app_bearer varchar(20));

CREATE TABLE IF NOT EXISTS tag (
id bigserial,
app_name varchar(50) references applicationname(app_name),
app_tag varchar(20));

CREATE TABLE IF NOT EXISTS activation (
id bigserial,
app_name varchar(50) references applicationname(app_name),
app_activation varchar(20));

# --- !Downs
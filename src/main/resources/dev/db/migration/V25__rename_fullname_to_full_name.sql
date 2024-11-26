-- V2__rename_fullname_to_full_name.sql
ALTER TABLE users CHANGE fullname full_name VARCHAR(100);
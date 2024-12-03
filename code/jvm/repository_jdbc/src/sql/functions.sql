create or replace function compare_partial_name(name text, partial text) returns boolean as $$
    select lower(name) like lower(concat('%', partial, '%'));
$$ language sql;
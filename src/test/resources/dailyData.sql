insert into daily_log (id, url, tenant, create_time, type)
values (1, 'test1', 1, now(), 0);
insert into daily_log (id, url, tenant, create_time, type)
values (2, 'test2', 1, now(), 0);
insert into daily_log (id, url, tenant, create_time, type)
values (3, 'test2', 1, dateadd('DAY', -2, CURRENT_TIMESTAMP), 1);
insert into daily_log (id, url, tenant, create_time, type)
values (4, 'test1', 1, CURRENT_TIMESTAMP - INTERVAL '1' DAY, 0);
insert into daily_log (id, url, tenant, create_time, type)
values (5, 'test3', 0, now(), 0);
insert into daily_log (id, url, tenant, create_time, type)
values (6, 'test3', 0, now(), 0);
insert into daily_log (id, url, tenant, create_time, type)
values (7, 'test3', 0, dateadd('DAY', -2, CURRENT_TIMESTAMP), 0);
insert into daily_log (id, url, tenant, create_time, type)
values (8, 'test3', 0, dateadd('DAY', -1, now()), 0);
insert into daily_log (id, url, tenant, create_time, type)
values (9, 'test4', 0, dateadd('DAY', -1, now()), 0);


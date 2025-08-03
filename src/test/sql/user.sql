/* 都走索引 */
explain
select t1.id, t1.name, t2.name as coll_name, JSON_ARRAYAGG(t4.name) as cat_names, t5.name as major_name
from user t1
         left join college t2 on t1.coll_id = t2.id
         left join user_category t3 on t1.id = t3.user_id
         left join category t4 on t4.id = t3.cat_id
         left join major t5 on t1.major_id = t5.id
where t1.id = 1399271139387179008;


/* 查询 */
explain
select concat_ws('-', u.name, u.account) from user u where u.id=1399271139387179008;

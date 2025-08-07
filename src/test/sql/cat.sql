/**/
select * from category t1 left join major t2
on t1.id=t2.cat_id where t1.id=1397990712034787328


select t1.id, t1.name, t1.coll_id, t1.comment, t1.weighting,
       t1.due_time, t2.id, t2.name, t2.cat_id
from category t1 left join major t2
                           on t1.id=t2.cat_id where t1.id=1397990712034787328;



explain
select t2.name as category_name, t2.id as cat_id, t3.name as user_name, t3.id as user_id
from user_category t1, category t2, user t3
where t1.cat_id=t2.id and t1.user_id=t3.id and t2.coll_id=1397954199536336896 and t3.role='Rcz9N';

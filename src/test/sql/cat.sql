/**/
select * from category t1 left join major t2
on t1.id=t2.cat_id where t1.id=1397990712034787328


select t1.id, t1.name, t1.coll_id, t1.comment, t1.weighting,
       t1.due_time, t2.id, t2.name, t2.cat_id
from category t1 left join major t2
                           on t1.id=t2.cat_id where t1.id=1397990712034787328
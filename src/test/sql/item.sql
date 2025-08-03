
explain
select * from item t1 where t1.cat_id=1397992599949737984 and t1.parent_id is null;


explain
with recursive t0 as (
    select * from item t1 where t1.cat_id=1397992599949737984 and (1398318844717629440 IS NULL AND t1.parent_id IS NULL) OR (t1.parent_id = 1398318844717629440)
    union
    select t2.* from item t2 join t0 where t2.parent_id=t0.id
)
select * from t0;


select * from student_item_file t1 join student_item t2
on t1.student_item_id=t2.id
where t2.id=:studentItemId and t2.user_id=:uid;

select * from item t1 where t1.parent_id=1399760678044106752;

/* 太麻烦，不利于维护 */
explain
with recursive t0 as (
    select * from item t1 where t1.parent_id=1399760678044106752
    union
    select t2.* from item t2 join t0 where t2.parent_id=t0.id
)
select * from t0, student_item t3 where t0.id=t3.item_id;

explain
select * from student_item t1 where t1.root_item_id=1399760678044106752 and t1.user_id=1399271139387179008

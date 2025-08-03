/* 基于顶级itemid，查询下面的全部提交，及文件 */
select t1.id, t1.user_id, t1.root_item_id, t1.item_id, t1.point, t1.name as name, t1.comment as comment,
t1.status, t2.path, t3.name as item_name, t3.comment as item_comment, t3.max_points, t3.max_items
    from student_item t1 join student_item_file t2
         on t1.id = t2.student_item_id
         join item t3 on t3.id=t1.item_id
         where t1.root_item_id=1399760678044106752 and t1.user_id=1399271139387179008;



explain
select t1.id, t1.user_id, t1.root_item_id, t1.item_id, t1.point, t1.name as name, t1.comment as comment,
       t1.status, t2.path, t2.filename, t3.name as item_name, t3.comment as item_comment, t3.max_points, t3.max_items, t3.id
from student_item t1 left join student_item_file t2
                               on t1.id = t2.student_item_id
                     join item t3 on t3.id=t1.item_id
where t1.user_id=1399271139387179008 order by t3.id;



select concat_ws('/', t1.path, t1.filename)
from student_item_file t1, student_item t2
where t1.student_item_id=t2.id and t2.user_id=1399271139387179008 and t1.id=1400856307617038336



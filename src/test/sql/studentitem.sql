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
where t1.student_item_id=t2.id and t2.user_id=1399271139387179008 and t1.id=1400856307617038336;


/* 查询指定专业下的全部学生提交项，包括学生姓名，总提交数，认定数，总分，加权成绩认定情况 */
explain
SELECT
    u.id as user_id,
    u.name as user_name,
    ws.score as score,
    ws.ranking as ranking,
    COALESCE(SUM(si.point), 0) as total_point,
    count(u.id) as total_count,
    COALESCE(SUM(si.status = 'av8c'), 0) as submitted_count,
    COALESCE(SUM(si.status = 'ciG1'), 0) as rejected_count,
    COALESCE(SUM(si.status = 'EmBq'), 0) as pending_count,
    COALESCE(SUM(si.status = 'yJ3C'), 0) as confirmed_count
FROM
    `user` u join weighted_score ws on ws.id=u.id
        LEFT JOIN student_item si ON u.id = si.user_id
WHERE
    u.major_id = 1399439239998930944
GROUP BY
    u.id, u.name
ORDER BY
    u.id;

/* 查询提交项，向上，提一个包含maxitems的节点 */
with recursive t0 as (
    select t1.id, t1.parent_id, 0 as depth, t1.max_items from item t1 where t1.id=1399768556448579584
    union
    select t2.id, t2.parent_id, t0.depth + 1, t2.max_items from item t2 join t0 on t2.id=t0.parent_id
)
select * from t0 where t0.max_items is not null order by t0.depth limit 1;



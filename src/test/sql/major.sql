explain

select concat_ws('/',t3.name, t2.name, t1.name) from major t1,category t2, college t3
where t1.cat_id=t2.id and t2.coll_id=t3.id and t1.id=1399439239998930944;
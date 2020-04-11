insert into sys_role(id,name) values(1,'ROLE_ADMIN');
insert into sys_role(id,name) values(2,'ROLE_UPLOAD');
insert into sys_role(id,name) values(3,'ROLE_DOWNLOAD');
insert into sys_role(id,name) values(4,'ROLE_ADD');
insert into sys_role(id,name) values(5,'ROLE_DELETE');

insert into excel_head(id,name) values(1,'图片');
insert into excel_head(id,name) values(2,'模具情况');
insert into excel_head(id,name) values(3,'产品生产情况，20160413更新');
insert into excel_head(id,name) values(4,'型号');
insert into excel_head(id,name) values(5,'类型');
insert into excel_head(id,name) values(6,'区域');
insert into excel_head(id,name) values(7,'描述');
insert into excel_head(id,name) values(8,'彩本尺寸');
insert into excel_head(id,name) values(9,'实量尺寸(mm)');
insert into excel_head(id,name) values(10,'包装尺寸（mm)');
insert into excel_head(id,name) values(11,'毛重(kg)');
insert into excel_head(id,name) values(12,'净重(kg)');
insert into excel_head(id,name) values(13,'体积(㎥)');
insert into excel_head(id,name) values(14,'FOB费用');
insert into excel_head(id,name) values(15,'包装等费用');
insert into excel_head(id,name) values(16,'空瓷');
insert into excel_head(id,name) values(17,'水件品牌');
insert into excel_head(id,name) values(18,'进水型号');
insert into excel_head(id,name) values(19,'排水型号');
insert into excel_head(id,name) values(20,'按钮型号');
insert into excel_head(id,name) values(21,'水件价格');
insert into excel_head(id,name) values(22,'盖板品牌');
insert into excel_head(id,name) values(23,'盖板型号');
insert into excel_head(id,name) values(24,'盖板价格');
insert into excel_head(id,name) values(25,'法兰');
insert into excel_head(id,name) values(26,'地脚螺丝');
insert into excel_head(id,name) values(27,'打标');
insert into excel_head(id,name) values(28,'包装物');
insert into excel_head(id,name) values(29,'其他');
insert into excel_head(id,name) values(30,'成本');
insert into excel_head(id,name) values(31,'毛利率');
insert into excel_head(id,name) values(32,'毛利');
insert into excel_head(id,name) values(33,'人民币');
insert into excel_head(id,name) values(34,'美金');
insert into excel_head(id,name) values(35,'汇率');
insert into excel_head(id,name) values(36,'含CUPC报价');
insert into excel_head(id,name) values(37,'备注');
insert into excel_head(id,name) values(38,'建档日期');
insert into excel_head(id,name) values(39,'调价后毛利（人民币）');
insert into excel_head(id,name) values(40,'调从后的毛利率');



insert into sys_user(id,username,password) values(1,'admin','$2a$10$seA.mioy0vcw4dGfviDybeURvJq0e6ThgO/RILvZ0PdhrEzKVC86y');
insert into sys_user(id,username,password) values(2,'user','$2a$10$seA.mioy0vcw4dGfviDybeURvJq0e6ThgO/RILvZ0PdhrEzKVC86y');

INSERT INTO SYS_USER_ROLES(SYS_USER_ID,ROLES_ID) VALUES (1,1);
INSERT INTO SYS_USER_ROLES(SYS_USER_ID,ROLES_ID) VALUES (1,2);
INSERT INTO SYS_USER_ROLES(SYS_USER_ID,ROLES_ID) VALUES (1,3);
INSERT INTO SYS_USER_ROLES(SYS_USER_ID,ROLES_ID) VALUES (1,4);
INSERT INTO SYS_USER_ROLES(SYS_USER_ID,ROLES_ID) VALUES (1,5);
INSERT INTO SYS_USER_ROLES(SYS_USER_ID,ROLES_ID) VALUES (2,2);
INSERT INTO SYS_USER_ROLES(SYS_USER_ID,ROLES_ID) VALUES (2,3);

INSERT INTO sys_user_heads(sys_user_id,heads_ID) VALUES (2,1);
INSERT INTO sys_user_heads(sys_user_id,heads_ID) VALUES (2,2);



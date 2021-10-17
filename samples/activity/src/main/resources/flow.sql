drop table if exists sys_flow;
create table sys_flow
(
    `id`          varchar(32) not null,
    `name`        varchar(32) not null,
    `data`        text,
    `update_time` datetime    not null,
    primary key (`id`)
) default character set utf8mb4 comment '流程定义表';

insert into sys_flow value ('holidayApply', '请假', '{"key":"holidayApply","name":"请假","nodeList":[{"id":"start","name":"开始","type":"START_EVENT"},{"id":"leaderTask","name":"领导审批","type":"USER_TASK"},{"id":"dayGateway","name":"请假时间","type":"EXCLUSIVE_GATEWAY"},{"id":"bossTask","name":"老板审批","type":"USER_TASK"},{"id":"hrTask","name":"人事审批","type":"USER_TASK"},{"id":"end","name":"结束","type":"END_EVENT"}],"sequenceList":[{"id":"s1","sourceId":"start","targetId":"leaderTask"},{"id":"s2","sourceId":"leaderTask","targetId":"dayGateway"},{"id":"s3","sourceId":"dayGateway","targetId":"bossTask","condition":"${day >= 5}"},{"id":"s4","sourceId":"dayGateway","targetId":"hrTask","condition":"${day < 5}"},{"id":"s5","sourceId":"bossTask","targetId":"end"},{"id":"s6","sourceId":"hrTask","targetId":"end"}]}', now());

drop table if exists sys_flow_deploy;
create table sys_flow_deploy
(
    `id`          int unsigned not null auto_increment,
    `flow_id`     varchar(32) not null,
    `name`        varchar(32) not null,
    `data`        text,
    `version`     int         not null,
    `deploy_time` datetime    not null,
    primary key (`id`)
) default character set utf8mb4 comment '流程部署记录表';
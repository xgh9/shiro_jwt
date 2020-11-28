package com.xing.shiro_jwt.vo;

import lombok.Data;

@Data
public class Task {

    private int id;

    private String name;

    //删除作业后变为0
    private int status = 1;
    //作业提交人数
    private int count;
}

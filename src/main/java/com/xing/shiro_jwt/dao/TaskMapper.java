package com.xing.shiro_jwt.dao;

import com.xing.shiro_jwt.vo.Task;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskMapper {
    Integer addTask(Task task);

    Integer checkExist(String name);

    Integer delete(int id);

    Integer hiddenTask(int id);

    List<Task> getTasks();

    List<Task> getCount();

    List<Task> getHiddenTasks();

    Task getTaskById(int id);
}

package com.xing.shiro_jwt.dao;

import com.xing.shiro_jwt.vo.JsonResponse;
import com.xing.shiro_jwt.vo.Submission;
import com.xing.shiro_jwt.vo.Task;
import com.xing.shiro_jwt.vo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskMapper {
    Integer insert(String name);

    Integer checkExist(String name);

//    List<Submission> getSubmissionsByTaskId(int taskId);
//
    List<Task> getAllTasks();

    Integer delete(int id);
}

package com.xing.shiro_jwt.dao;

import com.xing.shiro_jwt.vo.Submission;
import com.xing.shiro_jwt.vo.Task;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubmissionMapper {
    Integer checkSubmissionOfTask(int taskId);

    Integer addSubmission(Submission submission);

    Integer updateSubmissionName(Submission submission);

    Integer submissionCount(int taskId, String studentId);

    String getSubmission(int taskId, String studentId);

    List<Task> getSubmissionsByStudentId(String studentId);
}

package com.xing.shiro_jwt.service;

import com.xing.shiro_jwt.vo.JsonResponse;

public interface TaskService {

    JsonResponse addTask(String taskName);

    JsonResponse deleteTask(int taskId);

    JsonResponse getAllTasks();

    JsonResponse batchDownload(int taskId);

    JsonResponse downloadTask(String studentId);

    JsonResponse getSubmissionsByTaskId(int taskId);
}

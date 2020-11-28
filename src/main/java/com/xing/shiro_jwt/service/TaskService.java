package com.xing.shiro_jwt.service;

import com.xing.shiro_jwt.vo.JsonResponse;
import com.xing.shiro_jwt.vo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TaskService {

    JsonResponse addTask(String taskName);

    JsonResponse deleteTask(int taskId);

    JsonResponse getAllTasks();

    JsonResponse getHiddenTasks();

    JsonResponse upload(int taskId, MultipartFile file);

    ResponseEntity<byte[]> downloadOneTask(int taskId, String studentId);

    JsonResponse getSubmissionsByStudentId(String studentId);

    JsonResponse batchDownload(int taskId);

    JsonResponse getSubmissionsByTaskId(int taskId);
}

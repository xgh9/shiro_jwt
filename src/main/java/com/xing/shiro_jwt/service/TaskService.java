package com.xing.shiro_jwt.service;

import com.xing.shiro_jwt.vo.JsonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public interface TaskService {

    //发布作业
    JsonResponse addTask(String taskName);

    //删除作业，被提交过作业时改为隐藏
    JsonResponse deleteTask(int taskId);

    //获取所有作业及提交情况
    JsonResponse getTasks();

    //获取隐藏的作业
    JsonResponse getHiddenTasks();

    //作业上传
    JsonResponse upload(int taskId, MultipartFile file);

    //下载单个作业
    ResponseEntity<byte[]> downloadOneTask(int taskId, String studentId) throws UnsupportedEncodingException, FileNotFoundException;

    //获取单个学生的提交情况
    JsonResponse getSubmissionsByStudentId(String studentId);

    //批量下载单个作业的左右提交
    ResponseEntity<byte[]> batchDownload(int taskId) throws FileNotFoundException, UnsupportedEncodingException;

    //获取单个作业的提交情况
    JsonResponse getStudentSubmissionsByTaskId(int taskId);
}

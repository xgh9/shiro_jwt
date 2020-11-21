package com.xing.shiro_jwt.service;

import com.alibaba.fastjson.JSON;
import com.xing.shiro_jwt.dao.TaskMapper;
import com.xing.shiro_jwt.vo.JsonResponse;
import com.xing.shiro_jwt.vo.Task;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import javax.security.auth.Subject;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService{

    @Resource
    TaskMapper taskMapper;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public JsonResponse addTask(String taskName) {
        if (taskMapper.checkExist(taskName) > 0){
            return JsonResponse.invalidParam("作业 " + taskName + "已存在！");
        }
        int res = taskMapper.insert(taskName);
        if (res == 0){
            return JsonResponse.unknownError("发布作业失败！");
        }
        log.info(SecurityUtils.getSubject().getPrincipals().toString() + "发布作业：" + taskName);
        return JsonResponse.success();
    }

    @Override
    public JsonResponse deleteTask(int taskId) {
        taskMapper.delete(taskId);
        log.info(SecurityUtils.getSubject().getPrincipals().toString() + "删除作业" + taskId);
        return JsonResponse.success();
    }

    @Override
    public JsonResponse getAllTasks() {
        List<Task> tasks = taskMapper.getAllTasks();
        JsonResponse jsonResponse = JsonResponse.success();
        jsonResponse.put("data", JSON.toJSON(tasks));
        return jsonResponse;
    }

    @Override
    public JsonResponse batchDownload(int taskId) {
        return null;
    }

    @Override
    public JsonResponse downloadTask(String studentId) {
        return null;
    }

    @Override
    public JsonResponse getSubmissionsByTaskId(int taskId) {
        return null;
    }
}

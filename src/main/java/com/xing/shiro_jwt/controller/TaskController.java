package com.xing.shiro_jwt.controller;

import com.xing.shiro_jwt.service.TaskService;
import com.xing.shiro_jwt.service.TaskServiceImpl;
import com.xing.shiro_jwt.vo.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "作业操作")
@RequiresRoles("admin")
public class TaskController {

    @Autowired
    TaskService taskService;

    @PostMapping("/addTask")
    @ApiOperation("发布作业")
    public JsonResponse addTask(String taskName){
        if (StringUtils.isEmpty(taskName)){
            return JsonResponse.invalidParam("请输入作业名称！");
        }
        return taskService.addTask(taskName);
    }
    @PostMapping("/deleteTask")
    @ApiOperation("删除作业")
    public JsonResponse deleteTask(int taskId){
        if (StringUtils.isEmpty(taskId)){
            return JsonResponse.invalidParam("请输入作业ID！");
        }
        return taskService.deleteTask(taskId);
    }

    @GetMapping("/getAllTasks")
    @ApiOperation("获取所有作业")
    public JsonResponse getAllTasks(){
        return taskService.getAllTasks();
    }

    @PostMapping("/batchDownload")
    @ApiOperation("批量下载作业")
    public JsonResponse batchDownload(int taskId){
        if (StringUtils.isEmpty(taskId)){
            return JsonResponse.invalidParam("请输入批量下载的作业ID！");
        }
        return null;
    }

    @PostMapping("/downloadTask")
    @ApiOperation("下载单个学生的作业")
    public JsonResponse downloadTask(String studentId){
        if (StringUtils.isEmpty(studentId)){
            return JsonResponse.invalidParam("请输入学号！");
        }
        return null;
    }
}

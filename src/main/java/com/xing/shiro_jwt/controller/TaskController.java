package com.xing.shiro_jwt.controller;

import com.xing.shiro_jwt.service.TaskService;
import com.xing.shiro_jwt.vo.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@Api(tags = "作业操作")
public class TaskController {

    @Autowired
    TaskService taskService;

    @PostMapping("/addTask")
    @ApiOperation("发布作业")
    @RequiresRoles("admin")
    public JsonResponse addTask(@RequestBody String taskName){
        if (StringUtils.isEmpty(taskName)){
            return JsonResponse.invalidParam("请输入作业名称！");
        }
        return taskService.addTask(taskName);
    }

    /**
     * 删除作业时删除数据库记录但保留文件夹和里面的提交的作业
     * @param taskId
     * @return
     */
    @PostMapping("/deleteTask")
    @ApiOperation("删除作业")
    @RequiresRoles("admin")
    public JsonResponse deleteTask(@RequestBody int taskId){
        if (StringUtils.isEmpty(taskId)){
            return JsonResponse.invalidParam("请输入作业ID！");
        }
        return taskService.deleteTask(taskId);
    }

    @GetMapping("/getTasks")
    @ApiOperation("获取所有作业")
    @RequiresAuthentication
    public JsonResponse getAllTasks(){
        return taskService.getTasks();
    }

    @GetMapping("/getHiddenTasks")
    @ApiOperation("获取已删除/隐藏的作业")
    @RequiresRoles("admin")
    public JsonResponse getHiddenTasks(){
        return taskService.getHiddenTasks();
    }

    @PostMapping("/upload")
    @ApiOperation("作业上传")
    @RequiresAuthentication
    public JsonResponse upload(@RequestParam("file") MultipartFile file, int taskId){
        if (StringUtils.isEmpty(taskId)){
            return JsonResponse.invalidParam("请输入作业ID");
        }
        if (file.isEmpty()) {
            return JsonResponse.invalidParam("上传失败，请选择文件");
        }
        return taskService.upload(taskId,file);
    }

    //    "taskId":"1","studentId":"1001"
    @PostMapping("/downloadSelf")
    @ApiOperation("下载自己的的单个作业")
    @RequiresAuthentication
    public ResponseEntity<byte[]> downloadSelf(@RequestBody int taskId) throws UnsupportedEncodingException {
        Subject subject = SecurityUtils.getSubject();
        String id = (String) subject.getPrincipal();
        return taskService.downloadOneTask(taskId,id);
    }

    @PostMapping("/downloadById")
    @ApiOperation("下载单个学生的单个作业")
    @RequiresRoles("admin")
    public ResponseEntity<byte[]> downloadById(@RequestBody Map<String, String> params) throws UnsupportedEncodingException {
        int taskId = Integer.parseInt(params.get("taskId"));
        String studentId = params.get("studentId");
        return taskService.downloadOneTask(taskId,studentId);
    }

    //返回数据中的count是作业编号，如果为0代表这个人没教过作业
    @GetMapping("/getSubmissions")
    @ApiOperation("查看全部作业和本人的提交情况")
    @RequiresAuthentication
    public JsonResponse getSubmissions(){
        Subject subject = SecurityUtils.getSubject();
        String id = (String) subject.getPrincipal();
        return taskService.getSubmissionsByStudentId(id);
    }

    //返回数据中的count是作业编号，如果为0代表这个人没教过作业
    @PostMapping("/getSubmissionsById")
    @ApiOperation("查看单个学生的作业提交情况")
    @RequiresRoles("admin")
    public JsonResponse getSubmissionsById(@RequestBody String id){
        if (StringUtils.isEmpty(id)){
            return JsonResponse.invalidParam("请输入学号!");
        }
        return taskService.getSubmissionsByStudentId(id);
    }


    @PostMapping("/getSubmission")
    @ApiOperation("获取单个作业的所有提交信息")
    @RequiresAuthentication
    public JsonResponse getSubmission(@RequestBody int taskId){
        if (StringUtils.isEmpty(taskId)){
            return JsonResponse.invalidParam("请输入作业ID！");
        }
        return null;
    }

    @PostMapping("/batchDownload")
    @ApiOperation("批量下载作业")
    public JsonResponse batchDownload(int taskId){
        if (StringUtils.isEmpty(taskId)){
            return JsonResponse.invalidParam("请输入批量下载的作业ID！");
        }
        return null;
    }


}

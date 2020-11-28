package com.xing.shiro_jwt.service;

import com.alibaba.fastjson.JSON;
import com.xing.shiro_jwt.dao.SubmissionMapper;
import com.xing.shiro_jwt.dao.TaskMapper;
import com.xing.shiro_jwt.dao.UserMapper;
import com.xing.shiro_jwt.vo.JsonResponse;
import com.xing.shiro_jwt.vo.Submission;
import com.xing.shiro_jwt.vo.Task;
import com.xing.shiro_jwt.vo.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService{

    @Resource
    TaskMapper taskMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    SubmissionMapper submissionMapper;

    private String submissonPath;

    @Value("${submissonPath}")
    public void setsubmissonPath(String value){
        if (!value.endsWith(File.separator)){
            value += File.separator;
        }
        this.submissonPath = value;
    }

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonResponse addTask(String taskName) {
        if (taskMapper.checkExist(taskName) > 0){
            return JsonResponse.invalidParam("作业 " + taskName + "已存在！");
        }
        Task task = new Task();
        task.setName(taskName);
        int res = taskMapper.addTask(task);
        if (res == 0){
            return JsonResponse.unknownError("插入数据库失败！");
        }
        File file = new File(submissonPath);
        if (!file.exists()){
            file.mkdir();
        }
        file = new File(submissonPath  + task.getId());
        if (!file.exists()){
            file.mkdir();
        }
        log.info(SecurityUtils.getSubject().getPrincipals().toString() + "发布作业：" + taskName);
        return JsonResponse.success();
    }

    /**
     * 删除作业时分情况讨论
     * 1，该作业已经有学生提交过作业，隐藏改作业，将task的status置为0
     * 2，没有学生提交过作业，删除文件夹和数据库记录
     * @param taskId
     * @return
     */
    @Override
    public JsonResponse deleteTask(int taskId) {
        int submissionCount = submissionMapper.checkSubmissionOfTask(taskId);
        if (submissionCount == 0){
            taskMapper.delete(taskId);
            File file = new File(submissonPath  + taskId);
            if (file.isDirectory()){
                file.delete();
            }
        }else{
            taskMapper.hiddenTask(taskId);
        }
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
    public JsonResponse getHiddenTasks() {
        List<Task> tasks = taskMapper.getHiddenTasks();
        JsonResponse jsonResponse = JsonResponse.success();
        jsonResponse.put("data", JSON.toJSON(tasks));
        return jsonResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonResponse upload(int taskId, MultipartFile multipartFile) {
        //获取用户信息，不从token中取是为了关闭token用cookie时也能使用
        Subject subject = SecurityUtils.getSubject();
        String id = (String) subject.getPrincipal();
        User user = userMapper.getUserById(id);

        String[] strings = multipartFile.getOriginalFilename().split("\\.");
        if (strings.length < 2){
            return JsonResponse.invalidParam("文件名不规范");
        }
        String fileType = "." + strings[strings.length-1];
        String fileName = user.getId() + "_" + user.getName() + "_" + taskMapper.getTaskById(taskId).getName() + fileType;
        String path = submissonPath + taskId + File.separator + fileName;
        File file = new File(path);
        try {
            //若有同名文件会直接覆盖
            multipartFile.transferTo(file);
        } catch (IOException e) {
            log.error(e.getMessage());
            return JsonResponse.unknownError("文件上传失败，请重试！");
        }

        Submission submission = new Submission();
        submission.setTaskId(taskId);
        submission.setStudentId(user.getId());
        submission.setName(path);

        int count = submissionMapper.submissionCount(taskId, user.getId());
        //第一次提交作业时数据库表submissions新增数据
        if (count == 0){
            submissionMapper.addSubmission(submission);
        }else {//若已经提交过数据
            String oldPath = submissionMapper.getSubmission(taskId, user.getId());
            if (!oldPath.equals(path)){//数据库存的是文件名，当文件名不同时才修改数据库并删除旧文件
                submissionMapper.updateSubmissionName(submission);

                File oldFile = new File(oldPath);
                if (oldFile.exists()){
                    oldFile.delete();
                }
            }
        }
        return JsonResponse.success();
    }

    @Override
    public JsonResponse batchDownload(int taskId) {
        return null;
    }

    @Override
    public ResponseEntity<byte[]> downloadOneTask(int taskId, String studentId) {
        String path = submissionMapper.getSubmission(taskId, studentId);
        File file = new File(path);
        FileInputStream fis;
        byte[] b = null;
        try {
            fis = new FileInputStream(file);
            b= new byte[fis.available()];
            fis.read(b);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;fileName=" +  path);
        HttpStatus httpStatus = HttpStatus.OK;

        ResponseEntity<byte[]> entity = new ResponseEntity<>(b, headers, httpStatus);
        return entity;
    }

    @Override
    public JsonResponse getSubmissionsByStudentId(String studentId) {
//        List<Task> tasks = submissionMapper.getSubmissionsByStudentId(studentId);
//        System.out.println(tasks);
        JsonResponse jsonResponse = JsonResponse.success();
        List<Integer> list = submissionMapper.getSubmissionsByStudentId2(studentId);
        System.out.println(list);
//        jsonResponse.put("data",JSON.toJSON(tasks));
        return jsonResponse;
    }

    @Override
    public JsonResponse getSubmissionsByTaskId(int taskId) {

        return null;
    }
}

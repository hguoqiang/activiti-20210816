package org.hgq.activiti.test;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.hgq.activiti.pojo.Evection;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @description:
 * @author: huangguoqiang
 * @create: 2021-08-16 21:36
 **/
public class ActivitiDemo4 {
    /**
     * 部署流程定义
     */
    @Test
    public void testDeployment() {
//        1、创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        2、得到RepositoryService实例
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        3、使用RepositoryService进行部署
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/evectioncondition.bpmn") // 添加bpmn资源
                .name("出差申请流程请假天数")
                .deploy();
//        4、输出部署信息
        System.out.println("流程部署id：" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
        System.out.println("流程部署Key：" + deployment.getKey());
    }

    /**
     * 启动流程实例,设置流程变量的值
     */
    @Test
    public void startProcess() {
//        获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        流程定义key
        String key = "evectioncondition";
//       创建变量集合
        Map<String, Object> map = new HashMap<>();
//        创建出差pojo对象
        Evection evection = new Evection();
//        设置出差天数
        evection.setNum(3d);
//      定义流程变量，把出差pojo对象放入map
        map.put("evection", evection);
//      设置assignee的取值，用户可以在界面上设置流程的执行
        map.put("assignee0", "张三");
        map.put("assignee1", "李经理");
        map.put("assignee2", "王总经理");
        map.put("assignee3", "赵财务");
//        启动流程实例，并设置流程变量的值（把map传入）
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(key, map);
//      输出
        System.out.println("流程实例名称=" + processInstance.getName());
        System.out.println("流程定义id==" + processInstance.getProcessDefinitionId());

    }

    /**
     * 完成任务，判断当前用户是否有权限
     */
    @Test
    public void completTask() {
        //任务id
        String key = "evectioncondition";
//        任务负责人
        String assingee = "赵财务";
        //获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 创建TaskService
        TaskService taskService = processEngine.getTaskService();
//        完成任务前，需要校验该负责人可以完成当前任务
//        校验方法：
//        根据任务id和任务负责人查询当前任务，如果查到该用户有权限，就完成
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(key)
                .taskAssignee(assingee)
                .singleResult();
        if (task != null) {
            taskService.complete(task.getId());
            System.out.println("任务执行完成");
        }
    }


    /**
     * 启动流程实例
     */
    @Test
    public void testStartProcess() {

//        获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        流程定义key
        String key = "evectioncondition";
//       创建变量集合
        Map<String, Object> map = new HashMap<>();

//      设置assignee的取值，用户可以在界面上设置流程的执行
        map.put("assignee0", "张三");
        map.put("assignee1", "李经理");
        map.put("assignee2", "王总经理");
        map.put("assignee3", "赵财务");
//        启动流程实例，并设置流程变量的值（把map传入）
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(key, map);
//      输出
        System.out.println("流程实例名称=" + processInstance.getName());
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
        System.out.println("当前活动Id：" + processInstance.getActivityId());
    }


    /**
     * 完成任务时设置流程变量，判断当前用户是否有权限
     */
    @Test
    public void completTaskSetVariables() {
        //任务id
        String key = "evectioncondition";
//        任务负责人
        String assingee = "张三";
//       获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//       创建TaskService
        TaskService taskService = processEngine.getTaskService();
//       创建变量集合
        Map<String, Object> map = new HashMap<>();
//        创建出差pojo对象
        Evection evection = new Evection();
//        设置出差天数
        evection.setNum(4d);
//      定义流程变量
        map.put("evection", evection);
//        完成任务前，需要校验该负责人可以完成当前任务
//        校验方法：
//        根据任务id和任务负责人查询当前任务，如果查到该用户有权限，就完成
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(key)
                .taskAssignee(assingee)
                .singleResult();
        if (task != null) {
            //完成任务是，设置流程变量的值
            taskService.complete(task.getId(), map);
            System.out.println("任务执行完成");
        }
    }


    @Test
    public void setGlobalVariableByExecutionId() {
//    当前流程实例执行 id，通常设置为当前执行的流程实例
        String executionId = "40001";
//     获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        获取RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        创建出差pojo对象
        Evection evection = new Evection();
//        设置天数
        evection.setNum(3d);
//      通过流程实例 id设置流程变量
        runtimeService.setVariable(executionId, "evection", evection);
//      一次设置多个值
//      runtimeService.setVariables(executionId, variables)
    }

    @Test
    public void getGlobalVariableByExecutionId() {
//    当前流程实例执行 id，通常设置为当前执行的流程实例
        String executionId = "40001";
//     获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        获取RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();

//      通过流程实例 id 获取 流程变量
        Map<String, Object> variables = runtimeService.getVariables(executionId);

        Set<Map.Entry<String, Object>> entries = variables.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }


    @Test
    public void setGlobalVariableByTaskId() {

        //当前待办任务id
        String taskId = "40009";
//     获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        Evection evection = new Evection();
        evection.setNum(1d);
        //通过任务设置流程变量
        taskService.setVariable(taskId, "evection", evection);
        //一次设置多个值
        //taskService.setVariables(taskId, variables)
    }


    /*
     *处理任务时设置local流程变量
     */
    @Test
    public void completTaskSetLocalVariables() {
        //任务id
        String taskId = "40009";
//  获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
//  定义流程变量
        Map<String, Object> variables = new HashMap<String, Object>();
        Evection evection = new Evection();
        evection.setNum(6d);

//  变量名是holiday，变量值是holiday对象
        variables.put("evection", evection);
//  设置local变量，作用域为该任务
        taskService.setVariablesLocal(taskId, variables);
//  完成任务
        taskService.complete(taskId);
    }

    @Test
    public void setLocalVariableByTaskId() {
//   当前待办任务id
        String taskId = "1404";
//  获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        Evection evection = new Evection();
        evection.setNum(3d);
//  通过任务设置流程变量
        taskService.setVariableLocal(taskId, "evection", evection);
//  一次设置多个值
        //taskService.setVariablesLocal(taskId, variables)
    }

}

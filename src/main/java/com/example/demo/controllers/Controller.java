package com.example.demo.controllers;

import com.example.demo.configuration.PropertiesModel;
import com.example.demo.configuration.PropertiesUtils;
import com.example.demo.dto.TaskUrlDto;
import com.example.demo.dto.TasksInfoDto;
import com.example.demo.models.TaskModel;
import com.example.demo.services.SparkApplicationService;
import com.example.demo.services.SparkService;
import org.apache.spark.SparkConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by as on 28.06.2018.
 */
@RestController
public class Controller {
    private SparkApplicationService sparkApplicationService;
    private SparkService sparkService;
    private volatile static Hashtable<UUID, TaskModel> runningTasks = new Hashtable<>();
    private SparkConf conf;

    @Autowired
    public Controller(SparkService sparkService, SparkApplicationService sparkApplicationService) {
        this.sparkService = sparkService;
        this.sparkApplicationService = sparkApplicationService;

        conf = new SparkConf()
                .setAppName("Apache_Spark_Application")
                .set("spark.driver.allowMultipleContexts", "true")
                .set("spark.executor.memory", "1g")
                //.set("spark.submit.deployMode", "cluster") // startPort should be between 1024 and 65535 (inclusive), or 0 for a random free port.
                .set("spark.driver.host", PropertiesModel.spark_driver_host)
                .set("spark.driver.port", PropertiesModel.spark_driver_port) //
                .set("spark.blockManager.port", PropertiesModel.spark_blockManager_port) // Raw socket via ServerSocketChannel
                ///
                .set("spark.cores.max","4")
                .set("spark.eventLog.enabled", "true")
                //.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//                .set("spark.shuffle.service.enabled", "false")
//                .set("spark.dynamicAllocation.enabled", "false")
//                .set("spark.io.compression.codec", "snappy")
//                .set("spark.rdd.compress", "true")
                //.set("spark.executor.cores", "4c")
                //.setJars(new String[]{PropertiesModel.jars, PropertiesModel.databaseJar})
                .setJars(PropertiesUtils.getJars(PropertiesModel.jars,PropertiesUtils.delimiter))
                //.set("spark.dynamicAllocation.enabled", "false")
                .setMaster(PropertiesModel.spark_master);
        //.setMaster("local");
    }

    // GET ALL TASKS
    @GetMapping("/all")
    public ResponseEntity<Iterable<TaskModel>> getAllTasks() {
        return new ResponseEntity<>(sparkService.getAllTasks(runningTasks), HttpStatus.OK);
    }

    // GET TASKS SUMMARY INFO
    @GetMapping("/info")
    public ResponseEntity<TasksInfoDto> getTasksInfo() {
        return new ResponseEntity<>(sparkService.getTasksInfo(runningTasks), HttpStatus.OK);
    }

    // GET PROPERTIES
    @GetMapping("/prop")
    public ResponseEntity<String> getProperties() {
        return new ResponseEntity<>(sparkService.getProperties(), HttpStatus.OK);
    }

    // SET MASTER
    @GetMapping("/master/set")
    public ResponseEntity<String> setMaster(@RequestParam(name = "master", required = false, defaultValue = "local") String master) {
        return new ResponseEntity<>(sparkService.setMaster(conf, master), HttpStatus.OK);
    }

    // GET MASTER
    @GetMapping(value = "/master")
    public ResponseEntity<String> getMaster() {
        return new ResponseEntity<>(sparkService.getMaster(conf), HttpStatus.OK);
    }

    // START TASK
    @GetMapping(value = "/start")
    public ResponseEntity<TaskUrlDto> startTask(@RequestParam(name = "task", required = false, defaultValue = "1") String task, HttpServletRequest request) {
        return new ResponseEntity<>(sparkService.startTask(runningTasks, conf, request, task), HttpStatus.OK);
    }

    // GET TASK BY ID
    @GetMapping(value = "/{id}")
    public ResponseEntity<TaskModel> getTask(@PathVariable String id) {
        return new ResponseEntity<>(sparkService.getTask(runningTasks, id), HttpStatus.OK);
    }

    // STOP TASK BY ID
    @GetMapping(value = "/stop/{id}")
    public ResponseEntity<TaskModel> stopTaskById(@PathVariable String id) {
        return new ResponseEntity<>(sparkService.stopTaskById(runningTasks, id), HttpStatus.OK);
    }

    // STOP ALL
    @GetMapping(value = "/stop")
    public ResponseEntity<String> stopAllTasks() {
        return new ResponseEntity<>(sparkService.stopAllTasks(runningTasks), HttpStatus.OK);
    }

    // CLEAN INFO ABOUT TASKS
    @GetMapping(value = "/clean")
    public ResponseEntity<String> clean() {
        return new ResponseEntity<>(sparkService.clean(runningTasks), HttpStatus.OK);
    }
}

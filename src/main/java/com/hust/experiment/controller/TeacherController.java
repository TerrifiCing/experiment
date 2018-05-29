package com.hust.experiment.controller;

import com.hust.experiment.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TeacherController {
    @Autowired
    MailService mailService;

    @Autowired
    UserService userService;

    @Autowired
    StudentService studentService;

    @Autowired
    ExpService expService;

    @Autowired
    ReportService reportService;

    @Autowired
    CourseService courseService;

}

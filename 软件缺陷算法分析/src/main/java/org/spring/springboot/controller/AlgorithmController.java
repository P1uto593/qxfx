package org.spring.springboot.controller;

import org.spring.springboot.domain.Dataset;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller

@RequestMapping(value = "/")
public class AlgorithmController {
    @ResponseBody
    // 添加文件ajax处理
    @RequestMapping(value = "train")
    public Map train(HttpServletRequest request, HttpServletResponse response, @RequestBody Map data
    ) throws IOException {
        Map resultmap = new HashMap<>();//返回结果
        return resultmap;
    }
}

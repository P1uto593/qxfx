package org.spring.springboot.controller;

import org.spring.springboot.dao.DatasetMapper;
import org.spring.springboot.dao.ModelMapper;
import org.spring.springboot.domain.Dataset;
import org.spring.springboot.domain.model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller

@RequestMapping(value = "/")
public class DataSetController {
    @Autowired
    private DatasetMapper datasetdao;

    @ResponseBody
    // 添加文件ajax处理
    @RequestMapping(value = "getdataset")
    public List<Dataset> getdataset(HttpServletRequest request, HttpServletResponse response, @RequestBody Map nowuser
    ) throws IOException {

        List<Dataset> datasets=datasetdao.select(nowuser);
        return datasets;
    }

    @ResponseBody
    // 添加文件ajax处理
    @RequestMapping(value = "new_addfilejson")
    public String new_addfilejson(HttpServletRequest request, HttpServletResponse response, MultipartFile file, @RequestParam(value = "username") String usr_name, @RequestParam(value = "type") String type,
                                  HttpSession session) throws IOException {
        upload(file, request,usr_name);
        Dataset dataset=new Dataset();
        String originalFileName = file.getOriginalFilename();
        dataset.setName(originalFileName);
        dataset.setOwner(usr_name);
        dataset.setType(type);
        datasetdao.insert(dataset);
        return "1";
    }
    public String upload(MultipartFile file, HttpServletRequest request,String name) throws IOException {


        String rootPath = request.getSession().getServletContext().getRealPath("resource/uploads/");

        String originalFileName = file.getOriginalFilename();


        File newFile = new File(rootPath + name+File.separator + originalFileName);

        // 判断目标文件所在目录是否存在

        if (!newFile.getParentFile().exists()) {

            // 如果目标文件所在的目录不存在，则创建父目录

            newFile.getParentFile().mkdirs();

        }
        file.transferTo(newFile);



        return "1";

    }
}

package com.code_generator_server;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import com.codegen.SeqModel;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.sortBySpecificity;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CodeGeneratorController {

    @RequestMapping(value = "/greeting", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    String generateCode(HttpServletRequest request) throws IOException, JSONException {
        ServletInputStream stream = request.getInputStream();
        String body = IOUtils.toString(stream);
        SeqModel model = new SeqModel(new JSONObject(body), "model");

        return model.toCode();
    }

    @GetMapping("/main_page.html")
    String main_page()
    {
        return "main_page";
    }

    @GetMapping("/navigation_page/{pageName}")
    String navigation_page(@PathVariable String pageName) {
        pageName = pageName.split("\\.")[0];
        return pageName;
    }

    @GetMapping("/css/{filename}")
    @ResponseBody
    byte[] modelStyle(@PathVariable String filename) throws IOException {
        File file = ResourceUtils.getFile("classpath:static/css/" + filename);
        FileInputStream stream = new FileInputStream(file);
        byte[] res = IOUtils.toByteArray(stream);
        stream.close();
        return res;
    }

    @GetMapping("/lib/{filename}")
    @ResponseBody
    byte[] getLib(@PathVariable String filename) throws IOException {
        File file = ResourceUtils.getFile("classpath:static/lib/" + filename);
        FileInputStream stream = new FileInputStream(file);
        byte[] res = IOUtils.toByteArray(stream);
        stream.close();
        return res;
    }

    @GetMapping("/gui/{filename}")
    @ResponseBody
    byte[] getGui(@PathVariable String filename) throws IOException {
        File file = ResourceUtils.getFile("classpath:static/gui/" + filename);
        FileInputStream stream = new FileInputStream(file);
        byte[] res = IOUtils.toByteArray(stream);
        stream.close();
        return res;
    }

}

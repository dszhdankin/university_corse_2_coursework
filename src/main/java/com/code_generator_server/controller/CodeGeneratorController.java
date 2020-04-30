package com.code_generator_server.controller;

import java.io.*;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import com.code_generator_server.entity.User;
import com.code_generator_server.service.UserService;
import com.codegen.SeqModel;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import javax.validation.Valid;

@Controller
public class CodeGeneratorController {

    @Autowired
    UserService service;

    @GetMapping("/")
    String site() {
        return "redirect:/main_page.html";
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

    @GetMapping("/register.html")
    String registerPage() {
        return "register";
    }

    @PostMapping("/register.html")
    String handleRegistry(@ModelAttribute("user") @Valid User user) {
        service.saveUser(user);
        return "redirect:/";
    }

    @PostMapping("/upload_layers")
    @ResponseBody
    String handleLayers(HttpEntity<String> httpEntity, Principal principal) {
        String layers = httpEntity.getBody();
        service.updateLayers(principal, layers);
        return "";
    }

    @PostMapping("/upload_hyper_parameters")
    @ResponseBody
    String handleHyperParameters(HttpEntity<String> httpEntity, Principal principal) {
        String hyperParameters = httpEntity.getBody();
        service.updateHyperParameters(principal, hyperParameters);
        return "";
    }

    @GetMapping("/download_json")
    @ResponseBody
    String getModelJSON(Principal principal) {
        User user = (User) service.loadUserByUsername(principal.getName());
        JSONObject res = new JSONObject(user.getHyperparameters());
        JSONArray layersArray = new JSONArray(user.getLayers());
        res.put("layers", layersArray);
        System.out.println(res.toString());
        return res.toString();
    }

    @GetMapping("/download_layers")
    @ResponseBody
    String handleDownloadLayers(Principal principal) {
        User user = (User) service.loadUserByUsername(principal.getName());
        return user.getLayers();
    }

    @GetMapping("/download_hyper_parameters")
    @ResponseBody
    String handleDownloadHyperParameters(Principal principal) {
        User user = (User) service.loadUserByUsername(principal.getName());
        return user.getHyperparameters();
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

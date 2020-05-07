package com.code_generator_server.controller;

import java.io.*;
import java.security.Principal;
import java.sql.*;
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
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.sortBySpecificity;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @PostMapping("/upload_dataset")
    @ResponseBody
    String handleDataset(@RequestParam(name = "data") MultipartFile file, Principal principal) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost/neural_network_visual_creator_db",
                    "postgres",
                    "#Jagarvasya1");
            try {
                byte[] fileBytes = file.getBytes();
                PreparedStatement ps =
                        connection.prepareStatement("UPDATE users SET dataset = ? WHERE username=" +
                                "'" + principal.getName() + "'" + ";");
                ps.setBytes(1, fileBytes);
                ps.executeUpdate();
            } catch (IOException ioException) {
                System.out.println("Error reading file");
            }

        } catch (SQLException e) {
            System.out.println("Update failed");
            System.out.println(e.getMessage());
        }
        return "Uploaded!";
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

    @GetMapping("/download_script")
    void getFile(Principal principal, HttpServletResponse response) throws IOException {
        File file = ResourceUtils.getFile("classpath:static/script/parse_zip.py");
        FileInputStream stream = new FileInputStream(file);
        String code = IOUtils.toString(stream);
        code += "\n";
        code += "data = form_train_data(zip_archive)\n";
        code += "x = numpy.stack(data[0], axis=0)\n";
        code += "y = numpy.array(data[1])\n";
        code += "side = data[2]\n\n";
        JSONObject modelJSON = new JSONObject(getModelJSON(principal));
        SeqModel model = new SeqModel(modelJSON, "model", "x", "y", "side");
        code += model.toCode();
        response.setContentType("application/download");
        InputStream scriptStream = new ByteArrayInputStream(code.getBytes());
        IOUtils.copy(scriptStream, response.getOutputStream());
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

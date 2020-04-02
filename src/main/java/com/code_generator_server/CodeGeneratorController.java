package com.code_generator_server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.codegen.SeqModel;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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

    @GetMapping("/first/page")
    String sampleView()
    {
        return "static_sample";
    }

    @GetMapping("/model")
    String model()
    {
        return "model_page";
    }

}

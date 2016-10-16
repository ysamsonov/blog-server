package me.academeg.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JsAppController {

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }
}

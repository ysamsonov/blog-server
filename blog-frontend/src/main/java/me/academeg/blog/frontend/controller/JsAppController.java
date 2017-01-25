package me.academeg.blog.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * JsAppController Controller
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Controller
public class JsAppController {

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }
}

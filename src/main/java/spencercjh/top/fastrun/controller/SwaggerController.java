package spencercjh.top.fastrun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author spencercjh
 */
@Controller
public class SwaggerController {

    @RequestMapping("/")
    public ModelAndView home() {
        return new ModelAndView("test.html");
    }
}

package boot;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;

import org.slf4j.*;

@Controller
public class WebController {

    private static final Logger log = LoggerFactory.getLogger(WebController.class);

    @RequestMapping("/hello")
    public String greeting(
    	@RequestParam(value="name", required=false, defaultValue="World") 
    	String name, Model model) {
    	// put result in model
        model.addAttribute("name", name);
        
        // return view name pointing to templates/hello.html
        return "hello";
    }
}
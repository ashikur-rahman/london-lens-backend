
package uk.co.londonlens.publicsite;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicPageController {

    @GetMapping("/")
    public String home() {
        return "public/home";
    }
}

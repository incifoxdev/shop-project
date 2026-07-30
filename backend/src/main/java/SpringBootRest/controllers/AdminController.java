package SpringBootRest.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {


    @GetMapping("/test")
    public String test(){

        return "Bienvenido dueño";

    }
}
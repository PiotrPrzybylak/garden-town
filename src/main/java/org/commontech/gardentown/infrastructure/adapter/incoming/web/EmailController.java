package org.commontech.gardentown.infrastructure.adapter.incoming.web;

import lombok.AllArgsConstructor;
import org.commontech.gardentown.infrastructure.adapter.outgoing.email.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Controller
class EmailController {

    private final EmailService emailService;

    @GetMapping("/email")
    String send(){
        emailService.sendSimpleMessage("", "ęą", "cośtam");
        return "redirect:/";
    }

}

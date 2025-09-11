package com.br.td.utfpr.edu.tsi.frontendcadastro.controller;

import com.br.td.utfpr.edu.tsi.frontendcadastro.model.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
public class UsuarioController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("usuario", new Usuario("", "", "", ""));
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8081/cliente_api/usuario";
        Usuario[] usuariosArray = restTemplate.getForObject(apiUrl, Usuario[].class);
        List<Usuario> usuarios = usuariosArray != null ? Arrays.asList(usuariosArray) : List.of();
        model.addAttribute("usuarios", usuarios);
        return "index";
    }


    @PostMapping("/usuarios/salvar")
    public String salvarUsuario(@ModelAttribute Usuario usuario) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8081/cliente_api/usuario";
        restTemplate.postForObject(apiUrl, usuario, Usuario.class);
        return "redirect:/";
    }
}
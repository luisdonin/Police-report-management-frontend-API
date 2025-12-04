package com.br.td.utfpr.edu.tsi.frontendcadastro.controller;

import com.br.td.utfpr.edu.tsi.frontendcadastro.model.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
public class UsuarioController {


    private static final String API_BASE = "http://localhost:8081/cliente_api/boletim";

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("usuario", new Usuario("", "", "", "", "","", "", "", "", "", "", "", "", "", ""));

        return "index";
    }

    @GetMapping("/boletim")
    public String listaUsuarios(Model model,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "size", defaultValue = "9") int size) {
        RestTemplate restTemplate = new RestTemplate();
        String url = API_BASE;

        Usuario[] usuariosArray = restTemplate.getForObject(url, Usuario[].class);
        List<Usuario> usuarios = usuariosArray != null ? Arrays.asList(usuariosArray) : List.of();

        // normalize page/size (user-facing UI is 1-based page)
        if (size <= 0) {
            size = 9;
        }
        int totalItems = usuarios.size();
        int totalPages = (totalItems == 0) ? 1 : (int) Math.ceil((double) totalItems / (double) size);
        if (page < 1) {
            page = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, totalItems);
        List<Usuario> usuariosPage;
        if (totalItems == 0) {
            usuariosPage = List.of();
        } else {

            if (startIndex >= totalItems) {
                usuariosPage = List.of();
            } else {
                usuariosPage = usuarios.subList(startIndex, endIndex);
            }
        }

        model.addAttribute("usuarios", usuariosPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        return "list";
    }


    @PostMapping("/boletim/salvar")
    public String salvarUsuario(@ModelAttribute Usuario usuario) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(API_BASE, usuario, Usuario.class);

        return "redirect:/boletim";
    }
    @GetMapping("/boletim/remover/{id}")
    public String removerUsuario(@PathVariable("id") String id) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = API_BASE + "/remover/" + id;
        restTemplate.getForObject(apiUrl, Void.class);
        return "redirect:/boletim";
    }
    @GetMapping("/boletim/editar/{id}")
    public String editarUsuario(@PathVariable("id") String id, Model model) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = API_BASE + "/" + id;
        Usuario usuario = restTemplate.getForObject(apiUrl, Usuario.class);

        if (usuario == null) {
            usuario = new Usuario("", "", "", "", "","", "", "", "", "", "", "", "", "", "");
            usuario.setId(id);
        }

        model.addAttribute("usuario", usuario);


        Usuario[] usuariosArray = restTemplate.getForObject(API_BASE, Usuario[].class);
        List<Usuario> usuarios = usuariosArray != null ? Arrays.asList(usuariosArray) : List.of();
        model.addAttribute("usuarios", usuarios);

        return "editar";
    }

    @PostMapping("/boletim/atualizar")
    public String atualizarUsuario(@ModelAttribute Usuario usuario) {
        if (usuario == null || usuario.getId() == null || usuario.getId().isBlank()) {

            return "redirect:/boletim";
        }

        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = API_BASE + "/" + usuario.getId();

        try {
            restTemplate.put(apiUrl, usuario);
        } catch (HttpClientErrorException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Erro inesperado ao atualizar usuário: " + e.getMessage());
        }

        return "redirect:/boletim";
    }


}
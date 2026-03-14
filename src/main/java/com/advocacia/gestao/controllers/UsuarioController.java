package com.advocacia.gestao.controllers;

import com.advocacia.gestao.dto.UsuarioCreateDTO;
import com.advocacia.gestao.dto.UsuarioDTO;
import com.advocacia.gestao.services.UsuarioService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Criar usuário
    @PostMapping
    public UsuarioDTO criarUsuario(@RequestBody UsuarioCreateDTO dto) {
        return usuarioService.criarUsuario(dto);
    }

    // Listar usuários
    @GetMapping
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public UsuarioDTO buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    // Desativar usuário
    @PatchMapping("/{id}/desativar")
    public void desativarUsuario(@PathVariable Long id) {
        usuarioService.desativarUsuario(id);
    }
}
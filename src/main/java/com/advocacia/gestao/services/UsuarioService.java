package com.advocacia.gestao.services;

import com.advocacia.gestao.entities.Usuario;
import com.advocacia.gestao.dto.UsuarioCreateDTO;
import com.advocacia.gestao.dto.UsuarioDTO;
import com.advocacia.gestao.repositories.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Criar usuário
    public UsuarioDTO criarUsuario(UsuarioCreateDTO dto) {

        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username já existe");
        }

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario usuario = new Usuario();

        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setRole(dto.getRole());
        usuario.setFullName(dto.getFullName());
        usuario.setIsActive(true);

        Usuario salvo = usuarioRepository.save(usuario);

        return toDTO(salvo);
    }

    // Buscar todos usuários
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Buscar por ID
    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return toDTO(usuario);
    }

    // Desativar usuário
    public void desativarUsuario(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setIsActive(false);

        usuarioRepository.save(usuario);
    }

    // Converter Entity → DTO
    private UsuarioDTO toDTO(Usuario usuario) {

        return new UsuarioDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getFullName(),
                usuario.getIsActive(),
                usuario.getCreatedAt()
        );
    }
}
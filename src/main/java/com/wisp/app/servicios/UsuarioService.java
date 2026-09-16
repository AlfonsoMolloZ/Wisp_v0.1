package com.wisp.app.servicios;

import com.wisp.app.entity.Usuario;
import java.util.List;

public interface UsuarioService {
    Usuario guardarUsuario(Usuario usuario);

    List<Usuario> listarUsuarios();

    Usuario obtenerUsuarioPorId(Long id);

    void eliminarUsuario(Long id);
}

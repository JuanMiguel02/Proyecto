package triplej.banco.Repositories;


import triplej.banco.Models.Usuarios.RolUsuario;
import triplej.banco.Models.Usuarios.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UsuarioRepository {
    private static UsuarioRepository instancia;
    // Unica lista para todos los usuarios.
    private final ArrayList<Usuario> usuarios;

    public UsuarioRepository() {
        this.usuarios = new ArrayList<>();
    }

    public static synchronized UsuarioRepository getInstancia() {
        if (instancia == null) {
            instancia = new UsuarioRepository();
        }
        return instancia;
    }

    // Unico metodo para guardar cualquier tipo de usuario.
    public void guardar(Usuario usuario) {
        if(buscarUsuarioPorEmail(usuario.getCorreo()).isPresent()){
            throw  new IllegalArgumentException("Advertencia: Se intentó guardar un cliente con un email que ya existe: " + usuario.getCorreo());
        }
        usuarios.add(usuario);
    }

    public Optional<Usuario> buscarUsuarioPorEmail(String correo) {
        return usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }

    public List<Usuario> obtenerPorRol(RolUsuario rol){
        return usuarios.stream()
                .filter(u -> u.getRolUsuario() == rol)
                .collect(Collectors.toList());
    }

    public int contarTodos(){
        return usuarios.size();
    }


}

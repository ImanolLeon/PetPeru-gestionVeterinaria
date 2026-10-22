package com.example.GestionVeterinaria.service;

import com.example.GestionVeterinaria.entity.Cliente;
import com.example.GestionVeterinaria.exception.DniInvalidoException;
import com.example.GestionVeterinaria.repository.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClienteService {

    //Inicializamos el repositorio
    private final ClienteRepository clienteRepository;


    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    //listar clientes
    public List<Cliente> listarTodos(){
        return clienteRepository.findAll();
    }

    //listar clientes paginado (para las tablas)
    public Page<Cliente> listarPaginado(int page, int size){
        return clienteRepository.findAll(
                PageRequest.of(page, size, Sort.by("id").ascending()));
    }

    //Buscar cliente por id
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }


    public Cliente registrar(Cliente cliente){

        //El DNI es obligatorio y único
        String dni = normalizarDni(cliente.getDni());

        if (dni.isEmpty()){
            throw new DniInvalidoException("El DNI es obligatorio.");
        }
        if (clienteRepository.existsByDni(dni)){
            throw new DniInvalidoException("El DNI ya está registrado.");
        }

        cliente.setDni(dni);
        cliente.setFechRegistro(LocalDate.now());
        return clienteRepository.save(cliente);

    }
    public long mostrarCantidadClientes(){
        return clienteRepository.countTodosLosUsuarios();
    }

    //Actualizar los datos de un cliente existente
    public Cliente actualizar(Cliente cliente){

        //Buscamos el cliente actual para no perder datos que no se editan
        Cliente existente = buscarPorId(cliente.getId());

        //El DNI debe ser único; sí se permite conservar el del propio cliente
        String dni = normalizarDni(cliente.getDni());

        if (dni.isEmpty()){
            throw new DniInvalidoException("El DNI es obligatorio.");
        }
        //existsByDniAndIdNot ignora al cliente que se está editando
        if (clienteRepository.existsByDniAndIdNot(dni, cliente.getId())){
            throw new DniInvalidoException("El DNI ya está registrado.");
        }

        existente.setDni(dni);
        existente.setNombre(cliente.getNombre());
        existente.setApellido(cliente.getApellido());
        existente.setTelefono(cliente.getTelefono());
        existente.setEmail(cliente.getEmail());
        existente.setDireccion(cliente.getDireccion());

        return clienteRepository.save(existente);
    }

    //¿Ese DNI ya lo tiene OTRO cliente? Lo usa el aviso en vivo del formulario.
    //idExcluir es el cliente que se está editando (null si es uno nuevo).
    public boolean existeDni(String dni, Long idExcluir){

        String limpio = normalizarDni(dni);

        if (limpio.isEmpty()){
            return false;
        }
        return idExcluir == null
                ? clienteRepository.existsByDni(limpio)
                : clienteRepository.existsByDniAndIdNot(limpio, idExcluir);
    }

    //Eliminar cliente
    public void eliminar(Long id){
        Cliente cliente =buscarPorId(id);
        clienteRepository.delete(cliente);
    }

    //El DNI se guarda y se compara sin espacios y en mayúsculas: así
    //"61230116 " o "abc123" no crean duplicados que parecen distintos
    private String normalizarDni(String dni){
        return dni == null ? "" : dni.trim().toUpperCase();
    }

}

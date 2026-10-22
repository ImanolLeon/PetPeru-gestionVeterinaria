package com.example.GestionVeterinaria.service;

import com.example.GestionVeterinaria.entity.Cliente;
import com.example.GestionVeterinaria.entity.Mascota;
import com.example.GestionVeterinaria.repository.ClienteRepository;
import com.example.GestionVeterinaria.repository.MascotaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final ClienteRepository clienteRepository;

    public MascotaService(MascotaRepository mascotaRepository,
                          ClienteRepository clienteRepository) {
        this.mascotaRepository = mascotaRepository;
        this.clienteRepository = clienteRepository;
    }

    // Listar todas las mascotas
    public List<Mascota> listarTodas() {
        return mascotaRepository.findAll();
    }

    // Total de mascotas registradas (para el dashboard)
    public long contarMascotas() {
        return mascotaRepository.count();
    }

    // Listar mascotas por cliente
    public List<Mascota> listarPorCliente(Long clienteId) {
        if(!clienteRepository.existsById(clienteId)){
            throw  new RuntimeException("Cliente no encontrado");
        }
        return mascotaRepository.findByClienteId(clienteId);
    }

    // Listar mascotas por cliente, paginado (para las cards)
    public Page<Mascota> listarPorCliente(Long clienteId, int page, int size) {
        if(!clienteRepository.existsById(clienteId)){
            throw  new RuntimeException("Cliente no encontrado");
        }
        return mascotaRepository.findByClienteId(clienteId,
                PageRequest.of(page, size, Sort.by("id").ascending()));
    }

   public Mascota buscarPorId(Long id){
     return mascotaRepository.findById(id).orElseThrow(()->
             new RuntimeException("Mascota no encontrada"));
   }

   //Registrar mascota
    public Mascota registrar(Mascota mascota, Long id_cliente){
        Cliente cliente= clienteRepository.findById(id_cliente).
                orElseThrow(()-> new RuntimeException("Cliente no encontrado"));
        //Asociamos cliente con mascota
        mascota.setCliente(cliente);
        return mascotaRepository.save(mascota);
    }

    //Actualizar una mascota existente: conserva su cliente y su imagen
    //(la imagen solo se reemplaza si llega una nueva)
    public Mascota actualizar(Mascota datos){
        Mascota existente = buscarPorId(datos.getId());
        existente.setNombre(datos.getNombre());
        existente.setEspecie(datos.getEspecie());
        existente.setRaza(datos.getRaza());
        existente.setSexo(datos.getSexo());
        existente.setEdad(datos.getEdad());
        existente.setPeso(datos.getPeso());
        if (datos.getImagen() != null){
            existente.setImagen(datos.getImagen());
        }
        return mascotaRepository.save(existente);
    }


    //Eliminar por id
    public void eliminarMascota(Long id){
        Mascota mascota= buscarPorId(id);
        mascotaRepository.delete(mascota);
    }
}

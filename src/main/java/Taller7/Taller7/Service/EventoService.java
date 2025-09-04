/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Taller7.Taller7.Service;

import Taller7.Taller7.Model.Evento;
import Taller7.Taller7.Repository.EventoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    public Evento guardarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Optional<Evento> obtenerEventoPorId(Long id) {
        return eventoRepository.findById(id);
    }

    public void eliminarEvento(Long id) {
        eventoRepository.deleteById(id);
    }
}
package com.devsuperior.bds04.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds04.dto.EventDTO;
import com.devsuperior.bds04.entities.Event;
import com.devsuperior.bds04.repositories.EventRepository;
import com.devsuperior.bds04.services.exceptions.DatabaseException;
import com.devsuperior.bds04.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;


@Service
public class EventService {
	@Autowired
	private EventRepository repository;
	
	
	/*
	@Transactional(readOnly = true)
	public Page<EventDTO> findAllPaged(PageRequest pageRequest){
		Page<Event> categoryList = repository.findAll(pageRequest);
		return categoryList.map(x -> new EventDTO(x));
	}
	*/
	
	@Transactional(readOnly = true)
	public Page<EventDTO> findAllPaged(Pageable pageable){
		Page<Event> categoryList = repository.findAll(pageable);
		return categoryList.map(x -> new EventDTO(x));
	}
	
	@Transactional(readOnly = true)
	public EventDTO findById(Long id){
		Optional<Event> category = repository.findById(id);
		return new EventDTO(category.orElseThrow(() -> new ResourceNotFoundException("Entity not found")));
	}
	
	@Transactional
	public EventDTO insert(EventDTO dto) {
		Event category = new Event();
		category.setName(dto.getName());
		category = repository.save(category);
		return new EventDTO(category);
	}
	
	@Transactional
	public EventDTO update(Long id, EventDTO dto) {
		try {
			Event entity = repository.getReferenceById(id);
			entity.setName(dto.getName());
			entity = repository.save(entity);
			return new EventDTO(entity);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("ID not found: " + id);	
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
		try {
			repository.deleteById(id);    		
		}
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Falha de integridade referencial");
		}
	}
}

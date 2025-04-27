package com.devsuperior.bds04.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds04.dto.CityDTO;
import com.devsuperior.bds04.dto.EventDTO;
import com.devsuperior.bds04.entities.City;
import com.devsuperior.bds04.entities.Event;
import com.devsuperior.bds04.repositories.CityRepository;
import com.devsuperior.bds04.repositories.EventRepository;
import com.devsuperior.bds04.services.exceptions.DatabaseException;
import com.devsuperior.bds04.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CityService {
	
	@Autowired
	private CityRepository repository;
	
	@Autowired
	private EventRepository eventRepository;
	
	/*
	@Transactional(readOnly = true)
	public Page<CityDTO> findAllPaged(PageRequest pageRequest){
		Page<City> CityList = repository.findAll(pageRequest);
		return CityList.map(x -> new CityDTO(x));
	}
	*/
	
	@Transactional(readOnly = true)
	public List<CityDTO> findAll(){
		List<City> CityList = repository.findAll(Sort.by("name"));
		return CityList.stream().map(x -> new CityDTO(x, x.getEvents())).toList();
	}
	
	@Transactional(readOnly = true)
	public CityDTO findById(Long id){
		Optional<City> product = repository.findById(id);
		City entity = product.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new CityDTO(entity, entity.getEvents());
	}
	
	@Transactional
	public CityDTO insert(CityDTO dto) {
		City entity = new City();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new CityDTO(entity, entity.getEvents());
	}
	

	@Transactional
	public CityDTO update(Long id, CityDTO dto) {
		try {
			City entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new CityDTO(entity, entity.getEvents());
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
	
	private void copyDtoToEntity(CityDTO dto, City entity) {
		entity.setName(dto.getName());
		entity.getEvents().clear();
		for(EventDTO catDto : dto.getEvents()) {
			Event event = eventRepository.getReferenceById(catDto.getId());
			entity.getEvents().add(event);
		}	
	}
}

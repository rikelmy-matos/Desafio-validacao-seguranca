package com.devsuperior.bds04.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.devsuperior.bds04.entities.City;
import com.devsuperior.bds04.entities.Event;

import jakarta.validation.constraints.NotBlank;

public class CityDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	@NotBlank(message = "Campo requerido")
	private String name;
	
	private List<EventDTO> events = new ArrayList<>();
	
	public CityDTO() {
	}

	public CityDTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public CityDTO(City entity) {
		id = entity.getId();
		name = entity.getName();
	}
	
	public CityDTO(City entity, List<Event> categories) {
		this(entity);
		categories.forEach(x -> this.events.add(new EventDTO(x)));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EventDTO> getEvents() {
		return events;
	}

	public void setEvents(List<EventDTO> events) {
		this.events = events;
	}
}

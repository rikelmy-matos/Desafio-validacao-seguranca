package com.devsuperior.bds04.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds04.dto.RoleDTO;
import com.devsuperior.bds04.dto.UserDTO;
import com.devsuperior.bds04.dto.UserInsertDTO;
import com.devsuperior.bds04.dto.UserUpdateDTO;
import com.devsuperior.bds04.entities.Role;
import com.devsuperior.bds04.entities.User;
import com.devsuperior.bds04.projections.UserDetailsProjection;
import com.devsuperior.bds04.repositories.RoleRepository;
import com.devsuperior.bds04.repositories.UserRepository;
import com.devsuperior.bds04.services.exceptions.DatabaseException;
import com.devsuperior.bds04.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService implements UserDetailsService{
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	
	/*
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
		Page<Product> ProductList = repository.findAll(pageRequest);
		return ProductList.map(x -> new ProductDTO(x));
	}
	*/
	
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable){
		Page<User> ProductList = repository.findAll(pageable);
		return ProductList.map(x -> new UserDTO(x));
	}
	
	@Transactional(readOnly = true)
	public UserDTO findById(Long id){
		Optional<User> product = repository.findById(id);
		User entity = product.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new UserDTO(entity);
	}
	
	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		copyDtoToEntity(dto, entity);
		entity.setPassword(passwordEncoder.encode(dto.getPassword()));
		entity = repository.save(entity);
		return new UserDTO(entity);
	}
	

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {
		try {
			User entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new UserDTO(entity);
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
	
	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setEmail(dto.getEmail());
		entity.getRoles().clear();
		for(RoleDTO roleDTO : dto.getRoles()) {
			Role role = roleRepository.getReferenceById(roleDTO.getId());
			entity.getRoles().add(role);
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
		
		if(result.size() == 0) {
			throw new UsernameNotFoundException("User not found");
		}
		User user =  new User();
		user.setEmail(username);
		user.setPassword(result.get(0).getPassword());
		for(UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
			
		}
		return user;
	}
}

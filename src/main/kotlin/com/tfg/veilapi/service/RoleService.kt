package com.tfg.veilapi.service

import com.tfg.veilapi.model.Role
import com.tfg.veilapi.repository.RoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.annotation.PostConstruct

@Service
class RoleService(private val roleRepository: RoleRepository) {

    @PostConstruct
    @Transactional
    fun initialize() {
        if (!roleRepository.existsById("USER")) {
            roleRepository.save(Role(name = "USER"))
        }
        if (!roleRepository.existsById("ADMIN")) {
            roleRepository.save(Role(name = "ADMIN"))
        }
    }

    fun findByName(name: String): Role {
        return roleRepository.findById(name).orElseThrow { RuntimeException("Role not found: $name") }
    }
}

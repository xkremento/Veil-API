package com.tfg.veilapi.model

import jakarta.persistence.*

@Entity
@Table(name = "roles")
data class Role(
    @Id @Column(length = 20) val name: String = "",

    @ManyToMany(mappedBy = "roles") val players: MutableSet<Player> = mutableSetOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Role
        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "Role(name='$name')"
    }
}
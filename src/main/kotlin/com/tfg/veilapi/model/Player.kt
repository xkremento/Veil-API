package com.tfg.veilapi.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import lombok.EqualsAndHashCode

@EqualsAndHashCode(
    onlyExplicitlyIncluded = true,
    exclude = ["friends", "sentFriendRequests", "receivedFriendRequests", "playerGames", "roles"]
)
@Entity
@Table(
    indexes = [
        Index(name = "idx_player_nickname", columnList = "nickname")
    ]
)
data class Player(
    @Id @Email @Column(length = 254, unique = true) @EqualsAndHashCode.Include val email: String = "",

    @NotBlank @Column(length = 30, unique = true) val nickname: String = "",

    @NotBlank @Column(length = 128) val password: String = "",

    val coins: Int = 0,

    @Column(length = 2048) val skinUrl: String? = null,

    @Column(length = 2048) val profileImageUrl: String? = null,

    @ManyToMany @JoinTable(
        name = "friends",
        joinColumns = [JoinColumn(name = "player_email")],
        inverseJoinColumns = [JoinColumn(name = "friend_email")]
    ) val friends: MutableSet<Player> = mutableSetOf(),

    @OneToMany(
        mappedBy = "requester",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    ) val sentFriendRequests: MutableSet<FriendRequest> = mutableSetOf(),

    @OneToMany(
        mappedBy = "player",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    ) val receivedFriendRequests: MutableSet<FriendRequest> = mutableSetOf(),

    @OneToMany(
        mappedBy = "player",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    ) val playerGames: MutableSet<PlayerGame> = mutableSetOf(),

    @ManyToMany(fetch = FetchType.EAGER) @JoinTable(
        name = "player_roles",
        joinColumns = [JoinColumn(name = "player_email")],
        inverseJoinColumns = [JoinColumn(name = "role_name")]
    ) val roles: MutableSet<Role> = mutableSetOf()
) {
    override fun toString(): String {
        return "Player(email='$email', nickname='$nickname', coins=$coins, skinUrl=$skinUrl, profileImageUrl=$profileImageUrl)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Player

        return email == other.email
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }
}
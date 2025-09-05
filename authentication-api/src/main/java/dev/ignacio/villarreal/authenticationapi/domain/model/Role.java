package dev.ignacio.villarreal.authenticationapi.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Audited
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "role",  cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.LAZY)
    private Set<UserRole> userRoles = new HashSet<>();
}

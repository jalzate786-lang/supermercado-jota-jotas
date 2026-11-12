
package org.example.supermercadojotajotas.repository;

import org.example.supermercadojotajotas.model.RegistroEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroEntregaRepository extends JpaRepository<RegistroEntrega, Integer> {
}

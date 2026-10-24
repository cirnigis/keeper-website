
package vn.ubm.usedbookmarketplace.repository;

import vn.ubm.usedbookmarketplace.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	boolean existsByEmailIgnoreCase(String email);

	@Query("""
			    SELECT DISTINCT u
			    FROM AppUser u
			    LEFT JOIN FETCH u.roles
			    WHERE LOWER(u.email) = LOWER(:email)
			""")
	Optional<AppUser> findByEmailWithRoles(@Param("email") String email);

	Optional<AppUser> findByEmailIgnoreCase(String email);
}

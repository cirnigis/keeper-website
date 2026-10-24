
package vn.ubm.usedbookmarketplace.service;

import vn.ubm.usedbookmarketplace.entity.AppUser;
import vn.ubm.usedbookmarketplace.repository.AppUserRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarketplaceUserDetailsService implements UserDetailsService {

	private final AppUserRepository appUserRepository;

	public MarketplaceUserDetailsService(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		AppUser account = appUserRepository.findByEmailWithRoles(email.trim())
				.orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

		boolean active = "ACTIVE".equals(account.getAccountStatus());
		boolean locked = "LOCKED".equals(account.getAccountStatus());

		String[] authorities = account.getRoles().stream().map(role -> "ROLE_" + role.getRoleCode())
				.toArray(String[]::new);

		return User.withUsername(account.getEmail()).password(account.getPasswordHash()).authorities(authorities)
				.disabled(!active).accountLocked(locked).build();
	}
}

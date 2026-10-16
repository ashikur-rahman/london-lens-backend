
package uk.co.londonlens.security;

import java.util.List;
import java.util.Locale;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private static final String FIND_USER_SQL = """
        SELECT
            u.email,
            u.password_hash,
            u.status,
            r.name AS role_name
        FROM app_users u
        JOIN user_roles ur ON ur.user_id = u.id
        JOIN roles r ON r.id = ur.role_id
        WHERE LOWER(u.email) = ?
          AND u.password_hash IS NOT NULL
        ORDER BY r.name
        """;

    private final JdbcTemplate jdbcTemplate;

    public DatabaseUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        String email = username == null
            ? ""
            : username.trim().toLowerCase(Locale.ROOT);

        List<UserRoleRecord> records = jdbcTemplate.query(
            FIND_USER_SQL,
            (rs, rowNum) -> new UserRoleRecord(
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("status"),
                rs.getString("role_name")
            ),
            email
        );

        if (records.isEmpty()) {
            throw new UsernameNotFoundException(
                "User not found or no role assigned"
            );
        }

        UserRoleRecord user = records.get(0);

        String[] authorities = records.stream()
            .map(UserRoleRecord::roleName)
            .distinct()
            .map(DatabaseUserDetailsService::toAuthority)
            .toArray(String[]::new);

        boolean active = "ACTIVE".equalsIgnoreCase(user.status());

        return org.springframework.security.core.userdetails.User
            .withUsername(user.email())
            .password(user.passwordHash())
            .authorities(authorities)
            .disabled(!active)
            .build();
    }

    private static String toAuthority(String roleName) {
        return roleName.startsWith("ROLE_")
            ? roleName
            : "ROLE_" + roleName;
    }

    private record UserRoleRecord(
        String email,
        String passwordHash,
        String status,
        String roleName
    ) {}
}

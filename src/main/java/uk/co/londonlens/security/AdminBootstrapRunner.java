
package uk.co.londonlens.security;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@ConditionalOnProperty(
    prefix = "app.security.bootstrap-admin",
    name = "enabled",
    havingValue = "true"
)
public class AdminBootstrapRunner implements ApplicationRunner {

    private static final Logger log =
        LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;

    @Value("${BOOTSTRAP_ADMIN_EMAIL:}")
    private String configuredEmail;

    @Value("${BOOTSTRAP_ADMIN_PASSWORD:}")
    private String configuredPassword;

    public AdminBootstrapRunner(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder,
            TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        String email = configuredEmail == null
            ? ""
            : configuredEmail.trim().toLowerCase(Locale.ROOT);

        String password = configuredPassword == null
            ? ""
            : configuredPassword;

        validateCredentials(email, password);

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            List<Long> existingUsers = jdbcTemplate.query(
                "SELECT id FROM app_users WHERE LOWER(email) = ?",
                (rs, rowNum) -> rs.getLong("id"),
                email
            );

            if (!existingUsers.isEmpty()) {
                throw new IllegalStateException(
                    "An account with this email already exists. "
                    + "Bootstrap stopped without changing that account."
                );
            }

            Long adminRoleId = jdbcTemplate.queryForObject(
                "SELECT id FROM roles WHERE name = ?",
                Long.class,
                "ADMIN"
            );

            Long userId = jdbcTemplate.queryForObject("""
                INSERT INTO app_users (
                    email,
                    password_hash,
                    first_name,
                    last_name,
                    status
                )
                VALUES (?, ?, ?, ?, 'ACTIVE')
                RETURNING id
                """,
                Long.class,
                email,
                passwordEncoder.encode(password),
                "London Lens",
                "Administrator"
            );

            jdbcTemplate.update("""
                INSERT INTO user_roles (user_id, role_id)
                VALUES (?, ?)
                """,
                userId,
                adminRoleId
            );
        });

        log.info(
            "Bootstrap administrator created successfully. "
            + "Stop the application and restart without bootstrap enabled."
        );
    }

    private void validateCredentials(String email, String password) {
        if (email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException(
                "Set BOOTSTRAP_ADMIN_EMAIL to a valid email address."
            );
        }

        if (password.length() < 16) {
            throw new IllegalArgumentException(
                "The bootstrap password must contain at least 16 characters."
            );
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException(
                "The bootstrap password must not exceed 72 UTF-8 bytes."
            );
        }
    }
}

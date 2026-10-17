
package uk.co.londonlens.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void anonymousVisitorIsRedirectedFromAdminDashboard()
            throws Exception {

        mockMvc.perform(get("/admin/dashboard"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string(
                "Location",
                containsString("/login")
            ));
    }

    @Test
    void anonymousVisitorCannotViewProductManagement()
            throws Exception {

        mockMvc.perform(get("/admin/products"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string(
                "Location",
                containsString("/login")
            ));
    }

    @Test
    void customerCannotAccessAdminProductManagement()
            throws Exception {

        mockMvc.perform(
                get("/admin/products")
                    .with(user("customer@example.com")
                        .roles("CUSTOMER"))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    void administratorCanViewProductManagement()
            throws Exception {

        mockMvc.perform(
                get("/admin/products")
                    .with(user("admin@example.com")
                        .roles("ADMIN"))
            )
            .andExpect(status().isOk());
    }

    @Test
    void productCreationRequiresCsrfToken()
            throws Exception {

        mockMvc.perform(
                post("/admin/products")
                    .with(user("admin@example.com")
                        .roles("ADMIN"))
                    .param("name", "Security Test Product")
                    .param("slug", "security-test-product")
                    .param("productType", "DIGITAL")
                    .param("priceMinor", "599")
                    .param("currency", "GBP")
                    .param("status", "DRAFT")
            )
            .andExpect(status().isForbidden());
    }

    @Test
    void customerCannotCreateProductEvenWithCsrfToken()
            throws Exception {

        mockMvc.perform(
                post("/admin/products")
                    .with(user("customer@example.com")
                        .roles("CUSTOMER"))
                    .with(csrf())
                    .param("name", "Unauthorised Product")
                    .param("slug", "unauthorised-product")
                    .param("productType", "DIGITAL")
                    .param("priceMinor", "599")
                    .param("currency", "GBP")
                    .param("status", "DRAFT")
            )
            .andExpect(status().isForbidden());
    }

    @Test
    void administratorCannotArchiveWithoutCsrfToken()
            throws Exception {

        mockMvc.perform(
                post("/admin/products/1/archive")
                    .with(user("admin@example.com").roles("ADMIN"))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    void customerCannotPublishEvenWithCsrfToken()
            throws Exception {

        mockMvc.perform(
                post("/admin/products/1/publish")
                    .with(user("customer@example.com").roles("CUSTOMER"))
                    .with(csrf())
            )
            .andExpect(status().isForbidden());
    }

    
}

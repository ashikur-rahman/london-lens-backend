
package uk.co.londonlens.catalogue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PublicProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnOnlyActiveProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(
                MediaType.APPLICATION_JSON
            ))
            .andExpect(jsonPath("$[*].slug").value(
                containsInAnyOrder(
                    "hidden-london-walking-guide",
                    "london-photography-spot-map"
                )
            ))
            .andExpect(content().string(
                not(containsString("weekend-london-itinerary"))
            ));
    }

    @Test
    void shouldReturnActiveProductDetails() throws Exception {
        mockMvc.perform(get(
                "/api/v1/products/{slug}",
                "hidden-london-walking-guide"
            ))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.slug").value(
                "hidden-london-walking-guide"
            ))
            .andExpect(jsonPath("$.name").value(
                "Hidden London Walking Guide"
            ))
            .andExpect(jsonPath("$.priceMinor").value(599))
            .andExpect(jsonPath("$.currency").value("GBP"));
    }

    @Test
    void shouldHideDraftProducts() throws Exception {
        mockMvc.perform(get(
                "/api/v1/products/{slug}",
                "weekend-london-itinerary"
            ))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForUnknownProduct() throws Exception {
        mockMvc.perform(get(
                "/api/v1/products/{slug}",
                "product-that-does-not-exist"
            ))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldKeepHomepageAvailable() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());
    }
}

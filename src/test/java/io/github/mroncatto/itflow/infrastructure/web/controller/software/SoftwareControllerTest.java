package io.github.mroncatto.itflow.infrastructure.web.controller.software;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mroncatto.itflow.ItflowApiApplication;
import io.github.mroncatto.itflow.application.config.constant.EndpointUrlConstant;
import io.github.mroncatto.itflow.domain.software.dto.SoftwareRequestDto;
import io.github.mroncatto.itflow.domain.software.dto.SoftwareLicenseRequestDto;
import io.github.mroncatto.itflow.domain.software.entity.Software;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ItflowApiApplication.class
)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(profiles = "test")
class SoftwareControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    @DisplayName("Should create a software and return code 201")
    @WithMockUser(username = "admin", authorities = "HELPDESK")
    void save() throws Exception {
        final String softwareName = "Software test";
        RequestBuilder request = MockMvcRequestBuilders.post(EndpointUrlConstant.COMPUTER_SOFTWARE)
                .content(objectMapper.writeValueAsString(
                        SoftwareRequestDto.builder()
                                .name(softwareName)
                                .build()))
                .contentType(APPLICATION_JSON_VALUE);
        mvc.perform(request).andExpectAll(
                MockMvcResultMatchers.status().isCreated(),
                MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("name", Is.is(softwareName))
        );
    }

    @Test
    @Order(2)
    @DisplayName("Should update a software and return code 200")
    @WithMockUser(username = "admin", authorities = "HELPDESK")
    void update() throws Exception {
        final String softwareName = "Software test edit";
        RequestBuilder request = MockMvcRequestBuilders.put(EndpointUrlConstant.COMPUTER_SOFTWARE)
                .content(objectMapper.writeValueAsString(
                        Software.builder()
                                .id(1L)
                                .name(softwareName)
                                .build()))
                .contentType(APPLICATION_JSON_VALUE);
        mvc.perform(request).andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("name", Is.is(softwareName))
        );
    }

    @Test
    @Order(3)
    @DisplayName("Should find all software and return not empty list and code 200")
    @WithMockUser(username = "admin")
    void findAll() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get(EndpointUrlConstant.COMPUTER_SOFTWARE);
        mvc.perform(request).andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$").exists()
        );
    }

    @Test
    @Order(4)
    @DisplayName("Should find all software and return page format and code 200")
    @WithMockUser(username = "admin")
    void findAllPagination() throws Exception {
        final String page = "/page/1";
        RequestBuilder request = MockMvcRequestBuilders.get(EndpointUrlConstant.COMPUTER_SOFTWARE + page);
        mvc.perform(request).andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("pageable.pageNumber", Is.is(1)));
    }

    @Test
    @Order(5)
    @DisplayName("Should find a software by ID and return code 200")
    @WithMockUser(username = "admin")
    void findById() throws Exception {
        final String softwareName = "Software test edit";
        final String id = "/1";
        RequestBuilder request = MockMvcRequestBuilders.get(EndpointUrlConstant.COMPUTER_SOFTWARE + id);
        mvc.perform(request).andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("name", Is.is(softwareName))
        );
    }

    @Test
    @Order(6)
    @DisplayName("Should add license to software and return code 201")
    @WithMockUser(username = "admin", authorities = "HELPDESK")
    void addLicense() throws Exception {
        final String licenseDesc = "License test";
        final String endpoint = "/1/license";
        RequestBuilder request = MockMvcRequestBuilders.post(EndpointUrlConstant.COMPUTER_SOFTWARE + endpoint)
                .content(objectMapper.writeValueAsString(
                        SoftwareLicenseRequestDto.builder()
                                .active(true)
                                .description(licenseDesc)
                                .build()))
                .contentType(APPLICATION_JSON_VALUE);
        mvc.perform(request).andExpectAll(
                MockMvcResultMatchers.status().isCreated(),
                MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.licenses[0].description", Is.is(licenseDesc))
        );
    }

    @Test
    @Order(7)
    @DisplayName("Should find and disable a software by ID and return code 200")
    @WithMockUser(username = "admin", authorities = "HELPDESK")
    void deleteById() throws Exception {
        final String id = "/1";
        RequestBuilder request = MockMvcRequestBuilders.delete(EndpointUrlConstant.COMPUTER_SOFTWARE + id);
        mvc.perform(request).andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("active", Is.is(false))
        );
    }
}
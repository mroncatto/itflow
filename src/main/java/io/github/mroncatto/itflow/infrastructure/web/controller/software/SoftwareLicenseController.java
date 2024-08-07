package io.github.mroncatto.itflow.infrastructure.web.controller.software;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.mroncatto.itflow.application.config.constant.EndpointUrlConstant;
import io.github.mroncatto.itflow.domain.commons.exception.BadRequestException;
import io.github.mroncatto.itflow.domain.computer.dto.ComputerSoftwareRequestDto;
import io.github.mroncatto.itflow.domain.device.entity.DeviceComputerSoftware;
import io.github.mroncatto.itflow.domain.software.dto.LicenseKeyRequestDto;
import io.github.mroncatto.itflow.domain.software.dto.SoftwareLicenseRequestDto;
import io.github.mroncatto.itflow.domain.software.entity.SoftwareLicense;
import io.github.mroncatto.itflow.domain.software.entity.SoftwareLicenseKey;
import io.github.mroncatto.itflow.domain.software.model.ISoftwareLicenseService;
import io.github.mroncatto.itflow.infrastructure.web.advice.CustomHttpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.github.mroncatto.itflow.application.config.constant.ControllerConstant.PAGE_SIZE;
import static io.github.mroncatto.itflow.domain.commons.helper.SwaggerPropertiesHelper.*;
import static io.github.mroncatto.itflow.domain.user.helper.RolesHelper.HELPDESK_OR_COORDINATOR_OR_MANAGER_OR_ADMIN;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = EndpointUrlConstant.COMPUTER_LICENSE)
@Tag(name = "Computer", description = "Computer properties")
@RequiredArgsConstructor
public class SoftwareLicenseController {
    private final ISoftwareLicenseService service;

    @Operation(summary = "Get all software licenses", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = SoftwareLicense.class)))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @GetMapping()
    public ResponseEntity<List<SoftwareLicense>> findAll() {
        return new ResponseEntity<>(this.service.findAll(), OK);
    }

    @Operation(summary = "Get all software licenses with pagination", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @GetMapping(EndpointUrlConstant.PAGE)
    public ResponseEntity<Page<SoftwareLicense>> findAll(@PathVariable("page") int page, String filter) {
        return new ResponseEntity<>(this.service.findAll(PageRequest.of(page, PAGE_SIZE), filter), OK);
    }

    @Operation(summary = "Create a new software license", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_201, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = SoftwareLicense.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = BAD_REQUEST, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = CREATED)
    @PostMapping()
    @PreAuthorize(HELPDESK_OR_COORDINATOR_OR_MANAGER_OR_ADMIN)
    public ResponseEntity<SoftwareLicense> save(@RequestBody @Validated(SoftwareLicenseRequestDto.SoftwareLicenseView.SoftwareLicensePost.class)
                                                @JsonView(SoftwareLicenseRequestDto.SoftwareLicenseView.SoftwareLicensePost.class) SoftwareLicenseRequestDto licenseDto, BindingResult result) throws BadRequestException {
        return new ResponseEntity<>(this.service.save(licenseDto, result), CREATED);
    }

    @Operation(summary = "Update a specific software license", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = SoftwareLicense.class))),
            @ApiResponse(responseCode = RESPONSE_400, description = BAD_REQUEST, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = NOT_FOUND, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @PutMapping()
    @PreAuthorize(HELPDESK_OR_COORDINATOR_OR_MANAGER_OR_ADMIN)
    public ResponseEntity<SoftwareLicense> update(@RequestBody @Validated(SoftwareLicenseRequestDto.SoftwareLicenseView.SoftwareLicensePost.class)
                                                  @JsonView(SoftwareLicenseRequestDto.SoftwareLicenseView.SoftwareLicensePost.class) SoftwareLicenseRequestDto licenseDto, BindingResult result) throws BadRequestException, NoResultException {
        return new ResponseEntity<>(this.service.update(licenseDto, result), OK);
    }

    @Operation(summary = "Get software license by ID", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = SoftwareLicense.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = NOT_FOUND, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @GetMapping(EndpointUrlConstant.ID)
    public ResponseEntity<SoftwareLicense> findById(@PathVariable("id") Long id) throws NoResultException {
        return new ResponseEntity<>(this.service.findById(id), OK);
    }

    @Operation(summary = "Disable a software license by ID", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = SoftwareLicense.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = NOT_FOUND, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @DeleteMapping(EndpointUrlConstant.ID)
    @PreAuthorize(HELPDESK_OR_COORDINATOR_OR_MANAGER_OR_ADMIN)
    public ResponseEntity<SoftwareLicense> deleteById(@PathVariable("id") Long id) throws NoResultException {
        return new ResponseEntity<>(this.service.deleteById(id), OK);
    }

    @Operation(summary = "Add a key to software license", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_201, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = SoftwareLicense.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = BAD_REQUEST, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = CREATED)
    @PostMapping(EndpointUrlConstant.UPDATE_LICENSE_KEY)
    @PreAuthorize(HELPDESK_OR_COORDINATOR_OR_MANAGER_OR_ADMIN)
    public ResponseEntity<SoftwareLicense> addLicenseKey(@PathVariable("id") Long id,
                                                         @RequestBody @Validated(LicenseKeyRequestDto.LicenseKeyView.LicenseKeyPost.class)
                                                         @JsonView(LicenseKeyRequestDto.LicenseKeyView.LicenseKeyPost.class) LicenseKeyRequestDto licenseKeyRequestDto, BindingResult result) throws NoResultException, BadRequestException {
        return new ResponseEntity<>(this.service.addLicenseKey(id, licenseKeyRequestDto, result), CREATED);
    }

    @Operation(summary = "Remove a key from software license", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = SoftwareLicense.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = NOT_FOUND, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @PutMapping (EndpointUrlConstant.UPDATE_LICENSE_KEY)
    @PreAuthorize(HELPDESK_OR_COORDINATOR_OR_MANAGER_OR_ADMIN)
    public ResponseEntity<SoftwareLicense> removeLicenseKey(@PathVariable("id") Long id,
                                                            @RequestBody @Validated(LicenseKeyRequestDto.LicenseKeyView.LicenseKeyPut.class)
                                                            @JsonView(LicenseKeyRequestDto.LicenseKeyView.LicenseKeyPut.class) LicenseKeyRequestDto licenseKeyRequestDto, BindingResult result) throws NoResultException, BadRequestException {
        return new ResponseEntity<>(this.service.removeLicenseKey(id, licenseKeyRequestDto, result), OK);
    }

    @Operation(summary = "Get a license key by ID", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = SoftwareLicense.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = NOT_FOUND, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @GetMapping(EndpointUrlConstant.LICENSE_KEY_ASSIGNMENTS)
    public ResponseEntity<SoftwareLicenseKey> findLicenseKeyById(@PathVariable("id") Long id) throws NoResultException {
        return new ResponseEntity<>(this.service.findLicenseKey(id), OK);
    }

    @Operation(summary = "Assign computer device to a license key", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_201, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = DeviceComputerSoftware.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = BAD_REQUEST, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = CREATED)
    @PostMapping(EndpointUrlConstant.LICENSE_KEY_ADD_ASSIGNMENT)
    @PreAuthorize(HELPDESK_OR_COORDINATOR_OR_MANAGER_OR_ADMIN)
    public ResponseEntity<DeviceComputerSoftware> addAssignLicenseKey(@PathVariable("id") Long id,
                                                         @RequestBody @Validated(ComputerSoftwareRequestDto.ComputerSoftwareView.ComputerSoftwarePost.class)
                                                         @JsonView(ComputerSoftwareRequestDto.ComputerSoftwareView.ComputerSoftwarePost.class) ComputerSoftwareRequestDto computerSoftwareRequestDto, BindingResult result) throws NoResultException, BadRequestException {
        return new ResponseEntity<>(this.service.addLicenseKeyAssignment(id, computerSoftwareRequestDto, result), CREATED);
    }
}

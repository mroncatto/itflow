package io.github.mroncatto.itflow.infrastructure.web.controller.software;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.mroncatto.itflow.application.config.constant.EndpointUrlConstant;
import io.github.mroncatto.itflow.domain.commons.exception.BadRequestException;
import io.github.mroncatto.itflow.domain.computer.entity.ComputerStorage;
import io.github.mroncatto.itflow.domain.device.dto.DeviceComputerResponseDto;
import io.github.mroncatto.itflow.domain.device.entity.DeviceComputer;
import io.github.mroncatto.itflow.domain.device.entity.DeviceComputerSoftware;
import io.github.mroncatto.itflow.domain.device.model.IDeviceComputerService;
import io.github.mroncatto.itflow.domain.software.dto.SoftwareListResponseDto;
import io.github.mroncatto.itflow.domain.software.dto.SoftwareRequestDto;
import io.github.mroncatto.itflow.domain.software.dto.SoftwareLicenseRequestDto;
import io.github.mroncatto.itflow.domain.software.entity.Software;
import io.github.mroncatto.itflow.domain.software.model.ISoftwareService;
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
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.EqualIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.domain.IsTrue;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Conjunction;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
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
@RequestMapping(value = EndpointUrlConstant.COMPUTER_SOFTWARE)
@Tag(name = "Computer", description = "Computer properties")
@RequiredArgsConstructor
public class SoftwareController {
    private final ISoftwareService service;
    private final IDeviceComputerService computerService;

    @Operation(summary = "Get all software", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = Software.class)))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @GetMapping()
    public ResponseEntity<List<Software>> findAll() {
        return new ResponseEntity<>(this.service.findAll(), OK);
    }

    @Operation(summary = "Get all software with pagination", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @GetMapping(EndpointUrlConstant.PAGE)
    public ResponseEntity<Page<Software>> findAll(@PathVariable("page") int page, @RequestParam(required = false, name = "filter") String filter) {
        return new ResponseEntity<>(this.service.findAll(PageRequest.of(page, PAGE_SIZE), filter), OK);
    }

    @Operation(summary = "Create a new software", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_201, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Software.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = BAD_REQUEST, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = CREATED)
    @PostMapping()
    @PreAuthorize(HELPDESK_OR_COORDINATOR_OR_MANAGER_OR_ADMIN)
    public ResponseEntity<Software> save(@RequestBody @Validated(SoftwareRequestDto.SoftwareView.SoftwarePost.class)
                                         @JsonView(SoftwareRequestDto.SoftwareView.SoftwarePost.class) SoftwareRequestDto softwareRequestDto, BindingResult result) throws BadRequestException {
        return new ResponseEntity<>(this.service.save(softwareRequestDto, result), CREATED);
    }

    @Operation(summary = "Update a specific software", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Software.class))),
            @ApiResponse(responseCode = RESPONSE_400, description = BAD_REQUEST, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = NOT_FOUND, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @PutMapping()
    @PreAuthorize(HELPDESK_OR_COORDINATOR_OR_MANAGER_OR_ADMIN)
    public ResponseEntity<Software> update(@RequestBody @Validated(SoftwareRequestDto.SoftwareView.SoftwarePut.class)
                                           @JsonView(SoftwareRequestDto.SoftwareView.SoftwarePut.class) SoftwareRequestDto softwareRequestDto, BindingResult result) throws BadRequestException, NoResultException {
        return new ResponseEntity<>(this.service.update(softwareRequestDto, result), OK);
    }

    @Operation(summary = "Get software by ID", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Software.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = NOT_FOUND, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @GetMapping(EndpointUrlConstant.ID)
    public ResponseEntity<Software> findById(@PathVariable("id") Long id) throws NoResultException {
        return new ResponseEntity<>(this.service.findById(id), OK);
    }

    @Operation(summary = "Disable a software by ID", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Software.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = NOT_FOUND, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @DeleteMapping(EndpointUrlConstant.ID)
    @PreAuthorize(HELPDESK_OR_COORDINATOR_OR_MANAGER_OR_ADMIN)
    public ResponseEntity<Software> deleteById(@PathVariable("id") Long id) throws NoResultException {
        return new ResponseEntity<>(this.service.deleteById(id), OK);
    }

    @Operation(summary = "add license to software", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_201, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Software.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = BAD_REQUEST, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = CREATED)
    @PostMapping(EndpointUrlConstant.UPDATE_SOFTWARE_LICENSE)
    @PreAuthorize(HELPDESK_OR_COORDINATOR_OR_MANAGER_OR_ADMIN)
    public ResponseEntity<Software> addLicense(@PathVariable("id") Long id,
                                               @RequestBody @Validated(SoftwareLicenseRequestDto.SoftwareLicenseView.SoftwareLicenseAddLicense.class)
                                               @JsonView(SoftwareLicenseRequestDto.SoftwareLicenseView.SoftwareLicenseAddLicense.class) SoftwareLicenseRequestDto licenseDto, BindingResult result) throws NoResultException, BadRequestException {
        return new ResponseEntity<>(this.service.addLicense(id, licenseDto, result), CREATED);
    }

    @Operation(summary = "Get all software autocomplete filter", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = ComputerStorage.class)))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @GetMapping(EndpointUrlConstant.AUTO_COMPLETE)
    public ResponseEntity<List<SoftwareListResponseDto>> findAllAutoComplete(
            @Conjunction(value = {
                    @Or({
                            @Spec(path = "id", params = "filter", spec = Equal.class),
                            @Spec(path = "name", params = "filter", spec = LikeIgnoreCase.class),
                            @Spec(path = "developer", params = "filter", spec = LikeIgnoreCase.class),
                    }),
            }, and = {
                    @Spec(path = "active", defaultVal = "true" ,spec = IsTrue.class)
            })
            Specification<Software> spec) {
        return new ResponseEntity<>(this.service.findAll(spec), OK);
    }

    @Operation(summary = "Get all Device Computer autocomplete filter by software", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = DeviceComputerResponseDto.class)))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @GetMapping(EndpointUrlConstant.DEVICE_COMPUTER_AUTOCOMPLETE)
    public ResponseEntity<List<DeviceComputerSoftware>> findAll(
            @Conjunction(value = {
                    @Or({
                            @Spec(path = "deviceComputer.description", params = "filter", spec = LikeIgnoreCase.class),
                            @Spec(path = "deviceComputer.device.hostname", params = "filter", spec = LikeIgnoreCase.class),
                            @Spec(path = "deviceComputer.device.description", params = "filter", spec = LikeIgnoreCase.class),
                            @Spec(path = "deviceComputer.device.code", params = "filter", spec = EqualIgnoreCase.class),
                            @Spec(path = "deviceComputer.device.tag", params = "filter", spec = EqualIgnoreCase.class),
                            @Spec(path = "deviceComputer.id", params = "filter", spec = Equal.class)
                    }),
            }, and = {
                    @Spec(path = "software.id", params = "software", spec = Equal.class),
            })
            Specification<DeviceComputerSoftware> spec) {
        return new ResponseEntity<>(this.computerService.findDeviceComputerBySoftware(spec), OK);
    }

    /**
     * TODO:
     * - FALTA CRIAR UM AUTOCOMPLETE QUE CARREGUE OS DEVICE COMPUTER
     * EM BASE A LISTA DE DEVICECOMPUTERSOFTWARE (COMPUTADORES COM UM SOFTWARE ESPECIFICO)
     * POIS NESSA ENTITY E ATRIBUIDA A CLAVE !!!
     * - DEPOIS AJUSTAR O PUT PARA ADICIONAR OU REMOVER A CHAVE !!
     */




}

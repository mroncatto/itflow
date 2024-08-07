package io.github.mroncatto.itflow.infrastructure.web.controller.user;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.mroncatto.itflow.application.config.constant.EndpointUrlConstant;
import io.github.mroncatto.itflow.domain.commons.exception.BadRequestException;
import io.github.mroncatto.itflow.domain.user.dto.UserProfileRequestDto;
import io.github.mroncatto.itflow.domain.user.dto.UserRequestDto;
import io.github.mroncatto.itflow.domain.user.entity.User;
import io.github.mroncatto.itflow.domain.user.entity.UserRole;
import io.github.mroncatto.itflow.domain.user.exception.AlreadExistingUserByEmail;
import io.github.mroncatto.itflow.domain.user.exception.AlreadExistingUserByUsername;
import io.github.mroncatto.itflow.domain.user.exception.BadPasswordException;
import io.github.mroncatto.itflow.domain.user.model.IRoleService;
import io.github.mroncatto.itflow.domain.user.model.IUserService;
import io.github.mroncatto.itflow.infrastructure.web.advice.CustomHttpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Conjunction;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.github.mroncatto.itflow.domain.commons.helper.SwaggerPropertiesHelper.*;
import static io.github.mroncatto.itflow.domain.user.helper.RolesHelper.ADMIN_ONLY;
import static io.github.mroncatto.itflow.domain.user.helper.RolesHelper.HELPDESK_OR_ADMIN;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = EndpointUrlConstant.USER)
@Tag(name = "User", description = "User accounts")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final IRoleService roleService;

    @Operation(summary = "Get all users with pagination and filters", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = User.class)))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @GetMapping()
    public ResponseEntity<Page<User>> findAll(
            @Conjunction(value = {
                    @Or({
                            @Spec(path = "email", params = "filter", spec = LikeIgnoreCase.class),
                            @Spec(path = "username", params = "filter", spec = LikeIgnoreCase.class),
                            @Spec(path = "fullName", params = "filter", spec = LikeIgnoreCase.class)
                    }),
            }/*, and = {
                    @Spec(path = "active", params = "status", spec = IsTrue.class)
            }*/)
            Specification<User> spec, @PageableDefault(size = 20) Pageable pageable) {
        return new ResponseEntity<>(this.userService.findAll(spec, pageable), OK);
    }

    @Operation(summary = "Get user by username", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = NOT_FOUND, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @GetMapping(EndpointUrlConstant.USERNAME)
    public ResponseEntity<User> findUserByUsername(@PathVariable("username")  String username) {
        return new ResponseEntity<>(this.userService.findUserByUsername(username), OK);
    }

    @Operation(summary = "Create a new user account", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = RESPONSE_400, description = BAD_REQUEST, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = CREATED)
    @PostMapping()
    @PreAuthorize(ADMIN_ONLY)
    public ResponseEntity<User> save(@RequestBody @Validated(UserRequestDto.UserView.UserPost.class)
                                         @JsonView(UserRequestDto.UserView.UserPost.class) UserRequestDto dto, BindingResult result) throws BadRequestException, AlreadExistingUserByUsername, AlreadExistingUserByEmail {
        return new ResponseEntity<>(this.userService.save(dto, result), CREATED);
    }

    @Operation(summary = "Update a specific user account", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = RESPONSE_400, description = BAD_REQUEST, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = NOT_FOUND, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @PutMapping(EndpointUrlConstant.USERNAME)
    @PreAuthorize(HELPDESK_OR_ADMIN)
    public ResponseEntity<User> update(@PathVariable("username") String username,
                                       @RequestBody @Validated(UserRequestDto.UserView.UserPut.class)
                                       @JsonView(UserRequestDto.UserView.UserPut.class) UserRequestDto dto, BindingResult result) throws BadRequestException, AlreadExistingUserByEmail {
        return new ResponseEntity<>(this.userService.update(username, dto, result), OK);
    }

    @Operation(summary = "Inactive a specific user account", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON)),
            @ApiResponse(responseCode = RESPONSE_404, description = NOT_FOUND, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @DeleteMapping(EndpointUrlConstant.USERNAME)
    @PreAuthorize(ADMIN_ONLY)
    public ResponseEntity<User> delete(@PathVariable("username") String username) throws BadRequestException {
        this.userService.delete(username);
        return new ResponseEntity<>(null, OK);
    }

    @Operation(summary = "Get all roles", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = UserRole.class)))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @GetMapping(EndpointUrlConstant.ROLE)
    @PreAuthorize(HELPDESK_OR_ADMIN)
    public ResponseEntity<List<UserRole>> findAllRoles() {
        return new ResponseEntity<>(this.roleService.findAll(), OK);
    }

    @Operation(summary = "Update user roles", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @PutMapping(EndpointUrlConstant.USERNAME_ROLE)
    @PreAuthorize(HELPDESK_OR_ADMIN)
    public ResponseEntity<User> updateUserRoles(@PathVariable("username") String username, @RequestBody List<UserRole> roles) {
        return new ResponseEntity<>(this.userService.updateUserRoles(username, roles), OK);
    }

    @Operation(summary = "Update user profile", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @PutMapping(EndpointUrlConstant.PROFILE)
    public ResponseEntity<User> updateProfile(@RequestBody
                                                  @Validated(UserProfileRequestDto.UserProfileView.ProfileUpdate.class)
                                                  @JsonView(UserProfileRequestDto.UserProfileView.ProfileUpdate.class) UserProfileRequestDto dto) throws AlreadExistingUserByEmail, BadRequestException {
        return new ResponseEntity<>(this.userService.updateProfile(dto), OK);
    }

    @Operation(summary = "Update user password with old password", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @PutMapping(EndpointUrlConstant.UPDATE_PASSWORD)
    public ResponseEntity<Object> updateUserPassword(@RequestParam("oldPassword") String oldPassword,
                                                @RequestParam("newPassword") String newPassword) throws BadPasswordException {
        this.userService.updateUserPassword(oldPassword, newPassword);
        return new ResponseEntity<>(null, OK);
    }

    @Operation(summary = "Reset user password", responses = {@ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = OK)
    @PostMapping(EndpointUrlConstant.RESET_PASSWORD)
    @PreAuthorize(HELPDESK_OR_ADMIN)
    public ResponseEntity<Object> resetUserPassword(@RequestParam("username") String username) {
        this.userService.resetUserPassword(username);
        return new ResponseEntity<>(null, OK);
    }

    @Operation(summary = "Unlock a specific user account", security = {
            @SecurityRequirement(name = BEARER_AUTH)}, responses = {
            @ApiResponse(responseCode = RESPONSE_200, description = SUCCESSFUL, content = @Content(mediaType = APPLICATION_JSON)),
            @ApiResponse(responseCode = RESPONSE_400, description = BAD_REQUEST, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_404, description = NOT_FOUND, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class))),
            @ApiResponse(responseCode = RESPONSE_401, description = UNAUTHORIZED, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CustomHttpResponse.class)))})
    @ResponseStatus(value = CREATED)
    @PutMapping(EndpointUrlConstant.LOCK_UNLOCK_USERNAME)
    @PreAuthorize(HELPDESK_OR_ADMIN)
    public ResponseEntity<Object> lockUnlockUser(@PathVariable("username") String username) {
        this.userService.lockUnlockUser(username);
        return new ResponseEntity<>(null, OK);
    }
}

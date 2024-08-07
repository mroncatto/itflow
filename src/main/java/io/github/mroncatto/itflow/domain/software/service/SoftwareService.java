package io.github.mroncatto.itflow.domain.software.service;

import io.github.mroncatto.itflow.application.model.AbstractService;
import io.github.mroncatto.itflow.application.service.MessageService;
import io.github.mroncatto.itflow.domain.commons.exception.BadRequestException;
import io.github.mroncatto.itflow.domain.software.dto.SoftwareLicenseRequestDto;
import io.github.mroncatto.itflow.domain.software.dto.SoftwareListResponseDto;
import io.github.mroncatto.itflow.domain.software.dto.SoftwareRequestDto;
import io.github.mroncatto.itflow.domain.software.entity.Software;
import io.github.mroncatto.itflow.domain.software.entity.SoftwareLicense;
import io.github.mroncatto.itflow.domain.software.model.ISoftwareService;
import io.github.mroncatto.itflow.infrastructure.persistence.ISoftwareRepository;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class SoftwareService extends AbstractService implements ISoftwareService {
    public static final String SOFTWARE = "software";
    private final ISoftwareRepository repository;
    private final MessageService messageService;

    @Override
    @Transactional(readOnly = true)
    public List<Software> findAll() {
        return this.repository.findAllByActiveTrue();
    }

    @Override
    public Software save(SoftwareRequestDto softwareRequestDto, BindingResult result) throws BadRequestException {
        validateResult(result);
        var software = new Software();
        BeanUtils.copyProperties(softwareRequestDto, software);
        log.debug(">>>CREATING SOFTWARE: {}", softwareRequestDto);
        return this.repository.save(software);
    }

    @Override
    public Software update(SoftwareRequestDto softwareRequestDto, BindingResult result) throws BadRequestException, NoResultException {
        validateResult(result);
        Software software = this.repository.findById(softwareRequestDto.getId()).orElseThrow(()
                -> new NoResultException(messageService.getMessageNotFound(SOFTWARE)));
        software.setName(softwareRequestDto.getName());
        software.setDeveloper(softwareRequestDto.getDeveloper());
        log.debug(">>>UPDATING SOFTWARE: {}", softwareRequestDto);
        return this.repository.save(software);
    }

    @Override
    public Software findById(Long id) throws NoResultException {
        return this.repository.findById(id).orElseThrow(()
                -> new NoResultException(messageService.getMessageNotFound(SOFTWARE)));
    }

    @Override
    public Page<Software> findAll(Pageable pageable, String filter) {
        log.debug(">>>FILTERING SOFTWARE BY: {}", filter);
        return this.repository.findAllByActiveTrue(pageable);
    }

    @Override
    @Transactional
    public Software deleteById(Long id) throws NoResultException {
        Software software = this.repository.findById(id).orElseThrow(()
                -> new NoResultException(messageService.getMessageNotFound(SOFTWARE)));
        software.setActive(false);
        software.getLicenses().forEach(SoftwareLicense::disable);
        log.debug(">>>DELETING SOFTWARE BY: {}", id);
        return this.repository.save(software);
    }

    @Override
    @Transactional
    public Software addLicense(Long id, SoftwareLicenseRequestDto licenseDto, BindingResult result) throws NoResultException, BadRequestException {
        validateResult(result);
        Software software = this.repository.findById(id).orElseThrow(()
                -> new NoResultException(messageService.getMessageNotFound(SOFTWARE)));
        var license = new SoftwareLicense();
        BeanUtils.copyProperties(licenseDto, license);
        license.setSoftware(software);
        software.getLicenses().add(license);
        log.debug(">>>ADD SOFTWARE LICENSE: {}", licenseDto);
        return this.repository.save(software);
    }

    @Override
    public List<SoftwareListResponseDto> findAll(Specification<Software> spec) {
        return this.repository.findAll(spec)
                .stream()
                .sorted(Comparator.comparing(Software::getName))
                .limit(10)
                .map(Software::response)
                .collect(Collectors.toList());
        // TODO: Filtrar e ordenar a nivel de banco
    }
}

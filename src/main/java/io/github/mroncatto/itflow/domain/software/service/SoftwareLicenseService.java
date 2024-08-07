package io.github.mroncatto.itflow.domain.software.service;

import io.github.mroncatto.itflow.application.model.AbstractService;
import io.github.mroncatto.itflow.application.service.MessageService;
import io.github.mroncatto.itflow.domain.commons.exception.BadRequestException;
import io.github.mroncatto.itflow.domain.computer.dto.ComputerSoftwareRequestDto;
import io.github.mroncatto.itflow.domain.device.dto.DeviceComputerSoftwareRequestDto;
import io.github.mroncatto.itflow.domain.device.entity.DeviceComputerSoftware;
import io.github.mroncatto.itflow.domain.device.entity.pk.DeviceComputerSoftwarePK;
import io.github.mroncatto.itflow.domain.software.dto.LicenseKeyRequestDto;
import io.github.mroncatto.itflow.domain.software.dto.SoftwareLicenseRequestDto;
import io.github.mroncatto.itflow.domain.software.entity.SoftwareLicense;
import io.github.mroncatto.itflow.domain.software.entity.SoftwareLicenseKey;
import io.github.mroncatto.itflow.domain.software.model.ISoftwareLicenseService;
import io.github.mroncatto.itflow.infrastructure.persistence.IComputerSoftwareRepository;
import io.github.mroncatto.itflow.infrastructure.persistence.IDeviceComputerRepository;
import io.github.mroncatto.itflow.infrastructure.persistence.ISoftwareLicenseKeyRepository;
import io.github.mroncatto.itflow.infrastructure.persistence.ISoftwareLicenseRepository;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class SoftwareLicenseService extends AbstractService implements ISoftwareLicenseService {
    private final ISoftwareLicenseRepository repository;
    private final ISoftwareLicenseKeyRepository softwareLicenseKeyRepository;
    private final IDeviceComputerRepository computerRepository;
    private final IComputerSoftwareRepository computerSoftwareRepository;
    private final MessageService messageService;

    @Override
    public List<SoftwareLicense> findAll() {
        return this.repository.findAllByActiveTrue();
    }

    @Override
    public SoftwareLicense save(SoftwareLicenseRequestDto softwareLicenseRequestDto, BindingResult result) throws BadRequestException {
        validateResult(result);
        var softwareLicense = new SoftwareLicense();
        BeanUtils.copyProperties(softwareLicenseRequestDto, softwareLicense);
        log.debug(">>>CREATING SOFTWARE LICENSE: {}", softwareLicenseRequestDto);
        return this.repository.save(softwareLicense);
    }

    @Override
    @Transactional
    public SoftwareLicense update(SoftwareLicenseRequestDto softwareLicenseRequestDto, BindingResult result) throws BadRequestException, NoResultException {
        validateResult(result);
        SoftwareLicense license = this.findById(softwareLicenseRequestDto.getId());
        license.setDescription(softwareLicenseRequestDto.getDescription());
        license.setCode(softwareLicenseRequestDto.getCode());
        license.setExpireAt(softwareLicenseRequestDto.getExpireAt());
        log.debug(">>>UPDATING SOFTWARE LICENSE: {}", softwareLicenseRequestDto);
        return this.repository.save(license);
    }

    @Override
    public SoftwareLicense findById(Long id) throws NoResultException {
        return this.repository.findById(id).orElseThrow(()
                -> new NoResultException(messageService.getMessageNotFound("software_license")));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SoftwareLicense> findAll(Pageable pageable, String filter) {
        return this.repository.findAllByActiveTrue(pageable);
    }

    @Override
    public SoftwareLicense deleteById(Long id) throws NoResultException {
        SoftwareLicense license = this.findById(id);
        license.setActive(false);
        //TODO: Validar licensa com keys em uso!
        return this.repository.save(license);
    }

    @Override
    @Transactional
    public SoftwareLicense addLicenseKey(Long id, LicenseKeyRequestDto licenseKeyRequestDto, BindingResult result) throws NoResultException, BadRequestException {
        validateResult(result);
        SoftwareLicense license = this.findById(id);
        licenseKeyRequestDto.setSoftwareLicense(license);
        var licenseKey = new SoftwareLicenseKey();
        BeanUtils.copyProperties(licenseKeyRequestDto, licenseKey);
        license.getKeys().add(licenseKey);
        log.debug(">>>ADD SOFTWARE LICENSE KEY: {}", licenseKeyRequestDto);
        return this.repository.save(license);
    }

    @Override
    @Transactional
    public SoftwareLicense removeLicenseKey(Long id, LicenseKeyRequestDto licenseKeyRequestDto, BindingResult result) throws NoResultException, BadRequestException {
        validateResult(result);
        SoftwareLicense license = this.findById(id);
        license.getKeys().removeIf(k -> k.getId().equals(licenseKeyRequestDto.getId()));
        log.debug(">>>REMOVE SOFTWARE LICENSE KEY: {}", licenseKeyRequestDto);
        return this.repository.save(license);
    }

    @Override
    public SoftwareLicenseKey findLicenseKey(Long id) {
        log.debug(">>>FINDING COMPUTER LICENSE KEY BY ID: {}", id);
        return softwareLicenseKeyRepository.findById(id).orElseThrow(()
                -> new NoResultException(messageService.getMessageNotFound("software_license_key")));
    }

    @Override
    public DeviceComputerSoftware addLicenseKeyAssignment(Long id, ComputerSoftwareRequestDto dto, BindingResult result) throws NoResultException, BadRequestException {
        log.debug(">>>ASSIGN COMPUTER LICENSE KEY AT DEVICE: {}", dto.toString());
        var licenceKey = this.findLicenseKey(id);
        var computerSoftware = this.computerSoftwareRepository.findById(dto.getDeviceComputerSoftware().getId());
        if(computerSoftware.isPresent()) {
            computerSoftware.get().setSoftwareLicenseKey(licenceKey);
            this.computerSoftwareRepository.save(computerSoftware.get());
            return computerSoftware.get();
        } else {
            throw new NoResultException(messageService.getMessageNotFound("computer_software"));
        }
    }
}

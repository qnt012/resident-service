package com.nhnacademy.resident.service.impl;

import com.nhnacademy.resident.domain.dto.HouseholdCompositionDto;
import com.nhnacademy.resident.domain.dto.HouseholdDto;
import com.nhnacademy.resident.domain.dto.MovementAddressDto;
import com.nhnacademy.resident.domain.dto.ResidentRegistrationDto;
import com.nhnacademy.resident.entity.CertificateIssue;
import com.nhnacademy.resident.entity.Resident;
import com.nhnacademy.resident.exception.HouseholdNotFoundException;
import com.nhnacademy.resident.exception.ResidentNotFoundException;
import com.nhnacademy.resident.repository.*;
import com.nhnacademy.resident.service.ResidentRegistrationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DefaultResidentRegistrationService implements ResidentRegistrationService {
    private final ResidentRepository residentRepository;
    private final HouseholdRepository householdRepository;
    private final MovementAddressRepository movementAddressRepository;
    private final HouseholdCompositionRepository householdCompositionRepository;
    private final CertificateIssueRepository certificateIssueRepository;
    private final Random random = new Random();

    public DefaultResidentRegistrationService(ResidentRepository residentRepository, HouseholdRepository householdRepository,
                                              MovementAddressRepository movementAddressRepository, HouseholdCompositionRepository householdCompositionRepository,
                                              CertificateIssueRepository certificateIssueRepository) {
        this.residentRepository = residentRepository;
        this.householdRepository = householdRepository;
        this.movementAddressRepository = movementAddressRepository;
        this.householdCompositionRepository = householdCompositionRepository;
        this.certificateIssueRepository = certificateIssueRepository;
    }

    @Override
    public ResidentRegistrationDto getResidentRegistrationDto(Long serialNumber) {
        HouseholdDto householdDto = householdRepository.findResidentRegistrationDto(serialNumber);
        if (Objects.isNull(householdDto)) throw new HouseholdNotFoundException();

        Resident resident = residentRepository.findById(serialNumber).orElseThrow(ResidentNotFoundException::new);

        CertificateIssue certificateIssue = new CertificateIssue(9876543200000000L + random.nextInt(100000000), resident,"주민등록등본", LocalDate.now());
        certificateIssueRepository.save(certificateIssue);
        return new ResidentRegistrationDto(certificateIssue.getIssueDate(), certificateIssue.getConfirmationNumber(), householdDto.getSerialNumber(),
                householdDto.getName(), householdDto.getCompositionReasonCode(), householdDto.getCompositionDate());
    }

    @Override
    public List<MovementAddressDto> getMovementAddresses(Long householdSerialNumber) {
        return movementAddressRepository.findAllByPkHouseholdSerialNumberOrderByPkReportDateDesc(householdSerialNumber);
    }

    @Override
    public List<HouseholdCompositionDto> getHouseholdComposition(Long householdSerialNumber) {
        return householdCompositionRepository.findAllByPkHouseholdSerialNumber(householdSerialNumber);
    }
}
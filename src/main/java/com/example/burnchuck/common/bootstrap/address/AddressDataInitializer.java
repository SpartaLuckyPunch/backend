package com.example.burnchuck.common.bootstrap.address;

import com.example.burnchuck.common.bootstrap.csv.CsvReader;
import com.example.burnchuck.common.entity.Address;
import com.example.burnchuck.domain.user.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AddressDataInitializer implements ApplicationRunner {

    private final AddressRepository addressRepository;
    private final CsvReader csvReader;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        // 이미 데이터 있으면 스킵
        if (addressRepository.count() > 0) {
            return;
        }

        List<AddressCsv> addresses =
                csvReader.read("data/address.csv", AddressCsv.class);

        List<Address> entities = addresses.stream()
                .map(dto -> new Address(
                        dto.getProvince(),
                        dto.getCity(),
                        dto.getDistrict(),
                        dto.getLatitude(),
                        dto.getLongitude()
                ))
                .toList();

        addressRepository.saveAll(entities);
    }
}
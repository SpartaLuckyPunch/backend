package com.example.burnchuck.common.bootstrap.dataInitializer;

import com.example.burnchuck.common.bootstrap.csv.AddressCsv;
import com.example.burnchuck.common.bootstrap.csv.CsvReader;
import com.example.burnchuck.domain.user.repository.AddressRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AddressInitData implements ApplicationRunner {

    private final AddressRepository addressRepository;
    private final CsvReader csvReader;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        // 이미 데이터 있으면 스킵
        if (addressRepository.count() > 0) {
            return;
        }

        List<AddressCsv> addresses = csvReader.read("data/address.csv", AddressCsv.class);

        String sql = "INSERT INTO addresses (province, city, district, latitude, longitude) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, addresses, addresses.size(), (ps, address) -> {
            ps.setString(1, address.getProvince());
            ps.setString(2, address.getCity());
            ps.setString(3, address.getDistrict());
            ps.setDouble(4, address.getLatitude());
            ps.setDouble(5, address.getLongitude());
        });
    }
}
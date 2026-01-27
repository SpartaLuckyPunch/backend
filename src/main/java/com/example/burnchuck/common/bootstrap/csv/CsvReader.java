package com.example.burnchuck.common.bootstrap.csv;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Component
public class CsvReader {

    /**
     * CSV 파서 - CSV 파일을 읽고 List 형태의 DTO로 매핑
     */
    public <T> List<T> read(String path, Class<T> type) {
        try (
                Reader reader = new InputStreamReader(
                        new ClassPathResource(path).getInputStream()
                )
        ) {
            return new CsvToBeanBuilder<T>(reader)
                    .withType(type)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
        } catch (Exception e) {
            throw new RuntimeException("CSV 로드 실패: " + path, e);
        }
    }
}
package com.example.burnchuck.common.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "meetings")
public class MeetingDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    public MeetingDocument(Meeting meeting) {
        this.id = String.valueOf(meeting.getId());
        this.title = meeting.getTitle();
    }
}

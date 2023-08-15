package com.covenant.tribe.domain.event.search;

import com.covenant.tribe.domain.user.search.UserSearchUnit;
import com.covenant.tribe.util.reflection.MyNested;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "event", createIndex = true)
@TypeAlias("Event")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSearchUnit {
    @Id
    Long id;

    @Field(type = FieldType.Object)
    EventAddressSearchUnit eventAddress;
    
    @Field
    String eventName;
    
    @Field
    String eventDescription;
    
    @Field
    String eventType;

    @Field
    List<String> taglist;

    @Field(type = FieldType.Nested)
    List<UserSearchUnit> users;
}
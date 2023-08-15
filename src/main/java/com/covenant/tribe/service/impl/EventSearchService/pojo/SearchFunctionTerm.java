package com.covenant.tribe.service.impl.EventSearchService.pojo;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchFunctionTerm {

    String field;

    Boolean isOptional;
}

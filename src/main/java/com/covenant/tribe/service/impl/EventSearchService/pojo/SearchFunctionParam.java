package com.covenant.tribe.service.impl.EventSearchService.pojo;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchFunctionParam {

    String field;

    Float boost;

    Integer fuzziness = 2;
}

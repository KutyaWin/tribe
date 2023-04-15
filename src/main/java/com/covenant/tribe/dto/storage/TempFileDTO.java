package com.covenant.tribe.dto.storage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TempFileDTO {
    @JsonProperty("unique_file_name")
    String uniqueFileName;
}

package com.covenant.tribe.util;

import com.covenant.tribe.util.reflection.MyNested;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class UpdateTestObj {

    String firstField;

    String secondField;

    @MyNested
    UpdateTestObj child;
}

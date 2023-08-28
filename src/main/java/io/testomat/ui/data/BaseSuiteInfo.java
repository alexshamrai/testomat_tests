package io.testomat.ui.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseSuiteInfo {

    private String name;
    private int testsCount;
}

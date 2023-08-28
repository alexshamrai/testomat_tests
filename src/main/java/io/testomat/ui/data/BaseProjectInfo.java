package io.testomat.ui.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseProjectInfo {

    private String name;
    private int count;
    private ProjectType label;

    @AllArgsConstructor
    public enum ProjectType {
        CLASSICAL("Classical"),
        BDD("BDD");

        public final String label;

        @Override
        public String toString() {
            return label;
        }
    }

}

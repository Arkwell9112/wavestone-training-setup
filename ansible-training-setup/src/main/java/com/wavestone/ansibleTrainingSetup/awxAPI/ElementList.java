package com.wavestone.ansibleTrainingSetup.awxAPI;

import java.util.Map;

public record ElementList(
        ElementResponse[] results
) {
    public record ElementResponse(
            String id,
            String name,
            SummaryFields summary_fields
    ) {
        public record SummaryFields(
                Map<String, ObjectRole> object_roles
        ) {
            public record ObjectRole(
                    String id,
                    String name
            ) {
            }
        }
    }
}

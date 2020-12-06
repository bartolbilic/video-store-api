package com.degombo.videostore.models.projections;

import org.springframework.beans.factory.annotation.Value;

public interface UserProjection {
    String getUsername();

    @Value("#{target.first_name}")
    String getFirstName();

    @Value("#{target.last_name}")
    String getLastName();
}

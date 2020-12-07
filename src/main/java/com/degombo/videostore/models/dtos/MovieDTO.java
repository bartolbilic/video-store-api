package com.degombo.videostore.models.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MovieDTO {
    private String title;
    private String description;
    @NotNull
    private List<GenreDTO> genres;
}

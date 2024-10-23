package org.company.trashambulance.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FactResponse {
    private String id;
    private String text;
    private String source;
    private String language;
    private String permalink;
}


package com.app.gestionInterventions.models.recources.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "teams")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {
}

package ru.ibs.diploma.data;

import lombok.Getter;
import lombok.Setter;

// csv parking.csv properties.csv population.txt log
@Getter
@Setter
public class Arguments {

    private String parkingFormat;

    private String parkingFile;

    private String propertiesFile;

    private String populationFile;

    private String logFile;
}

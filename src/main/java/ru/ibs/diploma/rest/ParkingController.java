package ru.ibs.diploma.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ibs.diploma.cache.Answer;
import ru.ibs.diploma.cache.FirstAnswer;
import ru.ibs.diploma.data.Arguments;
import ru.ibs.diploma.data.Field;
import ru.ibs.diploma.data.FileNames;
import ru.ibs.diploma.logging.WriteLogService;
import ru.ibs.diploma.service.AnalyseService;
import ru.ibs.diploma.validation.ArgsValidation;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ArgsValidation argsValidation;

    private final AnalyseService analyseService;

    private final WriteLogService writeLogService;

    private final FileNames fileNames;

    @GetMapping("/arguments")
    public ResponseEntity<String> getArguments(){
        if(fileNames.getParkingFile() == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No arguments found.");
        }

        String arguments = fileNames.getParkingType() + ", " +
            fileNames.getParkingFile() + ", " +
            fileNames.getPopulationFile() + ", " +
            fileNames.getPropertiesFile() + ", " +
            fileNames.getLogFile();
        return ResponseEntity.ok(arguments);
    }

    @PostMapping("/arguments")
    public ResponseEntity<String> postAndValidateArgs(@RequestBody Arguments args){
        fileNames.setParkingType(args.getParkingFormat());
        fileNames.setParkingFile(args.getParkingFile());
        fileNames.setPopulationFile(args.getPopulationFile());
        fileNames.setPropertiesFile(args.getPropertiesFile());
        fileNames.setLogFile(args.getLogFile());

        String[] arguments = new String[5];
        arguments[0] = args.getParkingFormat();
        arguments[1] = args.getParkingFile();
        arguments[2] = args.getPropertiesFile();
        arguments[3] = args.getPopulationFile();
        arguments[4] = args.getLogFile();

        try {
            argsValidation.validateArgs(arguments);
        }catch (IllegalArgumentException ie){
            System.out.println(ie.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ie.getMessage());
        }

        writeLogService.logFileEntry(args.getLogFile());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Arguments are correct. You can choose parameter");
    }

    @GetMapping("/questions")
    public ResponseEntity<List<String>> getQuestionsList(){
        return ResponseEntity.ok(List.of("1 - Print Total Population",
            "2 - Print Total Parking Fines per Capita",
            "3 - Print Average Market Value",
            "4 - Print Average Total Livable Area",
            "5 - Print Total Market Value per Capita",
            "6 - Surprise action"));
    }

    @GetMapping("/questions/{number}")
    public ResponseEntity<String> answerQuestionByNumber(@PathVariable String number,
                                        @RequestParam(name = "zip", required = false) String zip){
        writeLogService.logChoice(number);

        try {
            switch (number) {
                case "1":
                    Answer result1 = analyseService.totalPopulation();
                    return ResponseEntity.ok(result1.getAnswer());
                case "2":
                    Answer result2 = analyseService.totalParkingFinesPerCapita();
                    return ResponseEntity.ok(result2.getAnswer());
                case "3":
                    if(zip == null || zip.isEmpty()){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("You should enter ZIP-code for this question");
                    }
                    writeLogService.logChoice(zip);
                    Answer result3 = analyseService.averageProperties(zip, Field.MARKET_VALUE);
                    return ResponseEntity.ok(result3.getAnswer());
                case "4":
                    if(zip == null || zip.isEmpty()){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("You should enter ZIP-code for this question");
                    }
                    writeLogService.logChoice(zip);
                    Answer result4 = analyseService.averageProperties(zip, Field.LIVABLE_AREA);
                    return ResponseEntity.ok(result4.getAnswer());
                case "5":
                    if(zip == null || zip.isEmpty()){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("You should enter ZIP-code for this question");
                    }
                    writeLogService.logChoice(zip);
                    Answer result5 = analyseService.totalMarketValuePerCapita(zip);
                    return ResponseEntity.ok(result5.getAnswer());
                case "6":
                    Answer result6 = analyseService.surpriseOption();
                    return ResponseEntity.ok(result6.getAnswer());
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Unknown question, try choosing another one.");
            }
        }catch (IOException ie){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("The problem occurred: " + ie.getMessage());
        }
    }
}

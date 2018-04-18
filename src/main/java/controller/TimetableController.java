package controller;

import ga.TimetableGA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import service.ParserService;
import service.TimetableService;

import java.io.IOException;

@RestController
public class TimetableController {

    @Value("${controller.requestUrl}")
    private String requestUrl;

    @Value("${controller.groupsList}")
    private String groupsList;

    @Value("${ga.populationSize}")
    private int populationSize;

    @Value("${ga.mutationRate}")
    private double mutationRate;

    @Value("${ga.crossoverRate}")
    private double crossoverRate;

    @Value("${ga.elitismCount}")
    private int elitismCount;

    @Value("${ga.tournamentSize}")
    private int tournamentSize;

    @Autowired
    private TimetableService timetableService;

    @Autowired
    private ParserService parserService;

    @RequestMapping("/timetable")
    public ModelAndView getItemInfo(ModelAndView modelAndView) throws IOException {

        modelAndView.setViewName("timetable");
        modelAndView.addObject(
                timetableService.timetableToDTO(
                        TimetableGA.run(
                                parserService.getItemInfo(requestUrl, groupsList),
                                populationSize, mutationRate, crossoverRate, elitismCount, tournamentSize)));
        return modelAndView;

    }
}
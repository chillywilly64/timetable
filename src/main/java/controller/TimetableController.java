package controller;

import service.GAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import service.parser.ParserService;
import service.TimetableService;

import java.io.IOException;

/**
 * @author Sergey_Dovadzhyan
 */
@RestController
public class TimetableController {

    @Autowired
    private TimetableService timetableService;

    @Autowired
    private ParserService parserService;

    @Autowired
    private GAService gaService;

    @RequestMapping("/timetable")
    public ModelAndView getTimetable(ModelAndView modelAndView) throws IOException {

        modelAndView.setViewName("timetable");
        modelAndView.addObject(
                timetableService.timetableToDTO(
                        gaService.runGA(
                                parserService.getTimetableData())));
        return modelAndView;

    }
}
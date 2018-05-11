package service.parser;

import ga.Timetable;

import java.io.IOException;

/**
 * @author Sergey_Dovadzhyan
 */
public interface ParserService {
    Timetable getTimetableData() throws IOException;
}

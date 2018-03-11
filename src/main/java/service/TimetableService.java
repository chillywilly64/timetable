package service;

import dto.CellDTO;
import dto.TimetableDTO;
import ga.Class;
import ga.Timeslot;
import ga.Timetable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimetableService {

    public TimetableDTO timetableToDTO(Timetable timetable) {
        TimetableDTO timetableDTO = new TimetableDTO();

        timetableDTO.setTimeslots(getTimeslots(timetable));
        timetableDTO.setDaysOfWeek(getDaysOfWeek());
        timetableDTO.setTables(getCells(timetable));

        return timetableDTO;
    }

    private List<String> getTimeslots(Timetable timetable) {
        Set<String> timeslots = new HashSet<>();
        for (Timeslot timeslot: timetable.getTimeslots().values()) {
            timeslots.add(timeslot.getTimeslot());
        }
        List<String> timeslotsList = new ArrayList<>(timeslots);
        Collections.sort(timeslotsList, (s, t1) -> {
           String[] s1 = s.split("\\s*[-|\\s]\\s*");
           String[] s2 = t1.split("\\s*[-|\\s]\\s*");

           LocalTime from1 = LocalTime.parse(s1[0]);
           LocalTime to1 = LocalTime.parse(s1[1]);
           LocalTime from2 = LocalTime.parse(s2[0]);
           LocalTime to2 = LocalTime.parse(s2[1]);

           return from1.equals(from2) ? from1.compareTo(from2) : to1.compareTo(to2);
        });
        return timeslotsList;
    }

    private List<String> getDaysOfWeek() {
        return Arrays.stream(DayOfWeek.values())
                .map(dayOfWeek -> dayOfWeek.getDisplayName(TextStyle.FULL, new Locale("ru")))
                .collect(Collectors.toList());
    }

    private Map<Integer, CellDTO[][]> getCells(Timetable timetable) {
        List<String> timeslots = getTimeslots(timetable);
        List<DayOfWeek> days = Arrays.asList(DayOfWeek.values());

        Map<Integer, CellDTO[][]> tables = new HashMap<>();

        Map<Integer, List<Class>> classesByGroups = timetable.getClassesByGroups();

        for (Integer group: classesByGroups.keySet()) {

            CellDTO[][] table = new CellDTO[timeslots.size()][days.size()];

            for (Class clas : classesByGroups.get(group)) {
                int timeslotNumber = timeslots.indexOf(clas.getTimeslot().getTimeslot());
                int dayNumber = days.indexOf(clas.getTimeslot().getDayOfWeek());

                table[timeslotNumber][dayNumber] =
                        new CellDTO(
                                clas.getModule().getModuleName(),
                                clas.getProfessor().getProfessorName(),
                                clas.getRoom().getRoomNumber());
            }

            tables.put(group, table);
        }
        return tables;
    }

}

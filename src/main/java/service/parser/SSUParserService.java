package service.parser;

import ga.Timetable;
import ga.entity.Module;
import ga.entity.Professor;
import ga.entity.Room;
import ga.entity.Timeslot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sergey_Dovadzhyan
 */
@Service
public class SSUParserService implements ParserService{

    private static final Logger log = LoggerFactory.getLogger(SSUParserService.class);

    @Value("${controller.requestUrl}")
    private String requestUrl;

    @Value("${controller.groupsList}")
    private String[] groupsList;

    private static final int DEFAULT_ROOM_CAPACITY = 40;
    private static final int DEFAULT_GROUP_SIZE = 30;

    private int roomId = 0;
    private int moduleId = 0;
    private int professorId = 0;
    private int timeslotId = 0;

    @Override
    public Timetable getTimetableData() throws IOException{
        Timetable timetable = new Timetable();

        Set<String> rooms = new HashSet<>();
        Map<String, Professor> professorsMap = new HashMap<>();
        Set<String> timeslots = new LinkedHashSet<>();
        List<Module> modules = new ArrayList<>();

        resetIndexes();

        log.info("Parsing start");

        for (String group: groupsList) {
            log.info("Parsing from " + requestUrl + group);
            Document doc = Jsoup.connect(requestUrl + group).get();

            Element schedule = doc.select("table[id=schedule]").first();
            Elements rows = schedule.select("tr");

            Map<String, Module> modulesMap = new HashMap<>();

            for (Element row : rows) {
                addTimeslot(timeslots, row);
                for (Element cell : row.select("td")) {
                    Element element = cell.selectFirst("div.l");
                    if (element != null) {
                        addClassroom(rooms, element);
                        addModuleAndProfessor(modulesMap, professorsMap, element);
                    }
                }
            }

            List<Module> groupModules = new ArrayList<>(modulesMap.values());
            modules.addAll(groupModules);
            timetable.addGroup(Integer.parseInt(group), DEFAULT_GROUP_SIZE, groupModules);
        }

        rooms.forEach(roomName -> timetable.addRoom(new Room(roomId++, roomName, DEFAULT_ROOM_CAPACITY)));
        modules.forEach(module -> timetable.addModule(module));
        professorsMap.values().forEach(professor -> timetable.addProfessor(professor));
        Arrays.stream(DayOfWeek.values())
            .filter(dayOfWeek -> !dayOfWeek.equals(DayOfWeek.SUNDAY))
            .forEach(dayOfWeek -> timeslots
                .forEach(timeslot -> timetable.addTimeslot(new Timeslot(timeslotId++, dayOfWeek, timeslot))));
//        for (String timeslot: timeslots) {
//            for (DayOfWeek dayOfWeek: DayOfWeek.values()) {
//                if (!dayOfWeek.equals(DayOfWeek.SUNDAY)) {
//                    timetable.addTimeslot(new Timeslot(timeslotId++, dayOfWeek, timeslot));
//                }
//            }
//        }
        timetable.setDaysTimeslot(new ArrayList<>(timeslots));

        return timetable;

    }

    private void addTimeslot(Set<String> timeslots, Element element) {
        String timeslot = element.selectFirst("th").text();
        if (timeslot != null && !timeslot.isEmpty()) {
            timeslots.add(timeslot);
        }
    }

    private void addClassroom(Set<String> classRooms, Element element) {

        String room = element.selectFirst("div.l-p").text();

        classRooms.add(room);
    }

    private void addModuleAndProfessor(Map<String,Module> modules, Map<String, Professor> professors, Element element) {
        String name = element.selectFirst("div.l-dn").text();
        Professor professor = addProfessors(professors, element);
        Module module = modules.get(name);
        if (module != null) {
            module.incremateNumberOfLecturesPerWeek();
            module.addProfessor(professor);
        } else {
            List<Professor> professorList = new ArrayList<>();
            professorList.add(professor);
            module = new Module(moduleId++, name, 1, professorList);
            modules.put(name, module);
        }
    }

    private Professor addProfessors(Map<String,Professor> professors, Element element) {
        String name = element.selectFirst("div.l-tn").text();
        Professor professor = professors.get(name);
        if (professor == null) {
            professor = new Professor(professorId++, name);
            professors.put(name, professor);
        }

        return professor;
    }

    private void resetIndexes(){
        roomId = 0;
        moduleId = 0;
        professorId = 0;
        timeslotId = 0;
    }
}

package service;

import ga.*;
import ga.entity.Module;
import ga.entity.Professor;
import ga.entity.Room;
import ga.entity.Timeslot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.*;

@Service
public class ParserService {

    private int roomId = 0;
    private int moduleId = 0;
    private int professorId = 0;
    private int timeslotId = 0;

    public Timetable getItemInfo(String requestUrl, String groupsList) throws IOException {
        Timetable timetable = new Timetable();

        Set<String> rooms = new HashSet<>();
        Map<String, Professor> professorsMap = new HashMap<>();
        Set<String> timeslots = new LinkedHashSet<>();
        List<Module> modules = new ArrayList<>();

        resetIncrements();

        for (String group: groupsList.split(",")) {
            Document doc = Jsoup.connect(requestUrl + group).get();

            Element schedule = doc.select("table[id=schedule]").first();
            Elements rows = schedule.select("tr");

            Map<String, Module> modulesMap = new HashMap<>();

            for (Element row : rows) {
                addTimeslot(timeslots, row);
                for (Element cell : row.select("td")) {
                    for (Element element : cell.select("div.l")) {
                        if (element != null) {
                            addClassroom(rooms, element);
                            addModuleAndProfessor(modulesMap, professorsMap, element);
                        }
                    }
                }
            }

            List<Module> groupModules = new ArrayList<>(modulesMap.values());
            modules.addAll(groupModules);
            timetable.addGroup(Integer.parseInt(group), 30, groupModules);
        }

        rooms.stream().map(roomName -> new Room(roomId++, roomName, 40)).forEach(room -> timetable.addRoom(room));
        modules.stream().forEach(module -> timetable.addModule(module));
        professorsMap.values().stream().forEach(professor -> timetable.addProfessor(professor));
        for (String timeslot: timeslots) {
            for (DayOfWeek dayOfWeek: DayOfWeek.values()) {
                if (!dayOfWeek.equals(DayOfWeek.SUNDAY)) {
                    timetable.addTimeslot(new Timeslot(timeslotId++, dayOfWeek, timeslot));
                }
            }
        }
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

    private void resetIncrements(){
        roomId = 0;
        moduleId = 0;
        professorId = 0;
        timeslotId = 0;
    }

}

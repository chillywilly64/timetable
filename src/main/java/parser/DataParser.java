package parser;

import ga.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import service.TimetableService;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class DataParser {
    private static final Logger log = LoggerFactory.getLogger(DataParser.class);

    private static final String PRACTIC = "пр.";
    private static final String LECTURE = "лек.";
    private static final Pattern ROOM_PATTERN_1 = Pattern.compile("ауд. [0-9]{1,3}");
    private static final Pattern ROOM_PATTERN_2 = Pattern.compile("[0-9]{1,3} ауд.");
    private static final Pattern DEP_PATTERN = Pattern.compile("[0-9]{1,2} корп.");

    @Value("${requestUrl}")
    private String requestUrl;

    @Value("${groupsList}")
    private String groupsList;

    private int roomId = 0;
    private int moduleId = 0;
    private int professorId = 0;
    private int timeslotId = 0;

    @Autowired
    private TimetableService timetableService;

    @RequestMapping("/timetable")
    public ModelAndView getTimetable(ModelAndView modelAndView) {
        modelAndView.setViewName("timetable");
        modelAndView.addObject(timetableService.timetableToDTO(TimetableGA.run(null)));
        return modelAndView;
    }

    @RequestMapping("/parser")
    public ModelAndView getItemInfo(ModelAndView modelAndView) throws IOException {
        Timetable timetable = new Timetable();

        Set<String> rooms = new HashSet<>();
        Map<String, Professor> professorsMap = new HashMap<>();
        Set<String> timeslots = new LinkedHashSet<>();
        List<Module> modules = new ArrayList<>();

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

        modelAndView.setViewName("timetable");
        modelAndView.addObject(timetableService.timetableToDTO(TimetableGA.run(timetable)));
        return modelAndView;

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

    private boolean isPractit(Element element) {
        String type = element.selectFirst("div.l-pr-t").text();

        boolean isLab = false;
        if (type.equals(PRACTIC)) {
            isLab = true;
        } else if (type.equals(LECTURE)) {
            isLab = false;
        }
        return isLab;
    }

    private String extractSubstringByRegex(String s, Pattern ... patterns) {
        for (Pattern pattern: patterns) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        throw new IllegalArgumentException("No string found");
    }
}
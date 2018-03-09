package parser;

import ga_old.ClassRoom;
import ga_old.Professor;
import ga_old.Subject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

    @RequestMapping("/timetable")
    public void getItemInfo() throws IOException {
        Document doc = Jsoup.connect(requestUrl).get();
        log.info("Getting info from " + doc.title());
        Element schedule = doc.select("table[id=schedule]").first();
        Elements rows = schedule.select("tr");

        Set<ClassRoom> classRooms = new HashSet<>();
        Map<String,Subject> subjects = new HashMap<>();
        Map<String,Professor> professors = new HashMap<>();

        for (Element row: rows) {
            for (Element cell: row.select("td")) {
                for (Element element: cell.select("div.l")) {
                    if (element != null) {
                        addClassroom(classRooms, element);
                        Subject subject = getAndAddSubjects(subjects, element);
                        addProfessors(professors, element, subject);

                    }
                }
            }
        }
        classRooms.forEach(room -> System.out.println(room.getDepartment() + " " + room.getRoomNo()));
        subjects.forEach((s, subject) -> System.out.println(subject.getSubjectName() + " " + subject.getNumberOfLecturesPerWeek()));
    }

    private void addClassroom(Set<ClassRoom> classRooms, Element element) {
        boolean isLab = isPractit(element);

        String roomAndDep = element.selectFirst("div.l-p").text();
        String dep = extractSubstringByRegex(roomAndDep, DEP_PATTERN);
        String room = extractSubstringByRegex(roomAndDep, ROOM_PATTERN_1, ROOM_PATTERN_2);

        classRooms.add(new ClassRoom(room, 40, isLab, dep));
    }

    private Subject getAndAddSubjects(Map<String,Subject> subjects, Element element) {
        boolean isLab = isPractit(element);

        String roomAndDep = element.selectFirst("div.l-p").text();
        String dep = extractSubstringByRegex(roomAndDep, DEP_PATTERN);

        String name = element.selectFirst("div.l-dn").text();

        Subject subject = subjects.get(name);
        if (subject != null) {
            subject.incremateNumberOfLecturesPerWeek();
        } else {
            subject = new Subject(name, 1, isLab, dep);
            subjects.put(name, subject);
        }
        return subject;
    }

    private void addProfessors(Map<String,Professor> professors, Element element, Subject subject) {
        String name = element.selectFirst("div.l-tn").text();

        Professor professor = professors.get(name);
        if (professor != null) {
            professor.addSubject(subject.getSubjectName());
        } else {
            professors.put(name, new Professor(name, subject.getSubjectName()));
        }
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
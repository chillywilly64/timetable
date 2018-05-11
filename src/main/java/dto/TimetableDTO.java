package dto;

import java.util.List;
import java.util.Map;

/**
 * @author Sergey_Dovadzhyan
 */
public class TimetableDTO {

    private List<String> daysOfWeek;
    private List<String> timeslots;

    private Map<Integer, CellDTO[][]> tables;

    public List<String> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<String> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public List<String> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(List<String> timeslots) {
        this.timeslots = timeslots;
    }

    public  Map<Integer, CellDTO[][]> getTables() {
        return tables;
    }

    public void setTables(Map<Integer, CellDTO[][]> table) {
        this.tables = table;
    }
}

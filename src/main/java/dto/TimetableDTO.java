package dto;

import java.util.List;

public class TimetableDTO {

    private List<String> daysOfWeek;
    private List<String> timeslots;

    private List<CellDTO[][]> tables;

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

    public List<CellDTO[][]> getTables() {
        return tables;
    }

    public void setTables(List<CellDTO[][]> table) {
        this.tables = table;
    }
}

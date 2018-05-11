package dto;

/**
 * @author Sergey_Dovadzhyan
 */
public class CellDTO {
    private String subject;
    private String professor;
    private String room;

    public CellDTO(String subject, String professor, String room) {
        this.subject = subject;
        this.professor = professor;
        this.room = room;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return subject + "\n"
                + professor + "\n"
                + room + "\n";
    }
}

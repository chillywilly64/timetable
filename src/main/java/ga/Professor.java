package ga;

import java.util.Objects;

/**
 * Simple Professor abstraction.
 */
public class Professor {
    private int professorId;
    private final String professorName;

    /**
     * Initalize new Professor
     * 
     * @param professorId The ID for this professor
     * @param professorName The name of this professor
     */
    public Professor(int professorId, String professorName){
        this.professorId = professorId;
        this.professorName = professorName;
    }

    /**
     * Initalize new Professor
     *
     * @param professorName The name of this professor
     */
    public Professor(String professorName){
        this.professorName = professorName;
    }
    
    /**
     * Get professorId
     * 
     * @return professorId
     */
    public int getProfessorId(){
        return this.professorId;
    }
    
    /**
     * Get professor's name
     * 
     * @return professorName
     */
    public String getProfessorName(){
        return this.professorName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Professor)) return false;
        Professor professor = (Professor) o;
        return professorId == professor.professorId &&
                Objects.equals(professorName, professor.professorName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(professorId, professorName);
    }
}

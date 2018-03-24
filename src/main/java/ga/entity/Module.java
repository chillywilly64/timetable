package ga.entity;

import java.util.List;
import java.util.Objects;

/**
 * Simple course module abstraction, which defines the Professors teaching the module.
 */
public class Module {
    private int moduleId;
    private final String module;
    private final List<Professor> professors;
    private int numberOfClassesPerWeek;
    
    /**
     * Initialize new Module
     * 
     * @param moduleId
     * @param module
     * @param professors
     */
    public Module(int moduleId, String module, int numberOfClassesPerWeek, List<Professor> professors){
        this.moduleId = moduleId;
        this.module = module;
        this.professors = professors;
        this.numberOfClassesPerWeek = numberOfClassesPerWeek;
    }

    /**
     * Initialize new Module
     *
     * @param module
     * @param professors
     */
    public Module(String module, int numberOfClassesPerWeek, List<Professor> professors){
        this.module = module;
        this.professors = professors;
        this.numberOfClassesPerWeek = numberOfClassesPerWeek;
    }
    
    /**
     * Get moduleId
     * 
     * @return moduleId
     */
    public int getModuleId(){
        return this.moduleId;
    }
    
    /**
     * Get module name
     * 
     * @return moduleName
     */
    public String getModuleName(){
        return this.module;
    }
    
    /**
     * Get random professor Id
     * 
     * @return professorId
     */
    public Professor getRandomProfessor(){
        return professors.get((int) (professors.size() * Math.random()));
    }

    public int getNumberOfClassesPerWeek() {
        return numberOfClassesPerWeek;
    }

    public void incremateNumberOfLecturesPerWeek() {
        numberOfClassesPerWeek++;
    }

    public void addProfessor(Professor professor) {
        if (!professors.contains(professor)) professors.add(professor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Module)) return false;
        Module module1 = (Module) o;
        return moduleId == module1.moduleId &&
                numberOfClassesPerWeek == module1.numberOfClassesPerWeek &&
                Objects.equals(module, module1.module) &&
                Objects.equals(professors, module1.professors);
    }

    @Override
    public int hashCode() {

        return Objects.hash(moduleId, module, professors, numberOfClassesPerWeek);
    }
}

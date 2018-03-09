package ga_old;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Course {

	private String courseName;
	private List<Subject> subjectsIncluded= new ArrayList<Subject>();
	private List<Combination> combinations=new ArrayList<Combination>();
	
	public List<Combination> getCombinations() {
		return combinations;
	}

	public void setCombinations(List<Combination> combinations) {
		this.combinations = combinations;
	}
	private List<StudentGroups> studentGroups=new ArrayList<StudentGroups>();
	
	public List<StudentGroups> getStudentGroups() {
		return studentGroups;
	}

	public void setStudentGroups(List<StudentGroups> studentGroups) {
		this.studentGroups = studentGroups;
	}

	Course(String name, List<Subject> subjects){
		System.out.println("creating new course.......");
		this.courseName=name;
		this.subjectsIncluded=subjects;
	}
	
	public void createStudentGroups(){
		int size=0;
		ArrayList <Combination>combs=new ArrayList<Combination>();
		Iterator<Subject> subjectIterator = subjectsIncluded.iterator();
		while(subjectIterator.hasNext()){
			Subject subject = subjectIterator.next();
			Iterator combIterator =combinations.iterator();
			while(combIterator.hasNext()){
				Combination combination = (Combination) combIterator.next();
				List<String> subjects = combination.getSubjects();
				Iterator<String> subjectItr = subjects.iterator();
				while(subjectItr.hasNext()){
					if(subjectItr.next().equalsIgnoreCase(subject.getSubjectName())){
						size=size+combination.getSizeOfClass();
						if(!combs.contains(combination.getSubjects())){
						combs.add(combination);
						}
					}
				}
			}
			StudentGroups studentGroup=new StudentGroups(this.courseName+"/"+subject.getSubjectName(), subject.getNumberOfLecturesPerWeek(), size, combs, subject.getSubjectName(), subject.isPractic(), subject.getDepartment());
		    studentGroups.add(studentGroup);
		    size=0;
		}
	}
	
	//creates all possible professor x subject he teaches combinations and saves as lecture objects
	
	public void createCombination(int size){
		List<String> subjects = new ArrayList<>();
		for(Subject subject: subjectsIncluded){
			subjects.add(subject.getSubjectName());
		}
		Combination combination=new Combination(subjects, size);
		combinations.add(combination);
	}

	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
//	public ArrayList<Professor> getProfessorsTeaching() {
//		return professorsTeaching;
//	}
//	public void setProfessorsTeaching(ArrayList<Professor> professorsTeaching) {
//		this.professorsTeaching = professorsTeaching;
//	}
	public List<Subject> getSubjectsTaught() {
		return subjectsIncluded;
	}
	public void setSubjectsTaught(List<Subject> subjectsTaught) {
		this.subjectsIncluded = subjectsTaught;
	}
	
}

package ga_old;

import java.util.ArrayList;
import java.util.List;

public class Professor {
	private String professorName;
	private List <String> subjectsTaught = new ArrayList<>();

	public Professor(String name, List<String> subjects){
		this.professorName=name;
		subjectsTaught = subjects;
	}

	public Professor(String name, String subj){
		this.professorName=name;
		String[] subjectNames=subj.split("/");
		for(int i=0; i<subjectNames.length; i++){
			this.subjectsTaught.add(subjectNames[i]);
		}
	}

	public String getProfessorName() {
		return professorName;
	}
	public void setProfessorName(String professorName) {
		this.professorName = professorName;
	}

	public List<String> getSubjectTaught() {
		return subjectsTaught;
	}

	public void setSubjectTaught(List<String> subjectTaught) {
		this.subjectsTaught = subjectTaught;
	}

	public void addSubject(String subject) {
		subjectsTaught.add(subject);
	}
}

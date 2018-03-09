package ga_old;

public class Subject {

	private String subjectName;
	private int numberOfLecturesPerWeek;
	private boolean practic;
	private String department;
	
	public Subject(String name, int lectures, boolean lab, String dept){
		this.subjectName=name;
		this.numberOfLecturesPerWeek=lectures;
		this.practic =lab;
		this.department=dept;
	}
	
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public int getNumberOfLecturesPerWeek() {
		return numberOfLecturesPerWeek;
	}
	public void setNumberOfLecturesPerWeek(int numberOfLecturesPerWeek) {
		this.numberOfLecturesPerWeek = numberOfLecturesPerWeek;
	}

	public boolean isPractic() {
		return practic;
	}

	public void setPractic(boolean practic) {
		this.practic = practic;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void incremateNumberOfLecturesPerWeek() {
		numberOfLecturesPerWeek++;
	}
	
}

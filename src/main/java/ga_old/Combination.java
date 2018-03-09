package ga_old;

import java.util.ArrayList;
import java.util.List;

public class Combination {
	
	private int sizeOfClass;
	private List<String> subjectCombination=new ArrayList<>();

	public Combination(List<String> subjects, int size) {
		// TODO Auto-generated constructor stub
		setSizeOfClass(size);
		subjectCombination = subjects;

	}

	public int getSizeOfClass() {
		return sizeOfClass;
	}

	public void setSizeOfClass(int sizeOfClass) {
		this.sizeOfClass = sizeOfClass;
	}

	public List<String> getSubjects() {
		return subjectCombination;
	}

	public void setSubjects(List<String> subjects) {
		this.subjectCombination = subjects;
	}
}

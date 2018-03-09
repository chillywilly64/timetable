package ga_old;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class Initialization {
	
	//this class takes all inputs from a file. courseID, courseName, roomID's, subjects and professors associated with course
	//currently hardcoded by taking one course with 6 subjects and 6 teachers
	
	private List<Subject> subjects = new ArrayList();
	private List<Professor> professors = new ArrayList();
	private List<TimeTable> timetables = new ArrayList();
	private List<Lecture> classes = new ArrayList<>();
	private List<Combination> combinations = new ArrayList<>();

	//reads input from a file.
	
	public void readInput() throws IOException {
		List<ClassRoom> classroom;
		List<Professor> professors;
		TimeTable timetb1 = null;

		System.out.println("Setting tt.......");
		
		System.out.println("adding tt.......");

		timetables.add(timetb1);

		System.out.println("populating.......");
		
		//display();
		
		populateTimeTable(timetb1);
		GeneticAlgorithm ge=new GeneticAlgorithm();

		ge.populationAccepter(timetables);
	}

	public void readInput2() throws IOException{

		ArrayList<ClassRoom> classroom=new ArrayList<>();
		ClassRoom room1 = new ClassRoom("D101", 20, false, "Common");
		classroom.add(room1);
		ClassRoom room2 = new ClassRoom("E101", 20, false, "ComputerScience");
		classroom.add(room2);
		ClassRoom room3 = new ClassRoom("LAB1", 20, true, "ComputerScience");
		classroom.add(room3);


		professors.add(new Professor("Shruti", "IR/IRlab/DM"));
		professors.add(new Professor("Snehal", "P&S"));
		professors.add(new Professor("Ramrao", "DS"));
		professors.add(new Professor("Ranjit", "WR"));
		professors.add(new Professor("Shekhar", "TOC"));
		professors.add(new Professor("Monica", "SS"));
		professors.add(new Professor("Ravi", "R"));
		professors.add(new Professor("Amit", "ML/MLlab"));
		professors.add(new Professor("Rama", "DAA/UML"));

		createLectures(professors);

		TimeTable timetb1=new TimeTable(classroom, classes);

		int courseid = 1;
		String courseName="MSc.I.T. Part I";
		System.out.println("reading input.......");
		subjects.add(new Subject("IR",4,false, "ComputerScience"));
		subjects.add(new Subject("P&S",4,false,"ComputerScience"));
		subjects.add(new Subject("DS",4,false,"ComputerScience"));
		subjects.add(new Subject("WR",1,false,"Common"));
		subjects.add(new Subject("TOC",4,false,"ComputerScience"));
		subjects.add(new Subject("IRlab",3,true,"ComputerScience"));
		subjects.add(new Subject("JAVA",3,true,"ComputerScience"));


		System.out.println("new course creation.......");
		Course course1 = new Course(courseName, subjects);
		course1.createCombination(20);
		course1.createStudentGroups();
		List<StudentGroups> studentGroups = course1.getStudentGroups();
		timetb1.addStudentGroups(studentGroups);

		subjects.clear();

		subjects.add(new Subject("DM",4,false,"ComputerScience"));
		subjects.add(new Subject("DAA",4,false,"ComputerScience"));
		subjects.add(new Subject("SS",1,false,"ComputerScience"));
		subjects.add(new Subject("ML",4,false,"Common"));
		subjects.add(new Subject("UML",4,false,"ComputerScience"));
		subjects.add(new Subject("MLlab",3,true,"ComputerScience"));
		subjects.add(new Subject("R",3,true,"ComputerScience"));

		Course course2 = new Course("MSc.I.T. Part II", subjects);
		course2.createCombination(20);
		course2.createStudentGroups();
		studentGroups = course2.getStudentGroups();
		timetb1.addStudentGroups(studentGroups);

		System.out.println("Setting tt.......");

		System.out.println("adding tt.......");
		timetb1.initializeTimeTable();
		timetables.add(timetb1);


		System.out.println("populating.......");



		//display();

		populateTimeTable(timetb1);
		GeneticAlgorithm ge=new GeneticAlgorithm();

		ge.populationAccepter(timetables);
}

	public void populateTimeTable(TimeTable timetb1){
		int i=0;
		System.out.println("populating started.......");
		while(i<3){
			TimeTable tempTimetable = timetb1;
			List<ClassRoom> allrooms = tempTimetable.getRoom();
			Iterator<ClassRoom> allroomsIterator = allrooms.iterator();
			while(allroomsIterator.hasNext()){
				ClassRoom room = allroomsIterator.next();
				List<Day> weekdays = room.getWeek().getWeekDays();
				Collections.shuffle(weekdays);
				if(!room.isLaboratory()){
					Iterator<Day> daysIterator=weekdays.iterator();
					while(daysIterator.hasNext()){
						Day day = daysIterator.next();
						Collections.shuffle(day.getTimeSlot());
					}
				}				
			}
			timetables.add(tempTimetable);
			i++;
		}
		System.out.println("populating done.......");
		System.out.println("display called.......");
		display();
	}
	
	private void createLectures (List<Professor> professors) {
		// TODO Auto-generated method stub
		
		Iterator<Professor> professorIterator=professors.iterator();
		while(professorIterator.hasNext()){
			Professor professor=professorIterator.next();
			List<String> subjectsTaught = professor.getSubjectTaught();
			Iterator<String> subjectIterator = subjectsTaught.iterator();
			while(subjectIterator.hasNext()){
				String subject = subjectIterator.next();
				classes.add(new Lecture (professor, subject));
			}
		}
	}
	
	//creates another 3 timetable objects for population by taking first yimetable and shuffling it.
	
//	public void populateTimeTable(){
//		int i=0;
//		System.out.println("populating started.......");
//		while(i<6){
//			TimeTable tempTimetable = timetbl;
//			ArrayList<ClassRoom> allrooms = tempTimetable.getRoom();
//			Iterator<ClassRoom> allroomsIterator = allrooms.iterator();
//			while(allroomsIterator.hasNext()){
//				ClassRoom room = allroomsIterator.next();
//				ArrayList<Day> weekdays = room.getWeek().getWeekDays();
//				Iterator<Day> daysIterator=weekdays.iterator();
//				while(daysIterator.hasNext()){
//					Day day = daysIterator.next();
//					Collections.shuffle(day.getTimeSlot());
//				}
//			}
//			timetable.add(tempTimetable);
//			i++;
//		}
//		System.out.println("populating done.......");
//		System.out.println("display called.......");
//		display();
//		
//		GeneticAlgorithm.populationAccepter(timetable);
//	}
	
	//displays all timetables
	
	private void display() {
		// TODO Auto-generated method stub
		int i=1;
		System.out.println("displaying all tt's.......");
		Iterator<TimeTable> timetableIterator = timetables.iterator();
		while(timetableIterator.hasNext()){
			System.out.println("+++++++++++++++++++++++++++++++++++++++++\nTime Table no. "+i);
			TimeTable currentTimetable = timetableIterator.next();
			System.out.println("Score : "+currentTimetable.getFittness());
			List<ClassRoom> allrooms = currentTimetable.getRoom();
			Iterator<ClassRoom> allroomsIterator = allrooms.iterator();
			while(allroomsIterator.hasNext()){
				ClassRoom room = allroomsIterator.next();
				System.out.println("Room: "+room.getRoomNo());
				ArrayList<Day> weekdays = room.getWeek().getWeekDays();
				Iterator<Day> daysIterator=weekdays.iterator();
				while(daysIterator.hasNext()){
					Day day = daysIterator.next();
					ArrayList<TimeSlot> timeslots = day.getTimeSlot();
					Iterator<TimeSlot> timeslotIterator= timeslots.iterator();
					//System.out.print(""+day.getName()+": ");
					while(timeslotIterator.hasNext()){
						TimeSlot lecture = (TimeSlot) timeslotIterator.next();
						if(lecture.getLecture()!=null){
						//System.out.print(" (Subject: "+lecture.getLecture().getSubject()+" --> Professor: "+lecture.getLecture().getProfessor().getProfessorName()+" GrpName: "+lecture.getLecture().getStudentGroup().getName()+")");
							System.out.print("("+lecture.getLecture().getSubject()+"#"+lecture.getLecture().getProfessor().getProfessorName()+"#"+lecture.getLecture().getStudentGroup().getName().split("/")[0]+")");
						}
						else{
							System.out.print("   free   ");
						}
					}
					System.out.print("\n");
				}
				System.out.print("\n\n");
			}
			i++;
		}
	}
}

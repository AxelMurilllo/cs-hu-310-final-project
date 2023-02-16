import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This application will keep track of things like what classes are offered by
 * the school, and which students are registered for those classes and provide
 * basic reporting. This application interacts with a database to store and
 * retrieve data.
 */
public class SchoolManagementSystem {
	
	/** List instructor and class sections taught by instructor
	 * 
	 * @param first_name first name of instructor
	 * @param last_name last name of instructor
	 */
    public static void getAllClassesByInstructor(String first_name, String last_name) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
             /* Your logic goes here */
        	connection = Database.getDatabaseConnection();
        	if(connection != null) {
        		sqlStatement = connection.createStatement();
        		
        		String sql = String.format("SELECT instructors.first_name, instructors.last_name, \n"
        				+ "academic_titles.title AS title, classes.code, classes.name AS class_name,\n"
        				+ "terms.name AS term\n"
        				+ "FROM instructors, classes, academic_titles, terms, class_sections\n"
        				+ "WHERE instructors.academic_title_id = academic_titles.academic_title_id\n"
        				+ "AND terms.term_id = class_sections.term_id\n"
        				+ "AND instructors.instructor_id = class_sections.instructor_id\n"
        				+ "AND class_sections.class_id = classes.class_id\n"
        				+ "AND instructors.first_name = '%s'\n"
        				+ "AND instructors.last_name = '%s';", first_name, last_name);
        		ResultSet resultSet = sqlStatement.executeQuery(sql);
        		System.out.println("First Name | Last Name | Title | Code | Name | Term");
        		System.out.println("-".repeat(80));
        		
        		while(resultSet.next()) {
        			String firstName = resultSet.getString("first_name");
        			String lastName = resultSet.getString("last_name");
        			String title = resultSet.getString("title");
        			String code = resultSet.getString("code");
        			String name = resultSet.getString("class_name");
        			String term = resultSet.getString("term");
        			System.out.println(firstName +" | "+lastName+" | "+title+" | "+code+" | "+name+" | "+term);
        		}
        	} else {
        		System.out.println("Connection failed");
        	}  
        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }
    
    /** Changes students grade in specified class section
     * 
     * @param studentId studentID of students grade to be changed
     * @param classSectionID class to change grade
     * @param grade the grade to be set by user
     */
    public static void submitGrade(String studentId, String classSectionID, String grade) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
            /* Your logic goes here */
       	connection = Database.getDatabaseConnection();
       	if(connection != null) {
       		sqlStatement = connection.createStatement();
       		
       		String sql = String.format ("UPDATE class_registrations \n"
       				+ "SET grade_id = (SELECT grade_id FROM grades WHERE letter_grade = '%s')\n"
       				+ "WHERE class_section_id = %s\n"
       				+ "AND student_id = %s;", grade, classSectionID, studentId);
       		sqlStatement.executeUpdate(sql);
       		System.out.println("-".repeat(80));
       		System.out.println("Grade has been submitted!");
       	} else {
       		System.out.println("Connection failed");
       	}
        } catch (SQLException sqlException) {
            System.out.println("Failed to submit grade");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /**Registers student into specified class section
     * 
     * @param studentId
     * @param classSectionID
     */
    public static void registerStudent(String studentId, String classSectionID) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
             /* Your logic goes here */
        	connection = Database.getDatabaseConnection();
        	if(connection != null) {
        		sqlStatement = connection.createStatement();
        		
        		String sql = String.format("INSERT INTO class_registrations(student_id, class_section_id) VALUES(%s, %s);", studentId, classSectionID);
        		sqlStatement.executeUpdate(sql);
        		
        		sql = String.format("SELECT class_registration_id, student_id, class_section_id\n"
        				+ "FROM class_registrations\n"
        				+ "WHERE student_id = %s\n"
        				+ "AND class_section_id = %s;", studentId, classSectionID);
        		ResultSet resultSet = sqlStatement.executeQuery(sql);
        		System.out.println("Class Registration ID | Student ID | Class Section ID ");
        		System.out.println("-".repeat(80));
        		
        		while(resultSet.next()) {
        			int classRegID = resultSet.getInt("class_registration_id");
        			int studentID = resultSet.getInt("student_id");
        			int classSecID = resultSet.getInt("class_section_id");
        			System.out.println(classRegID +" | "+studentID+" | "+classSecID);
        		}
        	} else {
        		System.out.println("Connection failed");
        	}
        } catch (SQLException sqlException) {
            System.out.println("Failed to register student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /**
     * Deletes student with inputted studentID
     * <p>Confirms deletion of student
     * 
     * @param studentId the studentID of student being deleted
     */
    public static void deleteStudent(String studentId) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
             /* Your logic goes here */
        	connection = Database.getDatabaseConnection();
        	if(connection != null) {
        		sqlStatement = connection.createStatement();
        		
        		String sql = String.format ("DELETE FROM students WHERE student_id = %s;", studentId);
        		int output = sqlStatement.executeUpdate(sql);
        		if(output == 1) {
            		System.out.println("-".repeat(80));
            		System.out.println(String.format("Student with id: %s was deleted", studentId));
        		} else {
            		System.out.println("-".repeat(80));
            		System.out.println(String.format("Student with id: %s does not exist", studentId));
        		}

        	} else {
        		System.out.println("Connection failed");
        	}
        } catch (SQLException sqlException) {
            System.out.println("Failed to delete student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /**
     * Updates SQL students table to add new student
     * <p>Returns created student
     * 
     * @param firstName first name of created student
     * @param lastName last name of created student
     * @param birthdate birthdate of created student in SQL Date format
     */
    public static void createNewStudent(String firstName, String lastName, String birthdate) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
             /* Your logic goes here */
        	connection = Database.getDatabaseConnection();
        	if(connection != null) {
        		sqlStatement = connection.createStatement();
        		
        		String sql = String.format("INSERT INTO students(first_name, last_name, birthdate) VALUES ('%s', '%s', '%s');",firstName,lastName,birthdate);
        		sqlStatement.executeUpdate(sql);
        		
        		sql = String.format("SELECT *\n"
        				+ "FROM students\n"
        				+ "WHERE first_name = '%s'\n"
        				+ "AND last_name = '%s'\n"
        				+ "AND birthdate = '%s';", firstName, lastName, birthdate);
        		ResultSet resultSet = sqlStatement.executeQuery(sql);
        		System.out.println("Student ID | First Name | Last Name | Birthdate");
        		System.out.println("-".repeat(80));
        		while(resultSet.next()) {
        			int studentID = resultSet.getInt("student_id");
        			String studentFirstName = resultSet.getString("first_name");
        			String studentLastName = resultSet.getString("last_name");
        			String studentBirthdate = resultSet.getString("birthdate");
        			System.out.println(studentID +" | "+studentFirstName+" | "+studentLastName+" | "+studentBirthdate);
        		}
        	} else {
        		System.out.println("Connection failed");
        	}
        } catch (SQLException sqlException) {
            System.out.println("Failed to create student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    /*
     * Lists class_registrations table
     */
    public static void listAllClassRegistrations() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
             /* Your logic goes here */
        	connection = Database.getDatabaseConnection();
        	if(connection != null) {
        		sqlStatement = connection.createStatement();
        		
        		String sql = "SELECT students.student_id, class_sections.class_section_id, students.first_name AS first_name,  students.last_name AS last_name,\n"
        				+ "classes.code AS code, classes.name AS name, terms.name AS term, grades.letter_grade as grade\n"
        				+ "FROM class_sections\n"
        				+ "JOIN class_registrations ON class_registrations.class_section_id = class_sections.class_section_id\n"
        				+ "JOIN classes ON classes.class_id = class_sections.class_id\n"
        				+ "JOIN students ON students.student_id = class_registrations.student_id\n"
        				+ "JOIN terms ON terms.term_id = class_sections.term_id\n"
        				+ "JOIN grades ON grades.grade_id = class_registrations.grade_id\n"
        				+ "GROUP BY classes.class_id, terms.term_id, students.first_name, students.last_name, students.student_id, class_sections.class_section_id;";
        		ResultSet resultSet = sqlStatement.executeQuery(sql);
        		System.out.println("Student ID | class_section_id | First Name | Last Name | Code | Name | Term | Letter Grade");
        		System.out.println("-".repeat(80));
        		
        		while(resultSet.next()) {
        			int studentSectionID = resultSet.getInt("student_id");
        			int classSectionID = resultSet.getInt("class_section_id");
        			String firstName = resultSet.getString("first_name");
        			String lastName = resultSet.getString("last_name");
        			String code = resultSet.getString("code");
        			String name = resultSet.getString("name");
        			String term = resultSet.getString("term");
        			String grade = resultSet.getString("grade");
        			System.out.println(studentSectionID +" | "+classSectionID+" | "+firstName+" | "+lastName+" | "+code+" | "
        			+name+" | "+term+" | "+grade);
        		}
        	} else {
        		System.out.println("Connection failed");
        	}
        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /*
     * list class_sections table
     */
    public static void listAllClassSections() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
             /* Your logic goes here */
        	connection = Database.getDatabaseConnection();
        	if(connection != null) {
        		sqlStatement = connection.createStatement();
        		
        		String sql = "SELECT class_sections.class_section_id, classes.code AS code, classes.name AS name, terms.name AS term\n"
        				+ "FROM class_sections\n"
        				+ "JOIN classes ON classes.class_id = class_sections.class_id\n"
        				+ "JOIN terms ON terms.term_id = class_sections.term_id\n"
        				+ "GROUP BY classes.class_id, terms.term_id, class_sections.class_section_id;";
        		ResultSet resultSet = sqlStatement.executeQuery(sql);
        		System.out.println("Class Section ID | Code | Name | term");
        		System.out.println("-".repeat(80));
        		
        		while(resultSet.next()) {
        			int classSectionID = resultSet.getInt("class_section_id");
        			String code = resultSet.getString("code"); //from classes
        			String name = resultSet.getString("name"); //from classes
        			String term = resultSet.getString("term"); //from terms
        			System.out.println(classSectionID +" | "+code+" | "+name+" | "+term);
        		}
        	} else {
        		System.out.println("Connection failed");
        	}
        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /*
     * list classes table
     */
    public static void listAllClasses() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
             /* Your logic goes here */
        	connection = Database.getDatabaseConnection();
        	if(connection != null) {
        		sqlStatement = connection.createStatement();
        		
        		String sql = "SELECT * FROM classes;";
        		ResultSet resultSet = sqlStatement.executeQuery(sql);
        		System.out.println("Class ID | Code | Name | Description");
        		System.out.println("-".repeat(80));
        		
        		while(resultSet.next()) {
        			int classID = resultSet.getInt("class_id");
        			String code = resultSet.getString("code");
        			String name = resultSet.getString("name");
        			String description = resultSet.getString("description");
        			System.out.println(classID +" | "+code+" | "+name+" | "+description);
        		}
        	} else {
        		System.out.println("Connection failed");
        	}
        } catch (SQLException sqlException) {
            System.out.println("Failed to get students");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /*
     * list students table
     */
    public static void listAllStudents() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
             /* Your logic goes here */
        	connection = Database.getDatabaseConnection();
        	if(connection != null) {
        		sqlStatement = connection.createStatement();
        		
        		String sql = "SELECT * FROM students;";
        		ResultSet resultSet = sqlStatement.executeQuery(sql);
        		System.out.println("Student ID | First Name | Last Name | Birthdate");
        		System.out.println("-".repeat(80));
        		
        		while(resultSet.next()) {
        			int studentID = resultSet.getInt("student_id");
        			String firstName = resultSet.getString("first_name");
        			String lastName = resultSet.getString("last_name");
        			Date birthDate = resultSet.getDate("birthdate");
        			System.out.println(studentID +" | "+firstName+" | "+lastName+" | "+birthDate);
        		}
        	} else {
        		System.out.println("Connection failed");
        	}
        } catch (SQLException sqlException) {
            System.out.println("Failed to get students");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /***
     * Splits a string up by spaces. Spaces are ignored when wrapped in quotes.
     *
     * @param command - School Management System cli command
     * @return splits a string by spaces.
     */
    public static List<String> parseArguments(String command) {
        List<String> commandArguments = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
        while (m.find()) commandArguments.add(m.group(1).replace("\"", ""));
        return commandArguments;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the School Management System");
        System.out.println("-".repeat(80));

        Scanner scan = new Scanner(System.in);
        String command = "";

        do {
            System.out.print("Command: ");
            command = scan.nextLine();
            ;
            List<String> commandArguments = parseArguments(command);
            command = commandArguments.get(0);
            commandArguments.remove(0);

            if (command.equals("help")) {
                System.out.println("-".repeat(38) + "Help" + "-".repeat(38));
                System.out.println("test connection \n\tTests the database connection");

                System.out.println("list students \n\tlists all the students");
                System.out.println("list classes \n\tlists all the classes");
                System.out.println("list class_sections \n\tlists all the class_sections");
                System.out.println("list class_registrations \n\tlists all the class_registrations");
                System.out.println("list instructor <first_name> <last_name>\n\tlists all the classes taught by that instructor");


                System.out.println("delete student <studentId> \n\tdeletes the student");
                System.out.println("create student <first_name> <last_name> <birthdate> \n\tcreates a student");
                System.out.println("register student <student_id> <class_section_id>\n\tregisters the student to the class section");

                System.out.println("submit grade <studentId> <class_section_id> <letter_grade> \n\tcreates a student");
                System.out.println("help \n\tlists help information");
                System.out.println("quit \n\tExits the program");
            } else if (command.equals("test") && commandArguments.get(0).equals("connection")) {
                Database.testConnection();
            } else if (command.equals("list")) {
                if (commandArguments.get(0).equals("students")) listAllStudents();
                if (commandArguments.get(0).equals("classes")) listAllClasses();
                if (commandArguments.get(0).equals("class_sections")) listAllClassSections();
                if (commandArguments.get(0).equals("class_registrations")) listAllClassRegistrations();

                if (commandArguments.get(0).equals("instructor")) {
                    getAllClassesByInstructor(commandArguments.get(1), commandArguments.get(2));
                }
            } else if (command.equals("create")) {
                if (commandArguments.get(0).equals("student")) {
                    createNewStudent(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
                }
            } else if (command.equals("register")) {
                if (commandArguments.get(0).equals("student")) {
                    registerStudent(commandArguments.get(1), commandArguments.get(2));
                }
            } else if (command.equals("submit")) {
                if (commandArguments.get(0).equals("grade")) {
                    submitGrade(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
                }
            } else if (command.equals("delete")) {
                if (commandArguments.get(0).equals("student")) {
                    deleteStudent(commandArguments.get(1));
                }
            } else if (!(command.equals("quit") || command.equals("exit"))) {
                System.out.println(command);
                System.out.println("Command not found. Enter 'help' for list of commands");
            }
            System.out.println("-".repeat(80));
        } while (!(command.equals("quit") || command.equals("exit")));
        System.out.println("Bye!");
    }
}


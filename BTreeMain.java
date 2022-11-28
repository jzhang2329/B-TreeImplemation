import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main Application.
 */
public class BTreeMain {

    static long currRecordId;

    public static void main(String[] args) throws IOException {


        /** Read the input file -- input.txt */
        Scanner scan = null;
        try {
            scan = new Scanner(new File("input.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }

        /** Read the minimum degree of B+Tree first */

        int degree = scan.nextInt();

        BTree bTree = new BTree(degree);

        /** Reading the database student.csv into B+Tree Node*/
        List<Student> studentsDB = getStudents();

        for (Student s : studentsDB) {
            bTree.insert(s);
        }

       try {
            while (scan.hasNextLine()) {
                Scanner s2 = new Scanner(scan.nextLine());

                while (s2.hasNext()) {

                    String operation = s2.next();

                    switch (operation) {
                        case "insert": {

                            long studentId = Long.parseLong(s2.next());
                            String studentName = s2.next() + " " + s2.next();
                            String major = s2.next();
                            String level = s2.next();
                            int age = Integer.parseInt(s2.next());

                            long recordID = -1;
                            if(s2.hasNext()){
                                recordID = Long.parseLong(s2.next());
                            }else {
                                recordID = currRecordId + 1;
                                currRecordId = recordID;
                            }
                            Student s = new Student(studentId, age, studentName, major, level, recordID);
                            bTree.insert(s);

                            BufferedWriter writer = new BufferedWriter(new FileWriter("Student.csv", true));
                            writer.write(s.toString());
                            writer.close();

                            break;
                        }
                        case "delete": {
                            long studentId = Long.parseLong(s2.next());
                            boolean result = bTree.delete(studentId);
                            if (result)
                                System.out.println("Student deleted successfully.");
                            else
                                System.out.println("Student deletion failed.");

                            Student temp = null;
                            for (Student s : studentsDB) {
                                if (s.studentId == studentId){
                                    temp = s;
                                    break;
                                }
                            }
                            studentsDB.remove(temp);
                            BufferedWriter writer = new BufferedWriter(new FileWriter("Student.csv", false));
                            for (Student s : studentsDB) {
                                writer.write(s.toString());
                            }
                            writer.close();

                            break;
                        }
                        case "search": {
                            long studentId = Long.parseLong(s2.next());
                            long recordID = bTree.search(studentId);
                            if (recordID != -1)
                                System.out.println("Student exists in the database at " + recordID);
                            else
                                System.out.println("Student does not exist.");
                            break;
                        }
                        case "print": {
                            List<Long> listOfRecordID = new ArrayList<>();
                            listOfRecordID = bTree.print();
                            System.out.println("List of recordIDs in B+Tree " + listOfRecordID.toString());
                        }
                        default:
                            System.out.println("Wrong Operation");
                            break;
                    }
                }
            }
       } catch (Exception e) {
            e.printStackTrace();
       }
        System.out.print(bTree.print());
    }

    private static List<Student> getStudents() throws IOException {


        List<Student> studentList = new ArrayList<>();
        BufferedReader csvReader = new BufferedReader(new FileReader("Student.csv"));
        long studentId;
        long recordId;
        int age;
        String studentName;
        String major;
        String level;
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            studentId = Long.parseLong(data[0]);
            studentName = data[1];
            major = data[2];
            level = data[3];
            age = Integer.parseInt(data[4]);
            recordId = Long.parseLong(data[5]);
            Student s = new Student(studentId, age, studentName, major, level, recordId);
            studentList.add(s);
            currRecordId = recordId; 
        }
        csvReader.close();
        return studentList;
    }
}

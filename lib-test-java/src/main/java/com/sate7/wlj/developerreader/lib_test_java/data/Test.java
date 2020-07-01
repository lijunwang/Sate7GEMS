package com.sate7.wlj.developerreader.lib_test_java.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.naming.BinaryRefAddr;

public class Test {
    private ArrayList<Student> studentsList = new ArrayList<>();
    Comparator<Student> comparator = new Comparator<Student>() {
        @Override
        public int compare(Student student, Student t1) {
            return student.compareTo(t1);
        }
    };
    private void addStudent(Student student){
        HashSet<Student> studentSet = new HashSet<>();
        studentSet.addAll(studentsList);
        studentSet.add(student);
        studentsList.clear();
        studentsList.addAll(studentSet);
        studentsList.sort(comparator);
    }

    private void addStudentList(List<Student> studentList){
        HashSet<Student> studentSet = new HashSet<>();
        studentSet.addAll(studentsList);
        studentSet.addAll(studentList);

        studentsList.clear();
        studentsList.addAll(studentSet);
        studentsList.sort(comparator);
    }
    public static void main(String[] args) {
        Test test = new Test();
        test.addStudent(new Student("wang",24));
        test.addStudent(new Student("li",25));
        test.addStudent(new Student("jun",26));
        test.addStudent(new Student("jun",26));
        System.out.println("after add :" + test.studentsList);
//        ArrayList<Student> students = new ArrayList<>();
//        students.add(new Student("wang",23));
//        students.add(new Student("wang",24));
//        System.out.println("students == " + students);
//        System.out.println(test.studentsList);
//        test.addStudentList(students);
//        System.out.println(test.studentsList);

    }
}

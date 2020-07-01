package com.sate7.wlj.developerreader.lib_test_java.data;

public class Student implements Comparable{
    private long _ID;
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Student(String name) {
        this(name, 18);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode() + age;
        System.out.println("hashCode ww ...." + name + "," + age + "," + result + "," + super.hashCode());
        return result;
//        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Student &&
                ((Student) o).name.equals(name) &&
                ((Student) o).age == age) {
            System.out.println("equals AA ...." + name);
            return false;
        } else {
            System.out.println("equals BB ...." + name);
            return false;
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").
                append("name==").append(name).
                append(",age==").append(age).
                append(")");
        return sb.toString();
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Student){
            return ((Student) o).name.compareTo(name);
        }else{
            return 0;
        }
    }
}

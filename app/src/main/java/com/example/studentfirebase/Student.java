package com.example.studentfirebase;

public class Student {

    private String id;
    private String name;
    private String group;
    private String age;
    private String birthDate;

    public Student() {
    }

    public Student(String id, String name, String group, String age, String birthDate) {
        this.id = id;
        this.name = name;
        this.group = group;
        this.age = age;
        this.birthDate = birthDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getAge() {
        return age;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
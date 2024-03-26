package com.example.batchprocessing;

import java.sql.Date;
import java.time.LocalDate;

//NOTE. record 클래스는 데이터를 저장하기 위해 사용되며, 그 외에 메서드를 추가하거나 상속을 받지 않음
// 이러한 특성으로 인해 record 클래스는 간단하게 데이터를 표현하고 다루는 데 유용.
// 코드를 간결하게 만들어주고 데이터의 불변성을 보장함으로써 코드의 안정성을 높이는 데 도움이 됨
public class TodayRegisteredUserDTO {
    private long personId;
    private String firstName;
    private String lastName;
    private String gender;
    private boolean married;
    private int age;
    private String address;
    private LocalDate joinDate;

    private LocalDate workDate;

    public TodayRegisteredUserDTO() {
    }

    public TodayRegisteredUserDTO(long personId, String firstName, String lastName, String gender, boolean married, int age, String address, LocalDate joinDate) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.married = married;
        this.age = age;
        this.address = address;
        this.joinDate = joinDate;
        this.workDate = workDate;
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isMarried() {
        return married;
    }

    public void setMarried(boolean married) {
        this.married = married;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }
}

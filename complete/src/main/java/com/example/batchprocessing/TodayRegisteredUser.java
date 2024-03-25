package com.example.batchprocessing;
//NOTE. record 클래스는 데이터를 저장하기 위해 사용되며, 그 외에 메서드를 추가하거나 상속을 받지 않음
// 이러한 특성으로 인해 record 클래스는 간단하게 데이터를 표현하고 다루는 데 유용.
// 코드를 간결하게 만들어주고 데이터의 불변성을 보장함으로써 코드의 안정성을 높이는 데 도움이 됨
public class TodayRegisteredUser {
    private String firstName;
    private String lastName;
    private String gender;
    private boolean married;
    private int age;
    private String address;

    public TodayRegisteredUser() {
    }

    public TodayRegisteredUser(String firstName, String lastName, String gender, boolean married, int age, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.married = married;
        this.age = age;
        this.address = address;
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
}

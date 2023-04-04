package com.GLAS.LakeDistrictNavigation;

public class Survey {

    //Strings for storing stuff...
    //Everything needs to be private
    private String age;
    private String gender;
    private String group;
    private String employment;
    private String arrival;
    private Boolean useLocation;

    //We need seters and geters for everything to make this work...
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getEmployment() {
        return employment;
    }

    public void setEmployment(String employment) {
        this.employment = employment;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public Boolean getUseLocation() {return useLocation;}
    public void setUseLocation(Boolean useLocation){this.useLocation = useLocation;}
}

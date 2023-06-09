package com.mindtoheart.licenta;

public class ReadWriteUserDetails {
    public String fullName, doB, gender ;

    //Constructor
    public ReadWriteUserDetails(){}
    public ReadWriteUserDetails( String fullName, String doB, String gender) {
        this.fullName=fullName;
        this.doB = doB;
        this.gender = gender;
    }

}

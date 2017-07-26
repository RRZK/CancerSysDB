package de.cancersysdb

class UserDetail {

    String firstName
    String lastName
    String academicTitle
    String institution
    String homepage

    User user


    static embedded = ['adress']

    static belongsTo= [user:User]


    static constraints = {
        academicTitle nullable: true
        institution nullable: true
        homepage nullable: true
    }
}

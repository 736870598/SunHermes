package com.sunxy.sunhermes.dao;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/28 0028.
 */
public class UserManager implements IUserManager{
    private static final UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
    }

    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

}

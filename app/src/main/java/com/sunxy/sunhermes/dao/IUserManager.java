package com.sunxy.sunhermes.dao;

import com.sunxy.hermes.core.annotion.ClassId;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/28 0028.
 */
@ClassId("com.sunxy.sunhermes.dao.UserManager")
public interface IUserManager {

    Person getPerson();
    void setPerson(Person person);

}

package com.cyb.mobile.domain;

import java.util.Date;

/**
 * @ClassName：NbaPlayer
 * @Description：TODO
 * @Author：chenyb
 * @Date：2020/8/17 11:54 上午
 * @Versiion：1.0
 */
public class NbaPlayer {
    private Integer id;
    private String countryEn;
    private String teamName;
    private Long birthDay;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountryEn() {
        return countryEn;
    }

    public void setCountryEn(String countryEn) {
        this.countryEn = countryEn;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Long getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Long birthDay) {
        this.birthDay = birthDay;
    }

    @Override
    public String toString() {
        return "NbaPlayer{" +
                "id=" + id +
                ", countryEn='" + countryEn + '\'' +
                ", teamName='" + teamName + '\'' +
                ", birthDay=" + birthDay +
                '}';
    }
}

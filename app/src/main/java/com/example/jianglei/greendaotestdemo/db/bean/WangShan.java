package com.example.jianglei.greendaotestdemo.db.bean;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

// KEEP INCLUDES END
/**
 * Entity mapped to table "WANG_SHAN".
 */
public class WangShan {

    private Long id;
    /** Not-null value. */
    private String wangshan;
    private int age;

    // KEEP FIELDS - put your custom fields here

    // KEEP FIELDS END

    public WangShan() {
    }

    public WangShan(Long id) {
        this.id = id;
    }

    public WangShan(Long id, String wangshan, int age) {
        this.id = id;
        this.wangshan = wangshan;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getWangshan() {
        return wangshan;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setWangshan(String wangshan) {
        this.wangshan = wangshan;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // KEEP METHODS - put your custom methods here

    // KEEP METHODS END

}
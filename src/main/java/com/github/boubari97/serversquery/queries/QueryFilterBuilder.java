package com.github.boubari97.serversquery.queries;

public class QueryFilterBuilder {

    private String filter = "";

    public QueryFilterBuilder(){
        // empty constructor
    }


    public QueryFilterBuilder nor() {
        this.filter += "\\nor";
        return this;
    }

    public QueryFilterBuilder nand() {
        this.filter += "\\nand";
        return this;
    }

    public QueryFilterBuilder dedicated() {
        this.filter += "\\dedicated\\1";
        return this;
    }

    public QueryFilterBuilder secure() {
        this.filter += "\\secure\\1";
        return this;
    }

    public QueryFilterBuilder game(String game) {
        this.filter += "\\gamedir\\" + game;
        return this;
    }

    public QueryFilterBuilder map(String map) {
        this.filter += "\\map\\" + map;
        return this;
    }

    public QueryFilterBuilder linux() {
        this.filter += "\\linux\\1";
        return this;
    }

    public QueryFilterBuilder password() {
        this.filter += "\\password\\0";
        return this;
    }

    public QueryFilterBuilder notEmpty() {
        this.filter += "\\empty\\1";
        return this;
    }

    public QueryFilterBuilder notFull() {
        this.filter += "\\full\\1";
        return this;
    }

    public QueryFilterBuilder proxy() {
        this.filter += "\\proxy\\1";
        return this;
    }

    public QueryFilterBuilder appId(int id) {
        this.filter += "\\appid\\" + id;
        return this;
    }

    public QueryFilterBuilder napp(int value) {
        this.filter += "\\napp\\" + value;
        return this;
    }

    public QueryFilterBuilder isEmpty() {
        this.filter += "\\noplayers\\1";
        return this;
    }

    public QueryFilterBuilder white() {
        this.filter += "\\white\\1";
        return this;
    }

    public QueryFilterBuilder gameType(String type) {
        this.filter += "\\gametype\\" + type;
        return this;
    }

    public QueryFilterBuilder gameData(String data) {
        this.filter += "\\gamedata\\" + data;
        return this;
    }

    public QueryFilterBuilder gameDataOr(String data) {
        this.filter += "\\gamedataor\\" + data;
        return this;
    }

    public QueryFilterBuilder nameMatches(String name) {
        this.filter += "\\name_match\\" + name;
        return this;
    }

    public QueryFilterBuilder searchMatcherByNameExist(String name) {
        this.filter += "\\name_match\\*" + name + "*";
        return this;
    }

    public QueryFilterBuilder versionMatches(String version) {
        this.filter += "\\version_match\\" + version;
        return this;
    }

    public QueryFilterBuilder unique() {
        this.filter += "\\collapse_addr_hash\\1";
        return this;
    }

    public QueryFilterBuilder addressMatches(String ip) {
        this.filter += "\\collapse_addr_hash\\" + ip;
        return this;
    }


    public String build() {
        return filter + "\0";
    }

}

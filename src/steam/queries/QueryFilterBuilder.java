package steam.queries;

public class QueryFilterBuilder {

    private String filter = "";

    public QueryFilterBuilder(){}


    public QueryFilterBuilder nor() {
        this.filter += "\\nor\\";
        return this;
    }

    public QueryFilterBuilder nand() {
        this.filter += "\\nand\\";
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

    public QueryFilterBuilder game(String v) {
        this.filter += "\\gamedir\\" + v;
        return this;
    }

    public QueryFilterBuilder map(String v) {
        this.filter += "\\map\\" + v;
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

    public QueryFilterBuilder appid(int v) {
        this.filter += "\\appid\\" + v;
        return this;
    }

    public QueryFilterBuilder napp(int v) {
        this.filter += "\\napp\\" + v;
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

    public QueryFilterBuilder gametype(String v) {
        this.filter += "\\gametype\\" + v;
        return this;
    }

    public QueryFilterBuilder gamedata(String v) {
        this.filter += "\\gamedata\\" + v;
        return this;
    }

    public QueryFilterBuilder gamedataor(String v) {
        this.filter += "\\gamedataor\\" + v;
        return this;
    }

    public QueryFilterBuilder nameMatches(String v) {
        this.filter += "\\name_match\\" + v;
        return this;
    }

    public QueryFilterBuilder search(String v) {
        this.filter += "\\name_match\\*" + v + "*";
        return this;
    }

    public QueryFilterBuilder versionMatches(String v) {
        this.filter += "\\version_match\\" + v;
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

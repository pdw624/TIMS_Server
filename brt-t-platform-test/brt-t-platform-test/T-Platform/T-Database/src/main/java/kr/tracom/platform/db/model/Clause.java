package kr.tracom.platform.db.model;

import lombok.Data;

@Data
public class Clause {
    private String column;
    private String operator;
    private String value;

    public Clause(String column, String operator, String value){
        this.column = column;
        this.operator = operator;
        this.value = value;
    }
}

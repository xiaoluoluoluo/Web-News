package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableReqDTO {
    private Integer pageSize;
    private Integer currentPage;
    private String queryText;

    //根据pageSize和currentPage计算起始条数用户表格查询
    public Integer getStart() {
        return (this.currentPage - 1) * this.pageSize;
    }

}

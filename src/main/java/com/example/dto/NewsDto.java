package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsDto {
    private Integer id;
    private String title;
    private String description;
    private String imageurl;
    private String context;
    private String editor;
    private String annex;
}

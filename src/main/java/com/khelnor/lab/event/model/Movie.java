package com.khelnor.lab.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie implements Serializable {

    private String title;
    private String director;
    private List<String> actors;
    private int rating;
}

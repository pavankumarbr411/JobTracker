
package com.example.jobtracker;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "job_applications")
public class JobApplication {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String company;
    public String jobTitle;
    public String location;
    public String status;
    public String jobUrl;

    // Room uses this no-argument constructor
    public JobApplication() {
    }

    // Used when creating a new job application
    @Ignore
    public JobApplication(String company, String jobTitle,
                          String location, String status, String jobUrl) {
        this.company = company;
        this.jobTitle = jobTitle;
        this.location = location;
        this.status = status;
        this.jobUrl = jobUrl;
    }
}

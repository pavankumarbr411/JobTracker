
package com.example.jobtracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface JobApplicationDao {

    @Insert
    void insert(JobApplication application);

    @Update
    void update(JobApplication application);

    @Delete
    void delete(JobApplication application);

    @Query("SELECT * FROM job_applications ORDER BY id DESC")
    List<JobApplication> getAllApplications();

    @Query("SELECT COUNT(*) FROM job_applications")
    int getApplicationCount();

    @Query("SELECT COUNT(*) FROM job_applications WHERE status = 'Interview'")
    int getInterviewCount();

    @Query("SELECT * FROM job_applications WHERE id = :jobId LIMIT 1")
    JobApplication getApplicationById(int jobId);
}

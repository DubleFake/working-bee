package com.homework.task.database.services.mappers;

import com.homework.task.database.templates.Task;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskRowMapper implements RowMapper<Task> {

    /**
     * Maps a row from the ResultSet to a Task object.
     * This method is typically used for mapping a row of the result set retrieved from a database query
     * to a Task object, where the columns in the result set correspond to the fields of the Task.
     *
     * @param rs - The ResultSet object containing the data from the query.
     * @param rowNum - The row number in the ResultSet (used for pagination or processing multiple rows).
     * @throws SQLException - If an SQL error occurs while accessing the ResultSet data.
     * @return task - A Task object populated with the data from the current row of the ResultSet.
     */
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setName(rs.getString("name"));;
        task.setDescription(rs.getString("description"));
        task.setStatus(Task.Status.valueOf(rs.getString("status")));
        return task;
    }

}

package com.honey.demo.processor;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.honey.demo.model.Course;
import org.apache.parquet.example.data.Group;
import org.springframework.batch.item.ItemProcessor;

public class ParquetToJsonItemProcessor implements ItemProcessor<Group, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String process(Group group) throws Exception {
        // Assuming 'group' represents a single row in Parquet.
        // You need to convert this group to a JSON string.
        // This conversion depends on your Parquet schema.
        // Here is a simplified example:

        String json = convertGroupToJson(group);
        return json;
    }

    private String convertGroupToJson(Group group) throws JsonProcessingException {
        // Implement this method to convert the Parquet Group to a JSON string.
        // This implementation highly depends on your Parquet file's schema.
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Course course = groupToCourse(group);
        return objectMapper.writeValueAsString(course);
    }

    public Course groupToCourse(Group group) {
        Course course = new Course();
        course.setCourseId(group.getInteger("course_id", 0));
        course.setCourseName(group.getString("course_name", 0));
        course.setCourseDescription(group.getString("course_description", 0));
        course.setFivetranDeleted(group.getBoolean("_fivetran_deleted", 0));
        course.setFivetranSynced(group.getLong("_fivetran_synced", 0));
        return course;
    }
}

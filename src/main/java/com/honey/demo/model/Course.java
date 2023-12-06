package com.honey.demo.model;

import java.util.Objects;

public class Course {
    private int courseId;
    private String courseName;
    private String courseDescription;
    private boolean fivetranDeleted;
    private long fivetranSynced;

    public Course() {
    }

    public int getCourseId() {
        return this.courseId;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public String getCourseDescription() {
        return this.courseDescription;
    }

    public boolean isFivetranDeleted() {
        return this.fivetranDeleted;
    }

    public long getFivetranSynced() {
        return this.fivetranSynced;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public void setFivetranDeleted(boolean fivetranDeleted) {
        this.fivetranDeleted = fivetranDeleted;
    }

    public void setFivetranSynced(long fivetranSynced) {
        this.fivetranSynced = fivetranSynced;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof final Course other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.getCourseId() != other.getCourseId()) return false;
        final Object this$courseName = this.getCourseName();
        final Object other$courseName = other.getCourseName();
        if (!Objects.equals(this$courseName, other$courseName))
            return false;
        final Object this$courseDescription = this.getCourseDescription();
        final Object other$courseDescription = other.getCourseDescription();
        if (!Objects.equals(this$courseDescription, other$courseDescription))
            return false;
        if (this.isFivetranDeleted() != other.isFivetranDeleted()) return false;
        return this.getFivetranSynced() == other.getFivetranSynced();
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Course;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getCourseId();
        final Object $courseName = this.getCourseName();
        result = result * PRIME + ($courseName == null ? 43 : $courseName.hashCode());
        final Object $courseDescription = this.getCourseDescription();
        result = result * PRIME + ($courseDescription == null ? 43 : $courseDescription.hashCode());
        result = result * PRIME + (this.isFivetranDeleted() ? 79 : 97);
        final long $fivetranSynced = this.getFivetranSynced();
        result = result * PRIME + (int) ($fivetranSynced >>> 32 ^ $fivetranSynced);
        return result;
    }

    public String toString() {
        return "Course(courseId=" + this.getCourseId() + ", courseName=" + this.getCourseName() + ", courseDescription=" + this.getCourseDescription() + ", fivetranDeleted=" + this.isFivetranDeleted() + ", fivetranSynced=" + this.getFivetranSynced() + ")";
    }
}
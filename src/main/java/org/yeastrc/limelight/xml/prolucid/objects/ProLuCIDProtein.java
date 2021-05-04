package org.yeastrc.limelight.xml.prolucid.objects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class ProLuCIDProtein {
    private Collection<Annotation> annotations;
    private String uniqueId;

    @Override
    public String toString() {
        return "ProLuCIDProtein{" +
                ", annotations=" + annotations +
                ", uniqueId='" + uniqueId + '\'' +
                '}';
    }

    public ProLuCIDProtein(String uniqueId) {
        this.annotations = new HashSet<>();
        this.uniqueId = uniqueId;
    }


    public Collection<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Collection<Annotation> annotations) {
        this.annotations = annotations;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public static class Annotation {
        private String name;
        private String description;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Annotation that = (Annotation) o;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }


    }

}

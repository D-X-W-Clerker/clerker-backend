package conference.clerker.domain.project.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import conference.clerker.domain.organization.schema.Organization;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Project parentProject;

    @OneToMany(mappedBy = "parentProject", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Project> childProjects = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<Organization> organizations = new ArrayList<>();

    public static Project create() {
        return Project.builder()
                .name("새로운 프로젝트").build();
    }

    public static Project create(Project parentProject) {
        Project project = new Project();
        project.setName(parentProject.getName());
        project.setParentProject(parentProject);
        return project;
    }

    public void setParentProject(Project project) {
        this.parentProject = project;
        if (!project.getChildProjects().contains(this)) {
            project.getChildProjects().add(this);
        }
    }

    public void removeParentProject() {
        if (this.parentProject != null) {
            this.parentProject.getChildProjects().remove(this);
        }
    }
}
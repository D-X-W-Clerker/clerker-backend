package conference.clerker.domain.organization.entity;


import conference.clerker.domain.member.entity.Member;
import conference.clerker.domain.project.entity.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, name = "organizaion_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "type")
    private String type;

    @Column(name = "phone_number")
    private String phoneNumber;

    public Organization createMember(Member member, Project project) {
        return Organization.builder()
                .role(Role.MEMBER)
                .member(member)
                .project(project)
                .build();
    }

    public Organization createOwner(Member member, Project project) {
        return Organization.builder()
                .role(Role.OWNER)
                .member(member)
                .project(project)
                .build();
    }
}

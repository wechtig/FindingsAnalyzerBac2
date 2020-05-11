package data;

import java.util.List;

public class ProjectConfig {
    private String name;
    private String description;
    private String vcsRepositoryLink;
    private List<User> users;

    public ProjectConfig(String name, String description, String vcsRepositoryLink, List<User> users) {
        this.name = name;
        this.description = description;
        this.vcsRepositoryLink = vcsRepositoryLink;
        this.users = users;
    }

    public ProjectConfig() {}

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

    public String getVcsRepositoryLink() {
        return vcsRepositoryLink;
    }

    public void setVcsRepositoryLink(String vcsRepositoryLink) {
        this.vcsRepositoryLink = vcsRepositoryLink;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}

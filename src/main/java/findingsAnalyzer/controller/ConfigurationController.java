package findingsAnalyzer.controller;

import findingsAnalyzer.data.ProjectConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import findingsAnalyzer.service.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class ConfigurationController {
    ConfigurationService configurationService = new ConfigurationService();

    @GetMapping("/config/projects")
    public List<ProjectConfig> getProjectNames() {
        return configurationService.getProjectConfigs();

    }

    @PostMapping("/config/add/user")
    public void saveUserToProject(@RequestBody String userData) {
        if(userData==null) {
            return;
        }

        String[] parts = userData.split("&&");
        String project = parts[0];
        String usermail = parts[1];

        configurationService.addUserToProject(usermail, project);
    }


        @PostMapping("/config/save")
    public void saveConfig(@RequestBody String projectsData) {
        if(projectsData==null) {
            return;
        }
        String[] parts = projectsData.split("&&");
        String name = parts[0];
        String description = parts[1];
        String vcsLink = parts[2];

        configurationService.saveConfig(name, description, vcsLink);
    }
}

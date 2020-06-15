package findingsAnalyzer.controller;

import findingsAnalyzer.data.ProjectConfig;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/config/projects/{project}")
    public ProjectConfig getProjectConfig(@PathVariable String project) {
        return configurationService.findConfigByProjectname(project);

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

        String[] projectsFieldData = projectsData.split("%%");

        for(int i = 0; i < projectsFieldData.length; i++) {
            String[] parts = projectsFieldData[i].split("&&");
            String name = parts[0];
            String description = parts[1];
            String vcsLink = parts[2];

            configurationService.saveConfig(name, description, vcsLink);
        }

    }
}

package findingsAnalyzer;

import data.ProjectConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import service.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class ConfigurationController {
    ConfigurationService configurationService = new ConfigurationService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @GetMapping("config/projects")
    public List<ProjectConfig> getProjectNames() {
        return configurationService.getProjectConfigs();

    }

}

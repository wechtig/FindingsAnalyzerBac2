package findingsAnalyzer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import findingsAnalyzer.converter.DBObjectConverter;
import findingsAnalyzer.data.Finding;
import findingsAnalyzer.data.ImageContainer;
import findingsAnalyzer.data.ProjectConfig;
import org.apache.tomcat.jni.Local;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.bson.Document;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class FindingsService {
    public static final String DATABASE_NAME = "findings";
    public static final String COLLECTION_SONG = "checkstyleFindings";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private MongoClient mongoClient;
    private MongoDatabase db;
    private MongoCollection<Document> collection;
    private IgnoreMessageService ignoreMessageService;
    private ConfigurationService configurationService;
    private HttpGithubClient httpGithubClient;
    private UserService userService;

    public FindingsService() {
        connect();
        httpGithubClient = new HttpGithubClient();
        configurationService = new ConfigurationService();
        userService = new UserService();
        ignoreMessageService = new IgnoreMessageService();
    }

    public List<String> getProjectsAndroid() {
        BasicDBObject field = new BasicDBObject("project", "1");
        BasicDBObject id = new BasicDBObject("project", "1");
        DistinctIterable<String> cursor = collection.distinct("project", String.class);
        Iterator it = cursor.iterator();

        return getStringDataFromQuery(it);
    }


    public List<String> getProjects() {
        BasicDBObject field = new BasicDBObject("project", "1");
        BasicDBObject id = new BasicDBObject("project", "1");
        DistinctIterable<String> cursor = collection.distinct("project", String.class);
        Iterator it = cursor.iterator();

        List<String> allProjects = getStringDataFromQuery(it);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> allowedProjects = getStringDataFromQuery(it);

        for(String project : allProjects) {
            ProjectConfig projectConfig = configurationService.findConfigByProjectname(project);
            if(projectConfig == null || projectConfig.getUsers() == null) {
                continue;
            }

            for(findingsAnalyzer.data.User projectUser : projectConfig.getUsers()) {
                if(projectUser.getEmail().equals(user.getUsername())) {
                    allowedProjects.add(project);
                }
            }

        }

        return allowedProjects;
    }

    public List<Finding> getFindingsByProjectAndDate(String projectName, LocalDate startDate, LocalDate endDate, boolean allProjects) {
        ProjectConfig projectConfig = configurationService.findConfigByProjectname(projectName);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean allowed = false;

        if(projectConfig.getUsers() == null) {
            return new ArrayList<>();
        }

        if(!allProjects) {
            for(findingsAnalyzer.data.User projectUser : projectConfig.getUsers()) {
                if(projectUser.getEmail().equals(user.getUsername())) {
                    allowed = true;
                }
            }
            if(!allowed) {
                return new ArrayList<>();
            }
        } else {
            // todo: filter all projects
        }

        BasicDBObject dateRange = new BasicDBObject ("$gte", startDate);
        dateRange.put("$lt", endDate);

        BasicDBObject query = new BasicDBObject("date", dateRange);

        FindIterable<Document> cursor = collection.find(query);
        Iterator it = cursor.iterator();

        List<Finding> findings = getCheckstyleDataFromQuery(it);

        if (allProjects != true) {
            findings = findings.stream().filter(c -> c.getProject().equals(projectName)).collect(Collectors.toList());
        }

        List<Finding> filteredFindings = getFindingsWithoutIgnored(findings);

        return filteredFindings;
    }

    public List<Finding> getFindingByProjectDateMessageFile(String project, LocalDate date, String message, String file, int line) {
        BasicDBObject criteria = new BasicDBObject();
        criteria.append("project", project);
        FindIterable<Document> cursor = collection.find(criteria);
        Iterator it = cursor.iterator();
        List<Finding> findings = getCheckstyleDataFromQuery(it);
        List<Finding> commentFindings = new ArrayList<>();
        for (Finding finding : findings) {
            if (finding.getLine().equals(line+"") && finding.getMessage().equals(message) && finding.getFile().contains(file)) {
                commentFindings.add(finding);
            }
        }


        return commentFindings;
    }

    public List<Finding> getFindingsForSchedulerByProjectAndDate(String projectName, LocalDate startDate, LocalDate endDate, boolean allProjects) {
        BasicDBObject dateRange = new BasicDBObject ("$gte", startDate);
        dateRange.put("$lt", endDate);

        BasicDBObject query = new BasicDBObject("date", dateRange);

        FindIterable<Document> cursor = collection.find(query);
        Iterator it = cursor.iterator();

        List<Finding> findings = getCheckstyleDataFromQuery(it);

        if (allProjects != true) {
            findings = findings.stream().filter(c -> c.getProject().equals(projectName)).collect(Collectors.toList());
        }

        List<Finding> filteredFindings = getFindingsWithoutIgnored(findings);

        return filteredFindings;
    }


    private void connect() {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        db = mongoClient.getDatabase(DATABASE_NAME);
        collection = db.getCollection(COLLECTION_SONG);
    }


    private List<String> getStringDataFromQuery(Iterator iterator) {
        List<String> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add((String) iterator.next());
        }

        return list;
    }

    private List<Finding> getCheckstyleDataFromQuery(Iterator iterator) {
        List<Finding> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add(DBObjectConverter.convertDocumentToFinding((Document) iterator.next()));
        }

        return list;
    }

    private List<Finding> getFindingsWithoutIgnored(List<Finding> findings) {
        List<String> ignoredMessages = ignoreMessageService.findAll();
        List<Finding> filtered = new ArrayList<>();

        if(ignoredMessages.size() == 0) {
            return findings;
        }

        for(Finding finding : findings) {
            boolean isIgnored = false;
            for (String message : ignoredMessages) {
                if(finding.getMessage().contains(message)) {
                    isIgnored = true;
                }
            }

            if(isIgnored == false) {
                filtered.add(finding);
            }
        }

        return filtered;
    }

    public List<Finding> getFindingsByProjectAndDateForUser(String project, LocalDate startDate, LocalDate endDate) {
        ProjectConfig projectConfig = configurationService.findConfigByProjectname(project);
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        findingsAnalyzer.data.User user = userService.findByEmail(loggedInUser.getUsername());

        List<String> changedFilesByUser = new ArrayList<>();
        if(projectConfig.getVcsRepositoryLink() != null) {
            String projectPaths[] = projectConfig.getVcsRepositoryLink().split("/");
            String userPath = projectPaths[projectPaths.length-2];
            String repoName = projectPaths[projectPaths.length-1];

            String githubLink = "https://api.github.com/repos/"+userPath+"/"+repoName+"/commits?since="+startDate+"&until="+endDate;
            String data = httpGithubClient.get(githubLink);

            if(data == null) {
                return new ArrayList<>();
            }

            List<String> commitLinks = new ArrayList<>();
            try {
                List<Map<String, String>> response = new ObjectMapper().readValue(data, List.class);
                for(Map<String, String> commitArray : response) {
                    Object author = commitArray.get("author");

                    if(author == null) {
                        continue;
                    }

                    String name = (String) ((LinkedHashMap) author).get("login");
                    if(!user.getFullname().equals(name)) {
                        continue;
                    }

                    String url = commitArray.get("url");
                    commitLinks.add(url);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<String> files = new ArrayList<>();
            for (String commit : commitLinks) {
                String commitData = httpGithubClient.get(commit);
                if(data == null) {
                    return new ArrayList<>();
                }
                try {
                    Map<String, List<Object>> commitResponse = new ObjectMapper().readValue(commitData, Map.class);
                    List<Object> changedFiles = commitResponse.get("files");

                    if(changedFiles == null) {
                        continue;
                    }

                    for(Object o : changedFiles)  {
                        String filename = (String) ((LinkedHashMap) o).get("filename");
                        files.add(filename);
                    }

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            List<String> changedJavaFiles = new ArrayList<>();
            for(String changedFile : files) {
                if(changedFile.contains(".java")) {
                    int idx = changedFile.lastIndexOf("/");
                    String filename = idx >= 0 ? changedFile.substring(idx + 1) : changedFile;;
                    changedJavaFiles.add(filename.replace(".java",""));
                }
            }
            changedFilesByUser = files.stream().distinct().collect(Collectors.toList());

        }
        boolean allowed = false;

        for(findingsAnalyzer.data.User projectUser : projectConfig.getUsers()) {
            if(projectUser.getEmail().equals(loggedInUser.getUsername())) {
                allowed = true;
            }
        }
        if(!allowed) {
            return new ArrayList<>();
        }

        BasicDBObject dateRange = new BasicDBObject ("$gte", startDate);
        dateRange.put("$lt", endDate);

        BasicDBObject query = new BasicDBObject("date", dateRange);

        FindIterable<Document> cursor = collection.find(query);
        Iterator it = cursor.iterator();

        List<Finding> findings = getCheckstyleDataFromQuery(it);
        List<Finding> filteredFindings = getFindingsWithoutIgnoredAndUserFiltered(findings, changedFilesByUser);

        return filteredFindings;
    }

    private List<Finding> getFindingsWithoutIgnoredAndUserFiltered(List<Finding> findings, List<String> changedFilesByUser) {
        List<String> ignoredMessages = ignoreMessageService.findAll();
        List<Finding> filtered = new ArrayList<>();

        if(ignoredMessages.size() == 0) {
            return findings;
        }

        for(Finding finding : findings) {
            boolean isIgnored = false;
            boolean isNotChanged = false;
            for (String message : ignoredMessages) {
                if(finding.getMessage().contains(message)) {
                    isIgnored = true;
                }

                for (String file : changedFilesByUser) {
                    if(finding.getFile().equals(file)) {
                        isNotChanged = true;
                    }
                }
            }

            if(isIgnored == false && isNotChanged == false) {
                filtered.add(finding);
            }
        }

        return filtered;
    }

    public List<ImageContainer> getFindingsForSchedulerImageByProjectAndDate(String project, LocalDate startDate, LocalDate endDate, boolean allProjects) {
        File folder = new File("images");
        File[] listOfFiles = folder.listFiles();
        List<ImageContainer> imageContainers = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String fileName = listOfFiles[i].getName();
                String parts[] = fileName.split("_");
                String description = "";
                String datePart = parts[2].replace(".gif","");
                LocalDate date = LocalDate.parse(datePart, formatter);
                if(parts[1].contains("1")) {
                    description = "Most common findings in project " + project;
                }

                else if(parts[1].contains("2")) {
                    description = "Classes with most findings in " + project;
                }

                else if(parts[1].contains("3")) {
                    description = "Findings per package in projcet " + project;
                }

                if(parts[0].equals(project) && date.isAfter(startDate) && date.isBefore(endDate)) {
                    try {

                        byte[] fileContent = Files.readAllBytes(listOfFiles[i].toPath());
                        String content =  Base64.getEncoder().encodeToString(fileContent);
                        ImageContainer imageContainer = new ImageContainer();
                        imageContainer.setDescription(description);
                        imageContainer.setImage(content);
                        imageContainers.add(imageContainer);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }

        return imageContainers;
    }
}


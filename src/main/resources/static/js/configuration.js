var urlProjectConfig = "http://localhost:8084/config/projects";
var projects = [];
function loadConfigurationUI() {
    fetch(urlProjectConfig).then(function(response) {
        return response.json();
    }).then(function(json) {
        var result = json;

        var dataCollapse = "";
        result.forEach(function (entry) {
            var description = entry["description"];
            var name = entry["name"];
            projects.push(name);
            var vcsRepositoryLink = entry["vcsRepositoryLink"];
            var users = entry["users"];

            if(description == null) {
                description = "";
            }

            if(vcsRepositoryLink == null) {
                vcsRepositoryLink = "";
            }

            var usersArray = users;

            var projectCollapse = "   <div class='card'>" +
                "<div class='card-header' id='headingOne'>" +
                " <h3>" +
                " <button class='btn btn-link' data-toggle='collapse' data-target='#collapseOne' aria-expanded='true' aria-controls='collapseOne' style='font-size: 25px'>"+name+"</button>" +
                "                </h3>" +
                "            </div>" +
                "            <div id='collapseOne' class='collapse show' aria-labelledby='headingOne' data-parent='#accordion'>" +
                "                <div class='card-body'>" +
                "                     <div class='form-group row'>" +
                "                          <h4 class='col-sm-1' >Description: </h4>"+
                "                          <div class='col-sm-5'>"+
                "                              <input class='form-control' id='"+name+"Description' value='"+description+"'/>"+
                "                          </div>" +
                "                     </div>"+
                "                     <div class='form-group row'>" +
                "                          <h4 class='col-sm-1' >VCS Link: </h4>"+
                "                          <div class='col-sm-5'>"+
                "                              <input class='form-control' id='"+name+"VCSLink' value='"+vcsRepositoryLink+"'/>"+
                "                          </div>" +
                "                     </div>"+
                "                     <div class='form-group row'>" +
                "                          <h4 class='col-sm-1' >Users: </h4>"+
                "                          <div class='col-sm-5'>"+
                "                              <input class='form-control' id='"+name+"Users' value='"+usersArray+"'/>"+
                "                          </div>" +
                "                          " +
                "                     </div>"+
                "                </div>" +
                "            </div>" +
                "        </div>";

            dataCollapse += projectCollapse;
        });

        document.getElementById("projectContainer").innerHTML = dataCollapse;
    })
}

function saveConfigurations() {

    var projectsData = [];
    projects.forEach(function (value) {
        var description = document.getElementById(value+"Description").value;
        var vcsLink = document.getElementById(value+"VCSLink").value;

        var projectData = value + "&&" + description + "&&" + vcsLink;
        projectsData.push(projectData);
        }
    );

    var messageAddRequest = new XMLHttpRequest();
    var requestUrl = "http://localhost:8084/config/save";
    messageAddRequest.open("POST",requestUrl);
    messageAddRequest.onreadystatechange = function() {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            //  to something
        }
    }

    messageAddRequest.send(projectsData);
}
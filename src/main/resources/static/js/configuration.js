var urlProjectConfig = "http://localhost:8084/config/projects";
var projects = [];
function loadConfigurationUI() {

    fetch(urlProjectConfig).then(function(response) {
        return response.json();
    }).then(function(json) {
        var result = json;
        var projectData = document.getElementById("projectForUser");
        var dataCollapse = "";
        result.forEach(function (entry) {
            var name = entry["name"];
            var opt = document.createElement('option');
            opt.value = name;
            opt.id = name;
            opt.innerHTML = name;
            projectData.appendChild(opt);

            var description = entry["description"];
            projects.push(name);
            var vcsRepositoryLink = entry["vcsRepositoryLink"];
            var users = entry["users"];

            if(description == null) {
                description = "";
            }

            if(vcsRepositoryLink == null) {
                vcsRepositoryLink = "";
            }

            var usersArray = "";
            users.forEach(function (entry) {
                var text = entry["fullname"] + "(" + entry["email"] + "); " ;
                usersArray += text;
            })

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
                "                              <p id='"+name+"Users'>"+usersArray+"</p>"+
                "                          </div>" +
                "                     </div>"+
                "                </div>" +
                "            </div>" +
                "        </div>";

            dataCollapse += projectCollapse;
        });

        document.getElementById("projectContainer").innerHTML = dataCollapse;
    })
}

function addUserToProject() {
    var newUserName = document.getElementById("newUserMail").value;
    var e = document.getElementById("projectForUser");
    var project = e.options[e.selectedIndex].value;
    var messageAddRequest = new XMLHttpRequest();

    var data = project+"&&"+newUserName;
    data = data.replace(/\"/g, "");

    fetch('http://localhost:8084/config/add/user', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body:data,
    }).then(function (data) {
        console.log('Request success: ', data);
    }).catch(function (error) {
        console.log('Request failure: ', error);
    });
    console.log(project + " " + newUserName);
}

function saveConfigurations() {

    var projectsData = "";
    projects.forEach(function (value) {
        var description = document.getElementById(value+"Description").value;
        var vcsLink = document.getElementById(value+"VCSLink").value;

        var projectData = value + "&&" + description + "&&" + vcsLink;
        projectsData += projectData+"%%";
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
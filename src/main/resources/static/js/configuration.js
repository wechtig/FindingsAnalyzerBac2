var urlProjectConfig = "http://localhost:8084/config/projects";
function loadConfigurationUI() {
    fetch(urlProjectConfig).then(function(response) {
        return response.json();
    }).then(function(json) {
        var result = json;

        var dataCollapse = "";
        result.forEach(function (entry) {
            var description = entry["description"];
            var name = entry["name"];
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
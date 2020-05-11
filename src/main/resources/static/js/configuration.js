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

            var projectCollapse = "   <div class='card'>" +
                "<div class='card-header' id='headingOne'>" +
                " <h3>" +
                " <button class='btn btn-link' data-toggle='collapse' data-target='#collapseOne' aria-expanded='true' aria-controls='collapseOne' style='font-size: 25px'>"+name+"</button>" +
                "                </h3>" +
                "            </div>" +
                "            <div id='collapseOne' class='collapse show' aria-labelledby='headingOne' data-parent='#accordion'>" +
                "                <div class='card-body'>" +
                "<h4>Description"+description+"</h4>"+
                "<h4>VCS Link"+vcsRepositoryLink+"</h4>"+
                "<h4>Users"+users+"</h4>"+
                "                </div>" +
                "            </div>" +
                "        </div>";

            dataCollapse += projectCollapse;
        });

        document.getElementById("projectContainer").innerHTML = dataCollapse;
    })
}
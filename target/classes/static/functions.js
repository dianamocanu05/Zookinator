function post(){
responses = getResponses();
$.ajax({
    type: 'POST',
    url : "http://localhost:5432/api/send",
    contentType: "application/json",
     headers: {
        "Content-type": "application/json",
        "Access-Control-Allow-Origin": "*"
      },
    data: JSON.stringify(object),
    // success: function(data) { alert("ajax worked"); },
    // error: function(data) {alert("ajax error"); },
     dataType: 'json'
  })
}

function getResponses(){

        var checkboxes = document.getElementsByName("input");
        var checkboxChecked = [];

        for(var i = 0; i < checkboxes.length; i++) {
            if(checkboxes[i].checked) {
                checkboxChecked.push(checkboxes[i]);
            }
        }

        alert(checkboxChecked.length);

}

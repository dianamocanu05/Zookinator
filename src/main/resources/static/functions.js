function post(){
responses = getResponses();
object = mapToJson(responses);
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
    var map = new Map();
    var tags = [hasHair,hasFeathers,laysEggs,hasMilk,isAirborne,isAquatic,isPredator,isToothed,hasBackbone,breathes,isVenomous,hasFins,legs,hasTail,isDomestic,isCatsized];
    for(var i = 0; i < checkboxes.length; i++) {
            map.put(tags[i], checkboxes[i]);
    }
    return map;
}

//function mapToJson(Map map){
//    object = {};
//    map.forEach((value, key) => {
//        var keys = key.split('.'),
//            last = keys.pop();
//        keys.reduce((r, a) => r[a] = r[a] || {}, object)[last] = value;
//    });
//    return object;
//}

function upload(){
    // Select your input type file and store it in a variable
    const input = document.getElementById('fileinput');

    // This will upload the file after having read it
    const upload = (file) => {
      fetch('http://localhost:5432/api/image', { // Your POST endpoint
        method: 'POST',
        headers: {
          // Content-Type may need to be completely **omitted**
          // or you may need something
          "Content-Type": "You will perhaps need to define a content-type here"
        },
        body: file // This is your file object
      }).then(
        response => response.json() // if the response is a JSON object
      ).then(
        success => console.log(success) // Handle the success response object
      ).catch(
        error => console.log(error) // Handle the error response object
      );
    };

    // Event handler executed when a file is selected
    const onSelectFile = () => upload(input.files[0]);

    // Add a listener on your input
    // It will be triggered when a file will be selected
    input.addEventListener('change', onSelectFile, false);
}